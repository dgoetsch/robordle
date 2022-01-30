package robordle

import robordle.fact.Fact
import cats.effect.{IO, IOApp}

object main extends IOApp.Simple {
  val everythingWeKnow = Fact.All(
    Seq(Fact.Contains("l"), Fact.Exact('u', 3), Fact.ExactNot('l', 2), Fact.Not("trnkpms"))
  )

  import dictionary.Dictionary
  given Dictionary = Dictionary()

  import dsl._
  val run = startWithProbablyAllOfTheEnglishWords[IO].onlyKeepTheOnesWithFiveLetters
    .thatAreCongruentWith(everythingWeKnow)
    .thenPrintAllTheOptions
    .andThenJustStop
}
