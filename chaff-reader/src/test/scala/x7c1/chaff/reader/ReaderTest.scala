package x7c1.chaff.reader

import org.scalatest.{FlatSpecLike, Matchers}

import scala.collection.mutable.ArrayBuffer

class ReaderTest extends FlatSpecLike with Matchers {

  val getLength: Reader[String, Int] = Reader(_.length)

  "Reader" can "behave like function" in {
    val length = getLength run "hello"
    length shouldBe 5
  }

  it should "have map" in {
    val doubled = getLength map (_ * 2)
    doubled run "hello" shouldBe 10
  }

  it should "have flatMap" in {
    val doubled = getLength flatMap (n => Reader(_ => n * 2))
    doubled run "hello" shouldBe 10
  }

  "Reader[A, Unit]" should "have append" in {
    val buffer = ArrayBuffer[Int]()
    val print: Int => Unit = buffer += _
    val store1 = getLength map (n => print(n))
    val store2 = getLength map (n => print(n * 2))

    store1 append store2 run "hello"
    buffer shouldBe Seq(5, 10)
  }

  "Seq[Reader[A, Unit]]" should "have uniteAll" in {
    val buffer = ArrayBuffer[Int]()
    val print: Int => Unit = buffer += _
    val store1 = getLength map (n => print(n))
    val store2 = getLength map (n => print(n * 2))

    Seq(store1, store2).uniteAll run "hello"
    buffer shouldBe Seq(5, 10)
  }
}
