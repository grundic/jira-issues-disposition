AJS.$(function () {

    JIRA.Dialogs.deleteIssue = new JIRA.FormDialog({
        id: "disposition-issue-dialog",
        trigger: "a.issueaction-disposition-issue",
        //targetUrl: "#delete-issue-return-url",
        ajaxOptions: JIRA.Dialogs.getDefaultAjaxOptions,
        onSuccessfulSubmit : JIRA.Dialogs.storeCurrentIssueIdOnSucessfulSubmit,
        issueMsg : 'thanks_for_disposition',
        onContentRefresh: function () {
            jQuery(".overflow-ellipsis").textOverflow();
        }
    });

});