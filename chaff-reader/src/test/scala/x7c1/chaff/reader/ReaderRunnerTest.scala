package x7c1.chaff.reader

import org.scalatest.{FlatSpecLike, Matchers}

import scala.collection.mutable

class ReaderRunnerTest extends FlatSpecLike with Matchers  {

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
    ReaderRunner.traverse(node){
      label => buffer += label
    }
    buffer shouldBe "A,C,E,D,B,H,I,G,F".split(",")
  }

}
