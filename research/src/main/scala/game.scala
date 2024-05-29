
import GameType.GameType

import scala.language.postfixOps



class Game(
            val game_mode: GameType = GameType.Resistance,
            val num_players: Int = 5,
            var poss_worlds: List[(Int, Int)] = worlds(5),
            val reports: List[Report] = List.empty,
            val history: List[Seq[Int]] = List.empty,
            val winRates: Array[Double],
            val guessRates: Array[Array[Double]]) {

  /**
   * Generalizing function to report the size of teams for different player counts.
   *
   * @param players The number of players
   * @return The team sizes - [3,3,3] for 5 players, or [3,4,3,4] for 6 players
   */
  private def getTeamSizesForPlayers(players: Int): List[Int] = {
    if players == 5 then List(3, 3, 3)
    else List(3, 4, 3, 4)
  }

  /**
   * Generalizing function to report the number of rounds for different player counts
   *
   * @param players The number of players
   * @return The number of rounds - 3 for 5 players, or 4 for 6 players
   */
  private def getNumMissionsForPlayers(players: Int): Int = {
    getTeamSizesForPlayers(players).length
  }

  /**
   * Calculates the payoff for a particular pure strategy assuming optimal play.
   *
   * @param depth Current depth of the mission performed (e.g., depth 1 is first mission)
   * @param maxDepth The total number of missions to be performed
   * @param verbose True if insight print statements should fire
   * @return Nash equilibrium value
   */
  def findPayoff(depth: Int = 1, maxDepth: Int = 3, verbose: Boolean = false): (Int, Int) = {
    if verbose then println(s"")

    // filter off worlds at the beginning
    if depth == 1 then poss_worlds = filter_worlds

    // get a list of all viable actions
    val actions: List[Seq[Int]] = get_actions(num_players, depth)(poss_worlds)

    val equilibriums = for action <- actions yield {
      // split possible worlds into successes and fails given an action
      val (successes, fails) = poss_worlds.partition(actionSucceeds(action))
      if verbose then println(s"${tabs(depth)} Depth: $depth, $action ${successes.length}/${poss_worlds.length}")

      val hist = action :: history
      if depth == maxDepth then {
        // if the game is over, return the probability that the action succeeded
        val ret = (successes.length, poss_worlds.length)
        val strat = strategy(num_players, hist)
        winRates(strat) = ret._1.doubleValue / ret._2
        assignGuessRates(hist)
        ret
      } else {
        // otherwise recurse on the fails and calculate further equilibriums
        winRates(strategy(num_players, hist)) = 1.0 * successes.length / (successes.length + fails.length)
        assignGuessRates(hist)
        val action_eq = Game(
          game_mode,
          num_players,
          fails,
          reports,
          hist,
          winRates,
          guessRates
        ).findPayoff(depth + 1, maxDepth, verbose)
        val ret = ((action_eq * (fails.length, 1)) / (poss_worlds.length, 1)) + (successes.length, poss_worlds.length)
        ret
      }
    }
    val ret = equilibriums.max
    if verbose then println(s"${tabs(depth-1)} $ret")
    ret
  }

  /**
   * Determines the odds of randomly guessing Merlin from among the specified group, on the assumption that
   *   Merlin is player 0.
   *
   * @param group The indices of the players to guess among.
   * @return The probability of a successful Merlin guess, if Merlin is in the group, or 0 otherwise.
   */
  private def merlinGuessed(group: Seq[Int]): Double = {
    if !(group contains 0) || (game_mode == GameType.Resistance) then 0.0
    else {
      1.0 / group.length
    }
  }


  /**
   * After dividing players into equivalence classes based on the history, calculates the odds of
   *   the Puppetmaster guessing merlin for each equivalence class and records that in the guessRates table.
   * @param hist A history of all missions sent so far
   */
  private def assignGuessRates(hist: List[Seq[Int]]): Unit = {
    val numMissions = getNumMissionsForPlayers(players = num_players)
    val numEqClasses = Math.pow(2, numMissions).toInt
    val eqClasses = createEquivalenceClasses(hist, numMissions)

    (0 until numEqClasses).foreach(i => {
      guessRates(strategy(num_players, hist))(i) = merlinGuessed(eqClasses.getOrElse(i, List.empty))
    })
  }

  /**
   * Divides players into a set of equivalence classes such that all players in a given equivalence class have
   *   participated in the same missions up to this point. The total number of equivalence classes produced is
   *   pow(2, # of missions to perform); if any missions have not yet been performed, all players are assumed
   *   to not participate.
   *
   * @param hist A history of all missions sent so far
   */
  private def createEquivalenceClasses(hist: List[Seq[Int]], numMissions: Int): Map[Int, IndexedSeq[Int]] = {

    val eqClasses = (0 until num_players).groupBy(i => {
      var presence: List[Boolean] = List()
      for (j <- 0 until numMissions) {
        presence =
          if j < hist.length then {
            (hist(j) contains i) :: presence
          } else {
            false :: presence
        }
      }
      getEqClassIndex(presence)
    })
    eqClasses

//      val eqs = (0 until num_players).groupBy(i => {
//        val (m3, m2, m1) = hist.length match {
//          case 3 => (hist.head contains i, hist(1) contains i, hist(2) contains i)
//          case 2 => (hist.head contains i, hist(1) contains i, false)
//          case 1 => (hist.head contains i, false, false)
//          case 0 => (false, false, false)
//        }
//        eq_class(m1, m2, m3)
//      })
//      (0 until 8).foreach(i => {
//        guessRates(strategy(num_players, hist))(i) = merlinGuessed(eqs.getOrElse(i, List.empty))
//      })
  }

  //TODO: Come back for this and below
  def payoffMatrix: Array[Array[Double]] = {
    val payoff: Array[Array[Double]] = (0 until 1000).map(_ => (0 until 512).map(_ => 0.0).toArray).toArray
    for i <- 110 until 1110 do {
      val strat = strategy_inverse(num_players, i)
      val p_idx = strategy(num_players, strat.tail.tail)
      val q_idx = strategy(num_players, strat.tail)
      val p = winRates(p_idx)
      val q = winRates(q_idx)
      val r = winRates(i)
      val result = p + ((1 - p) * q) + ((1 - p) * (1 - q) * r)
      for {
        j <- 0 until 8
        k <- 0 until 8
        l <- 0 until 8
      } do {
        val (guess1, guess2, guess3) = (1 - guessRates(p_idx)(j), 1 - guessRates(q_idx)(k), 1 - guessRates(i)(l))
        val nextPay = (p * guess1) + (((1 - p) * q) * guess2) + (((1 - p) * (1 - q) * r) * guess3)
        //if i == 122 then println("" + (p, q, r) + " " + (guess1, guess2, guess3) + " " + nextPay + " " + (i-110) + " " + ((j * 64) + (k * 7) + l))
        payoff(i - 110)((j * 64) + (k * 7) + l) = {nextPay
        }
      }
    }
    payoff
  }

  /**
   * TODO: I (Dellinger) don't understand this function at all.
   * @return
   */
  private def filter_worlds: List[(Int, Int)] = {
    val merlins = reports.map(_.assertions.zipWithIndex.filter((c, _) => c == 'm').head._2)

    merlins.length match {
      case 0 => poss_worlds
      case _ => poss_worlds.filter((spy1, spy2) => {
        merlins.count(_ == spy1) + merlins.count(_ == spy2) == merlins.length - 1
      })
    }
  }

  /**
   * Determines whether a particular team assignment would succeed in th specified world.
   * @param action The team sent on a particular mission
   * @param world The world in question (in particular, the spies for that possible world)
   * @return True if the mission contains no spies, false otherwise
   */
  private def actionSucceeds(action: Seq[Int])(world: (Int, Int)): Boolean = {
    val enemies = List(world._1, world._2)
    !action.exists(enemies.contains)
  }

  /**
   * Returns a number of tabs equal to n, for formatting
   * @param n The number of desired tabs
   * @return A string containing that many tabs.
   */
  private def tabs(n: Int): String = (0 until n-1).map(_ => '\t').mkString
}

/**
 * Core game loop for Resistance/Avalon. Executes all variants of game and calculates payoff matrices.
 * TODO: Make this more generic and handle six players.
 *
 * @param game_mode
 * 0 -> resistance
 * 1 -> Merlin + 2 mordreds
 */
def runGame(players: Int = 5, game_mode: GameType = GameType.Resistance, verbose: Boolean = false): Double = {
  val wr: Array[Double] = if players == 5 then {
    (0 until 1110).toArray.map(_ => 0.0)
  } else {
    (0 until 2410).toArray.map(_ => 0.0)
  }
  val gr: Array[Array[Double]] = if players == 5 then {
    (0 until 1110).toArray.map(_ => (0 until 8).toArray.map(_ => 0.0))
  } else {
    (0 until 2410).toArray.map(_ => (0 until 8).toArray.map(_ => 0.0))
  }
  val maxRounds: Int = if players == 5 then {
    2
  } else {
    3
  }
  game_mode match
    case GameType.Resistance =>
      val game = Game(
        num_players = players,
        poss_worlds = worlds(players),
        winRates = wr.clone(),
        guessRates = gr.clone()
      )
//      println(game.winRates.zip(game.guessRates).zipWithIndex.map((data: (Double, Array[Double]), i: Int) => {
//        s"$i\t: ${data._1} ${data._2.mkString("[", " ", "]")}"
//      }).mkString("\n"))
      val ret = game.findPayoff(maxDepth = maxRounds, verbose = verbose)
    case _ =>
      report_possibilities(players, game_mode).map(reports =>
        //println(reports)
        val game = Game(
          game_mode,
          players,
          worlds(players),
          reports,
          winRates = wr.clone(),
          guessRates = gr.clone()
        )
        val ret = game.findPayoff(maxDepth = maxRounds, verbose = verbose)
        println(game.payoffMatrix.zipWithIndex.map((row, i) => s"$i: ${row.mkString("[", ", ", "]")}").mkString("\n"))
//        println(game.winRates.zip(game.guessRates).zipWithIndex.map((data: (Double, Array[Double]), i: Int) => {
//          s"$i\t: ${data._1} ${data._2.mkString("[", " ", "]")}"
//        }).mkString("\n"))
        ret
      ).min
  0.0
}

/**
 * Converts participation in a set of missions into an equivalence class index.
 * @param missions A list of the missions participated in; true if the given player(s) participated in that mission
 * @return A value between 0 and Math.pow(2, # missions), excluding the later endpoint.
 */
def getEqClassIndex(missions: List[Boolean]): Int = {
  var total = 0
  for (i <- missions.indices) {
    if missions(i) then total += Math.pow(2,i).toInt
  }
  total
}

/**
 * Converts a given sequence of teams chosen (for the specified number of players) into a row in the payoff matrix.
 * TODO: Update this for six players.
 *
 * @param players The number of players.
 * @param history A history of all missions sent.
 * @return An index in the payoff matrix
 */
def strategy(players: Int, history: List[Seq[Int]]): Int = {
  val six_player = players == 6
  val d3 = if six_player then three_man_six else three_man_five
  val d2 = if six_player then four_man_six else three_man_five
  val d1 = if six_player then four_man_six else three_man_five
  if (history.size == 3) {
    (d3.indexOf(history.head) + 1) * d2.length * d1.length
      + (d2.indexOf(history(1)) + 1) * d1.length
      + d1.indexOf(history(2))
  } else if (history.size == 2){
    d1.length * (d2.indexOf(history.head) + 1) + d1.indexOf(history(1))
  }
  else {
    d1.indexOf(history.head)
  }
}

/**
 * Converts an index in the payoff matrix (for a given number of players) into a strategy.
 * TODO: Update this for six players.
 *
 * @param players The number of players.
 * @param strategyIdx The index of the given strategy in the payoff matrix.
 * @return
 */
def strategy_inverse(players: Int, strategyIdx: Int): List[Seq[Int]] = {
  val six_player = players == 6
  val d3 = if six_player then three_man_six else three_man_five
  val d2 = if six_player then four_man_six else three_man_five
  val d1 = if six_player then four_man_six else three_man_five
  if strategyIdx < d1.length then {
    List(d1(strategyIdx))
  } else if strategyIdx < d1.length * d2.length then {
    val firstHistIdx = strategyIdx % d1.length
    List(d1(firstHistIdx), d2(((strategyIdx - firstHistIdx) / d1.length) - 1))
  } else {
    val firstHistIdx = strategyIdx % d1.length
    val secondHistIdx = (((strategyIdx - firstHistIdx) / d1.length) - 1) % d2.length
    List(d1(firstHistIdx), d2(secondHistIdx), d3((strategyIdx / (d1.length * (d2.length + 1))) - 1))
  }
}

@main def main(): Unit = {
  //val P = strategy(5, List(Seq(0, 1, 2)))
  //val Q = strategy(5, List(Seq(0, 1, 2), Seq(0, 1, 3)))
  //val R = strategy(5, List(Seq(2, 3, 4), Seq(2, 3, 4), Seq(2, 3, 4)))
  //println(s"$P $Q $R")
  //runGame(game_mode = GameType.MerlinTwoMords)
  for g <- List(GameType.Resistance, GameType.MerlinTwoMords, GameType.MerlinOnly) do {
    val result = generateAllPossibleReportLists(5, g)
    val intent = getPossibleSpyIntentions(g)
    val map = mapSpyIntentionsToOutcomeCategories(5, g, intent, result)
    val reducedMap = reduceSpyIntentionsMap(map)
    println(s"$g")
    // Print out the reports each player (merlin, spy1, spy2) can generate
    println("Possible reports for each player: ")
    for i <- result do {
      print("\tList: ")
      for j <- i do {
        val temp = j._2.mkString("Array(", ", ", ")")
        print(s"$temp ")
      }
      println()
    }
    // Print the intents the spies can have for this game
    println(s"Possible intents: $intent")
    // Print the reports consistent with those intents
    println("MAP")
    for key <- map.keys do {
      println(s"\t$key:")
      for i <- map(key) do {
        print("\t\tList: ")
        for j <- i do {
          val temp = j._2.mkString("(", ", ", ")")
          print(s"$temp ")
        }
        println()
      }
    }
    // Print the reduced version of the map.
    println("REDUCED MAP")
    for key <- reducedMap.keys do {
      println(s"\t$key:")
      for outcome <- reducedMap(key) do {
        val count = outcome._1
        val repList: List[Report] = outcome._2
        print(s"\t\t$count ")
        for rep <- repList do {
          val repText = rep._2.mkString("(", ", ", ")")
          print(s"$repText ")
        }
        println()
      }
    }
    println()
  }
}