package com.automattic.versioning

/**
  * Represents a version
  *
  * @param major The major version number
  * @param minor The minor version number
  * @param patch The patch version number
  * @param beta The beta version number
  */
case class Version(major: Major,
                   minor: Minor,
                   patch: Patch,
                   beta: Option[Beta]) {

  /**
    * @return The version in string form
    */
  override def toString: String =
    Seq(Some(Seq(major, minor, patch).mkString(".")), beta).flatten
      .mkString("-")

  // scalastyle:off cyclomatic.complexity
  /**
    * Increments a specific version number
    *
    * @param mode The mode to increment by
    * @return The incremented version
    */
  def increment(mode: IncrementMode): Version = {
    mode match {
      case IncrementMode.Major =>
        beta match {
          case None =>
            Version(major + 1, Minor.zero, Patch.zero, None)
          case Some(_) =>
            Version(major, Minor.zero, Patch.zero, None)
        }
      case IncrementMode.Minor =>
        beta match {
          case None =>
            Version(major, minor + 1, Patch.zero, None)
          case Some(_) =>
            Version(major, minor, Patch.zero, None)
        }
      case IncrementMode.Patch =>
        beta match {
          case None =>
            Version(major, minor, patch + 1, None)
          case Some(_) =>
            Version(major, minor, patch, None)
        }
      case IncrementMode.PatchBeta =>
        beta match {
          case None =>
            Version(major, minor, patch + 1, Some(Beta.zero))
          case Some(betaVersion) =>
            Version(major, minor, patch, Some(betaVersion + 1))
        }
      case IncrementMode.MinorBeta =>
        beta match {
          case None =>
            Version(major, minor + 1, Patch.zero, Some(Beta.zero))
          case Some(betaVersion) if patch == Patch.zero =>
            Version(major, minor, Patch.zero, Some(betaVersion + 1))
          case Some(_) =>
            Version(major, minor + 1, Patch.zero, Some(Beta.zero))
        }
      case IncrementMode.MajorBeta =>
        beta match {
          case None =>
            Version(major + 1, Minor.zero, Patch.zero, Some(Beta.zero))
          case Some(betaVersion)
              if minor == Minor.zero && patch == Patch.zero =>
            Version(major, Minor.zero, Patch.zero, Some(betaVersion + 1))
          case Some(_) =>
            Version(major + 1, Minor.zero, Patch.zero, Some(Beta.zero))
        }
      case IncrementMode.Identity =>
        copy()
    }
  }
}

/**
  * The companion object for parsing
  */
case object Version {

  /**
    * Parses a version string into a version object
    *
    * @param version The version string
    * @return The parsed version
    */
  def parse(version: String): Version =
    Version(
      Major.parse(version),
      Minor.parse(version),
      Patch.parse(version),
      Beta.parse(version)
    )
}
