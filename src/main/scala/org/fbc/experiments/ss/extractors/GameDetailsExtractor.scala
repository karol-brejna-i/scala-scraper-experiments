package org.fbc.experiments.ss.extractors

import com.typesafe.scalalogging.StrictLogging
import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Element
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.{attr, element, elementList, text => stext}
import org.fbc.experiments.ss.model.Piece


object GameDetailsExtractor extends StrictLogging {

  def apply(doc: Browser#DocumentType) = extractData(doc)

  def extractData(doc: Browser#DocumentType) = {
    val boardElements = doc >> elementList("body > div > table > tbody > tr > td")

    val gameMetadataElement = boardElements(0) >> element("td > table")
    val gameBoardElement = boardElements(1) >> element("div:nth-child(2)")
    val gameHistoryElement = boardElements(2)

    val pieces = getPieces(gameBoardElement)
    pieces
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
      (a, hight) => {
        a match {
          case Some(piece) => Piece(piece.colour, piece.value, hight)
          case None => None
        }
      }
    )
  }

  private def decodePiece(code: Tuple2[String, String]): Piece = {
    new Piece(code._1 match {
      case "1" => "TOOT"
      case "2" => "TZAAR"
      case "3" => "TZAARA"
    }, if (code._2 == "1") "WHITE" else "BLACK", 0)
  }

  private def decodeStack(code: String) = {
    logger.debug(s"decodeStack $code")
    val regexPattern = """img/num(\d).gif""".r
    for (m <- regexPattern.findFirstMatchIn(code)) yield m.group(1).toInt
  }
}
