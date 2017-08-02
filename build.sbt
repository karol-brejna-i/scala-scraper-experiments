import Dependencies._


lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "org.fbc",
      scalaVersion := "2.12.3",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "Extractor",
    libraryDependencies ++= Seq(scalaScrapper, logback, scalaLogging),
    mainClass in assembly := Some("org.fbc.experiments.ss.Extractor")

  )





