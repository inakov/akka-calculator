name := "akka-calculator"

version := "1.0"

scalaVersion := "2.12.0"

libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4.12"

exportJars := true

assemblyJarName in assembly := "akka-calculator.jar"