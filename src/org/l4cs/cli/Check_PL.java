package org.l4cs.cli;

import java.io.PrintStream;

import org.l4cs.cli.CommandLine.View;
import org.l4cs.pl.nd.Derivation;
import org.l4cs.pl.nd.Violation;
import org.l4cs.util.Block;
import org.l4cs.util.TextUtil;

public class Check_PL extends Command {

	private static PrintStream out = System.out;

	public static void execute(CommandLine cl) {
		if (cl.getNumInputs() != 1)
			cl.clErr("expected one derivation specified but saw " + cl.getNumInputs());
		try {
			Derivation der = cl.getPLDerivationInput(0);
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
			if (cl.view == View.TREE || cl.view == View.ALL) {
				out.println();
				der.printTree(out, cl.number);
			}
			if (cl.view == View.HIERARCHY || cl.view == View.ALL) {
				out.println();
				der.printHierarchy(out, cl.number);
			}
			if (cl.view == View.FITCH || cl.view == View.ALL) {
				out.println();
				der.printFitch(out);
			}
		} catch (Violation v) {
			out.println("false");
			if (cl.verbose) {
				v.print(out);
			}
		}
	}

	public static void describe(CommandLine cl) {
		out.println("Usage: lap check <options> [<filename>]");
		out.println("Description:");

		StringBuilder sb = new StringBuilder();
		sb.append("Checks a natural deduction derivation. ");
		sb.append("By default, the derivation is read from a ");
		sb.append("file, specified by <filename>.  However, using options below, ");
		sb.append("this can be changed to read from stdin or to specify the ");
		sb.append("derivation on the command line.  Output is sent to stdout.");
		sb = TextUtil.wrap(5, sb);
		out.print(sb.toString());

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
		out.println("For derivation syntax, type \"lap help derivations\".");
	}

	@Override
	public String name() {
		return "lap check";
	}

	@Override
	public String shortDescription() {
		return "check a natural deduction derivation";
	}

	@Override
	public String synopsis() {
		return TextUtil.bold("lap check") + " [ " + TextUtil.underline("options") + " ] [ " + TextUtil.underline("file")
				+ " ]";
	}

	@Override
	public Block description() {
		Block block1 = par("Checks a natural deduction derivation. ",
				"By default, the derivation is read from the file named ", TextUtil.underline("file"),
				".  However, using options below, ", "this can be changed to read from stdin or to specify the ",
				"derivation on the command line.  Output is sent to stdout.");

		Block block2 = sub(bf("Options:"), seq(optEncoding(), optHighlight(), optIn(""), optLang(), optNumber(),
				optPlain(), optVerbose(" This option implies " + bf("--view=all") + "."), optView()));

		Block block3 = par("For derivation syntax, type ", bf("lap help derivations"), ".");
		return seq(block1, block2, block3);
	}

}
