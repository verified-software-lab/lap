package org.l4cs.pl.semantics;

import org.l4cs.pl.syntax.FormulaFactory;

/**
 * Semantic algorithms for propositional logic.
 * 
 * @author Stephen Siegel
 */
public class Semantics {

	/**
	 * An enumeration of the different SAT algorithms provided by this module.
	 */
	public static enum SATAlgorithm {
		BRUTE_FORCE, DPLL
	}

	/**
	 * The formula factory used by SAT solvers to extract information from formulas
	 * or create new formulas.
	 */
	FormulaFactory fac;

	/**
	 * Creates a new Semantics instance based on the given formula factory.
	 * 
	 * @param fact formula factory that will be used to instantiate new instances of
	 *             SAT solvers
	 */
	public Semantics(FormulaFactory fac) {
		this.fac = fac;
	}

	public SATSolver newSolver(SATAlgorithm alg) {
		switch (alg) {
		case BRUTE_FORCE:
			return new BruteForceSATSolver(fac);
		case DPLL:
			return new DPLLSATSolver(fac);
		default:
			throw new RuntimeException("unreachable");
		}
	}
}
