package utils

import java.util.UUID

import anorm.{ToStatement, Column, TypeDoesNotMatch}

/**
 * Created By: paullawler
 */
object UUIDHelper {

  /**
   * Implicit conversion from UUID to Anorm statement value
   */
  implicit def uuidToStatement = new ToStatement[UUID] {
    def set(s: java.sql.PreparedStatement, index: Int, aValue: UUID): Unit = s.setObject(index, aValue)
  }

  /**
   * Implicit conversion from Anorm row to UUID
   */
  implicit def rowToUUID: Column[UUID] = {
    Column.nonNull[UUID] { (value, meta) =>
      value match {
        case v: UUID => Right(v)
        case _ => Left(TypeDoesNotMatch(s"Cannot convert $value:${value.asInstanceOf[AnyRef].getClass} to UUID for column ${meta.column}"))
      }
    }
  }

}
