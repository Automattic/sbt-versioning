resolvers += Resolver.bintrayIvyRepo("automattic", "generic")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value
addSbtPlugin("com.automattic" % "sbt-versioning" % "0.1.4")