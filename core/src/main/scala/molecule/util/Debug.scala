package molecule.util
import scala.collection.JavaConversions._
import molecule.ast.model._

trait Debug {

  def hej(clazz: String, threshold: Int, max: Int = 9999, debug: Boolean = false, maxLevel: Int = 99) =
    new debug(clazz, threshold, max, debug, maxLevel)

  case class debug(clazz: String, threshold: Int, max: Int = 9999, debug: Boolean = false, maxLevel: Int = 99) {

    def apply(id: Int, params: Any*): Unit = {
      val stackTrace = if (debug) Thread.currentThread.getStackTrace mkString "\n" else ""
      if (id >= threshold && id <= max) {
        var lastId: Long = 0L
        var ids: Set[Long] = Set()

        def traverse(x: Any, level: Int, i: Int): String = {
          if (!x.isInstanceOf[datomic.db.Datum]) {lastId = 0L; ids = Set()}
          val pad1 = if (i == 0) "" else "  " * level
          val pad2 = if (i < 10) "          " else "         "
          val indent = if (i == 0) "" else pad1 + i + pad2
          val max = level >= maxLevel
          x match {
            case l: List[_] if max           => indent + "List(" + l.mkString(",   ") + ")"
            case l: List[_]                  => indent + "List(\n" + l.zipWithIndex.map { case (y, j) => traverse(y, level + 1, j + 1)}.mkString("\n") + ")"
            case l: java.util.List[_] if max => indent + "JavaList(" + l.mkString(",   ") + ")"
            case l: java.util.List[_]        => indent + "JavaList(\n" + l.zipWithIndex.map { case (y, j) => traverse(y, level + 1, j + 1)}.mkString("\n") + ")"
            case l: Map[_, _] if max         => indent + "Map(" + l.mkString(",   ") + ")"
            case l: Map[_, _]                => indent + "Map(\n" + l.zipWithIndex.map { case (y, j) => traverse(y, level + 1, j + 1)}.mkString("\n") + ")"
            case m: Model                => indent + "Model(\n" + m.elements.zipWithIndex.map { case (y, j) => traverse(y, level + 1, j + 1)}.mkString("\n") + ")"

            case m: java.util.Map[_, _] => {
              if (m.size() == 4 && m.keys.map(_.toString).contains(":db-before")) {
                val tx = m.toList
                indent + "Transaction(\n" +
                  traverse(tx(0), level + 1, 1) + "\n" +
                  traverse(tx(1), level + 1, 2) + "\n" +
                  traverse(tx(2), level + 1, 3) + "\n" +
                  traverse(":tempids(\n" + tx(3)._2.asInstanceOf[java.util.Map[_, _]].iterator.zipWithIndex.map { case (y, j) => traverse(y, level + 2, j + 1)}.mkString("\n") + "))", level + 1, 4)
              } else if (max)
                indent + "JavaMap(" + m.iterator.mkString(",   ") + ")"
              else
                indent + "JavaMap(\n" + m.iterator.zipWithIndex.map { case (y, j) => traverse(y, level + 1, j + 1)}.mkString("\n") + ")"
            }

            case (a, b) => {
              val bb = b match {
                case it: Iterable[_]             => traverse(it, level, 0)
                case it: java.util.Collection[_] => traverse(it, level, 0)
                case other                       => other
              }
              indent + s"$a -> " + bb
            }

            case d: datomic.db.Datum => {
              val (entitySep, no) = if (lastId == 0L) ("", 1) else if (lastId != d.e) ("\n", ids.size + 1) else ("", "")
              lastId = d.e
              ids += d.e
              val pad3 = " " * (5 - no.toString.length)
              val pad4 = " " * (6 - i.toString.length)
              val datomS = d.toString + (" " * (30 - d.toString.length))
              val added = if (d.added) "true " else "false"
              val r = if (d.added) " " else "-"
              val datum = List("added: " + added, "t: " + d.tx, "e: " + d.e, "a: " + d.a, "v: " + d.v)
              entitySep + pad1 + no + pad3 + i + pad4 + datum.mkString(",  " + r)
            }

            case value => indent + value //+ s" TYPE: " + value.getClass
          }
        }

        println(s"## $id: $clazz \n========================================================================\n" +
          params.toList.zipWithIndex.map { case (e, i) => traverse(e, 0, i + 1)}
            .mkString("\n------------------------------------------------\n") +
          s"\n========================================================================\n$stackTrace")
      }
    }
  }

  case class trace(debug: Boolean = true) {
    def apply(id: Int, msgs: Any*) { print(s"$id -> " + msgs.mkString("  ###  ")) }
  }
}