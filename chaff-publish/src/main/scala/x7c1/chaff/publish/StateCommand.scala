package x7c1.chaff.publish

import sbt.Command.command
import sbt.Def.{SettingList, SettingsDefinition}
import sbt.Keys.{commands, projectID, state}
import sbt.{Command, Def, Project, State, TaskKey}

object StateCommand {

  def apply[A](
    name: String,
    updates: Seq[Def.Setting[_]],
    task: TaskKey[A],
    nextState: States => State): Command = {

    command(name) { originalState =>
      val extracted = Project extract originalState
      val newState = extracted.append(updates, originalState)
      val (updatedState, _) = Project.extract(newState).runTask(task, newState)
      nextState(States(originalState, updatedState))
    }
  }

  def taskDefinition[A](
    key: TaskKey[Unit],
    updates: Seq[Def.Setting[_]],
    task: TaskKey[A]): SettingsDefinition = {

    val commandName = s"${key.key.label}Command"
    val settings = Seq(
      key := {
        val id = projectID.value.name
        CommandRunner.runCommand(s";project $id;$commandName")(state.value)
      },
      commands ++= Seq(StateCommand(
        name = commandName,
        updates = updates,
        task = task,
        nextState = _.original
      ))
    )
    new SettingList(settings)
  }

}

case class States(
  original: State,
  updated: State
)
