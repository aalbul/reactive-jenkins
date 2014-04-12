name := "jenkins"

version := "0.1"

scalaVersion := "2.10.4"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.0"

libraryDependencies += "io.spray" % "spray-client" % "1.3.1"

libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.3.2"

libraryDependencies += "com.fasterxml.jackson.datatype" % "jackson-datatype-joda" % "2.3.2"

libraryDependencies += "org.joda" % "joda-convert" % "1.6"

resolvers += "spray repo" at "http://repo.spray.io"
