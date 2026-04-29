package org.l4cs.pl.nd;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A derivation in the proof system Natural Deduction for Propositional Logic. A
 * derivation is a recursive data structure. It consists of a rule, a
 * conclusion, and a sequence of 0 or more subderivations.
 * 
 * @author Stephen Siegel
 */
public class Derivation {

	// Static...

	/**
	 * Auxiliary method used by {@link #getStepNumbers(Derivation)} to assign a
	 * unique "line number" to each sub-derviation of a derivation. The derivation
	 * may be thought of as a DAG. The numbers are assigned in a DFS post-order,
	 * i.e., a node is assigned a number after all of its children have been
	 * assigned numbers.
	 * 
	 * @param d      the derivation to number
	 * @param seen   a map from derivations already seen to the associated line
	 *               number
	 * @param lineno the next number that will be assigned to a node
	 * @return a number one greater than the largest number assigned to a node that
	 *         is reachable from d (including d)
	 */
	private static int assignNumbers(Derivation d, Map<Derivation, Integer> seen, int lineno) {
		if (seen.containsKey(d))
			return lineno;
		for (Derivation sub : d.subderivations) {
			lineno = assignNumbers(sub, seen, lineno);
		}
		seen.put(d, lineno);
		return lineno + 1;
	}

	/**
	 * Utility method to number all sub-derivations of a derivation.
	 * 
	 * @param d the derivation to number
	 * @return a map from the set of all sub-derivations of {@code d} (including
	 *         {@code d}) to the assigned number.
	 */
	static Map<Derivation, Integer> getStepNumbers(Derivation d) {
		Map<Derivation, Integer> seen = new HashMap<>();
		assignNumbers(d, seen, 1);
		return seen;
	}

	// Instance fields...

	/**
	 * The rule of this derivation.
	 */
	protected Rule rule;

	/**
	 * The conclusion of this derivation.
	 */
	protected Sequent conclusion;

	/**
	 * The sequence of sub-derviations of this derivations. The number of
	 * sub-derivations equals the arity of {@link #rule}.
	 */
	protected Derivation[] subderivations;

	// Constructor...

	/**
	 * Constructs a new derivation from the given rule, conclusion, and immediate
	 * subderivations. Precondition: the subderivations are valid (i.e., they have
	 * been checked) and the conclusion follows from the subderivations' conclusions
	 * by the rule (i.e., this is a valid instance of the rule). * @param rule the
	 * rule for the root node of the new derivation
	 * 
	 * @param rule           the rule of the derivation
	 * @param conclusion     the conclusion of the derivation
	 * @param subderivations derivations whose conclusions form the premises for the
	 *                       instance of the rule
	 */
	Derivation(Rule rule, Sequent conclusion, Derivation... subderivations) {
		this.rule = rule;
		this.conclusion = conclusion;
		this.subderivations = subderivations;
	}

	// Instance methods...

	/**
	 * Auxiliary method for {@link #reachableDerivations()}. Adds to {@code reached}
	 * all sub-derivations reachable from {@code d}.
	 * 
	 * @param reached set of sub-derivations found so far; this set will be modified
	 * @param d       the derivation to explore
	 */
	private static void reach(Set<Derivation> reached, Derivation d) {
		if (!reached.add(d))
			return;
		for (Derivation child : d.subderivations)
			reach(reached, child);
	}

	/**
	 * Computes the set of all sub-derivations of this derivation, including this
	 * derivation.
	 * 
	 * @return the set of sub-derivations
	 */
	public Set<Derivation> reachableDerivations() {
		Set<Derivation> result = new HashSet<>();
		reach(result, this);
		return result;
	}

	/**
	 * The number of nodes in this derivation, i.e., the number of sub-derivations,
	 * including this derivation itself.
	 * 
	 * @return the size of the derivation
	 */
	public int size() {
		int result = 1;
		for (Derivation d : subderivations)
			result += d.size();
		return result;
	}

	/**
	 * Is another derivation equivalent to this one? Two derivations are equivalent
	 * if they have equal rules, the conclusions are equal (as formulas), they have
	 * the same number of immediate sub-derivations, and corresponding
	 * sub-derivations are equivalent. Hence they are the same in every way that
	 * matters.
	 * 
	 * @param obj any object (can even be {@code null}; but this method will return
	 *            {@code false} if {@code obj} is not a {@link Derivation}.
	 * @return {@code true} iff {@code obj} is a {@code Derivation} equivalent to
	 *         this one
	 */
	public boolean equiv(Object obj) {
		if (!(obj instanceof Derivation))
			return false;
		Derivation that = (Derivation) obj;
		if (!rule.equals(that.rule))
			return false;
		if (!conclusion.equals(that.conclusion))
			return false;
		int n = subderivations.length;
		if (n != that.subderivations.length)
			return false;
		for (int i = 0; i < n; i++)
			if (!subderivations[i].equiv(that.subderivations[i]))
				return false;
		return true;
	}

	// Printing methods...

	/**
	 * Prints this derivation in a linear format.
	 * 
	 * @param out where to print
	 */
	public void printLinear(PrintStream out) {
		new LinearPrinter(out, this);
	}

	/**
	 * Prints this derivation in Fitch format.
	 * 
	 * @param out where to print
	 */
	public void printFitch(PrintStream out) {
		new FitchPrinter(out, this);
	}

	/**
	 * Prints this derivation in Tree format.
	 * 
	 * @param out    where to print
	 * @param number number the lines?
	 */
	public void printTree(PrintStream out, boolean number) {
		new TreePrinter(out, this, number);
	}

	/**
	 * Prints this derivation in Hierarchy format.
	 * 
	 * @param out    where to print
	 * @param number number the lines?
	 */
	public void printHierarchy(PrintStream out, boolean number) {
		new HierarchyPrinter(out, this, number);
	}

	/**
	 * Prints this derivation in Tuple format.
	 * 
	 * @param out    where to print
	 * @param number number the lines?
	 */
	public void printTuple(PrintStream out, boolean number) {
		new TuplePrinter(out, this, number);
	}
}
