import sbt._

object Dependencies {
  lazy val scalaScrapper = "net.ruippeixotog" %% "scala-scraper" % "2.0.0"

  lazy val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"
  lazy val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2"

  lazy val scalastic = "org.scalactic" %% "scalactic" % "3.0.1"
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1"

}