# Basic script
$ ->
  $(".nav-list a").click (event) ->
    $this = $(this)
    target = $this.attr "data-target"
    if target
      $(".nav-list li").removeClass "active"
      url = $this.attr "href"
      $(target).load url, (event) ->
        $this.parent.addClass "active"
      event.preventDefault
    !target
