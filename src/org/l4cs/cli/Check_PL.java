package org.l4cs.cli;

import org.l4cs.cli.CommandLine.View;
import org.l4cs.pl.nd.Derivation;
import org.l4cs.pl.nd.Violation;
import org.l4cs.util.Block;

public class Check_PL extends Command {

	public Check_PL(CommandLine cl) {
		super(cl);
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
		return bf("lap check") + " [ " + ul("options") + " ] [ " + ul("file") + " ]";
	}

	@Override
	public Block description() {
		Block block1 = par("Checks a natural deduction derivation. ",
				"By default, the derivation is read from the file named ", ul("file"),
				".  However, using options below, ", "this can be changed to read from stdin or to specify the ",
				"derivation on the command line.  Output is sent to stdout.");
		Block block2 = sub(bf("Options:"), seq(optEncoding(), optHighlight(), optIn(), optLang(), optNumber(),
				optPlain(), optVerbose(" This option implies " + bf("--view=all") + "."), optView()));
		Block block3 = par("For derivation syntax, type ", bf("lap help derivations"), ".");
		return seq(block1, block2, block3);
	}

	@Override
	public void execute() {
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

}
