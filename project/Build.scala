import sbt._
import sbt.Keys._
import sbt.{ThisBuild, Project}

object OrganizeImportsBuild extends Build {
  val repoKind = SettingKey[String]("repo-kind", "Maven repository kind (\"snapshots\" or \"releases\")")
  val projectName = "sbt-organize-imports"
  val githubUser = "xdotai"
  val sbtScalariform: Project = Project(
    projectName,
    file("."),
    settings = Defaults.defaultSettings ++ Seq(
      //sbtPlugin := true,
      scalaVersion := "2.11.8",
      name := projectName,
      description := "sbt plugin to organize imports on compile",
      organization := "ai.x",
      name := projectName,
      version in ThisBuild := "0.9",
      libraryDependencies ++= Seq(
        "org.scala-refactoring" % ("org.scala-refactoring.library_" ++ scalaVersion.value) % "0.10.0-SNAPSHOT",
        "org.scala-lang" % "scala-compiler" % scalaVersion.value
      ),
      scalacOptions ++= List(
        "-unchecked",
        "-deprecation",
        "-feature"
      ),
      resolvers ++= Seq(Resolver.sonatypeRepo("releases"),Resolver.sonatypeRepo("snapshots")),
      organizationName := "x.ai",
      organization := "ai.x",
      repoKind <<= (version)(v => if(v.trim.endsWith("SNAPSHOT")) "snapshots" else "releases"),
      //publishTo <<= (repoKind)(r => Some(Resolver.file("test", file("c:/temp/repo/"+r)))),
      publishTo <<= (repoKind){
        case "snapshots" => Some("snapshots" at "https://oss.sonatype.org/content/repositories/snapshots")
        case "releases" =>  Some("releases"  at "https://oss.sonatype.org/service/local/staging/deploy/maven2")
      },
      publishMavenStyle := true,
      publishArtifact in Test := false,
      pomIncludeRepository := { _ => false },
      makePomConfiguration ~= { _.copy(configurations = Some(Seq(Compile, Runtime, Optional))) },
      licenses += ("Two-clause BSD-style license", url("http://github.com/"+githubUser+"/"+projectName+"/blob/master/LICENSE.txt")),
      homepage := Some(url("http://github.com/"+githubUser+"/"+projectName)),
      startYear := Some(2016),
      pomExtra :=
        <developers>
          <developer>
            <id>cvogt</id>
            <name>Jan Christopher Vogt</name>
            <timezone>-5</timezone>
            <url>https://github.com/cvogt/</url>
          </developer>
        </developers>
          <scm>
            <url>git@github.com:{githubUser}/{projectName}.git</url>
            <connection>scm:git:git@github.com:{githubUser}/{projectName}.git</connection>
          </scm>
    )
  )
}
