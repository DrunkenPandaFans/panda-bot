import scoverage.ScoverageKeys._


lazy val buildSettings = Seq(
  organization := "me.janferko",
  version := "0.0.1",
  scalaVersion := "2.11.8",
  crossScalaVersions := Seq("2.11.8", "2.12.1")
)

lazy val compilerOptions = Seq(
  "-encoding", "UTF-8",
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-value-discard",
  "-Yrangepos",
  "-Ywarn-numeric-widen"
)


lazy val repositories = Seq(
  "typesafe" at "http://repo.typesafe.com/typesafe/releases/",
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

lazy val dependencies = Seq(
  "org.typelevel"  %% "cats-core"       % "0.9.0",
  "io.reactivex"   %% "rxscala"         % "0.26.5",
  "org.parboiled"  %% "parboiled-scala" % "1.1.6",
  "org.scalatest"  %% "scalatest"       % "3.0.1"  % "test",
  "org.scalacheck" %% "scalacheck"      % "1.13.4" % "test",
  "org.mockito"     % "mockito-all"     % "1.9.5"  % "test"
)

lazy val commonSettings = Seq(
  resolvers ++= repositories,
  libraryDependencies ++= dependencies,
  scapegoatVersion := "1.3.0",
  scalacOptions ++= compilerOptions,
  scalacOptions in (Compile, console) ~= (_ filterNot (_ == "-Ywarn-unused-imports")),
  scalacOptions in (Compile, console) += "-Yrepl-class-based"
)

lazy val scoverageSettings = Seq(
  coverageMinimum := 60,
  coverageFailOnMinimum := true,
  coverageHighlighting := scalaBinaryVersion.value != "2.11"
)

lazy val allSettings = commonSettings ++ buildSettings ++ scoverageSettings

lazy val pandaBot = project.in(file("."))
  .settings(moduleName := "PandaBot")
  .settings(allSettings)
  .aggregate(core, examples)
  .dependsOn(core, examples)

lazy val core = project.in(file("core"))
  .settings(moduleName := "core")
  .settings(allSettings: _*)

lazy val examples = project.in(file("examples"))
  .settings(moduleName := "examples")
  .settings(allSettings: _*)
  .dependsOn(core)
  .enablePlugins(JavaAppPackaging)
