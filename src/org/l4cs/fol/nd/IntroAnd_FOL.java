package org.l4cs.fol.nd;

import java.io.PrintStream;
import java.util.Set;

import org.l4cs.fol.syntax.FOLFormula;
import org.l4cs.fol.syntax.FOLFormulaFactory;
import org.l4cs.util.TextUtil;

/**
 * Represents the natural deduction inference rule $\text{AndI}$ (Introduce And)
 * for First-Order Logic (FOL).
 * <p>
 * This rule states that if a context $\Gamma$ entails $A$, and $\Gamma$ also
 * entails $B$, then $\Gamma$ entails the conjunction of $A$ and $B$, written as
 * $A \land B$.
 * 
 * <pre>
 * $\frac{\Gamma \vdash A \quad \Gamma \vdash B}{\Gamma \vdash A \land B}$
 * </pre>
 * </p>
 *
 * @author Yuxin Zhou
 */
public class IntroAnd_FOL extends FOLRule {

	/**
	 * Constructs the IntroAnd_FOL rule.
	 *
	 * @param fac The factory used to create {@link FOLFormula} instances.
	 */
	public IntroAnd_FOL(FOLFormulaFactory fac) {
		super(fac);
	}

	/**
	 * The rule requires two premises (A and B) to deduce the conclusion.
	 *
	 * @return The arity of the rule, which is 2.
	 */
	@Override
	public int arity() {
		return 2;
	}

	/**
	 * Checks if a given {@link FOLSequent} conclusion correctly follows from two
	 * premises using the Intro And rule.
	 * <p>
	 * This method validates three conditions: 1. The antecedents (contexts) of the
	 * conclusion and both premises must be equal. 2. The succedent (conclusion) of
	 * the sequence must be the conjunction of the succedents of the two premises.
	 * 3. Returns {@code null} if the premises correctly imply the conclusion,
	 * otherwise returns a {@link FOLViolation} detailing the inconsistency.
	 *
	 * @param conclusion The sequent being checked (the resulting conclusion).
	 * @param premises   The two premises required by the rule.
	 * @return {@code null} if the rule applies correctly, or a {@link FOLViolation}
	 *         otherwise.
	 */
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

	/**
	 * Returns the symbolic representation of the rule, e.g., "I&".
	 *
	 * @return The string representation of the rule.
	 */
	@Override
	public String toString() {
		return "I" + TextUtil.and();
	}

	/**
	 * Prints the detailed description of the rule to the given output stream.
	 * <p>
	 * It illustrates the rule's structure and functionality in natural deduction
	 * format.
	 *
	 * @param out The {@link PrintStream} to write the description to.
	 */
	@Override
	public void printDescription(PrintStream out) {
		out.println("Rule " + this + " (\"introduce and\"):");
		String s1 = GAMMA + " " + TextUtil.infers() + " A     " + GAMMA + " " + TextUtil.infers() + " B";
		String s2 = GAMMA + " " + TextUtil.infers() + " A" + TextUtil.and() + "B";
		TextUtil.printFrac(out, 5, s1, s2);
		StringBuilder buf = new StringBuilder();
		buf.append("Rule " + this + " says that if you know A, and " + "you know B, then you can conclude A"
				+ TextUtil.and() + "B. " + "All premises and the conclusion use the same context " + GAMMA + ". ");
		buf.append("This rule has two premises.");
		out.print(TextUtil.wrap(buf));
	}

	/**
	 * Checks if the object is an instance of {@code IntroAnd_FOL}.
	 *
	 * @param obj The object to compare.
	 * @return true if the object is {@code IntroAnd_FOL}, false otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof IntroAnd_FOL;
	}

	/**
	 * Returns the hash code for the IntroAnd_FOL class.
	 *
	 * @return The hash code.
	 */
	@Override
	public int hashCode() {
		return IntroAnd_FOL.class.hashCode();
	}

}
