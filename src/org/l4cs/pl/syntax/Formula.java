package org.l4cs.pl.syntax;

/**
 * The class Formula represents a propositional logic formula. It is an
 * abstract class, and the various kinds of formulas are represented by concrete
 * subclasses.
 * 
 * @author Stephen Siegel
 */
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
