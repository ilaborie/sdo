# Ligue
class Ligue
  constructor: (@path) ->
    # Data
    self = @
    self.path = @path
    self.samPath = ko.observable()
    # Behavior
    # Routes
    Sammy(->
      @get "#ligue", () ->
        url = self.path + "/body"
        $("#bodyLigue").load url, () ->
          self.samPath("#ligue")
        @
      @get "#single", () ->
        url = self.path + "/single"
        $("#bodyLigue").load url, () ->
          self.samPath("#single")
        @
      @get "#women", () ->
        url = self.path + "/feminine"
        $("#bodyLigue").load url, () ->
          self.samPath("#women")
        @
      @get "#junior", () ->
        url = self.path + "/junior"
        $("#bodyLigue").load url, () ->
          self.samPath("#junior")
        @
      @get "#pair", () ->
        url = self.path + "/double"
        $("#bodyLigue").load url, () ->
          self.samPath("#pair")
        @
      @get "#team", () ->
        url = self.path + "/team"
        $("#bodyLigue").load url, () ->
          self.samPath("#team")
        @
      @get "#tour/:name", () ->
        name = this.params.name
        url = self.path + "/tour/" + name
        $("#bodyLigue").load url, () ->
          self.samPath("#tour/"+name)
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
