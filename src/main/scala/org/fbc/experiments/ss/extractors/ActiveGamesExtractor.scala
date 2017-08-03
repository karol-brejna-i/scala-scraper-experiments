package org.fbc.experiments.ss.extractors

import com.typesafe.scalalogging.StrictLogging
import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Element
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.{elementList, text => stext}
import org.fbc.experiments.ss.model.GameListItem

object ActiveGameListExtractor extends StrictLogging {

  def apply(doc: Browser#DocumentType) = extractData(doc)

  def extractData(doc: Browser#DocumentType) = {
    logger.info("getting games in progress")
    val games = getGamesInProgress(doc)
    val list = games.getOrElse(List())

    list.map( extractGameListItem(_) )
  }

  private def getGamesInProgress(gameListPage: Browser#DocumentType) = {
    logger.info("getGamesInProgress")
    val idx = gameListPage.toHtml.indexOf("You have to log in to access this page!")
    if (idx > 0) {
      logger.warn(s"Obtaining games in progress, but not logged in.")
    }
    val items = gameListPage >?> elementList("#dvEnCours> div[class^=clLigne]")
    items
  }

  private def extractGameListItem(element: Element) = {
    val gameId = element >> stext("div:nth-child(1)")
    val gameName = element >> stext("div:nth-child(2)")
    val users = element >> elementList("div:nth-child(3) > a > span")
    val userOnMove = element >> stext("div:nth-child(3) > a > span[style~=red]")

    val userWhite = users(0).text
    val userBlack = users(1).text
    new GameListItem(gameId, gameName, userWhite, userBlack, userOnMove, "")
  }
}
