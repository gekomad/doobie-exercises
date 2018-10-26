import cats.effect.IO
import org.scalatest.FunSuite

class Logging extends FunSuite {

  case class Person(id: Int, name: String)

  import MyPredef.xa

  test("logging") {

    import doobie.implicits._
    import doobie.util.log.LogHandler

    def byName(pat: String): IO[List[(String, String)]] = {
      sql"select name, code from country where name like $pat"
        .queryWithLogHandler[(String, String)](LogHandler.jdkLogHandler)
        .to[List]
        .transact(xa)
    }

    assert(byName("U%").unsafeRunSync.take(2) == List(("United Arab Emirates", "ARE"), ("United Kingdom", "GBR")))
  }


  test("implicit logging") {

    import doobie.implicits._
    import doobie.util.log.LogHandler

    implicit val han = LogHandler.jdkLogHandler

    def byName(pat: String) = {
      sql"select name, code from country where name like $pat"
        .query[(String, String)] // handler will be picked up here
        .to[List]
        .transact(xa)
    }

    assert(byName("U%").unsafeRunSync.take(2) == List(("United Arab Emirates", "ARE"), ("United Kingdom", "GBR")))
  }

}


