package ru.mail.jira.plugins.disposition.rest;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.jql.parser.JqlParseException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.mail.jira.plugins.disposition.manager.DispositionManager;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collection;

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

    public DispositionResource(@NotNull DispositionManager dispositionManager, @NotNull IssueManager issueManager) {
        this.dispositionManager = dispositionManager;
        this.issueManager = issueManager;
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
                                   @Nullable @QueryParam("low") final String low) {


        Issue highIssue = issueManager.getIssueObject(high);
        Issue draggedIssue = issueManager.getIssueObject(dragged);
        Issue lowIssue = issueManager.getIssueObject(low);

        Collection<String> errors = new ArrayList<String>();

        try {
            dispositionManager.setDisposition(highIssue, draggedIssue, lowIssue, errors);
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

    @Path("/check")
    @GET
    public Response parseJqlQuery() {

        Collection<String> errors = new ArrayList<String>();
        errors.add("Error-1");
        errors.add("Error-2");
        errors.add("Error-3");
        errors.add("Error-4");

        return Response.status(Response.Status.BAD_REQUEST).
                type(MediaType.APPLICATION_JSON).
                entity(new ErrorListEntity(Response.Status.BAD_REQUEST, errors)).
                build();
    }

}
