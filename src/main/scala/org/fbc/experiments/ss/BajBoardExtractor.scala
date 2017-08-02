package org.fbc.experiments.ss

import com.typesafe.scalalogging.StrictLogging
import net.ruippeixotog.scalascraper.browser.{Browser, JsoupBrowser}
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.elementList
import org.fbc.experiments.ss.extractors.{ActiveGameListExtractor, GameDetailsExtractor}


object BajBoardExtractor extends App with StrictLogging {

  val browser = JsoupBrowser()

  val basicUri = "http://www.boiteajeux.net"
  val loginUri = "http://www.boiteajeux.net/gestion.php"
  val inProgressUri = "http://www.boiteajeux.net/index.php?p=encours"

  val password = System.getenv().get("BAJ_PASSWORD")
  val login = System.getenv().get("BAJ_LOGIN")

  val gameId = "37682"

  def staticFileExperiment(browser: Browser) = {
    logger.info("staticFileExperiment")
    //val doc = browser.parseResource("/board-1.html", "UTF-8")
    val doc = browser.parseResource("/board-2.html", "UTF-8")
    //  val doc2 =  browser.get("http://www.boiteajeux.net/jeux/tza/partie.php?id=37682")
    val items = doc >> elementList("div div")
  }


  // --------------------------------------------------------------
  def loginPost(browser: Browser) = {
    logger.info("login")
    val form = Map("p" -> "", "pAction" -> "login", "username" -> login, "password" -> password)
    browser.post(loginUri, form)
  }

  def showCookies(browser: Browser) = {
    logger.info("show cookies")
    logger.info(s"basic uri: ${browser.cookies(basicUri)}")
  }

  def obtainGames(browser: Browser) = {
    val result = ActiveGameListExtractor.obtainGames(browser)

    logger.info(s"------------------------------- $result")
  }


  def latestExperiment(browser: Browser) = {
    val result = GameDetailsExtractor(browser, gameId)

    logger.info(s"------------------------------- ")
    logger.info(s" result pocz ${result}")
//    logger.info(s" result count: ${result.size}")
  }

  loginPost(browser)

  latestExperiment(browser)
}
