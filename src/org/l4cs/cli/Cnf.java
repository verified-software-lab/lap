package org.l4cs.cli;

import org.l4cs.pl.syntax.Formula;
import org.l4cs.util.Block;

public class Cnf extends Command {

	public Cnf(CommandLine cl) {
		super(cl);
	}

	@Override
	public String name() {
		return "lap cnf";
	}

	@Override
	public String shortDescription() {
		return "convert a propositional formula to conjunctive normal form";
	}

	@Override
	public String synopsis() {
		return bf("lap cnf") + " [ " + ul("options") + " ] [ " + ul("file") + " ]";
	}

	@Override
	public Block description() {
		Block block1 = par(
				"Converts an arbitrary propositional logic (PL) formula into an equivalent formula "
						+ "in conjunctive normal form. ",
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
		Formula cnf = cl.fac.cnf(f);
		out.println(cnf);
	}

}
