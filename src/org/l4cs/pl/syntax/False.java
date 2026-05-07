package org.l4cs.pl.syntax;

import static org.l4cs.pl.syntax.Formula.FormulaKind.FALSE;

import org.l4cs.util.TextUtil;

/**
 * The class {@code False} represents the false formula.
 *
 * @author Stephen Siegel
 */
public class False extends Formula {

	False() {
		super(FALSE);
	}

	@Override
	public String toString() {
		return TextUtil.BOT;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof False;
	}

	@Override
	public int hashCode() {
		return FALSE.hashCode();
	}
}
