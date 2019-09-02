lazy val root = (project in file("."))
  .enablePlugins(VersionPlugin)
  .settings()
  .aggregate(test1)

lazy val test1 = (project in file("test1"))
  .settings(
    isClient := true,
    replaceVersions := Map(
      file("test1/clientVersion.txt") -> ("VERSION", "VERSION")
    )
  )
