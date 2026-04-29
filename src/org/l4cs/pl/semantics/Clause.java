package org.l4cs.pl.semantics;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.l4cs.pl.syntax.Formula;
import org.l4cs.pl.syntax.FormulaFactory;
import org.l4cs.pl.syntax.Proposition;

/**
 * Represents a CNF clause as an ordered set of literal IDs.
 * 
 * <p>
 * The literal ID is defined as follows. If p is a proposition, the literal ID
 * of p is i=1+ID(p). The literal ID of !p is -i. Notice that 0 is not a literal
 * ID. The reason for this is that there is a proposition with ID 0, but -0=0.
 * So we shift all the IDs up one.
 * </p>
 * 
 * <p>
 * Note that bar operator corresponds to negation of literal IDs, i.e., if the
 * literal ID of a literal l is i, the literal ID of \bar{l} is -i.
 * </p>
 * 
 * <p>
 * A Clause will never contain both p and !p. Such a clause represents true and
 * we forbid these from occurring in a CNF structure.
 * </p>
 * 
 * <p>
 * A Clause may be empty (in which case it represents false).
 * </p>
 * 
 * <p>
 * As a Clause is a set, there are no duplicate literals.
 * </p>
 * 
 * <p>
 * Clauses are immutable.
 * </p>
 * 
 * @author Stephen Siegel
 */
public class Clause implements Comparable<Clause> {

	/** The literal IDs, in strictly increasing order. */
	int[] data;

	/**
	 * Constructs new instance based on data
	 * 
	 * @param data a sequence of unique literal IDs in strictly increasing order
	 */
	Clause(int[] data) {
		this.data = data;
	}

	public boolean isEmpty() {
		return data.length == 0;
	}

	public boolean isUnit() {
		return data.length == 1;
	}

	public int first() {
		return data[0];
	}

	public Collection<Proposition> vars(FormulaFactory fac) {
		Collection<Proposition> result = new HashSet<Proposition>();
		for (int literalID : data) {
			int id = (literalID < 0 ? -literalID : literalID) - 1;
			result.add(fac.getProposition(id));
		}
		return result;
	}

	/**
	 * Provides a total order on Clauses. Clauses are ordered first by size. For
	 * Clauses of the same size, dictionary order is used.
	 */
	@Override
	public int compareTo(Clause that) {
		int n1 = data.length, n2 = that.data.length;
		if (n1 < n2)
			return -1;
		if (n1 > n2)
			return 1;
		for (int i = 0; i < n1; i++) {
			int a1 = data[i], a2 = that.data[i];
			if (a1 < a2)
				return -1;
			if (a1 > a2)
				return 1;
		}
		return 0;
	}

	/**
	 * Provides a "pretty" representation of this clause by writing to the given
	 * string buffer. This representation prints formulas using the formula factory,
	 * so looks natural and hides the literal ID representation.
	 * 
	 * @param fac the formula factory used to create formulas
	 * @param sb  the string buffer to write to
	 */
	void stringify(FormulaFactory fac, StringBuffer sb) {
		sb.append('[');
		for (int i = 0; i < data.length; i++) {
			if (i > 0)
				sb.append(',');
			int val = data[i];
			if (val < 0)
				sb.append(fac.not(fac.getProposition(-val - 1)).toString());
			else
				sb.append(fac.getProposition(val - 1).toString());
		}
		sb.append(']');
	}

	/**
	 * A simple representation, showing the literal IDs.
	 */
	@Override
	public String toString() {
		return data.toString();
	}

	/**
	 * If parity is true: does this clause contain the literal p? If parity is
	 * false: does this clause contain the literal !p?
	 * 
	 * @param p      the proposition to search for
	 * @param parity the sign of the proposition
	 * @return true iff this clause contains the proposition with the given sign
	 */
	public boolean contains(Proposition p, boolean parity) {
		int x = p.id() + 1;
		if (!parity)
			x = -x;
		return Arrays.binarySearch(data, x) >= 0;
	}

	/**
	 * Does this clause contain the given literal ID?
	 * 
	 * @param literalID a literal ID
	 * @return true iff this clause contains literalID
	 */
	boolean contains(int literalID) {
		return Arrays.binarySearch(data, literalID) >= 0;
	}

	/**
	 * Returns a Clause obtained by removing from this clause the given literalID.
	 * If the given literalID does not occur in this clause then this clause is
	 * returned. (This is OK because Clauses are immutable.)
	 * 
	 * @param literalID a literal ID
	 * @return a Clause which is same as this one except that literalID has been
	 *         removed
	 */
	Clause without(int literalID) {
		int idx = Arrays.binarySearch(data, literalID);
		if (idx < 0)
			return this;
		int newSize = data.length - 1;
		int[] newData = new int[newSize];
		for (int i = 0; i < idx; i++)
			newData[i] = data[i];
		for (int i = idx; i < newSize; i++)
			newData[i] = data[i + 1];
		return new Clause(newData);
	}

	// Factory methods...

	private static int literalID(FormulaFactory fac, Formula literal) {
		return fac.isProp(literal) ? 1 + fac.id(literal) : -1 - fac.id(fac.arg(literal));
	}

	/**
	 * Constructs a Clause representing the given CNF clause formula. Returns null
	 * if the clause contains both p and !p for some proposition p. Such a clause is
	 * equivalent to true and should not be included in a CNF.
	 * 
	 * @param fac       the formula factory used to manipulate formulas
	 * @param cnfClause a formula which is a CNF clause
	 * @return a Clause object representing cnfClause, or null
	 */
	public static Clause make(FormulaFactory fac, Formula cnfClause) {
		SortedSet<Integer> intSet = new TreeSet<Integer>();
		Iterator<Formula> iter = fac.cnfLiteralIterator(cnfClause);
		while (iter.hasNext()) {
			int val = literalID(fac, iter.next());
			if (intSet.contains(-val))
				return null;
			intSet.add(val);
		}
		int[] result = new int[intSet.size()];
		int i = 0;
		for (int val : intSet)
			result[i++] = val;
		return new Clause(result);
	}
}
