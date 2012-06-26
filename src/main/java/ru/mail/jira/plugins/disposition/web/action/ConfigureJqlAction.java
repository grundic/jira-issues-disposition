package ru.mail.jira.plugins.disposition.web.action;

import com.atlassian.jira.bc.filter.SearchRequestService;
import com.atlassian.jira.charts.util.ChartReportUtils;
import com.atlassian.jira.issue.search.SearchRequest;
import com.atlassian.jira.jql.parser.JqlParseException;
import com.atlassian.jira.jql.parser.JqlQueryParser;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.web.action.admin.customfields.AbstractEditConfigurationItemAction;
import com.atlassian.jira.web.action.admin.customfields.ManageConfigurationScheme;
import com.atlassian.plugin.webresource.WebResourceManager;
import org.jetbrains.annotations.NotNull;
import ru.mail.jira.plugins.disposition.config.ConfigType;
import ru.mail.jira.plugins.disposition.manager.DispositionConfigurationManager;

import java.util.Map;

/**
 * @author g.chernyshev
 */
public class ConfigureJqlAction extends AbstractEditConfigurationItemAction {

    @NotNull
    private final DispositionConfigurationManager dispositionConfigurationManager;
    @NotNull
    private final JqlQueryParser jqlQueryParser;
    @NotNull
    private final WebResourceManager webResourceManager;
    @NotNull
    private final I18nHelper.BeanFactory i18nFactory;
    @NotNull
    private final SearchRequestService searchRequestService;


    private String jql;
    private String filterId;
    private int typeId;
    private final Map<Integer, String> configType = ConfigType.getTypes();
    private SearchRequest filter;

    public ConfigureJqlAction(@NotNull DispositionConfigurationManager dispositionConfigurationManager, @NotNull JqlQueryParser jqlQueryParser, @NotNull WebResourceManager webResourceManager, @NotNull BeanFactory i18nFactory, @NotNull SearchRequestService searchRequestService) {
        this.dispositionConfigurationManager = dispositionConfigurationManager;
        this.jqlQueryParser = jqlQueryParser;
        this.webResourceManager = webResourceManager;
        this.i18nFactory = i18nFactory;
        this.searchRequestService = searchRequestService;
    }

    @Override
    protected void doValidation() {

        final I18nHelper i18n = i18nFactory.getInstance(getLoggedInUser());

        super.doValidation();

        switch (typeId) {
            case ConfigType.RAW_JQL: {

                if (jql.isEmpty()) {
                    addError("jql", i18n.getText("ru.mail.jira.plugins.disposition.web.jql.empty"));
                } else {
                    try {
                        jqlQueryParser.parseQuery(jql);
                    } catch (JqlParseException e) {
                        addErrorMessage(e.getMessage());
                    }
                }

                break;
            }

            case ConfigType.SAVED_FILTER: {
                String extractedFilterId = ChartReportUtils.extractProjectOrFilterId(filterId);
                if (null != extractedFilterId) {
                    SearchRequest filter = searchRequestService.getFilter(getJiraServiceContext(), Long.valueOf(extractedFilterId));
                    if (null == filter) {
                        addError("filterId", i18n.getText("ru.mail.jira.plugins.disposition.web.filter.empty", extractedFilterId));
                    }
                } else {
                    addError("filterId", i18n.getText("ru.mail.jira.plugins.disposition.web.filter.empty", filterId));
                }
                break;
            }

            default: {
                addErrorMessage(i18n.getText("ru.mail.jira.plugins.disposition.web.select.query.unknown"));
            }
        }
    }

    @Override
    public String doDefault() throws Exception {
        jql = dispositionConfigurationManager.getJqlQuery(getCustomField());
        filterId = dispositionConfigurationManager.getSavedFilter(getCustomField());
        typeId = dispositionConfigurationManager.getType(getCustomField());

        String extractedFilterId = ChartReportUtils.extractProjectOrFilterId(filterId);
        if (null != extractedFilterId) {
            filter = searchRequestService.getFilter(getJiraServiceContext(), Long.valueOf(extractedFilterId));
        }

        webResourceManager.requireResource("ru.mail.jira.plugins.jira-issues-disposition:init-filter-picker");
        return super.doDefault();
    }

    @Override
    protected String doExecute() throws Exception {
        dispositionConfigurationManager.setJqlQuery(getCustomField(), jql);
        dispositionConfigurationManager.setSavedFilter(getCustomField(), filterId);
        dispositionConfigurationManager.setType(getCustomField(), typeId);

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

    @SuppressWarnings("unused")
    public String getFilterId() {
        return filterId;
    }

    @SuppressWarnings("unused")
    public void setFilterId(String filterId) {
        this.filterId = filterId;
    }

    @SuppressWarnings("unused")
    public int getTypeId() {
        return typeId;
    }

    @SuppressWarnings("unused")
    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    @SuppressWarnings("unused")
    public Map<Integer, String> getConfigType() {
        return configType;
    }

    @SuppressWarnings("unused")
    public SearchRequest getFilter() {
        return filter;
    }
}
