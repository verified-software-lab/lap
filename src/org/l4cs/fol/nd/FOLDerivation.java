package org.l4cs.fol.nd;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.l4cs.pl.nd.Derivation;

/**
 * A derivation in the proof system Natural Deduction for FOL Logic. A
 * derivation is a recursive data structure. It consists of a rule, a
 * conclusion, and a sequence of 0 or more subderivations.
 */
public class FOLDerivation {

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
	private static int assignNumbers(FOLDerivation d, Map<FOLDerivation, Integer> seen, int lineno) {
		if (seen.containsKey(d))
			return lineno;
		for (FOLDerivation sub : d.subderivations) {
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
	static Map<FOLDerivation, Integer> getStepNumbers(FOLDerivation d) {
		Map<FOLDerivation, Integer> seen = new HashMap<>();
		assignNumbers(d, seen, 1);
		return seen;
	}

	// Instance fields...

	protected FOLRule rule;

	protected FOLSequent conclusion;

	protected FOLDerivation[] subderivations;

	// Constructor...

	/**
	 * Constructs a new derivation from the given rule, conclusion, and
	 * subderivations. Precondition: the subderivations are valid (i.e., they have
	 * been checked) and the conclusion follows from the subderivations' conclusions
	 * by the rule (i.e., this is a valid instance of the rule). * @param rule the
	 * rule for the root node of the new derivation
	 * 
	 * @param conclusion     the conclusion of the derivation
	 * @param subderivations derivations whose conclusions form the premises for the
	 *                       instance of the rule
	 */
	FOLDerivation(FOLRule rule, FOLSequent conclusion, FOLDerivation... subderivations) {
		this.rule = rule;
		this.conclusion = conclusion;
		this.subderivations = subderivations;
	}

	public FOLRule rule() {
		return rule;
	}

	public FOLSequent conclusion() {
		return conclusion;
	}

	public FOLDerivation[] subderivations() {
		return subderivations;
	}

	// Instance methods...

	/**
	 * Auxiliary method for {@link #reachableDerivations()}. Adds to {@code reached}
	 * all sub-derivations reachable from {@code d}.
	 * 
	 * @param reached set of sub-derivations found so far; this set will be modified
	 * @param d       the derivation to explore
	 */
	private static void reach(Set<FOLDerivation> reached, FOLDerivation d) {
		if (!reached.add(d))
			return;
		for (FOLDerivation child : d.subderivations)
			reach(reached, child);
	}

	/**
	 * Computes the set of all sub-derivations of this derivation, including this
	 * derivation.
	 * 
	 * @return the set of sub-derivations
	 */
	public Set<FOLDerivation> reachableDerivations() {
		Set<FOLDerivation> result = new HashSet<>();
		reach(result, this);
		return result;
	}

	public int size() {
		int result = 1;
		for (FOLDerivation d : subderivations)
			result += d.size();
		return result;
	}

	public boolean equiv(Object obj) {
		if (!(obj instanceof FOLDerivation))
			return false;
		FOLDerivation that = (FOLDerivation) obj;
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
	 * @param number the conclusions?
	 */
	public void printTree(PrintStream out, boolean number) {
		new TreePrinter(out, this, number);
	}

	/**
	 * Prints this derivation in Hierarchy format.
	 * 
	 * @param out    where to print
	 * @param number the conclusions?
	 */
	public void printHierarchy(PrintStream out, boolean number) {
		new HierarchyPrinter(out, this, number);
	}

	/**
	 * Prints this derivation in Tuple format.
	 * 
	 * @param out    where to print
	 * @param number the conclusions?
	 */
	public void printTuple(PrintStream out, boolean number) {
		new TuplePrinter(out, this, number);
	}
}
