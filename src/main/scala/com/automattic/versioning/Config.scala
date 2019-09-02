package com.automattic.versioning

/**
  * The configuration
  *
  * @param currentVersion The current version
  * @param increment The mode to increment
  * @param doUpdate Whether to update files
  */
case class Config(currentVersion: Version,
                  increment: IncrementMode,
                  doUpdate: Boolean)
