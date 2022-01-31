package robordle
import robordle.dictionary.Dictionary
import robordle.fact.Facts

import fs2.Stream
import fs2.io.stdoutLines
import cats.effect.{IO, Sync}
import ranking.Ranking
object dsl {
  def startWithProbablyAllOfTheEnglishWords[F[_]](using
      dictionary: Dictionary[F]
  ): Stream[F, String] = {
    dictionary.allEnglishWords
  }

  extension [F[_]](stream: Stream[F, String])
    def butOnlyTheOnesWithFiveLetters: Stream[F, String] = stream.filter(_.size == 5)

    def thatAreCongruentWith(fact: Facts): Stream[F, String] = stream.filter(fact.matches)

  extension [F[_]: Sync](stream: Stream[F, Ranking])
    def thenPrintAllTheOptions: Stream[F, Ranking] = stream
      .through(
        _.map(_.toString + "\n")
          .through(fs2.io.stdoutLines())
      )

  extension [F[_]: cats.effect.Concurrent](stream: Stream[F, String])
    def rankAccordingToFrequency(withAwarenessOf: Facts): Stream[F, Ranking] = {
      Ranking.rank(stream, withAwarenessOf)
    }

  extension [F[_]: cats.effect.Concurrent](stream: Stream[F, Ranking])
    def selectTheMostLikelyCandidates: Stream[F, Ranking] = {
      stream
        .fold((Seq.empty[Ranking])) { case (rankings, ranking) =>
          val newRankings: Seq[Ranking] = rankings :+ ranking
          newRankings.sortBy(_.score).reverse.take(10)
        }
        .flatMap(fs2.Stream.apply[F, Ranking](_*))

    }

  extension [T](stream: Stream[IO, T]) def andThenJustStop: IO[Unit] = stream.compile.drain

}
