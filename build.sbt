lazy val V = _root_.scalafix.sbt.BuildInfo

inThisBuild(
  List(
    organization := "com.github.rcmartins",
    licenses := List(
      "Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")
    ),
    developers := List(
      Developer(
        "rcmartins",
        "Ricardo Carvalho Martins",
        "ricardocmartins91@gmail.com",
        url("https://github.com/rcmartins")
      )
    ),
    scalaVersion := V.scala213,
    addCompilerPlugin(
      "org.scalameta" % "semanticdb-scalac" % "4.7.8" cross CrossVersion.full
    ),
    githubOwner := "rcmartins",
    githubRepository := "lint-to-string",
  )
)

lazy val rules =
  project
    .settings(
      moduleName := "lint-to-string",
      libraryDependencies += "ch.epfl.scala" %% "scalafix-core" % V.scalafixVersion,
      Test / publishArtifact := false,
      pomIncludeRepository := { _ => false }
    )

lazy val input =
  project
    .settings(
      publish / skip := true
    )

lazy val output =
  project
    .settings(
      publish / skip := true
    )

lazy val tests =
  project
    .settings(
      publish / skip := true,
      libraryDependencies += "ch.epfl.scala" % "scalafix-testkit" % "0.11.0" % Test cross CrossVersion.full,
      scalafixTestkitOutputSourceDirectories :=
        (output / Compile / sourceDirectories).value,
      scalafixTestkitInputSourceDirectories :=
        (input / Compile / sourceDirectories).value,
      scalafixTestkitInputClasspath :=
        (input / Compile / fullClasspath).value
    )
    .dependsOn(rules, output)
    .enablePlugins(ScalafixTestkitPlugin)

Global / onChangedBuildSource := ReloadOnSourceChanges
