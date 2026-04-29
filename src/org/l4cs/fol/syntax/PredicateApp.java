package org.l4cs.fol.syntax;

import java.util.Arrays;

/**
 * Represents an atomic Formula composed of a Predicate Symbol and a list of
 * Term arguments. Its canonical identity is managed by a factory cache (like
 * the TermFactory's FunctionAppKey) and is based on its structure: the
 * predicate symbol and the arguments.
 */
public class PredicateApp extends FOLFormula {

	private final PredicateSymbol predicate;
	private final Term[] arguments;

	/**
	 * Constructs a new PredicateApp. Should be called only by FormulaFactory.
	 */
	PredicateApp(PredicateSymbol predicate, Term[] arguments) {
		super(FormulaKind.PREDICATEAPP);
		// Precondition: the factory ensures this matches before calling constructor
		if (predicate.arity() != arguments.length) {
			throw new IllegalArgumentException("Predicate symbol arity mismatch.");
		}
		this.predicate = predicate;
		this.arguments = arguments;
	}

	public PredicateSymbol predicateSymbol() {
		return this.predicate;
	}

	public Term[] arguments() {
		return arguments;
	}

	// -----------------------------------------------------------
	// Canonical Identity Methods
	// -----------------------------------------------------------

	@Override
	public int hashCode() {
		// Use a standard prime multiplier (31) and deep hash for array content
		final int prime = 31;
		int result = prime + predicate.hashCode();
		result = prime * result + Arrays.deepHashCode(arguments);
		return result;//
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		// Check against null and class type
		if (obj == null || getClass() != obj.getClass())
			return false;

		PredicateApp that = (PredicateApp) obj;

		// 1. Check Predicate Symbol: Must be the same canonical instance (by ID)
		if (!this.predicate.equals(that.predicate))
			return false;

		// 2. Check Arguments: Must be structurally identical (element by element)
		// Arrays.equals checks for same length AND element equality (using
		// element.equals())
		return Arrays.equals(this.arguments, that.arguments);//
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		// Use the symbol's name() for a cleaner output
		sb.append(predicate.name() != null ? predicate.name() : predicate.toString());
		sb.append("(");

		for (int i = 0; i < arguments.length; i++) {
			sb.append(arguments[i].toString());
			if (i < arguments.length - 1) {
				sb.append(", ");
			}
		}
		sb.append(")");
		return sb.toString();
	}//
}