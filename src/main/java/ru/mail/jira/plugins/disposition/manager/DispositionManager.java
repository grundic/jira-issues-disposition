package ru.mail.jira.plugins.disposition.manager;

/**
 * User: g.chernyshev
 * Date: 6/8/12
 * Time: 3:47 PM
 */

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.issue.Issue;
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
     * Set order for issue
     *
     * @param issue  - issue to be ordered
     * @param value  - value for order
     * @param errors - container for errors
     */
    public void setDisposition(@NotNull Issue issue, @NotNull Double value, @NotNull Collection<String> errors) throws JqlParseException, SearchException;


    /**
     * Change order for dragged issue
     *
     * @param high          - issue above current (should have higher order)
     * @param dragged       - current issue
     * @param low           - issue below current (should have lower order)
     * @param users         - users whom issues can be reordered
     * @param errors        - container for errors
     */
    public void setDisposition(@Nullable Issue high, @NotNull Issue dragged, @Nullable Issue low, @NotNull Collection<User> users, @NotNull Collection<String> errors) throws SearchException, JqlParseException;


    /**
     * Get query for issue navigator
     *
     * @param user - searcher
     * @return - query for web request
     * @throws JqlParseException
     */
    public String getQueryLink(@NotNull User user) throws JqlParseException;
}
