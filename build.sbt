name := """play-demo-api"""
organization := "com.demo"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.8"

libraryDependencies += filters
libraryDependencies += javaJdbc
libraryDependencies += "org.xerial" % "sqlite-jdbc" % "3.16.1"
libraryDependencies += "be.cafeba" %% "play-cors" % "1.0"
libraryDependencies += "org.mindrot" % "jbcrypt" % "0.3m"