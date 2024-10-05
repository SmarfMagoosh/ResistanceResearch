object Main extends App {
  @main def main(): Unit = {
    //val P = strategy(5, List(Seq(0, 1, 2)))
    //val Q = strategy(5, List(Seq(0, 1, 2), Seq(0, 1, 3)))
    //val R = strategy(5, List(Seq(2, 3, 4), Seq(2, 3, 4), Seq(2, 3, 4)))
    //println(s"$P $Q $R")
    //runGame(game_mode = GameType.MerlinTwoMords)
    //  for g <- List(GameType.Resistance, GameType.MerlinTwoMords, GameType.MerlinOnly) do {
    //    val result = generateAllPossibleReportLists(5, g)
    //    val intent = getPossibleSpyIntentions(g)
    //    val map = mapSpyIntentionsToOutcomeCategories(5, g, intent, result)
    //    val reducedMap = reduceSpyIntentionsMap(map)
    //    println(s"$g")
    //    // Print out the reports each player (merlin, spy1, spy2) can generate
    //    println("Possible reports for each player: ")
    //    for i <- result do {
    //      print("\tList: ")
    //      for j <- i do {
    //        val temp = j._2.mkString("Array(", ", ", ")")
    //        print(s"$temp ")
    //      }
    //      println()
    //    }
    //    // Print the intents the spies can have for this game
    //    println(s"Possible intents: $intent")
    //    // Print the reports consistent with those intents
    //    println("MAP")
    //    for key <- map.keys do {
    //      println(s"\t$key:")
    //      for i <- map(key) do {
    //        print("\t\tList: ")
    //        for j <- i do {
    //          val temp = j._2.mkString("(", ", ", ")")
    //          print(s"$temp ")
    //        }
    //        println()
    //      }
    //    }
    //    // Print the reduced version of the map.
    //    println("REDUCED MAP")
    //    for key <- reducedMap.keys do {
    //      println(s"\t$key:")
    //      for outcome <- reducedMap(key) do {
    //        val count = outcome._1
    //        val repList: List[Report] = outcome._2
    //        print(s"\t\t$count ")
    //        for rep <- repList do {
    //          val repText = rep._2.mkString("(", ", ", ")")
    //          print(s"$repText ")
    //        }
    //        println()
    //      }
    //    }
    //    println()
    //  }
    println(-1 choose 6)
  }
}
