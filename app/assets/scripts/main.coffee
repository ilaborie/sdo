# Basic script
$ ->
  # Handle Mailto
  $("a.mailto").each (index, link) ->
    $link = $(link)
    href = "mailto:#{$link.attr 'data-name'}@#{$link.attr 'data-server'}"
    $link.attr "href", href
