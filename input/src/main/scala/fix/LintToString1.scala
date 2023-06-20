/*
rule = LintToString
 */
package fix

object LintToString1 {
  def testMethod(): Unit = {
    val a = Option("string")
    println(a.toString) // assert: LintToString

    val b = 5
    println(b.toString) // Safe toString

    val c = "string"
    println(c + a) // assert: LintToString

    println(s"$a") // assert: LintToString
  }
}
