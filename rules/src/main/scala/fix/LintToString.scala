package fix

import metaconfig.Configured
import scalafix.v1._

import scala.meta.Term.ArgClause
import scala.meta._

class LintToString(configuration: LintConfiguration) extends SemanticRule("LintToString") {

  def this() = this(LintConfiguration.default)

  override def withConfiguration(config: Configuration): Configured[Rule] =
    config.conf
      .getOrElse(name.value)(LintConfiguration.default)
      .map(new LintToString(_))

  override def fix(implicit doc: SemanticDocument): Patch = {
    def isSafeError(term: Term, targetTerm: Term, message: Symbol => String): Patch =
      term.symbol.info match {
        case Some(info) =>
          info.signature match {
            case ValueSignature(TypeRef(_, symbol, _)) =>
              if (!configuration.safeTypes.contains(symbol.normalized.toString()))
                Patch.lint(Diagnostic("", message(symbol), targetTerm.pos))
              else
                Patch.empty
            case MethodSignature(_, _, TypeRef(_, symbol, _)) =>
              if (!configuration.safeTypes.contains(symbol.normalized.toString()))
                Patch.lint(Diagnostic("", message(symbol), targetTerm.pos))
              else
                Patch.empty
            case TypeSignature(_, TypeRef(_, symbol, _), _) =>
              if (!configuration.safeTypes.contains(symbol.normalized.toString()))
                Patch.lint(Diagnostic("", message(symbol), targetTerm.pos))
              else
                Patch.empty
            case _ =>
              Patch.empty
          }
        case None =>
          Patch.empty
      }

    doc.tree.collect {
      case t @ Term.Select(instance, Term.Name("toString")) =>
        isSafeError(instance, t, symbol => s"Don't call .toString on ${symbol.normalized}")
      case Term.Interpolate(Term.Name("s"), _, args) =>
        args.map { arg =>
          isSafeError(
            arg,
            arg,
            symbol => s"Don't use ${symbol.normalized} on string interpolations"
          )
        }.asPatch
      case Term.ApplyInfix.After_4_6_0(_, Term.Name("+"), _, ArgClause(List(arg), _)) =>
        isSafeError(arg, arg, symbol => s"Don't use ${symbol.normalized} on + operator")
    }.asPatch
  }

}
