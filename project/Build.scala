
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
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName = "sdo"
  val appVersion = "1.0-SNAPSHOT"

  val secureSocial = "securesocial" %% "securesocial" % "master-SNAPSHOT"
  val reactiveMongo = "org.reactivemongo" %% "reactivemongo" % "0.9"
  val pdf = "pdf" % "pdf_2.10" % "0.5"

  val appDependencies = Seq(
    jdbc, // FIXME remove ?
    secureSocial,
    reactiveMongo,
    pdf
  )

  // Only compile main.less
  def customLessEntryPoints(base: File): PathFinder = (base / "app" / "assets" / "style" / "main.less") +++
    (base / "app" / "assets" / "style" / "pdf-landscape.less") +++
    (base / "app" / "assets" / "style" / "pdf-letter.less")

  val defaultSettings = Project.defaultSettings ++ org.scalastyle.sbt.ScalastylePlugin.Settings

  val main = play.Project(appName, appVersion, appDependencies, settings = defaultSettings).settings(
    // Add your own project settings here
    lessEntryPoints <<= baseDirectory(customLessEntryPoints),
    resolvers += Resolver.url("sbt-plugin-snapshots", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots/"))(Resolver.ivyStylePatterns),
    resolvers += Resolver.url("Violas Play Modules", url("http://www.joergviola.de/releases/"))(Resolver.ivyStylePatterns)
  )


}
