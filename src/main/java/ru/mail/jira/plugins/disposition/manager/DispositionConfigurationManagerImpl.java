package ru.mail.jira.plugins.disposition.manager;

import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.propertyset.JiraPropertySetFactory;
import com.opensymphony.module.propertyset.PropertySet;
import org.jetbrains.annotations.NotNull;

/**
 * @author g.chernyshev
 */
public class DispositionConfigurationManagerImpl implements DispositionConfigurationManager {

    @NotNull
    private final JiraPropertySetFactory jiraPropertySetFactory;

    private static final String ENTITY = "disposition";
    private static final String JQL_KEY = "jql";

    public DispositionConfigurationManagerImpl(@NotNull JiraPropertySetFactory jiraPropertySetFactory) {
        this.jiraPropertySetFactory = jiraPropertySetFactory;
    }

    @Override
    public String getJqlQuery(@NotNull CustomField customField) {
        PropertySet propertySet = jiraPropertySetFactory.buildNoncachingPropertySet(ENTITY);
        if (null != propertySet) {
            return propertySet.getString(getJqlKey(customField));
        }

        return null;
    }

    @Override
    public void setJqlQuery(@NotNull CustomField customField, String value) {
        PropertySet propertySet = jiraPropertySetFactory.buildNoncachingPropertySet(ENTITY);
        if (null != propertySet) {
            propertySet.setString(getJqlKey(customField), value);
        }
    }

    private String getJqlKey(@NotNull CustomField customField) {
        return String.format("%s_%s", JQL_KEY, customField.getId());
    }
}
