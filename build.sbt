
import ChaffSettings.forPlugin
import ChaffDependencies.forTests

lazy val `chaff-reader` = project.
  settings(forPlugin).
  settings(
    version := "0.1.0",
    libraryDependencies ++= forTests
  )
