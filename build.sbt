// See LICENSE for license details.

enablePlugins(SiteScaladocPlugin)

enablePlugins(GhpagesPlugin)

git.remoteRepo := "git@github.com:ucb-bar/dsptools.git"

def scalacOptionsVersion(scalaVersion: String): Seq[String] = {
  Seq() ++ {
    // If we're building with Scala > 2.11, enable the compile option
    //  switch to support our anonymous Bundle definitions:
    //  https://github.com/scala/bug/issues/10047
    CrossVersion.partialVersion(scalaVersion) match {
      case Some((2, scalaMajor: Long)) if scalaMajor < 12 => Seq()
      case _ => Seq("-Xsource:2.11")
    }
  }
}

def javacOptionsVersion(scalaVersion: String): Seq[String] = {
  Seq() ++ {
    // Scala 2.12 requires Java 8. We continue to generate
    //  Java 7 compatible code for Scala 2.11
    //  for compatibility with old clients.
    CrossVersion.partialVersion(scalaVersion) match {
      case Some((2, scalaMajor: Long)) if scalaMajor < 12 =>
        Seq("-source", "1.7", "-target", "1.7")
      case _ =>
        Seq("-source", "1.8", "-target", "1.8")
    }
  }
}

resolvers ++= Seq (
  Resolver.sonatypeRepo("snapshots"),
  Resolver.sonatypeRepo("releases")
)

// Provide a managed dependency on X if -DXVersion="" is supplied on the command line.
val defaultVersions = Map(
  "chisel3" -> "3.2-SNAPSHOT",
  "chisel-iotesters" -> "1.3-SNAPSHOT",
  "rocketchip" -> "1.2",
  "vegas" -> "0.3.11",
  "handlebars-scala" -> "2.1.1"
)

name := "dsptools"

val commonSettings = Seq(
  organization := "edu.berkeley.cs",
  version := "1.2-SNAPSHOT",
  git.remoteRepo := "git@github.com:ucb-bar/dsptools.git",
  autoAPIMappings := true,
  scalaVersion := "2.12.6",
  crossScalaVersions := Seq("2.12.6", "2.11.12"),
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-language:reflectiveCalls") ++ scalacOptionsVersion(scalaVersion.value),
  javacOptions ++= javacOptionsVersion(scalaVersion.value),
)

val dsptoolsSettings = Seq(
  name := "dsptools",
  pomExtra := (<url>http://chisel.eecs.berkeley.edu/</url>
  <licenses>
    <license>
      <name>BSD-style</name>
      <url>http://www.opensource.org/licenses/bsd-license.php</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <developers>
    <developer>
      <id>grebe</id>
      <name>Paul Rigge</name>
      <url>http://www.eecs.berkeley.edu/~rigge/</url>
    </developer>
    <developer>
      <id>shunshou</id>
      <name>Angie Wang</name>
      <url>https://www.linkedin.com/in/angie-wang-ee/</url>
    </developer>
    <developer>
      <id>chick</id>
      <name>Charles Markley</name>
      <url>https://aspire.eecs.berkeley.edu/author/chick/</url>
    </developer>
   </developers>),
  publishTo := {
    val v = version.value
    val nexus = "https://oss.sonatype.org/"
    if (v.trim.endsWith("SNAPSHOT")) {
      Some("snapshots" at nexus + "content/repositories/snapshots")
    }
    else {
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
    }
  },
  libraryDependencies ++= Seq("chisel3","chisel-iotesters").map {
    dep: String => "edu.berkeley.cs" %% dep % sys.props.getOrElse(dep + "Version", defaultVersions(dep))
  },
  libraryDependencies ++= Seq(
    "org.typelevel" %% "spire" % "0.14.1",
    "org.scalanlp" %% "breeze" % "0.13.2",
    "org.scalatest" %% "scalatest" % "3.0.1",
    "org.scalacheck" %% "scalacheck" % "1.13.4"
  ),
)

val rocketSettings = Seq(
    name := "rocket-dsptools",
    libraryDependencies ++= Seq("chisel3", "chisel-iotesters", "rocketchip").map {
      dep: String => "edu.berkeley.cs" %% dep % sys.props.getOrElse(dep + "Version", defaultVersions(dep))
    },
    parallelExecution in Test := false,
)

publishMavenStyle := true

publishArtifact in Test := false
pomIncludeRepository := { x => false }

// Don't add 'scm' elements if we have a git.remoteRepo definition.


val dsptools = (project in file(".")).
  enablePlugins(BuildInfoPlugin).
  enablePlugins(ScalaUnidocPlugin).
  settings(commonSettings: _*).
  settings(dsptoolsSettings: _*)


val `rocket-dsptools` = (project in file("rocket")).
  settings(commonSettings: _*).
  settings(rocketSettings: _*).
  dependsOn(dsptools)
