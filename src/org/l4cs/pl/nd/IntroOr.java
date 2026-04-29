package org.l4cs.pl.nd;

import java.io.PrintStream;
import java.util.Set;

import org.l4cs.pl.syntax.Formula;
import org.l4cs.pl.syntax.FormulaFactory;
import org.l4cs.util.TextUtil;

/**
 * Inference rule introduce-or in natural deduction for propositional logic.
 * Actually an instance of this class can represent one of two rules:
 * introduce-or-1 or introduce-or-2.
 * 
 * @author Stephen Siegel
 */
public class IntroOr extends Rule {

	/**
	 * 1 or 2.
	 */
	int i;

	public IntroOr(FormulaFactory fac, int i) {
		super(fac);
		if (i != 1 && i != 2)
			throw new IllegalArgumentException("In IntroOr, i must be 1 or 2, not " + i);
		this.i = i;
	}

	@Override
	public int arity() {
		return 1;
	}

	@Override
	public Violation check(Sequent conclusion, Sequent... premises) {
		Violation v = super.check(conclusion, premises);
		if (v != null)
			return v;
		Sequent s0 = premises[0];
		Set<Formula> gamma = conclusion.antecedent(), gamma0 = s0.antecedent();
		if (!gamma.equals(gamma0))
			return violation(conclusion, premises, fill("The first premise's antecedent, " + gamma0
					+ ", and the conclusion's antecedent, " + gamma + ", should be equal, but are not."));
		Formula f0 = s0.succedent(), f = conclusion.succedent();
		if (!fac.isOr(f))
			return violation(conclusion, premises, fill("The conclusion's succedent, " + f
					+ ", must be a disjunction, i.e., an OR formula." + " Instead, it has kind " + f.kind() + "."));
		Formula g = i == 1 ? fac.arg0(f) : fac.arg1(f);
		if (!f0.equals(g))
			return violation(conclusion, premises,
					fill("The premise's succedent, " + f0 + ", should be the OR formula's "
							+ (i == 1 ? "left" : "right") + " argument, " + g + ", but is not."));
		return null;
	}

	@Override
	public String toString() {
		return "I" + OR + i;
	}

	@Override
	public void printDescription(PrintStream out) {
		out.println("Rule " + this + " (\"introduce or " + i + "\"):");
		String s1 = GAMMA + " " + TextUtil.infers() + " " + (i == 1 ? "A" : "B");
		String s2 = GAMMA + " " + TextUtil.infers() + " A" + OR + "B";
		TextUtil.printFrac(out, 5, s1, s2);
		StringBuffer buf = new StringBuffer();
		buf.append("Rule " + this + " says that if you know " + (i == 1 ? "A" : "B") + ", then you can conclude A" + OR
				+ "B. " + "The premise and the conclusion use the same context " + GAMMA + ". ");
		buf.append("This rule has one premise.");
		out.print(fill(buf));
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof IntroOr && ((IntroOr) obj).i == i;
	}

	@Override
	public int hashCode() {
		return IntroOr.class.hashCode() + i;
	}

}
