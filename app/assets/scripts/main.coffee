# Basic script
$ ->
  # Handle Mailto
  $("a.mailto").each (index, link) ->
    $link = $(link)
    href = "mailto:#{$link.attr 'data-name'}@#{$link.attr 'data-server'}"
    $link.attr "href", href

  # Navbar activation
  path = window.location.pathname
  $(".navbar .nav a").each (index, elt) ->
    href = $(elt).attr("href")
    if (href == path)
      $(elt).parent().addClass "active"

  # Auto Login
  $("#autoLogin").click () ->
    $("#username").val "ilaborie@gmail.com"
    $("#password").val "plop"
    $("form").submit
