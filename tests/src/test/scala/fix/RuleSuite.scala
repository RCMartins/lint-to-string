package fix

import org.scalatest.wordspec.AnyWordSpecLike
import scalafix.testkit.AbstractSemanticRuleSuite

class RuleSuite extends AbstractSemanticRuleSuite with AnyWordSpecLike {
  runAllTests()
}
