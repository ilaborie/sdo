# Player
class Player
  constructor: (@name, @license) ->
    self = @
    self.name = @name
    self.license = @license
    self.toJson = () ->
      {
      name: self.name,
      license: self.license
      }

# Pair
class Pair
  constructor: ->
    self = @
    self.j1 = ko.observable()
    self.j2 = ko.observable()
    self.toJson = () ->
      [self.j1(), self.j2() ]

# Team
class Team
  constructor: (@name) ->
    # Data
    self = @
    self.name = @name
    self.player1 = ko.observable()
    self.player2 = ko.observable()
    self.player3 = ko.observable()
    self.player4 = ko.observable()
    self.d1 = ko.observable()
    self.d2 = ko.observable()
    self.psubs = ko.observable()
    self.isSubst = ko.observable(false)
    self.psubsWho = ko.observable()
    self.psubsWhen = ko.observable()
    self.capitain = ko.observable()
    self.signed = ko.observable(false)

    # Behavior
    self.sign = () ->
     self.signed(!self.signed())
    self.substitue = () ->
      self.isSubst(true)
      # FIXME get last finished match
      # FIXME open dialog for Who
    self.toJson = () ->
      name: self.name,
      players: [
        self.player1(),
        self.player2(),
        self.player3(),
        self.player4()],
      d1: self.d1(),
      d2: self.d2(),
      psubs: self.psubs(),
      "psubs-who": self.psubsWho(),
      "psubs-when": self.psubsWhen(),
      capitain: self.capitain()

# Match
class ChampionshipDay
  constructor: ->
    # Data
    self = @
    self.day = $("#day").val()
    self.date = ko.observable()
    self.location = ko.observable()
    self.team1 = ko.observable()
    self.team2 = ko.observable()
    self.started = ko.observable(false)
    self.matches = ko.observableArray([])

    # Behavior
    self.start = () ->
      # FIXME Check can start
      self.started(true)
    self.send = () ->
      json =
        day: self.day,
        date: self.date(),
        location: self.location(),
        team1: self.team1().toJson(),
        team2: self.team1().toJson(),
        matches: self.matches()
      console.log json

    # Run
    self.team1Players = []
    self.team2Players = []
    # FIXME current Date...
    # TODO Load Teams...
    self.team1(new Team("team1"))
    self.team2(new Team("team2"))

# Run on Ready
$ ->
  champDay = new ChampionshipDay();
  ko.applyBindings(champDay)
  # FIXME typeahead
