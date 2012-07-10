package ru.mail.jira.plugins.disposition.manager;

/**
 * User: g.chernyshev
 * Date: 6/8/12
 * Time: 3:47 PM
 */

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.jql.parser.JqlParseException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Manager for disposition custom field
 */
public interface DispositionManager {


    /**
     * Reset disposition for all issues in configured Jql query for selected user
     *
     * @param currentUser - user, for whom reindex will be executed
     * @param step        - value between each issue
     * @param errors      - container for errors
     * @throws JqlParseException
     * @throws SearchException
     */
    public void resetDisposition(@NotNull User currentUser, @NotNull Double step, @NotNull Collection<String> errors) throws JqlParseException, SearchException;


    /**
     * Check disposition value for issue
     *
     * @param issue  - issue to be ordered
     * @param value  - value for order
     * @param users  - users whom issues can be reordered
     * @param errors - container for errors
     */
    public boolean validateDisposition(@NotNull Issue issue, @Nullable Double value, @NotNull Collection<User> users, @NotNull Collection<String> errors);

    /**
     * Set order for issue
     *
     * @param issue - issue to be ordered
     * @param value - value for order
     * @param users - users whom issues can be reordered
     */
    public void setDisposition(@NotNull Issue issue, @NotNull Double value, @NotNull Collection<User> users, @NotNull Collection<String> errors) throws JqlParseException, SearchException;


    /**
     * Change order for dragged issue
     *
     * @param high    - issue above current (should have higher order)
     * @param dragged - current issue
     * @param low     - issue below current (should have lower order)
     * @param users   - users whom issues can be reordered
     * @param errors  - container for errors
     * @param index   - drag destination issue row index
     */
    public void setDisposition(@Nullable Issue high, @NotNull Issue dragged, @Nullable Issue low, @NotNull Collection<User> users, @NotNull Collection<String> errors, @Nullable Integer index) throws SearchException, JqlParseException;


    /**
     * Shift issues down - change disposition in turn
     *
     * @param jql          - query, used to get list of issues
     * @param startValue   - value of disposition field, from which we are starting shifting
     * @param field        - disposition custom field
     * @param user         - searcher
     * @param currentIssue - issue, currently moved - should be skipped from query
     */
    public void shiftIssuesDown(@NotNull String jql, @NotNull Double startValue, @NotNull CustomField field, @NotNull User user, @NotNull Issue currentIssue);

    /**
     * Shift issues up - change disposition in turn
     *
     * @param jql          - query, used to get list of issues
     * @param startValue   - value of disposition field, from which we are starting shifting
     * @param field        - disposition custom field
     * @param user         - searcher
     * @param currentIssue - issue, currently moved - should be skipped from query
     */
    public void shiftIssuesUp(@NotNull String jql, @NotNull Double startValue, @NotNull CustomField field, @NotNull User user, @NotNull Issue currentIssue);

    /**
     * Get first custom field of specified type for issue
     *
     * @param type  - type of custom field to search
     * @param issue - issue to get custom fields from
     * @return - single custom field or null
     */
    public CustomField getCustomFieldByIssueAndType(@NotNull Class<?> type, @Nullable Issue issue);


    /**
     * Get query for issue navigator
     *
     * @param jql  - jql query to redirect
     * @param user - searcher
     * @return - query for web request
     * @throws JqlParseException
     */
    public String getQueryLink(@NotNull String jql, @NotNull User user) throws JqlParseException;

    /**
     * Replace all occurrences of 'currentUser()' to user
     *
     * @param jql  - jql query
     * @param user - substitution value
     * @return - jql query (without validation)
     */
    @Nullable
    public String replaceCurrentUser(@Nullable String jql, @Nullable String user);
}
