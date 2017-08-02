package org.fbc.experiments.ss.extractors

import com.typesafe.scalalogging.StrictLogging
import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.{Document, Element}
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.{element, elementList, text => stext}
import org.fbc.experiments.ss.model.GameListItem


object GameDetailsExtractor extends StrictLogging {
  private val gameDetailsUri = "http://www.boiteajeux.net/jeux/tza/partie.php?id=%s"

  def apply(browser: Browser, gameId: String) = {

    val doc = getGameDetailsDoc(browser, gameId)
    val boardElements = doc >> elementList("body > div > table > tbody > tr > td")

    val gameMetadataElement = boardElements(0) >> element("td > table")
    val gameBoardElement = boardElements(1) >> element("div:nth-child(2)")
    val gameHistoryElement = boardElements(2)

    gameBoardElement

  }

  private def getGameDetailsDoc(browser: Browser, gameId: String) = {
    logger.info("getGameDetailsDoc")
    val gameDetailsDoc = browser.get(String.format(gameDetailsUri, gameId))
    gameDetailsDoc
  }

}
