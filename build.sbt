ThisBuild / scalaVersion     := "3.1.0"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "robordle"
ThisBuild / organizationName := "robordle"
scalacOptions ++= Seq(
  "-Xfatal-warnings",
  "-deprecation",
  "-unchecked"
  // "-source:future"
)
lazy val root = (project in file("."))
  .settings(
    name := "robordle",
    libraryDependencies += "co.fs2" %% "fs2-io" % "3.2.4"
  )

