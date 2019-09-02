package com.automattic.versioning

import org.scalatest.{FlatSpec, Matchers}

class ClientVersionSpec extends FlatSpec with Matchers {
  val version = Version.parse("1.2.3")
  val beta = Version.parse("1.2.0-beta.4")

  "A client version" should "just be the first two numbers without update" in {
    ClientVersion(version, Config(version, IncrementMode.Identity, doUpdate = false)).toString shouldBe "1.2"
  }

  it should "maintain beta status when it's a major or minor status" in {
    val upgrades = Map(
      IncrementMode.Identity -> "1.2",
      IncrementMode.PatchBeta -> "1.2",
      IncrementMode.Patch -> "1.2",
      IncrementMode.MinorBeta -> "1.2-beta.5",
      IncrementMode.Minor -> "1.2",
      IncrementMode.MajorBeta -> "2.0-beta.0",
      IncrementMode.Major -> "1.0"
    )

    upgrades.foreach(up => ClientVersion(beta.increment(up._1), Config(beta, up._1, doUpdate = false)).toString shouldBe up._2)
  }
}
