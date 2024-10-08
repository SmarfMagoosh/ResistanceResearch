import Roles._

/**
 * An object used to represent a report submitted by the agent to the strategist.
 * only origin is private knowledge known only by the mediator,
 * roleOfOrigin and assertions are commonKnowledge known by every player at the table
 *
 * without loss of generality, we assume the following about role assignment
 * Merlin, when present, will be player 0
 * The spies will always be players 1 and 2.
 * If only 1 mordred is present, he will be player 1
 * Percival, when present will be player 3
 *
 * @param origin       The agent submitting the report
 * @param roleOfOrigin tells us whether someone pretending to be Merlin or Percival is submitting a report
 * @param assertions   An array of roles which represent the agent's claims about other players
 */
case class Report(origin: Int, roleOfOrigin: Role, assertions: Vector[Int]) {
  override def toString: String = s"Player $origin reporting as ${roleOfOrigin.toString} makes a claim about ${assertions.toString}"
}