# Comite
class Ligue
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
      @get "#comite", () ->
        url = self.path + "/body"
        self.loadBody url, "#comite"
        @
      @get "#single", () ->
        url = self.path + "/single"
        self.loadBody url, "#single"
        @
      @get "#women", () ->
        url = self.path + "/feminine"
        self.loadBody url, "#women"
        @
      @get "#junior", () ->
        url = self.path + "/junior"
        self.loadBody url, "#junior"
        @
      @get "#pair", () ->
        url = self.path + "/double"
        self.loadBody url, "#pair"
        @
      @get "#team", () ->
        url = self.path + "/team"
        self.loadBody url, "#team"
        @
      @get "#tour/:name", () ->
        name = this.params.name
        url = self.path + "/tour/" + name
        path = "#tour/" + name
        self.loadBody url, path
        @
      @get "#club/:name", () ->
        name = this.params.name
        url = self.path + "/clubs/" + name
        path = "#club/" + name
        self.loadBody url, path
        @
      @get "/sdo/:name", () ->
        name = this.params.name
        url = "/sdo/" + name
        window.location = url
        @
      @.get "", () ->
        @.app.runRoute("get", "#comite")
        @
    ).run()


# Run on Ready
$ ->
  path = window.location.pathname
  ligue = new Ligue(path);
  ko.applyBindings(ligue)
  @
