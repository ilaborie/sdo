$(function () {
    // rank team
    $("#teamsRank").click(showTeamsRank);

});

var showTeamsRank = function () {
    var url = window.location.pathname + "/team";
    $("#bodyComite").load(url);
};
