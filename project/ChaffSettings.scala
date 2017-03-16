import bintray.BintrayKeys.bintrayRepository
import sbt.Def.SettingList
import sbt.Keys._
import sbt._


object ChaffSettings {

  lazy val common = new SettingList(Seq(
    organization := "x7c1",
    scalacOptions ++= Seq(
      "-deprecation",
      "-feature",
      "-unchecked",
      "-Xlint"
    )
  ))

  lazy val forPlugin = new SettingList(common ++
    forKindProjector ++
    Seq(
      sbtPlugin := true,
      bintrayRepository := "sbt-plugins"
    )
  )

  lazy val forKindProjector = Seq(
    resolvers += Resolver.sonatypeRepo("releases"),
    addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.3")
  )

}
