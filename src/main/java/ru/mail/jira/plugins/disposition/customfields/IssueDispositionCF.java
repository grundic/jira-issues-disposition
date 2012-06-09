package ru.mail.jira.plugins.disposition.customfields;

import com.atlassian.jira.issue.customfields.converters.DoubleConverter;
import com.atlassian.jira.issue.customfields.impl.NumberCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;

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

    public IssueDispositionCF(CustomFieldValuePersister customFieldValuePersister, DoubleConverter doubleConverter, GenericConfigManager genericConfigManager) {
        super(customFieldValuePersister, doubleConverter, genericConfigManager);
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
