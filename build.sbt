enablePlugins(org.nlogo.build.NetLogoExtension)

scalacOptions ++= Seq("-deprecation", "-unchecked", "-Xlint", "-Xfatal-warnings", "-encoding", "UTF8")

resolvers += Resolver.bintrayRepo("netlogo", "NetLogo-JVM")

scalaVersion        := "2.12.2"

name                := "DistExtension"

netLogoVersion      := "6.0.2"

netLogoClassManager := "nicolaspayette.dist.DistExtension"

netLogoExtName      := "dist"

netLogoZipSources   := false
