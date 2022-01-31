package robordle

import robordle.fact.{Facts, Fact}
import cats.effect.{IO, IOApp}

object main extends IOApp.Simple {
  val everythingWeKnow = Facts(Seq(Fact.Not("sales")))
  //   Seq(Fact.Contains("l"), Fact.Exact('u', 3), Fact.ExactNot('l', 2), Fact.Not("trnkpms"))
  // )

  import dictionary.Dictionary
  given Dictionary[IO] = Dictionary[IO]()

  import dsl._
  val run = startWithProbablyAllOfTheEnglishWords.butOnlyTheOnesWithFiveLetters
    .thatAreCongruentWith(everythingWeKnow)
    .rankAccordingToFrequencyWithAwarenessOf(everythingWeKnow)
    .map(_.toString)
    .thenPrintAllTheOptions
    .andThenJustStop
}
