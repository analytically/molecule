package molecule.examples.dayOfDatomic.schema

import molecule.dsl.schemaDefinition._

@InOut(3, 6)
object DbDefinition {

  trait Db {
    val valueType = oneString
  }
}