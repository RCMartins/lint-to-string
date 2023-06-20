/*
rule = LintToString
LintToString.defaultSafeTypes = []
 */
package fix

object LintToString4 {
  def testMethod(): Unit = {
    // unsafe because the default safe types were overridden
    val b = 5
    println(b.toString) // assert: LintToString
  }
}
