package com.automattic.versioning

/**
  * Represents the client version
  *
  * @param version The api version
  * @param config The configuration
  */
case class ClientVersion(version: Version, config: Config) {

  /**
    * @return The string representation of the client version
    */
  override def toString: String = {
    version.beta match {
      case Some(_)
          if config.increment == IncrementMode.MajorBeta || config.increment == IncrementMode.MinorBeta =>
        Seq(Some(Seq(version.major, version.minor).mkString(".")), version.beta).flatten
          .mkString("-")
      case Some(_) | None =>
        Seq(version.major, version.minor).mkString(".")
    }
  }
}
