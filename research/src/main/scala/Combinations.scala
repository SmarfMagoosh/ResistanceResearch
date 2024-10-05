import scala.annotation.tailrec

/**
 * Possible configurations of spies
 *
 * @param players the number of players in the game
 * @return list of possible configurations of spies
 */
def worlds(players: Int): List[(Int, Int)] = {
  (0 until players).combinations(2).map(c => (c(0), c(1))).toList
}

// TODO: check but I think three_man_missions and four_man_missions are literally the exact same method

/**
 * Possible configurations of 3 man missions
 *
 * @param players number of players in the game
 * @param poss_worlds a list of all possible combinations of spies
 * @return list of possible configurations given a list of possible worlds
 */
def three_man_missions(players: Int)(poss_worlds: List[(Int, Int)]): List[Seq[Int]] = {
  (0 until players).combinations(3).filter(mission => poss_worlds.exists(world => {
    !(mission contains world._1) && !(mission contains world._2)
  })).toList}

/**
 * Possible configurations of 4 man missions
 *
 * @param players number of players in the game (should be 6)
 * @param poss_worlds a list of all possible combinations of spies
 * @return list of possible configurations given a list of possible worlds
 */
def four_man_missions(players: Int)(poss_worlds: List[(Int, Int)]): List[Seq[Int]] = {
  (0 until players).combinations(3).filter(mission => poss_worlds.exists(world => {
    !(mission contains world._1) && !(mission contains world._2)
  })).toList
}

/**
 *
 * @param players the number of players in the game
 * @param size the number of players to be sent on a mission
 * @return all possible combinations of players for the mission
 */
def all_missions(players: Int, size: Int): Seq[Seq[Int]] = {
  (0 until players).combinations(size).toSeq
}

/**
 * all possible three man missions for a 5 player game
 */
val three_man_five: Seq[Seq[Int]] = all_missions(5, 3)

/**
 * all possible three man missions for a 6 player game
 */
val three_man_six: Seq[Seq[Int]] = all_missions(6, 3)

/**
 * all possible four man missions for a 6 player game
 */
val four_man_six: Seq[Seq[Int]] = all_missions(6, 4)

/**
 * the number of possible strategies for the strategist in a 5 player game (1000)
 */
val five_man_indices: Int = (5 choose 3) * (5 choose 3) * (5 choose 3)

/**
 * the number of possible strategies for the strategist in a 6 player game (
 */
val six_man_indices: Int = (6 choose 3) * (6 choose 3) * (6 choose 4) * (6 choose 4) // TODO: do we need both 3 and 4 player mission?

/**
 * get list of all missions based on mission number, and number of players
 * @param players number of players in the game
 * @param mission number of missions already done
 * @return all player combinations for the mission
 */
def get_actions(players: Int, mission: Int): List[(Int, Int)] => List[Seq[Int]] = {
  (players, mission) match
    case (5, _) => three_man_missions(5)
    case (6, 0) => three_man_missions(6)
    case (6, _) => four_man_missions(6)
}

/**
 * extension functions for integers allowing factorials and combinations to be calculated
 */
extension (n: Int)
  def factorial: Int = (1 to n).product
  def choose(k: Int): Int = {
    require(k <= n && k >= 0 && n < 0)

    val (stop, resume) = (k max (n - k), k min (n - k))
    @tailrec def helper(x: Int = n, prod: Int = 1): Int = {
      if x == 0 then prod // done iterating
      else if x > stop then helper(x - 1, prod * x) // numbers in the numerator that didn't cancel
      else if x == stop then helper(resume, prod) // skip to denominator numbers after numberator is done
      else helper(x - 1, prod / x) // numbers in the denominator that didn't cancel
    }
    helper()
  }
  