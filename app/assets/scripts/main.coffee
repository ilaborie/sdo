# Basic script
$ ->
  # Ajax list navitation
  $(".nav-list a[data-target]").click (event) ->
    $this = $(this)
    target = $this.attr "data-target"
    $(".nav-list li").removeClass "active"
    url = $this.attr "href"
    $(target).load url, () ->
      $this.parent().addClass "active"
    event.preventDefault
    false

  # Handle Mailto
  $("a.mailto").each (index, link) ->
    $link = $(link)
    href = "mailto:#{$link.attr 'data-name'}@#{$link.attr 'data-server'}"
    $link.attr "href", href

  # Navbar activation
  $(".navbar .nav a").each (index, elt) ->
    href = $(elt).attr("href")
    if (href == window.location.pathname)
      $(elt).parent().addClass "active"

  # AutoClick
  $("a.autoClick").click()
