import Roles.*

/**
 * An object used to represent a report submitted by the agent to the strategist.
 *
 * @param origin The agent submitting the report TODO: maybe remove this since its not "public knowledge"
 * @param assertions An array of roles which represent the agent's claims about other players
 */
case class Report(origin: Int, assertions: Array[Role]) {
  override def toString: String = s"Report($origin, ${assertions.map(_.toChar).mkString("[", " ", "]")})"
}