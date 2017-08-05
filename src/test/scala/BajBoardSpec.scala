import org.fbc.experiments.ss.model.BajBoard
import org.scalatest.FlatSpec

class BajBoardSpec extends FlatSpec {

  "BajBoard" should "throw illegal argument exception when illegal position is converted" in {
    val board = new BajBoard()
    assertThrows[IllegalArgumentException] { board.toPhysicalCoordinates("-") }
    assertThrows[IllegalArgumentException] { board.toPhysicalCoordinates("AA1") }
    assertThrows[IllegalArgumentException] { board.toPhysicalCoordinates("") }
    assertThrows[IllegalArgumentException] { board.toPhysicalCoordinates("A") }
    assertThrows[IllegalArgumentException] { board.toPhysicalCoordinates("1") }
  }

  it should "accept legal positions" in {
    val positions = for { x <- 'A' to 'I'; y <- 1 to (if (x == 'E') 8 else 9 -Math.abs( 4 - (x-65))) } yield s"$x$y"
    val board = new BajBoard()
    positions.foreach { it =>
      val (row, column) = board.toPhysicalCoordinates(it)
    }
  }

  it should "convert correctly column E" in {
    val positions = for (x <- 1 to 8) yield s"E$x"
    val expectedResults = List(8, 7, 6, 5, 3, 2, 1, 0).map( (_, 4) )
    val board = new BajBoard()
    (positions, expectedResults).zipped.toList.foreach( it => assert(board.toPhysicalCoordinates(it._1) == it._2, s"Wrong result for ${it._1}"))
  }


  it should "convert correctly column D" in {
    val positions = for (x <- 1 to 8) yield s"D$x"
    val expectedResults = List(7, 6, 5, 4, 3, 2, 1, 0).map( (_, 3) )
    val board = new BajBoard()
    (positions, expectedResults).zipped.toList.foreach( it => assert(board.toPhysicalCoordinates(it._1) == it._2, s"Wrong result for ${it._1}"))
  }

  it should "convert correctly A2, E7, A5, E1, I5" in {
    val positions = List("A2", "E7", "A5", "E1", "I5", "E5")
    val expectedResults = List((3, 0), (1, 4),(0, 0), (8, 4), (4, 8), (3, 4))
    val board = new BajBoard()
    (positions, expectedResults).zipped.toList.foreach( it => assert(board.toPhysicalCoordinates(it._1) == it._2, s"Wrong result for ${it._1}"))
  }
}
