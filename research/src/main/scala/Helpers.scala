/**
 * Generalizing function to report the size of teams for different player counts.
 */
def getTeamSizes: Int => List[Int] =
  case 5 => 3 :: 3 :: 3 :: Nil
  case 6 => 3 :: 4 :: 3 :: 4 :: Nil

/**
 * Generalizing function to report the number of rounds for different player counts
 */
private def getNumMissions: Int => Int =
  case 5 => 3
  case 6 => 4