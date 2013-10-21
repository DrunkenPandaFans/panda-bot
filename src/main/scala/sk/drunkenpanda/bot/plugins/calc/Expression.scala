package sk.drunkenpanda.bot.plugins.calc

sealed trait Expression
case class Number(value: BigDecimal) extends Expression
case class UnaryOperator(value: String, exp: Expression) extends Expression
case class BinaryOperator(op: String, leftExp: Expression, rightExp: Expression) 
  extends Expression

