package org.l4cs.pl.syntax;

public class Formula {

	public static enum FormulaKind {
		FALSE, PROP, AND, OR, NOT, IMPLIES
	}

	private FormulaKind kind;

	Formula(FormulaKind kind) {
		this.kind = kind;
	}

	public FormulaKind kind() {
		return kind;
	}
}
