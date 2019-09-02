package com.automattic.versioning

sealed trait IncrementMode

object IncrementMode {
  case object Major extends IncrementMode
  case object Minor extends IncrementMode
  case object Patch extends IncrementMode
  case object PatchBeta extends IncrementMode
  case object MinorBeta extends IncrementMode
  case object MajorBeta extends IncrementMode
  case object Identity extends IncrementMode
}
