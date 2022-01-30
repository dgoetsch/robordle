package robordle
import robordle.dictionary.Dictionary
import robordle.fact.Fact

import fs2.Stream
import fs2.io.file.Files
import fs2.io.stdoutLines
import cats.effect.{Concurrent, IO, Sync}

object dsl {
  def startWithProbablyAllOfTheEnglishWords[F[_]: Files: Concurrent](using
      dictionary: Dictionary
  ): Stream[F, String] = {
    dictionary.allEnglishWords[F]
  }

  extension [F[_]](stream: Stream[F, String])
    def onlyKeepTheOnesWithFiveLetters: Stream[F, String] = stream.filter(_.size == 5)

    def thatAreCongruentWith(fact: Fact): Stream[F, String] = stream.filter(fact.matches)

  extension [F[_]: Sync](stream: Stream[F, String])
    def thenPrintAllTheOptions: Stream[F, String] = stream
      .map(_ + "\n")
      .through(fs2.io.stdoutLines())

  extension (stream: Stream[IO, String]) def andThenJustStop: IO[Unit] = stream.compile.drain

}
