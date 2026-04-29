package org.l4cs.pl.nd;

import java.util.Set;

import org.l4cs.pl.syntax.Formula;
import org.l4cs.pl.syntax.FormulaFactory;

/**
 * A factory for producing {@link Derivation}s and components of derivations,
 * such as {@link Sequent}s and {@link Rule}s.
 * 
 * @author Stephen Siegel
 */
public class DerivationFactory {

	/**
	 * The formula factory used by this derivation factory.
	 */
	protected FormulaFactory fac;

	/**
	 * All of the {@link Rule}s used in the proof system, natural deduction for
	 * propositional logic. There is only one instance of each {@link Rule}. (See:
	 * Singleton Pattern).
	 */
	private Rule ax, raa, introImplies, elimImplies, introAnd, elimAnd1, elimAnd2, introOr1, introOr2, elimOr, introNot,
			elimNot;

	public DerivationFactory(FormulaFactory fac) {
		this.fac = fac;
		ax = new Ax(fac);
		raa = new RAA(fac);
		introImplies = new IntroImplies(fac);
		elimImplies = new ElimImplies(fac);
		introAnd = new IntroAnd(fac);
		elimAnd1 = new ElimAnd(fac, 1);
		elimAnd2 = new ElimAnd(fac, 2);
		introOr1 = new IntroOr(fac, 1);
		introOr2 = new IntroOr(fac, 2);
		elimOr = new ElimOr(fac);
		introNot = new IntroNot(fac);
		elimNot = new ElimNot(fac);
	}

	public Rule ax() {
		return ax;
	}

	public Rule raa() {
		return raa;
	}

	public Rule introImplies() {
		return introImplies;
	}

	public Rule elimImplies() {
		return elimImplies;
	}

	public Rule introAnd() {
		return introAnd;
	}

	public Rule elimAnd1() {
		return elimAnd1;
	}

	public Rule elimAnd2() {
		return elimAnd2;
	}

	public Rule introOr1() {
		return introOr1;
	}

	public Rule introOr2() {
		return introOr2;
	}

	public Rule elimOr() {
		return elimOr;
	}

	public Rule introNot() {
		return introNot;
	}

	public Rule elimNot() {
		return elimNot;
	}

	public Rule[] rules() {
		return new Rule[] { ax, raa, introImplies, elimImplies, introAnd, elimAnd1, elimAnd2, introOr1, introOr2,
				elimOr, introNot, elimNot };
	}

	public Sequent sequent(Set<Formula> antecedent, Formula succedent) {
		if (antecedent == null)
			throw new IllegalArgumentException("null antecedent");
		if (succedent == null)
			throw new IllegalArgumentException("null succedent");
		return new Sequent(antecedent, succedent);
	}

	public Derivation axDerivation(Sequent conclusion) throws Violation {
		return derivation(ax, conclusion, new Derivation[0]);
	}

	public Derivation derivation(Rule rule, Sequent conclusion, Derivation... subderivations) throws Violation {
		if (rule == null)
			throw new IllegalArgumentException("null rule");
		if (conclusion == null)
			throw new IllegalArgumentException("null conclusion");
		if (subderivations == null)
			throw new IllegalArgumentException("null subderivations");
		int n = subderivations.length;
		Sequent[] premises = new Sequent[n];
		for (int i = 0; i < n; i++)
			premises[i] = subderivations[i].conclusion;
		Violation v = rule.check(conclusion, premises);
		if (v != null)
			throw v;
		return new Derivation(rule, conclusion, subderivations);
	}

}
