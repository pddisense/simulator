name := "simulator"
version := "0.1.0"
organization := "ucl"

scalaVersion := "2.12.6"

resolvers += Resolver.mavenLocal
resolvers += Resolver.bintrayRepo("pvcnt", "lumos")

libraryDependencies += "org.apache.commons" % "commons-math3" % "3.6.1"
libraryDependencies += "com.univocity" % "univocity-parsers" % "2.7.5"
libraryDependencies += "io.lumos" % "lumos-sdk" % "0.1.7"

assemblyJarName in assembly := "simulator.jar"

mainClass in(Compile, run) := Some("ucl.simulator.SimulatorMain")
mainClass in assembly := Some("ucl.simulator.SimulatorMain")

run in Compile <<= Defaults.runTask(fullClasspath in Compile, mainClass in(Compile, run), runner in(Compile, run))
