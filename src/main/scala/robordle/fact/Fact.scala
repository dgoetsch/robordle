package robordle.fact

sealed trait Fact {
  def matches(subject: String): Boolean
}

object Fact {
  final case class All(matchers: Seq[Fact]) extends Fact {
    override def matches(subject: String): Boolean =
      matchers.forall(_.matches(subject))
  }
  final case class Contains(chars: String) extends Fact {
    override def matches(subject: String): Boolean =
      chars.forall(subject.contains)
  }

  final case class Exact(char: Char, pos: Int) extends Fact {
    override def matches(subject: String): Boolean =
      subject.charAt(pos - 1) == char
  }

  final case class ExactNot(char: Char, pos: Int) extends Fact {
    override def matches(subject: String): Boolean =
      subject.charAt(pos - 1) != char
  }

  final case class Not(chars: String) extends Fact {
    override def matches(subject: String): Boolean =
      chars.forall(c => !subject.contains(c))
  }
}
