package dev.i10416.example.core.article

import cats.Parallel
import cats.effect.Async
import cats.mtl.Local
import cats.syntax.all.*
import dev.i10416.example.core.company.*
import dev.i10416.example.core.person.*
import dev.i10416.example.protocol.ID
import dev.i10416.example.protocol.Types.Article
import dev.i10416.example.protocol.Types.ArticleConnection
import dev.i10416.example.protocol.Types.ArticleEdge
import dev.i10416.example.protocol.Types.PageInfo
import dev.i10416.example.protocol.Types.Revision


trait ArticlesQuery[F[_]] {
  def findById(id: String): F[Option[Article[F]]]
  def searchByPerson(
      id: String
  ): F[ArticleConnection[F]]
  def searchByCompany(
      id: String
  ): F[ArticleConnection[F]]
}

object ArticlesQuery {
  val data = List(
    (
      "ai-innovation-transforms-industry-structure",
      "AIが変革する産業構造：2025年の労働市場予測",
      "-",
      "...",
      List("鈴木太郎", "山田花子", "佐藤一"),
      List("industry-and-labor-research-lab")
    ),
    (
      "next-gen-infra-investment",
      "次世代インフラ投資：政府の新政策と民間企業の挑戦",
      "-",
      "...",
      List("山田花子"),
      List("future-energy")
    ),
    (
      "us-china-trade-war-next-stage",
      "米中貿易戦争の次のステージ：新興市場への影響を読み解く",
      "-",
      "...",
      List("佐藤一", "鈴木太郎", "山田花子"),
      List("fictional-journal")
    )
  )

  def apply[F[_]](
      peopleQuery: => PeopleQuery[F],
      companiesQuery: => CompaniesQuery[F]
  )(using
      F: Async[F],
      P: Parallel[F]
  ): ArticlesQuery[F] =
    new ArticlesQuery[F] {
      def findById(id: String): F[Option[Article[F]]] = data.find(_._1 ==id).traverse {
        case (id, title, publishedAt, description, authorIds, companyIds) => (
            authorIds.traverseFilter(peopleQuery.find),
            companyIds.traverseFilter(companiesQuery.find)
          ).mapN((authors, companies) =>
                Article(
                  ID(id),
                  title,
                  publishedAt,
                  List(),
                  description,
                  Some(authors),
                  companies
                )
            )
      }

      def searchByCompany(id: String): F[ArticleConnection[F]] =
        data.filter(_._6.contains(id)).traverse { case (id, title, publishedAt, description, authorIds, companyIds) =>
          (
            authorIds.traverseFilter(peopleQuery.find),
            companyIds.traverseFilter(companiesQuery.find)
          ).mapN((authors, companies) =>
            ArticleEdge(
              Some(
                Article(
                  ID(id),
                  title,
                  publishedAt,
                  List(),
                  description,
                  Some(authors),
                  companies
                )
              ),
              None
            )
          )
        }.map(edges => ArticleConnection(edges, None, None))

      def searchByPerson(
          personId: String
      ): F[ArticleConnection[F]] =
        data.filter(_._5.contains(personId)).traverse{case (id, title, publishedAt, description,authorIds,companyIds) =>
          (
            authorIds.traverseFilter(peopleQuery.find),
            companyIds.traverseFilter(companiesQuery.find)
          ).mapN((authors, companies) =>
            ArticleEdge(
              Some(
                Article(
                  ID(id),
                  title,
                  publishedAt,
                  List(),
                  description,
                  Some(authors),
                  companies
                )
              ),
              None
            )
          )
        }.map(edges => ArticleConnection(edges,None,None))
    }
}
