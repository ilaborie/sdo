# Teams
class Match
  constructor: ->
    # Data
    self = @
    self.date = ko.observable()
    self.location = ko.observable()
    self.team1 = ko.observable()
    self.team2 = ko.observable()

    # Behavior
    #self.loadBody = (url, path) ->

    # Run
    # Load Teams...

# Run on Ready
$ ->
  match = new Match();
  ko.applyBindings(match)
