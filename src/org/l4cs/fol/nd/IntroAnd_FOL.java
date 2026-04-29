package org.l4cs.fol.nd;

import java.io.PrintStream;
import java.util.Set;

import org.l4cs.fol.syntax.FOLFormula;
import org.l4cs.fol.syntax.FOLFormulaFactory;
import org.l4cs.util.TextUtil;

public class IntroAnd_FOL extends FOLRule {

	public IntroAnd_FOL(FOLFormulaFactory fac) {
		super(fac);
	}

	@Override
	public int arity() {
		return 2;
	}

	@Override
	public FOLViolation check(FOLSequent conclusion, FOLSequent... premises) {
		FOLViolation v = super.check(conclusion, premises);
		if (v != null)
			return v;
		FOLSequent s0 = premises[0], s1 = premises[1];
		Set<FOLFormula> gamma = conclusion.antecedent(), gamma0 = s0.antecedent(), gamma1 = s1.antecedent();
		if (!gamma.equals(gamma0))
			return violation(conclusion, premises, fill("The first premise's antecedent, " + gamma0
					+ ", and the conclusion's antecedent, " + gamma + ", should be equal, but are not."));
		if (!gamma.equals(gamma1))
			return violation(conclusion, premises, fill("The second premise's antecedent, " + gamma1
					+ ", and the conclusion's antecedent, " + gamma + ", should be equal, but are not."));
		FOLFormula f0 = s0.succedent(), f1 = s1.succedent(), f = conclusion.succedent();
		if (!f.equals(fac.and(f0, f1)))
			return violation(conclusion, premises,
					fill("The conclusion's succedent, " + f + ", should be the conjunction of the succedents of "
							+ "the two premises, i.e., " + fac.and(f0, f1) + ".  Instead, it is " + f + "."));
		return null;
	}

	@Override
	public String toString() {
		return "I" + AND;
	}

	@Override
	public void printDescription(PrintStream out) {
		out.println("Rule " + this + " (\"introduce and\"):");
		String s1 = GAMMA + " " + TextUtil.infers() + " A     " + GAMMA + " " + TextUtil.infers() + " B";
		String s2 = GAMMA + " " + TextUtil.infers() + " A" + AND + "B";
		TextUtil.printFrac(out, 5, s1, s2);
		StringBuffer buf = new StringBuffer();
		buf.append("Rule " + this + " says that if you know A, and " + "you know B, then you can conclude A" + AND
				+ "B. " + "All premises and the conclusion use the same context " + GAMMA + ". ");
		buf.append("This rule has two premises.");
		out.print(TextUtil.fill(buf, DEFAULT_WIDTH));
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof IntroAnd_FOL;
	}

	@Override
	public int hashCode() {
		return IntroAnd_FOL.class.hashCode();
	}

}
