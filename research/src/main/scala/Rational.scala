import scala.annotation.{tailrec, targetName}

/**
 *
 * @param num
 * @param den
 */
case class Rational(num: Int, den: Int) extends Ordered[Rational] {
  require(den != 0)

  @targetName("Negation")
  def unary_- : Rational = Rational(-num, den)

  @targetName("Addition")
  def +(o: Rational): Rational = Rational((num * o.den) + (o.num * den), den * o.den).reduce

  @targetName("Subtraction")
  def -(o: Rational): Rational = this + -o

  @targetName("Multiplication")
  def *(o: Rational): Rational = Rational(num * o.num, den * o.den).reduce

  @targetName("Division")
  def /(o: Rational): Rational = this * o.reciprocal

  @targetName("Addition")
  def +(o: Int): Rational = Rational(num + (o * den), den).reduce

  @targetName("Subtraction")
  def -(o: Int): Rational = this + -o

  @targetName("Multiplication")
  def *(o: Int): Rational = Rational(num * o, den).reduce

  @targetName("Division")
  def /(o: Int): Rational = Rational(num, den * o).reduce

  private def gcd: Int = {
    @tailrec def helper(a: Int, b: Int): Int = if b == 0 then a else helper(b, a % b)
    helper(num, den)
  }

  private def reduce: Rational = this match
    case Rational(0, _) => Rational(0, 1)
    case Rational(x, y) => ((i: Int) => Rational(num / i, den / i))(gcd)

  private def reciprocal: Rational = this match
    case Rational(0, _) => throw new ArithmeticException("Rational number with 0 in the denominator")
    case Rational(num, den) => Rational(den, num)

  def eval: Double = num.toDouble / den

  // for ordering
  override def compare(that: Rational): Int = (num * that.den) compare (den * that.num)
}

object / {
  def unapply(ratio: Rational): (Int, Int) = (ratio.num, ratio.den)
}