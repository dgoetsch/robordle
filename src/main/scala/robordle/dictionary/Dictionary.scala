package robordle.dictionary

trait Dictionary[F[_]] {
  def allEnglishWords: fs2.Stream[F, String]
}

object Dictionary {
  import fs2.io.file.Files
  import cats.effect.Concurrent

  def apply[F[_]: Files: Concurrent](): Dictionary[F] = new Dictionary[F] {
    import robordle.fs
    // import cats.effect._

    private val fileName         = "src/main/resources/words_alpha.txt"
    override def allEnglishWords = fs.stream[F](fileName)
  }
}
