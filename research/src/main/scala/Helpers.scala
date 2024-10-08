import Roles._

/**
 * Generalizing function to report the size of teams for different player counts.
 */
def getTeamSizes: Int => List[Int] =
  case 5 => 3 :: 3 :: 3 :: Nil
  case 6 => 3 :: 4 :: 3 :: 4 :: Nil

/**
 * Generalizing function to report the number of rounds for different player counts
 */
def getNumMissions: Int => Int =
  case 5 => 3
  case 6 => 4

/**
 * object that defines methods for mapping strategies to indices (apply), and inverting the operation (unapply)
 */
object Strategy {
  def apply(players: Int, history: List[Seq[Int]]): Int = {
    val d1 = allMissions(players, if players == 6 then 4 else 3)
    val d2 = d1
    val d3 = allMissions(players, 3)

    history.size match
      case 1 => d1.indexOf(history.head)
      case 2 => d1.length * (d2.indexOf(history.head) + 1) + d1.indexOf(history(1))
      case 3 => (d3.indexOf(history.head) + 1) * d2.length * d1.length
        + (d2.indexOf(history(1)) + 1) * d1.length
        + d1.indexOf(history(2))
  }

  // TODO: maybe switch to extractor object?
  // TODO: also account for possibly four rounds of gameplay for 6 player games
  def inverse(players: Int, stratIdx: Int): List[Seq[Int]] = {
    val d1 = allMissions(players, if players == 6 then 4 else 3)
    val d2 = d1
    val d3 = allMissions(players, 3)
    if stratIdx < d1.length then d1(stratIdx) :: Nil
    else if stratIdx < d1.length * d2.length then {
      val firstHistIdx = stratIdx % d1.length
      val secondHistIdx = ((stratIdx - firstHistIdx) / d1.length) - 1
      d1(firstHistIdx) :: d2(secondHistIdx) :: Nil
    } else {
      val firstHistIdx = stratIdx % d1.length
      val secondHistIdx = (((stratIdx - firstHistIdx) / d1.length) - 1) % d2.length
      val thirdHistIdx = (stratIdx / (d1.length * (d2.length + 1))) - 1
      d1(firstHistIdx) :: d2(secondHistIdx) :: d3(thirdHistIdx) :: Nil
    }
  }
}

/**
 * Converts participation in a set of missions into an equivalence class index.
 *
 * @param missions A list of the missions participated in; true if the given player(s) participated in that mission
 * @return A value between 0 and Math.pow(2, # missions), excluding the later endpoint.
 */
def getEqClassIndex(missions: List[Boolean]): Int = {
  missions.foldLeft(0)((acc, b) => (acc << 1) & (if b then 1 else 0))
}

/**
 * Determines whether a particular team assignment would succeed in th specified world.
 *
 * @param action The team sent on a particular mission
 * @param world  The world in question (in particular, the spies for that possible world)
 * @return True if the mission contains no spies, false otherwise
 */
def actionSucceeds(action: Seq[Int])(world: (Int, Int)): Boolean = {
  action.forall(player => player != world._1 || player != world._2)
}

/**
 * takes all game information and converts it into a map of equivalence classes based on mission participation
 *
 * @param hist       the list of missions done in the game
 * @param numPlayers the number of players in the game
 * @return a map of equivalence classes to the players in that equivalence class
 */
def getEquivalenceClasses(hist: List[Seq[Int]], numPlayers: Int): Map[Int, Seq[Int]] = {
  def helper(i: Int, j: Int): List[Boolean] = j match
    case 0 => Nil
    case x => (x < hist.length && (hist(x) contains i)) :: helper(i, x - 1)

  (0 until numPlayers).groupBy(i => getEqClassIndex(helper(i, 0))).withDefaultValue(Nil)
}

/**
 * used for printing out trees
 * gets a string containing n number of tabs where n is the argument passed in
 *
 * @return a string containing only tabs
 */
def tabs: Int => String =
  case 0 => ""
  case n => s"\t${tabs(n - 1)}"

/**
 * takes a list of spy combinations and filters out implausible worlds given a report
 * in a game of avalon, the total list of possible worlds will be the union of all plausible
 * worlds filtered by each report
 *
 * @param possWorlds all spy combinations
 * @param report     the report to filter by
 * @return
 */
def filterImplausible(possWorlds: List[(Int, Int)])(report: Report): List[(Int, Int)] = {
  report match
    // if merlin is reporting, filter out any world that doesn't include all claimed spies in it
    case Report(origin, Merlin, assertions) => possWorlds.filter((x, y) => {
      assertions.forall(spy => spy == x || spy == y)
    })
    // TODO: implement
    case Report(origin, Percival, assertions) => possWorlds.filter((x, y) => {
      val argsAsList = x :: y :: Nil
      true

      /**
       * TODO: implement because things get weiiiiiiiird I think
       * for example, suppose we have a truthful percival report as follows
       * Report(3, Percival, Vector(0, 2)) that is to say, player 3 claims to be percival and makes
       * a claim about players 0 and 2 (one being merlin one being morgana)
       * The easy solution is to say that any world is plausible according to this report
       * so long as exactly one of 0 and 2 are one of the spies in the world.
       * This includes (0, 1) (0, 3) (0, 4) (1, 2) (2, 3) (2, 4).
       *
       * I think when percival's report is to be considered there's a chance that a world is plausible
       * if for any percival report and merlin report, the world is plausible.
       * I.E you have to get the cartesian product of merlin reports and percival reports and check
       * plausibility between them all
       *
       * Do I have a basis for this? No. Do I believe it anyway? yes
       */
    })
    case _ => Nil // default case so that the code compiles, but it will never be reached
}

def getWinRatesArray: Int => Array[Rational] =
  case 5 => Array.fill(1110)(Rational.zero)

def getGuessRatesArray: Int => Array[Array[Rational]] =
  case 5 => Array.fill(1110)(Array.fill(8)(Rational.zero))