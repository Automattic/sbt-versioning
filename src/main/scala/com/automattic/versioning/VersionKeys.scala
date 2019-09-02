package com.automattic.versioning

import sbt._

trait VersionKeys {
  // upgrade task
  val upgradeVersion = inputKey[Unit]("Upgrades Feature Store Version")
  val versionFile = settingKey[File]("Location of a VERSION file")
  val isClient = settingKey[Boolean]("Whether this version is a client version")
  val beforeUpgrade = settingKey[Option[(String, String) => Unit]](
    "Execute a command before upgrading the version"
  )
  val afterUpgrade = settingKey[Option[(String, String) => Unit]](
    "Execute a command after upgrading the version"
  )
  val replaceVersions =
    settingKey[Map[File, (String, String)]]("Update files with version numbers")
}
