name := "traktalk"

version := "1.0"

scalaVersion := "2.9.1"

resolvers += "twitter4j.org Repository" at "http://twitter4j.org/maven2"

libraryDependencies ++= Seq("commons-logging" % "commons-logging" % "1.1.1",
                           "commons-lang" % "commons-lang" % "2.6",
                           "commons-configuration" % "commons-configuration" % "1.8",
                           "org.scala-lang" % "scala-swing" % "2.9.1",
                           "org.slf4j" % "slf4j-api" % "1.6.4",
                           "org.twitter4j" % "twitter4j-core" % "2.2.5",
                           "net.sf.nimrod" % "nimrod-laf" % "1.2"
                           )

seq(assemblySettings: _*)


