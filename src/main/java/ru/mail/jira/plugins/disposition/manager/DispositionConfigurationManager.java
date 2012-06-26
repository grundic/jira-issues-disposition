package ru.mail.jira.plugins.disposition.manager;

import com.atlassian.jira.issue.fields.CustomField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author g.chernyshev
 */
public interface DispositionConfigurationManager {

    /**
     * Get configured query string for configured custom field.
     *
     * @param customField - configured custom field
     * @return jql query string, depending of configured type (raw jql of saved filter)
     */
    @Nullable
    public String getQuery(@NotNull CustomField customField);

    /**
     * Get configured type. Types are declared in {@link ru.mail.jira.plugins.disposition.config.ConfigType}
     *
     * @param customField - configured custom field
     * @return configured type
     */
    public int getType(@NotNull CustomField customField);

    /**
     * Save type of jql query (raw jql or saved filter)
     *
     * @param customField - configured custom field
     * @param type        - value of saved type
     */
    public void setType(@NotNull CustomField customField, int type);

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

    /**
     * Get saved filter id.
     *
     * @param customField - configured custom field
     * @return saved filter id, starting with "filter-"
     */
    public String getSavedFilter(@NotNull CustomField customField);

    /**
     * Set saved filter id for querying
     *
     * @param customField - configured custom field
     * @param filterId    - filter id, starting with "filter-"
     */
    public void setSavedFilter(@NotNull CustomField customField, String filterId);
}
