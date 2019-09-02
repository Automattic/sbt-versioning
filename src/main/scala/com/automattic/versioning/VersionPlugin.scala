package com.automattic.versioning

import java.io.PrintWriter

import sbt._
import complete.DefaultParsers._
import sbt.Keys.{sLog, version}

import scala.io.Source

/**
  * Handles upgrading and downgrading versions
  */
object VersionPlugin extends AutoPlugin {

  private val usage: String =
    """
      |Usage: sbt upgrade
      |--increment [major-beta|minor-beta|patch-beta|patch|minor|major]
      |--update
  """.stripMargin

  /**
    * Import tasks
    */
  object autoImport extends VersionKeys

  import autoImport._

  /**
    * Parses command line arguments into a configuration
    *
    * @param args The arguments
    * @param config The configuration
    * @return A new configuration or None
    */
  @scala.annotation.tailrec
  def parseArgs(args: List[String], config: Config): Option[Config] = {
    args match {
      case Nil => Some(config)
      case "--increment" :: part :: rest =>
        val mode = part match {
          case "patch"      => IncrementMode.Patch
          case "patch-beta" => IncrementMode.PatchBeta
          case "minor"      => IncrementMode.Minor
          case "minor-beta" => IncrementMode.MinorBeta
          case "major"      => IncrementMode.Major
          case "major-beta" => IncrementMode.MajorBeta
          case _            => IncrementMode.Identity
        }
        parseArgs(rest, config.copy(increment = mode))
      case "--update" :: rest =>
        parseArgs(rest, config.copy(doUpdate = true))
      case _ =>
        println(usage) // scalastyle:ignore
        None
    }
  }

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
    * @param clientVersion The client version to update with
    * @param config The configuration
    * @param nextVersion The next version
    */
  def doUpdate(clientVersion: ClientVersion,
               config: Config,
               nextVersion: Version,
               isClient: Boolean,
               beforeUpgrade: Option[(String, String) => Unit],
               replaceVersions: Map[File, (String, String)],
               afterUpgrade: Option[(String, String) => Unit],
               versionFile: File): Unit = {

    val version =
      if (isClient) clientVersion.toString else nextVersion.toString

    beforeUpgrade.foreach(
      v => v(config.currentVersion.toString, version.toString)
    )

    /**
      * A reusable version regex
      */
    val versionRegex = "\\d+\\.\\d+(\\.\\d+|)(\\-beta\\.\\d+|)"

    if (isClient && ClientVersion(config.currentVersion, config).toString == clientVersion.toString)
      return

    replaceVersions.foreach { regexFile =>
      val (file, (regex, replacement)) = regexFile
      replace(
        file.getAbsolutePath,
        regex.replaceAllLiterally("VERSION", versionRegex),
        replacement.replaceAllLiterally("VERSION", version),
        shouldChange = true
      )
    }

    replace("VERSION", ".*", s"$nextVersion", shouldChange = false)

    afterUpgrade.foreach(
      v => v(config.currentVersion.toString, version.toString)
    )
  }

  private def getCurrentVersion: String = {
    val source = Source.fromFile(versionFile.value)
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
      version := getCurrentVersion,
      isClient := false,
      beforeUpgrade := None,
      afterUpgrade := None,
      replaceVersions := Map(),
      upgradeVersion := {
        val args = spaceDelimited("").parsed
        val VERSION = version.value

        if (args.isEmpty) {
          sys.error(usage)
        }

        /**
          * Get the configuration
          */
        parseArgs(
          args.toList,
          Config(
            Version.parse(VERSION),
            IncrementMode.Identity,
            doUpdate = false
          )
        ) match {
          case None         => sLog.value.error("Unable to parse: " + VERSION)
          case Some(config) =>
            /**
              * Get the next version
              */
            val nextVersion = config.increment match {
              case IncrementMode.Identity => config.currentVersion
              case mode: IncrementMode =>
                config.currentVersion.increment(mode)
            }

            /**
              * Get the client version
              */
            val clientVersion = ClientVersion(nextVersion, config)

            sLog.value.info(s"version: $nextVersion")
            sLog.value.info(s"client version: $clientVersion")

            if (config.doUpdate) {
              doUpdate(
                clientVersion,
                config,
                nextVersion,
                isClient.value,
                beforeUpgrade.value,
                replaceVersions.value,
                afterUpgrade.value,
                versionFile.value
              )

              version := getCurrentVersion
            }
        }
      }
    )
}
