package ru.mail.jira.plugins.disposition.manager;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.bc.JiraServiceContextImpl;
import com.atlassian.jira.bc.filter.SearchRequestService;
import com.atlassian.jira.charts.util.ChartReportUtils;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.search.SearchRequest;
import com.atlassian.jira.propertyset.JiraPropertySetFactory;
import com.atlassian.jira.util.SimpleErrorCollection;
import com.opensymphony.module.propertyset.PropertySet;
import org.jetbrains.annotations.NotNull;
import ru.mail.jira.plugins.disposition.config.ConfigType;

/**
 * @author g.chernyshev
 */
public class DispositionConfigurationManagerImpl implements DispositionConfigurationManager {

    @NotNull
    private final JiraPropertySetFactory jiraPropertySetFactory;
    @NotNull
    private final SearchRequestService searchRequestService;

    private static final String ENTITY = "disposition";
    private static final String JQL_KEY = "jql";
    private static final String FILTER_KEY = "filter";
    private static final String CFG_TYPE_KEY = "type";

    public DispositionConfigurationManagerImpl(@NotNull JiraPropertySetFactory jiraPropertySetFactory, @NotNull SearchRequestService searchRequestService) {
        this.jiraPropertySetFactory = jiraPropertySetFactory;
        this.searchRequestService = searchRequestService;
    }

    @Override
    public String getQuery(@NotNull CustomField customField) {

        PropertySet propertySet = jiraPropertySetFactory.buildNoncachingPropertySet(ENTITY);
        if (null != propertySet) {
            int type = getType(customField);

            switch (type) {
                case ConfigType.RAW_JQL:
                    return propertySet.getString(getJqlKey(customField, JQL_KEY));
                case ConfigType.SAVED_FILTER:

                    String filterId = propertySet.getString(getJqlKey(customField, FILTER_KEY));

                    String extractedFilterId = ChartReportUtils.extractProjectOrFilterId(filterId);
                    if (null != extractedFilterId) {
                        User user = ComponentManager.getInstance().getJiraAuthenticationContext().getLoggedInUser();

                        SearchRequest filter = searchRequestService.getFilter(
                                new JiraServiceContextImpl(user, new SimpleErrorCollection()),
                                Long.valueOf(extractedFilterId)
                        );
                        return filter.getQuery().getQueryString();
                    }
                default:
                    return null;
            }
        }
        return null;
    }

    public int getType(@NotNull CustomField customField) {
        PropertySet propertySet = jiraPropertySetFactory.buildNoncachingPropertySet(ENTITY);
        if (null != propertySet) {
            return propertySet.getInt(getJqlKey(customField, CFG_TYPE_KEY));
        }
        return 0;
    }

    @Override
    public void setType(@NotNull CustomField customField, int type) {
        PropertySet propertySet = jiraPropertySetFactory.buildNoncachingPropertySet(ENTITY);
        if (null != propertySet) {
            propertySet.setInt(getJqlKey(customField, CFG_TYPE_KEY), type);
        }
    }

    @Override
    public String getJqlQuery(@NotNull CustomField customField) {
        PropertySet propertySet = jiraPropertySetFactory.buildNoncachingPropertySet(ENTITY);
        if (null != propertySet) {
            return propertySet.getString(getJqlKey(customField, JQL_KEY));
        }
        return null;
    }

    @Override
    public void setJqlQuery(@NotNull CustomField customField, String value) {
        PropertySet propertySet = jiraPropertySetFactory.buildNoncachingPropertySet(ENTITY);
        if (null != propertySet) {
            propertySet.setString(getJqlKey(customField, JQL_KEY), value);
        }
    }

    @Override
    public String getSavedFilter(@NotNull CustomField customField) {
        PropertySet propertySet = jiraPropertySetFactory.buildNoncachingPropertySet(ENTITY);
        if (null != propertySet) {
            return propertySet.getString(getJqlKey(customField, FILTER_KEY));
        }
        return null;
    }

    @Override
    public void setSavedFilter(@NotNull CustomField customField, String filterId) {
        PropertySet propertySet = jiraPropertySetFactory.buildNoncachingPropertySet(ENTITY);
        if (null != propertySet) {
            propertySet.setString(getJqlKey(customField, FILTER_KEY), filterId);
        }
    }

    private String getJqlKey(@NotNull CustomField customField, String key) {
        return String.format("%s_%s", key, customField.getId());
    }
}

