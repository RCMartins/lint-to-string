package fix

import metaconfig.{ConfDecoder, Configured}

case class LintConfiguration(
    defaultSafeTypes: List[String],
    safeTypes: List[String],
)

object LintConfiguration {
  val default: LintConfiguration =
    LintConfiguration(
      defaultSafeTypes = List(
        "scala.Int.",
        "scala.Boolean.",
        "scala.Byte.",
        "scala.Short.",
        "scala.Long.",
        "scala.Float.",
        "scala.Double.",
        "scala.Char."
      ),
      safeTypes = Nil,
    )

  implicit val decoder: ConfDecoder[LintConfiguration] =
    ConfDecoder.from[LintConfiguration] { c =>
      Configured.Ok(
        LintConfiguration(
          c.get[List[String]]("defaultSafeTypes").getOrElse(default.defaultSafeTypes),
          c.get[List[String]]("safeTypes").getOrElse(default.safeTypes),
        )
      )
    }
}
