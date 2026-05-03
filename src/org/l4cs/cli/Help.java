package org.l4cs.cli;

import org.l4cs.fol.nd.FOLRule;
import org.l4cs.pl.nd.Rule;
import org.l4cs.util.Block;
import org.l4cs.util.TextUtil;

public class Help extends Command {

	public Help(CommandLine cl) {
		super(cl);
	}

	private void formulas_PL() {
		out.println("PL Formula syntax:");
		out.println();
		out.println("Propositions are identifiers as in C or Java programs.");
		out.println("An identifier starts with a letter or underscore, which is");
		out.println("followed by any number of letters, digits, and underscores");
		out.println();
		out.println("Connectives and primitives can be represented in multiple ways:");
		out.println("  - NOT     : NOT ! " + TextUtil.NOT);
		out.println("  - AND     : AND & &&" + TextUtil.AND);
		out.println("  - OR      : OR | ||" + TextUtil.OR);
		out.println("  - IMPLIES : IMPLIES -> " + TextUtil.IMPLIES);
		out.println("  - IFF     : IFF <-> " + "\u2194");
		out.println("  - FALSE   : false " + TextUtil.BOT);
		out.println("  - TRUE    : true " + TextUtil.TOP);
		out.println();
		out.println("Operator precedence (highest first): NOT, AND, OR, IMPLIES, IFF.");
		out.println("Parentheses (\"(...)\") can also be used for grouping.");
	}

	private void formulas_FOL() {
		out.println("FOL Formula syntax:");
		out.println();
		out.println("Identifiers are as in C or Java programs.");
		out.println("An identifier starts with a letter or underscore, which is");
		out.println("followed by any number of letters, digits, and underscores");
		out.println();
		out.println("Vocabulary conventions:");
		out.println("  - Constants must be explicitly declared using \"const ...\".");
		out.println("  - Any identifier can be used as a constant, predicate, or function symbol.");
		out.println("  - Functions and predicates are ALWAYS immediately followed by '('; variables NEVER are.");
		out.println("  - Functions occur ONLY in term context, whereas predicates occur ONLY in formula context.");
		out.println();
		out.println("Connectives and primitives can be represented in multiple ways:");
		out.println("  - FORALL  : FORALL forall \u2200");
		out.println("  - EXISTS  : EXISTS exists \u2203");
		out.println("  - NOT     : NOT ! " + TextUtil.NOT);
		out.println("  - AND     : AND & &&" + TextUtil.AND);
		out.println("  - OR      : OR | ||" + TextUtil.OR);
		out.println("  - IMPLIES : IMPLIES -> " + TextUtil.IMPLIES);
		out.println("  - IFF     : IFF <-> " + "\u2194");
		out.println("  - FALSE   : false " + TextUtil.BOT);
		out.println("  - TRUE    : true " + TextUtil.TOP);
		out.println();
		out.println("Operator precedence (highest first): NOT, AND, OR, IMPLIES, IFF, Quantifiers.");
		out.println("Note: Quantifiers have low precedence and MUST be followed by '.' after the variable.");
		out.println("Example: forall x. P(x) & Q(x) means forall x. (P(x) & Q(x))");
		out.println("Parentheses (\"(...)\") can also be used for grouping.");
	}

	private void derivations_PL() {
		out.println("PL Derivation syntax: ");
		out.println();
		out.println("The sequent symbol is denoted |- or ⊢");
		out.println("Inference rules:");
		out.println();

		org.l4cs.pl.syntax.FormulaFactory fac = new org.l4cs.pl.syntax.FormulaFactory();
		org.l4cs.pl.nd.DerivationFactory df = new org.l4cs.pl.nd.DerivationFactory(fac);
		for (Rule rule : df.rules()) {
			rule.printDescription(out);
			out.println();
		}
		out.println("A derivation is expressed as a sequence of steps.");
		out.println("Each step begins with a number, followed by '.'.");
		out.println("This is followed by a sequent: a comma-separated list of");
		out.println("formulas, followed by the sequent symbol, followed by a ");
		out.println("formula.   This is followed by the name of a rule in ");
		out.println("parentheses, e.g. \"(RAA)\".");
		out.println("If the rule has premises, this is followed by a ");
		out.println("comma-separated list of numbers, the line numbers of the");
		out.println("premises.  Finally, the step is terminated by ';'.");
		out.println("White space is ignored.");
		out.println();
		out.println("Each step has the form:");
		out.println("  <label>. <sequent> (<rule>) <premises>;");
		out.println();
		out.println("Example step:");
		out.println("  3. p, p -> q |- q (E->) 1, 2;");
		out.println();
		formulas_PL();
	}

	private void derivations_FOL() {
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
			new Check_PL(cl).man(out);
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
