/*
rule = LintToString
 */
package fix

object LintToString1 {
  def testMethod(): Unit = {
    val a = Option("string")
    println(a.toString) // assert: LintToString
    println(a.toIndexedSeq.toString) // assert: LintToString

    val b = 5
    println(b.toString) // Safe toString
    println(b.toLong.toString) // Safe toString
    println(b.toByte.toString) // Safe toString
    println(b.toShort.toString) // Safe toString
    println(b.toFloat.toString) // Safe toString
    println(b.toDouble.toString) // Safe toString
    println('a'.toString) // Safe toString
    println(true.toString) // Safe toString

    new Exception().toString // assert: LintToString

    val c = "string"
    println(c + a) // assert: LintToString

    println(s"$a") // assert: LintToString
  }
}
