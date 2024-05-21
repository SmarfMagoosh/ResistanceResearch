/**
 * Game mode reference
 * 0 -> resistance
 * 1 -> 1 avalon
 * 2 -> mordred
 * 3 -> 2 mordreds
 * 4 -> mordred + percival + morgana
 * 5 -> morgana + percival
 *
 * Report character reference
 * m -> merlin
 * b -> bad
 * g -> good (or at least not known to be bad)
 * p -> percival
 * e -> morgana or merlin
 *
 * Without loss of generality we always assume the following about role assignment:
 * player 0 is merlin.
 * when present, we assume percival to be player 3
 * min team players will always be players 1 and 2
 *  - if morgana is present she is player 1
 *  - if one mordred is present he is player 2
 */

/**
 * Player 0 is merlin so he will always submit a truthful report about it
 * @param players the number of players in the game
 * @param game_mode the game mode
 * @return
 */
def player_0_report(players: Int, game_mode: Int): Report = {
  (players, game_mode) match
    case (5, 0) => Report(0, Array('n', 'n', 'n', 'n', 'n'))
    case (6, 0) => Report(0, Array('n', 'n', 'n', 'n', 'n', 'n'))
    case (5, 1) => Report(0, Array('m', 'b', 'b', 'g', 'g'))
    case (6, 1) => Report(0, Array('m', 'b', 'b', 'g', 'g', 'g'))
    case (5, 2) => Report(0, Array('m', 'b', 'g', 'g', 'g'))
    case (6, 2) => Report(0, Array('m', 'b', 'g', 'g', 'g', 'g'))
    case (5, 3) => Report(0, Array('m', 'g', 'g', 'g', 'g'))
    case (6, 3) => Report(0, Array('m', 'g', 'g', 'g', 'g', 'g'))
    case (5, 4) => Report(0, Array('m', 'b', 'g', 'g', 'g'))
    case (6, 4) => Report(0, Array('m', 'b', 'g', 'g', 'g', 'g'))
    case (5, 5) => Report(0, Array('m', 'b', 'b', 'g', 'g'))
    case (6, 5) => Report(0, Array('m', 'b', 'b', 'g', 'g', 'g'))
}

def player_1_report(players: Int, game_mode: Int): List[(Double, Report)] = {
  // TODO: implement this method
  /**
   * It should consider all possible reports to reports to submit for each game mode paired
   * with how likely it is to submit that report.
   * i.e for avalon there should be a list of 2 reports one of which reports merlin
   * and is paired with a likelihood of 1/3 and one which reports a servant of arthur with a
   * likelihood of 2/3
   */
  List.empty
}

def player_2_report(players: Int, game_mode: Int): List[(Double, Report)] = {
  // TODO: implement this method
  /**
   * It should consider all possible reports to reports to submit for each game mode paired
   * with how likely it is to submit that report.
   * i.e for avalon there should be a list of 2 reports one of which reports merlin
   * and is paired with a likelihood of 1/3 and one which reports a servant of arthur with a
   * likelihood of 2/3
   */
  List.empty
}

/**
 * Player 3 is always percival when the role is present and will always submit a truthful report
 * on what he knows.
 * END: I think there is no scenario in which someone would pretend to be percival... it feels
 * too risky?
 * @param players the number of players in the game
 * @param game_mode the game made
 * @return
 */
def player_3_report(players: Int, game_mode: Int): Report = {
  (players, game_mode) match
    case (5, 4) => Report(3, Array('e', 'e', 'g', 'p', 'g'))
    case (6, 4) => Report(3, Array('e', 'e', 'g', 'p', 'g', 'g'))
    case (5, 5) => Report(3, Array('e', 'e', 'g', 'p', 'g'))
    case (6, 5) => Report(3, Array('e', 'e', 'g', 'p', 'g', 'g'))
}