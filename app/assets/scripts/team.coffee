# Pair
class Pair
  constructor: ->
    # Data
    self = @
    self.j1 = ko.observable()
    self.j2 = ko.observable()
    self.fullName = ko.computed () ->
      "#{self.j1()} / #{self.j2()}"
    # Behavior
    self.canStart = ko.computed () ->
      j1 = $.trim self.j1()
      j2 = $.trim self.j2()
      !!j1 and !!j2
    self.toJson = () ->
      j1: self.j1().trim(),
      j2: self.j2().trim()

# Team
class Team
  constructor: (@name, @players) ->
    # Data
    self = @
    self.name = @name
    self.players = @players
    self.remainingPlayers = ko.observableArray []
    self.registeredPlayers = ko.observableArray []
    self.player1 = ko.observable()
    self.player2 = ko.observable()
    self.player3 = ko.observable()
    self.player4 = ko.observable()
    self.d1 = ko.observable new Pair()
    self.d2 = ko.observable new Pair()
    self.psubs = ko.observable()
    self.psubsSelection = ko.observable false
    self.isSubst = ko.observable false
    self.psubsWho = ko.observable()
    self.psubsWhen = ko.observable()
    self.capitain = ko.observable()
    self.signed = ko.observable false
    self.canStart = ko.computed () ->
      p1 = $.trim self.player1()
      p2 = $.trim self.player2()
      p3 = $.trim self.player3()
      p4 = $.trim self.player4()
      cap = $.trim self.capitain()
      hasPlayers = !!p1 and !!p2 and !!p3 and !!p4
      hasPairs = !!self.d1().canStart() and !!self.d2().canStart()
      hasCapitain = !!cap
      hasPlayers and hasPairs and hasCapitain
    self.player1Subst = ko.computed () ->
      if (self.isSubst() and (self.player1() is self.psubsWho()))
      then self.psubs()
      else self.player1()
    self.player2Subst = ko.computed () ->
      if (self.isSubst() and (self.player2() is self.psubsWho()))
      then self.psubs()
      else self.player2()
    self.player3Subst = ko.computed () ->
      if (self.isSubst() and (self.player3() is self.psubsWho()))
      then self.psubs()
      else self.player3()
    self.player4Subst = ko.computed () ->
      if (self.isSubst() and (self.player4() is self.psubsWho()))
      then self.psubs()
      else self.player4()
    self.d1Subst = ko.computed () ->
      if (self.isSubst() and (self.d1().j1() is self.psubsWho()))
        "#{self.psubs()} / #{self.d1().j2()}"
      else if (self.isSubst() and (self.d1().j2() is self.psubsWho()))
        "#{self.d1().j1()} / #{self.psubs()}"
      else
        self.d1().fullName()
    self.d2Subst = ko.computed () ->
      if (self.isSubst() and (self.d2().j1() is self.psubsWho()))
        "#{self.psubs()} / #{self.d2().j2()}"
      else if (self.isSubst() and (self.d2().j2() is self.psubsWho()))
        "#{self.d2().j1()} / #{self.psubs()}"
      else
        self.d2().fullName()

    # Behavior
    self.register = (data, event) ->
      player = $(event.target).val()
      p = $.trim player
      self.remainingPlayers.remove p
      self.registeredPlayers.push p
    self.unregister = (data, event) ->
      player = $(event.target).val()
      p = $.trim player
      self.remainingPlayers.push p
      self.registeredPlayers.remove p
    self.registerPair = (data, event) ->
      player = $(event.target).val()
      p = $.trim player
      self.registeredPlayers.remove p
    self.unregisterPair = (data, event) ->
      player = $(event.target).val()
      p = $.trim player
      self.registeredPlayers.push p
    self.sign = () ->
      self.signed(!self.signed())
    self.toJson = () ->
      name: self.name,
      players: [
        self.player1().trim(),
        self.player2().trim(),
        self.player3().trim(),
        self.player4().trim()],
      d1: self.d1().toJson(),
      d2: self.d2().toJson(),
      substitute:
        j: self.psubs().trim(),
        out: self.psubsWho().trim(),
        match: self.psubsWhen().trim(),
      capitain: self.capitain().trim()
    # DEV
    self.devFillPlayers = () ->
      self.player1 self.players[0]
      self.player2 self.players[1]
      self.player3 self.players[2]
      self.player4 self.players[3]
      self.d1().j1 self.players[0]
      self.d1().j2 self.players[3]
      self.d2().j1 self.players[1]
      self.d2().j2 self.players[2]
    # Run
    for p in self.players
      self.remainingPlayers.push(p)

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
    self.leg1 = ko.observable 0
    self.leg2 = ko.observable 0
    self.leg3 = ko.observable 0
    self.team1Leg = ko.computed () ->
      result = 0;
      if (self.leg1() is 1) then result++
      if (self.leg2() is 1) then result++
      if (self.leg3() is 1) then result++
      result
    self.team2Leg = ko.computed () ->
      result = 0;
      if (self.leg1() is 2) then result++
      if (self.leg2() is 2) then result++
      if (self.leg3() is 2) then result++
      result
    self.team1Win = ko.computed () ->
      self.team1Leg() is 2
    self.team2Win = ko.computed () ->
      self.team2Leg() is 2
    self.finished = ko.computed () ->
      self.team1Win() or self.team2Win()
    self.visibleLeg2 = ko.computed () ->
      self.leg1() > 0
    self.visibleLeg3 = ko.computed () ->
      (!self.finished() and self.leg2() > 0) or (self.finished() and self.leg3() > 0)

    # Behavior
    self.updateTeam1 = (team) ->
      switch self.team1
        when "p1" then self.player1 team.player1Subst()
        when "p2" then self.player1 team.player2Subst()
        when "p3" then self.player1 team.player3Subst()
        when "p4" then self.player1 team.player4Subst()
        when "d1" then self.player1 team.d1Subst()
        when "d2" then self.player1 team.d2Subst()
        else
          self.team1
    self.updateTeam2 = (team) ->
      switch self.team2
        when "p1" then self.player2 team.player1Subst()
        when "p2" then self.player2 team.player2Subst()
        when "p3" then self.player2 team.player3Subst()
        when "p4" then self.player2 team.player4Subst()
        when "d1" then self.player2 team.d1Subst()
        when "d2" then self.player2 team.d2Subst()
        else
          self.team2
    self.nextLeg = (win) ->
      winner = parseInt win, 10
      if (self.leg2() > 0) # leg3
        self.leg3 winner
      else if (self.leg1() > 0) # leg 2
        self.leg2 winner
      else # leg 1
        self.leg1 winner
    self.toJson = () ->
      leg1: self.leg1(),
      leg2: self.leg2(),
      leg3: self.leg3()
    # DEV
    self.randomLeg = () ->
      Math.floor(Math.random()*2)+1
    self.devFillMatch = () ->
      self.leg1 self.randomLeg()
      self.leg2 self.randomLeg()
      if (self.leg1()!=self.leg2())
        self.leg3 self.randomLeg()

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
    self.comment = ko.observable ""
    self.matches = ko.observableArray @matches
    self.team1MatchesWin = ko.computed () ->
      count = 0
      for m in self.matches()
        if (m.team1Win()) then count++
      count
    self.team2MatchesWin = ko.computed () ->
      count = 0
      for m in self.matches()
        if (m.team2Win()) then count++
      count
    self.team1LegsWin = ko.computed () ->
      count = 0
      for m in self.matches()
        count += m.team1Leg()
      count
    self.team2LegsWin = ko.computed () ->
      count = 0
      for m in self.matches()
        count += m.team2Leg()
      count
    self.team1PointsWin = ko.computed () ->
      legs = self.team1MatchesWin()
      switch
        when legs < 10  then 1
        when legs is 10 then 2
        when legs > 10  then 3
        else
          0
    self.team2PointsWin = ko.computed () ->
      legs = self.team2MatchesWin()
      switch
        when legs < 10  then 1
        when legs is 10 then 2
        when legs > 10  then 3
        else
          0
    self.finished = ko.computed () ->
      result = true
      for m in self.matches()
        result = result && m.finished()
      result
    self.canStart = ko.computed () ->
      self.team1().canStart() and self.team2().canStart()
    self.canSend = ko.computed () ->
      self.finished() and self.team1().signed() and self.team2().signed()

    # Behavior
    self.start = () ->
      for m in self.matches()
        m.updateTeam1 self.team1()
        m.updateTeam2 self.team2()
      self.started(true)
      $("button[tabindex=111]").focus()
    self.lastFinishedMatch = () ->
      last = 0
      for i in [1..20]
        if (self.matches()[i - 1].finished()) then last = i
      last
    self.substitue = (team) ->
      match = self.lastFinishedMatch()
      team.psubsWhen match
      team.psubsWho team.psubsSelection()
      team.isSubst true
      for i in [match..19]
        m = self.matches()[i]
        if (team is self.team1())
        then m.updateTeam1 team
        else m.updateTeam2 team
    self.send = () ->
      json =
        comment: self.comment()
        day: self.day,
        result:
          date: self.date().trim(),
          location: self.location().trim(),
          team1: self.team1().toJson(),
          team2: self.team2().toJson(),
          matches: for m in self.matches()
            m.toJson()
      data = JSON.stringify(json)
      post =
        type: "POST",
        contentType: "application/json",
        url: "/sdo/ligues/SDO/team/result",
        data: data,
      post.success = (data, status, jqXHR) ->
        $("#diaSendOk").modal().on "shown", () ->
          $("#btnSend").focus()
      $.ajax post
    # DEV function
    self.devFillPlayers = () ->
      self.team1().devFillPlayers()
      self.team2().devFillPlayers()
    self.devFillMatches = () ->
      for m in self.matches()
        m.devFillMatch()
    # Run
    self.date(today)

# Run on Ready
$ ->
  matches = for m in allMatches
    new Match m.team1Start, m.team1, m.team2
  team1 = new Team team1Name, players1
  team1.capitain(team1Captitain)
  team2 = new Team team2Name, players2
  team2.capitain(team2Captitain)
  champDay = new ChampionshipDay day, team1, team2, matches
  champDay.location team1Location
  ko.applyBindings champDay


