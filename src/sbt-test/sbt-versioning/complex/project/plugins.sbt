sys.props.get("plugin.version") match {
  case Some(x) =>
    addSbtPlugin("com.automattic" % "sbt-versioning" % x)
  case _ => sys.error("Failed to specify a version")
}
