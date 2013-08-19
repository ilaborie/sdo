# Basic script
$ ->
  # Ajax list navitation
  $(".nav-list a[data-target]").click doNavigation

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
  deepLink = window.location.href.split("#")[1]
  if (deepLink)
    $("#" + deepLink).click()
    window.scrollTo(0, 0);
  else
    $("a.autoClick").click()

# Click on Internal link
doNavigation = (event) ->
  $this = $(this)
  target = $this.attr "data-target"
  $(".nav-list li").removeClass "active"
  url = $this.attr "data-href"
  $(target).load url, () ->
    $this.parent().addClass "active"
    $(target).trigger("postLoad")
  event.preventDefault
  false

