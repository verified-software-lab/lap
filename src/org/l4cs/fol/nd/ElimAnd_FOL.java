package org.l4cs.fol.nd;

import java.io.PrintStream;
import java.util.Set;

import org.l4cs.fol.syntax.FOLFormula;
import org.l4cs.fol.syntax.FOLFormulaFactory;
import org.l4cs.util.TextUtil;

public class ElimAnd_FOL extends FOLRule {

	/**
	 * 1 or 2.
	 */
	int i;

	public ElimAnd_FOL(FOLFormulaFactory fac, int i) {
		super(fac);
		if (i != 1 && i != 2)
			throw new IllegalArgumentException("In ElimAnd, i must be 1 or 2, not " + i);
		this.i = i;
	}

	@Override
	public int arity() {
		return 1;
	}

	@Override
	public FOLViolation check(FOLSequent conclusion, FOLSequent... premises) {
		FOLViolation v = super.check(conclusion, premises);
		if (v != null)
			return v;
		FOLSequent s0 = premises[0];
		Set<FOLFormula> gamma = conclusion.antecedent(), gamma0 = s0.antecedent();
		if (!gamma.equals(gamma0))
			return violation(conclusion, premises, fill("The first premise's antecedent, " + gamma0
					+ ", and the conclusion's antecedent, " + gamma + ", should be equal, but are not."));
		FOLFormula f0 = s0.succedent(), f = conclusion.succedent();
		if (!fac.isAnd(f0))
			return violation(conclusion, premises, fill("The premise's succedent, " + f0
					+ ", must be a conjunction, i.e., an AND formula." + " Instead, it has kind " + f0.kind() + "."));
		FOLFormula g = i == 1 ? fac.arg0(f0) : fac.arg1(f0);
		if (!f.equals(g))
			return violation(conclusion, premises,
					fill("The conclusion's succedent, " + f + ", should be the AND formula's "
							+ (i == 1 ? "left" : "right") + " argument, " + g + ", but is not."));
		return null;
	}

	@Override
	public String toString() {
		return "E" + AND + i;
	}

	@Override
	public void printDescription(PrintStream out) {
		out.println("Rule " + this + " (\"eliminate and " + i + "\"):");
		String s1 = GAMMA + " " + TextUtil.infers() + " A" + AND + "B";
		String s2 = GAMMA + " " + TextUtil.infers() + " " + (i == 1 ? "A" : "B");
		TextUtil.printFrac(out, 5, s1, s2);
		StringBuilder buf = new StringBuilder();
		buf.append("Rule " + this + " says that if you can derive A" + AND + "B, then you can conclude "
				+ (i == 1 ? "A" : "B") + ". " + "The premise and the conclusion use the same context " + GAMMA + ". ");
		buf.append("This rule has one premise.");
		out.print(fill(buf));
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ElimAnd_FOL && ((ElimAnd_FOL) obj).i == i;
	}

	@Override
	public int hashCode() {
		return ElimAnd_FOL.class.hashCode() + i;
	}

}
