package org.fbc.experiments.ss

import net.ruippeixotog.scalascraper.browser.{Browser, JsoupBrowser}
import org.fbc.experiments.ss.extractors.{ActiveGameListExtractor, GameDetailsExtractor}
import org.fbc.experiments.ss.actuators.{GameActions, ResourceFetcher, WebFetcher}
import org.fbc.experiments.ss.model._


object BajExperiments extends App with DebugUtils {

  val browser = JsoupBrowser()
  val password = System.getenv().get("BAJ_PASSWORD")
  val login = System.getenv().get("BAJ_LOGIN")
  val gameId = "37683"

  def staticFileExperiment(browser: Browser) = {
    logger.info("staticFileExperiment")
    val doc = ResourceFetcher.getGameDetailsDoc(browser, "/game-details-half-move.html")
    val result = GameDetailsExtractor.extractTurnMarker(doc)
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


  def makeMove(browser: Browser) = {
    val result = GameActions.makeMove(browser, gameId,
      FullMove(
        Move(CAPTURE, Some("E7"), Some("E6")),
        Move(STACK, Some("E6"), Some("E8"))
      )
    )
    logResult(result)
  }

  def latestExperiment(browser: Browser) = {
    // doesn't work:
    GameActions.startNewGame(browser, GameInvitation("test5", None, None, Some("testuser"), SYMMETRICAL))
  }

  WebFetcher.loginPost(browser, login, password)
  obtainGames(browser)

}
