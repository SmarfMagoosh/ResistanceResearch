/**
 * represents a rational number as a numerator/denominator pair to prevent floating point
 * round off errors.
 * These extension methods allow arithmetic operations to be performed on the tuple
 */
extension (rational: (Int, Int))
  // rational to rational operations
  def *(o: (Int, Int)): (Int, Int) = (rational._1 * o._1, rational._2 * o._2).reduce

  def /(o: (Int, Int)): (Int, Int) = rational * o.swap

  def +(o: (Int, Int)): (Int, Int) = ((rational._1 * o._2) + (rational._2 * o._1), rational._2 * o._2).reduce

  def -(o: (Int, Int)): (Int, Int) = rational + (-o._1, o._2)

  // rational to integer operations
  def *(o: Int): (Int, Int) = (rational._1 * o, rational._2).reduce

  def /(o: Int): (Int, Int) = (rational._1, rational._2 * o).reduce

  def + (o: Int): (Int, Int) = (rational._1 + (o * rational._2), rational._2).reduce

  def - (o: Int): (Int, Int) = rational + (-1 * o)

  // finds greatest common divisor of the numerator and denominator for simplification purposes
  def gcd: Int = {
    if rational._2 == 0 then rational._1 else (rational._2, rational._1 % rational._2).gcd
  }

  // fraction simplification algorithm
  def reduce: (Int, Int) = {
    rational match
      case (0, _) => (0, 1) // rational number is 0, denominator does not matter
      case (_, 0) => rational // denominator is 0, will be handled in eval function
      case _ => (rational._1 / rational.gcd, rational._2 / rational.gcd) // divide top and bottom by gcd
  }

  // evaluate the rational number
  def eval: Double = rational._1.toDouble / rational._2