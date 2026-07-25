package org.l4cs.fol.nd;

import java.io.PrintStream;
import java.util.Set;

import org.l4cs.fol.syntax.FOLFormula;
import org.l4cs.fol.syntax.FOLFormulaFactory;
import org.l4cs.util.TextUtil;

/**
 * Inference rule eliminate-implies in natural deduction for first-order logic.
 * 
 * @author Yuxin Zhou
 */
public class ElimImplies_FOL extends FOLRule {

	public ElimImplies_FOL(FOLFormulaFactory fac) {
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
			return violation(conclusion, premises, fill("The antecedent of the first premise, " + gamma0
					+ ", and the antecedent of the conclusion, " + gamma + ", should be equal, but are not"));
		if (!gamma.equals(gamma1))
			return violation(conclusion, premises, fill("The antecedent of the second premise, " + gamma1
					+ ", and the antecedent of the conclusion, " + gamma + ", should be equal, but are not"));
		FOLFormula f0 = s0.succedent(), f1 = s1.succedent(), f = conclusion.succedent();
		if (!fac.isImplies(f1))
			return violation(conclusion, premises, fill("The second premise's succedent, " + f1
					+ ", must be an implication, not a formula of kind " + f1.kind() + "."));
		FOLFormula a = fac.arg0(f1), b = fac.arg1(f1); // f1 = a->b
		if (!f0.equals(a))
			return violation(conclusion, premises, fill("The first premise's succedent, " + f0
					+ ", must be the implication's left argument, " + a + ", but the two formulas differ."));
		if (!f.equals(b))
			return violation(conclusion, premises, fill("The conclusion's succedent, " + f
					+ ", must be the implication's right argument, " + b + ", but the two formulas differ."));
		return null;
	}

	@Override
	public String toString() {
		return "E" + TextUtil.implies();
	}

	@Override
	public void printDescription(PrintStream out) {
		out.println("Rule " + this + " (\"eliminate implies\", aka \"modus ponens\"):");
		String s1 = GAMMA + " " + TextUtil.infers() + " A     " + GAMMA + " " + TextUtil.infers() + " A"
				+ TextUtil.implies() + "B";
		String s2 = GAMMA + " " + TextUtil.infers() + " B";
		TextUtil.printFrac(out, 5, s1, s2);
		StringBuilder buf = new StringBuilder();
		buf.append("Rule " + this + " says that if you know A" + TextUtil.implies()
				+ "B, and you know A, then you can conclude B. "
				+ "All premises and the conclusion use the same context " + GAMMA + ". ");
		buf.append("This rule has two premises.");
		out.print(TextUtil.wrap(buf));
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ElimImplies_FOL;
	}

	@Override
	public int hashCode() {
		return ElimImplies_FOL.class.hashCode();
	}

}
