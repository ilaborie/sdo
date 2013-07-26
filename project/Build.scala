import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName = "sdo"
  val appVersion = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm
  )

  // Only compile main.less
  def customLessEntryPoints(base: File): PathFinder = ((base / "app" / "assets" / "style" * "*.less"))



  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
    lessEntryPoints <<= baseDirectory(customLessEntryPoints)
  )

}
