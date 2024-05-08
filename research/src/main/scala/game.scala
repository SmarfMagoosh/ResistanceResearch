import scala.util.Random

class Game(
            val game_mode: Int = 0,
            val num_players: Int = 5,
            var poss_worlds: List[(Int, Int)] = worlds(5),
            val reports: List[Report] = List.empty,
            val history: List[Seq[Int]] = List.empty,
            val winRates: Array[Double],
            val guessRates: Array[Array[Double]]) {

  def findEquilibrium(depth: Int = 0, verbose: Boolean = false): Double = {
    if verbose then println(s"")

    // filter off worlds at the beginning
    if depth == 0 then poss_worlds = filter_worlds

    // get a list of all viable actions
    val actions: List[Seq[Int]] = get_actions(num_players, depth)(poss_worlds)

    val equilibriums = for action <- actions yield {
      // split possible worlds into successes and fails given an action
      val (successes, fails) = poss_worlds.partition(action_succeeds(action))
      if verbose then println(s"${tabs(depth)} Depth: $depth, $action ${successes.length.doubleValue / poss_worlds.length}")

      val hist = action :: history
      if depth == 2 then {
        // if the game is over, return the probability that the action succeeded
        val ret = successes.length.doubleValue / poss_worlds.length
        val strat = strategy(num_players, hist)
        winRates(strat) = ret
        equivalenceClasses(hist)
        ret
      } else {
        // otherwise recurse on the fails and calculate further equilibriums
        winRates(strategy(num_players, hist)) = 1.0 * successes.length / (successes.length + fails.length)
        equivalenceClasses(hist)
        val action_eq = Game(
          game_mode,
          num_players,
          fails,
          reports,
          hist,
          winRates,
          guessRates
        ).findEquilibrium(depth + 1, verbose = verbose)
        val ret = ((action_eq * fails.length) / poss_worlds.length) + (1.0 * successes.length / poss_worlds.length)
        ret
      }
    }
    val ret = equilibriums.max
    if verbose then println(s"${tabs(depth)} $ret")
    ret
  }

  def merlinGuessed(mission: Seq[Int]): Double = {
    if !(mission contains 0) || game_mode == 0 then 0.0
    else {
      1.0 / mission.length
    }
  }

  def equivalenceClasses(hist: List[Seq[Int]]): Unit = {
    val eqs = (0 until num_players).groupBy(i => {
      val (m3, m2, m1) = hist.length match {
        case 3 => (hist.head contains i, hist(1) contains i, hist(2) contains i)
        case 2 => (hist.head contains i, hist(1) contains i, false)
        case 1 => (hist.head contains i, false, false)
        case 0 => (false, false, false)
      }
      eq_class(m1, m2, m3)
    })
    (0 until 8).foreach(i => {
      guessRates(strategy(num_players, hist))(i) = merlinGuessed(eqs.getOrElse(i, List.empty))
    })
  }

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

  private def filter_worlds: List[(Int, Int)] = {
    val merlins = reports.map(_.assertions.zipWithIndex.filter((c, _) => c == 'm').head._2)

    merlins.length match {
      case 0 => poss_worlds
      case _ => poss_worlds.filter((spy1, spy2) => {
        merlins.count(_ == spy1) + merlins.count(_ == spy2) == merlins.length - 1
      })
    }
  }

  private def action_succeeds(action: Seq[Int])(world: (Int, Int)): Boolean = {
    val enemies = List(world._1, world._2)
    !action.exists(enemies.contains)
  }

  private def tabs(n: Int): String = (0 until n).map(_ => '\t').mkString
}

/**
 * player count
 * base resistance
 * merlin
 * merlin mordred
 * merlin 2 morded
 *
 * @param game_mode
 * 0 -> resistance
 * 1 -> Merlin + 2 mordreds
 */
def run_game(players: Int = 5, game_mode: Int = 0, verbose: Boolean = false): Double = {
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
  game_mode match
    case 0 =>
      val game = Game(
        num_players = players,
        poss_worlds = worlds(players),
        winRates = wr.clone(),
        guessRates = gr.clone()
      )
//      println(game.winRates.zip(game.guessRates).zipWithIndex.map((data: (Double, Array[Double]), i: Int) => {
//        s"$i\t: ${data._1} ${data._2.mkString("[", " ", "]")}"
//      }).mkString("\n"))
      val ret = game.findEquilibrium(verbose = verbose)
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
        val ret = game.findEquilibrium(verbose = verbose)
        println(game.payoffMatrix.zipWithIndex.map((row, i) => s"$i: ${row.mkString("[", ", ", "]")}").mkString("\n"))
//        println(game.winRates.zip(game.guessRates).zipWithIndex.map((data: (Double, Array[Double]), i: Int) => {
//          s"$i\t: ${data._1} ${data._2.mkString("[", " ", "]")}"
//        }).mkString("\n"))
        ret
      ).min
  0.0
}

def eq_class(m1: Boolean, m2: Boolean = false, m3: Boolean = false): Int = {
  var total = 0
  if m1 then total += 1
  if m2 then total += 2
  if m3 then total += 4
  total
}

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
    List(d1(firstHistIdx), d2(secondHistIdx), d3(((strategyIdx)/ (d1.length * (d2.length + 1))) - 1))
  }
}

@main def main(): Unit = {
  val P = strategy(5, List(Seq(0, 1, 2)))
  val Q = strategy(5, List(Seq(0, 1, 2), Seq(0, 1, 3)))
  val R = strategy(5, List(Seq(2, 3, 4), Seq(2, 3, 4), Seq(2, 3, 4)))
  //println(s"$P $Q $R")
  run_game(game_mode = 1)
}