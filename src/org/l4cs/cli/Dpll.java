package org.l4cs.cli;

import java.io.PrintStream;

import org.l4cs.pl.semantics.CnfException;
import org.l4cs.pl.semantics.DPLLSATSolver;
import org.l4cs.pl.semantics.Model;
import org.l4cs.pl.semantics.Semantics;
import org.l4cs.pl.semantics.Semantics.SATAlgorithm;
import org.l4cs.pl.syntax.Formula;

public class Dpll {

	private static PrintStream out = System.out;

	public static void execute(CommandLine cl) {
		if (cl.getNumInputs() != 1)
			cl.clErr("one formula should be specified, but saw " + cl.getNumInputs());
		Formula f = cl.getPLFormulaInput(0);
		Semantics sem = new Semantics(cl.fac);
		DPLLSATSolver solver = (DPLLSATSolver) sem.newSolver(SATAlgorithm.DPLL);
		solver.setVerbose(cl.verbose);
		try {
			if (cl.model) {
				Model model = solver.sat_cnf(f);
				if (model == null)
					out.println("unsat");
				else
					out.println(model);
			} else {
				out.println(solver.sat_cnf(f) != null ? "true" : "false");
			}
		} catch (CnfException e) {
			cl.cnfErr("DPLL requires the formula to be in conjunctive normal form.");
		}
	}

	public static void describe(CommandLine cl) {
		out.println("Usage: lap dpll <options> [<filename>]");
		out.println("Description:");
		out.println("  Determines whether a CNF formula is satisfiable.");
		out.println("  The given formula must be in conjunctive normal form.");
		out.println("  By default, the output will be either \"true\" or \"false\".");
		out.println("  However there are options to also print a model if the formula ");
		out.println("  is satisfiable; see below.");
		out.println("  By default, the formula is read from the specified file, but");
		out.println("  this can be changed to the command line or to read from stdin.");
		out.println("  Output is sent to stdout.");
		out.println("Options:");
		out.println("  -model  : if the formula is satisfiable, print a model.  Otherwise, ");
		out.println("            print \"unsat\"");
		out.println("  -in     : read formula from stdin");
		out.println("  -f <string>");
		out.println("          : read the formula from <string> instead of a file");
		out.println("  -v      : verbose output");
		out.println("  -plain  : restrict output to plain text");
		out.println();
		out.println("For formula syntax, type \"lap help formulas\".");
	}

}
