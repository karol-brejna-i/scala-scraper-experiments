/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.fbc.experiments.ss.api

import com.typesafe.scalalogging.StrictLogging
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import org.fbc.experiments.ss.actuators.{GameActions, WebFetcher}
import org.fbc.experiments.ss.extractors.{ActiveGameListExtractor, GameDetailsExtractor}
import org.fbc.experiments.ss.model.{FullMove, GameBoard, GameInvitation, GameMetadata}

import scala.collection.immutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait GameApi extends StrictLogging {

  def createGame(login: String, password: String, invitation: GameInvitation): Future[String] = {
    logger.info(s"startNewGame $login, password, $invitation")
    val browser = JsoupBrowser()
    WebFetcher.loginPost(browser, login, password)
    Future {
      GameActions.startNewGame(browser, invitation)
    }
  }

  def joinGame(login: String, password: String, gameId: String): Future[String] = {
    logger.info(s"joinGame $login, password, $gameId")
    val browser = JsoupBrowser()
    WebFetcher.loginPost(browser, login, password)
    Future {
      GameActions.joinGame(browser, gameId)
    }
  }

  def makeMove(login: String, password: String, gameId: String, fullMove: FullMove): Future[String] = {
    logger.info("makeMove $login, password: $gameId, $fullMove")
    val browser = JsoupBrowser()
    WebFetcher.loginPost(browser, login, password)
    Future {
      GameActions.makeMove(browser, gameId, fullMove)
    }
  }

  def getActiveGames(login: String, password: String): Future[immutable.Seq[GameMetadata]] = {
    logger.info("getActiveGames $login, password")
    val browser = JsoupBrowser()
    WebFetcher.loginPost(browser, login, password)
    Future {
      ActiveGameListExtractor(WebFetcher.getGamesInProgressDoc(browser))
    }
  }

  def getGame(login: String, password: String, gameId: String): Future[GameBoard] = {
    logger.info("getGame $login, password, $gameId")
    val browser = JsoupBrowser()
    WebFetcher.loginPost(browser, login, password)
    Future {
      GameDetailsExtractor(WebFetcher.getGameDetailsDoc(browser, gameId))
    }
  }
}
