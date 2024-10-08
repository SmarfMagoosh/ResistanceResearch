/**
 * Enumeration object of roles from the Resistance and Avalon
 */
object Roles extends Enumeration {
  type Role = Value
  // the Null Role is for a report that doesn't have anything to say about a particular person
  val Merlin, Percival, Servant, Minion, Mordred, Morgana, Null = Value

  // One character constants for code reduction
  val N: Role = Roles.Null
  val M: Role = Roles.Merlin
  val P: Role = Roles.Percival
  val S: Role = Roles.Servant
  val E: Role = Roles.Minion
  val U: Role = Roles.Mordred
  val I: Role = Roles.Morgana
}

/**
 * Enumeration object of all the different role combinations for resistance and avalon
 */
object GameTypes extends Enumeration {
  type GameType = Value
  val Resistance, Avalon, Mordred, TwoMordreds, AllRoles = Value
}

/**
 * Enumeration object of all
 */
object Intentions extends Enumeration {
  type Intention = Value
  val NoReports, // Neither spy reports
  OneReportJustMe, // One spy reports, no blame
  TwoReportsJustMe, // Both spies reports, no blame
  OneReportSpyOther, // One spy reports, blame other spy + someone else
  OneReportTwoOthers, // One spy reports, blame 2 non-spies
  TwoReportsSpySameOther, // Both spies report, blame other spy + shared other person
  TwoReportsSpyDifferentOthers, // Both spies report, blame the other spy and a different other
  TwoReportsOneSpyThreeTotal, // Both spies report, one blames spy + other, the other blames to people w/ overlap
  TwoReportsOneSpyFourTotal, // both spies report, one blames spy + other, the other blames two non-spies w/o overlap
  TwoReportsTwoOthers, // Both spies report, blame two non-spies
  TwoReportsThreeOthers, // Both spies report, one common other, and one distinct other.
  TwoReportsFourOthers = Value // Both spies report, and distinct pairs of non-spies. (Requires 6+ players)
}

object ReportTypes extends Enumeration {
  type ReportType = Value
  val Merlin, Percival = Value
}

extension (gameType: GameTypes.GameType)
  def indexToRole(index: Int): Roles.Role = {
    index match
      case 0 => gameType match
        case GameTypes.Resistance => Roles.Servant
        case _ => Roles.Merlin
      case 1 => gameType match
        case GameTypes.Resistance | GameTypes.Avalon => Roles.Minion
        case _ => Roles.Mordred
      case 2 => gameType match
        case GameTypes.Resistance | GameTypes.Avalon | GameTypes.Mordred => Roles.Minion
        case GameTypes.TwoMordreds => Roles.Mordred
        case _ => Roles.Morgana
      case 3 => gameType match
        case GameTypes.AllRoles => Roles.Percival
        case _ => Roles.Servant
      case _ => Roles.Servant
  }

extension (role: Roles.Role)
  def toChar: Char = {
    role match
      case Roles.Null => 'N'
      case Roles.Merlin => 'M'
      case Roles.Percival => 'P'
      case Roles.Servant => 'S'
      case Roles.Minion => 'E' // 'E' for Evil
      case Roles.Mordred => 'U' // 'U' for Unknown
      case Roles.Morgana => 'I' // 'I' for Impostor
  }
