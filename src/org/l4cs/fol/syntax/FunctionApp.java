package org.l4cs.fol.syntax;

import java.util.Arrays;

/**
 * Represents a composite Function Application term (e.g., f(a, x)). Its
 * canonical identity is managed by the TermFactory's FunctionAppKey cache, and
 * is based on its structure: the function symbol and the argument terms.
 * 
 * @author Yuxin Zhou
 */
public class FunctionApp extends Term {

	private final FunctionSymbol function;
	private final Term[] arguments;

	/**
	 * Constructs a new FunctionApp. Should be called only by TermFactory.
	 */
	FunctionApp(FunctionSymbol function, Term[] arguments) {
		super(TermKind.FUNCTIONAPP);
		// Precondition: the factory ensures this matches before calling constructor
		if (function.arity() != arguments.length) {
			throw new IllegalArgumentException("Function symbol arity mismatch.");
		}
		this.function = function;
		this.arguments = arguments;
	}

	public FunctionSymbol functionSymbol() {
		return this.function;
	}

	public Term[] arguments() {
		return arguments;
	}

	// -----------------------------------------------------------
	// Canonical Identity Methods
	// -----------------------------------------------------------

	/**
	 * For composite terms, the identity is based on structure. We use the
	 * combination of the function symbol's ID and a deep hash of the arguments.
	 */
	@Override
	public int hashCode() {
		// Use a better prime multiplier (31 is common) and deep hash for array
		// 31 * function.hashCode() + Arrays.deepHashCode(arguments) is the standard
		// structure
		final int prime = 31;
		int result = prime + function.hashCode();
		result = prime * result + Arrays.deepHashCode(arguments);//wait
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;//or obj instanceof FunctionApp

		FunctionApp that = (FunctionApp) obj;

		// 1. Check Function Symbol: Must be the same canonical instance (by ID)
		if (!this.function.equals(that.function))
			return false;

		// 2. Check Arguments: Must be structurally identical (element by element)
		// Arrays.equals checks for same length AND element equality (using
		// element.equals())
		return Arrays.equals(this.arguments, that.arguments);
	}

	// -----------------------------------------------------------
	// Term Base Class Overrides
	// -----------------------------------------------------------

	@Override
	public int id() {
		// As a composite term, it doesn't have a unique symbol ID. Return a structural
		// ID.
		return hashCode();
	}

	@Override
	public String name() {
		// Composite terms do not have a simple name.
		return null;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		// Use the symbol's name() for a cleaner output
		sb.append(function.name() != null ? function.name() : function.toString());
		sb.append("(");

		for (int i = 0; i < arguments.length; i++) {
			sb.append(arguments[i].toString());
			if (i < arguments.length - 1) {
				sb.append(", ");
			}
		}
		sb.append(")");
		return sb.toString();
	}
	//could instead: return function.toString() + arguments;
}