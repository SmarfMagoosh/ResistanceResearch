import GameTypes.{GameType, Resistance}

abstract class FivePlayerGame(
            val gameMode: GameType = Resistance,
            val numPlayers: Int = 5,
            var possWorlds: List[(Int, Int)] = worlds(5),
            val reports: List[Report] = List.empty,
            val history: List[Seq[Int]] = List.empty,
            val winRates: Array[Double],
            val guessRates: Array[Array[Double]]) {


}
