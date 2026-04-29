package org.l4cs.cli;

import java.io.PrintStream;

import org.l4cs.pl.semantics.SATSolver;
import org.l4cs.pl.semantics.Semantics;
import org.l4cs.pl.syntax.Formula;

public class Equiv {

	private static PrintStream out = System.out;

	public static void execute(CommandLine cl) {
		if (cl.getNumInputs() != 2)
			cl.clErr("two formulas should be specified, but saw " + cl.getNumInputs());
		Formula f1 = cl.getPLFormulaInput(0);
		Formula f2 = cl.getPLFormulaInput(1);
		Semantics sem = new Semantics(cl.fac);
		SATSolver solver = sem.newSolver(cl.alg);
		solver.setVerbose(cl.verbose);
		out.println(solver.equiv(f1, f2) ? "true" : "false");
	}

	public static void describe(CommandLine cl) {
		out.println("Usage: lap equiv <options> [<filename1>] [<filename2>]");
		out.println("Description:");
		out.println("  Determines whether two propositional formulas are equivalent.");
		out.println("  The specific algorithm to use can be specified.");
		out.println("  The output is either \"true\" or \"false\".");
		out.println("  By default, formulas are read from the specified files, but");
		out.println("  this can be changed to the command line or to read from stdin.");
		out.println("Options:");
		out.println("  -alg (brute|dpll)  [default: brute]");
		out.println("    brute : brute force, iterating over all models until a refuting");
		out.println("            model is found.");
		out.println("    dpll  : use DPLL algorithm");
		out.println("  -in     : read formula from stdin");
		out.println("  -f <string>");
		out.println("          : read formula from <string>");
		out.println("  -v      : verbose output");
		out.println("  -plain  : restrict output to plain text");
		out.println();
		out.println("For formula syntax, type \"lap help formulas\".");
		out.println("Note: this command consumes two formulas.  Each formula");
		out.println("can be specified in any of three ways (file, string, or stdin),");
		out.println("for a total of 9 different possibilities.");
		out.println("Examples: ");
		out.println("  lap equiv -f 'p&q' -f 'q&p'");
		out.println("  lap equiv -in -f 'q&p'");
		out.println("  lap equiv -f 'p&q' file2.txt");
		out.println("  lap equiv -in -in");
	}

}
