enablePlugins(org.nlogo.build.NetLogoExtension)

scalacOptions ++= Seq("-deprecation", "-unchecked", "-Xlint", "-Xfatal-warnings", "-encoding", "UTF8")

resolvers += Resolver.bintrayRepo("netlogo", "NetLogo-JVM")

libraryDependencies ++= Seq(
  "org.nlogo" % "netlogo" % "6.0.0-BETA1" intransitive
)

scalaVersion        := "2.11.8"

name                := "DistExtension"

netLogoVersion      := "6.0.0-BETA1"

netLogoClassManager := "nicolaspayette.dist.DistExtension"

netLogoExtName      := "dist"

netLogoZipSources   := false

netLogoTarget       := org.nlogo.build.NetLogoExtension.directoryTarget(baseDirectory.value)
