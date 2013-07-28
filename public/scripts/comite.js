$(function () {
    // Menu Link
    $(".nav-list a").click(openInBody);

});

var openInBody = function (event) {
    $(".nav-list li").removeClass("active");
    var url = $(this).attr("href");
    var $this = $(this);
    $("#bodyComite").load(url, function() {
        $this.parent().addClass("active");
    });
    event.preventDefault();
    return false;
};
