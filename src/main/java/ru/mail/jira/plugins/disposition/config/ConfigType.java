package ru.mail.jira.plugins.disposition.config;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author g.chernyshev
 */

/**
 * Types of Jql configurations
 */
public class ConfigType {

    public final static int RAW_JQL = 1;
    public final static int SAVED_FILTER = 2;

    private static final Map<Integer, String> types;

    static {
        Map<Integer, String> aMap = new LinkedHashMap<Integer, String>();
        aMap.put(RAW_JQL, "ru.mail.jira.plugins.disposition.web.jql");
        aMap.put(SAVED_FILTER, "ru.mail.jira.plugins.disposition.web.saved.filter");

        types = Collections.unmodifiableMap(aMap);
    }

    public static Map<Integer, String> getTypes() {
        return types;
    }
}
