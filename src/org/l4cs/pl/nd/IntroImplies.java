package org.l4cs.pl.nd;

import java.io.PrintStream;

import org.l4cs.pl.syntax.Formula;
import org.l4cs.pl.syntax.FormulaFactory;
import org.l4cs.util.TextUtil;

/**
 * Inference rule introduce-implies in natural deduction for propositional
 * logic.
 * 
 * @author Stephen Siegel
 */
public class IntroImplies extends Rule {

	public IntroImplies(FormulaFactory fac) {
		super(fac);
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
		Sequent premise = premises[0];
		Formula implication = conclusion.succedent();
		if (!fac.isImplies(implication))
			return violation(conclusion, premises, fill("The conclusion's succedent, " + implication
					+ ", must be an implication, not a " + implication.kind() + " formula."));
		Formula a = fac.arg0(implication), b = fac.arg1(implication);
		if (!premise.succedent().equals(b))
			return violation(conclusion, premises,
					fill("The premise's succedent should be " + b + ", not " + premise.succedent()));
		if (!isUnionWith(premise.antecedent(), conclusion.antecedent(), a))
			return violation(conclusion, premises,
					fill("The premise's antecedent should be the union of the conclusion's antecedent, "
							+ conclusion.antecedent() + ", and the formula " + a + ", not " + premise.antecedent()
							+ "."));

		return null;
	}

	@Override
	public String toString() {
		return "I" + IMPLIES;
	}

	@Override
	public void printDescription(PrintStream out) {
		out.println("Rule " + this + " (\"introduce implies\"):");
		String s1 = GAMMA + "," + "A " + TextUtil.infers() + " B";
		String s2 = GAMMA + " " + TextUtil.infers() + " A" + IMPLIES + "B";
		TextUtil.printFrac(out, 5, s1, s2);
		StringBuilder buf = new StringBuilder();
		buf.append("Rule " + this + " says that if you start in some context ");
		buf.append(GAMMA + ", and assume A, ");
		buf.append("and from there you derive B, ");
		buf.append("then it must be the case that A" + IMPLIES + "B holds in ");
		buf.append(GAMMA + ". The rule has one premise.");
		out.print(TextUtil.wrap(buf));
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof IntroImplies;
	}

	@Override
	public int hashCode() {
		return IntroImplies.class.hashCode();
	}

}
