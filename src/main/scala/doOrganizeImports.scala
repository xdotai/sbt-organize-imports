package org.cvogt

/*
import sbt._
import sbt.Keys._
*/

import scala.tools.refactoring._
import scala.tools.refactoring.util._
import scala.tools.refactoring.common._
import scala.tools.refactoring.implementations.OrganizeImports
import scala.reflect.internal.util.BatchSourceFile
import scala.tools.refactoring.analysis.GlobalIndexes

object doOrganizeImports {
  def main(args:Array[String]): Unit = {
    println(
      refactor(
"""
      package tests.importing

      import scala.collection.mutable.{ListBuffer, HashMap}
      import scala.io.Source
      import scala.math.BigInt
      import scala.math._

      import scala.util.{Properties => ScalaProperties}
      object Main {
        // we need to actually use the imports, otherwise they are removed
        val lb = ListBuffer(1)
        val lb = HashMap(1 -> 1)
        var no: Source.type = null
        var elem: Source = null
        var bigi: BigInt = null
        var bigd: BigDecimal = null
        var props: ScalaProperties = null
      }
    """
      )
    )    
  }
  /*
  def apply(
    sourceDirectories: Seq[File],
    includeFilter:     FileFilter,
    excludeFilter:     FileFilter,
    configuration:     Configuration,
    streams:           TaskStreams
  ) = {

    def log(label: String, logger: Logger)(message: String)(count: String) =
      logger.info(message.format(count, label))

    /*
    val files = sourceDirectories.descendantsExcept(includeFilter, excludeFilter).get.toSet
    files.filter(_.exists).foreach{ file =>
      val code = IO.read(file)
      refactor(file, code) match {
        case f: Result.Failure => streams.log.error( generateErrorMessage( file.toString, code, f.index ) )
        case _ =>
      }
    }
    */
    ()
  }
  */
  def fileName = "someFileName"
  def refactor(source: String): String = {
    val file = new BatchSourceFile(fileName, source)
    try{
      val refactoring = new OrganizeImports with GlobalIndexes{
        val global = CompilerInstance.compiler
        import global._
        
        val tree: Tree = ask { () =>
          val response = new Response[Tree]
          askLoadedTyped(file, true, response)
          unitOfFile(file.file).body
          global.unitOfFile(file.file).body
        }

        override val index = ask { () =>
          GlobalIndex( // --> scala.reflect.internal.FatalError: class StringContext does not have a member f
            //List(
            //  CompilationUnitIndex(
                tree//global.unitOfFile(tree.pos.source.file).body
            //  )
            //)
          )
        }
      }
      import refactoring._
      import refactoring.global._

      ask { () =>
        /*
        response.get match {
          case Left(value) => value
          case Right(error) => throw error // scala.reflect.internal.FatalError: class StringContext does not have a member f
        }
        */

        /*
        if (project.expectCompilingCode) {
          trees.foreach { tree =>
            tree.find(_.isErroneous).foreach { erroneousTree =>
              val src = new String(tree.pos.source.content)
              val sep = "------------------------------------"
              throw new AssertionError(s"Expected compiling code but got:\n$sep\n$src\n$sep\nErroneous tree: $erroneousTree")
            }
          }
        }
        */

        val selection = refactoring.FileSelection(tree.pos.source.file, tree, 0, 0)
        val parameters: RefactoringParameters = new RefactoringParameters(
          importsToAdd = List(("ai.x", "CollectionExtensions")),
          options = List(SortImports),
          deps = Dependencies.FullyRecompute
          //organizeLocalImports = false
        )

        val changes: List[Change] =
          refactoring.prepare(selection) match {
            case Right(prepare) =>
              refactoring.perform(selection, prepare, parameters) match {
                case Right(modifications) => modifications
                case Left(error) => throw new RuntimeException(error.cause)
              }
            case Left(error) => throw new RuntimeException(error.cause)
          }

          println(changes)

        val textFileChanges = changes collect {
          case tfc: TextChange if tfc.sourceFile.file.name == fileName => tfc
        }
        
        Change.applyChanges(textFileChanges, source)
      }
    } catch { case _: InterruptedException => "" }
  }
}
