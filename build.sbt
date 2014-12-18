name := "PandaBot"

version := "1.0"

scalaVersion := "2.11.4"

resolvers ++= Seq(
  "typesafe" at "http://repo.typesafe.com/typesafe/releases/",
  "sbt-plugin-releases" at "http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"
)

libraryDependencies ++= Seq(
  "io.reactivex" %% "rxscala" % "0.23.0",
  "org.parboiled" %% "parboiled-scala" % "1.1.6",
  "org.specs2" %% "specs2" % "2.4.2" % "test",
  "org.mockito" % "mockito-all" % "1.9.5" % "test"
)

scalacOptions in Test ++= Seq("-Yrangepos")
