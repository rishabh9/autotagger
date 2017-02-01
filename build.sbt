import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._

name := """autotagger"""

version := (version in ThisBuild).value

lazy val root = (project in file(".")).enablePlugins(PlayJava)

organization := "me.rishabh"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "be.objectify" %% "deadbolt-java" % "2.5.4",
  "org.hibernate" % "hibernate-validator-cdi" % "5.2.4.Final",
  "org.mockito" % "mockito-core" % "1.10.19" % "test",
  "org.powermock" % "powermock-module-junit4" % "1.6.6" % "test",
  "org.powermock" % "powermock-api-mockito" % "1.6.6" % "test"
)

PlayKeys.externalizeResources := false

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

initialize := {
  val _ = initialize.value
  if (sys.props("java.specification.version") != "1.8")
    sys.error("Java 8 is required for this project.")
}

libraryDependencies += filters

sources in(Compile, doc) := Seq.empty

publishArtifact in(Compile, packageDoc) := false

packageDescription := "The Xola App Store"

maintainer := "Rishabh Joshi <rishabh9@gmail.com>"

dockerExposedPorts in Docker := Seq(9000, 9443)

dockerRepository := Some("rishabh9")

javaOptions in Test += "-Dconfig.file=conf/application-test.conf"

javaOptions in Test += "-Dlogger.file=conf/logback-test.xml"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-encoding", "UTF-8")

testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-v"))

// Uncomment below if play doesn't auto reload in vagrant
// PlayKeys.playWatchService := play.sbtplugin.run.PlayWatchService.sbt(pollInterval.value)

// The Release configuration
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies, // : ReleaseStep
  inquireVersions, // : ReleaseStep
  runTest, // : ReleaseStep
  setReleaseVersion, // : ReleaseStep
  commitReleaseVersion, // : ReleaseStep, performs the initial git checks
  tagRelease, // : ReleaseStep
  //publishArtifacts,                       // : ReleaseStep, checks whether `publishTo` is properly set up
  setNextVersion, // : ReleaseStep
  commitNextVersion, // : ReleaseStep
  pushChanges // : ReleaseStep, also checks that an upstream branch is properly configured
)
