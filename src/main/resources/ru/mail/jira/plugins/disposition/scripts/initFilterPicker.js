(function($) {

    JIRA.bind(JIRA.Events.NEW_CONTENT_ADDED, function (e, context) {
            AJS.$('#filter_filterId_name').click(function(e){
                var userpref = "filterId";
                var url = contextPath + "/secure/FilterPickerPopup.jspa?showProjects=false&field=" + userpref;
                var windowVal = "filter_" + userpref + "_window";
                var prefs = "width=800, height=500, resizable, scrollbars=yes";

                var newWindow = window.open(url, windowVal, prefs);
                newWindow.focus();
                e.preventDefault();
            });
    });
})(AJS.$);