package ru.mail.jira.plugins.disposition.web;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.web.util.CookieUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * @author g.chernyshev
 */
public class CookieHelper {

    public static final String AJS_CONGLOMERATE_COOKIE = "AJS.conglomerate.cookie";
    public static final String CONGLOMERATE_COOKIE_KEY = "disposition";


    /**
     * Get last reindexed user from request cookie
     *
     * @param request - Request to get cookie from
     * @return - found {@link com.atlassian.crowd.embedded.api.User} or null
     */
    @Nullable
    public static User getUserFromCookie(HttpServletRequest request) {
        String username = getConglomerateCookieValue(AJS_CONGLOMERATE_COOKIE, CONGLOMERATE_COOKIE_KEY, request);
        return ComponentManager.getInstance().getUserUtil().getUser(username);
    }

    /**
     * Get users for changing disposition
     *
     * @param request - Request to get cookie from
     * @return - all users, fow which disposition change can be applied
     */
    @NotNull
    public static Collection<User> getUsers(HttpServletRequest request) {
        Collection<User> users = new ArrayList<User>();
        users.add(ComponentManager.getInstance().getJiraAuthenticationContext().getLoggedInUser());
        users.add(getUserFromCookie(request));

        return users;
    }

    /**
     * Retrieve the value from a conglomerate Cookie from the request.
     * <p>Why this not in {@link com.atlassian.jira.web.util.CookieUtils} ?</p>
     *
     * @param cookieName The name of the conglomerate cookie
     * @param key        The key of the value
     * @param request    Request to get cookie from
     * @return the value (or the empty-string if it did not exist)
     */
    @NotNull
    public static String getConglomerateCookieValue(String cookieName, String key, HttpServletRequest request) {
        Map<String, String> map = CookieUtils.parseConglomerateCookie(cookieName, request);
        String value = map.get(key);
        return value != null ? value : "";
    }

}
