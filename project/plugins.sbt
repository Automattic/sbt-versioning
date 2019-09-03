resolvers += Resolver.bintrayIvyRepo("automattic", "generic")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value

// make itself it's own plugin
unmanagedSourceDirectories in Compile += baseDirectory.value.getParentFile / "src" / "main" / "scala"