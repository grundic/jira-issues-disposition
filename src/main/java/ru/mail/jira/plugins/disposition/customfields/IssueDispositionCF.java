package ru.mail.jira.plugins.disposition.customfields;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.converters.DoubleConverter;
import com.atlassian.jira.issue.customfields.impl.NumberCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.customfields.view.CustomFieldParams;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.config.FieldConfigItemType;
import com.atlassian.jira.util.ErrorCollection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.mail.jira.plugins.disposition.config.IssueDispositionConfiguration;
import ru.mail.jira.plugins.disposition.manager.DispositionConfigurationManager;
import ru.mail.jira.plugins.disposition.manager.DispositionManager;
import ru.mail.jira.plugins.disposition.manager.DispositionUtils;

import java.util.List;

/**
 * User: g.chernyshev
 * Date: 6/8/12
 * Time: 1:35 PM
 */


/**
 * Custom field for storing issue's disposition value
 */
public class IssueDispositionCF extends NumberCFType {

    @NotNull
    private final DispositionConfigurationManager dispositionConfigurationManager;
    @NotNull
    private final DispositionManager dispositionManager;

    public IssueDispositionCF(CustomFieldValuePersister customFieldValuePersister, DoubleConverter doubleConverter, GenericConfigManager genericConfigManager, @NotNull DispositionConfigurationManager dispositionConfigurationManager, @NotNull DispositionManager dispositionManager) {
        super(customFieldValuePersister, doubleConverter, genericConfigManager);
        this.dispositionConfigurationManager = dispositionConfigurationManager;
        this.dispositionManager = dispositionManager;
    }

    @Override
    public List<FieldConfigItemType> getConfigurationItemTypes() {
        final List<FieldConfigItemType> configurationItemTypes = super.getConfigurationItemTypes();
        configurationItemTypes.add(new IssueDispositionConfiguration(dispositionConfigurationManager));
        return configurationItemTypes;
    }

    @Override
    public void validateFromParams(CustomFieldParams relevantParams, ErrorCollection errorCollectionToAddTo, FieldConfig config) {
        super.validateFromParams(relevantParams, errorCollectionToAddTo, config);

        Double value = getValueFromCustomFieldParams(relevantParams);

        if (null != value && value <= 0) {
            errorCollectionToAddTo.addError(config.getCustomField().getId(), getI18nBean().getText("ru.mail.jira.plugins.disposition.manager.error.field.is.negative"));
        }
    }

    @Override
    public void updateValue(CustomField customField, Issue issue, Double value) {

        final User loggedInUser = ComponentManager.getInstance().getJiraAuthenticationContext().getLoggedInUser();

        super.updateValue(customField, issue, value);

        String jql = dispositionManager.replaceCurrentUser(dispositionConfigurationManager.getQuery(customField), loggedInUser.getName());
        assert null != jql;

        if (!DispositionUtils.isSkipShift()) {
            dispositionManager.shiftIssuesDown(jql, value, customField, loggedInUser, issue);
        }
    }

    @Override
    public Double getValueFromIssue(@Nullable CustomField field, @Nullable Issue issue) {
        Double value = super.getValueFromIssue(field, issue);
        if (null == field || null == issue) {
            return null;
        }
        if (null == value) {
            return getDefaultValue(field.getRelevantConfig(issue));
        }
        return value;
    }
}