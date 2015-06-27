name := "akka-calculator"

version := "1.0"

scalaVersion := "2.11.6"

libraryDependencies += "org.scala-lang.modules" % "scala-parser-combinators_2.11" % "1.0.2"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4-SNAPSHOT"

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

exportJars := true

assemblyJarName in assembly := "akka-calculator.jar"