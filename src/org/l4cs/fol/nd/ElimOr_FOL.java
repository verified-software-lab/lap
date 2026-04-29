package org.l4cs.fol.nd;

import java.io.PrintStream;
import java.util.Set;

import org.l4cs.fol.syntax.FOLFormula;
import org.l4cs.fol.syntax.FOLFormulaFactory;
import org.l4cs.util.TextUtil;

public class ElimOr_FOL extends FOLRule {

	public ElimOr_FOL(FOLFormulaFactory fac) {
		super(fac);
	}

	@Override
	public int arity() {
		return 3;
	}

	@Override
	public FOLViolation check(FOLSequent conclusion, FOLSequent... premises) {
		FOLViolation v = super.check(conclusion, premises);
		if (v != null)
			return v;
		FOLSequent s0 = premises[0], s1 = premises[1], s2 = premises[2];
		Set<FOLFormula> gamma = conclusion.antecedent(), gamma0 = s0.antecedent(), gamma1 = s1.antecedent(),
				gamma2 = s2.antecedent();
		if (!gamma.equals(gamma0))
			return violation(conclusion, premises, fill("The first premise's antecedent, " + gamma0
					+ ", and the conclusion's antecedent, " + gamma + ", should be equal, but are not."));
		FOLFormula or = s0.succedent();
		if (!fac.isOr(or))
			return violation(conclusion, premises,
					"The first premise's antecedent, " + or + ", should be a disjunction, i.e., an OR formula, "
							+ "but instead is a formula of kind " + or.kind() + ".");
		FOLFormula a = fac.arg0(or), b = fac.arg1(or);
		if (!isUnionWith(gamma1, gamma, a))
			return violation(conclusion, premises,
					fill("The second premise's antecedent, " + gamma1
							+ ", should be the union of the conclusion's antecedent, " + gamma
							+ ", and the OR formula's left argument, " + a + ", but is not."));
		if (!isUnionWith(gamma2, gamma, b))
			return violation(conclusion, premises,
					fill("The third premise's antecedent, " + gamma2
							+ ", should be the union of the conclusion's antecedent, " + gamma
							+ ", and the OR formula's right argument, " + b + ", but is not."));
		FOLFormula c = conclusion.succedent(), c1 = s1.succedent(), c2 = s2.succedent();
		if (!c.equals(c1))
			return violation(conclusion, premises, fill("The second premise's succedent, " + c1
					+ ", should be the same as the conclusion's succedent, " + c + ", but is not."));
		if (!c.equals(c2))
			return violation(conclusion, premises, fill("The third premise's succedent, " + c2
					+ ", should be the same as the conclusion's succedent, " + c + ", but is not."));
		return null;
	}

	@Override
	public String toString() {
		return "E" + OR;
	}

	@Override
	public void printDescription(PrintStream out) {
		out.println("Rule " + this + " (\"eliminate or\"):");
		String s1 = GAMMA + " " + TextUtil.infers() + " A" + OR + "B     " + GAMMA + ",A " + TextUtil.infers()
				+ " C     " + GAMMA + ",B " + TextUtil.infers() + " C";
		String s2 = GAMMA + " " + TextUtil.infers() + " C";
		TextUtil.printFrac(out, 5, s1, s2);
		StringBuffer buf = new StringBuffer();
		buf.append("Rule " + this + " says that if in some context " + GAMMA + ": (1) you can derive A" + OR + "B, "
				+ "(2) if you assume A, you can derive C, and " + "(3) if you assume B, you can derive C, "
				+ "then you can conclude that C holds in " + GAMMA + ". ");
		buf.append("This rule has three premises.");
		out.print(TextUtil.fill(buf, DEFAULT_WIDTH));
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ElimOr_FOL;
	}

	@Override
	public int hashCode() {
		return ElimOr_FOL.class.hashCode();
	}

}
