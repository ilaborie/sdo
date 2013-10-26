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
      url = ligue.path + "/mens"
      ligue.loadBody url, "#single"
    @get "#ladies", () ->
      url = ligue.path + "/ladies"
      ligue.loadBody url, "#ladies"
    @get "#youth", () ->
      url = ligue.path + "/youth"
      ligue.loadBody url, "#youth"
    @get "#pairs", () ->
      url = ligue.path + "/pairs"
      ligue.loadBody url, "#pairs"
    @get "#team", () ->
      url = ligue.path + "/team"
      ligue.loadBody url, "#team"
    @get "#ic-single", () ->
      url = ligue.path + "/ic-mens"
      ligue.loadBody url, "#ic-single"
    @get "#ic-ladies", () ->
      url = ligue.path + "/ic-ladies"
      ligue.loadBody url, "#ic-ladies"
    @get "#ic-youth", () ->
      url = ligue.path + "/ic-youth"
      ligue.loadBody url, "#ic-youth"
    @get "#ic-pairs", () ->
      url = ligue.path + "/ic-pairs"
      ligue.loadBody url, "#ic-pairs"
    @get "#ic-team", () ->
      url = ligue.path + "/ic-team"
      ligue.loadBody url, "#ic-team"
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
