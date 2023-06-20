/*
rule = LintToString
LintToString.safeTypes = [
  "java.lang.Throwable.",
]
 */
package fix

object LintToString2 {
  def testMethod(): Unit = {
    println(new Throwable().toString) // Safe toString because Throwable is in safe types
  }
}
