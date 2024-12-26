package dev.i10416.example.server.aspect

import cats.MonadThrow
import cats.data.Kleisli
import cats.data.OptionT
import cats.mtl.Local
import cats.mtl.syntax.local.*
import cats.syntax.all.*
import dev.i10416.example.core.aspect.auth.*
import dev.i10416.example.server.aspect.auth.AuthMiddleware
import org.http4s.HttpRoutes
import org.http4s.Request
import org.typelevel.ci.*

type Aspect = AuthInfo
type Context[F[_]] = Local[F, Aspect]

object Context:
  def apply[F[_]](implicit ev: Context[F]): Context[F] = ev

  def token[F[_]: MonadThrow](using L: Context[F]): F[AuthInfo.Token] =
    L.ask[Aspect].flatMap {
      case t: AuthInfo.Token => MonadThrow[F].pure(t)
      case AuthInfo.Empty =>
        MonadThrow[F].raiseError(new Exception("Unauthenticated"))
    }
end Context

object Middleware:
  def default[F[_]: MonadThrow](routes: HttpRoutes[F])(using
      Ctx: Context[F]
  ): HttpRoutes[F] =
    given AuthContext[F] = Ctx.imap(identity)(identity)
    AuthMiddleware(routes)

