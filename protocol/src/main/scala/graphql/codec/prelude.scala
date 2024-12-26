package dev.i10416.example.protocol.graphql.codec
import caliban.schema.ArgBuilder
import caliban.schema.Schema
import dev.i10416.example.protocol.ID
import dev.i10416.example.protocol.Types.Account
import dev.i10416.example.protocol.Types.Contact
import dev.i10416.example.protocol.Types.Currency
import dev.i10416.example.protocol.Types.Currency.Dollar
import dev.i10416.example.protocol.Types.Currency.Yen
import dev.i10416.example.protocol.Types.PageInfo
import dev.i10416.example.protocol.Types.Price
import dev.i10416.example.protocol.Types.QueryArticleArgs
import dev.i10416.example.protocol.Types.QueryCompanyArgs
import dev.i10416.example.protocol.Types.QueryPersonArgs
import dev.i10416.example.protocol.Types.SortOrder
import dev.i10416.example.protocol.Types.Viewer


object prelude {
  implicit val id: ArgBuilder[ID] = ArgBuilder.string.map(ID(_))
  implicit def idSchema[R]: Schema[R, ID] =
    Schema.stringSchema.contramap(_.value)
  // relay
  implicit def pageInfoSchema[R]: Schema[R, PageInfo] = Schema.gen
  implicit def ViewerSchema[R]: Schema[R, Viewer] =
    Schema.gen
  // basic types
  implicit def currency[R]: Schema[R, Currency] =
    import Schema.auto.*
    Schema.gen
  implicit def price[R]: Schema[R, Price] = Schema.gen
  implicit def contact[R]: Schema[R, Contact] = Schema.gen
  implicit def account[R]: Schema[R, Account] = Schema.gen
  // arguments
  implicit val pageInfoArgBuilder: ArgBuilder[PageInfo] = ArgBuilder.gen
  implicit def QueryPersonArgsSchema[R]: Schema[R, QueryPersonArgs] = Schema.gen
  implicit def QueryCompanyArgsSchema[R]: Schema[R, QueryCompanyArgs] =
    Schema.gen
  implicit def QueryArticleArgsSchema[R]: Schema[R, QueryArticleArgs] =
    Schema.gen
  implicit val QueryPersonArgs: ArgBuilder[QueryPersonArgs] = ArgBuilder.gen
  implicit val QueryCompanyArgs: ArgBuilder[QueryCompanyArgs] = ArgBuilder.gen
  implicit val QueryArticleArgs: ArgBuilder[QueryArticleArgs] = ArgBuilder.gen
}
