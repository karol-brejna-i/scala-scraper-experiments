package org.fbc.experiments.ss.model


sealed trait GameRepresentationTrait

case class GameListItem(val gameId: String, val gameName: String, val userWhite: String,
                        val userBlack: String, val userOnMove: String, deadline: String) extends GameRepresentationTrait

case class GameBoard() extends GameRepresentationTrait

case class Piece(colour: String, value: String, stack : Integer) extends GameRepresentationTrait