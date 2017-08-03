package org.fbc.experiments.ss.model


sealed trait GameRepresentationTrait


case class GameMetadata(gameId: String, gameName: String, userWhite: String,
                        userBlack: String, sideOnMove: String) extends GameRepresentationTrait

case class Piece(colour: String, value: String, stack: Integer) extends GameRepresentationTrait

case class GameBoard(piecePositions: Map[String, Piece], metadata: GameMetadata) extends GameRepresentationTrait

object GameBoard {
  /**
    * Fields names (one by one) in emulation of the hexagonal TZAAR board as s quadratic 9x9 array.
    * See: http://iuuk.mff.cuni.cz/~vesely/tzaar/thesis.pdf
    *
    * I am not sure if it sticks well to the board notation shown in TZAAR manual (and on BAJ page).
    * @return
    */
  def fieldNames = List(
    "A1", "B1", "C1", "D1", "E1", "-", "-", "-", "-",
    "A2", "B2", "C2", "D2", "E2", "F1", "-", "-", "-",
    "A3", "B3", "C3", "D3", "E3", "F2", "G1", "-", "-",
    "A4", "B4", "C4", "D4", "E4", "F3", "G2", "H1", "-",
    "A5", "B5", "C5", "D5", "-", "F4", "G3", "H2", "I1",
    "-", "B6", "C6", "D6", "E5", "F5", "G4", "H3", "I2",
    "-", "-", "C7", "D7", "E6", "F6", "G5", "H4", "I3",
    "-", "-", "-", "D8", "E7", "F7", "G6", "H5", "I4",
    "-", "-", "-", "-", "E8", "F8", "G7", "H6", "I5")

}