package dev.i10416.example.server.aspect.auth
import dev.i10416.example.core.aspect.auth.*
import cats.MonadThrow
import cats.data.Kleisli
import cats.data.OptionT
import cats.syntax.all.*
import org.http4s.HttpRoutes
import org.http4s.Request

object AuthMiddleware:
  import org.typelevel.ci.*
  import cats.syntax.all.*
  import cats.mtl.syntax.local.*
  private val TokenHeader = ci"X-Token"
  def apply[F[_]: MonadThrow: AuthContext](
      routes: HttpRoutes[F]
  ): HttpRoutes[F] =
    Kleisli { (req: Request[F]) =>
      req.headers.get(TokenHeader) match {
        case Some(token) =>
          routes.run(req).scope(AuthInfo.Token(token.head.value): AuthInfo)
        case None =>
          OptionT.liftF(
            MonadThrow[F].raiseError(new Exception("Unauthenticated"))
          )
      }
    }
