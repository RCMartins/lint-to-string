lazy val V = _root_.scalafix.sbt.BuildInfo

inThisBuild(
  List(
    organization := "com.github.rcmartins",
    homepage := Some(url("https://github.com/rcmartins/lint-to-string")),
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
    version := "0.2.0",
    scalaVersion := V.scala213,
    addCompilerPlugin(
      "org.scalameta" % "semanticdb-scalac" % "4.7.8" cross CrossVersion.full
    ),
    publishTo := Some(
      "GitHub Package Registry" at "https://maven.pkg.github.com/rcmartins/lint-to-string"
    ),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/rcmartins/lint-to-string"),
        "scm:git:https://github.com/rcmartins/lint-to-string.git"
      )
    ),
    credentials +=
      Credentials(
        "GitHub Package Registry",
        "maven.pkg.github.com",
        sys.env.getOrElse("GITHUB_USERNAME", ""),
        sys.env.getOrElse("GITHUB_TOKEN", ""),
      )
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
