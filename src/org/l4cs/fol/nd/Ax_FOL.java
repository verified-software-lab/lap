package org.l4cs.fol.nd;

import java.io.PrintStream;
import java.util.Set;

import org.l4cs.fol.syntax.FOLFormula;
import org.l4cs.fol.syntax.FOLFormulaFactory;
import org.l4cs.util.TextUtil;

/**
 * @auther Yuxin Zhou
 */
public class Ax_FOL extends FOLRule {

	public Ax_FOL(FOLFormulaFactory fac) {
		super(fac);
	}

	@Override
	public int arity() {
		return 0;
	}

	@Override
	public FOLViolation check(FOLSequent conclusion, FOLSequent... premises) {
		FOLViolation v = super.check(conclusion, premises);
		if (v != null)
			return v;
		Set<FOLFormula> context = conclusion.antecedent();
		FOLFormula a = conclusion.succedent();
		if (!context.contains(a))
			return violation(conclusion, premises, fill(
					"The conclusion's antecedent, " + context + ", must contain the formula " + a + " but does not."));
		return null;
	}

	@Override
	public String toString() {
		return "Ax";
	}

	@Override
	public void printDescription(PrintStream out) {
		out.println("Rule Ax:");
		String s1 = "";
		String s2 = GAMMA + "," + "A " + TextUtil.infers() + " A";
		TextUtil.printFrac(out, 5, s1, s2);
		StringBuilder buf = new StringBuilder();
		buf.append("Rule Ax says that at any time, you may conclude any "
				+ "formula that is currently being assumed to hold. ");
		buf.append("The Ax rule has no premises and holds whenever the "
				+ "conclusion's succedent belongs to the set of formulas " + "which form the conclusion's antecedent.");
		out.print(TextUtil.wrap(buf));
		out.println();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Ax_FOL;
	}

	@Override
	public int hashCode() {
		return Ax_FOL.class.hashCode();
	}

}
