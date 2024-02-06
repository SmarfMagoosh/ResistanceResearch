val (t, f): (Boolean, Boolean) = (true, false)

val team_configs: Map[Int, Seq[Boolean]] = Map(
  1  -> Seq(f, f, t, t, t), 2  -> Seq(f, t, f, t, t),
  3  -> Seq(f, t, t, f, t), 4  -> Seq(f, t, t, t, f),
  5  -> Seq(t, f, f, t, t), 6  -> Seq(t, f, t, f, t),
  7  -> Seq(t, f, t, t, f), 8  -> Seq(t, t, f, f, t),
  9  -> Seq(t, t, f, t, f), 10 -> Seq(t, t, t, f, f))

val mission_configs: Map[Int, (Int, Int, Int)] = Map(
  1  -> (1, 2, 3), 2  -> (1, 2, 4),
  3  -> (1, 2, 5), 4  -> (1, 3, 4),
  5  -> (1, 3, 4), 6  -> (1, 4, 5),
  7  -> (2, 3, 4), 8  -> (2, 3, 5),
  9  -> (2, 4, 5), 10 -> (3, 4, 5))

def mission_succeeds(team_config: Int)(action: Int): Boolean = {
  val team: Seq[Boolean] = team_configs(team_config)
  val (p1, p2, p3): (Int, Int, Int) = mission_configs(action)
  team(p1) && team(p2) && team(p3)
}

def game_outcome(team_config: Int)(actions: Seq[Int]): Boolean = {
  val setup: Int => Boolean = mission_succeeds(team_config)
  actions.map(setup).reduce(_ || _)
}

def tuplify(seq: Seq[Int]): (Int, Int, Int) = (seq.head, seq(1), seq(2))

def all_games: IndexedSeq[(Int, Seq[Int])] = for {
  team_config <- 1 until 10
  mission1 <- 1 until 10
  mission2 <- 1 until 10
  mission3 <- 1 until 10
} yield (team_config, Seq(mission1, mission2, mission3))

lazy val all_game_outcomes: Map[(Int, (Int, Int, Int)), Boolean] =
  all_games.map((tuple: (Int, Seq[Int])) => {
    ((tuple._1, tuplify(tuple._2)), game_outcome(tuple._1)(tuple._2))
  }).toMap

@main def main(): Unit = {
  println("hello world")
}
