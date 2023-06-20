package fix

import metaconfig.{ConfDecoder, Configured}

case class LintConfiguration(safeTypes: List[String])

object LintConfiguration {
  val default: LintConfiguration =
    LintConfiguration(
      List("Int", "Boolean", "Byte", "Short", "Long", "Float", "Double", "Char")
    )

  implicit val decoder: ConfDecoder[LintConfiguration] =
    ConfDecoder.from[LintConfiguration] { c =>
      Configured.Ok(
        LintConfiguration(c.get[List[String]]("safeTypes").getOrElse(default.safeTypes))
      )
    }
}
