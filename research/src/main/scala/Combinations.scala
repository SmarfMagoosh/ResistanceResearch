/**
 * Possible configurations of spies
 *
 * @param n the number of players in the game
 * @return list of possible configurations
 */
def worlds(n: Int): List[(Int, Int)] = {
  (0 until n).combinations(2).map(c => (c(0), c(1))).toList
}

/**
 * Possible configurations of 3 man missions
 *
 * @param n number of players in the game
 * @return list of possible configurations given a list of possible worlds
 */
def three_man_missions(players: Int)(poss_worlds: List[(Int, Int)]): List[Seq[Int]] = {
  poss_worlds.map((0 until players) diff _.toList)
}

/**
 * Possible configurations of 4 man missions
 *
 * @param n number of players in the game (should be 6)
 * @return list of possible configurations given a list of possible worlds
 */
def four_man_missions(players: Int)(poss_worlds: List[(Int, Int)]): List[Seq[Int]] = {
  poss_worlds.map((0 until players) diff _.toList)
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
val five_man_indices: Int = Math.pow(5 choose 3, 3).toInt

/**
 * the number of possible strategies for the strategist in a 6 player game (
 */
val six_man_indices: Int = (6 choose 3) * (6 choose 4) * (6 choose 4)

/**
 * get list of all missions based on mission number, and number of players
 * @param players number of players in the game
 * @param mission number of missions already done
 * @return all player combinations for the mission
 */
def get_actions: (Int, Int) => List[(Int, Int)] => List[Seq[Int]] = {
  case (5, _) => three_man_missions(5)
  case (6, 0) => three_man_missions(6)
  case (6, _) => four_man_missions(6)
}



/**
 * extension functions for integers allowing factorials and combinations to be calculated
 */
extension (n: Int)
  def factorial: Int = (1 to n).product
  def choose(k: Int): Int = n.factorial / (k.factorial * (n-k).factorial) // TODO: possibly inefficient but probably fine
  