package molecule.examples.dayOfDatomic.schema

import molecule.dsl.schemaDefinition._

@InOut(0, 4)
object GraphDefinition {

  trait User {
    val name        = oneString.uniqueIdentity
    val roleInGroup = many[RoleInGroup]
  }

  trait RoleInGroup {
    val name  = oneString
    val role  = one[Role]
    val group = one[Group]
  }

  trait Group {
    val name  = oneString.uniqueIdentity
    val roles = many[Role]
  }

  trait Role {
    val name = oneString.uniqueIdentity
  }
}