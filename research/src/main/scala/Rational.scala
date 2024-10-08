import scala.annotation.{tailrec, targetName}

/**
 * A value class for rational numbers
 * under the hood each rational number is a 64 bit long.
 * the least significant 32 bits represent a 32 bit integer as the numerator
 * the most significant 32 bits represent a 32 bit integer as the denominator
 *
 * @param ratio a 64 bit long representing the rational number.
 */
case class Rational(ratio: Long)
  extends AnyVal with Ordered[Rational] {
  @targetName("Negation")
  def unary_- : Rational = Rational.RationalNumeric.negate(this)

  @targetName("Addition")
  def +(o: Rational): Rational = Rational.RationalNumeric.plus(this, o)

  @targetName("Subtraction")
  def -(o: Rational): Rational = Rational.RationalNumeric.minus(this, o)

  @targetName("Multiplication")
  def *(o: Rational): Rational = Rational.RationalNumeric.times(this, o)

  @targetName("Division")
  def /(o: Rational): Rational = Rational.RationalNumeric.times(this, o.reciprocal)

  @targetName("Addition")
  def +(o: Int): Rational = this + Rational.RationalNumeric.fromInt(o)

  @targetName("Subtraction")
  def -(o: Int): Rational = this - Rational.RationalNumeric.fromInt(o)

  @targetName("Multiplication")
  def *(o: Int): Rational = this * Rational.RationalNumeric.fromInt(o)

  @targetName("Division")
  def /(o: Int): Rational = this / Rational.RationalNumeric.fromInt(o)

  def reciprocal: Rational = this match
    case 0 over _ => throw new ArithmeticException("Rational number with 0 in the denominator")
    case num over den => Rational(den, num)

  def inverse: Rational = this match
    case x over y if x == y => Rational.zero
    case 0 over x => Rational.one
    case x over y => Rational(x - y, x)

  // for ordering
  override def compare(that: Rational): Int = Rational.RationalNumeric.compare(this, that)
}

/**
 * companion object use for construction and also implementation of Numeric
 */
object Rational {
  def apply(num: Int, den: Int): Rational =
    (num, den) match
      case (_, 0) => throw new ArithmeticException("Rational number with 0 in the denominator")
      case (x, y) => new Rational(x & (y << 32))

  val zero: Rational = apply(0, 1)

  val one: Rational = apply(1, 1)

  implicit object RationalNumeric extends Numeric[Rational] {
    private def den(x: Rational): Int = ((0xFFFFFFFF00000000L ^ x.ratio) >>> 32).toInt

    private def num(x: Rational): Int = (0xFFFFFFFF & x.ratio).toInt

    private def gcd(x: Rational): Int = {
      @tailrec def helper(x: Int, y: Int): Int = (x, y) match
        case (x, 0) => x
        case (x, y) => helper(y, x % y)

      helper(num(x), den(x))
    }

    private def reduce(x: Rational): Rational = x match
      case 0 over _ => Rational(0, 1)
      case a over b => {
        val denom = gcd(x)
        Rational(a / denom, b / denom)
      }

    override def plus(x: Rational, y: Rational): Rational = {
      reduce(Rational((num(x) * den(y)) + (num(y) * den(x)), den(x) * den(y)))
    }

    override def minus(x: Rational, y: Rational): Rational = plus(x, negate(y))

    override def times(x: Rational, y: Rational): Rational = reduce(Rational(num(x) * num(y), den(x) * den(y)))

    override def negate(x: Rational): Rational = Rational(-num(x), den(x))

    override def fromInt(x: Int): Rational = Rational(x, 1)

    override def parseString(str: String): Option[Rational] = str match
      case x over y => Some(reduce(Rational(x, y)))
      case _ => None

    override def toInt(x: Rational): Int = num(x) / den(x)

    override def toLong(x: Rational): Long = toInt(x).toLong

    override def toFloat(x: Rational): Float = (num(x).doubleValue() / den(x)).toFloat

    override def toDouble(x: Rational): Double = num(x).doubleValue() / den(x)

    override def compare(x: Rational, y: Rational): Int = (num(x) * den(y)) compare (den(x) * num(y))
  }
}

/**
 * companion object convenient for pattern matching
 */
object over {
  def unapply(rational: Rational): Option[(Int, Int)] = {
    Some(((0xFFFFFFFF & rational.ratio).toInt, (0xFFFFFFFF00000000L & rational.ratio).toInt))
  }

  def unapply(string: String): Option[(Int, Int)] = {
    val split = string.split("/")
    if split.length != 2 || split.exists(_.contains("/")) then None else {
      Some(split(0).toInt, split(1).toInt)
    }
  }
}

extension (i: Int)
  @targetName("subtraction")
  def -(r: Rational): Rational = r - i

  @targetName("addition")
  def +(r: Rational): Rational = r + i

  @targetName("multiplication")
  def *(r: Rational): Rational = r * i

  @targetName("division")
  def /(r: Rational): Rational = i * r.reciprocal
