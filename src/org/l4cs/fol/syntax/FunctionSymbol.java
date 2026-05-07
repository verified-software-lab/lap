package org.l4cs.fol.syntax;

import org.l4cs.util.TextUtil;

/**
 * Represents a canonical Function Symbol (e.g., f, g, sum) in FOL.
 * 
 * @author Yuxin Zhou
 */
public class FunctionSymbol extends FOLAbstractSymbol {

	private final int arity;

	/**
	 * Constructs a new FunctionSymbol. Should be called only by TermFactory.
	 */
	FunctionSymbol(String name, int arity) {
		super(name); // Auto-assigns ID
		this.arity = arity;
	}

	public int arity() {
		return arity;
	}

	public String toString() {
		return name() != null ? name() + "/" + arity : "f" + TextUtil.subscript(id()) + "/" + arity;
	}
	//id, equals (), and so on, are already in AbstractSymbol.

}
