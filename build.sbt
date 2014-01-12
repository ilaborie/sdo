
// The MIT License (MIT)
//
// Copyright (c) 2013 Igor Laborie
//
// Permission is hereby granted, free of charge, to any person obtaining a copy of
// this software and associated documentation files (the "Software"), to deal in
// the Software without restriction, including without limitation the rights to
// use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
// the Software, and to permit persons to whom the Software is furnished to do so,
// subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
// FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
// COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
// IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
// CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

import sbt._
import sbt.Keys._
import play.Project._

play.Project.playScalaSettings

org.scalastyle.sbt.ScalastylePlugin.Settings

name := "sdo"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.10.3"

//offline := "true".equalsIgnoreCase(sys.props("sbt.offline"))
offline := "false".equalsIgnoreCase(sys.props("sbt.offline"))

// "pdf" % "pdf_2.10" % "0.5"
libraryDependencies ++= Seq(
  "securesocial" %% "securesocial" % "2.1.2" withSources,
  "org.yaml" % "snakeyaml" % "1.13" withSources,
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.2" withSources,
  "pdf-scala" % "pdf-scala_2.10" % "0.6" exclude("org.scala-stm", "scala-stm_2.10.0") exclude("play", "*")
)

organization := "org.ilaborie"

// resolvers += Resolver.url("Violas Play Modules", url("http://www.joergviola.de/releases/"))(Resolver.ivyStylePatterns)
resolvers ++= Seq(
  "webjars" at "http://webjars.github.com/m2",
  Resolver.url("play-plugin-releases", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns),
  Resolver.url("play-plugin-snapshots", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots/"))(Resolver.ivyStylePatterns),
  Resolver.url("sbt-plugin-snapshots", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots/"))(Resolver.ivyStylePatterns),
  Resolver.url("Sonatype Snapshots", url("http://oss.sonatype.org/content/repositories/snapshots/"))(Resolver.ivyStylePatterns),
  Resolver.url("Objectify Play Repository", url("http://schaloner.github.com/releases/"))(Resolver.ivyStylePatterns),
  Resolver.url("Objectify Play Snapshot Repository", url("http://schaloner.github.com/snapshots/"))(Resolver.ivyStylePatterns),
  "Mandubian repository snapshots" at "https://github.com/mandubian/mandubian-mvn/raw/master/snapshots/",
  "Mandubian repository releases" at "https://github.com/mandubian/mandubian-mvn/raw/master/releases/",
  "Sonatype Snapshots 2" at "http://oss.sonatype.org/content/repositories/snapshots/",
  Resolver.url("Violas Play Modules", url("http://www.joergviola.de/releases/"))(Resolver.ivyStylePatterns)
)

scalacOptions ++= Seq("-deprecation", "-encoding", "UTF-8", "-feature", "-target:jvm-1.6", "-unchecked",
  "-Ywarn-adapted-args", "-Ywarn-value-discard", "-Xlint", "-language:reflectiveCalls")

// Custom Less entry points
lessEntryPoints <<= baseDirectory(_ / "app" / "assets" / "style" ** "main*.less")
