package ru.mail.jira.plugins.disposition.web.action;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.issue.fields.rest.json.beans.JiraBaseUrls;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.jql.parser.JqlParseException;
import com.atlassian.jira.jql.parser.JqlQueryParser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.query.Query;
import ru.mail.jira.plugins.disposition.manager.DispositionManager;
import ru.mail.jira.plugins.disposition.manager.DispositionManagerImpl;

/**
 * User: g.chernyshev
 * Date: 6/9/12
 * Time: 1:17 PM
 */
public class ResetIssuesDispositionAction extends JiraWebActionSupport {

    private final WebResourceManager webResourceManager;
    private final UserManager userManager;
    private final DispositionManager dispositionManager;
    private final SearchService searchService;
    private final JqlQueryParser jqlQueryParser;
    private final JiraBaseUrls jiraBaseUrls;

    private String assignee;

    public ResetIssuesDispositionAction(WebResourceManager webResourceManager, UserManager userManager, DispositionManager dispositionManager, SearchService searchService, JqlQueryParser jqlQueryParser, JiraBaseUrls jiraBaseUrls) {
        this.webResourceManager = webResourceManager;
        this.userManager = userManager;
        this.dispositionManager = dispositionManager;
        this.searchService = searchService;
        this.jqlQueryParser = jqlQueryParser;
        this.jiraBaseUrls = jiraBaseUrls;
    }

    @Override
    public String doDefault() throws Exception {
        webResourceManager.requireResource("ru.mail.jira.plugins.jira-issues-disposition:init-user-picker");
        return super.doDefault();
    }

    @Override
    protected String doExecute() {
        User remoteUser = ComponentManager.getInstance().getJiraAuthenticationContext().getLoggedInUser();
        User assigneeUser = userManager.getUser(assignee);
        try {
            dispositionManager.resetDisposition(assigneeUser);
        } catch (JqlParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (SearchException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }


        Query query;
        try {
            query = jqlQueryParser.parseQuery(DispositionManagerImpl.JQL_QUERY);
        } catch (JqlParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return getRedirect(jiraBaseUrls.baseUrl() + "/secure/IssueNavigator.jspa?reset=true" + searchService.getQueryString(remoteUser, query));
    }


    /* --------------------------------------------------------------------------------------------------------------*/

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }
}
