package com.automattic.versioning

import java.io.PrintWriter

import sbt._
import sbt.Keys._
import sbt.Package.ManifestAttributes

import scala.io.Source

/**
  * Handles upgrading and downgrading versions
  */
object VersionPlugin extends AutoPlugin {

  /**
    * Import tasks
    */
  object autoImport extends VersionKeys

  import autoImport._

  /**
    * Replace a regex in a given file with the current version
    *
    * @param file The file to replace
    * @param regex The regex to replace with
    * @param newVersion The new version string
    * @param shouldChange Whether the file should change or not
    */
  def replace(file: String,
              regex: String,
              newVersion: String,
              shouldChange: Boolean = true): Unit = {
    val source = Source.fromFile(file, "utf-8")
    val contents = source.getLines().mkString("\n")
    source.close()

    val newContents = contents.replaceFirst(regex, newVersion.toString)

    if (newContents == contents && shouldChange) {
      println(s"No changes were made to $file! with regex: $regex") // scalastyle:ignore
      System.exit(1)
    }

    val writer = new PrintWriter(file)
    writer.print(newContents)
    writer.close()
  }

  /**
    *
    * @param version The client version to update with
    * @param replaceVersions The configuration
    * @param versionFile The next version
    */
  def doUpdate(version: String,
               fullVersion: String,
               replaceVersions: Map[File, (String, String)],
               versionFile: File): Unit = {

    /**
      * A reusable version regex
      */
    val versionRegex = "\\d+\\.\\d+(\\.\\d+|)(\\-beta\\.\\d+|)"

    replaceVersions.foreach { regexFile =>
      val (file, (regex, replacement)) = regexFile
      replace(
        file.getAbsolutePath,
        regex.replaceAllLiterally("VERSION", versionRegex),
        replacement.replaceAllLiterally("VERSION", version)
      )
    }

    replace(
      versionFile.getAbsolutePath,
      ".*",
      s"$fullVersion",
      shouldChange = false
    )
  }

  private def getCurrentVersion(file: File): String = {
    val source = Source.fromFile(file)
    val VERSION = source.getLines().mkString("")
    source.close()

    VERSION
  }

  /**
    * @return Requirements
    */
  override def trigger: PluginTrigger = allRequirements

  override val projectSettings: Seq[Setting[_]] =
    Seq(
      versionFile := file("VERSION"),
      version := getCurrentVersion(versionFile.value),
      nextVersion := version.value,
      isClient := false,
      replaceVersions := Map(),
      commands += Command("upgradeVersion")(_ => upgradeParser.increment) {
        (state, incrementMode) =>
          val config =
            Config(Version.parse(version.value), incrementMode, doUpdate = true)
          val nextVersionString =
            config.currentVersion.increment(config.increment)
          val clientVersion = ClientVersion(nextVersionString, config)

          sLog.value.info(s"previous version: ${version.value}")
          sLog.value.info(s"version: $nextVersionString")
          sLog.value.info(s"client version: $clientVersion")

          var nextState = state
          val extracted = Project.extract(nextState)
          import extracted._

          structure.allProjectRefs.foreach {
            ref =>
              val projectIsClient = isClient in ref get structure.data getOrElse (isClient.value)
              val nextProjectVersion =
                if (projectIsClient) clientVersion else nextVersionString

              val versionWillNotChange = projectIsClient && clientVersion.toString == ClientVersion(
                config.currentVersion,
                config
              ).toString

              if (versionWillNotChange) {
                sLog.value.info(
                  name in ref get structure.data getOrElse ("Project") + " will not change, skipping."
                )
              } else {
                nextState = appendWithSession(
                  Seq(
                    nextVersion in ref := nextVersionString.toString,
                    nextVersion := nextVersionString.toString
                  ),
                  nextState
                )
                Project.runTask(beforeUpgrade in ref, nextState)

                doUpdate(
                  nextProjectVersion.toString,
                  nextVersionString.toString,
                  replaceVersions in ref get structure.data getOrElse (replaceVersions.value),
                  versionFile in ref get structure.data getOrElse (versionFile.value)
                )

                Project.runTask(afterUpgrade in ref, nextState)
              }
          }

          structure.allProjectRefs.foreach({ ref =>
            nextState = appendWithSession(
              Seq(
                version in ref := nextVersionString.toString,
                version := nextVersionString.toString
              ),
              nextState
            )
          })

          nextState
      }
    )
}
