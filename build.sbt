name := "PandaBot"

version := "1.0"

scalaVersion := "2.9.2"

resolvers ++= Seq(
  "typesafe" at "http://repo.typesafe.com/typesafe/releases/",
  "sbt-plugin-releases" at "http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"
)

libraryDependencies += "com.typesafe.akka" % "akka-actor" % "2.0"
