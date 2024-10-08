//import GameTypes.*
//import Roles.*
//import Intentions.*
//
///**
// * M = Merlin
// * N = nothing
// * E = spy
// *
// * @return
// */
//def report_possibilities: (Int, GameType) => List[List[Report]] = {
//  case (_, Resistance) => List.empty // for resistance
//  case (5, TwoMordreds) =>
//    List(
//      Report(2, Array(N, N, M, N, N)),
//      Report(1, Array(N, M, N, N, N)),
//      Report(0, Array(M, N, N, N, N))
//    ).tails.filter(_.nonEmpty).toList
//  case (6, TwoMordreds) =>
//    List(
//      Report(2, Array(N, N, M, N, N, N)),
//      Report(1, Array(N, M, N, N, N, N)),
//      Report(0, Array(M, N, N, N, N, N))
//    ).tails.filter(_.nonEmpty).toList
//  case (5, Avalon) =>
//    val merlinRep = List(Report(0, Array(M, U, U, N, N)))
//    val mord1Reps = List(
//      Report(1, Array(E, M, E, N, N, N)),
//      Report(1, Array(E, M, N, E, N, N)),
//      Report(1, Array(N, M, E, E, N, N)),
//      Report(1, Array(N, M, N, E, E, N)),
//    )
//    val mord2Reps = List(
//      Report(2, Array(E, E, M, N, N, N)),
//      Report(2, Array(E, N, M, E, N, N)),
//      Report(2, Array(N, E, M, E, N, N)),
//      Report(2, Array(N, N, M, E, E, N)),
//    )
//    var fullPossibleReports = List(List(Report))
//    for i <- merlinRep do {
//      for j <- mord1Reps do {
//        for k <- mord2Reps do {
//          //fullPossibleReports = fullPossibleReports :: List(i, j, k)
//          // Look, this is just wrongheaded. Mord1 and Mord2 can coordinate strategies. What we need is the idea
//          // of what they're *trying* to do, and how likely each possible result is given that. In other words, we
//          // should start with "Mord1 tries to report one unknown plus throw Mord2 under the bus, and Mord 2 does
//          // the same for a *different* rando, and how likely is it either of them end up with Merlin?"
//          // Not sure how to do that yet, but I think it starts by specifying intentions (which are guaranteed)
//          // and then looking at possible results of those intentions. Again, back to "strategy, and likely outcomes" -
//          // this has, basically, a chance node component.
//        }
//      }
//    }
//    List.empty
//  case (_, _) => List.empty // TODO: fill in additional game modes
//}
//
//def generateAllPossibleReportLists(numPlayers: Int, gameType: GameType): List[List[Report]] = {
//  // WLOG assume merlin is 0, and spies are 1 & 2... only works for 5 and 6 players
//  val spies: List[Int] = List(1, 2)
//  val mordreds: List[Int] = gameType match
//    case TwoMordreds => List(1, 2)
//    case _ => List.empty
//
//  // Get Merlin reports
//  val merlinReport: Report = Report(0, Array.tabulate(numPlayers) {
//    case 0 => Roles.Merlin
//    case x if spies.contains(x) && !mordreds.contains(x) => Roles.Minion
//    case _ => Roles.Null
//  })
//
//  val spyReports: List[List[Report]] = spies.map(spy => {
//    //Report an appropriate number of spies, excluding ourselves.
//    val possibleIndices = (0 until numPlayers).filter(_ != spy)
//    val possibleSpyClaims = possibleIndices.combinations(spies.length - mordreds.length).toList
//
//    possibleSpyClaims.map(spyClaim => {
//      val assertions = Array.tabulate(numPlayers) {
//        case x if spyClaim contains x => Roles.Minion
//        case x if x == spy => Roles.Merlin
//        case _ => Roles.Null
//      }
//      Report(spy, assertions)
//    })
//  })
//
//  gameType match
//    case Resistance => List()
//    case _ => List(merlinReport) :: spyReports
//  //  // TODO: figure out what on earth this does
//  //  var spyReports: List[List[Report]] = List()
//  //  // Keeps track of which list of spy reports we're currently adding to
//  //  var index = -1
//  //  for i <- spies do {
//  //    index += 1
//  //    // All reports by one spy in one list.
//  //    spyReports = spyReports :+ List()
//  //    if gameType != GameType.Resistance then {
//  //      var assertions: Array[Roles.Value] = Array.fill(numPlayers)(N)
//  //
//  //      // Report ourselves as Merlin
//  //      assertions(i) = M
//  //
//  //      //Report an appropriate number of spies, excluding ourselves.
//  //      val possibleIndices = (0 until numPlayers).filter(z => z != i)
//  //      val possibleSpyClaims = possibleIndices.combinations(nonMordSpies)
//  //
//  //      for j: IndexedSeq[Int] <- possibleSpyClaims do {
//  //        for k: Int <- j do {
//  //          assertions = assertions.updated(k, E)
//  //        }
//  //        val rep = Report(i, assertions)
//  //        spyReports = spyReports.updated(index, spyReports(index) :+ rep)
//  //        // Remove the particular people we accused as spies
//  //        for k: Int <- j do {
//  //          assertions = assertions.updated(k, N)
//  //        }
//  //      }
//  //    }
//  //  }
//  //
//  //  // So, final reports: a list of all possible Merlin reports, then a list of all possible spy 1 reports,
//  //  // ... then a list of all possible spy 2 reports, then etc.
//  //  merlinReports :: spyReports
//}
//
///**
// * ASSUMES TWO SPIES
// * TODO: Revisit that
// */
//def getPossibleSpyIntentions(gameType: GameType): List[Intentions.Value] = {
//  gameType match
//    case Resistance => List(
//      Intentions.NoReports
//    )
//    case TwoMordreds => List(
//      Intentions.NoReports,
//      Intentions.OneReportJustMe,
//      Intentions.TwoReportsJustMe
//    )
//    case Avalon => List(
//      Intentions.NoReports,
//      Intentions.OneReportSpyOther,
//      Intentions.OneReportTwoOthers,
//      Intentions.TwoReportsSpySameOther,
//      Intentions.TwoReportsSpyDifferentOthers,
//      Intentions.TwoReportsTwoOthers,
//      Intentions.TwoReportsThreeOthers,
//      Intentions.TwoReportsFourOthers,
//      Intentions.TwoReportsOneSpyThreeTotal,
//      Intentions.TwoReportsOneSpyFourTotal
//    )
//    case _ => List.empty
//}
//
//def twoListProduct(one: List[Report], two: List[Report]): List[List[Report]] = {
//  one.flatMap(x => two.map(y => List(x, y)))
//}
//
//def threeListProduct(one: List[Report], two: List[Report], three: List[Report]): List[List[Report]] = {
//  one.flatMap(x => two.flatMap(y => three.map(z => List(x, y, z))))
//}
//
//def countMerlinsClaimedAsSpies(rep: Report, actual: Array[Roles.Value]): Int = {
//  (rep.assertions zip actual).count(_ == Roles.Minion && _ == Roles.Merlin)
//}
//
//def countSpiesClaimedAsSpies(rep: Report, actual: Array[Roles.Value]): Int = {
//  (rep.assertions zip actual).count((r, a) => r == Roles.Minion && (a == Roles.Minion || a == Roles.Mordred))
//}
//
//def countNonesClaimedAsSpies(rep: Report, actual: Array[Roles.Value]): Int = {
//  (rep.assertions zip actual).count(_ == Roles.Minion && _ == Roles.Null)
//}
//
//def countNonSpiesClaimedAsSpies(rep: Report, actual: Array[Roles.Value]): Int = {
//  (rep.assertions zip actual).count((r, a) => r == Roles.Minion && a != Roles.Minion && a != Roles.Mordred)
//}
//
//def countTotalClaimedSpies(rep: Report): Int = {
//  rep.assertions.count(_ == Roles.Minion)
//}
//
//def countUniqueClaimedSpies(rep1: Report, rep2: Report): Int = {
//  (rep1.assertions zip rep2.assertions).count(_ == Roles.Minion || _ == Roles.Minion)
//}
//
//def getMapSafely(mapping: Map[Intentions.Value, List[List[Report]]], key: Intentions.Value):
//List[List[Report]] = {
//  if mapping contains key then mapping(key) else List()
//}
//
///**
// * THIS ONE MIGHT ACTUALLY WORK. Given all the reports a spy could conceivably give, it divides them into a mapping
// * of spy intentions to the reports those intentions could in theory produce. I think we can use this to then figure
// * out, for a given intention, the odds of different kinds of outcomes. It needs some additional preprocessing
// * to eliminate, e.g., duplicate results with the spies reversed. Then we can count, for a given intention,
// * how many outcomes produce a given circumstance ('whoops, claimed merlin') and prep a report for each of those.
// * Bonus: I *think* this is straightforwardly extensible for Percival etc.
// *
// * @param numPlayers The number of players in the game.
// * @param gameType   The type of game, e.g., pure Resistance, Merlin only, etc.
// * @param intentions The list of all intentions the spies could have for the given game type
// * @param possibles  The full set of possible reports the spies could produce
// * @return A mapping from intentions consistent with the given game to possible reports produced by that intention
// */
//def mapSpyIntentionsToOutcomeCategories(numPlayers: Int, gameType: GameType,
//                                        intentions: List[Intentions.Value],
//                                        possibles: List[List[Report]]):
//Map[Intentions.Value, List[List[Report]]] = {
//  var merlins: List[Int] = List()
//  var spies: List[Int] = List()
//  var mordreds: List[Int] = List()
//  // The true array of identities
//  val actual: Array[Roles.Value] = Array.tabulate(numPlayers)(indexToRole(gameType))
//
//  // Pure merlin reports go into "no reports"
//  val merlinLists: List[List[Report]] = possibles.head.map(List(_))
//  var mapping: Map[Intentions.Value, List[List[Report]]] = Map(Intentions.NoReports -> merlinLists)
//
//  for merlinRep <- possibles(0) do {
//    for spy1Rep <- possibles(1) do {
//      // Single spy reports, based on contents of that report
//      // Nothing reported except your own identity
//      if countTotalClaimedSpies(spy1Rep) == 0 then {
//        val test = mapping.getOrElse(Intentions.OneReportJustMe, List.empty)
//        var current = getMapSafely(mapping, Intentions.OneReportJustMe)
//        val nextReport = List(merlinRep, spy1Rep)
//        current = current :+ nextReport
//        mapping = mapping + (Intentions.OneReportJustMe -> current)
//      }
//      // Reported an actual spy + somebody
//      else if countSpiesClaimedAsSpies(spy1Rep, actual) == 1 then {
//        var current = getMapSafely(mapping, Intentions.OneReportSpyOther)
//        val nextReport = List(merlinRep, spy1Rep)
//        current = current :+ nextReport
//        mapping = mapping + (Intentions.OneReportSpyOther -> current)
//      }
//      // Reported two non-spies
//      else if countSpiesClaimedAsSpies(spy1Rep, actual) == 0 then {
//        var current = getMapSafely(mapping, Intentions.OneReportTwoOthers)
//        val nextReport = List(merlinRep, spy1Rep)
//        current = current :+ nextReport
//        mapping = mapping + (Intentions.OneReportTwoOthers -> current)
//      }
//
//      // Both spies reporting
//      for spy2Rep <- possibles(2) do {
//        // Nothing reported except your own identity
//        if countTotalClaimedSpies(spy1Rep) == 0 && countTotalClaimedSpies(spy2Rep) == 0 then {
//          var current = getMapSafely(mapping, Intentions.TwoReportsJustMe)
//          val nextReport = List(merlinRep, spy1Rep, spy2Rep)
//          current = current :+ nextReport
//          mapping = mapping + (Intentions.TwoReportsJustMe -> current)
//        }
//        // Both claim a spy and one shared other
//        else if countUniqueClaimedSpies(spy1Rep, spy2Rep) == 3 && countSpiesClaimedAsSpies(spy1Rep, actual) == 1
//          && countSpiesClaimedAsSpies(spy2Rep, actual) == 1 then {
//          var current = getMapSafely(mapping, Intentions.TwoReportsSpySameOther)
//          val nextReport = List(merlinRep, spy1Rep, spy2Rep)
//          current = current :+ nextReport
//          mapping = mapping + (Intentions.TwoReportsSpySameOther -> current)
//        }
//        // Both claim a spy and unique others
//        else if countUniqueClaimedSpies(spy1Rep, spy2Rep) == 4 && countSpiesClaimedAsSpies(spy1Rep, actual) == 1
//          && countSpiesClaimedAsSpies(spy2Rep, actual) == 1 then {
//          var current = getMapSafely(mapping, Intentions.TwoReportsSpyDifferentOthers)
//          val nextReport = List(merlinRep, spy1Rep, spy2Rep)
//          current = current :+ nextReport
//          mapping = mapping + (Intentions.TwoReportsSpyDifferentOthers -> current)
//        }
//        // Both claim the same pair of others
//        else if countUniqueClaimedSpies(spy1Rep, spy2Rep) == 2 && countSpiesClaimedAsSpies(spy1Rep, actual) == 0
//          && countSpiesClaimedAsSpies(spy2Rep, actual) == 0 then {
//          var current = getMapSafely(mapping, Intentions.TwoReportsTwoOthers)
//          val nextReport = List(merlinRep, spy1Rep, spy2Rep)
//          current = current :+ nextReport
//          mapping = mapping + (Intentions.TwoReportsTwoOthers -> current)
//        }
//        // Both claim one shared person and one unique person
//        else if countUniqueClaimedSpies(spy1Rep, spy2Rep) == 3 && countSpiesClaimedAsSpies(spy1Rep, actual) == 0
//          && countSpiesClaimedAsSpies(spy2Rep, actual) == 0 then {
//          var current = getMapSafely(mapping, Intentions.TwoReportsThreeOthers)
//          val nextReport = List(merlinRep, spy1Rep, spy2Rep)
//          current = current :+ nextReport
//          mapping = mapping + (Intentions.TwoReportsThreeOthers -> current)
//        }
//
//        // Both claim a unique pair of non-spies
//        else if countUniqueClaimedSpies(spy1Rep, spy2Rep) == 4 && countSpiesClaimedAsSpies(spy1Rep, actual) == 0
//          && countSpiesClaimedAsSpies(spy2Rep, actual) == 0 then {
//          var current = getMapSafely(mapping, Intentions.TwoReportsFourOthers)
//          val nextReport = List(merlinRep, spy1Rep, spy2Rep)
//          current = current :+ nextReport
//          mapping = mapping + (Intentions.TwoReportsFourOthers -> current)
//        }
//        // Only one guy claims a spy; one shared non-spy claim
//        else if countUniqueClaimedSpies(spy1Rep, spy2Rep) == 3 && ((countSpiesClaimedAsSpies(spy1Rep, actual) == 1
//          && countSpiesClaimedAsSpies(spy2Rep, actual) == 0) || (countSpiesClaimedAsSpies(spy1Rep, actual) == 0
//          && countSpiesClaimedAsSpies(spy2Rep, actual) == 1)) then {
//          var current = getMapSafely(mapping, Intentions.TwoReportsOneSpyThreeTotal)
//          val nextReport = List(merlinRep, spy1Rep, spy2Rep)
//          current = current :+ nextReport
//          mapping = mapping + (Intentions.TwoReportsOneSpyThreeTotal -> current)
//        }
//        // Only one guy claims a spy; no shared non-spy claims
//        else if countUniqueClaimedSpies(spy1Rep, spy2Rep) == 4 && ((countSpiesClaimedAsSpies(spy1Rep, actual) == 1
//          && countSpiesClaimedAsSpies(spy2Rep, actual) == 0) || (countSpiesClaimedAsSpies(spy1Rep, actual) == 0
//          && countSpiesClaimedAsSpies(spy2Rep, actual) == 1)) then {
//          var current = getMapSafely(mapping, Intentions.TwoReportsOneSpyFourTotal)
//          val nextReport = List(merlinRep, spy1Rep, spy2Rep)
//          current = current :+ nextReport
//          mapping = mapping + (Intentions.TwoReportsOneSpyFourTotal -> current)
//        }
//        else {
//          println("!UNCATEGORIZED INTENTION!")
//        }
//      }
//    }
//  }
//  mapping
//}
//
//
//def reduceSpyIntentionsMap(map: Map[Intentions.Value, List[List[Report]]]):
//Map[Intentions.Value, List[(Int, List[Report])]] = {
//  var output: Map[Intentions.Value, List[(Int, List[Report])]] = Map()
//  for key <- map.keys do {
//    val possibles = map(key)
//    var reduced: List[(Int, List[Report])] = List()
//    var merlinCounts: List[Int] = List()
//    for possible <- possibles do {
//      var merlinCount = 0
//      // How many times is Merlin claimed?
//      if (possible.length > 1) then merlinCount = merlinCount + (if possible(1)._2(0) == Roles.Minion then 1 else 0)
//      if (possible.length > 2) then merlinCount = merlinCount + (if possible(2)._2(0) == Roles.Minion then 1 else 0)
//      // Either add this as a new exemplar for an unseen Merlin count...
//      if !merlinCounts.contains(merlinCount) then {
//        merlinCounts = merlinCounts :+ merlinCount
//        reduced = reduced :+ (1, possible)
//      } else { // ... or increase the number of ways to get a preexistent Merlin count by 1.
//        val index = merlinCounts.indexOf(merlinCount)
//        val current = reduced(index)
//        reduced = reduced.updated(index, (current._1 + 1, current._2))
//      }
//    }
//    output = output + (key -> reduced)
//  }
//  output
//}
