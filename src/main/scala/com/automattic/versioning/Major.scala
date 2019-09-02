package com.automattic.versioning

/**
  * Represents a major version
  *
  * @param version The version number
  */
case class Major(version: Int) {

  /**
    * @return The major version number
    */
  override def toString: String = version.toString

  // scalastyle:off method.name
  /**
    * Increment the major number by a specific amount
    *
    * @param number The number to increment by
    * @return The incremented version
    */
  def +(number: Int): Major = Major(version + number)
  // scalastyle:on
}

/**
  * The major version companion object
  */
case object Major {

  /**
    * Parse a version string for the major version
    *
    * @param vString The version string
    * @return The major version
    */
  def parse(vString: String): Major = {
    Major(vString.split('.')(0).toInt)
  }

  /**
    * @return The zero version
    */
  def zero: Major = Major(0)
}
