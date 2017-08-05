package org.fbc.experiments.ss.extractors

import com.typesafe.scalalogging.StrictLogging
import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Element
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.{attr, element, elementList, text => stext}
import org.fbc.experiments.ss.model.GameBoard.fieldNames
import org.fbc.experiments.ss.model.{GameBoard, GameMetadata, Piece}


object GameDetailsExtractor extends StrictLogging {

  def apply(doc: Browser#DocumentType) = extractData(doc)

  def extractData(doc: Browser#DocumentType) = {
    val boardElements = doc >> elementList("body > div > table > tbody > tr > td")

    val gameMetadataElement = boardElements(0) >> element("td > table > tbody > tr:nth-child(2) > td > table > tbody")
    val gameBoardElement = boardElements(1) >> element("div:nth-child(2)")
    val gameHistoryElement = boardElements(2)

    val pieces = getPieces(gameBoardElement)
    GameBoard(mapPiecesToPositions(pieces), getGameMetadata(gameMetadataElement))
  }

  def extractTurnMarker (doc: Browser#DocumentType) = {
    doc >> "input[name=pIdCoup]" >> attr("value")
  }

  private def getPlayers(element: Element) = {
  }

  private def getGameMetadata(metadataElement : Element) = {
    val gameId = (metadataElement  >> stext("tr:nth-child(1)")).split(" ")(1).substring(1)
    val gameName = (metadataElement  >> stext("tr:nth-child(2)")).stripPrefix("\"").stripSuffix("\"")
    val lines = metadataElement  >> elementList("tr:nth-child(7) > td > div > table > tbody > tr")

    val isWhiteOnMove = (lines(0) >> "span" >> attr("style")).contains("red")

    GameMetadata(gameId, gameName, lines(0).text, lines(2).text, if (isWhiteOnMove) "WHITE" else "BLACK")
  }

  private def getPieces(boardElement: Element) = {
    val piecesElements = boardElement >> elementList("div > div > div")

    val pieceCodes = (piecesElements >> attr("style")).map(
      it => {
        val regexPattern = """url\('img/(\d)(\d).png'\)""".r
        for (m <- regexPattern.findFirstMatchIn(it)) yield (m.group(1), m.group(2))
      }
    )

    val pieces: List[Option[Piece]] = pieceCodes.map(
      _ match {
        case Some(c) => Some(decodePiece(c))
        case None => None
      }
    )

    val pieceStacks = piecesElements >?> element("img") >> attr("src")

    val stacks : List[Int] = pieceStacks.map(
      _ match {
        case Some(c) => decodeStack(c).getOrElse(0)
        case None => 0
      }
    )

    (pieces, stacks).zipped.map(
      (a, height) => {
        a match {
          case Some(piece) => Some(Piece(piece.colour, piece.value, height))
          case None => None
        }
      }
    )
  }

  private def mapPiecesToPositions(pieces: List[Option[Piece]]) = {
    (pieces, fieldNameLookup).zipped.collect{case p if p._1.nonEmpty => (p._2 -> p._1.get)}.toMap
  }

  /**
    * Baj stores the fields (game details page) top-down from left-right.
    * After extracting board fields from BAJ page we'll get them in the following order A1, A2, A3, .... B1, B2, ...
    * Additionally:
    * BAJ doesn't hold "out of the board" fields, so we need to remove them ("-") from field names list,
    * but it keeps the empty center field (so we need to keep a name for it)
    * @return
    */
  def transposedFiledNames = fieldNames.sliding(9, 9).toList.reverse.transpose
  def fieldNameLookup = transposedFiledNames.flatten.zipWithIndex.filter{ it => (it._1 != "-" || it._2 == 40)}.unzip._1

  // the equivalent of above
  val bajFieldNames = List(
    "A5", "A4", "A3", "A2", "A1",
    "B6", "B5", "B4", "B3", "B2", "B1",
    "C7", "C6", "C5", "C4", "C3", "C2", "C1",
    "D8", "D7", "D6", "D5", "D4", "D3", "D2", "D1",
    "E8", "E7", "E6", "E5", "-", "E4", "E3", "E2", "E1",
    "F8", "F7", "F6", "F5", "F4", "F3", "F2", "F1",
    "G7", "G6", "G5", "G4", "G3", "G2", "G1",
    "H6", "H5", "H4", "H3", "H2", "H1",
    "I5", "I4", "I3", "I2", "I1")

  private def decodePiece(code: Tuple2[String, String]): Piece = {
    new Piece(code._1 match {
      case "1" => "TZAAR"
      case "2" => "TZAARA"
      case "3" => "TOOT"
    }, if (code._2 == "1") "WHITE" else "BLACK", 0)
  }

  private def decodeStack(code: String) = {
    logger.debug(s"decodeStack $code")
    val regexPattern = """img/num(\d).gif""".r
    for (m <- regexPattern.findFirstMatchIn(code)) yield m.group(1).toInt
  }
}
