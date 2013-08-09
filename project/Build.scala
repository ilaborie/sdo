import sbt._
import Keys._
import play.Project._


object ApplicationBuild extends Build {

  val appName = "sdo"
  val appVersion = "1.0-SNAPSHOT"

  val secureSocial = "securesocial" %% "securesocial" % "master-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
    secureSocial
  )

  // Only compile main.less
  def customLessEntryPoints(base: File): PathFinder = base / "app" / "assets" / "style" * "*.less"


  val defaultSettings =  Project.defaultSettings ++ org.scalastyle.sbt.ScalastylePlugin.Settings

  val main = play.Project(appName, appVersion, appDependencies, settings= defaultSettings).settings(
    // Add your own project settings here
    lessEntryPoints <<= baseDirectory(customLessEntryPoints),
    resolvers += Resolver.url("sbt-plugin-snapshots", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots/"))(Resolver.ivyStylePatterns)
  )


}
