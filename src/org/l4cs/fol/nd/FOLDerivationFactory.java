package org.l4cs.fol.nd;

import java.util.Set;

import org.l4cs.fol.syntax.FOLFormula;
import org.l4cs.fol.syntax.FOLFormulaFactory;
//import org.l4cs.fol.nd.FOLRule;

public class FOLDerivationFactory {

	protected FOLFormulaFactory fac;

	private FOLRule ax, raa, introImplies, elimImplies, introAnd, elimAnd1, elimAnd2, introOr1, introOr2, elimOr, introNot,
			elimNot;

	// [CHANGE] Fields for First-Order Logic Rules
	private FOLRule introForall, elimForall, introExists, elimExists;

	public FOLDerivationFactory(FOLFormulaFactory fac) {
		this.fac = fac;
		ax = new Ax_FOL(fac);
		raa = new RAA_FOL(fac);
		introImplies = new IntroImplies_FOL(fac);
		elimImplies = new ElimImplies_FOL(fac);
		introAnd = new IntroAnd_FOL(fac);
		elimAnd1 = new ElimAnd_FOL(fac, 1);
		elimAnd2 = new ElimAnd_FOL(fac, 2);
		introOr1 = new IntroOr_FOL(fac, 1);
		introOr2 = new IntroOr_FOL(fac, 2);
		elimOr = new ElimOr_FOL(fac);
		introNot = new IntroNot_FOL(fac);
		elimNot = new ElimNot_FOL(fac);

		// [CHANGE] Initialize FOL rules
		introForall = new IntroForall(fac) { @Override public String toString() { return "I\u2200"; } }; // I∀
		elimForall = new ElimForall(fac) { @Override public String toString() { return "E\u2200"; } };  // E∀
		introExists = new IntroExists(fac) { @Override public String toString() { return "I\u2203"; } }; // I∃
		elimExists = new ElimExists(fac) { @Override public String toString() { return "E\u2203"; } };  // E∃
	
	}

	// Accessors for PL rules
	public FOLRule ax() {
		return ax;
	}

	public FOLRule raa() {
		return raa;
	}

	public FOLRule introImplies() {
		return introImplies;
	}

	public FOLRule elimImplies() {
		return elimImplies;
	}

	public FOLRule introAnd() {
		return introAnd;
	}

	public FOLRule elimAnd1() {
		return elimAnd1;
	}

	public FOLRule elimAnd2() {
		return elimAnd2;
	}

	public FOLRule introOr1() {
		return introOr1;
	}

	public FOLRule introOr2() {
		return introOr2;
	}

	public FOLRule elimOr() {
		return elimOr;
	}

	public FOLRule introNot() {
		return introNot;
	}

	public FOLRule elimNot() {
		return elimNot;
	}

	// [CHANGE] Accessors for FOL rules
	public FOLRule introForall() {
		return introForall;
	}

	public FOLRule elimForall() {
		return elimForall;
	}

	public FOLRule introExists() {
		return introExists;
	}

	public FOLRule elimExists() {
		return elimExists;
	}
	
	public FOLRule[] rules() {
		return new FOLRule[] { ax, raa, introImplies, elimImplies, introAnd, elimAnd1, elimAnd2, introOr1, introOr2,
				elimOr, introNot, elimNot, introForall, elimForall, introExists, elimExists };
	}
	
	public FOLSequent sequent(Set<FOLFormula> antecedent, FOLFormula succedent) {
		if (antecedent == null)
			throw new IllegalArgumentException("null antecedent");
		if (succedent == null)
			throw new IllegalArgumentException("null succedent");
		return new FOLSequent(antecedent, succedent);
	}

	public FOLDerivation axDerivation(FOLSequent conclusion) throws FOLViolation {
		return derivation(ax, conclusion, new FOLDerivation[0]);
	}

	/**
	 * Constructs a new derivation from the given rule, conclusion, and
	 * subderivations.
	 * * @param rule           the rule for the root node
	 * * @param conclusion     the conclusion of the derivation
	 * @param subderivations premises for the rule
	 * @return a valid Derivation object
	 * @throws FOLViolation if the rule check fails
	 */
	public FOLDerivation derivation(FOLRule rule, FOLSequent conclusion, FOLDerivation... subderivations) throws FOLViolation {
		if (rule == null)
			throw new IllegalArgumentException("null rule");
		if (conclusion == null)
			throw new IllegalArgumentException("null conclusion");
		if (subderivations == null)
			throw new IllegalArgumentException("null subderivations");
		int n = subderivations.length;
		FOLSequent[] premises = new FOLSequent[n];
		for (int i = 0; i < n; i++)
			premises[i] = subderivations[i].conclusion;
		FOLViolation v = rule.check(conclusion, premises);
		if (v != null)
			throw v;
		return new FOLDerivation(rule, conclusion, subderivations);
	}
}