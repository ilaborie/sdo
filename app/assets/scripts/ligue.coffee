# Ligue
class Ligue
  constructor: (@path) ->
    # Data
    self = @
    self.path = @path
    self.samPath = ko.observable()

    # Behavior
    self.loadBody = (url, path) ->
      $("#bodyLigue").load url, () ->
      self.samPath(path)

    # Routes
    Sammy("#leftMenu",->
      # Ligue
      @get "#ligue", () ->
        url = self.path + "/body"
        self.loadBody url, "#ligue"
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
      @get "comites/:name", () ->
        name = this.params.name
        url = self.path + "/comites/" + name
        window.location = url
      @get "/sdo/:name", () ->
        name = this.params.name
        url = "/sdo/" + name
        window.location = url
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
      @get "/sdo/ligues/:ligue/comites/:comite", (context) ->
        url = context.path
        window.location = url
        window.location.reload()
      @get "/sdo/ligues/:ligue/comites/:comite/:tour", (context) ->
        url = context.path
        window.location = url
        window.location.reload()
      @get "/sdo/ligues/:ligue/team/day/:day/:team1:team2", (context) ->
        url = context.path
        window.open(url)
      # Default
      @.get "", () ->
        @.app.runRoute("get", "#ligue")
    ).run()


# Run on Ready
$ ->
  path = window.location.pathname
  ligue = new Ligue(path);
  ko.applyBindings(ligue)
