//import GameTypes._
//import Roles._
//
//class Game(
//            val gameMode: GameType = Resistance,
//            val numPlayers: Int = 5,
//            var possWorlds: List[(Int, Int)] = worlds(5),
//            val reports: List[Report] = List.empty,
//            val history: List[Seq[Int]] = List.empty,
//            val winRates: Array[Rational],
//            val guessRates: Array[Array[Rational]]) {
//
//  /**
//   * Calculates the payoff for a particular pure strategy assuming optimal play.
//   *
//   * @param depth Current depth of the mission performed (e.g., depth 1 is first mission)
//   * @param maxDepth The total number of missions to be performed
//   * @param verbose True if insight print statements should fire
//   * @return Nash equilibrium value
//   */
//  def findPayoff(depth: Int = 1, maxDepth: Int = 3, verbose: Boolean = false): Rational = {
//    // filter off worlds at the beginning
//    if depth == 1 then possWorlds = filter_worlds
//
//    // get a list of all viable actions
//    val actions: List[Seq[Int]] = get_actions(numPlayers, depth)(possWorlds)
//
//    val equilibriums = for action <- actions yield {
//      // split possible worlds into successes and fails given an action
//      val (successes, fails) = possWorlds.partition(actionSucceeds(action))
//      val hist = action :: history
//      val strat = Strategy(numPlayers, hist)
//      
//      if depth == maxDepth then {
//        // if the game is over, return the probability that the action succeeded
//        val ret = Rational(successes.length, possWorlds.length)
//        
//        winRates(strat) = ret
//        assignGuessRates(hist)
//        ret
//      } else {
//        // otherwise recurse on the fails and calculate further equilibriums
//        winRates(Strategy(numPlayers, hist)) = Rational(successes.length, successes.length + fails.length)
//        assignGuessRates(hist)
//        val action_eq = Game(
//          gameMode,
//          numPlayers,
//          fails,
//          reports,
//          hist,
//          winRates,
//          guessRates
//        ).findPayoff(depth + 1, maxDepth, verbose)
//        action_eq * Rational(fails.length, possWorlds.length) + Rational(successes.length, possWorlds.length)
//      }
//    }
//    val ret = equilibriums.max
//    if verbose then println(s"${tabs(depth-1)} $ret")
//    ret
//  }
//
//  /**
//   * Determines the odds of randomly guessing Merlin from among the specified group, on the assumption that
//   *   Merlin is player 0.
//   *
//   * @param group The indices of the players to guess among.
//   * @return The probability of a successful Merlin guess, if Merlin is in the group, or 0 otherwise.
//   */
//  private def merlinGuessed(group: Seq[Int]): Rational = {
//    if !(group contains 0) || (gameMode == Resistance) then Rational.zero else Rational(1, group.length)
//  } // Implemented
//
//  /**
//   * After dividing players into equivalence classes based on the history, calculates the odds of
//   *   the Puppetmaster guessing merlin for each equivalence class and records that in the guessRates table.
//   * @param hist A history of all missions sent so far
//   */
//  private def assignGuessRates(hist: List[Seq[Int]]): Unit = {
//    val numMissions = getNumMissions(numPlayers)
//    val numEqClasses = Math.pow(2, numMissions).toInt
//    val eqClasses = getEquivalenceClasses(hist, numMissions, numPlayers)
//
//    (0 until numEqClasses).foreach(i => {
//      guessRates(Strategy(numPlayers, hist))(i) = merlinGuessed(eqClasses.getOrElse(i, List.empty))
//    })
//  }
//
//  //TODO: Come back for this and below
//  def payoffMatrix: Array[Array[Rational]] = {
//    val payoff: Array[Array[Rational]] = Array.fill(1000)(Array.fill(512)(Rational.zero))
//    for i <- 110 until 1110 do {
//      val strat = Strategy.inverse(numPlayers, i)
//      val p_idx = Strategy(numPlayers, strat.tail.tail)
//      val q_idx = Strategy(numPlayers, strat.tail)
//      val p = winRates(p_idx)
//      val q = winRates(q_idx)
//      val r = winRates(i)
//      val result = p + ((1 - p) * q) + ((1 - p) * (1 - q) * r)
//      for {
//        j <- 0 until 8
//        k <- 0 until 8
//        l <- 0 until 8
//      } do {
//        val (guess1, guess2, guess3) = (1 - guessRates(p_idx)(j), 1 - guessRates(q_idx)(k), 1 - guessRates(i)(l))
//        val nextPay = (p * guess1) + (((1 - p) * q) * guess2) + (((1 - p) * (1 - q) * r) * guess3)
//        payoff(i - 110)((j * 64) + (k * 7) + l) = nextPay
//      }
//    }
//    payoff
//  }
//
//  /**
//   * TODO: I (Dellinger) don't understand this function at all.
//   * END: this function takes the list of reports handed to the strategist and filters out implausible
//   * worlds so that only plausible missions are attempted in the game loop
//   * @return
//   */
//  private def filter_worlds: List[(Int, Int)] = {
//    val merlins = reports.map(_.assertions.zipWithIndex.filter((r, _) => r == 0).head._2)
//
//    merlins.length match {
//      case 0 => possWorlds
//      case _ => possWorlds.filter((spy1, spy2) => {
//        merlins.count(_ == spy1) + merlins.count(_ == spy2) == merlins.length - 1
//      })
//    }
//  }
//}
//
/////**
//// * Core game loop for Resistance/Avalon. Executes all variants of game and calculates payoff matrices.
//// * TODO: Make this more generic and handle six players.
//// *
//// * @param game_mode
//// */
////def runGame(players: Int = 5, game_mode: GameType = Resistance, verbose: Boolean = false): Double = {
////  val wr: Array[Rational] = if players == 5 then {
////    (0 until 1110).toArray.map(_ => Rational.zero)
////  } else {
////    (0 until 2410).toArray.map(_ => Rational.zero)
////  }
////  val gr: Array[Array[Rational]] = if players == 5 then {
////    (0 until 1110).toArray.map(_ => (0 until 8).toArray.map(_ => Rational.zero))
////  } else {
////    (0 until 2410).toArray.map(_ => (0 until 8).toArray.map(_ => Rational.zero))
////  }
////  val maxRounds: Int = if players == 5 then 2 else 3
////  game_mode match
////    case Resistance =>
////      val game = Game(
////        numPlayers = players,
////        possWorlds = worlds(players),
////        winRates = wr.clone(),
////        guessRates = gr.clone()
////      )
//////      println(game.winRates.zip(game.guessRates).zipWithIndex.map((data: (Double, Array[Double]), i: Int) => {
//////        s"$i\t: ${data._1} ${data._2.mkString("[", " ", "]")}"
//////      }).mkString("\n"))
////      val ret = game.findPayoff(maxDepth = maxRounds, verbose = verbose)
////    case _ =>
////      report_possibilities(players, game_mode).map(reports =>
////        //println(reports)
////        val game = Game(
////          game_mode,
////          players,
////          worlds(players),
////          reports,
////          winRates = wr.clone(),
////          guessRates = gr.clone()
////        )
////        val ret = game.findPayoff(maxDepth = maxRounds, verbose = verbose)
////        println(game.payoffMatrix.zipWithIndex.map((row, i) => s"$i: ${row.mkString("[", ", ", "]")}").mkString("\n"))
//////        println(game.winRates.zip(game.guessRates).zipWithIndex.map((data: (Double, Array[Double]), i: Int) => {
//////          s"$i\t: ${data._1} ${data._2.mkString("[", " ", "]")}"
//////        }).mkString("\n"))
////        ret
////      ).min
////  0.0
////}
//
//
//
