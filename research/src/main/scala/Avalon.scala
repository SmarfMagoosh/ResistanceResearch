
object GameType extends Enumeration {
  type GameType = Value
  val Resistance, MerlinOnly, MerlinTwoMords = Value
}

object Intentions extends Enumeration {
  type Intentions = Value
  val NoReports, // Neither spy reports
  OneReportJustMe, // One spy claims only himself as Merlin
  TwoReportsJustMe, // Both spies claim only themselves as Merlin
  OneReportSpyOther, // One spy claims himself as Merlin, other spy + somebody as spies
  OneReportTwoOthers, // One spy claims himself as Merlin, two non-spies as spies
  TwoReportsSpySameOther, // Both spies claim themselves as Merlin, the other spy as a spy, and some shared other
  //  as a spy.
  TwoReportsSpyDifferentOthers, // Both spies claim themselves as Merlin, the other spy as a spy, and different others
  // as spies.
  TwoReportsOneSpyThreeTotal, // One player claims a spy + an other, the other spy claims two-non spies w/ overlap
  TwoReportsOneSpyFourTotal, // One player claims a spy + an other, the other spy claims two non-spies w/o overlap
  TwoReportsTwoOthers, // Both spies claim themselves as Merlin, the same two non-spies as spies
  TwoReportsThreeOthers, // Both spies claim themselves as Merlin, one common other, and one distinct other.
  TwoReportsFourOthers = Value // Both spies claim themselves as Merlin, and distinct pairs of non-spies. (Requires 6+ players)
}

/**
 * 'm' = Merlin
 * 'n' = nothing
 * 's' = spy
 *
 * @return
 */
def report_possibilities: (Int, GameType.GameType) => List[List[Report]] = {
  case (_, GameType.Resistance) => List.empty // for resistance
  case (5, GameType.MerlinTwoMords) =>
    List(
      Report(2, Array('n', 'n', 'm', 'n', 'n')),
      Report(1, Array('n', 'm', 'n', 'n', 'n')),
      Report(0, Array('m', 'n', 'n', 'n', 'n'))
    ).tails.filter(_.nonEmpty).toList
  case (6, GameType.MerlinTwoMords) =>
    List(
      Report(2, Array('n', 'n', 'm', 'n', 'n', 'n')),
      Report(1, Array('n', 'm', 'n', 'n', 'n', 'n')),
      Report(0, Array('m', 'n', 'n', 'n', 'n', 'n'))
    ).tails.filter(_.nonEmpty).toList
  case (5, GameType.MerlinOnly) =>
    val merlinRep = List(Report(0, Array('m', 'o', 'o', 'n', 'n')))
    val mord1Reps = List(
      Report(1, Array('s', 'm', 's', 'n', 'n', 'n')),
      Report(1, Array('s', 'm', 'n', 's', 'n', 'n')),
      Report(1, Array('n', 'm', 's', 's', 'n', 'n')),
      Report(1, Array('n', 'm', 'n', 's', 's', 'n')),
    )
    val mord2Reps = List(
      Report(2, Array('s', 's', 'm', 'n', 'n', 'n')),
      Report(2, Array('s', 'n', 'm', 's', 'n', 'n')),
      Report(2, Array('n', 's', 'm', 's', 'n', 'n')),
      Report(2, Array('n', 'n', 'm', 's', 's', 'n')),
    )
    var fullPossibleReports = List(List(Report))
    for i <- merlinRep do {
      for j <- mord1Reps do {
        for k <- mord2Reps do {
          //fullPossibleReports = fullPossibleReports :: List(i, j, k)
          // Look, this is just wrongheaded. Mord1 and Mord2 can coordinate strategies. What we need is the idea
          // of what they're *trying* to do, and how likely each possible result is given that. In other words, we
          // should start with "Mord1 tries to report one unknown plus throw Mord2 under the bus, and Mord 2 does
          // the same for a *different* rando, and how likely is it either of them end up with Merlin?"
          // Not sure how to do that yet, but I think it starts by specifying intentions (which are guaranteed)
          // and then looking at possible results of those intentions. Again, back to "strategy, and likely outcomes" -
          // this has, basically, a chance node component.
        }
      }
    }
    List.empty
  case (_, _) => List.empty // TODO: fill in additional game modes
}

def generateAllPossibleReportLists(numPlayers: Int, gameType: GameType.GameType): List[List[Report]] = {
  var merlins: List[Int] = List()
  var spies: List[Int] = List()
  var mordreds: List[Int] = List()

  if (gameType == GameType.Resistance) {
    spies = List(1, 2)
  } else if (gameType == GameType.MerlinTwoMords) {
    merlins = List(0)
    spies = List(1, 2)
    mordreds = List(1, 2)
  } else if (gameType == GameType.MerlinOnly) {
    merlins = List(0)
    spies = List(1, 2)
  }
  // Get Merlin reports
  var merlinReports: List[Report] = List()
  for i <- merlins do {
    var assertions: List[Char] = List()
    // Baseline empty assertion
    for j <- 0 until numPlayers do {
      assertions = assertions :+ 'n'
    }
    // Report ourselves as Merlin
    assertions = assertions.updated(i, 'm')
    //Report all spies we can see
    for j <- spies do {
      if !mordreds.contains(j) then {
        assertions = assertions.updated(j, 's')
      }
    }
    val rep = Report(i, assertions.toArray)
    merlinReports = merlinReports :+ rep
  }
  // Get all spy reports
  // Here's the number of people a spy should claim as spies
  val nonMordSpies = spies.length - mordreds.length

  var spyReports: List[List[Report]] = List()
  // Keeps track of which list of spy reports we're currently adding to
  var index = -1
  for i <- spies do {
    index += 1
    // All reports by one spy in one list.
    spyReports = spyReports :+ List()
    if gameType != GameType.Resistance then {
      var assertions: List[Char] = List()
      // Baseline empty assertion
      for j <- 0 until numPlayers do {
        assertions = assertions :+ 'n'
      }
      // Report ourselves as Merlin
      assertions = assertions.updated(i, 'm')
      //Report an appropriate number of spies, excluding ourselves.
      val possibleIndices = (0 until numPlayers).filter(z => z != i)
      val possibleSpyClaims = possibleIndices.combinations(nonMordSpies).toArray
      for j: IndexedSeq[Int] <- possibleSpyClaims do {
        for k: Int <- j do {
          assertions = assertions.updated(k, 's')
        }
        val rep = Report(i, assertions.toArray)
        spyReports = spyReports.updated(index, spyReports(index) :+ rep)
        // Remove the particular people we accused as spies
        for k: Int <- j do {
          assertions = assertions.updated(k, 'n')
        }
      }
    }
  }

  // So, final reports: a list of all possible Merlin reports, then a list of all possible spy 1 reports,
  // ... then a list of all possible spy 2 reports, then etc.
  val finalReports: List[List[Report]] = merlinReports :: spyReports
  // I think this is wrongheaded.
  /*
  // Okay, now generate a complete set of reports for all that
  // First, pick a possible merlin report. (Probably only one of these, but futureproofing)
  for (i <- merlinReports) do {
    var spyReportIndices: Vector[Int] = Vector()
    // Get a vector of indices - these are the particular possible spy reports we'll append
    for j <- 0 until spyReports.length do {
      spyReportIndices = spyReportIndices:+ 0
    }
    while spyReportIndices(numPlayers - 1) < numPlayers do {
      // Make a list of our current Merlin report, plus our currently selected spy reports.
      var nextReportList: List[Report] = List(i)
      for j <- 0 until numPlayers do {
        nextReportList = nextReportList:+ spyReports(j)(spyReportIndices(j))
      }
      // Move to the next available spy report - this is basically just "counting" big(?)-endian
      finalReports = finalReports:+ nextReportList
      var currentIndex = 0
      var currentValue = spyReportIndices(currentIndex) + 1
      spyReportIndices = spyReportIndices.updated(currentIndex, currentValue)
      while (currentValue >= numPlayers && currentIndex < numPlayers) {
        spyReportIndices = spyReportIndices.updated(currentIndex, 0)
        currentIndex = currentIndex + 1
        currentValue = spyReportIndices(currentIndex) + 1
        spyReportIndices = spyReportIndices.updated(currentIndex, currentValue)
      }
    }
  }
  */
  finalReports
}

/**
 * ASSUMES TWO SPIES
 * TODO: Revisit that
 */
def getPossibleSpyIntentions(gameType: GameType.GameType): List[Intentions.Intentions] = {
  if gameType == GameType.Resistance then {
    List(Intentions.NoReports)
  } else if gameType == GameType.MerlinTwoMords then {
    List(Intentions.NoReports, Intentions.OneReportJustMe, Intentions.TwoReportsJustMe)
  } else if gameType == GameType.MerlinOnly then {
    List(Intentions.NoReports, Intentions.OneReportSpyOther, Intentions.OneReportTwoOthers,
      Intentions.TwoReportsSpySameOther, Intentions.TwoReportsSpyDifferentOthers,
      Intentions.TwoReportsTwoOthers, Intentions.TwoReportsThreeOthers, Intentions.TwoReportsFourOthers,
      Intentions.TwoReportsOneSpyThreeTotal, Intentions.TwoReportsOneSpyFourTotal)
  } else {
    List.empty
  }
}

def twoListProduct(one: List[Report], two: List[Report]): List[List[Report]] = {
  var combo: List[List[Report]] = List.empty
  for i <- one do {
    for j <- two do {
      combo = combo :+ List(i, j)
    }
  }
  combo
}

def threeListProduct(one: List[Report], two: List[Report], three: List[Report]): List[List[Report]] = {
  var combo: List[List[Report]] = List.empty
  for i <- one do {
    for j <- two do {
      for k <- three do {
        combo = combo :+ List(i, j, k)
      }
    }
  }
  combo
}


def countMerlinsClaimedAsSpies(rep: Report, actual: Array[Char]): Int = {
  var count = 0
  for i <- actual.indices do {
    if rep._2(i) == 's' && actual(i) == 'm' then count = count + 1
  }
  count
}

def countSpiesClaimedAsSpies(rep: Report, actual: Array[Char]): Int = {
  var count = 0
  for i <- actual.indices do {
    if rep._2(i) == 's' && (actual(i) == 's' || actual(i) == 'o') then count = count + 1
  }
  count
}

def countNonesClaimedAsSpies(rep: Report, actual: Array[Char]): Int = {
  var count = 0
  for i <- actual.indices do {
    if rep._2(i) == 's' && actual(i) == 'n' then count = count + 1
  }
  count
}

def countNonSpiesClaimedAsSpies(rep: Report, actual: Array[Char]): Int = {
  var count = 0
  for i <- actual.indices do {
    if rep._2(i) == 's' && actual(i) != 's' && actual(i) != 'o' then count = count + 1
  }
  count
}

def countTotalClaimedSpies(rep: Report): Int = {
  rep._2.count(z => z == 's')
}

def countUniqueClaimedSpies(rep1: Report, rep2: Report): Int = {
  var count = 0
  for i <- rep1._2.indices do {
    if rep1._2(i) == 's' || rep2._2(i) == 's' then count = count + 1
  }
  count
}

def getMapSafely(mapping: Map[Intentions.Intentions, List[List[Report]]], key: Intentions.Intentions):
        List[List[Report]] = {
  if mapping.contains(key) then mapping(key) else List()
}

/**
 * THIS ONE MIGHT ACTUALLY WORK. Given all the reports a spy could conceivably give, it divides them into a mapping
 * of spy intentions to the reports those intentions could in theory produce. I think we can use this to then figure
 * out, for a given intention, the odds of different kinds of outcomes. It needs some additional preprocessing
 * to eliminate, e.g., duplicate results with the spies reversed. Then we can count, for a given intention,
 * how many outcomes produce a given circumstance ('whoops, claimed merlin') and prep a report for each of those.
 * Bonus: I *think* this is straightforwardly extensible for Percival etc.
 *
 * @param numPlayers The number of players in the game.
 * @param gameType   The type of game, e.g., pure Resistance, Merlin only, etc.
 * @param intentions The list of all intentions the spies could have for the given game type
 * @param possibles  The full set of possible reports the spies could produce
 * @return A mapping from intentions consistent with the given game to possible reports produced by that intention
 */
def mapSpyIntentionsToOutcomeCategories(numPlayers: Int, gameType: GameType.GameType,
                                        intentions: List[Intentions.Intentions],
                                        possibles: List[List[Report]]):
Map[Intentions.Intentions, List[List[Report]]] = {
  var mapping: Map[Intentions.Intentions, List[List[Report]]] = Map()

  var merlins: List[Int] = List()
  var spies: List[Int] = List()
  var mordreds: List[Int] = List()
  // The true array of identities
  var actual: Array[Char] = Array()
  for i <- 0 until numPlayers do {
    actual = actual :+ 'n'
  }

  if (gameType == GameType.Resistance) {
    actual(1) = 's'
    actual(2) = 's'
  } else if (gameType == GameType.MerlinTwoMords) {
    actual(0) = 'm'
    actual(1) = 'o'
    actual(2) = 'o'
  } else if (gameType == GameType.MerlinOnly) {
    actual(0) = 'm'
    actual(1) = 's'
    actual(2) = 's'
  }

  // Pure merlin reports go into "no reports"
  var merlinLists: List[List[Report]] = List()
  for (merlinRep <- possibles(0)) do {
    merlinLists = merlinLists :+ List(merlinRep)
  }
  mapping = mapping + (Intentions.NoReports -> merlinLists)

  for merlinRep <- possibles(0) do {
    for spy1Rep <- possibles(1) do {
      // Single spy reports, based on contents of that report
      // Nothing reported except your own identity
      if countTotalClaimedSpies(spy1Rep) == 0 then {
        var current = getMapSafely(mapping, Intentions.OneReportJustMe)
        val nextReport = List(merlinRep, spy1Rep)
        current = current :+ nextReport
        mapping = mapping + (Intentions.OneReportJustMe -> current)
      }
      // Reported an actual spy + somebody
      else if countSpiesClaimedAsSpies(spy1Rep, actual) == 1 then {
        var current = getMapSafely(mapping, Intentions.OneReportSpyOther)
        val nextReport = List(merlinRep, spy1Rep)
        current = current :+ nextReport
        mapping = mapping + (Intentions.OneReportSpyOther -> current)
      }
      // Reported two non-spies
      else if countSpiesClaimedAsSpies(spy1Rep, actual) == 0 then {
        var current = getMapSafely(mapping, Intentions.OneReportTwoOthers)
        val nextReport = List(merlinRep, spy1Rep)
        current = current :+ nextReport
        mapping = mapping + (Intentions.OneReportTwoOthers -> current)
      }

      // Both spies reporting
      for spy2Rep <- possibles(2) do {
        // Nothing reported except your own identity
        if countTotalClaimedSpies(spy1Rep) == 0 && countTotalClaimedSpies(spy2Rep) == 0 then {
          var current = getMapSafely(mapping, Intentions.TwoReportsJustMe)
          val nextReport = List(merlinRep, spy1Rep, spy2Rep)
          current = current :+ nextReport
          mapping = mapping + (Intentions.TwoReportsJustMe -> current)
        }
        // Both claim a spy and one shared other
        else if countUniqueClaimedSpies(spy1Rep, spy2Rep) == 3 && countSpiesClaimedAsSpies(spy1Rep, actual) == 1
          && countSpiesClaimedAsSpies(spy2Rep, actual) == 1 then {
          var current = getMapSafely(mapping, Intentions.TwoReportsSpySameOther)
          val nextReport = List(merlinRep, spy1Rep, spy2Rep)
          current = current :+ nextReport
          mapping = mapping + (Intentions.TwoReportsSpySameOther -> current)
        }
        // Both claim a spy and unique others
        else if countUniqueClaimedSpies(spy1Rep, spy2Rep) == 4 && countSpiesClaimedAsSpies(spy1Rep, actual) == 1
          && countSpiesClaimedAsSpies(spy2Rep, actual) == 1 then {
          var current = getMapSafely(mapping, Intentions.TwoReportsSpyDifferentOthers)
          val nextReport = List(merlinRep, spy1Rep, spy2Rep)
          current = current :+ nextReport
          mapping = mapping + (Intentions.TwoReportsSpyDifferentOthers -> current)
        }
        // Both claim the same pair of others
        else if countUniqueClaimedSpies(spy1Rep, spy2Rep) == 2 && countSpiesClaimedAsSpies(spy1Rep, actual) == 0
          && countSpiesClaimedAsSpies(spy2Rep, actual) == 0 then {
          var current = getMapSafely(mapping, Intentions.TwoReportsTwoOthers)
          val nextReport = List(merlinRep, spy1Rep, spy2Rep)
          current = current :+ nextReport
          mapping = mapping + (Intentions.TwoReportsTwoOthers -> current)
        }
        // Both claim one shared person and one unique person
        else if countUniqueClaimedSpies(spy1Rep, spy2Rep) == 3 && countSpiesClaimedAsSpies(spy1Rep, actual) == 0
          && countSpiesClaimedAsSpies(spy2Rep, actual) == 0 then {
          var current = getMapSafely(mapping, Intentions.TwoReportsThreeOthers)
          val nextReport = List(merlinRep, spy1Rep, spy2Rep)
          current = current :+ nextReport
          mapping = mapping + (Intentions.TwoReportsThreeOthers -> current)
        }

        // Both claim a unique pair of non-spies
        else if countUniqueClaimedSpies(spy1Rep, spy2Rep) == 4 && countSpiesClaimedAsSpies(spy1Rep, actual) == 0
          && countSpiesClaimedAsSpies(spy2Rep, actual) == 0 then {
          var current = getMapSafely(mapping, Intentions.TwoReportsFourOthers)
          val nextReport = List(merlinRep, spy1Rep, spy2Rep)
          current = current :+ nextReport
          mapping = mapping + (Intentions.TwoReportsFourOthers -> current)
        }
        // Only one guy claims a spy; one shared non-spy claim
        else if countUniqueClaimedSpies(spy1Rep, spy2Rep) == 3 && ((countSpiesClaimedAsSpies(spy1Rep, actual) == 1
          && countSpiesClaimedAsSpies(spy2Rep, actual) == 0) || (countSpiesClaimedAsSpies(spy1Rep, actual) == 0
          && countSpiesClaimedAsSpies(spy2Rep, actual) == 1)) then {
          var current = getMapSafely(mapping, Intentions.TwoReportsOneSpyThreeTotal)
          val nextReport = List(merlinRep, spy1Rep, spy2Rep)
          current = current :+ nextReport
          mapping = mapping + (Intentions.TwoReportsOneSpyThreeTotal -> current)
        }
        // Only one guy claims a spy; no shared non-spy claims
        else if countUniqueClaimedSpies(spy1Rep, spy2Rep) == 4 && ((countSpiesClaimedAsSpies(spy1Rep, actual) == 1
          && countSpiesClaimedAsSpies(spy2Rep, actual) == 0) || (countSpiesClaimedAsSpies(spy1Rep, actual) == 0
          && countSpiesClaimedAsSpies(spy2Rep, actual) == 1)) then {
          var current = getMapSafely(mapping, Intentions.TwoReportsOneSpyFourTotal)
          val nextReport = List(merlinRep, spy1Rep, spy2Rep)
          current = current :+ nextReport
          mapping = mapping + (Intentions.TwoReportsOneSpyFourTotal -> current)
        }
      }
    }
  }
  mapping
}


/*for intention <- intentions do {
  intention match  {
    case Intentions.NoReports => // No reports - just tell me what Merlin said
      mapping = mapping + (intention -> List(possibles(0)))
    case Intentions.OneReportJustMe =>  // Merlin plus first spy, WLOG
      val product = twoListProduct(possibles(0), possibles(1))
      mapping = mapping + (intention -> product)
    case Intentions.TwoReportsJustMe => // Merlin plus both spies
      val product = threeListProduct(possibles(0), possibles(1), possibles(2))
      mapping = mapping + (intention -> product)
    case Intentions.OneReportSpyOther => {
      var pruned1: List[Report] = List()
      for rep <- possibles(1) do {
        val repArray = rep._2
        if spies.nonEmpty && (repArray(spies(0)) == 's' || repArray(spies(1)) == 's') then {
          pruned1 = pruned1:+ rep
        }
      }
      val product = twoListProduct(possibles(0), pruned1)
      mapping = mapping + (intention -> product)
    }
    case Intentions.OneReportTwoOthers => {
      var pruned1: List[Report] = List()
      for rep <- possibles(1) do {
        val repArray = rep._2
        if spies.nonEmpty && (repArray(spies(0)) != 's' || repArray(spies(1)) != 's') then {
          pruned1 = pruned1:+ rep
        }
      }
      val product = twoListProduct(possibles(0), pruned1)
      mapping = mapping + (intention -> product)
    }
    case Intentions.TwoReportsSpySameOther => {
      val product = twoListProduct(possibles(1), possibles(2))
      for repPair <- product do {
      }
    }
    case Intentions.TwoReportsSpyDifferentOthers =>
    case Intentions.TwoReportsTwoOthers =>
    case Intentions.TwoReportsThreeOthers =>
    case Intentions.TwoReportsFourOthers =>
  }
}

Map.empty
}
*/

/*
/**
 * I'm sure there's some way to do this that doesn't assume two spies, but that's too much work for right now.
 * TODO: Revisit that.
 * @param numPlayers
 * @param gameType
 * @param possibles
 * @return
 */
def mapIntentionsToReportLists(numPlayers: Int, gameType: GameType.GameType,
                               possibles: List[List[Report]]): Map[Intentions.Intentions, List[List[Report]]] = {
  var merlins: List[Int] = List()
  var spies: List[Int] = List()
  var mordreds: List[Int] = List()
  if (gameType == GameType.Resistance) {
    spies = List(1, 2)
  } else if (gameType == GameType.MerlinTwoMords) {
    merlins = List(0)
    spies = List(1, 2)
    mordreds = List(1, 2)
  } else if (gameType == GameType.MerlinOnly) {
    merlins = List(0)
    spies = List(1, 2)
  }

  // Get all the spies' possible intentions
  val intentions = getPossibleSpyIntentions(gameType)

  // Some game types have no reductions possible.
  if (gameType == GameType.Resistance) then {
    // In Resistance, just hand back the Merlin reports
    List(possibles(0))
  } else if gameType == GameType.MerlinTwoMords) then {

  } else if gameType == GameType.MerlinOnly then {
    // First, identify the actual possible intentions that can be had
    // ASSUMES TWO SPIES
    val spy1Intentions: List[List[Char]] = List.empty
  } else {
    Map.empty
  }
}
 */


