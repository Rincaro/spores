package scala.spores
package run
package basic

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(classOf[JUnit4])
class BasicSpec {
  @Test
  def `simple spore transformation`() {
    val v1 = 10
    val s: Spore[Int, String] = spore {
      val c1 = v1
      (x: Int) => s"arg: $x, c1: $c1"
    }

    assert(s(20) == "arg: 20, c1: 10")
  }
}

// this is just to test that `super` is judged by the framework as a stable path
class SuperTest {
  val name = "super test"
}

package stablePathPkg {
  object StablePathObj {
    val kitteh = "i can haz stable path"
  }
}

@RunWith(classOf[JUnit4])
class StablePathSpec extends SuperTest {
  override val name = "stable path spec"
  val v0 = 12

  @Test
  def `can capture this in a stable path`() {
    val s: Spore[Int, String] = spore {
      (x: Int) => s"${capture(this.v0)}"
    }

    assert(s(42) == "12")
  }

  // we can't seem to have a super in paths because of S-1938, pity
  // https://issues.scala-lang.org/browse/SI-1938
  // @Test
  // def `can capture super in a stable path`() {
  //   val s: Spore[Int, String] = spore {
  //     (x: Int) => s"arg: $x, c1: ${capture(super.name)}"
  //   }

  //   assert(s(20) == "arg: 20, c1: super test")
  // }

  @Test
  def `can capture an innocuous simple stable path`() {
    object Innocuous {
      val cute = "fluffy"
    }
    val s: Spore[Int, String] = spore {
      (x: Int) => s"${capture(Innocuous.cute)}"
    }

    assert(s(42) == "fluffy")
  }

  @Test
  def `can capture an innocuous stable path in a package`() {
    val s: Spore[Int, String] = spore {
      (x: Int) => s"${capture(stablePathPkg.StablePathObj.kitteh)}"
    }

    assert(s(42) == "i can haz stable path")
  }
}