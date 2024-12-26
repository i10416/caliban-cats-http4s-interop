package dev.i10416.example.server.bootstrap

import caliban.CalibanError
import caliban.GraphQLInterpreter
import caliban.RootResolver
import caliban.graphQL
import caliban.interop.cats.CatsInterop
import caliban.interop.cats.implicits.*
import caliban.schema.Schema
import caliban.wrappers.Wrappers.maxDepth
import caliban.wrappers.Wrappers.printErrors
import caliban.wrappers.Wrappers.printSlowQueries
import cats.Parallel
import cats.data.Kleisli
import cats.effect.kernel.Async
import cats.effect.kernel.Resource
import cats.effect.std.Dispatcher
import cats.syntax.all.*
import dev.i10416.example.core.article.*
import dev.i10416.example.core.aspect.auth.*
import dev.i10416.example.core.company.*
import dev.i10416.example.core.person.*
import dev.i10416.example.protocol.Operations.Query
import dev.i10416.example.protocol.Types.Company
import dev.i10416.example.protocol.Types.Contact
import dev.i10416.example.protocol.Types.Person
import dev.i10416.example.protocol.Types.Viewer
import dev.i10416.example.server.aspect.*
import zio.durationInt

import scala.concurrent.duration.*

object Components:

  def mkGraphQL[F[_]: Async: Parallel: AuthContext: Dispatcher](using
      // required for context propagation between zio and cats
      interop: CatsInterop[F, Aspect]
  ): F[GraphQLInterpreter[Aspect, CalibanError]] =
    import dev.i10416.example.protocol.graphql.codec.prelude.*
    import dev.i10416.example.server.graphql.schema.account.*
    import dev.i10416.example.server.graphql.schema.recursive.*
    given Schema[Aspect, Query[F]] = Schema.gen
    (graphQL(resolver[F])
      @@ maxDepth(10)
      @@ printSlowQueries(500.milliseconds)
      @@ printErrors)
      .interpreterF[F]

  private def resolver[F[_]: Async: Parallel: AuthContext] = RootResolver(
    query[F]
  )

  private def query[F[_]: Parallel: AuthContext](using F: Async[F]) =
    lazy val articlesQuery: ArticlesQuery[F] =
      ArticlesQuery[F](peopleQuery, companiesQuery)
    lazy val peopleQuery: PeopleQuery[F] =
      PeopleQuery[F](articlesQuery)
    lazy val companiesQuery: CompaniesQuery[F] =
      CompaniesQuery[F](articlesQuery)

    Query[F](
      person = args => peopleQuery.find(args.id.value),
      company = args => companiesQuery.find(args.id.value),
      article = args => articlesQuery.findById(args.id.value),
      viewer = F.delay(Some(Viewer("Anonymous User"))),
      me = F.raiseError(new Exception("Not Implemented"))
    )

end Components
