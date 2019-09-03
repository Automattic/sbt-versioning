lazy val root = (project in file("."))
  .enablePlugins(VersionPlugin)
  .settings(
    beforeUpgrade := {
      println("Running before: " + version.value)
      import java.io.{File,FileInputStream,FileOutputStream}
      val src = new File("VERSION")
      val dest = new File("VERSION.BEFORE")
      new FileOutputStream(dest) getChannel() transferFrom(
        new FileInputStream(src).getChannel, 0, Long.MaxValue )
    },
    name := "root"
  )
  .aggregate(test1)

def done(before:String, after:String): Unit = println(before, after)

lazy val test1 = (project in file("test1"))
  .settings(
    name := "test1",
    isClient := true,
    replaceVersions := Map(
      file("test1/clientVersion.txt") -> ("VERSION", "VERSION")
    ),
    afterUpgrade := {
      println("Running after: " + nextVersion.value)
      import java.io.{File,FileInputStream,FileOutputStream}
      val src = new File("VERSION")
      val dest = new File("VERSION.AFTER")
      new FileOutputStream(dest) getChannel() transferFrom(
        new FileInputStream(src).getChannel, 0, Long.MaxValue )
    }

  )
