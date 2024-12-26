package dev.i10416.example.server.graphql.schema

import caliban.interop.cats.implicits.*
import caliban.schema.Schema
import caliban.schema.Schema.auto.field
import cats.effect.std.Dispatcher
import dev.i10416.example.protocol.Types.Article
import dev.i10416.example.protocol.Types.ArticleConnection
import dev.i10416.example.protocol.Types.ArticleEdge
import dev.i10416.example.protocol.Types.Company
import dev.i10416.example.protocol.Types.FinancialReport
import dev.i10416.example.protocol.Types.Person
import dev.i10416.example.protocol.Types.QueryArticleArgs
import dev.i10416.example.protocol.Types.QueryCompanyArgs
import dev.i10416.example.protocol.Types.QueryPersonArgs
import dev.i10416.example.protocol.graphql.codec.prelude.*
import dev.i10416.example.server.aspect.*


object recursive:
  implicit def articleConn[
      F[_]: Dispatcher, R
  ]: Schema[R, ArticleConnection[F]] = Schema.gen
  implicit def articleEdge[
      F[_]: Dispatcher, R
  ]: Schema[R, ArticleEdge[F]] = Schema.gen

  implicit def person[
      F[_]: Dispatcher, R
  ]: Schema[R, Person[F]] =
    Schema.obj("Person", Some("person"), Nil) { implicit fields =>
      List(
        field("id")(_.id),
        field("fullName")(_.fullName),
        field(
          "articles",
          Some("articles"),
          Nil
        )(_.articles)
      )
    }
  implicit def personF[
      F[_]: Dispatcher, R
  ]: Schema[R, F[Person[F]]] =
    catsEffectSchema

    // derives Article schemas
  implicit def article[
      F[_]: Dispatcher, R
  ]: Schema[R, Article[F]] =
    Schema.obj("Article", Some("article"), Nil) { implicit fields =>
      List(
        field("id")(_.id),
        field("title")(_.title),
        field("publishedAt")(_.publishedAt),
        field("abstract")(_.`abstract`),
        field("authors")(_.authors)
      )
    }
  implicit def articleF[
      F[_]: Dispatcher, R
  ]: Schema[R, F[Article[F]]] =
    catsEffectSchema

  // derives Company schemas
  implicit def company[
      F[_]: Dispatcher, R
  ]: Schema[R, Company[F]] =
    Schema.obj("Company", Some("company"), Nil) { implicit fields =>
      List(
        field("id")(_.id),
        field("name")(_.name),
        field("businessDescription")(_.businessDescription),
        field("primaryContact").apply(_.primaryContact),
        field(
          "articles",
          Some("articles"),
          Nil
        )(_.articles),
      )
    }
  implicit def companyF[
      F[_]: Dispatcher, R
  ]: Schema[R, F[Company[F]]] =
    catsEffectSchema

  // derives FinancialReport schemas
  implicit def financialReport[
      F[_]: Dispatcher, R
  ]: Schema[R, FinancialReport] =
    Schema.obj("FinancialReport", Some("financial report"), Nil) {
      implicit fields =>
        List(
          field("id")(_.id),
          field("season")(_.season),
          field("sales")(_.sales),
          field("operatingProfit")(_.operatingProfit),
          field("ordinaryProfit")(_.ordinaryProfit),
        )
    }
  implicit def financialReportF[
      F[_]: Dispatcher, R
  ]: Schema[R, F[FinancialReport]] =
    catsEffectSchema
end recursive
