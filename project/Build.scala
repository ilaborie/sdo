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
    jdbc, // FIXME remove
    secureSocial,
    reactiveMongo,
    pdf
  )

  // Only compile main.less
  def customLessEntryPoints(base: File): PathFinder =
    ((base / "app" / "assets" / "style" / "main.less") +++ (base / "app" / "assets" / "style" / "pdf-*.less"))

  val defaultSettings = Project.defaultSettings ++ org.scalastyle.sbt.ScalastylePlugin.Settings

  val main = play.Project(appName, appVersion, appDependencies, settings = defaultSettings).settings(
    // Add your own project settings here
    lessEntryPoints <<= baseDirectory(customLessEntryPoints),
    resolvers += Resolver.url("sbt-plugin-snapshots", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots/"))(Resolver.ivyStylePatterns),
    resolvers += Resolver.url("Violas Play Modules", url("http://www.joergviola.de/releases/"))(Resolver.ivyStylePatterns)
  )


}
