package ru.mail.jira.plugins.disposition.manager;

import com.atlassian.jira.issue.fields.CustomField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author g.chernyshev
 */
public interface DispositionConfigurationManager {

    /**
     * Get configured jql query for custom field
     *
     * @param customField - configured custom field
     * @return - saved value
     */
    @Nullable
    public String getJqlQuery(@NotNull CustomField customField);

    /**
     * Save configured jql query for custom field
     *
     * @param customField - configured custom field
     * @param value       - value to store
     */
    public void setJqlQuery(@NotNull CustomField customField, String value);
}
