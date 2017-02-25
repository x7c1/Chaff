
import sbt._

object ChaffDependencies {
  lazy val forTests = Seq(
    "org.scalatest" %% "scalatest" % "2.2.4" % Test
  )
}
