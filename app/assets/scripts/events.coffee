# Basic script
$ ->
  # Register PostLoadEvent
  $("#bodyEvents").on "postLoad", registerFilterAction


# Filtering events
registerFilterAction = () ->
  $("#eventTypeFilter button").click () ->
    active = $(this).hasClass "active"
    filter = $(this).attr "data-filter"
    if (active)
      $("." + filter).show()
    else
      $("." + filter).hide()
