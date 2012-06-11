AJS.toInit(function() {

    var helper = function(e, tr)
    {
        var $originals = tr.children();
        var $helper = tr.clone();
        $helper.children().each(function(index)
        {
            // Set helper cell sizes to match the original sizes
            AJS.$(this).width($originals.eq(index).width())
        });
        return $helper;
    }

    AJS.$('#issuetable tbody').sortable({
        axis: 'y',
        helper: helper,

        update: function(event, ui) {

            var that = this;

            var high = AJS.$(ui.item).prev().data('issuekey') || '';
            var dragged = AJS.$(ui.item).data('issuekey');
            var low = AJS.$(ui.item).next().data('issuekey') || '';

            var params = AJS.template("high={high}&dragged={dragged}&low={low}").fill({"high":high, "dragged":dragged, "low":low})

            JIRA.SmartAjax.makeRequest({
                url: contextPath + "/rest/issue-disposition/1.0/disposition?" + params,
                complete: function (xhr, textStatus, smartAjaxResult) {
                    if (smartAjaxResult.successful){
                        JIRA.IssueNavigator.reload();
                    }
                    else {
                        AJS.$(that).sortable('cancel');

                        var data = JSON.parse(smartAjaxResult.data);
                        JIRA.Messages.showMsg(data.message, {'type':JIRA.Messages.Types.ERROR});
                    }
                }
            });
        }
    }).disableSelection();
});