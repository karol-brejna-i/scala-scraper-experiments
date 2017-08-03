import Dependencies._


lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "org.fbc.experiments.baj",
      scalaVersion := "2.12.3",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "baj-experiments",
    libraryDependencies ++= Seq(scalaScrapper, logback, scalaLogging),
    mainClass in assembly := Some("org.fbc.experiments.ss.BajExperiments"),
    assemblyJarName in assembly := "application.jar"
  )





