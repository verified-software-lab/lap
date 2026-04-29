package org.l4cs.pl.syntax;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class CnfConverter {

	private FormulaFactory fac;

	CnfConverter(FormulaFactory fac) {
		this.fac = fac;
	}

	/**
	 * An iterator over the literals of a CNF clause.
	 */
	class LiteralIterator implements Iterator<Formula> {

		Formula clause;

		/**
		 * Creates new iterator, given a CNF clause
		 * 
		 * @param clause any CNF clause, including false
		 */
		LiteralIterator(Formula clause) {
			assert fac.isCnfClause(clause);
			this.clause = clause;
		}

		@Override
		public boolean hasNext() {
			return !fac.isFalse(clause);
		}

		@Override
		public Formula next() {
			if (fac.isFalse(clause))
				throw new NoSuchElementException();
			Formula literal;
			if (fac.isLiteral(clause)) {
				literal = clause;
				clause = fac.falseFormula();
			} else {
				assert fac.isOr(clause);
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
	class CnfClauseIterator implements Iterator<Formula> {

		Formula cnf;

		/**
		 * Creates new iterator, given a CNF formula.
		 * 
		 * @param cnf any CNF formula, including true, false, etc.
		 */
		CnfClauseIterator(Formula cnf) {
			assert fac.isCnf(cnf);
			this.cnf = cnf;
		}

		@Override
		public boolean hasNext() {
			return !fac.isTrue(cnf);
		}

		@Override
		public Formula next() {
			if (fac.isTrue(cnf))
				throw new NoSuchElementException();
			Formula clause;
			if (fac.isCnfClause(cnf)) {
				clause = cnf;
				cnf = fac.trueFormula();
			} else {
				assert fac.isAnd(cnf);
				clause = fac.arg0(cnf);
				cnf = fac.arg1(cnf);
			}
			assert fac.isCnfClause(clause);
			return clause;
		}
	}

	/**
	 * Returns an iterator over the clauses of the CNF formula. For the "true"
	 * formula, this will be an empty iterator.
	 * 
	 * @param cnf a CNF formula
	 * @return iterator over clauses
	 */
	public Iterator<Formula> cnfClauseIterator(Formula cnf) {
		return new CnfClauseIterator(cnf);
	}

	/**
	 * Returns an iterator over the literals of a CNF clause. For the "false"
	 * clause, this will be an empty iterator.
	 * 
	 * @param cnfClause a CNF clause (disjunction of literals or false)
	 * @return iterator over the literals
	 */
	public Iterator<Formula> cnfLiteralIterator(Formula cnfClause) {
		return new LiteralIterator(cnfClause);
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
	 * Given a clause c with at least one literal, and CNF formula c1&...&cn, with
	 * n>=1, returns the disjunction (or) as a CNF formula, (c|c1)&...&(c|cn).
	 * 
	 * @param clause a CNF clause with at least one literal
	 * @param cnf    a CNF formula with at least one clause
	 * @return a CNF formula equivalent to the disjunction of clause and cnf
	 */
	private Formula clauseORcnfAux(Formula clause, Formula cnf) {
		return !fac.isAnd(cnf) ? concatOr(clause, cnf)
				: fac.and(concatOr(clause, fac.arg0(cnf)), clauseORcnfAux(clause, fac.arg1(cnf)));
	}

	/**
	 * Given a CNF clause c and CNF formula c1&...&cn, with n>=1, returns the
	 * disjunction (or) as a CNF formula, (c|c1)&...&(c|cn).
	 * 
	 * @param clause any CNF clause (including possibly false)
	 * @param cnf    a CNF formula with at least one clause
	 * @return a CNF formula equivalent to the disjunction of clause and cnf
	 */
	private Formula clauseORcnf(Formula clause, Formula cnf) {
		return fac.isFalse(clause) ? cnf : clauseORcnfAux(clause, cnf);
	}

	/**
	 * Computes a CNF formula equivalent to the disjunction of the two given ones,
	 * in the case where the two given ones each have at least one clause.
	 * 
	 * @param cnf0 a CNF formula with at least one clause
	 * @param cnf1 a CNF formula with at least one clause
	 * @return the disjunction of cnf0 and cnf1, converted to equivalent CNF
	 */
	private Formula cnfORcnfAux(Formula cnf0, Formula cnf1) {
		return !fac.isAnd(cnf0) ? clauseORcnf(cnf0, cnf1)
				: concatAnd(clauseORcnf(fac.arg0(cnf0), cnf1), cnfORcnfAux(fac.arg1(cnf0), cnf1));
	}

	/**
	 * Computes a CNF formula equivalent to the disjunction of two given CNF
	 * formulas.
	 * 
	 * @param cnf0 a CNF formula
	 * @param cnf1 a CNF formula
	 * @return a CNF formula equivalent to cnf0|cnf1
	 */
	private Formula cnfORcnf(Formula cnf0, Formula cnf1) {
		return fac.isTrue(cnf0) || fac.isTrue(cnf1) ? fac.trueFormula() : cnfORcnfAux(cnf0, cnf1);
	}

	/**
	 * Converts any formula to an equivalent CNF formula.
	 * 
	 * @param a a formula
	 * @return a CNF formula equivalent to a
	 */
	public Formula cnf(Formula a) {
		a = fac.nnf(a);
		if (fac.isFalse(a) || fac.isTrue(a) || fac.isLiteral(a))
			return a;
		Formula cnf0 = cnf(fac.arg0(a)), cnf1 = cnf(fac.arg1(a));
		return fac.isAnd(a) ? concatAnd(cnf0, cnf1) : cnfORcnf(cnf0, cnf1);
	}

}
