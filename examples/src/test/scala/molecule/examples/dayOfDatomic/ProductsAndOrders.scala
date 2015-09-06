package molecule.examples.dayOfDatomic
import molecule._
import molecule.examples.dayOfDatomic.dsl.productsOrder._
import molecule.examples.dayOfDatomic.schema._
import molecule.util.MoleculeSpec


class ProductsAndOrders extends MoleculeSpec {


  "Nested data" >> {

    // See: http://blog.datomic.com/2013/06/component-entities.html

    // Make db
    implicit val conn = load(ProductsOrderSchema, "Orders")

    // Insert 2 products
    val List(chocolateId, whiskyId) = Product.description.insert("Expensive Chocolate", "Cheap Whisky").eids


    // Insert nested data .................................

    // Template for Order with multiple LineItems
    val order = m(Order.orderid.LineItems * LineItem.product.price.quantity)

    // Make order with two line items and return created entity id
    val orderId = order.insert(23, List((chocolateId, 48.00, 1), (whiskyId, 38.00, 2))).eid

    // Find id of order with chocolate
    Order.e.LineItems.Product.description_("Expensive Chocolate").get.head === orderId


    // Touch entity ................................

    // Get all attributes/values of this entity. Sub-component values are recursively retrieved and sorted by their ids
    orderId.touch === Map(
      ":db/id" -> 17592186045422L,
      ":order/lineItems" -> List(
        Map(
          ":db/id" -> 17592186045424L,
          ":lineItem/price" -> 38.0,
          ":lineItem/product" -> Map(
            ":db/id" -> 17592186045420L,
            ":product/description" -> "Cheap Whisky"),
          ":lineItem/quantity" -> 2),
        Map(
          ":db/id" -> 17592186045423L,
          ":lineItem/price" -> 48.0,
          ":lineItem/product" -> Map(
            ":db/id" -> 17592186045419L,
            ":product/description" -> "Expensive Chocolate"),
          ":lineItem/quantity" -> 1)),
      ":order/orderid" -> 23)


    // Retract nested data ............................

    // Retract entity - all subcomponents/lineItems are retracted
    orderId.retract

    // The products are still there
    Product.e.description_("Expensive Chocolate" or "Cheap Whisky").get === List(chocolateId, whiskyId)
  }
}




























































