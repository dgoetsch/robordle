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

  extension [F[_]: Sync](stream: Stream[F, String])
    def thenPrintAllTheOptions: Stream[F, String] = stream
      .map(_ + "\n")
      .through(fs2.io.stdoutLines())

  extension (stream: Stream[IO, String])
    def rankAccordingToFrequencyWithAwarenessOf(theFacts: Facts): Stream[IO, Ranking] = {
      Ranking.rank(stream, theFacts)
    }
    def andThenJustStop: IO[Unit] = stream.compile.drain

}
