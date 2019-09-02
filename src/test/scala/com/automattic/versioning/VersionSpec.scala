package com.automattic.versioning

import org.scalatest.{FlatSpec, Matchers}

class VersionSpec extends FlatSpec with Matchers {
  "A version" must "be parsed" in {
    val vString = "0.1.2"
    Version.parse(vString).toString shouldBe vString
  }

  it can "have a beta identifier" in {
    val vString = "1.2.3-beta.0"
    Version.parse(vString).toString shouldBe vString
  }

  it should "fail to parse an invalid version" in {
    val vString = "1.2"
    the[ArrayIndexOutOfBoundsException] thrownBy Version.parse(vString)
  }

  "A beta version" should "be 0 by default" in {
    val vString = "2.3.4-beta"
    Version.parse(vString).toString shouldBe vString + ".0"
  }

  "A major version" can "be incremented" in {
    Version.parse("1.2.3").increment(IncrementMode.Major).toString shouldBe "2.0.0"
  }

  it can "be incremented with a beta version" in {
    Version.parse("2.0.0-beta.4").increment(IncrementMode.Major).toString shouldBe "2.0.0"
  }

  it can "be incremented as a beta" in {
    Version.parse("1.2.3").increment(IncrementMode.MajorBeta).toString shouldBe "2.0.0-beta.0"
    Version.parse("2.0.0-beta.3").increment(IncrementMode.MajorBeta).toString shouldBe "2.0.0-beta.4"
  }

  "A minor version" can "be incremented" in {
    Version.parse("1.2.3").increment(IncrementMode.Minor).toString shouldBe "1.3.0"
  }

  it can "be incremented with a beta version" in {
    Version.parse("1.2.0-beta.4").increment(IncrementMode.Minor).toString shouldBe "1.2.0"
  }

  it can "be incremented as a beta" in {
    Version.parse("1.2.3").increment(IncrementMode.MinorBeta).toString shouldBe "1.3.0-beta.0"
    Version.parse("1.3.0-beta.4").increment(IncrementMode.MinorBeta).toString shouldBe "1.3.0-beta.5"
  }

  "A patch version" can "be incremented" in {
    Version.parse("1.2.3").increment(IncrementMode.Patch).toString shouldBe "1.2.4"
  }

  it can "be incremented with a beta version" in {
    Version.parse("1.2.3-beta.4").increment(IncrementMode.Patch).toString shouldBe "1.2.3"
  }

  it can "be incremented as a beta" in {
    Version.parse("1.2.3").increment(IncrementMode.PatchBeta).toString shouldBe "1.2.4-beta.0"
    Version.parse("1.2.4-beta.5").increment(IncrementMode.PatchBeta).toString shouldBe "1.2.4-beta.6"
  }
}
