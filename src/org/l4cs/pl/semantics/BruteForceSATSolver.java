package org.l4cs.pl.semantics;

import org.l4cs.pl.syntax.Formula;
import org.l4cs.pl.syntax.FormulaFactory;

/**
 * A SAT solver which works by "brute force": iterating over all models
 * (restricted to the variables occurring in the given formula) until it finds
 * one that satisfies the formula or exhausts all models.
 * 
 * @author Stephen Siegel
 */
public class BruteForceSATSolver extends SATSolver {

	/**
	 * Constructs new BruteForceSATSolver which can be used repeatedly to solve SAT
	 * problems.
	 * 
	 * @param fact the formula factory that will be used to extract information from
	 *             formulas
	 */
	public BruteForceSATSolver(FormulaFactory fac) {
		super(fac);
	}

	@Override
	public Model sat(Formula f) {
		sayLine("Formula: "+f);
		RestrictedModel model = new RestrictedModel(fac, fac.vars(f));
		do {
			say("Evaluating formula at model " + model+".  ");
			if (model.eval(f)) {
				sayLine("Result: true");
				return model;
			} else {
				sayLine("Result: false");
			}
		} while (model.next());
		return null;
	}
}
