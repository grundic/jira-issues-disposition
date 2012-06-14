package ru.mail.jira.plugins.disposition.web.action;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.jql.parser.JqlParseException;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.plugin.webresource.WebResourceManager;
import org.jetbrains.annotations.NotNull;
import ru.mail.jira.plugins.disposition.manager.DispositionManager;
import ru.mail.jira.plugins.disposition.manager.DispositionManagerImpl;
import ru.mail.jira.plugins.disposition.web.CookieHelper;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: g.chernyshev
 * Date: 6/9/12
 * Time: 1:17 PM
 */
public class ResetIssuesDispositionAction extends JiraWebActionSupport {

    @NotNull
    private final WebResourceManager webResourceManager;
    @NotNull
    private final UserManager userManager;
    @NotNull
    private final DispositionManager dispositionManager;

    private String assignee;

    private Double step = DispositionManagerImpl.DISPOSITION_STEP;

    public ResetIssuesDispositionAction(@NotNull WebResourceManager webResourceManager, @NotNull UserManager userManager, @NotNull DispositionManager dispositionManager) {
        this.webResourceManager = webResourceManager;
        this.userManager = userManager;
        this.dispositionManager = dispositionManager;
    }

    @Override
    public String doDefault() throws Exception {
        webResourceManager.requireResource("ru.mail.jira.plugins.jira-issues-disposition:init-user-picker");
        return super.doDefault();
    }

    @Override
    protected String doExecute() {
        User selectedUser = userManager.getUser(assignee);

        Collection<String> errors = new ArrayList<String>();

        try {
            dispositionManager.resetDisposition(selectedUser, getStep(), errors);
            // save reindexed username in cookie to be able to sort issue for this user
            setConglomerateCookieValue(CookieHelper.AJS_CONGLOMERATE_COOKIE, CookieHelper.CONGLOMERATE_COOKIE_KEY, selectedUser.getName());
        } catch (JqlParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (SearchException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        if (!errors.isEmpty()) {
            for (String error : errors) {
                addErrorMessage(error);
            }
            return ERROR;
        }

        try {
            return getRedirect(dispositionManager.getQueryLink(selectedUser));
        } catch (JqlParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    /* --------------------------------------------------------------------------------------------------------------*/

    @SuppressWarnings("unused")
    public String getAssignee() {
        return assignee;
    }

    @SuppressWarnings("unused")
    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    @SuppressWarnings("unused")
    public Double getStep() {
        return step;
    }

    @SuppressWarnings("unused")
    public void setStep(Double step) {
        this.step = step;
    }
}
