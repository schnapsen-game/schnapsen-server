val commonDependencies =  {
  Seq(
    "com.typesafe.akka" %% "akka-http-core" % "2.4.11",
    "com.typesafe.akka" %% "akka-http-experimental" % "2.4.11"
  )
}

lazy val root = (
  project.in(file("."))
    aggregate(tableCreate)
)

lazy val projectSettings = Seq(
  //for prod: optimise
  scalacOptions ++= Seq(
    "-encoding", "UTF-8",
    "-deprecation",
    "-unchecked",
    "-feature",
    "-explaintypes",
    "-uniqid",
    "-Xfuture",
    //"-Xfatal-warnings",
    //    "-Xprompt", //for debugging
     "-Xstrict-inference",
    "-Yno-adapted-args",
  //  "-Yno-predef", //for optimising?
  //  "-Yno-imports", //for optimising?
    "-Ywarn-dead-code",
    "-Ywarn-numeric-widen",
    "-Ywarn-value-discard",
    "-Ywarn-unused",
    "-Ywarn-unused-import",
    "-Ywarn-inaccessible",
    "-Ywarn-infer-any",
    "-language:existentials",
    "-language:higherKinds"),
  name := "snapszer-microservices-demo",
  version := "0.1",
  scalaVersion := "2.11.8",
  fork in Test := true
)

def baseProject(name: String): Project = (
  Project(name, file(name))
    settings (projectSettings: _*)
  )


lazy val tableCreate = (
  baseProject("table-create")
    settings(libraryDependencies ++= commonDependencies)
)

lazy val tableQuery = (
  baseProject("table-query")
    settings(libraryDependencies ++= commonDependencies)
  )

// lazy val table-join = (
//   baseProject("table-join")
//     settings(libraryDependencies ++= commonDependencies)
//   )
