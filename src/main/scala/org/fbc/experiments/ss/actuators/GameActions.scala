package org.fbc.experiments.ss.actuators

import com.typesafe.scalalogging.StrictLogging
import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.{text => stext}
import org.fbc.experiments.ss.DebugUtils
import org.fbc.experiments.ss.extractors.GameDetailsExtractor
import org.fbc.experiments.ss.model._

class GameActions

object GameActions extends StrictLogging with DebugUtils {
  private val moveUri = "http://www.boiteajeux.net/jeux/tza/traitement.php?id=%s"
  private val newGameUri = "http://www.boiteajeux.net/gestion.php"

  private val FROM_ACTION = "choisirSource"
  private val TO_ACTION   = "destination"
  private val PASS_ACTION = "passer"


  private def extractInviteId(text: String): Either[String, String] = {
    val regexPattern = """game (\d+).*""".r
    text match {
      case regexPattern(gameId) => Right(gameId)
      case _ => Left(text)
    }
  }

  def startNewGame(browser: Browser, invitation: GameInvitation) = {
    logger.info("startNewGame {}", invitation)
    val form = Map(
      "pAction" -> "creer",
      "pJeu" -> "tza",
      "pNomPartie" -> invitation.gameName,
      "pEloMin" -> invitation.eloFrom.getOrElse(""),
      "pEloMax" -> invitation.eloTo.getOrElse(""),
      "pInvite%5B%5D" -> invitation.invitedPlayer.getOrElse(""),
      "pTypePlateau" -> (if (invitation.setup == SYMMETRICAL) "0" else "1")
    )
    val result = browser.post(newGameUri, form)

    // BAJ doesn't validate input parameters and answers with "game created" - unless you are not logged in
    // (for example, you could give pTypePlateau=17 and the game gets created, with an empty board)
    val gameId = extractInviteId(result >> stext("strong"))
    gameId match {
      case Right(gameId) => gameId
      case Left(msg) => throw new IllegalStateException(msg)
    }
  }

  def joinGame(browser: Browser, login: String, password: String) = ???


  // In every move-making stage there is a form submitted:
  // <form name="fmPlateau" method="post" action="traitement.php?id=37682">
  //    <input type="hidden" name="pAction" value="">
  //    <input type="hidden" name="pL" value="">
  //    <input type="hidden" name="pC" value="">
  //    <input type="hidden" name="pIdCoup" value="598413fbb7dfb">
  //	<input type="button" class="clBouton" value="PASS" "="" onclick="faire('passer',0,0)">
  //  </form>
  // Move stages (pAction) are:
  // * choose source (`choisirSource`)-> `destination` / `annuler`, or
  // * pass (`passer`)
  // The only "strange" thing here is `pIdCoup` parameter. (It's probably some kind of timestamp or turn "marker".)
  // I am not sure if it is required. For beginning, I'll send it exactly as original BAJ page does
  def makeMove(browser: Browser, gameId: String, fullMove: FullMove) = {
    logger.info("makeMove {}", fullMove)

    require(fullMove.firstMove.moveType != PASS, "You cannot PASS on first move")
    require(fullMove.firstMove.moveType == CAPTURE, "First move must be capture")

    makeCaptureOrStackMove(browser, gameId, fullMove.firstMove)
    fullMove.secondMove.moveType match {
      case PASS => makePassMove(browser, gameId)
      case _ => makeCaptureOrStackMove(browser, gameId, fullMove.secondMove)
    }
  }

  def makePassMove(browser: Browser, gameId: String) = {
    logger.info("makePassMove")
    val mark = GameDetailsExtractor.extractTurnMarker(WebFetcher.getGameDetailsDoc(browser, gameId))
    val doc = postPassAction(browser, mark, gameId)
  }

  def makeCaptureOrStackMove(browser: Browser, gameId: String, move: Move) = {
    require(move.from.nonEmpty && move.to.nonEmpty, "Capture or stack move needs `from` and `to`")

    // get game page and extract pIdCoup
    var mark = GameDetailsExtractor.extractTurnMarker(WebFetcher.getGameDetailsDoc(browser, gameId))

     // send first half move (and read new pIdCoup)
    var doc = postMoveAction(browser, mark, gameId, FROM_ACTION, move.from.get)
//    logger.info("--------------- dumping response html")
//    dumpToFile("response_1.html", doc.toHtml)
    mark = GameDetailsExtractor.extractTurnMarker(doc)

    // send second half move
    doc = postMoveAction(browser, mark, gameId, TO_ACTION, move.to.get)
    mark = GameDetailsExtractor.extractTurnMarker(doc)
  }

  private def postMoveAction(browser: Browser, mark: String, gameId: String, action: String, position: String) = {
    logger.info("post move form {}, {}", action, position)
    val (row, column) = new BajBoard().toPhysicalCoordinates(position)
    logger.info("column and row for {} are {}", position, (column, row))
    val moveForm = Map("pAction"-> action, "pL"-> row.toString, "pC"-> column.toString, "pIdCoup" -> mark)
    browser.post(String.format(moveUri, gameId), moveForm)
  }

  private def postPassAction(browser: Browser, mark: String, gameId: String) = {
    logger.info("post move form with pass action")
    val moveForm = Map("pAction"-> PASS_ACTION, "pL"-> "", "pC"-> "", "pIdCoup" -> mark)
    browser.post(String.format(moveUri, gameId), moveForm)
  }

}
