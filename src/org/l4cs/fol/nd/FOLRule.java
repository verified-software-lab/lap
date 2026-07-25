package org.l4cs.fol.nd;

import java.io.PrintStream;
import java.util.Set;

import org.l4cs.fol.syntax.FOLFormula;
import org.l4cs.fol.syntax.FOLFormulaFactory;
import org.l4cs.util.TextUtil;

public abstract class FOLRule {

	final static String GAMMA = TextUtil.GAMMA;
	final static int terminalWidth = TextUtil.terminalWidth();

	protected FOLFormulaFactory fac;

	public FOLRule(FOLFormulaFactory fac) {
		this.fac = fac;
	}

	/**
	 * 
	 * Is set2 the union of set1 and the singleton set containing a?
	 * 
	 * @param set2 any set of formulas (possibly empty)
	 * @param set1 any set of formulas (possibly empty)
	 * @param a    any formula
	 * @return true iff set2 is the union of set1 and {a}
	 */
	protected static boolean isUnionWith(Set<FOLFormula> set2, Set<FOLFormula> set1, FOLFormula a) {
		if (set1.contains(a))
			return set1.equals(set2);
		return set2.containsAll(set1) && set2.contains(a) && set2.size() == set1.size() + 1;
	}

	protected static String fill(String s) {
		return TextUtil.wrap(s).toString();
	}

	protected static String fill(StringBuilder s) {
		return TextUtil.wrap(s).toString();
	}

	protected FOLViolation violation(FOLSequent conclusion, FOLSequent[] premises, String explanation) {
		return new FOLViolation(this, conclusion, premises, explanation);
	}

	protected FOLViolation violation(FOLSequent conclusion, FOLSequent[] premises, StringBuffer explanation) {
		return new FOLViolation(this, conclusion, premises, explanation);
	}

	/**
	 * The number of premises in this rule.
	 * 
	 * @return number of premises
	 */
	public abstract int arity();

	/**
	 * Does this rule relate the given premises to the conclusion? If not, return a
	 * Violation object explaining the violation. This basic implementation checks
	 * that the arguments are not null and that the number of premises matches the
	 * arity of the rule. It should be overridden by concrete rules.
	 * 
	 * @param conclusion the judgment that is proposed to be the conclusion
	 * @param premises   the premises that the rule may relate to the conclusion
	 * @return null if the rule correctly relates the premises to the conclusion,
	 *         else returns a Violation object explaining why not
	 */
	public FOLViolation check(FOLSequent conclusion, FOLSequent... premises) {
		if (conclusion == null)
			return violation(conclusion, premises, "null conclusion");
		if (premises == null)
			return violation(conclusion, premises, "null premises");
		if (premises.length != arity())
			return violation(conclusion, premises,
					"Expected " + arity() + " premise(s) but received " + premises.length + ".");
		return null;
	}

	/**
	 * A short name for this rule, such as "Ax".
	 * 
	 * @return short name
	 */
	public abstract String toString();

	/**
	 * A longer multi-line description of the rule.
	 * 
	 * @return description
	 */
	public abstract void printDescription(PrintStream out);

}
