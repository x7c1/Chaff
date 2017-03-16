
import ChaffSettings.forPlugin
import ChaffDependencies.forTests

lazy val `chaff-reader` = project.
  settings(forPlugin).
  settings(
    version := "0.1.1",
    libraryDependencies ++= forTests
  )

lazy val `chaff-process` = project.
  settings(forPlugin).
  settings(
    version := "0.1.1",
    libraryDependencies ++= forTests
  ).
  dependsOn(`chaff-reader`)

lazy val `chaff-publish` = project.
  settings(forPlugin).
  settings(
    version := "0.1.0"
  )
