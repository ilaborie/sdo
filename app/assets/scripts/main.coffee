# Basic script
$ ->
  $(".nav-list a[data-target]").click (event) ->
    $this = $(this)
    target = $this.attr "data-target"
    $(".nav-list li").removeClass "active"
    url = $this.attr "href"
    $(target).load url, () -> $this.parent().addClass "active"
    event.preventDefault
    false
