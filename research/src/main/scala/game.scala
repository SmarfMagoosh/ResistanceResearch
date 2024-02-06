/**
 * Contains all 10 possible team configurations of 5 players with 2 spies.
 * The Sequence: true, false, true, true, false is interpreted as players 2 and 5 are the spies.
 */
val team_configs: Seq[Seq[Boolean]] = Seq(
  Seq(false, false, true, true, true),
  Seq(false, true, false, true, true),
  Seq(false, true, true, false, true),
  Seq(false, true, true, true, false),
  Seq(true, false, false, true, true),
  Seq(true, false, true, false, true),
  Seq(true, false, true, true, false),
  Seq(true, true, false, false, true),
  Seq(true, true, false, true, false),
  Seq(true, true, true, false, false)
)

/**
 * Contains all 10 possible 3-man mission combinations
 */
val mission_configs: Seq[(Int, Int, Int)] = Seq(
  (0, 1, 2),
  (0, 1, 3),
  (0, 1, 4),
  (0, 2, 3),
  (0, 2, 4),
  (0, 3, 4),
  (1, 2, 3),
  (1, 2, 4),
  (1, 3, 4),
  (2, 3, 4)
)

/**
 * a list of functions that map actions to outcomes given a player configuration
 * games(0)(3) is true if players roles in the 0th configuration would succeed when
 * selecting mission configuration #3
 */
val games: IndexedSeq[Int => Boolean] = for i <- 0 to 9 yield { (action: Int) => {
    // arrangement of roles
    val team: Seq[Boolean] = team_configs(i)

    // players on the mission
    val (p1, p2, p3): (Int, Int, Int) = mission_configs(action)

    // role of each player anded together to determine success
    team(p1) && team(p2) && team(p3)
  }
}

/**
 * Allows an entire game to be played with 3 missions in a given role configuration
 * @param team_config integer from 0-9 denoting the assignment of roles to the player
 * @param m1 the action number for the first mission
 * @param m2 the action number for the second mission
 * @param m3 the action number for the third mission
 * @return true if one of the missions succeeded and false otherwise
 */
def rebels_win(team_config: Int)(m1: Int)(m2: Int)(m3: Int): Boolean = {
  // function to map action to outcome
  val g: Int => Boolean = games(team_config)

  // outcome of all 3 missions ored together
  g(m1) || g(m2) || g(m3)
}

@main def main(): Unit = {
  /**
   * result of all possible games. If you left pad the index with 0s, you can interpret the result
   * digit-wise from left to right, i.e if index 1234 is true, then players in configuration 1 will win
   * if they go on mission configurations 2, 3, and 4. if 12 is false, then players in the 0th
   * configuration will lose if they go on missions 0, 1, and 2.
   */
  val possibilities: IndexedSeq[Boolean] =
    for config <- 0 to 9; m1 <- 0 to 9; m2 <- 0 to 9; m3 <- 0 to 9 yield {
      rebels_win(config)(m1)(m2)(m3)
    }
}
