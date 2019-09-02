package com.automattic.versioning

/**
  * Represents a minor number
  *
  * @param version The version number
  */
case class Minor(version: Int) {

  /**
    * @return The minor version number
    */
  override def toString: String = version.toString

  // scalastyle:off method.name
  /**
    * Add numbers to the current minor version
    *
    * @param number The number to increment by
    * @return The incremented Minor
    */
  def +(number: Int): Minor = Minor(version + number)
  // scalastyle:on
}

/**
  * Minor version number companion object
  */
case object Minor {

  /**
    * Parses the minor version from a string
    *
    * @param vString The version string
    * @return The parsed minor version
    */
  def parse(vString: String): Minor = {
    Minor(vString.split('.')(1).toInt)
  }

  /**
    * @return A zero minor version
    */
  def zero: Minor = Minor(0)
}
