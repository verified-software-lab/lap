package org.l4cs.pl.semantics;

import org.l4cs.pl.syntax.Formula;

/**
 * An exception that is thrown when a formula was expected to be in CNF but was
 * not.
 * 
 * @author Stephen Siegel
 */
public class CnfException extends RuntimeException {

	private static final long serialVersionUID = -6357210208172436865L;

	public CnfException(Formula f) {
		super("A formula was expected to be in Conjunctive Normal Form but was not:\n" + f);
	}

}
