package x7c1.chaff.reader

import org.scalatest.{FlatSpecLike, Matchers}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class ReaderRunnerTest extends FlatSpecLike with Matchers {

  "ReaderRunner" can "traverse nodes by post-order" in {
    val node = ReaderNodeSample(
      label = "F",
      left = Some(ReaderNodeSample(
        label = "B",
        left = Some(ReaderNodeSample(
          label = "A"
        )),
        right = Some(ReaderNodeSample(
          label = "D",
          left = Some(ReaderNodeSample("C")),
          right = Some(ReaderNodeSample("E"))
        ))
      )),
      right = Some(ReaderNodeSample(
        label = "G",
        right = Some(ReaderNodeSample(
          label = "I",
          left = Some(ReaderNodeSample("H"))
        ))
      ))
    )
    val buffer = mutable.ArrayBuffer[String]()
    ReaderRunner.traverse(node) {
      label => buffer += label
    }
    buffer shouldBe "A,C,E,D,B,H,I,G,F".split(",")
  }

  it can "traverse Readers[A, Unit] in order" in {
    val observer = ArrayBuffer[String]()

    def dispatch(n: Int) = Reader[String, Unit] {
      s => observer += s"$s$n"
    }

    val reader = for {
      _ <- dispatch(10) append dispatch(12) append (dispatch(14) append dispatch(16))
      _ <- dispatch(20)
      _ <- dispatch(30) append (dispatch(32) append dispatch(34)) append dispatch(36)
    } yield ()

    reader run "x"
    observer shouldBe "x10,x12,x14,x16,x20,x30,x32,x34,x36".split(",")
  }

  it can "traverse Readers[A, Int] in order" in {
    val observer = ArrayBuffer[Int]()

    def dispatch(n: Int) = Reader[Int, Int] { i =>
      val result = n + i
      observer += result
      result
    }

    val reader = for {
      x1 <- dispatch(10)
      x2 <- dispatch(20)
    } yield {
      x1 + x2
    }

    reader run 1 shouldBe 32
    observer shouldBe Seq(11, 21)

    reader run 3 shouldBe 36
    observer shouldBe Seq(11, 21, 13, 23)
  }

}
