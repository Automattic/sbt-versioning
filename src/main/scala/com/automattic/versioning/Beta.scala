package com.automattic.versioning

/**
  * Represents a beta/pre-release part
  *
  * @param version The whole version string
  */
case class Beta(version: Int) {

  /**
    * Render this value as a string
    *
    * @return The string
    */
  override def toString: String = s"beta.$version"

  // scalastyle:off method.name
  /**
    * Allow adding a number of increments to the version string
    *
    * @param number The number of increments
    * @return The incremented version
    */
  def +(number: Int): Beta = Beta(version + number)
  // scalastyle:on
}

/**
  * Beta companion object for parsing
  */
case object Beta {

  /**
    * Parse the pre-release information from a version string
    *
    * @param vString The string to parse
    * @return The Some(Beta) or None
    */
  def parse(vString: String): Option[Beta] = {
    vString.split('-').last match {
      case version: String
          if version.startsWith("beta") && version.contains(".") =>
        Some(Beta(version.split('.')(1).toInt))
      case version: String if version.startsWith("beta") =>
        Some(Beta(0))
      case _ =>
        None
    }
  }

  /**
    * Returns a zero version number
    *
    * @return The zero
    */
  def zero: Beta = Beta(0)
}
