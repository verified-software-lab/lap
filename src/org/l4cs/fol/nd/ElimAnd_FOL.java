package org.l4cs.fol.nd;

import java.io.PrintStream;
import java.util.Set;

import org.l4cs.fol.syntax.FOLFormula;
import org.l4cs.fol.syntax.FOLFormulaFactory;
import org.l4cs.util.TextUtil;

//TODO: always format-code (shift-cmd-F) on the final product before committing

/**
 * Inference rule eliminate-and in natural deduction for first-order logic.
 * Actually two rules are represented: eliminate-and-1 and eliminate-and-2.
 * 
 * @author Yuxin Zhou
 */
public class ElimAnd_FOL extends FOLRule {

	/**
	 * 1 or 2.
	 */
	int i;

	/**
	 * Constructs an eliminate-and rule with the specified index.
	 * 
	 * @param fac the formula factory for creating and manipulating formulas
	 * @param i   either 1 or 2, indicating which conjunct to extract
	 * @throws IllegalArgumentException if i is not 1 or 2
	 */
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

	/**
	 * Checks whether the premises correctly support the conclusion for the
	 * eliminate-and rule. The premise must have an AND formula as its succedent,
	 * and the conclusion's succedent must be the specified conjunct (1st or 2nd) of
	 * that AND formula. Both the premise and conclusion must have the same
	 * antecedent (context).
	 * 
	 * @param conclusion the proposed conclusion sequent
	 * @param premises   the premise sequent (exactly one)
	 * @return {@code null} if the rule is correctly applied, otherwise a
	 *         {@link FOLViolation} describing the error
	 */
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

	/**
	 * Returns a short name for this rule.
	 * 
	 * @return the string "E∧i" where i is 1 or 2
	 */
	@Override
	public String toString() {
		return "E" + TextUtil.and() + i;
	}

	/**
	 * Prints a detailed description of this rule to the given output stream,
	 * including the rule format and an explanation of how it works.
	 * 
	 * @param out the PrintStream to which the description is written
	 * 
	 */
	@Override
	public void printDescription(PrintStream out) {
		out.println("Rule " + this + " (\"eliminate and " + i + "\"):");
		// TODO: again, AND is always Unicode, GAMMA is always Unicode, etc.
		String s1 = GAMMA + " " + TextUtil.infers() + " A" + TextUtil.and() + "B";
		String s2 = GAMMA + " " + TextUtil.infers() + " " + (i == 1 ? "A" : "B");
		TextUtil.printFrac(out, 5, s1, s2);
		StringBuilder buf = new StringBuilder();
		buf.append("Rule " + this + " says that if you can derive A" + TextUtil.and() + "B, then you can conclude "
				+ (i == 1 ? "A" : "B") + ". " + "The premise and the conclusion use the same context " + GAMMA + ". ");
		buf.append("This rule has one premise.");
		out.print(fill(buf));
	}

	/**
	 * Determines whether this rule is equal to the given object. Two eliminate-and
	 * rules are equal if they have the same index i.
	 * 
	 * @param obj the object to compare with
	 * TODO: the "obj" below needs to be in {@code ...}.  The ElimAnd_FOL
	 * should be an {@link} to this class.
	 *  
	 * @return {@code true} if obj is an ElimAnd_FOL with the same index,
	 *         {@code false} otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof ElimAnd_FOL && ((ElimAnd_FOL) obj).i == i;
	}

	/**
	 * Returns a hash code for this rule based on the class and the index.
	 * 
	 * @return the hash code
	 */
	@Override
	public int hashCode() {
		return ElimAnd_FOL.class.hashCode() + i;
	}

}
