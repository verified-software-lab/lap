package org.l4cs.fol.syntax;

import static org.l4cs.fol.syntax.FOLFormula.FormulaKind.FALSE;

import org.l4cs.util.TextUtil;

public class FOLFalse extends FOLFormula {

	FOLFalse() {
		super(FALSE);
	}

	@Override
	public String toString() {
		return TextUtil.BOT;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof FOLFalse;
	}

	@Override
	public int hashCode() {
		return FALSE.hashCode();
	}
}
