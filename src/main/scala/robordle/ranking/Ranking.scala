package robordle.ranking
import robordle.fact.Facts
import fs2.concurrent.Channel
import cats.Monad
import cats.effect.IO
final case class Ranking(subject: String, score: Double)

object Ranking {
  import cats.syntax.flatMap._
  import cats.syntax.functor._
  import cats.syntax.applicative._

  def rank[F[_]: cats.effect.Concurrent: Monad](
      stream: fs2.Stream[F, String],
      accordingTo: Facts
  ): fs2.Stream[F, Ranking] = {
    val rubrickBuilderState: (fs2.Stream[F, String], RubrickBuilder) =
      (fs2.Stream.empty[F], new RubrickBuilder(accordingTo))

    stream
      .fold(rubrickBuilderState) { case ((downStream, builder), subject) =>
        (downStream.append(fs2.Stream(subject)), builder.accountFor(subject))
      }
      .flatMap { case (stream, builder) =>
        stream
          .fold((Seq.empty[Ranking], builder.rubrick)) { case ((rankings, rubrick), subject) =>
            val newRankings: Seq[Ranking] = rankings :+ rubrick.rankingFor(subject)
            (newRankings.sortBy(_.score).reverse.take(10), rubrick)
          }
          .flatMap { case (rankings, _) => fs2.Stream.apply[F, Ranking](rankings: _*) }
      }
  }
}

private final class RubrickBuilder(val facts: Facts) {
  private var totalLength: Int = 0
  private val characterLocationFrequency: collection.mutable.Map[Char, Map[Int, Int]] =
    collection.mutable.Map()
  val ignoreIndices = facts.exact.map(_.pos - 1).toSet

  def accountFor(subject: String): RubrickBuilder = {
    this.totalLength += 1
    subject.zipWithIndex
      .foreach { case (character, idx) =>
        if (!ignoreIndices.contains(idx)) {
          val frequencies = characterLocationFrequency
            .get(character)
            .getOrElse(Map.empty)

          val thisFrequency = frequencies.get(idx).getOrElse(0) + 1

          characterLocationFrequency
            .put(
              character,
              frequencies + (idx -> thisFrequency)
            )
        }
      }
    this
  }

  def rubrick: Rubrick = {
    Rubrick(
      totalLength,
      characterLocationFrequency.toMap,
      characterLocationFrequency.toMap.map { case (k, v) => k -> v.map(_._2).sum },
      ignoreIndices
    )
  }
}

private final case class Rubrick(
    totalLength: Int,
    characterLocationFrequency: Map[Char, Map[Int, Int]],
    characterTotalAppearances: Map[Char, Int],
    ignoreIndices: Set[Int]
) {
  def rankingFor(subject: String): Ranking = {
    val indexedScore = subject.zipWithIndex
      .filterNot { case (_, idx) => ignoreIndices.contains(idx) }
      .map { case (c, idx) =>
        characterLocationFrequency
          .get(c)
          .flatMap(_.get(idx))
          .getOrElse(0)
      }
      .sum
      .toDouble / (totalLength * subject.size)

    val overallScore = subject.toSet
      .map { case c =>
        characterTotalAppearances.get(c).getOrElse(0)
      }
      .sum
      .toDouble / (totalLength * subject.size)

    val score = (overallScore + indexedScore) / 2
    Ranking(subject, score)
  }
}
