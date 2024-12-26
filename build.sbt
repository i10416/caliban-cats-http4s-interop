import _root_.caliban.tools.Codegen
ThisBuild / resolvers +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
val calibanVersion = "2.9.0+70-aa97e09d-SNAPSHOT"
val http4sVersion = "0.23.30"
val scala3 = "3.6.2"

lazy val protocol = project
  .enablePlugins(CalibanPlugin)
  .in(file("protocol"))
  .settings(
    scalaVersion := scala3,
    libraryDependencies ++= Seq(
      "com.github.ghostdogpr" %% "caliban" % calibanVersion
    ),
    Compile / caliban / calibanSettings ++= Seq(
      calibanSetting(file("protocol/src/main/graphql/schema.graphql"))(
        _.genType(Codegen.GenType.Schema)
          .clientName("Generated")
          .abstractEffectType(true)
          .scalarMapping("ID" -> "dev.i10416.example.protocol.ID")
          .packageName("dev.i10416.example.protocol")
      )
    )
  )

lazy val core = project
  .in(file("core"))
  .settings(
    scalaVersion := scala3,
    libraryDependencies ++= Seq(
      "co.fs2" %% "fs2-core" % "3.11.0",
      "org.typelevel" %% "cats-core" % "2.12.0",
      "org.typelevel" %% "cats-effect" % "3.5.7",
      "org.typelevel" %% "cats-mtl" % "1.4.0"
    )
  )
  // Compromise:
  // Depends on protocol so that we can use generated types, but it brings caliban dependencies to core
  .dependsOn(protocol)

lazy val server = project
  .in(file("server"))
  .settings(
    run / fork := true,
    scalacOptions ++=
      Seq("-Xmax-inlines", "128"),
    scalaVersion := scala3,
    libraryDependencies ++= Seq(
      "com.github.ghostdogpr" %% "caliban-cats" % calibanVersion,
      "com.github.ghostdogpr" %% "caliban-tapir" % calibanVersion,
      "com.github.ghostdogpr" %% "caliban-http4s" % calibanVersion,
      "org.typelevel" %% "cats-effect" % "3.5.7",
      "co.fs2" %% "fs2-core" % "3.11.0",
      "org.http4s" %% "http4s-core" % http4sVersion,
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-ember-server" % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion,
      "io.circe" %% "circe-core" % "0.14.10",
      "dev.zio" %% "zio" % "2.1.14",
      "dev.zio" %% "zio-interop-cats" % "23.1.0.3"
    )
  )
  .dependsOn(protocol, core)
