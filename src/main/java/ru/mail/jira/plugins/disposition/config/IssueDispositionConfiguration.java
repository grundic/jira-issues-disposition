package ru.mail.jira.plugins.disposition.config;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.config.FieldConfigItemType;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import org.jetbrains.annotations.NotNull;
import ru.mail.jira.plugins.disposition.manager.DispositionConfigurationManager;

/**
 * @author grundic
 */
public class IssueDispositionConfiguration implements FieldConfigItemType {

    @NotNull
    private final static String KEY = "dispositionconfiguration";

    @NotNull
    private final DispositionConfigurationManager dispositionConfigurationManager;

    public IssueDispositionConfiguration(@NotNull DispositionConfigurationManager dispositionConfigurationManager) {
        this.dispositionConfigurationManager = dispositionConfigurationManager;
    }

    @Override
    public String getDisplayName() {
        return "Disposition JQL";
    }

    @Override
    public String getDisplayNameKey() {
        return "ru.mail.jira.plugins.disposition.web.configure.disposition.jql";
    }

    @Override
    public String getViewHtml(FieldConfig fieldConfig, FieldLayoutItem fieldLayoutItem) {
        return "<p>Configured Jql: <b>" + dispositionConfigurationManager.getQuery(fieldConfig.getCustomField()) + "</b></p>";
    }

    @Override
    public String getObjectKey() {
        return KEY;
    }

    @Override
    public Object getConfigurationObject(Issue issue, FieldConfig config) {
        return null;
    }

    @Override
    public String getBaseEditUrl() {
        return "ConfigureJqlAction!default.jspa";
    }
}
