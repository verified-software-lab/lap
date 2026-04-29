package org.l4cs.pl.nd;

import java.io.PrintStream;
import java.util.Set;

import org.l4cs.pl.syntax.Formula;
import org.l4cs.pl.syntax.FormulaFactory;
import org.l4cs.util.TextUtil;

/**
 * An inference rule in the proof system natural deduction for propositional
 * logic.
 * 
 * <p>
 * An instance of this class represents a single rule. Each rule has a name, a
 * human-readable description, a fixed arity >= 0 (the number of premises of the
 * rule), and a method to check an application of the rule. The
 * {@link #check(Sequent, Sequent...)} method consumes a conclusion sequent, and
 * a sequence of premise sequents (the length of the sequence being the arity of
 * the rule). This method will return either {@code null} (if the application of
 * the rule is correct) or a {@link Violation} (if the application is
 * incorrect).
 * </p>
 * 
 * <p>
 * This is an abstract class. A basic implementation of
 * {@link #check(Sequent, Sequent...)} is provided, which only checks the number
 * of premises and that the conclusion is not {@code null}. A subclass should
 * override this method.
 * </p>
 * 
 * @author Stephen Siegel
 */
public abstract class Rule {

	/*
	 * The following static fields are here for convenience. Subclasses may use
	 * these elements in their description.
	 */

	final static int DEFAULT_WIDTH = TextUtil.DEFAULT_WIDTH;
	final static String GAMMA = TextUtil.GAMMA;
	// final static String INFERS = TextUtil.INFERS;
	final static String AND = TextUtil.AND;
	final static String OR = TextUtil.OR;
	final static String IMPLIES = TextUtil.IMPLIES;
	final static String NOT = TextUtil.NOT;
	final static String TOP = TextUtil.TOP;
	final static String BOT = TextUtil.BOT;

	/**
	 * The factory used to extract information and create and manipulate formulas.
	 */
	protected FormulaFactory fac;

	public Rule(FormulaFactory fac) {
		this.fac = fac;
	}

	/**
	 * Is set2 the union of set1 and the singleton set containing a?
	 * 
	 * @param set2 any set of formulas (possibly empty)
	 * @param set1 any set of formulas (possibly empty)
	 * @param a    any formula
	 * @return true iff set2 is the union of set1 and {a}
	 */
	protected static boolean isUnionWith(Set<Formula> set2, Set<Formula> set1, Formula a) {
		if (set1.contains(a))
			return set1.equals(set2);
		return set2.containsAll(set1) && set2.contains(a) && set2.size() == set1.size() + 1;
	}

	protected static String fill(String s) {
		return TextUtil.fill(s, DEFAULT_WIDTH).toString();
	}

	protected static String fill(StringBuffer s) {
		return TextUtil.fill(s, DEFAULT_WIDTH).toString();
	}

	protected Violation violation(Sequent conclusion, Sequent[] premises, String explanation) {
		return new Violation(this, conclusion, premises, explanation);
	}

	protected Violation violation(Sequent conclusion, Sequent[] premises, StringBuffer explanation) {
		return new Violation(this, conclusion, premises, explanation);
	}

	/**
	 * The number of premises in this rule.
	 * 
	 * @return number of premises
	 */
	public abstract int arity();

	/**
	 * Does this rule relate the given premises to the conclusion? If not, return a
	 * {@link Violation} object explaining the violation. This basic implementation
	 * checks that the arguments are not null and that the number of premises
	 * matches the arity of the rule. It should be overridden by concrete rules.
	 * 
	 * @param conclusion the judgment that is proposed to be the conclusion
	 * @param premises   the premises that the rule may relate to the conclusion
	 * @return {@code null} if the rule correctly relates the premises to the
	 *         conclusion, else returns a {@link Violation} object explaining why
	 *         not
	 */
	public Violation check(Sequent conclusion, Sequent... premises) {
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
