# Basic script
$ ->
  # Register PostLoadEvent
  $("#bodyEvents").on "postLoad", () ->
    registerFilter()
    registerPopover()


# Filtering events
registerFilter = () ->
  $("#eventTypeFilter button").click () ->
    active = $(this).hasClass "active"
    filter = $(this).attr "data-filter"
    if (active)
      $("." + filter).show()
    else
      $("." + filter).hide()

registerPopover = () ->
  $("td.event").popover {
    html: true,
    placement: autoPlacement,
    trigger: "hover",
    title: getTitle,
    container: "#bodyEvents",
    content: getContent
  }

getTitle = () ->
  content = $(this).find ".title.hidden"
  content.html()

getContent = () ->
  content = $(this).find ".content.hidden"
  content.html()


autoPlacement = (tip, element) ->
  offset = $(element).offset()
  height = $(document).outerHeight()
  width = $(document).outerWidth()
  vert = 0.5 * height - offset.top
  horiz = 0.5 * width - offset.left
  if (Math.abs(horiz) > Math.abs(vert))
    if (horiz > 0)
      'right'
    else
      'left'
  else
    if (vert > 0)
      'bottom'
    else
      'top'
