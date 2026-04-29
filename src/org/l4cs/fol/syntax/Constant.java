package org.l4cs.fol.syntax;

//import org.l4cs.fol.syntax.Term.TermKind;

/**
 * Represents a canonical Constant term in First-Order Logic (FOL).
 */
public class Constant extends Term {

	private final FOLAbstractSymbol symbol;

	/**
	 * Constructs a new Constant. Should be called only by TermFactory.
	 */
	Constant(String name) {
		super(TermKind.CONST);
		this.symbol = new AnonymousSymbol(name);
	}
	
	@Override
	public int id() { return symbol.id(); }
	
	@Override
	public String name() { return symbol.name(); }

	@Override
	public String toString() {
		return name() != null ? name() : "C" + id();
	}
	
	@Override
	public final int hashCode() { return symbol.hashCode(); }
	
	@Override
	public final boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		Constant that = (Constant) obj;
		return symbol.equals(that.symbol);
	}
	
	private static class AnonymousSymbol extends FOLAbstractSymbol {
		AnonymousSymbol(String name) { super(name); }
	}
}