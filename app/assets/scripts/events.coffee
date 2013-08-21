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

    # Routes
    Sammy("#leftMenu",->
      @get "#list", () ->
        url = self.path + "/list"
        self.loadBody url, "#list"
      @get "#calendar", () ->
        url = self.path + "/calendar"
        self.loadBody url, "#calendar"
      # Basic Navigation
      @get "/sdo/", (context) ->
        url = context.path
        window.location = url
        window.location.reload()
      @get "/sdo/contacts", (context) ->
        url = context.path
        window.location = url
        window.location.reload()
      @get "/sdo/ligues/:ligue", (context) ->
        url = context.path
        window.location = url
        window.location.reload()
      @get "/sdo/ligues/:ligue/:tour", (context) ->
        url = context.path
        window.location = url
        window.location.reload()
      @get "/sdo/ligues/:ligue/comites/:comite", (context) ->
        url = context.path
        window.location = url
        window.location.reload()
      @get "/sdo/ligues/:ligue/comites/:comite/:tour", (context) ->
        url = context.path
        window.location = url
        window.location.reload()
      # Default link
      @.get "", () ->
        @.app.runRoute("get", "#list")
    ).run()


# Run on Ready
$ ->
  events = new Events();
  ko.applyBindings(events)

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
