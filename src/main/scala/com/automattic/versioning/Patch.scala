package com.automattic.versioning

/**
  * Represents the patch number in a version string
  *
  * @param version The patch number
  */
case class Patch(version: Int) {

  /**
    * @return The patch number string
    */
  override def toString: String = version.toString

  // scalastyle:off method.name
  /**
    * Increment the patch number
    *
    * @param number The patch number
    * @return The incremented patch
    */
  def +(number: Int): Patch = Patch(version + number)
  // scalastyle:on
}

/**
  * Patch companion object for parsing
  */
case object Patch {

  /**
    * Parses the patch number from a version string
    *
    * @param vString The version string
    * @return The Patch
    */
  def parse(vString: String): Patch = {
    Patch(vString.split('.')(2).split('-')(0).toInt)
  }

  /**
    * @return A zero patch number
    */
  def zero: Patch = Patch(0)
}
