package org.fbc.experiments.ss

import java.io.PrintWriter

import com.typesafe.scalalogging.StrictLogging

trait DebugUtils extends StrictLogging {


  def dumpToFile(fileName: String, contents: String) = {
    new PrintWriter(fileName) { write(contents); close }
  }

  def logResult(result : Any) : Unit = {
    logger.info(" result: {}", result)
    result match {
      case a : Traversable[Any] =>  logger.info(" result size: {}", a.size)
      case _ =>
    }
  }
}
