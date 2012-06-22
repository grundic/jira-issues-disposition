package ru.mail.jira.plugins.disposition.web.action;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.jql.parser.JqlParseException;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.util.I18nHelper;
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
    @NotNull
    private final I18nHelper.BeanFactory i18nFactory;

    private String assignee;

    private Double step = DispositionManagerImpl.DISPOSITION_STEP;

    private boolean skipReindex = false;

    public ResetIssuesDispositionAction(@NotNull WebResourceManager webResourceManager, @NotNull UserManager userManager, @NotNull DispositionManager dispositionManager, @NotNull BeanFactory i18nFactory) {
        this.webResourceManager = webResourceManager;
        this.userManager = userManager;
        this.dispositionManager = dispositionManager;
        this.i18nFactory = i18nFactory;
    }

    @Override
    protected void doValidation() {
        super.doValidation();

        final I18nHelper i18n = i18nFactory.getInstance(getLoggedInUser());

        if (null == ComponentManager.getInstance().getUserUtil().getUser(assignee)) {
            addError("assignee", i18n.getText("ru.mail.jira.plugins.disposition.web.reindex.error.user.null", assignee));
        }

        if (step <= 0) {
            addError("step", i18n.getText("ru.mail.jira.plugins.disposition.web.reindex.error.step.negative"));
        }
    }

    @Override
    protected String doExecute() {
        User selectedUser = userManager.getUser(assignee);

        Collection<String> errors = new ArrayList<String>();

        try {
            if (!skipReindex) {
                dispositionManager.resetDisposition(selectedUser, getStep(), errors);
            }

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

        return getRedirect("secure/IssueNavigator.jspa");
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

    @SuppressWarnings("unused")
    public boolean getSkipReindex() {
        return skipReindex;
    }

    @SuppressWarnings("unused")
    public void setSkipReindex(boolean skipReindex) {
        this.skipReindex = skipReindex;
    }

    @SuppressWarnings("unused")
    @NotNull
    public WebResourceManager getWebResourceManager() {
        return webResourceManager;
    }
}
