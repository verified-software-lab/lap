package org.l4cs.fol.nd;

import java.io.PrintStream;
import java.util.Set;

import org.l4cs.fol.syntax.FOLFormula;
import org.l4cs.fol.syntax.FOLFormulaFactory;
import org.l4cs.util.TextUtil;

/**
 * Inference rule Ax of natural deduction for first-order logic.
 * 
 * @author Yuxin Zhou
 */
public class Ax_FOL extends FOLRule {

	/**
	 * Constructs the Ax rule with the specified formula factory.
	 * 
	 * @param fac the formula factory for creating and manipulating formulas
	 */
	public Ax_FOL(FOLFormulaFactory fac) {
		super(fac);
	}

	@Override
	public int arity() {
		return 0;
	}

	/**
	 * Checks whether the Ax rule is correctly applied. The conclusion is valid
	 * if its succedent appears in its antecedent (context).
	 * 
	 * @param conclusion the proposed conclusion sequent
	 * @param premises   no premises are required for the Ax rule (empty array)
	 * @return {@code null} if the rule is correctly applied, otherwise a
	 *         {@link FOLViolation} describing the error
	 */
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

	/**
	 * Returns a string representation of this rule.
	 * 
	 * @return the string "Ax"
	 */
	@Override
	public String toString() {
		return "Ax";
	}

	/**
	 * Prints a detailed description of this rule to the given output stream,
	 * including the rule format and an explanation of how it works.
	 * 
	 * @param out the PrintStream to which the description is written
	 */
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

	/**
	 * Determines whether this rule is equal to the given object. Two Ax rules
	 * are always equal.
	 * 
	 * @param obj the object to compare with
	 * @return {@code true} if obj is an Ax_FOL instance, {@code false}
	 *         otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Ax_FOL;
	}

	/**
	 * Returns a hash code for this rule.
	 * 
	 * @return the hash code
	 */
	@Override
	public int hashCode() {
		return Ax_FOL.class.hashCode();
	}

}
