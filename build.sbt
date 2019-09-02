val pluginVersion = "0.1-SNAPSHOT"

ThisBuild / version := pluginVersion
ThisBuild / organization := "com.automattic"
ThisBuild / description := "An opinionated versioning plugin"
ThisBuild / licenses += ("GPL-3.0", url(
  "https://www.gnu.org/licenses/gpl-3.0.en.html"
))

lazy val root = (project in file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    name := "sbt-versioning",
    sbtPlugin := true,
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.8" % Test
    ),
    scriptedLaunchOpts := { scriptedLaunchOpts.value ++
      Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
    },
    scriptedBufferLog := false,
    bintrayRepository := "generic",
    bintrayOrganization := Some("automattic")
  )
