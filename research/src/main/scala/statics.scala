import scala.annotation.tailrec

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

val three_man_five: Seq[Seq[Int]] = all_missions(5, 3)

val three_man_six: Seq[Seq[Int]] = all_missions(6, 3)

val four_man_six: Seq[Seq[Int]] = all_missions(6, 4)

val five_man_indices: Int = Math.pow(5 choose 3, 3).toInt

val six_man_indices: Int = (Math.pow(6 choose 3, 2).toInt * (Math.pow(6 choose 4, 2))).toInt

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

/**
 * should allows us to do the whole "precise" values by representing a rational number as
 * a tuple of ints which we can now perform arithmetic operations on
 */
extension (rational: (Int, Int))
  def *(o: (Int, Int)): (Int, Int) = (rational._1 * o._1, rational._2 * o._2).reduce
  def /(o: (Int, Int)): (Int, Int) = rational * o.swap
  def +(o: (Int, Int)): (Int, Int) = ((rational._1 * o._2) + (rational._2 * o._1), rational._2 * o._2).reduce
  def -(o: (Int, Int)): (Int, Int) = rational + (-o._1, o._2)
  
  def reduce: (Int, Int) = {
    @tailrec def gcd(a: Int, b: Int): Int = {
      if a != b then a else {
        if a > b then gcd(a - b, b) else gcd(a, b - a)
      }
    }
    val numerator = rational._1
    val denominator = rational._2
    if numerator == 0 then {
      (0, 1)
    } else {
      val negative: Boolean = ((numerator < 0) || (denominator < 0)) && !((numerator < 0) && denominator < 0)
      val num = Math.abs(numerator)
      val den = Math.abs(denominator)
      val scale = if negative then -1 else 1
      val divisor = gcd(num, den)
      (scale * num / divisor, den / divisor)
    }
  }