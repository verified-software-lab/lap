package org.l4cs.pl.syntax;

import static org.l4cs.pl.syntax.Formula.FormulaKind.PROP;

/**
 * Every proposition has an ID number and a name. Each of these is unique within
 * the set of all propositions created by a single {@link FormulaFactory}. Since
 * {@link Proposition}s are "flyweighted", == can be used to compare for
 * equality.
 */
public class Proposition extends Formula {

	/** This proposition's unique ID number, a nonnegative int. */
	private int id;

	/** This proposition's unique name, a string. */
	private String name;

	Proposition(int id, String name) {
		super(PROP);
		this.id = id;
		this.name = name;
	}

	public int id() {
		return id;
	}

	public String name() {
		return name;
	}

	public String toString() {
		return name;
	}
}
