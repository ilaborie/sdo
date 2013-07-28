$(function () {
    // Menu Link
    $(".nav-list a").click(openMenu);

});

var openMenu = function (event) {
    var $this = $(this);
    var target = $this.attr("data-target");
    if (target) {
        $(".nav-list li").removeClass("active");
        var url = $this.attr("href");
        $(target).load(url, function () {
            $this.parent().addClass("active");
        });
        event.preventDefault();
        return false;
    } else {
        return true;
    }
};
