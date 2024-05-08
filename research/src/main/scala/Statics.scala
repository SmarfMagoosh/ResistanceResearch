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
 * @return list of possible configurations
 */
def three_man_missions(players: Int)(poss_worlds: List[(Int, Int)]): List[Seq[Int]] = {
  poss_worlds.map((0 until players) diff _.toList)
}

/**
 * Possible configurations of 4 man missions
 *
 * @param n number of players in the game (should be 6)
 * @return list of possible configurations
 */
def four_man_missions(players: Int)(poss_worlds: List[(Int, Int)]): List[Seq[Int]] = {
  poss_worlds.map((0 until players) diff _.toList)
}

def all_missions(players: Int, size: Int): Seq[Seq[Int]] = {
  (0 until players).combinations(size).toSeq
}

val three_man_five = all_missions(5, 3)

val three_man_six = all_missions(6, 3)

val four_man_six = all_missions(6, 4)

val five_man_indices = Math.pow(5 choose 3, 3).toInt

val six_man_indices = ((6 choose 3) * (Math.pow(6 choose 4, 2))).toInt

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
 * Code definitions in a report
 * n -> no comment
 * g -> good
 * b -> bad
 * m -> merlin
 * p -> percival
 * e -> morgana or merlin (for percival)
 */
case class Report(origin: Int, assertions: Array[Char])

extension (n: Int)
  def factorial: Int = (1 to n).product
  def choose(k: Int): Int = n.factorial / (k.factorial * (n-k).factorial)