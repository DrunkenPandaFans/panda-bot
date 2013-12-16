import com.typesafe.sbt.SbtStartScript

seq(SbtStartScript.startScriptForClassesSettings: _*)

seq(ScctPlugin.instrumentSettings : _*)

seq(CoverallsPlugin.singleProject: _*)

org.scalastyle.sbt.ScalastylePlugin.Settings

name := "PandaBot"

version := "1.0"

scalaVersion := "2.10.2"

resolvers ++= Seq(
  "typesafe" at "http://repo.typesafe.com/typesafe/releases/",
  "sbt-plugin-releases" at "http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.2.1",
  "org.parboiled" %% "parboiled-scala" % "1.1.5",
  "org.specs2" %% "specs2" % "2.2.3" % "test",
  "org.mockito" % "mockito-all" % "1.9.5" % "test"
)

scalacOptions in Test ++= Seq("-Yrangepos")
