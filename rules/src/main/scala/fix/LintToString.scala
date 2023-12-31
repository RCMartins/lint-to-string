package fix

import metaconfig.Configured
import scalafix.v1._

import scala.meta._

class LintToString(configuration: LintConfiguration) extends SemanticRule("LintToString") {

  def this() = this(LintConfiguration.default)

  override def withConfiguration(config: Configuration): Configured[Rule] =
    config.conf
      .getOrElse(name.value)(LintConfiguration.default)
      .map(new LintToString(_))

  private val allSafeTypes: List[String] =
    configuration.defaultSafeTypes ++ configuration.safeTypes

  override def fix(implicit doc: SemanticDocument): Patch =
    doc.tree.collect {
      case t @ Term.Select(instance, Term.Name("toString")) =>
        isSafeError(
          instance,
          t,
          symbol => s"Don't call .toString on ${symbol.normalized}",
          ignoreStrings = false,
        )
      case Term.Interpolate(Term.Name("s"), _, args) =>
        args.map { arg =>
          isSafeError(
            arg,
            arg,
            symbol => s"Don't use ${symbol.normalized} on string interpolations",
            ignoreStrings = true,
          )
        }.asPatch
      case term @ Term.ApplyInfix.After_4_6_0(_, Term.Name("+"), _, Term.ArgClause(List(arg), _)) =>
        if (SymbolMatcher.exact("java/lang/String#`+`().").matches(term.symbol))
          isSafeError(
            arg,
            arg,
            symbol => s"Don't use ${symbol.normalized} on + operator",
            ignoreStrings = true,
          )
        else
          Patch.empty
    }.asPatch

  private def isSafeError(
      term: Term,
      targetTerm: Term,
      message: Symbol => String,
      ignoreStrings: Boolean,
  )(implicit doc: SemanticDocument): Patch = {
    def check(symbol: Symbol): Patch =
      if (isString(symbol) && ignoreStrings)
        Patch.empty
      else if (allSafeTypes.contains(symbol.normalized.toString()))
        Patch.empty
      else
        Patch.lint(Diagnostic("", message(symbol), targetTerm.pos))

    term.symbol.info match {
      case Some(info) =>
        info.signature match {
          case ValueSignature(TypeRef(_, symbol, _))        => check(symbol)
          case MethodSignature(_, _, TypeRef(_, symbol, _)) => check(symbol)
          case TypeSignature(_, TypeRef(_, symbol, _), _)   => check(symbol)
          case _                                            => Patch.empty
        }
      case None =>
        Patch.empty
    }
  }

  private def isString(symbol: Symbol): Boolean = {
    val t: String = symbol.normalized.toString()
    t == "java.lang.String." ||
    t == "scala.Predef.String." ||
    t == "scala.String."
  }

}
