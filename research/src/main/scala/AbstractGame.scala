import GameTypes.*

abstract class AbstractGame (
  gameMode: GameType,
  possWorlds: List[(Int, Int)],
  reports: List[Report],
  history: List[Seq[Int]],

                   ) {
  def findPayoff(depth: Int, maxDepth: Int): Rational
}
