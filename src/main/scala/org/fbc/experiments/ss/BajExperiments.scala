package org.fbc.experiments.ss

import com.typesafe.scalalogging.StrictLogging
import net.ruippeixotog.scalascraper.browser.{Browser, JsoupBrowser}
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.elementList
import org.fbc.experiments.ss.extractors.{ActiveGameListExtractor, GameDetailsExtractor}
import org.fbc.experiments.ss.fetchers.{ResourceFetcher, WebFetcher}


object BajExperiments extends App with StrictLogging {

  val browser = JsoupBrowser()
  val password = System.getenv().get("BAJ_PASSWORD")
  val login = System.getenv().get("BAJ_LOGIN")
  val gameId = "37682"

  private def logResult(result : Any) : Unit = {
    logger.info(" result: {}", result)
    result match {
      case a : Traversable[Any] =>  logger.info(" result size: {}", a.size)
      case _ =>
    }
  }
  def staticFileExperiment(browser: Browser) = {
    logger.info("staticFileExperiment")
    val doc = ResourceFetcher.getGameDetailsDoc(browser, "/board-2.html")
    val result = doc >> elementList("div div")
    logResult(result)
  }

  def obtainGames(browser: Browser) = {
    val doc = WebFetcher.getGamesInProgressDoc(browser)
    val result = ActiveGameListExtractor(doc)
    logResult(result)
  }

  def obtainBoard(browser: Browser) = {
    val doc = WebFetcher.getGameDetailsDoc(browser, gameId)
    val result = GameDetailsExtractor(doc)
    logResult(result)
  }

  def latestExperiment(browser: Browser) = {
    val doc = ResourceFetcher.getGameDetailsDoc(browser, "/game-details-half-move.html")
    val result = GameDetailsExtractor(doc)
    logResult(result)
  }

  //loginPost(browser)
  //staticFileExperiment(browser)
  latestExperiment(browser)
}
