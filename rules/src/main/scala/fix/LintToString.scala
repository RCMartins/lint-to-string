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
    doc.tree.collect {
      case t @ Term.Select(instance, Term.Name("toString")) =>
        instance.symbol.info match {
          case Some(info) =>
            info.signature match {
              case ValueSignature(TypeRef(_, symbol, _)) =>
                if (!configuration.safeTypes.contains(symbol.displayName))
                  Patch.lint(
                    Diagnostic("", s"Don't call .toString on ${symbol.displayName}", t.pos)
                  )
                else
                  Patch.empty
              case _ =>
                Patch.empty
            }
          case None =>
            Patch.empty
        }
      case t @ Term.Interpolate(Term.Name("s"), _, args) =>
        // Check types of args and lint
        args.map { arg =>
          arg.symbol.info match {
            case Some(info) =>
              info.signature match {
                case ValueSignature(TypeRef(_, symbol, _)) =>
                  if (!configuration.safeTypes.contains(symbol.displayName))
                    Patch.lint(
                      Diagnostic(
                        "",
                        s"Don't use ${symbol.displayName} on string interpolations",
                        t.pos
                      )
                    )
                  else
                    Patch.empty
                case _ =>
                  Patch.empty
              }
            case None =>
              Patch.empty
          }
        }.asPatch
      case t @ Term.ApplyInfix.After_4_6_0(_, Term.Name("+"), _, ArgClause(List(arg), _)) =>
        // Check type of arg and lint
        arg.symbol.info match {
          case Some(info) =>
            info.signature match {
              case ValueSignature(TypeRef(_, symbol, _)) =>
                if (!configuration.safeTypes.contains(symbol.displayName))
                  Patch.lint(
                    Diagnostic("", s"Don't use ${symbol.displayName} on + operator", t.pos)
                  )
                else
                  Patch.empty
              case _ =>
                Patch.empty
            }
          case None =>
            Patch.empty
        }
    }.asPatch
  }

}
