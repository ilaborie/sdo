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
    title: getTitle,
    container: "body",
    content: getContent
  }

getTitle = () ->
  content = $(this).find ".title.hidden"
  content.html()

getContent = () ->
  content = $(this).find ".content.hidden"
  content.html()
