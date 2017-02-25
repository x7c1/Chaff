import bintray.BintrayKeys.bintrayRepository

import sbt.Def.SettingList
import sbt.Keys._


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
    Seq(
      sbtPlugin := true,
      bintrayRepository := "sbt-plugins"
    )
  )
}
