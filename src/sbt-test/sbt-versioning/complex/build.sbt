lazy val root = (project in file("."))
  .enablePlugins(VersionPlugin)
  .settings()
  .aggregate(test1)

def done(before:String, after:String): Unit = println(before, after)

lazy val test1 = (project in file("test1"))
  .settings(
    isClient := true,
    replaceVersions := Map(
      file("test1/clientVersion.txt") -> ("VERSION", "VERSION")
    )
  )
