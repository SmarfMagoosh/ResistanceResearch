import GameTypes.{GameType, Resistance}

/**
 * Data structure for representing a 5 player game of resistance and computing its payoff matrix
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
class FivePlayerGame(
                      gameMode: GameType = Resistance,
                      plausWorlds: List[(Int, Int)] = spyCombos(5),
                      reports: List[Report] = List.empty,
                      history: List[Seq[Int]] = List.empty,
                      winRates: Array[Rational] = getWinRatesArray(5),
                      guessRates: Array[Array[Rational]] = getGuessRatesArray(5))
  extends AbstractGame(gameMode, plausWorlds, reports, history, winRates, guessRates) {

  override val numPlayers: Int = 5

  override def payoffMatrix: Array[Array[Rational]] = {
    Array.tabulate(1000)(i => {
      val thdRndWinIdx = i + 110
      val strategistStrat: List[Seq[Int]] = Strategy.inverse(numPlayers, thdRndWinIdx)
      val fstRndWinIdx = Strategy(numPlayers, strategistStrat.drop(2))
      val sndRndWinIdx = Strategy(numPlayers, strategistStrat.drop(1))
      val fstRndWR = winRates(fstRndWinIdx)
      val sndRndWR = winRates(sndRndWinIdx)
      val thdRndWR = winRates(thdRndWinIdx)
      val overall = fstRndWR + (fstRndWR.inverse * sndRndWR) + (fstRndWR.inverse * sndRndWR.inverse * thdRndWR)

      Array.tabulate(512)(j => {
        val (cnt1, cnt2, cnt3) = (j & 0xFF, (j & 0xFF00) >>> 8, (j & 0xFF0000) >>> 16)
        val (guess1, guess2, guess3) = (guessRates(fstRndWinIdx)(cnt1).inverse, guessRates(sndRndWinIdx)(cnt2).inverse, guessRates(thdRndWinIdx)(cnt3).inverse)
        (fstRndWR * guess1) + (fstRndWR.inverse * sndRndWR * guess2) + (fstRndWR.inverse * sndRndWR.inverse * thdRndWR * guess3)
      })
    })
  }

  override def findPayoff(depth: Int, maxDepth: Int): Rational = {
    // filter off implausible worlds
    val filteredWorlds = if depth == 1 then filterWorlds else plausWorlds

    // get a list of viable actions
    val actions = getActions(numPlayers, depth)(filteredWorlds)

    // for each action, yields its equilibrium
    actions.map(action => {
      // for a given action, split the plausible worlds into those where the actions succeeds and those where it doen't
      val (succs, fails) = filteredWorlds.partition(actionSucceeds(action))

      // prepend the action taken to the action history
      val newHistory = action :: history

      // get the index of the strategy the mediator is using
      val strat = Strategy(numPlayers, newHistory)

      // save the odds that we win at this node in the array
      winRates(strat) = Rational(succs.length, plausWorlds.length)

      // save the odds that we get guessed at this node in the array
      assignGuessRate(newHistory)

      // if we are at the max depth, simply return the winRate to help build the minimax tree
      if depth == maxDepth then winRates(strat) else {
        // otherwise, recurse on all failed actions
        val actionEquilibrium = FivePlayerGame(
          gameMode, fails, reports, newHistory, winRates, guessRates
        ).findPayoff(depth + 1, maxDepth)

        actionEquilibrium * Rational(fails.length, plausWorlds.length) + Rational(succs.length, plausWorlds.length)
      }
    }).max
  }
}
