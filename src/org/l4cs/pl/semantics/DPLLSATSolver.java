package org.l4cs.pl.semantics;

import org.l4cs.pl.syntax.Formula;
import org.l4cs.pl.syntax.FormulaFactory;

/**
 * Implementation of SATSolver based on the Davis–Putnam–Logemann–Loveland
 * (DPLL) algorithm.
 * 
 * @see <a href="https://en.wikipedia.org/wiki/DPLL_algorithm">DPLL Algorithm
 *      (Wikipedia)</a>
 * @author Stephen Siegel
 */
public class DPLLSATSolver extends SATSolver {

	/**
	 * Creates new solver using the given formula factory.
	 * 
	 * @param fac the factory to use to extract information from formulas
	 */
	DPLLSATSolver(FormulaFactory fac) {
		super(fac);
	}

	/**
	 * Is the CNF structure satisfiable? If yes, modify the given model to reflect
	 * the assignments made to variables and return {@code true}. Otherwise return
	 * {@code false}.
	 * 
	 * @param cs    the CNF structure which provides a structured representation of
	 *              a formula in conjunctive normal form
	 * @param model a (non-{@code null}) model which will be modified by this
	 *              method; typically this model is empty (all variables are
	 *              assigned {@code false}) when this method is called
	 * @return {@code true} iff the formula represented by {@code cs} is satisfiable
	 */
	private boolean sat(CnfStruct cs, Model model) {
		sayLine("Push " + CnfStruct.toString(fac, cs) + ".  Model = " + model);
		// accelerate cs
		if (cs.isEmpty()) {
			sayLine("Satisfying model found!\nPop.");
			return true;
		}
		if (cs.containsEmpty()) {
			sayLine("Unsatisfiable.\nPop.");
			return false;
		}
		Clause firstClause = cs.first();
		int literalID = firstClause.first();
		boolean parity = literalID > 0;
		int propID = (parity ? literalID : -literalID) - 1;
		model.set(propID, parity);
		CnfStruct cs2 = cs.withTrue(literalID);
		if (firstClause.isUnit())
			say("[UNIT] ");
		sayLine("Setting " + fac.getProposition(propID) + " to " + parity + ".");
		if (sat(cs2, model)) {
			sayLine("Pop.");
			return true;
		}
		if (!firstClause.isUnit()) {
			sayLine("Setting " + fac.getProposition(propID) + " to " + (!parity) + ".");
			model.set(propID, !parity);
			cs2 = cs.withTrue(-literalID);
			if (sat(cs2, model)) {
				sayLine("Pop.");
				return true;
			}
		}
		model.set(propID, false);
		sayLine("Pop.");
		return false;
	}

	/**
	 * Determines whether a CNF formula is satisfiable using the DPLL algorithm.
	 * 
	 * @param cnf a CNF formula
	 * @return a model satisfying {@code cnf}, if one exists, else {@code null}
	 * @throws {@link CnfException} if {@code cnf} is not in Conjunctive Normal Form
	 */
	public Model sat_cnf(Formula cnf) {
		sayLine("CNF formula: " + cnf);
		CnfStruct cs = CnfStruct.make(fac, cnf);
		sayLine("CNF Structure: " + CnfStruct.toString(fac, cs));
		RestrictedModel model = new RestrictedModel(fac, cs.vars(fac));
		return sat(cs, model) ? model : null;
	}

	/**
	 * Determines whether a CNF formula is valid using the straightforward linear
	 * algorithm. If you have a formula already in CNF form, you want to use this
	 * method to determine if it is valid.
	 * 
	 * @param cnf any CNF formula
	 * @return a model refuting f, if there is one, else null
	 * @throws {@link CnfException} if {@code cnf} is not in Conjunctive Normal Form
	 */
	public Model valid_cnf(Formula cnf) {
		CnfStruct cs = CnfStruct.make(fac, cnf);
		// note cs will not contain any clause that contains both p and !p
		// (for some proposition p).
		if (cs.isEmpty())
			return null; // valid
		// pick one clause and make every literal true
		Model model = new RestrictedModel(fac, cs.vars(fac));
		Clause c = cs.first();
		int numLiterals = c.data.length;
		for (int i = 0; i < numLiterals; i++) {
			int literalID = c.data[i];
			if (literalID < 0)
				model.set(-literalID - 1, true);
		}
		return model;
	}

	/**
	 * Determines if {@code f} is satisfiable by first using Tseytin's algorithm to
	 * convert to an equisatisfiable CNF formula, then using DPLL.
	 * 
	 * @param f any (non-{@code null}) propositional formula
	 * @return a model satisfying {@code f}, if there is one, else {@code null}
	 */
	@Override
	public Model sat(Formula f) {
		sayLine("Formula: " + f);
		sayLine("Converting to equisatisfiable CNF formula...");
		Formula cnf = fac.tseytin(f);
		return sat_cnf(cnf);
	}
}
