packSettings

packMain := Map("DeleteAllQualifications" -> "edu.umass.cs.automan.adapters.mturk.DeleteAllQualifications")

name := "DeleteAllQualifications"

version := "1.0"

organization := "edu.umass.cs"

scalaVersion := "2.11.7"

exportJars := true

libraryDependencies ++= Seq(
  "edu.umass.cs"        %% "automan" % "1.1.6",
  "org.rogach"          %% "scallop" % "0.9.5"
)
