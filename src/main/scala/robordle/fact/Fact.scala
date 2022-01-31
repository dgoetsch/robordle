package robordle.fact

final case class Facts(theFacts: Seq[Fact]) {
  def matches(subject: String): Boolean =
    theFacts.forall(_.matches(subject))

  def exact: Seq[Fact.Exact] = theFacts.collect { case exact: Fact.Exact => exact }
}
sealed trait Fact {
  def matches(subject: String): Boolean
}

object Fact {

  final case class Contains(chars: String) extends Fact {
    override def matches(subject: String): Boolean =
      chars.forall(subject.contains)
  }

  final case class Exact(char: Char, pos: Int) extends Fact {

    def index = pos - 1
    override def matches(subject: String): Boolean =
      subject.charAt(index) == char
  }

  final case class ExactNot(char: Char, pos: Int) extends Fact {

    def index = pos - 1
    override def matches(subject: String): Boolean =
      subject.charAt(index) != char
  }

  final case class Not(chars: String) extends Fact {
    override def matches(subject: String): Boolean =
      chars.forall(c => !subject.contains(c))
  }
}
