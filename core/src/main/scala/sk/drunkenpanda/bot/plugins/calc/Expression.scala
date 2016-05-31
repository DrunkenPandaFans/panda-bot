package sk.drunkenpanda.bot.plugins.calc

sealed trait Expression
case class Number(value: BigDecimal) extends Expression
object Number {
  def apply(value: String): Number = Number(BigDecimal(value))

  def apply(value: Double): Number = Number(BigDecimal(value))
}

case class UnaryOperator(value: String, exp: Expression) extends Expression
case class BinaryOperator(op: String, leftExp: Expression, rightExp: Expression) 
  extends Expression

