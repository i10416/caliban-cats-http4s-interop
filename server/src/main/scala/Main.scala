package dev.i10416.example.server

import caliban.interop.cats.CatsInterop
import cats.data.Kleisli
import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import cats.effect.implicits.effectResourceOps
import cats.effect.std.Dispatcher
import dev.i10416.example.core.aspect.auth.AuthInfo
import dev.i10416.example.server.aspect.Aspect
import dev.i10416.example.server.bootstrap.*
import zio.ZEnvironment


object Main extends IOApp:
  type RIO[A] = Kleisli[IO, Aspect, A]
  def run(args: List[String]): IO[ExitCode] =
    given zio.Runtime[Aspect] =
      zio.Runtime.default.withEnvironment(ZEnvironment(AuthInfo.Empty))
    program.useForever.run(AuthInfo.Empty).as(ExitCode.Success)

  // core program
  def program(using zio.Runtime[Aspect]) = Dispatcher
    .parallel[RIO]
    .flatMap:
      case dispatcher @ (given Dispatcher[RIO]) =>
        given interop: CatsInterop.Contextual[RIO, Aspect] =
          CatsInterop.contextual(dispatcher)
        for
          interpreter <- Components
            .mkGraphQL[RIO]
            .toResource
          app <- Controller.bind[RIO](interpreter)
        yield app
end Main
