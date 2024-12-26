package dev.i10416.example.server.bootstrap

import caliban.CalibanError
import caliban.GraphQLInterpreter
import caliban.Http4sAdapter
import caliban.interop.cats.CatsInterop
import caliban.interop.tapir.HttpInterpreter
import cats.Applicative
import cats.data.OptionT
import cats.effect.kernel.Async
import cats.effect.std.Console
import cats.syntax.all.*
import com.comcast.ip4s.*
import dev.i10416.example.core.aspect.*
import dev.i10416.example.core.aspect.auth.*
import dev.i10416.example.server.aspect.*
import dev.i10416.example.server.aspect.Middleware
import fs2.io.net.Network
import io.circe.Json
import org.http4s.HttpRoutes
import org.http4s.circe.*
import org.http4s.dsl.io.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.Router
import org.http4s.server.Server

object Controller:
  def bind[F[_]: Async: AuthContext: Console: Network](
      interpreter: GraphQLInterpreter[Aspect, CalibanError],
      port: Port = port"8090"
  )(using interop: CatsInterop[F, Aspect]) =
    EmberServerBuilder
      .default[F]
      .withPort(port)
      .withHttpApp(
        Middleware.default(commands <+> queries(interpreter) <+> extras).orNotFound
      )
      .build

  private def commands[F[_]: Applicative] = HttpRoutes.empty

  private def queries[F[_]: Async: Console](
      interpreter: GraphQLInterpreter[Aspect, CalibanError]
  )(using CatsInterop[F, Aspect]) =
    import caliban.interop.cats.implicits.*
    val queryEndpoint = Root / "api" / "graphql"
    // transform GraphQLResponse type into HTTP Response type
    val inner = Http4sAdapter
      .makeHttpServiceF(
        HttpInterpreter(interpreter)
      )
    HttpRoutes[F] {
      case req @ (GET -> `queryEndpoint`) => inner.run(req)
      case req @ (POST -> `queryEndpoint`) => inner.run(req)
    }

  private def extras[F[_]: Applicative] = HttpRoutes.empty
