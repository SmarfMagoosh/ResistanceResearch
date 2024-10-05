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
object strategy {
  def apply(players: Int, history: List[Seq[Int]]): Int = {
    val d1 = all_missions(players, if players == 6 then 4 else 3)
    val d2 = d1
    val d3 = all_missions(players, 3)

    history.size match
      case 1 => d1.indexOf(history.head)
      case 2 => d1.length * (d2.indexOf(history.head) + 1) + d1.indexOf(history(1))
      case 3 => (d3.indexOf(history.head) + 1) * d2.length * d1.length
        + (d2.indexOf(history(1)) + 1) * d1.length
        + d1.indexOf(history(2))
  }

  def unapply(players: Int, stratIdx: Int): List[Seq[Int]] = {
    val d1 = all_missions(players, if players == 6 then 4 else 3)
    val d2 = d1
    val d3 = all_missions(players, 3)
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
 * @param missions A list of the missions participated in; true if the given player(s) participated in that mission
 * @return A value between 0 and Math.pow(2, # missions), excluding the later endpoint.
 */
def getEqClassIndex(missions: List[Boolean]): Int = {
  missions.foldLeft(0)((acc, b) => (acc << 1) & (if b then 1 else 0))
}