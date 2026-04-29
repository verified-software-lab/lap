package org.l4cs.cli;

import java.io.PrintStream;

import org.l4cs.pl.semantics.Model;
import org.l4cs.pl.semantics.SATSolver;
import org.l4cs.pl.semantics.Semantics;
import org.l4cs.pl.syntax.Formula;

public class Valid {

	private static PrintStream out = System.out;

	public static void execute(CommandLine cl) {
		if (cl.getNumInputs() != 1)
			cl.clErr("one formula should be specified, but saw " + cl.getNumInputs());
		Formula f = cl.getPLFormulaInput(0);
		Semantics sem = new Semantics(cl.fac);
		SATSolver solver = sem.newSolver(cl.alg);
		solver.setVerbose(cl.verbose);
		if (cl.model) {
			Model model = solver.valid(f);
			if (model == null)
				out.println("valid");
			else
				out.println(model);
		} else {
			out.println(solver.isValid(f) ? "true" : "false");
		}
	}

	public static void describe(CommandLine cl) {
		out.println("Usage: lap valid <options> [<filename>]");
		out.println("Description:");
		out.println("  Determines whether a propositional formula is valid.");
		out.println("  The specific algorithm to use can be specified.");
		out.println("  By default, the output will be either \"true\" or \"false\".");
		out.println("  However there are options to also print a model if the formula ");
		out.println("  is not valid; see below.");
		out.println("  By default, the formula is read from the specified file, but");
		out.println("  this can be changed to the command line or to read from stdin.");
		out.println("Options:");
		out.println("  -alg (brute|dpll)  [default: brute]");
		out.println("    brute : brute force, iterating over all models until a refuting");
		out.println("            model is found.");
		out.println("    dpll  : the negated formula is first converted to an equisatisfiable ");
		out.println("            formula using Tseytin's algorithm, then the DPLL algorithm ");
		out.println("            is applied;");
		out.println("  -model  : if the formula is not valid, print a refuting model.  ");
		out.println("            Otherwise, print \"valid\"");
		out.println("  -in     : read formula from stdin");
		out.println("  -f <string>");
		out.println("          : read the formula from <string> instead of a file");
		out.println("  -v      : verbose output");
		out.println("  -plain  : restrict output to plain text");
		out.println();
		out.println("For formula syntax, type \"lap help formulas\".");
	}

}
