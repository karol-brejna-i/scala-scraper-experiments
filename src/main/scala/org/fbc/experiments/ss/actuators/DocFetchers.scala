package org.fbc.experiments.ss.actuators

import com.typesafe.scalalogging.StrictLogging
import net.ruippeixotog.scalascraper.browser.Browser

class WebFetcher

object WebFetcher extends StrictLogging {
  private val basicUri = "http://www.boiteajeux.net"
  private val loginUri = "http://www.boiteajeux.net/gestion.php"
  private val gameDetailsUri = "http://www.boiteajeux.net/jeux/tza/partie.php?id=%s"
  private val inProgressUri = "http://www.boiteajeux.net/index.php?p=encours"


  def getGameDetailsDoc(browser: Browser, gameId: String) = {
    logger.info("getGameDetailsDoc")
    browser.get(String.format(gameDetailsUri, gameId))
  }

  def getGamesInProgressDoc(browser: Browser) = {
    logger.info("getGamesInProgress")
    browser.get(s"$inProgressUri")
  }

  def loginPost(browser: Browser, login: String, password: String) = {
    logger.info("login")
    val form = Map("p" -> "", "pAction" -> "login", "username" -> login, "password" -> password)
    browser.post(loginUri, form)
  }

  def showCookies(browser: Browser) = {
    logger.info("show cookies")
    logger.info(s"basic uri: ${browser.cookies(basicUri)}")
  }
}

class ResourceFetcher

object ResourceFetcher extends StrictLogging {

  def getDocFromResource(browser: Browser, resourceName : String) = {
    logger.info("getDocFeromResource")
    browser.parseResource(resourceName, "UTF-8")
  }
  def getGameDetailsDoc(browser: Browser, resourceName : String) = getDocFromResource(browser, resourceName)

  def getGamesInProgressDoc(browser: Browser, resourceName : String) = getDocFromResource(browser, resourceName)

  def loginPost(browser: Browser, login: String, password: String) = {}

  def showCookies(browser: Browser) = {}
}
