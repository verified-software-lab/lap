package org.l4cs.cli;

import java.io.PrintStream;

import org.l4cs.cli.CommandLine.View;
import org.l4cs.fol.nd.FOLDerivation;
import org.l4cs.fol.nd.FOLViolation;

public class Check_FOL {

	private static PrintStream out = System.out;

	public static void execute(CommandLine cl) {
		if (cl.getNumInputs() != 1)
			cl.clErr("expected one derivation specified but saw " + cl.getNumInputs());
		// Derivation must be read
		try {
			FOLDerivation der = cl.getFOLDerivationInput(0);
			// NONE, LINEAR, TREE, HIERARCHY, FITCH, TUPLE, ALL
			out.println("true");
			if (cl.view == View.NONE) {
			}
			if (cl.verbose || cl.view == View.TUPLE || cl.view == View.ALL) {
				out.println();
				der.printTuple(out, cl.number);
				out.println();
			}
			if (cl.view == View.LINEAR || cl.view == View.ALL) {
				out.println();
				der.printLinear(out);
			}
			if (cl.view == View.HIERARCHY || cl.view == View.ALL) {
				out.println();
				der.printHierarchy(out, cl.number);
			}
			if (cl.view == View.FITCH || cl.view == View.ALL) {
				out.println();
				der.printFitch(out);
			}
			if (cl.view == View.TREE || cl.view == View.ALL) {
				out.println();
				der.printTree(out, cl.number);
			}
		} catch (FOLViolation v) {
			out.println("false");
			if (cl.verbose) {
				v.print(out);
			}
		}
	}

	public static void describe(CommandLine cl) {
		out.println("Usage: lap check <options> [<filename>]");
		out.println("Description:");
		out.println("  Checks a natural deduction derivation. ");
		out.println("  By default, the derivation is read from a");
		out.println("  file, specified by <filename>.  However, using options below, ");
		out.println("  this can be changed to read from stdin or to specify the ");
		out.println("  derivation on the command line.  Output is sent to stdout.");
		out.println("Options:");
		out.println("  -in     : read formula from stdin");
		out.println("  -f <string>");
		out.println("          : read the derivation from <string> instead of a file");
		out.println("  -v      : verbose output");
		out.println("  -plain  : restrict output to plain text");
		out.println("  -lang (pl|fol)  [default: pl]");
		out.println("          : language (Propositional Logic or First Order Logic)");
		out.println("  -view (none|linear|tree|hierarchy|fitch|tuple|all)  [default: none]");
		out.println("          : format(s) to print derivation");
		out.println("For FOL formula syntax, type \"lap help -lang fol formulas\".");
		out.println("For FOL derivation syntax, type \"lap help -lang fol derivations\".");
	}

}
