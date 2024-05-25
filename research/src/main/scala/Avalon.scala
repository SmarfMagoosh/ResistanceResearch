
object GameType extends Enumeration {
  type GameType = Value
  val Resistance, MerlinOnly, MerlinTwoMords = Value
}

/**
 * Game Modes:
 *  0 -> resistance
 *  1 -> merlin + 2 mordreds
 *  2 -> merlin
 *  3 -> merlin mordred
 *  4 -> the whole gang
 * @return
 */
def report_possibilities: (Int, GameType.GameType) => List[List[Report]] =
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
    List.empty // TODO: regular avalon
  case (_, _) => List.empty // TODO: fill in additional game modes