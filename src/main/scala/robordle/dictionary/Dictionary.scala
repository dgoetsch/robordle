package robordle.dictionary
import fs2.io.file.{Files, Path}
import cats.effect.Concurrent
trait Dictionary {
  def allEnglishWords[F[_]: Files: Concurrent]: fs2.Stream[F, String]
}

object Dictionary {
  def apply() = new Dictionary {
    import robordle.fs
    // import cats.effect._

    private val fileName                                  = "src/main/resources/words_alpha.txt"
    override def allEnglishWords[F[_]: Files: Concurrent] = fs.stream[F](fileName)
  }
}
