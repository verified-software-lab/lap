package org.l4cs.cli;

import java.io.PrintStream;

import org.l4cs.pl.syntax.Formula;

public class Tseytin {

	private static PrintStream out = System.out;

	public static void execute(CommandLine cl) {
		if (cl.getNumInputs() != 1)
			cl.clErr("no formula specified");
		Formula f = cl.getPLFormulaInput(0);
		Formula cnf = cl.fac.tseytin(f);
		out.println(cnf);
	}

	public static void describe(CommandLine cl) {
		out.println("Usage: lap tseytin <options> [<filename>]");
		out.println("Description:");
		out.println("  Converts a propositional formula to an equisatisfiable formula");
		out.println("  in conjunctive normal form, using Tseytin's algorithm.");
		out.println("  By default, the formula is read from a");
		out.println("  file, specified by <filename>.  However, using options below, ");
		out.println("  this can be changed to read from stdin or to specify the ");
		out.println("  formula on the command line.  Output is sent to stdout.");
		out.println("Options:");
		out.println("  -in     : read formula from stdin");
		out.println("  -f <string>");
		out.println("          : read the formula from <string> instead of a file");
		out.println("  -v      : verbose output");
		out.println("  -plain  : restrict output to plain text");
		out.println();
		out.println("For formula syntax, type \"lap help formulas\".");
	}

}
