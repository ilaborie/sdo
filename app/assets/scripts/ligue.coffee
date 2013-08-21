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
    Sammy(->
      @get "#ligue", () ->
        url = self.path + "/body"
        self.loadBody url, "#ligue"
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
      @get "comites/:name", () ->
        name = this.params.name
        url = self.path + "/comites/" + name
        window.location = url
        @
      @.get "", () ->
        @.app.runRoute("get", "#ligue")
        @
    ).run()


# Run on Ready
$ ->
  path = window.location.pathname
  ligue = new Ligue(path);
  ko.applyBindings(ligue)
  @
