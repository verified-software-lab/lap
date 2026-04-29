package org.l4cs.fol.syntax;

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
