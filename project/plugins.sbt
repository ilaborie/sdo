// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository 
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("play" % "sbt-plugin" % "2.1.2")

// Magic auto refresh plugin
// addSbtPlugin("com.jamesward" %% "play-auto-refresh" % "0.0.4")

// Scalastyle
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.3.2")
