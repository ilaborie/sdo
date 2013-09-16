# Events
class Events
  constructor: ->
    # Data
    self = @
    self.path = "/sdo/events"
    self.samPath = ko.observable()

    # Behavior
    self.loadBody = (url, path) ->
      $("#bodyEvents").load url, () ->
        self.samPath(path)
        registerFilter()
        registerPopover()
        scrollToUnMuted()

# Run on Ready
$ ->
  events = new Events();
  # Routes
  sammy = Sammy("#leftMenu", ->
    @get "#list", () ->
      url = events.path + "/list"
      events.loadBody url, "#list"
    @get "#calendar", () ->
      url = events.path + "/calendar"
      events.loadBody url, "#calendar"
  )
  # Binding
  ko.applyBindings(events)

  # Intercept internal navigation
  $(document).delegate "a.sammy", "click", (linkElement) ->
    path = linkElement.currentTarget.href.replace /^[^#]*/, ""
    sammy.runRoute "get", path
    true

  # Default page
  hash = if (window.location.hash) then window.location.hash else "#list"
  sammy.runRoute "get", hash
  scrollToUnMuted()


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

scrollToUnMuted = () ->
  muted = $(".event.muted:last")
  if muted.length > 0
    window.scroll(0, muted.next().offset().top)

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
