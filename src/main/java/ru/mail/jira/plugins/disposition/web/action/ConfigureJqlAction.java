package ru.mail.jira.plugins.disposition.web.action;

import com.atlassian.jira.jql.parser.JqlParseException;
import com.atlassian.jira.jql.parser.JqlQueryParser;
import com.atlassian.jira.web.action.admin.customfields.AbstractEditConfigurationItemAction;
import com.atlassian.jira.web.action.admin.customfields.ManageConfigurationScheme;
import org.jetbrains.annotations.NotNull;
import ru.mail.jira.plugins.disposition.manager.DispositionConfigurationManager;

/**
 * @author g.chernyshev
 */
public class ConfigureJqlAction extends AbstractEditConfigurationItemAction {

    @NotNull
    private final DispositionConfigurationManager dispositionConfigurationManager;

    @NotNull
    private final JqlQueryParser jqlQueryParser;

    private String jql;


    public ConfigureJqlAction(@NotNull DispositionConfigurationManager dispositionConfigurationManager, @NotNull JqlQueryParser jqlQueryParser) {
        this.dispositionConfigurationManager = dispositionConfigurationManager;
        this.jqlQueryParser = jqlQueryParser;
    }

    @Override
    protected void doValidation() {
        try {
            jqlQueryParser.parseQuery(jql);
        } catch (JqlParseException e) {
            addErrorMessage(e.getMessage());
        }

        super.doValidation();
    }

    @Override
    public String doDefault() throws Exception {
        jql = dispositionConfigurationManager.getJqlQuery(getCustomField());
        return super.doDefault();
    }

    @Override
    protected String doExecute() throws Exception {
        dispositionConfigurationManager.setJqlQuery(getCustomField(), jql);
        return getRedirect("admin/" + ManageConfigurationScheme.REDIRECT_URL_PREFIX + getCustomField().getIdAsLong() + "&fieldConfigSchemeId=" + getFieldConfigId());
    }


    /* ----------------------------------------------------------------------------------------------------------------------------- */

    @SuppressWarnings("unused")
    public String getJql() {
        return jql;
    }

    @SuppressWarnings("unused")
    public void setJql(String jql) {
        this.jql = jql;
    }
}
