package ru.mail.jira.plugins.disposition.customfields;

import com.atlassian.jira.issue.customfields.converters.DoubleConverter;
import com.atlassian.jira.issue.customfields.impl.NumberCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.fields.config.FieldConfigItemType;
import org.jetbrains.annotations.NotNull;
import ru.mail.jira.plugins.disposition.config.IssueDispositionConfiguration;
import ru.mail.jira.plugins.disposition.manager.DispositionConfigurationManager;

import java.util.List;

/**
 * User: g.chernyshev
 * Date: 6/8/12
 * Time: 1:35 PM
 */


/**
 * Custom field for ordering issues
 * <p/>
 * Disposition query - query, which return list of issues for ordering.
 * <p/>
 * <p/>
 * project = FP
 * <p/>
 * issue 1: project = FP
 * issue 2: project = GG
 */
public class IssueDispositionCF extends NumberCFType {

    @NotNull
    private final DispositionConfigurationManager dispositionConfigurationManager;

    public IssueDispositionCF(CustomFieldValuePersister customFieldValuePersister, DoubleConverter doubleConverter, GenericConfigManager genericConfigManager, @NotNull DispositionConfigurationManager dispositionConfigurationManager) {
        super(customFieldValuePersister, doubleConverter, genericConfigManager);
        this.dispositionConfigurationManager = dispositionConfigurationManager;
    }

    @Override
    public List<FieldConfigItemType> getConfigurationItemTypes() {
        final List<FieldConfigItemType> configurationItemTypes = super.getConfigurationItemTypes();
        configurationItemTypes.add(new IssueDispositionConfiguration(dispositionConfigurationManager));
        return configurationItemTypes;
    }
}


//public class IssueDispositionCF extends AbstractSingleFieldType<BigDecimal> {
//
//    protected IssueDispositionCF(CustomFieldValuePersister customFieldValuePersister, GenericConfigManager genericConfigManager) {
//        super(customFieldValuePersister, genericConfigManager);
//    }
//
//    @Override
//    @NotNull
//    protected PersistenceFieldType getDatabaseType() {
//        return PersistenceFieldType.
//    }
//
//    @Override
//    @Nullable
//    protected Object getDbValueFromObject(BigDecimal bigDecimal) {
//        return getStringFromSingularObject(bigDecimal);
//    }
//
//    @Override
//    @Nullable
//    protected BigDecimal getObjectFromDbValue(@NotNull final Object databaseValue) throws FieldValidationException {
//        return getSingularObjectFromString((String) databaseValue);
//    }
//
//    @Override
//    @Nullable
//    public String getStringFromSingularObject(BigDecimal bigDecimal) {
//        if (bigDecimal == null) {
//            return null;
//        } else {
//            return bigDecimal.toString();
//        }
//    }
//
//    @Override
//    @Nullable
//    public BigDecimal getSingularObjectFromString(String string) throws FieldValidationException {
//        if (string == null) {
//            return null;
//        }
//
//        try {
//            final BigDecimal bigDecimal = new BigDecimal(string);
//            // Check that we don't have too many decimal places
//            if (bigDecimal.scale() > 2) {
//                throw new FieldValidationException("Maximum of 2 decimal places are allowed.");
//            }
//            return bigDecimal.setScale(2);
//        } catch (NumberFormatException ex) {
//            throw new FieldValidationException("Not a valid number.");
//        }
//    }
//}
