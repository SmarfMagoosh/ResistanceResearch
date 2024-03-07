import scala.util.Random

/**
 * Possible configurations of spies
 * @param players the number of players in the game
 * @return list of possible configurations
 */
def worlds(players: Int): List[(Int, Int)] = (for {
    i <- 0 until players - 1
    j <- i + 1 until players
  } yield (i, j)).toList

def three_man_missions(num_players: Int): List[Array[Int]] = (for {
  i <- 0 until num_players - 2
  j <- i + 1 until num_players - 1
  k <- j + 1 until num_players
} yield Array(i, j, k)).toList

val four_man_missions: List[Array[Int]] = (for {
  i <- 0 until 3
  j <- i + 1 until 4
  k <- j + 1 until 5
  l <- k + 1 until 6
} yield Array(i, j, k, l)).toList

/**
 * IGNORE FOR VERSION 1
 * Code definitions in a report
 * n -> no comment
 * g -> good
 * b -> bad
 * m -> merlin
 * p -> percival
 * e -> morgana or merlin (for percival)
 */
class Report(origin: Int, assertions: Array[Char]) {
  var valid: Option[Boolean] = None
}

class Game(
  val num_players: Int = 5,
  val poss_worlds: List[(Int, Int)] = worlds(5),
  val reports: List[Report] = List.empty,
  val history: List[Array[Int]] = List.empty,
  val knowledge: List[Option[Boolean]] = List(None, None, None, None, None)) {

  def gather_reports: Game = this // TODO: implement

  def findEquilibrium(depth: Int = 0): Double = {
    val actions: List[Array[Int]] = (num_players, depth) match
      case (5, _) => three_man_missions(5)
      case (6, 1) => three_man_missions(6)
      case (6, _) => four_man_missions

    val equilibriums = for action <- actions yield {
      val (successes, fails) = poss_worlds.partition(action_succeeds(action))
      if depth == 2 then successes.length.doubleValue / poss_worlds.length
      else {
        val action_eq = Game(
          num_players, // same number of players
          fails, // only recurse on a failure
          reports, // TODO: update
          action :: history, // add previously failed action to information history
          knowledge // TODO: update
        ).findEquilibrium(depth + 1)
        ((action_eq * fails.length) / poss_worlds.length) + (1.0 * successes.length / poss_worlds.length)
      }
    }
    equilibriums.max
  }

  def action_succeeds(action: Array[Int])(world: (Int, Int)): Boolean = {
    val enemies = List(world._1, world._2) // TODO: implement more complex
    action.forall(player => !(enemies contains player))
  }
}

def create_game(players: Int): Game = Game(
  num_players = players,
  poss_worlds = worlds(players))

@main def main(): Unit = {
  val g: Game = create_game(6)
  val e = g.findEquilibrium()
  println(e)
}