package ru.mail.jira.plugins.disposition.manager;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.ModifiedValue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.index.IndexException;
import com.atlassian.jira.issue.index.IssueIndexManager;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchProvider;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.jql.parser.JqlParseException;
import com.atlassian.jira.jql.parser.JqlQueryParser;
import com.atlassian.jira.util.ImportUtils;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.Query;
import com.atlassian.query.clause.Clause;
import com.atlassian.query.order.SortOrder;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.mail.jira.plugins.disposition.customfields.IssueDispositionCF;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * User: g.chernyshev
 * Date: 6/8/12
 * Time: 7:40 PM
 */
public class DispositionManagerImpl implements DispositionManager {

    public static final String JQL_QUERY = "assignee = currentUser() and resolution = Unresolved ORDER BY \"Order\", key";

    private static final Double DISPOSITION_START = 0.0;
    private static final Double DISPOSITION_STEP = 1.0;

    private static final int SHIFT_UP = -1;
    private static final int SHIFT_DOWN = 1;

    private static final Logger log = Logger.getLogger(DispositionManagerImpl.class);

    @NotNull
    private final JqlQueryParser jqlQueryParser;

    @NotNull
    private final SearchProvider searchProvider;

    @NotNull
    private final CustomFieldManager customFieldManager;


    public DispositionManagerImpl(@NotNull JqlQueryParser jqlQueryParser, @NotNull SearchProvider searchProvider, @NotNull CustomFieldManager customFieldManager) {
        this.jqlQueryParser = jqlQueryParser;
        this.searchProvider = searchProvider;
        this.customFieldManager = customFieldManager;
    }

    @Override
    public void resetDisposition(User userToBeReset) throws JqlParseException, SearchException {

        User user = ComponentManager.getInstance().getJiraAuthenticationContext().getLoggedInUser();

        String jql = replaceCurrentUser(JQL_QUERY, userToBeReset.getName());

        CustomField field = getCustomFieldByIssueAndType(IssueDispositionCF.class, null);
        if (null == field) {
            log.error("Can't find custom field object - is should be configured first!");
            return;
        }


        Query query = jqlQueryParser.parseQuery(jql);
        SearchResults searchResults = searchProvider.search(query, user, PagerFilter.getUnlimitedFilter());

        if (null == searchResults) {
            return;
        }

        Double disposition = DISPOSITION_START;
        for (Issue issue : searchResults.getIssues()) {
            Double prevValue = (Double) issue.getCustomFieldValue(field);

            disposition += DISPOSITION_STEP;
            updateValue(field, prevValue, disposition, issue, true);
        }
    }

    @Override
    public void setDisposition(Issue issue, Double value) throws JqlParseException, SearchException {
        User user = ComponentManager.getInstance().getJiraAuthenticationContext().getLoggedInUser();

        CustomField field = getCustomFieldByIssueAndType(IssueDispositionCF.class, issue);
        if (null == field) {
            log.error(String.format("Can't find custom field object for issue %s!", issue.getKey()));
            return;
        }

        Double prevValue = (Double) issue.getCustomFieldValue(field);
        // don't waste time for setting same value
        if (null != prevValue && prevValue.equals(value)) {
            log.warn("Values are equal!");
            return;
        }


        // if issue in not in configured Jql - return
        if (!isIssueInJQL(JQL_QUERY, issue, user)) {
            log.error(String.format("Issue %s in not in Jql '%s'!", issue.getKey(), JQL_QUERY));
            return;
        }

        // if some issue in query have the same disposition value - we have to shift other issues
        if (isDispositionInJQL(JQL_QUERY, value, field, user)) {
            shiftIssuesDown(JQL_QUERY, value, field, user, issue);
        }

        // set value of our issue
        updateValue(field, prevValue, value, issue, true);
    }

    @Override
    public void setDisposition(@Nullable Issue above, @NotNull Issue dragged, @Nullable Issue below) throws SearchException, JqlParseException {
        User user = ComponentManager.getInstance().getJiraAuthenticationContext().getLoggedInUser();

        if (null == above && null == below) {
            log.error("High and low issues can't be null at the same time!");
            return;
        }

        if (null != above && !isIssueInJQL(JQL_QUERY, above, user)) {
            log.warn("High issue must belong to configured jql query!");
            return;
        }

        if (null != below && !isIssueInJQL(JQL_QUERY, below, user)) {
            log.warn("Low issue must belong to configured jql query!");
            return;
        }

        if (!isIssueInJQL(JQL_QUERY, dragged, user)) {
            log.warn("Dragged issue must belong to configured jql query!");
            return;
        }


        // assume, that all issues have the same custom field
        @Nullable
        CustomField field = getCustomFieldByIssueAndType(IssueDispositionCF.class, dragged);
        if (null == field) {
            log.error(String.format("Can't find custom field object for issue %s!", dragged.getKey()));
            return;
        }


        Double aboveValue = (above != null) ? (Double) above.getCustomFieldValue(field) : null;
        Double draggedValue = (Double) dragged.getCustomFieldValue(field);
        Double belowValue = (below != null) ? (Double) below.getCustomFieldValue(field) : null;

        if (null == aboveValue && null == belowValue) {
            log.warn("Both above and below issues should have initialized disposition values!");
            return;
        }

        @Nullable
        Long average = null;
        if (null != aboveValue && null != belowValue) {
            average = getAverage(aboveValue, belowValue);
        }

        if (null == average) {
            if ((null != draggedValue && null != aboveValue && draggedValue < aboveValue)) {
                shiftIssuesUp(JQL_QUERY, aboveValue, field, user, dragged);
                updateValue(field, draggedValue, aboveValue, dragged, true);
            } else {
                if (null != belowValue) {
                    shiftIssuesDown(JQL_QUERY, belowValue, field, user, dragged);
                    updateValue(field, draggedValue, belowValue, dragged, true);
                } else {
                    updateValue(field, draggedValue, aboveValue + DISPOSITION_STEP, dragged, true);
                }
            }
        } else {
            updateValue(field, draggedValue, (double) average, dragged, true);
        }
    }


    /*-------------------------------------   Private helper methods   ----------------------------------------*/

    /**
     * Check that current issue is in configured jql query
     *
     * @param jql   - configured JQL query
     * @param issue - current issue
     * @param user  - searcher
     * @return - true if issue is in query false otherwise
     * @throws JqlParseException
     * @throws SearchException
     */
    private boolean isIssueInJQL(@NotNull String jql, @NotNull Issue issue, @NotNull User user) throws JqlParseException, SearchException {
        Query query = jqlQueryParser.parseQuery(jql);
        JqlQueryBuilder jqlQueryBuilder = JqlQueryBuilder.newBuilder(query);
        jqlQueryBuilder.where().and().issue(issue.getKey());

        return searchProvider.searchCount(jqlQueryBuilder.buildQuery(), user) == 1;
    }

    /**
     * Check if disposition with current value already exists
     *
     * @param jql   - configured jql query
     * @param value - disposition query
     * @param field - disposition custom field
     * @param user  - searcher
     * @return - true if disposition with given value found in configured Jql, else otherwise
     * @throws JqlParseException
     * @throws SearchException
     */
    private boolean isDispositionInJQL(@NotNull String jql, @NotNull Double value, @NotNull CustomField field, @NotNull User user) throws JqlParseException, SearchException {
        Query query = jqlQueryParser.parseQuery(jql);
        JqlQueryBuilder jqlQueryBuilder = JqlQueryBuilder.newBuilder(query);
        jqlQueryBuilder.where().and().customField(field.getIdAsLong()).eq(value.toString());

        return searchProvider.searchCount(jqlQueryBuilder.buildQuery(), user) == 1;
    }

    /**
     * Get all custom fields of specified type for issue
     *
     * @param type  - type of custom field to search
     * @param issue - issue to get custom fields from
     * @return - collection of founded fields
     */
    @NotNull
    @SuppressWarnings("unused")
    private Collection<CustomField> getCustomFieldsByIssueAndType(@NotNull Class<?> type, @Nullable Issue issue) {
        Set<CustomField> result = new TreeSet<CustomField>();
        Collection<CustomField> fields = (null == issue) ?
                customFieldManager.getCustomFieldObjects() : customFieldManager.getCustomFieldObjects(issue);

        for (CustomField cf : fields) {
            if (type.isAssignableFrom(cf.getCustomFieldType().getClass())) {
                result.add(cf);
            }
        }
        return result;
    }

    /**
     * Get first custom field of specified type for issue
     *
     * @param type  - type of custom field to search
     * @param issue - issue to get custom fields from
     * @return - single custom field or null
     */
    @Nullable
    private CustomField getCustomFieldByIssueAndType(@NotNull Class<?> type, @Nullable Issue issue) {

        Collection<CustomField> fields = (null == issue) ?
                customFieldManager.getCustomFieldObjects() : customFieldManager.getCustomFieldObjects(issue);

        for (CustomField cf : fields) {
            if (type.isAssignableFrom(cf.getCustomFieldType().getClass())) {
                return cf;
            }
        }
        return null;
    }

    /**
     * Shift issues up/down - change disposition in turn
     *
     * @param jql          - query, used to get list of issues
     * @param startValue   - value of disposition field, from which we are starting shifting
     * @param field        - disposition custom field
     * @param user         - searcher
     * @param currentIssue - issue, currently moved - should be skipped from query
     * @param shiftValue   - direction of shifting (up/down)
     * @throws JqlParseException
     * @throws SearchException
     */
    private void shiftIssues(@NotNull String jql, @NotNull Double startValue, @NotNull CustomField field, @NotNull User user, @NotNull Issue currentIssue, int shiftValue) throws JqlParseException, SearchException {

        Query query = jqlQueryParser.parseQuery(jql);
        JqlQueryBuilder jqlQueryBuilder = JqlQueryBuilder.newBuilder(query);

        if (shiftValue == SHIFT_DOWN) {
            jqlQueryBuilder.where().and().customField(field.getIdAsLong()).gtEq(startValue.toString()).and().not().issue(currentIssue.getKey());
            jqlQueryBuilder.orderBy().add(field.getName(), SortOrder.ASC);
        } else {
            jqlQueryBuilder.where().and().customField(field.getIdAsLong()).ltEq(startValue.toString()).and().not().issue(currentIssue.getKey());
            jqlQueryBuilder.orderBy().add(field.getName(), SortOrder.DESC, true);
        }


        SearchResults searchResults = searchProvider.search(jqlQueryBuilder.buildQuery(), user, PagerFilter.getUnlimitedFilter());
        if (null == searchResults) {
            return;
        }

        Collection<Issue> issues = new LinkedHashSet<Issue>();
        Issue prevIssue = null;

        // search for space in minimum 2, so that we can increase other issues order
        for (Issue issue : searchResults.getIssues()) {
            if (null == prevIssue) {
                prevIssue = issue;
                issues.add(issue);
                continue;
            }

            Double value = (Double) issue.getCustomFieldValue(field);
            Double prevValue = (Double) prevIssue.getCustomFieldValue(field);

            if (getAverage(prevValue, value) != null) {
                break;
            }

            prevIssue = issue;
            issues.add(issue);
        }


        // increase disposition of close (near) issues by 1
        for (Issue issue : issues) {
            Double disposition = (Double) issue.getCustomFieldValue(field);
            updateValue(field, disposition, disposition + shiftValue, issue, true);
        }
    }

    /**
     * Shift issues down - change disposition in turn
     *
     * @param jql          - query, used to get list of issues
     * @param startValue   - value of disposition field, from which we are starting shifting
     * @param field        - disposition custom field
     * @param user         - searcher
     * @param currentIssue - issue, currently moved - should be skipped from query
     * @throws JqlParseException
     * @throws SearchException
     */
    private void shiftIssuesDown(@NotNull String jql, @NotNull Double startValue, @NotNull CustomField field, @NotNull User user, @NotNull Issue currentIssue) throws JqlParseException, SearchException {
        shiftIssues(jql, startValue, field, user, currentIssue, SHIFT_DOWN);
    }

    /**
     * Shift issues up - change disposition in turn
     *
     * @param jql          - query, used to get list of issues
     * @param startValue   - value of disposition field, from which we are starting shifting
     * @param field        - disposition custom field
     * @param user         - searcher
     * @param currentIssue - issue, currently moved - should be skipped from query
     * @throws JqlParseException
     * @throws SearchException
     */
    private void shiftIssuesUp(@NotNull String jql, @NotNull Double startValue, @NotNull CustomField field, @NotNull User user, @NotNull Issue currentIssue) throws JqlParseException, SearchException {
        shiftIssues(jql, startValue, field, user, currentIssue, SHIFT_UP);
    }

    /**
     * Get average for two values
     *
     * @param first  - value of first disposition
     * @param second - value of second disposition
     * @return - average value if found or null
     */
    @Nullable
    private Long getAverage(@NotNull Double first, @NotNull Double second) {
        // check for space between values
        if (Math.abs(second - first) >= 2) {
            return Math.round((second + first) / 2);
        }
        return null;
    }

    /**
     * Update custom field value for current issue
     *
     * @param customField - custom field to be updated
     * @param prevValue   - previous value
     * @param newValue    - new value
     * @param issue       - issue to be changed
     * @param reindex     - should the issue be reindexed
     */
    private void updateValue(@NotNull CustomField customField, @Nullable Double prevValue, @Nullable Double newValue, @NotNull Issue issue, boolean reindex) {
        customField.updateValue(null, issue, new ModifiedValue(prevValue, newValue), new DefaultIssueChangeHolder());
        if (reindex) {
            indexIssue(issue);
        }
    }


    /**
     * Reindex issue (Lucene)
     *
     * @param issue - issue to be reindexed
     */
    private void indexIssue(@NotNull Issue issue) {
        try {
            boolean oldValue = ImportUtils.isIndexIssues();
            ImportUtils.setIndexIssues(true);
            IssueIndexManager issueIndexManager = ComponentManager.getInstance().getIndexManager();
            issueIndexManager.reIndex(issue);
            ImportUtils.setIndexIssues(oldValue);
        } catch (IndexException e) {
            log.error("Unable to index issue: " + issue.getKey(), e);
        }
    }

    /**
     * Recursively traverse all clauses to get list of them
     *
     * @param clauses - root clause
     * @return - collection of clauses
     */
    @NotNull
    @SuppressWarnings("unused")
    private Collection<String> clauseRecursion(@NotNull Collection<Clause> clauses) {

        Collection<String> fields = new LinkedHashSet<String>();

        for (Clause clause : clauses) {
            if (clause.getClauses() != null && clause.getClauses().size() > 0) {
                fields.addAll(clauseRecursion(clause.getClauses()));
            } else {
                fields.add(clause.getName());
            }
        }

        return fields;
    }


    /**
     * Replace all occurrences of 'currentUser()' to user
     *
     * @param jql  - jql query
     * @param user - substitution value
     * @return - jql query (without validation)
     */
    @NotNull
    private String replaceCurrentUser(String jql, String user) {
        return jql.replaceAll(Pattern.quote("currentUser()"), user);
    }
}
