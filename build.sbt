import scalariform.formatter.preferences._
import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import scoverage.ScoverageKeys._

lazy val commonSettings = Seq(
  organization := "com.github.iref",
  version := "1.0",
  scalaVersion := "2.11.8",
  crossScalaVersions := Seq("2.10.4", "2.11.8"),
  scalacOptions ++= Seq(
    "-encoding", "UTF-8",
    "-deprecation",
    "-feature",
    "-unchecked",
    "-Xlint",
    "-Yno-adapted-args",
    "-Ywarn-dead-code",
    "-Ywarn-value-discard",
    "-Yrangepos"
  ),
  resolvers ++= repositories,
  libraryDependencies ++= dependencies
)

lazy val repositories = Seq(
  "typesafe" at "http://repo.typesafe.com/typesafe/releases/"
)

lazy val dependencies = Seq(
  "io.reactivex"  %% "rxscala"         % "0.25.0",
  "org.parboiled" %% "parboiled-scala" % "1.1.6",
  "org.scalatest" %% "scalatest"       % "2.2.6" % "test",
  "org.mockito"   %  "mockito-all"     % "1.9.5" % "test"
)

lazy val scoverageSettings = Seq(
  coverageMinimum := 60,
  coverageFailOnMinimum := true,
  coverageHighlighting := scalaBinaryVersion.value != "2.10"
)

lazy val scalariformSettings = SbtScalariform.scalariformSettings ++ Seq(
  ScalariformKeys.preferences := ScalariformKeys.preferences.value
    .setPreference(DoubleIndentClassDeclaration, true)
)

lazy val pandaBotSettings = commonSettings ++ scalariformSettings

lazy val pandaBot = project.in(file("."))
  .settings(moduleName := "PandaBot")
  .settings(pandaBotSettings: _*)
  .settings(scoverageSettings: _*)
  .aggregate(core, examples)
  .dependsOn(core, examples)

lazy val core = project.in(file("core"))
  .settings(moduleName := "core")
  .settings(pandaBotSettings: _*)
  .settings(scoverageSettings: _*)

lazy val examples = project.in(file("examples"))
  .settings(moduleName := "examples")
  .settings(pandaBotSettings: _*)
  .dependsOn(core)
  .enablePlugins(JavaAppPackaging)
