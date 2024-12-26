package dev.i10416.example.protocol

sealed trait ID:
  def value: String

object ID:
  private case class Impl(val value: String) extends ID
  def apply(str: String): ID = Impl(str)
end ID

sealed trait Error
object Error
