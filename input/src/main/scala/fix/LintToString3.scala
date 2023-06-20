/*
rule = LintToString
LintToString.safeTypes = []
 */
package fix

object LintToString3 {
  def testMethod(): Unit = {
    val str = "some"
    val str2 = "thing"
    println(s"${str}_$str2") // safe because it's strings

    println(1 + 2) // safe because it's just numbers
    println("foo" + "bar") // safe because it's strings
    println(1 + "foo") // safe because it's strings
  }
}
