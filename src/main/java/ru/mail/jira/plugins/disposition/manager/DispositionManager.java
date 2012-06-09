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

/**
 * Manager for disposition custom field
 */
public interface DispositionManager {


    /**
     * @param currentUser
     * @throws JqlParseException
     * @throws SearchException
     */
    public void resetDisposition(User currentUser) throws JqlParseException, SearchException;


    /**
     * Set order for issue
     *
     * @param issue - issue to be ordered
     * @param value - value for order
     */
    public void setDisposition(Issue issue, Double value) throws JqlParseException, SearchException;


    /**
     * Change order for dragged issue
     *
     * @param above   - issue above current (should have higher order)
     * @param dragged - current issue
     * @param below   - issue below current (should have lower order)
     */
    public void setDisposition(Issue above, Issue dragged, Issue below) throws SearchException, JqlParseException;
}
