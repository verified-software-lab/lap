
package org.l4cs.fol.syntax;

//import org.l4cs.util.TextUtil;

//import org.l4cs.fol.syntax.Term.TermKind;

/**
 * Represents a canonical Variable term in First-Order Logic (FOL).
 */
public class Variable extends Term {

	// Variables are canonical symbols, but must also be Terms.
	private final FOLAbstractSymbol symbol;
	// is an intentional design pattern called Composition (or Delegation).
	// The alternative would be Inheritance, but that leads to a problem in this
	// specific case.

	/**
	 * Constructs a new Variable. Should be called only by Factory.
	 */
	Variable(String name) {
		super(TermKind.VAR);
		this.symbol = new AnonymousSymbol(name); // Use a helper symbol to manage ID/Name
	}

	@Override
	public int id() {
		return symbol.id();
	}

	@Override
	public String name() {
		return symbol.name();
	}

	@Override
	public String toString() {
		return name() != null ? name() : "V" + id();
	}

	// Canonical identity must be consistent with AbstractSymbol
	@Override
	public final int hashCode() {
		return symbol.hashCode();
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Variable that = (Variable) obj;
		return symbol.equals(that.symbol);
	}

	// Helper class to manage the canonical identity for Term subclasses!!!
	private static class AnonymousSymbol extends FOLAbstractSymbol {
		AnonymousSymbol(String name) {
			super(name);
		}
	}/**
		 * 3. Separation of Concerns: The AnonymousSymbol exists purely to house the
		 * canonical identity logic for a Term that isn't a symbol in its own right
		 * (like a FunctionSymbol is). It prevents us from having to expose
		 * AbstractSymbol's constructor more broadly or complicate the Variable class
		 * itself with inheritance gymnastics.
		 */

}