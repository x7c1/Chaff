package x7c1.chaff.publish

import sbt.Def.{SettingsDefinition, taskKey}
import sbt.Keys.{publishLocal, version}
import sbt.{Compile, TaskKey}

object PublishLocalSnapshot {

  def definition: SettingsDefinition = StateCommand.taskDefinition(
    key = publishLocalSnapshot,
    updates = Seq(
      version := withSuffix(version.value, "-SNAPSHOT")
    ),
    task = publishLocal in Compile
  )

  lazy val publishLocalSnapshot: TaskKey[Unit] = taskKey[Unit](
    "Runs publishLocal on '-SNAPSHOT' version."
  )

  private def withSuffix(original: String, suffix: String) = {
    if (original endsWith suffix) {
      original
    } else {
      original + suffix
    }
  }
}
