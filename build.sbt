scalaVersion := "2.9.3"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-Xlint", "-Xfatal-warnings", "-encoding", "UTF8")

libraryDependencies +=
  "org.nlogo" % "NetLogo" % "5.3.0" from
    "http://ccl-artifacts.s3-website-us-east-1.amazonaws.com/NetLogo-5.3.0.jar"

name := "dist"

enablePlugins(org.nlogo.build.NetLogoExtension)

netLogoExtName := "dist"

netLogoClassManager := "nicolaspayette.dist.DistExtension"

netLogoZipSources := false

netLogoTarget :=
    org.nlogo.build.NetLogoExtension.directoryTarget(baseDirectory.value)
