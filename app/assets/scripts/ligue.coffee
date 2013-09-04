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

# Run on Ready
$ ->
  path = window.location.pathname
  ligue = new Ligue(path);
  # Routes
  sammy = Sammy("#leftMenu", ->
    # Ligue
    @get "#ligue", () ->
      url = ligue.path + "/body"
      ligue.loadBody url, "#ligue"
    @get "#single", () ->
      url = ligue.path + "/single"
      ligue.loadBody url, "#single"
    @get "#women", () ->
      url = ligue.path + "/feminine"
      ligue.loadBody url, "#women"
    @get "#junior", () ->
      url = ligue.path + "/junior"
      ligue.loadBody url, "#junior"
    @get "#pair", () ->
      url = ligue.path + "/double"
      ligue.loadBody url, "#pair"
    @get "#team", () ->
      url = ligue.path + "/team"
      ligue.loadBody url, "#team"
    @get "#tour/:name", () ->
      name = this.params.name
      url = ligue.path + "/tour/" + name
      path = "#tour/" + name
      ligue.loadBody url, path
  )
  # Binding
  ko.applyBindings(ligue)

  # Intercept internal navigation
  $(document).delegate "a.sammy", "click", (linkElement) ->
    path = linkElement.currentTarget.href.replace /^[^#]*/, ""
    sammy.runRoute "get", path
    true

  # Default page
  hash = if (window.location.hash) then window.location.hash else "#ligue"
  sammy.runRoute "get", hash
