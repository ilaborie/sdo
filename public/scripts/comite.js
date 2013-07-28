$(function () {
    // Menu Link
    $(".nav-list a").click(openInBody);

});

var openInBody = function (event) {
    var url = $(this).attr("href");
    $("#bodyComite").load(url);

    event.preventDefault();
    return false;
};
