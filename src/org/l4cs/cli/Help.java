package org.l4cs.cli;

import static org.l4cs.util.TextUtil.Encoding.UNICODE;

import org.l4cs.fol.nd.FOLRule;
import org.l4cs.pl.nd.DerivationFactory;
import org.l4cs.pl.nd.Rule;
import org.l4cs.pl.syntax.FormulaFactory;
import org.l4cs.util.Block;
import org.l4cs.util.TextUtil;

public class Help extends Command {

	public Help(CommandLine cl) {
		super(cl);
	}

	private void formulas_PL() {
		boolean uni = TextUtil.encoding() == UNICODE;
		sub(bf("LAP Formula Syntax: Propositional Logic"), //
				seq(par("Propositions are identifiers as in C or Java programs. ",
						"An identifier starts with a letter or underscore, which is ",
						"followed by any number of letters, digits, and underscores."), //
						cat(par("Connectives and primitives can be represented in multiple ways. ",
								"The following list shows all of the ways, and is ordered by ",
								"operator precedence, from highest to lowest:"),
								fix(bf("  NOT     !    " + (uni ? TextUtil.NOT : ""))),
								fix(bf("  AND     &    &&   " + (uni ? TextUtil.AND : ""))),
								fix(bf("  OR      |    ||   " + (uni ? TextUtil.OR : ""))),
								fix(bf("  IMPLIES ->   " + (uni ? TextUtil.IMPLIES : ""))),
								fix(bf("  IFF     <->  " + (uni ? TextUtil.IFF : ""))),
								fix(bf("  false   " + (uni ? TextUtil.BOT : ""))),
								fix(bf("  true    " + (uni ? TextUtil.TOP : "")))),
						par("Binary operators are right associative.  ",
								"Parentheses " + bf("(...)") + " can also be used for grouping.  ",
								"White space is ignored (except to separate keywords and identifiers).")))
				.print(out);
	}

	private void formulas_FOL() {
		boolean uni = TextUtil.encoding() == UNICODE;
		sub(bf("LAP Formula Syntax: First Order Logic"), //
				seq(//
						par("Constants, predicate symbols, and function symbols are identifiers ",
								"as in C or Java programs. ", //
								"An identifier starts with a letter or underscore, which is ",
								"followed by any number of letters, digits, and underscores.  ",
								"Constants must be declared before they are used.  A declaration ",
								"consists of the keyword " + bf("const") + " followed by a comma-separated ",
								"list of identifiers, followed by a semicolon (" + bf(";") + ").  ", "Example: "),
						fix("  " + bf("const c,d,e;")), //
						cat(par("Connectives and primitives can be represented in multiple ways.  ",
								"The following list shows all the ways and also the operator precedence, ",
								"ordered from highest to lowest. "),
								fix(bf("  NOT     !     " + (uni ? TextUtil.NOT : ""))),
								fix(bf("  AND     &     &&   " + (uni ? TextUtil.AND : ""))),
								fix(bf("  OR      |     ||   " + (uni ? TextUtil.OR : ""))),
								fix(bf("  IMPLIES ->    " + (uni ? TextUtil.IMPLIES : ""))),
								fix(bf("  IFF     <->   " + (uni ? TextUtil.IFF : ""))),
								fix(bf("  FORALL forall " + (uni ? TextUtil.FORALL : ""))),
								fix(bf("  EXISTS exists " + (uni ? TextUtil.EXISTS : ""))),
								fix(bf("  false   " + (uni ? TextUtil.BOT : ""))),
								fix(bf("  true    " + (uni ? TextUtil.TOP : "")))),
						par("A quantified formula consists of a quantifier, followed by a variable, ",
								"followed by a dot, followed by a formula. ", //
								"Example: "),
						fix("  " + bf("FORALL x . P(x)")), //
						par("Binary operators are right associative.  ",
								"Parentheses " + bf("(...)") + " can also be used for grouping.  ",
								"White space is ignored (except to separate keywords and identifiers).")))
				.print(out);
	}

	private void derivations_PL() {
		FormulaFactory fac = new FormulaFactory();
		DerivationFactory df = new DerivationFactory(fac);
		boolean uni = TextUtil.encoding() == UNICODE;
		Block par1 = par("In the Natural Deduction proof system, judgments are ", bf("sequents"), ".  ",
				"A sequent consists of a comma-separated list of formulas (possibly empty), ",
				"followed by the sequent symbol, followed by a formula. ",
				"The list preceding the sequent symbol denotes a set of formulas ", "known as the sequent's ",
				bf("antecedent"), ".  ", "The formula occurring after the sequent symbol is the ", bf("succedent"),
				". ", "The sequent symbol is denoted " + bf("|- ") + (uni ? "or " + bf(TextUtil.infers()) + " " : ""),
				". ", "For formula syntax, type ", bf("lap help formulas"), ".");
		Block par2 = cat(
				par("A derivation is expressed as a sequence of steps. ", "Each step consists of a number (the step's ",
						bf("label"), "), followed by a dot (", bf("."), "), ",
						"followed by a sequent, then the name of an inference rule in parentheses, and, ",
						"if the rule has premises, a comma-separated list of the labels of the premises. ",
						"Finally, the step is terminated by a dot. ",
						"White space is ignored.  In summary, the syntax for a step is:"), //
				def("  ",
						par(ul("label"), bf(" . "), ul("sequent"), " ", bf("("), ul("rule"), bf(")"), " ",
								ul("premises"), " ", bf("."))), //
				par("An example step:"), //
				def("  ", par(TextUtil.boldOn(), "3. p, p -> q |- q (E->)1,2.", TextUtil.boldOff())));
		Block b1 = seq(//
				sub(bf("Derivation Syntax"), seq(par1, par2)), //
				sub(bf("Inference Rules"), par("")));
		b1.print(out);

		// TODO: get the rules to return blocks next...
		for (Rule rule : df.rules()) {
			rule.printDescription(out);
			out.println();
		}

	}

	private void derivations_FOL() {
		// TODO: prettify this using blocks

		out.println("FOL Derivation syntax: ");
		out.println();
		out.println("The sequent symbol is denoted |- or ⊢");
		out.println("Inference rules:");
		out.println();
		try {
			org.l4cs.fol.syntax.FOLFormulaFactory fac = new org.l4cs.fol.syntax.FOLFormulaFactory();
			org.l4cs.fol.nd.FOLDerivationFactory df = new org.l4cs.fol.nd.FOLDerivationFactory(fac);
			for (FOLRule rule : df.rules()) {
				rule.printDescription(out);
				out.println();
			}
		} catch (Exception | NoClassDefFoundError e) {
			out.println("  [Error dynamically loading FOL rules - check package configuration]");
			out.println();
		}
		out.println("FOL derivations extend PL derivations with quantifier rules and constants.");
		out.println("A FOL derivation begins with a constant declaration:");
		out.println("  const a, b, c;");
		out.println("This is followed by a sequence of steps.");
		out.println("Each step begins with a number, followed by '.'.");
		out.println("This is followed by a sequent: a comma-separated list of");
		out.println("formulas, followed by the sequent symbol, followed by a ");
		out.println("formula.   This is followed by the name of a rule in ");
		out.println("parentheses, e.g. \"(AllE)\".");
		out.println("If the rule has premises, this is followed by a ");
		out.println("comma-separated list of numbers, the line numbers of the");
		out.println("premises.  Finally, the step is terminated by ';'.");
		out.println("White space is ignored.");
		out.println();
		out.println("A FOL derivation example:");
		out.println("  const a, b, c;");
		out.println("  1. forall x. P(x) |- forall x. P(x) (Ax);");
		out.println("  2. forall x. P(x) |- P(y) (Eforall) 1;");
		out.println();
		formulas_FOL();
	}

	private void execute_PL() {
		// TODO: update all of these
		if (cl.helpCommand == null) {
			man(out);
			return;
		}
		switch (cl.helpCommand) {
		case "help":
			man(out);
			break;
		case "cnf":
			Cnf.describe(cl);
			break;
		case "dnf":
			Dnf.describe(cl);
			break;
		case "nnf":
			Nnf.describe(cl);
			break;
		case "equiv":
			Equiv.describe(cl);
			break;
		case "tseytin":
			Tseytin.describe(cl);
			break;
		case "sat":
			Sat.describe(cl);
			break;
		case "dpll":
			Dpll.describe(cl);
			break;
		case "valid":
			Valid.describe(cl);
			break;
		case "check":
			new Check_PL(cl).man(out); // this is the right way
			break;
		case "formulas":
			formulas_PL();
			break;
		case "derivations":
			derivations_PL();
			break;
		default:
			cl.clErr("Unknown command: " + cl.helpCommand);
		}
	}

	private void execute_FOL() {
		if (cl.helpCommand == null) {
			man(out);
			return;
		}
		switch (cl.helpCommand) {
		case "help":
			man(out);
			break;
		case "check":
			Check_FOL.describe(cl);
			break;
		case "formulas":
			formulas_FOL();
			break;
		case "derivations":
			derivations_FOL();
			break;
		default:
			cl.clErr("Unknown command: " + cl.helpCommand);
		}
	}

	@Override
	public void execute() {
		switch (cl.lang) {
		case PL:
			execute_PL();
			break;
		case FOL:
			execute_FOL();
			break;
		default:
			throw new RuntimeException("unreachable");
		}
	}

	@Override
	public String name() {
		return "lap";
	}

	@Override
	public String shortDescription() {
		String result = "Logic, Algorithms, Proof tool (language: ";
		switch (cl.lang) {
		case PL:
			result += "Propositional Logic";
			break;
		case FOL:
			result += "First Order Logic";
			break;
		default:
			throw new RuntimeException("unreachable");
		}
		result += ")";
		return result;
	}

	@Override
	public String synopsis() {
		return bf("lap") + " [ " + ul("command") + " ] [ " + ul("options") + " ] [ " + ul("file") + "... ]";
	}

	private Block description_PL() {
		Block commandBlock = sub(bf("Commands:"), cat( //
				def(bf("help") + "    - ", par("print usage information")), //
				def(bf("nnf") + "     - ", par("convert a propositional formula to negation normal form")), //
				def(bf("cnf") + "     - ", par("convert a propositional formula to conjunctive normal form")), //
				def(bf("dnf") + "     - ", par("convert a propositional formula to disjunctive normal form")), //
				def(bf("tseytin") + " - ", par("convert a propositional formula to an equisatisfiable CNF formula")), //
				def(bf("dpll") + "    - ", par("apply the DPLL algorithm to a CNF formula")), //
				def(bf("sat") + "     - ", par("determine if a propositional formula is satisfiable")), //
				def(bf("valid") + "   - ", par("determine if a propositional formula is valid (a tautology)")), //
				def(bf("equiv") + "   - ", par("determine whether two propositional formulas are equivalent")), //
				def(bf("check") + "   - ", par("check a natural deduction derivation"))));
		Block optBlock = sub(bf("General Options:"), cat( //
				optEncoding(), optHighlight(), optLang(), optPlain(), optVerbose(), optVersion()));
		Block langBlock = sub(bf("Languages:"), cat( //
				def(bf("pl") + "   - ", par("propositional logic (default)")), //
				def(bf("fol") + "  - ", par("first order logic"))));
		Block infoBlock = cat( //
				par("Type ", bf("lap help "), ul("command"), " for detailed help on a specific command."), //
				par("Type ", bf("lap help formulas"), " for formula syntax."), //
				par("Type ", bf("lap help derivations"), " for derivation syntax."), //
				par("Insert ", bf("-lang "), ul("language"), " after ", bf("help"), " to specify language."));
		return seq(commandBlock, optBlock, langBlock, infoBlock);
	}

	private Block description_FOL() {
		Block commandBlock = sub(bf("Commands:"), cat( //
				def(bf("help") + "    - ", par("print usage information")), //
				def(bf("check") + "   - ", par("check a natural deduction derivation"))));
		Block optBlock = sub(bf("General Options:"), cat( //
				optEncoding(), optHighlight(), optLang(), optPlain(), optVerbose(), optVersion()));
		Block langBlock = sub(bf("Languages:"), cat( //
				def(bf("pl") + "   - ", par("propositional logic (default)")), //
				def(bf("fol") + "  - ", par("first order logic"))));
		Block infoBlock = cat( //
				par("Type ", bf("lap help "), ul("command"), " for detailed help on a specific command."), //
				par("Type ", bf("lap help formulas"), " for formula syntax."), //
				par("Type ", bf("lap help derivations"), " for derivation syntax."), //
				par("Insert ", bf("-lang "), ul("language"), " after ", bf("help"), " to specify language."));
		return seq(commandBlock, optBlock, langBlock, infoBlock);
	}

	@Override
	public Block description() {
		switch (cl.lang) {
		case PL:
			return description_PL();
		case FOL:
			return description_FOL();
		default:
			throw new RuntimeException("unreachable");
		}
	}
}
