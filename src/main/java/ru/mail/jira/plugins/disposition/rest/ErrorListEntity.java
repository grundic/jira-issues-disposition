package ru.mail.jira.plugins.disposition.rest;

import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author g.chernyshev
 */

@SuppressWarnings("unused")
@XmlRootElement(name = "error")
public class ErrorListEntity {
    @XmlAttribute
    private int status;

    @XmlElement(name = "message")
    private Collection<String> errors;

    public ErrorListEntity() {
    }

    public ErrorListEntity(int status, String... errors) {
        this(status, Arrays.asList(errors));
    }

    public ErrorListEntity(int status, Collection<String> errors) {
        this.status = status;
        this.errors = errors;
    }

    public ErrorListEntity(Status status, String... errors) {
        this(status, Arrays.asList(errors));
    }

    public ErrorListEntity(Status status, Collection<String> errors) {
        this.status = status.getStatusCode();
        this.errors = errors;
    }

    public Collection<String> getErrors() {
        return errors;
    }

    public int getStatus() {
        return status;
    }
}

