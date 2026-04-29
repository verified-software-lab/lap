package org.l4cs.pl.semantics;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.l4cs.pl.syntax.Formula;
import org.l4cs.pl.syntax.FormulaFactory;
import org.l4cs.pl.syntax.Proposition;

/**
 * Structured representation of a CNF Formula. The structure consists of a
 * (possibly empty) set of Clauses.
 * 
 * @author Stephen Siegel
 */
public class CnfStruct {

	/**
	 * The clauses comprising this CNF Structure. The clauses are ordered in
	 * increasing order, using the order defined in the Clause class.
	 */
	protected SortedSet<Clause> clauses;

	protected CnfStruct(SortedSet<Clause> clauses) {
		this.clauses = clauses;
	}

	public boolean isEmpty() {
		return clauses.isEmpty();
	}

	public boolean containsEmpty() {
		return !clauses.isEmpty() && clauses.first().isEmpty();
	}

	/**
	 * Gets the first clause. This CnfStruct must be nonempty.
	 * 
	 * @return
	 */
	public Clause first() {
		return clauses.first();
	}

	public Collection<Proposition> vars(FormulaFactory fac) {
		Collection<Proposition> result = new HashSet<Proposition>();
		for (Clause c : clauses)
			result.addAll(c.vars(fac));
		return result;
	}

	/**
	 * Returns a CNF Structure that is obtained by first removing any clause
	 * containing the specified literal, then removing from the remaining clauses
	 * the negation of that literal.
	 * 
	 * @param literalID a literal ID number
	 * @return new CNF Structure obtained as described above
	 */
	CnfStruct withTrue(int literalID) {
		SortedSet<Clause> newClauses = new TreeSet<Clause>();
		for (Clause clause : clauses) {
			if (!clause.contains(literalID))
				newClauses.add(clause.without(-literalID));
		}
		return new CnfStruct(newClauses);
	}

	/**
	 * Returns a CNF Structure that results from setting the proposition p to the
	 * given truth value.
	 * 
	 * @return new CNF Structure obtained as described above
	 * @param p   the proposition to be set
	 * @param val the truth value being assigned to p
	 * @return the new CNF structure
	 */
	public CnfStruct withPropSet(Proposition p, boolean val) {
		int literalID = p.id() + 1;
		if (!val)
			literalID = -literalID;
		return withTrue(literalID);
	}

	/**
	 * String representation using the internal representation (literalIDs). It is
	 * probably better to use the static method
	 * {@link #toString(FormulaFactory, CnfStruct)}.
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append('{');
		boolean first = true;
		for (Clause clause : clauses) {
			if (first)
				first = false;
			else
				sb.append(", ");
			sb.append(clause.toString());
		}
		sb.append('}');
		return sb.toString();
	}

	// Static methods...

	/**
	 * Creates a new CNF Structure from the given CNF formula.
	 * 
	 * @param fac the formula factory used to build formulas
	 * @param cnf a CNF formula
	 * @return the CNF structure representing the formula
	 */
	public static CnfStruct make(FormulaFactory fac, Formula cnf) {
		if (!fac.isCnf(cnf))
			throw new CnfException(cnf);
		SortedSet<Clause> clauseSet = new TreeSet<Clause>();
		Iterator<Formula> iter = fac.cnfClauseIterator(cnf);
		while (iter.hasNext()) {
			Clause clause = Clause.make(fac, iter.next());
			if (clause != null) // null means clause is equivalent to true
				clauseSet.add(clause);
		}
		return new CnfStruct(clauseSet);
	}

	/**
	 * Pretty representation of a CNF Structure using the actual names of the
	 * propositions. This is the preferred way to display a CNF Structure.
	 * 
	 * @param cs any CnfStruct
	 * @return nice string representation
	 */
	public static String toString(FormulaFactory fac, CnfStruct cs) {
		StringBuffer sb = new StringBuffer();
		sb.append('{');
		boolean first = true;
		for (Clause clause : cs.clauses) {
			if (first)
				first = false;
			else
				sb.append(", ");
			clause.stringify(fac, sb);
		}
		sb.append('}');
		return sb.toString();
	}
}
