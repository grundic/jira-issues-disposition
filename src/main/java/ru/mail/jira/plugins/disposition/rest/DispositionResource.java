package ru.mail.jira.plugins.disposition.rest;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.jql.parser.JqlParseException;
import com.atlassian.jira.jql.parser.JqlQueryParser;
import com.atlassian.query.Query;
import com.atlassian.query.clause.Clause;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.mail.jira.plugins.disposition.manager.DispositionManager;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * User: g.chernyshev
 * Date: 6/8/12
 * Time: 4:20 PM
 */

@Path("/")
@Produces({MediaType.APPLICATION_JSON})
public class DispositionResource {

    @NotNull
    private final JqlQueryParser jqlQueryParser;
    @NotNull
    private final DispositionManager dispositionManager;
    @NotNull
    private final IssueManager issueManager;

    public DispositionResource(@NotNull JqlQueryParser jqlQueryParser, @NotNull DispositionManager dispositionManager, @NotNull IssueManager issueManager) {
        this.jqlQueryParser = jqlQueryParser;
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

        try {
            dispositionManager.setDisposition(issueObject, value);
        } catch (JqlParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (SearchException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return null;
    }

    @Path("/disposition")
    @GET
    public Response setDisposition(@Nullable @QueryParam("high") final String high,
                                   @Nullable @QueryParam("dragged") final String dragged,
                                   @Nullable @QueryParam("low") final String low) {


        Issue highIssue = issueManager.getIssueObject(high);
        Issue draggedIssue = issueManager.getIssueObject(dragged);
        Issue lowIssue = issueManager.getIssueObject(low);


        try {
            dispositionManager.setDisposition(highIssue, draggedIssue, lowIssue);
        } catch (SearchException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (JqlParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return null;
    }

    @Path("/check")
    @GET
    public Response parseJqlQuery(@Nullable @QueryParam("q") final String q) {
        if (null == q) {
            return null;
        }

        Query query;
        try {
            query = jqlQueryParser.parseQuery(q);
        } catch (JqlParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        Collection<String> fields = clauseRecursion(query.getWhereClause().getClauses());

        System.out.println("\n\n" + "-------------------------------------------------");
        for (String field : fields) {
            System.out.println(field);
        }

        return null;
    }


    private Collection<String> clauseRecursion(Collection<Clause> clauses) {

        Collection<String> fields = new LinkedHashSet<String>();

        for (Clause clause : clauses) {
            if (clause.getClauses() != null && clause.getClauses().size() > 0) {
                fields.addAll(clauseRecursion(clause.getClauses()));
            } else {
                fields.add(clause.getName());
            }
        }

        return fields;
    }

}
