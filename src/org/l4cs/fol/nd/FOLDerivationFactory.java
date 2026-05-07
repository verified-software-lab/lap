package org.l4cs.fol.nd;

import java.util.Set;

import org.l4cs.fol.syntax.FOLFormula;
import org.l4cs.fol.syntax.FOLFormulaFactory;
//import org.l4cs.fol.nd.FOLRule;

/**
 * A factory for producing {@link FOLDerivation}s and components of derivations,
 * such as {@link FOLSequent}s and {@link FOLRule}s.
 * 
 * @author Yuxin Zhou
 */
public class FOLDerivationFactory {

	/**
	 * The formula factory used by this derivation factory.
	 */
	protected FOLFormulaFactory fac;

	/**
	 * All of the {@link FOLRule}s used in the proof system, natural deduction for
	 * first-order logic. There is only one instance of each {@link FOLRule}. (See:
	 * Singleton Pattern).
	 */
	private FOLRule ax, raa, introImplies, elimImplies, introAnd, elimAnd1, elimAnd2, introOr1, introOr2, elimOr, introNot,
			elimNot;

	/**
	 * {@link FOLRule}s specific to first-order logic: introduction and elimination
	 * rules for universal and existential quantifiers.
	 */
	private FOLRule introForall, elimForall, introExists, elimExists;

	/**
	 * Constructs a derivation factory with the specified formula factory.
	 * 
	 * @param fac the formula factory for creating and manipulating formulas
	 */
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
	/**
	 * Returns the axiom rule (Ax).
	 * 
	 * @return the Ax rule
	 */
	public FOLRule ax() {
		return ax;
	}

	/**
	 * Returns the reductio ad absurdum rule (RAA).
	 * 
	 * @return the RAA rule
	 */
	public FOLRule raa() {
		return raa;
	}

	/**
	 * Returns the introduction-implies rule.
	 * 
	 * @return the intro-implies rule
	 */
	public FOLRule introImplies() {
		return introImplies;
	}

	/**
	 * Returns the elimination-implies rule.
	 * 
	 * @return the elim-implies rule
	 */
	public FOLRule elimImplies() {
		return elimImplies;
	}

	/**
	 * Returns the introduction-and rule.
	 * 
	 * @return the intro-and rule
	 */
	public FOLRule introAnd() {
		return introAnd;
	}

	/**
	 * Returns the elimination-and-1 rule (extracts left conjunct).
	 * 
	 * @return the elim-and-1 rule
	 */
	public FOLRule elimAnd1() {
		return elimAnd1;
	}

	/**
	 * Returns the elimination-and-2 rule (extracts right conjunct).
	 * 
	 * @return the elim-and-2 rule
	 */
	public FOLRule elimAnd2() {
		return elimAnd2;
	}

	/**
	 * Returns the introduction-or-1 rule (introduces left disjunct).
	 * 
	 * @return the intro-or-1 rule
	 */
	public FOLRule introOr1() {
		return introOr1;
	}

	/**
	 * Returns the introduction-or-2 rule (introduces right disjunct).
	 * 
	 * @return the intro-or-2 rule
	 */
	public FOLRule introOr2() {
		return introOr2;
	}

	/**
	 * Returns the elimination-or rule.
	 * 
	 * @return the elim-or rule
	 */
	public FOLRule elimOr() {
		return elimOr;
	}

	/**
	 * Returns the introduction-not rule.
	 * 
	 * @return the intro-not rule
	 */
	public FOLRule introNot() {
		return introNot;
	}

	/**
	 * Returns the elimination-not rule.
	 * 
	 * @return the elim-not rule
	 */
	public FOLRule elimNot() {
		return elimNot;
	}

	// [CHANGE] Accessors for FOL rules
	/**
	 * Returns the introduction-forall rule for universal quantification.
	 * 
	 * @return the intro-forall rule
	 */
	public FOLRule introForall() {
		return introForall;
	}

	/**
	 * Returns the elimination-forall rule for universal quantification.
	 * 
	 * @return the elim-forall rule
	 */
	public FOLRule elimForall() {
		return elimForall;
	}

	/**
	 * Returns the introduction-exists rule for existential quantification.
	 * 
	 * @return the intro-exists rule
	 */
	public FOLRule introExists() {
		return introExists;
	}

	/**
	 * Returns the elimination-exists rule for existential quantification.
	 * 
	 * @return the elim-exists rule
	 */
	public FOLRule elimExists() {
		return elimExists;
	}
	
	/**
	 * Returns an array of all inference rules in this derivation system.
	 * 
	 * @return an array containing all rules
	 */
	public FOLRule[] rules() {
		return new FOLRule[] { ax, raa, introImplies, elimImplies, introAnd, elimAnd1, elimAnd2, introOr1, introOr2,
				elimOr, introNot, elimNot, introForall, elimForall, introExists, elimExists };
	}
	
	/**
	 * Creates a new sequent with the specified antecedent and succedent.
	 * 
	 * @param antecedent the set of formulas assumed to hold (must not be null)
	 * @param succedent  the formula being derived (must not be null)
	 * @return a new FOLSequent with the given antecedent and succedent
	 * @throws IllegalArgumentException if antecedent or succedent is null
	 */
	public FOLSequent sequent(Set<FOLFormula> antecedent, FOLFormula succedent) {
		if (antecedent == null)
			throw new IllegalArgumentException("null antecedent");
		if (succedent == null)
			throw new IllegalArgumentException("null succedent");
		return new FOLSequent(antecedent, succedent);
	}

	/**
	 * Creates a simple derivation using the Ax rule with no premises.
	 * 
	 * @param conclusion the conclusion sequent (must be valid under Ax rule)
	 * @return a FOLDerivation using the Ax rule
	 * @throws FOLViolation if the conclusion is not valid under the Ax rule
	 */
	public FOLDerivation axDerivation(FOLSequent conclusion) throws FOLViolation {
		return derivation(ax, conclusion, new FOLDerivation[0]);
	}

	/**
	 * Constructs a new derivation from the given rule, conclusion, and
	 * subderivations.
	 * 
	 * @param rule           the rule for the root node
	 * @param conclusion     the conclusion of the derivation
	 * @param subderivations premises for the rule
	 * @return a valid FOLDerivation object
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