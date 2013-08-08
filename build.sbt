import com.typesafe.sbt.SbtStartScript

name := "PandaBot"

version := "1.0"

scalaVersion := "2.10.2"

sbtVersion := "0.12.4"

resolvers ++= Seq(
  "typesafe" at "http://repo.typesafe.com/typesafe/releases/",
  "sbt-plugin-releases" at "http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"
)

libraryDependencies += "com.typesafe.akka" % "akka-actor" % "2.0"

seq(SbtStartScript.startScriptForClassesSettings: _*)
