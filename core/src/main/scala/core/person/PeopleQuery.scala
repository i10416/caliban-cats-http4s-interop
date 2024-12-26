package dev.i10416.example.core.person
import cats.Parallel
import cats.effect.Async
import cats.mtl.Local
import cats.syntax.all.*
import dev.i10416.example.core.article.*
import dev.i10416.example.protocol.ID
import dev.i10416.example.protocol.Types.Person


trait PeopleQuery[F[_]] {
  def all: F[List[Person[F]]]
  def find(id: String): F[Option[Person[F]]]
}

object PeopleQuery {
  def apply[F[_]](
      articleService: => ArticlesQuery[F]
  )(using F: Async[F]): PeopleQuery[F] =
    new PeopleQuery[F] {
      def all: F[List[Person[F]]] = F.pure(Nil)
      def find(id: String): F[Option[Person[F]]] =
        id.toLowerCase() match
          case id if Set("鈴木太郎", "山田花子", "佐藤一").contains(id) =>
            F.delay(
              Some(
                Person(
                  ID(id),
                  id.take(2).mkString + " " + id.drop(2).mkString,
                  articleService
                    .searchByPerson(
                      id
                    )
                )
              )
            )

          case other => F.pure(None)
    }
}
