AJS.$(function () {

    JIRA.Dialogs.deleteIssue = new JIRA.FormDialog({
        id: "disposition-issue-dialog",
        trigger: "a.issueaction-disposition-issue",
        ajaxOptions: JIRA.Dialogs.getDefaultAjaxOptions,
        onSuccessfulSubmit : JIRA.Dialogs.storeCurrentIssueIdOnSucessfulSubmit,
        issueMsg : AJS.I18n.getText('ru.mail.jira.plugins.disposition.web.change.success'),
        onContentRefresh: function () {
            jQuery(".overflow-ellipsis").textOverflow();
        }
    });

});