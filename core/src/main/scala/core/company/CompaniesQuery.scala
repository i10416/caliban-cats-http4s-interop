package dev.i10416.example.core.company
import cats.Parallel
import cats.effect.Async
import cats.mtl.Local
import cats.syntax.all.*
import dev.i10416.example.core.article.*
import dev.i10416.example.core.person.*
import dev.i10416.example.protocol.ID
import dev.i10416.example.protocol.Types.Article
import dev.i10416.example.protocol.Types.ArticleConnection
import dev.i10416.example.protocol.Types.ArticleEdge
import dev.i10416.example.protocol.Types.Company
import dev.i10416.example.protocol.Types.Contact
import dev.i10416.example.protocol.Types.PageInfo


trait CompaniesQuery[F[_]] {
  def find(
      id: String
  ): F[Option[Company[F]]]
}

object CompaniesQuery {
  val data = List(
    (
      (
        "fictional-journal",
        "Fictional Journal",
        Contact(
          ID("fictional-journal"),
          Some("contact@example.com"),
          Some("01-2345-6789"),
          Some("The Earth, Utopia, #404")
        ),
        "........"
      )
    )
  )
  def apply[F[_]](
      articlesService: => ArticlesQuery[F]
  )(using
      F: Async[F],
      P: Parallel[F]
  ): CompaniesQuery[F] =
    new CompaniesQuery[F] {
      def find(
          id: String
      ): F[Option[Company[F]]] =
        findInternal(id).flatMap:
          case None => none.pure[F]
          case Some((id, name, contact, desc)) =>
            Some(
              Company(
                ID(id),
                name,
                F.pure(None),
                Some(desc),
                articlesService.searchByCompany(id),
                Nil
              )
            ).pure[F]

      def findInternal(
          id: String
      ): F[Option[(String, String, Contact, String)]] =
        data.find(_._1 == id) match
          case None        => F.pure(None)
          case Some(props) => F.pure(Some(props))
    }
}
