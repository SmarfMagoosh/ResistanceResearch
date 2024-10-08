import GameTypes.{GameType, Resistance}

abstract class SixPlayerGame(
                              gameMode: GameType = Resistance,
                              plausWorlds: List[(Int, Int)] = spyCombos(6),
                              reports: List[Report] = List.empty,
                              history: List[Seq[Int]] = List.empty,
                              winRates: Array[Rational],
                              guessRates: Array[Array[Rational]])
  extends AbstractGame(gameMode, plausWorlds, reports, history, winRates, guessRates) {

  override val numPlayers: Int = 6

  override def payoffMatrix: Array[Array[Rational]] = {
    // TODO: implement
    Array.empty
  }

  override def assignGuessRate(hist: List[Seq[Int]]): Unit = {
    // TODO: implement
  }

  override def findPayoff(depth: Int, maxDepth: Int): Rational = {
    Rational.zero
  }
}
