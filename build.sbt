lazy val root = (project in file(".")).
  settings(
    name := "schnapsens",
    version := "0.1",
    scalaVersion := "2.11.8",
    scalacOptions ++= Seq("-deprecation", "-feature")
  )
