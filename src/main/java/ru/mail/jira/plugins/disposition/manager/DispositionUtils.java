package ru.mail.jira.plugins.disposition.manager;

/**
 * @author g.chernyshev
 */


public class DispositionUtils {

    private static final ThreadLocal<Boolean> skipShift = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return Boolean.FALSE;
        }
    };

    public static void setSkipShift(final boolean index) {
        DispositionUtils.skipShift.set(index);
    }


    public static boolean isSkipShift() {
        final Boolean isSkipShift = DispositionUtils.skipShift.get();

        return isSkipShift;
    }
}
