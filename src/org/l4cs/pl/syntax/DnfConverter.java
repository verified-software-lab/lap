package org.l4cs.pl.syntax;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class DnfConverter {

	private FormulaFactory fac;

	DnfConverter(FormulaFactory fac) {
		this.fac = fac;
	}

	/**
	 * An iterator over the literals of a DNF clause.
	 */
	class LiteralIterator implements Iterator<Formula> {

		Formula clause;

		/**
		 * Creates new iterator, given a DNF clause
		 * 
		 * @param clause any DNF clause, including true
		 */
		LiteralIterator(Formula clause) {
			assert fac.isDnfClause(clause);
			this.clause = clause;
		}

		@Override
		public boolean hasNext() {
			return !fac.isTrue(clause);
		}

		@Override
		public Formula next() {
			if (fac.isTrue(clause))
				throw new NoSuchElementException();
			Formula literal;
			if (fac.isLiteral(clause)) {
				literal = clause;
				clause = fac.trueFormula();
			} else {
				assert fac.isAnd(clause);
				literal = fac.arg0(clause);
				clause = fac.arg1(clause);
			}
			assert fac.isLiteral(literal);
			return literal;
		}
	}

	/**
	 * An iterator over the clauses of a CNF formula.
	 */
	class DnfClauseIterator implements Iterator<Formula> {

		Formula dnf;

		/**
		 * Creates new iterator, given a CNF formula.
		 * 
		 * @param cnf any CNF formula, including true, false, etc.
		 */
		DnfClauseIterator(Formula dnf) {
			assert fac.isDnf(dnf);
			this.dnf = dnf;
		}

		@Override
		public boolean hasNext() {
			return !fac.isFalse(dnf);
		}

		@Override
		public Formula next() {
			if (fac.isFalse(dnf))
				throw new NoSuchElementException();
			Formula clause;
			if (fac.isDnfClause(dnf)) {
				clause = dnf;
				dnf = fac.falseFormula();
			} else {
				assert fac.isOr(dnf);
				clause = fac.arg0(dnf);
				dnf = fac.arg1(dnf);
			}
			assert fac.isDnfClause(clause);
			return clause;
		}
	}

	/**
	 * Returns an iterator over the clauses of the DNF formula. For the "false"
	 * formula, this will be an empty iterator.
	 * 
	 * @param dnf a DNF formula
	 * @return iterator over clauses
	 */
	public Iterator<Formula> dnfClauseIterator(Formula dnf) {
		return new DnfClauseIterator(dnf);
	}

	/**
	 * Returns an iterator over the literals of a DNF clause. For the "true" clause,
	 * this will be an empty iterator.
	 * 
	 * @param dnfClause a DNF clause (conjunction of literals or true)
	 * @return iterator over the literals
	 */
	public Iterator<Formula> dnfLiteralIterator(Formula dnfClause) {
		return new LiteralIterator(dnfClause);
	}

	/**
	 * Constructs the right-associated conjunction of two formulas, each of which is
	 * a conjunction of terms with at least one term.
	 * 
	 * @param f1 conjunction of terms with at least one term
	 * @param f2 ditto
	 * @return the right-associated conjunction of the terms from f1 followed by
	 *         those of f2
	 */
	private Formula concatAndAux(Formula f1, Formula f2) {
		return fac.isAnd(f1) ? fac.and(fac.arg0(f1), concatAndAux(fac.arg1(f1), f2)) : fac.and(f1, f2);
	}

	/**
	 * Constructs the right-associated conjunction of two formulas, each of which is
	 * a conjunction of n>=0 terms. Note the case n=0 is allowed, i.e., either or
	 * both of f1 and f2 may be "true".
	 * 
	 * @param f1 conjunction of 0 or more terms
	 * @param f2 ditto
	 * @return the right-associated conjunction of the terms from f1 followed by
	 *         those of f2
	 */
	private Formula concatAnd(Formula f1, Formula f2) {
		if (fac.isTrue(f1))
			return f2;
		if (fac.isTrue(f2))
			return f1;
		return concatAndAux(f1, f2);
	}

	/**
	 * Constructs the right-associated disjunction of two formulas, each of which is
	 * a disjunction of terms with at least one term.
	 * 
	 * @param f1 disjunction of terms with at least one term
	 * @param f2 ditto
	 * @return the right-associated disjunction of the terms from f1 followed by
	 *         those of f2
	 */
	private Formula concatOrAux(Formula f1, Formula f2) {
		return fac.isOr(f1) ? fac.or(fac.arg0(f1), concatOrAux(fac.arg1(f1), f2)) : fac.or(f1, f2);
	}

	/**
	 * Constructs the right-associated disjunction of two formulas, each of which is
	 * a disjunction of n>=0 terms. Note the case n=0 is allowed, i.e., either or
	 * both of f1 and f2 may be "false".
	 * 
	 * @param f1 disjunction of 0 or more terms
	 * @param f2 ditto
	 * @return the right-associated disjunction of the terms from f1 followed by
	 *         those of f2
	 */
	private Formula concatOr(Formula f1, Formula f2) {
		if (fac.isFalse(f1))
			return f2;
		if (fac.isFalse(f2))
			return f1;
		return concatOrAux(f1, f2);
	}

	/**
	 * Given a clause c with at least one literal, and DNF formula c1|...|cn, with
	 * n>=1, returns the conjunction (and) as a DNF formula, (c&c1)|...|(c&cn).
	 * 
	 * @param clause a DNF clause with at least one literal
	 * @param dnf    a DNF formula with at least one clause
	 * @return a DNF formula equivalent to the conjunction of clause and dnf
	 */
	private Formula clauseANDdnfAux(Formula clause, Formula dnf) {
		return !fac.isOr(dnf) ? concatAnd(clause, dnf)
				: fac.or(concatAnd(clause, fac.arg0(dnf)), clauseANDdnfAux(clause, fac.arg1(dnf)));
	}

	/**
	 * Given a DNF clause c and DNF formula c1|...|cn, with n>=1, returns the
	 * conjunction (and) as a DNF formula, (c&c1)|...|(c&cn).
	 * 
	 * @param clause any DNF clause (including possibly false)
	 * @param dnf    a DNF formula with at least one clause
	 * @return a DNF formula equivalent to the conjunction of clause and dnf
	 */
	private Formula clauseANDdnf(Formula clause, Formula dnf) {
		return fac.isTrue(clause) ? dnf : clauseANDdnfAux(clause, dnf);
	}

	/**
	 * Computes a DNF formula equivalent to the conjunction of the two given ones,
	 * in the case where the two given ones each have at least one clause.
	 * 
	 * @param dnf0 a DNF formula with at least one clause
	 * @param dnf1 a DNF formula with at least one clause
	 * @return the conjunction of dnf0 and dnf1, converted to equivalent DNF
	 */
	private Formula dnfANDdnfAux(Formula dnf0, Formula dnf1) {
		return !fac.isOr(dnf0) ? clauseANDdnf(dnf0, dnf1)
				: concatOr(clauseANDdnf(fac.arg0(dnf0), dnf1), dnfANDdnfAux(fac.arg1(dnf0), dnf1));
	}

	/**
	 * Computes a DNF formula equivalent to the conjunction of two given DNF
	 * formulas.
	 * 
	 * @param dnf0 a DNF formula
	 * @param dnf1 a DNF formula
	 * @return a DNF formula equivalent to dnf0&dnf1
	 */
	private Formula dnfANDdnf(Formula dnf0, Formula dnf1) {
		return fac.isFalse(dnf0) || fac.isFalse(dnf1) ? fac.falseFormula() : dnfANDdnfAux(dnf0, dnf1);
	}

	/**
	 * Converts any formula to an equivalent DNF formula.
	 * 
	 * @param a a formula
	 * @return a DNF formula equivalent to a
	 */
	public Formula dnf(Formula a) {
		a = fac.nnf(a);
		if (fac.isTrue(a) || fac.isFalse(a) || fac.isLiteral(a))
			return a;
		Formula dnf0 = dnf(fac.arg0(a)), dnf1 = dnf(fac.arg1(a));
		return fac.isOr(a) ? concatOr(dnf0, dnf1) : dnfANDdnf(dnf0, dnf1);
	}

}
