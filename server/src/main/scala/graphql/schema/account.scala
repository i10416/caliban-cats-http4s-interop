package dev.i10416.example.server.graphql.schema

import caliban.interop.cats.implicits.*
import caliban.schema.Schema
import cats.effect.std.Dispatcher
import dev.i10416.example.protocol.Types.Account
import dev.i10416.example.protocol.graphql.codec.prelude.*

object account {
  import cats.effect.kernel.Async
  implicit def accountF[
    F[_]: Dispatcher, R
  ]: Schema[R, F[Account]] =
    catsEffectSchema
}
