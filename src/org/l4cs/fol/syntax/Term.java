package org.l4cs.fol.syntax;

//import org.l4cs.fol.syntax.Formula.TermKind;

/**
 * Abstract base class for all terms in First-Order Logic (FOL).
 * Note: Terms like Variable and Constant are also canonical symbols managed by the factory.
 */
public abstract class Term {

	public static enum TermKind {
		CONST, FUNCTIONAPP, VAR
	}

	private TermKind kind;

	Term(TermKind kind) {
		this.kind = kind;
	}

	public TermKind kind() {
		return kind;
	}
	
	/**
	 * All terms (Variable, Constant, FunctionApp) must have a way to generate a 
	 * unique string representation for printing and internal use.
	 * @return A unique string representation of the term.
	 */
	@Override
	public abstract String toString();
	
	// New methods to expose AbstractSymbol properties via delegation/inheritance+?
	public abstract int id();
	public abstract String name();
}