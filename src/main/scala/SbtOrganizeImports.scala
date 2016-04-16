/*
package org.cvogt

import sbt._
import sbt.Keys._

object SbtOrganizeImports extends AutoPlugin {
  override def trigger = allRequirements // always auto-enable
  
  override lazy val projectSettings = Seq(
    includeFilter in organizeImports := "*.scala",
    compileInputs in (Compile, compile) <<= (compileInputs in (Compile, compile)) dependsOn (organizeImports in Compile),
    compileInputs in (Test, compile) <<= (compileInputs in (Test, compile)) dependsOn (organizeImports in Test)
  ) ++ inConfig(Compile)(configSettings) ++ inConfig(Test)(configSettings)
  
  val organizeImports: TaskKey[Unit] =
    TaskKey[Unit](
      "organizeImports",
      "Organize imports using scala-refactoring"
    )

  def configSettings: Seq[Setting[_]] =
    List(
      (sourceDirectories in organizeImports) := List(scalaSource.value),
      organizeImports := doOrganizeImports(
        (sourceDirectories in organizeImports).value.toList,
        (includeFilter in organizeImports).value,
        (excludeFilter in organizeImports).value,
        configuration.value,
        streams.value
      )
    )
}
*/
