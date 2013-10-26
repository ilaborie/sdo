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


# Run on Ready
$ ->
  path = window.location.pathname
  comite = new Comite(path);
  # Routes
  sammy = Sammy("#leftMenu",->
    # Comite page
    @get "#comite", () ->
      url = comite.path + "/body"
      comite.loadBody url, "#comite"
    @get "#single", () ->
      url = comite.path + "/mens"
      comite.loadBody url, "#single"
    @get "#ladies", () ->
      url = comite.path + "/ladies"
      comite.loadBody url, "#ladies"
    @get "#youth", () ->
      url = comite.path + "/youth"
      comite.loadBody url, "#youth"
    @get "#pairs", () ->
      url = comite.path + "/pairs"
      comite.loadBody url, "#pairs"
    @get "#team", () ->
      url = comite.path + "/team"
      comite.loadBody url, "#team"
    @get "#tour/:name", () ->
      name = this.params.name
      url = comite.path + "/tour/" + name
      path = "#tour/" + name
      comite.loadBody url, path
    @get "#club/:name", () ->
      name = this.params.name
      url = comite.path + "/clubs/" + name
      path = "#club/" + name
      comite.loadBody url, path
  )
  # Binding
  ko.applyBindings(comite)

  # Intercept internal navigation
  $(document).delegate "a.sammy", "click", (linkElement) ->
    path = linkElement.currentTarget.href.replace /^[^#]*/, ""
    sammy.runRoute "get", path
    true

  # Default page
  hash = if (window.location.hash) then window.location.hash else "#comite"
  sammy.runRoute "get", hash
