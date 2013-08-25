# Comite
class Comite
  constructor: (@path) ->
    # Data
    self = @
    self.path = @path
    self.samPath = ko.observable()
    # Behavior
    self.loadBody = (url, path) ->
      $("#bodyComite").load url, () ->
      self.samPath(path)

    # Routes
    Sammy("#leftMenu",->
      # Comite page
      @get "#comite", () ->
        url = self.path + "/body"
        self.loadBody url, "#comite"
      @get "#single", () ->
        url = self.path + "/single"
        self.loadBody url, "#single"
      @get "#women", () ->
        url = self.path + "/feminine"
        self.loadBody url, "#women"
      @get "#junior", () ->
        url = self.path + "/junior"
        self.loadBody url, "#junior"
      @get "#pair", () ->
        url = self.path + "/double"
        self.loadBody url, "#pair"
      @get "#team", () ->
        url = self.path + "/team"
        self.loadBody url, "#team"
      @get "#tour/:name", () ->
        name = this.params.name
        url = self.path + "/tour/" + name
        path = "#tour/" + name
        self.loadBody url, path
      @get "#club/:name", () ->
        name = this.params.name
        url = self.path + "/clubs/" + name
        path = "#club/" + name
        self.loadBody url, path
      # Basic Navigation
      @get "/sdo/", (context) ->
        url = context.path
        window.location = url
        window.location.reload()
      @get "/sdo/contacts", (context) ->
        url = context.path
        window.location = url
        window.location.reload()
      @get "/sdo/events", (context) ->
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
        if (url != self.path)
          window.location = url
          window.location.reload()
        else
          @.app.runRoute("get", "#comite")
      @get "/sdo/ligues/:ligue/team/day/:day/:team1/:team2", (context) ->
        url = context.path
        window.open(url, "_blank")
    ).run()


# Run on Ready
$ ->
  path = window.location.pathname
  comite = new Comite(path);
  ko.applyBindings(comite)
