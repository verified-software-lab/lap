package org.l4cs.fol.syntax;

import org.l4cs.util.TextUtil;

/**
 * Represents a canonical Predicate Symbol (e.g., P, Q, LessThan) in FOL.
 * 
 * @author Yuxin Zhou
 */
public class PredicateSymbol extends FOLAbstractSymbol {
	
	private final int arity;

	/**
	 * Constructs a new PredicateSymbol. Should be called only by FormulaFactory (or TermFactory, for consistency).
	 */
	PredicateSymbol(String name, int arity) {
		super(name); // Will auto-assigns ID.
		this.arity = arity;
	}
	
	public int arity() {
		return arity;
	}
	public String toString() {
		return name() != null ? name() + "/" + arity : "P" + TextUtil.subscript(id()) + "/" + arity;
	}


}
