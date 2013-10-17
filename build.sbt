import com.typesafe.sbt.SbtStartScript

seq(SbtStartScript.startScriptForClassesSettings: _*)

seq(ScctPlugin.instrumentSettings : _*)

seq(com.github.theon.coveralls.CoverallsPlugin.coverallsSettings: _*)

name := "PandaBot"

version := "1.0"

scalaVersion := "2.10.2"

resolvers ++= Seq(
  "typesafe" at "http://repo.typesafe.com/typesafe/releases/",
  "sbt-plugin-releases" at "http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.2.1"
)


