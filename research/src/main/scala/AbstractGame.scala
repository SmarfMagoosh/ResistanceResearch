import GameTypes._
import Roles._

/**
 * abstract version of the game class, allows 5 play and 6 player games to be treated separately
 *
 * @param gameMode    the version of resistance being played
 * @param plausWorlds a list of plausible combinations of spies
 * @param reports     a list of reports submitted to the strategist
 * @param history     a list of previous actions taken by the strategist
 * @param winRates    a 2D payoff matrix for max team win rates.
 *                    winRates[i][j] = % of wins when strategist follows strategy i and
 *                    puppetmaster follows strategy j
 * @param guessRates  a 2D payoff matrix for min team guess rates
 *                    guessRates[i][j] = % of wins when strategist follows strategy i and
 *                    puppetmaster follows strategy j
 */
abstract class AbstractGame(
                             gameMode: GameType,
                             plausWorlds: List[(Int, Int)],
                             reports: List[Report],
                             history: List[Seq[Int]],
                             winRates: Array[Rational],
                             guessRates: Array[Array[Rational]]
                           ) {

  /**
   * number of players sitting at the virtual table
   */
  val numPlayers: Int

  /**
   * consider all reports and return a list of plausible worlds (where a world is represented by a
   * tuple of ints indicating who the two spies are in that world)
   * maybe done... needs testing
   *
   * @return
   */
  def filterWorlds: List[(Int, Int)] = {
    val filterer = filterImplausible(plausWorlds)
    reports.flatMap(filterer).distinct
  }

  /**
   * Main game loop method. follows a strategy to its end and returns the equilibrium value of the terminal
   * node after being propagated back to the root
   *
   * @param depth    the current depth of the game tree (0, 1, 2, or 3)
   * @param maxDepth the number of missions in a game (3, or 4)
   * @return the minimax value of the game tree
   */
  def findPayoff(depth: Int, maxDepth: Int): Rational

  def payoffMatrix: Array[Array[Rational]]

  def assignGuessRate(hist: List[Seq[Int]]): Unit = {
    val numMissions = getNumMissions(numPlayers)
    val numEqClasses = 1 << numMissions
    val eqClasses = getEquivalenceClasses(hist, numPlayers)

    (0 until numEqClasses).foreach(eqClass => {
      guessRates(Strategy(numPlayers, hist))(eqClass) = chanceOfMerlinGuessed(eqClasses(eqClass))
    })
  }

  /**
   * finds the odds that merlin is guessed by the puppetmaster given a specific equivalence class of players
   *
   * @param group the list of players that share the same equivalence class
   * @return a rational number representing the probability of merlin being guessed out of the group
   */
  private def chanceOfMerlinGuessed(group: Seq[Int]): Rational = {
    if gameMode == Resistance || !(group contains 0) then Rational.zero else Rational(1, group.length)
  }

  private def getEquivalenceClasses(hist: List[Seq[Int]], numMissions: Int): Map[Int, IndexedSeq[Int]] = {
    def helper(i: Int, j: Int): List[Boolean] = {
      if j == numMissions then Nil else {
        (j < hist.length && (hist(j) contains i)) :: helper(i, j + 1)
      }
    }

    (0 until numPlayers).groupBy(i => getEqClassIndex(helper(i, 0)))
  }
}
