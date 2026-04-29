package org.l4cs.cli;

import java.io.PrintStream;

import org.l4cs.fol.nd.FOLRule;
import org.l4cs.pl.nd.Rule;
import org.l4cs.util.TextUtil;

public class Help {

	private static PrintStream out = System.out;

	private static void usage_PL() {
		out.println("lap: Logic, Algorithms, Proof tool (language: Propositional Logic)");
		out.println("Usage: lap <command> ...");
		out.println("Commands: ");
		out.println("  help    - print usage information");
		out.println("  nnf     - convert a propositional formula to negation normal form");
		out.println("  cnf     - convert a propositional formula to conjunctive normal form");
		out.println("  dnf     - convert a propositional formula to disjunctive normal form");
		out.println("  tseytin - convert a propositional formula to an equisatisfiable CNF formula");
		out.println("  dpll    - apply the DPLL algorithm to a CNF formula");
		out.println("  sat     - determine if a propositional formula is satisfiable");
		out.println("  valid   - determine if a propositional formula is valid (a tautology)");
		out.println("  equiv   - determine whether two propositional formulas are equivalent");
		out.println("  check   - check a natural deduction derivation for PL or FOL");
		out.println();
		out.println("Type \"lap help <command>\" for detailed help on a specific command.");
		out.println("Type \"lap help formulas\" for formula syntax.");
		out.println("Type \"lap help derivations\" for derivation syntax.");
		out.println("Insert \"-lang <language>\" after \"help\" to specify language.");
		out.println("Languages: pl (default), fol.");
	}

	private static void usage_FOL() {
		out.println("lap: Logic, Algorithms, Proof tool (language: First Order Logic)");
		out.println("Usage: lap <command> ...");
		out.println("Commands: ");
		out.println("  help    - print usage information");
		out.println("  check   - check a natural deduction derivation for PL or FOL");
		out.println();
		out.println("Type \"lap help <command>\" for detailed help on a specific command.");
		out.println("Type \"lap help formulas\" for formula syntax.");
		out.println("Type \"lap help derivations\" for derivation syntax.");
		out.println("Insert \"-lang <language>\" after \"help\" to specify language.");
		out.println("Languages: pl (default), fol.");
	}

	private static void formulas_PL(CommandLine cl) {
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

	private static void formulas_FOL(CommandLine cl) {
		// ADDED: FOL syntax based on PL counterpart and our explicit conventions
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
		//out.println("  - EQUALS  : =");
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

	private static void derivations_PL(CommandLine cl) {
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
		formulas_PL(cl);
	}

	private static void derivations_FOL(CommandLine cl) {
		// ADDED: FOL derivation syntax mirroring the PL counterpart. 
		out.println("FOL Derivation syntax: ");
		out.println();
		out.println("The sequent symbol is denoted |- or ⊢");
		out.println("Inference rules:");
		out.println();

		// ADDED: Mirrored the PL factory loading for FOL
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
		formulas_FOL(cl); // MODIFIED: Call formulas_FOL instead of formulas_PL
	}

	private static void execute_PL(CommandLine cl) {
		if (cl.helpCommand == null) {
			usage_PL();
			return;
		}
		switch (cl.helpCommand) {
		case "help":
			usage_PL();
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
			Check_PL.describe(cl);
			break;
		case "formulas":
			formulas_PL(cl);
			break;
		case "derivations":
			derivations_PL(cl);
			break;
		default:
			cl.clErr("Unknown command: " + cl.helpCommand);
		}
	}

	private static void execute_FOL(CommandLine cl) {
		if (cl.helpCommand == null) {
			usage_FOL();
			return;
		}
		switch (cl.helpCommand) {
		case "help":
			usage_FOL();
			break;
		case "check":
			Check_FOL.describe(cl);
			break;
		case "formulas":
			formulas_FOL(cl);
			break;
		case "derivations":
			derivations_FOL(cl);
			break;
		default:
			cl.clErr("Unknown command: " + cl.helpCommand);
		}
	}

	public static void execute(CommandLine cl) {
		switch (cl.lang) {
		case PL:
			execute_PL(cl);
			break;
		case FOL:
			execute_FOL(cl);
			break;
		default:
			throw new RuntimeException("unreachable");
		}
	}
}
