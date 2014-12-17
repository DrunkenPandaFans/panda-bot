import com.typesafe.sbt.SbtStartScript

seq(SbtStartScript.startScriptForClassesSettings: _*)

name := "PandaBot"

version := "1.0"

scalaVersion := "2.11.4"

resolvers ++= Seq(
  "typesafe" at "http://repo.typesafe.com/typesafe/releases/",
  "sbt-plugin-releases" at "http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.8",
  "org.parboiled" %% "parboiled-scala" % "1.1.6",
  "org.specs2" %% "specs2" % "2.4.15" % "test",
  "org.mockito" % "mockito-all" % "1.9.5" % "test",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.8" % "test"
)

scalacOptions in Test ++= Seq("-Yrangepos")
