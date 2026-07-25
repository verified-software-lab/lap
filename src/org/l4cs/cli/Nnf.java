package org.l4cs.cli;

import org.l4cs.pl.syntax.Formula;
import org.l4cs.util.Block;

public class Nnf extends Command {

	public Nnf(CommandLine cl) {
		super(cl);
	}

	@Override
	public String name() {
		return "lap nnf";
	}

	@Override
	public String shortDescription() {
		return "convert a propositional formula to negation normal form";
	}

	@Override
	public String synopsis() {
		return bf("lap nnf") + " [ " + ul("options") + " ] [ " + ul("file") + " ]";
	}

	@Override
	public Block description() {
		Block block1 = par(
				"Converts an arbitrary propositional logic (PL) formula into an equivalent formula "
						+ "in negation normal form. ",
				"By default, the formula is read from the file named ", ul("file"), ".  However, using options below, ",
				"this can be changed to read from stdin or to specify the ",
				"formula on the command line.  Output is sent to stdout.");
		Block block2 = sub(bf("Options:"),
				seq(optEncoding(), optFormula(), optHighlight(), optIn(), optPlain(), optVerbose()));
		Block block3 = par("For formula syntax, type ", bf("lap help formulas"), ".");
		return seq(block1, block2, block3);
	}

	@Override
	public void execute() {
		if (cl.getNumInputs() != 1)
			cl.clErr("no file or formula specified");
		Formula f = cl.getPLFormulaInput(0);
		Formula nnf = cl.verbose ? cl.fac.nnfv("", f) : cl.fac.nnf(f);
		out.println(nnf);
		// out.println(nnf + " "); // to cover up the ^D if stdin was used
	}

}
