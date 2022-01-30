package robordle.fs

import cats.effect.Concurrent
import fs2.{hash, text}
import fs2.io.file.{Files, Path}
import fs2.Stream

def stream[F[_]: Files: Concurrent](fileName: String): Stream[F, String] =
  Files[F]
    .readAll(Path(fileName))
    .through(text.utf8.decode)
    .through(text.lines)
