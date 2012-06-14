package ru.mail.jira.plugins.disposition.rest;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.jql.parser.JqlParseException;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.jira.web.util.CookieUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.mail.jira.plugins.disposition.manager.DispositionManager;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;


/**
 * User: g.chernyshev
 * Date: 6/8/12
 * Time: 4:20 PM
 */

@Path("/")
@Produces({MediaType.APPLICATION_JSON})
public class DispositionResource {

    @NotNull
    private final DispositionManager dispositionManager;
    @NotNull
    private final IssueManager issueManager;
    @NotNull
    private final UserUtil userUtil;

    public static final String AJS_CONGLOMERATE_COOKIE = "AJS.conglomerate.cookie";
    public static final String CONGLOMERATE_COOKIE_KEY = "disposition";

    public DispositionResource(@NotNull DispositionManager dispositionManager, @NotNull IssueManager issueManager, @NotNull UserUtil userUtil) {
        this.dispositionManager = dispositionManager;
        this.issueManager = issueManager;
        this.userUtil = userUtil;
    }


    @Path("/set-disposition-value")
    @GET
    public Response setDisposition(@Nullable @QueryParam("issue") final String issue,
                                   @Nullable @QueryParam("value") final Double value) {

        if (null == issue || null == value) {
            return null;
        }

        Issue issueObject = issueManager.getIssueObject(issue);

        Collection<String> errors = new ArrayList<String>();

        try {
            dispositionManager.setDisposition(issueObject, value, errors);
        } catch (JqlParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (SearchException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        if (errors.size() > 0) {
            return badRequest(errors);
        }

        return Response.ok().build();
    }

    @Path("/disposition")
    @GET
    public Response setDisposition(@Nullable @QueryParam("high") final String high,
                                   @Nullable @QueryParam("dragged") final String dragged,
                                   @Nullable @QueryParam("low") final String low,
                                   @Context HttpServletRequest request) {

        Issue highIssue = issueManager.getIssueObject(high);
        Issue draggedIssue = issueManager.getIssueObject(dragged);
        Issue lowIssue = issueManager.getIssueObject(low);

        Collection<String> errors = new ArrayList<String>();

        try {
            dispositionManager.setDisposition(highIssue, draggedIssue, lowIssue, getUsers(request), errors);
        } catch (SearchException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (JqlParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        if (errors.size() > 0) {
            return badRequest(errors);
        }

        return Response.ok().build();
    }

    private Response badRequest(final Collection<String> errors) {
        return Response.status(Response.Status.BAD_REQUEST).
                type(MediaType.APPLICATION_JSON).
                entity(new ErrorListEntity(Response.Status.BAD_REQUEST, errors)).
                build();
    }

    /**
     * Get last reindexed user from request cookie
     *
     * @param request - Request to get cookie from
     * @return - found {@link User} or null
     */
    @Nullable
    private User getUserFromCookie(HttpServletRequest request) {
        String username = getConglomerateCookieValue(AJS_CONGLOMERATE_COOKIE, CONGLOMERATE_COOKIE_KEY, request);
        return userUtil.getUser(username);
    }

    /**
     * Get users for changing disposition
     * @param request - Request to get cookie from
     * @return - all users, fow which disposition change can be applied
     */
    @NotNull
    private Collection<User> getUsers(HttpServletRequest request) {
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
    private String getConglomerateCookieValue(String cookieName, String key, HttpServletRequest request) {
        Map<String, String> map = CookieUtils.parseConglomerateCookie(cookieName, request);
        String value = map.get(key);
        return value != null ? value : "";
    }


    @GET()
    @Path("/check")
    public Response parseJqlQuery(@Context HttpServletRequest request) {
        String cookie = getConglomerateCookieValue(AJS_CONGLOMERATE_COOKIE, CONGLOMERATE_COOKIE_KEY, request);
        return Response.ok(cookie).build();
    }
}
