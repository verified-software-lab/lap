package org.l4cs.cli;

import java.io.PrintStream;

import org.l4cs.pl.syntax.Formula;

public class Cnf {

	private static PrintStream out = System.out;

	public static void execute(CommandLine cl) {
		if (cl.getNumInputs() != 1)
			cl.clErr("no formula specified");
		Formula f = cl.getPLFormulaInput(0);
		Formula cnf = cl.fac.cnf(f);
		out.println(cnf);
	}

	public static void describe(CommandLine cl) {
		out.println("Usage: lap cnf <options> [<filename>]");
		out.println("Description:");
		out.println("  Converts a propositional formula to an equivalent formula in ");
		out.println("  conjunctive normal form.  By default, the formula is read from a");
		out.println("  file, specified by <filename>.  However, using options below, ");
		out.println("  this can be changed to read from stdin or to specify the ");
		out.println("  formula on the command line.  Output is sent to stdout.");
		out.println("Options:");
		out.println("  -in     : read formula from stdin");
		out.println("  -f <string>");
		out.println("          : read the formula from <string> instead of a file");
		out.println("  -v      : verbose output");
		out.println("  -plain  : restrict output to plain text");
		out.println("For formula syntax, type \"lap help formulas\".");
	}

}
