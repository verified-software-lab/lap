package org.l4cs.fol.syntax;

/**
 * The class FOLFormula represents a first-order logic formula. It is an
 * abstract class, and the various kinds of formulas are represented by concrete
 * subclasses.
 * 
 * @author Yuxin Zhou
 */
public abstract class FOLFormula {

	public static enum FormulaKind {
		FALSE, AND, OR, NOT, IMPLIES, EXISTS, FORALL, PREDICATEAPP
	}

	private FormulaKind kind;

	FOLFormula(FormulaKind kind) {
		this.kind = kind;
	}

	public FormulaKind kind() {
		return kind;
	}

	public boolean isFreeFor(Term t, Variable x) {
		// TODO Auto-generated method stub
		return false;
	}
}
