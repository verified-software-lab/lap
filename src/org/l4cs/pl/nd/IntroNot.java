package org.l4cs.pl.nd;

import java.io.PrintStream;

import org.l4cs.pl.syntax.Formula;
import org.l4cs.pl.syntax.FormulaFactory;
import org.l4cs.util.TextUtil;

/**
 * Inference rule introduce-not in natural deduction for propositional logic.
 * 
 * @author Stephen Siegel
 */
public class IntroNot extends Rule {

	public IntroNot(FormulaFactory fac) {
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
		Sequent s0 = premises[0];
		Formula f = s0.succedent(), not = conclusion.succedent();
		if (!fac.isFalse(f))
			return violation(conclusion, premises,
					fill("The premise's succedent should be " + BOT + ", but instead was " + f + "."));
		if (!fac.isNot(not))
			return violation(conclusion, premises, fill("The conclusion's succedent, " + not
					+ ", should be a NOT formula, but instead has kind " + not.kind() + "."));
		Formula a = fac.arg(not);
		if (!isUnionWith(s0.antecedent(), conclusion.antecedent(), a))
			return violation(conclusion, premises,
					fill("The premise's antecedent should be the union of the " + "conclusion's antecedent, "
							+ conclusion.antecedent() + ", and the formula " + a + ", but was instead "
							+ s0.antecedent() + "."));
		return null;
	}

	@Override
	public String toString() {
		return "I" + NOT;
	}

	@Override
	public void printDescription(PrintStream out) {
		out.println("Rule " + this + " (\"introduce not\"):");
		String s1 = GAMMA + "," + "A " + TextUtil.infers() + " " + BOT;
		String s2 = GAMMA + " " + TextUtil.infers() + NOT + "A";
		TextUtil.printFrac(out, 5, s1, s2);
		StringBuilder buf = new StringBuilder();
		buf.append("Rule " + this + " says that if you start in some context ");
		buf.append(GAMMA + ", and assume " + "A, ");
		buf.append("and from there you derive " + BOT + ", ");
		buf.append("then it must be the case that " + NOT + "A holds in ");
		buf.append(GAMMA + ". The rule has one premise.");
		out.print(TextUtil.wrap(buf));
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof IntroNot;
	}

	@Override
	public int hashCode() {
		return IntroNot.class.hashCode();
	}

}
