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
  constructor: (@name, @players) ->
    # Data
    self = @
    self.name = @name
    self.remainingPlayers = ko.observableArray @players
    self.registeredPlayers = ko.observableArray []
    self.player1 = ko.observable()
    self.player2 = ko.observable()
    self.player3 = ko.observable()
    self.player4 = ko.observable()
    self.d1 = ko.observable new Pair()
    self.d2 = ko.observable new Pair()
    self.psubs = ko.observable()
    self.isSubst = ko.observable false
    self.psubsWho = ko.observable()
    self.psubsWhen = ko.observable()
    self.capitain = ko.observable()
    self.signed = ko.observable false

    self.canSend = ko.computed () ->
      self.signed() and !!self.capitain()

    # Behavior
    self.register = (data, event) ->
      player = $(event.target).val()
      self.remainingPlayers.remove player
      self.registeredPlayers.push player
    self.unregister = (data, event) ->
      player = $(event.target).val()
      self.remainingPlayers.push player
      self.registeredPlayers.remove player
    self.registerPair = (data, event) ->
      player = $(event.target).val()
      self.registeredPlayers.remove player
    self.unregisterPair = (data, event) ->
      player = $(event.target).val()
      self.registeredPlayers.push player
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
class Match
  constructor: (@start, @team1, @team2)->
    # Data
    self = @
    self.team1Start = ko.observable(@start)
    self.team1 = @team1
    self.team2 = @team2
    self.player1 = ko.observable @team1
    self.player2 = ko.observable @team2
    self.leg1 = ko.observable()
    self.leg2 = ko.observable()
    self.leg3 = ko.observable()
    self.team1Win = ko.observable false
    self.team2Win = ko.observable false
    self.team1Leg = ko.observable 0
    self.team1Leg = ko.observable 0
    self.isFinished = ko.computed () ->
      false

    # Behavior
    self.updateTeam1 = (team) ->
      # FIXME set player1
      false
    self.updateTeam2 = (team) ->
      # FIXME set player1
      false
    self.nextLeg = (winner) ->
      # FIXME compute
      false

# ChampionshipDay
class ChampionshipDay
  constructor: (@day, @team1, @team2, @matches)->
    # Data
    self = @
    self.day = @day
    self.date = ko.observable()
    self.location = ko.observable()
    self.team1 = ko.observable @team1
    self.team2 = ko.observable @team2
    self.started = ko.observable false
    self.matches = ko.observableArray @matches
    self.finished = ko.computed () ->
      # FIXME Check with matches
      true
    self.canStart = ko.computed () ->
      # FIXME Check can start
      true

    # Behavior
    self.start = () ->
      # FIXME update matches
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
    self.date(today)

# Run on Ready
$ ->
  matches = for m in allMatches
    new Match m.team1Start, m.team1, m.team2
  team1 = new Team team1Name, players1
  team2 = new Team team2Name, players2
  champDay = new ChampionshipDay day, team1, team2, matches
  ko.applyBindings champDay
