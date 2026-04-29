package org.l4cs.pl.syntax;

import static org.l4cs.pl.syntax.Formula.FormulaKind.NOT;

import org.l4cs.util.TextUtil;

public class NotFormula extends Formula {

	private Formula arg;

	public NotFormula(Formula arg) {
		super(NOT);
		assert arg != null;
		this.arg = arg;
	}

	public Formula arg() {
		return arg;
	}

	@Override
	public int hashCode() {
		return NOT.hashCode() + arg.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof NotFormula))
			return false;
		NotFormula that = (NotFormula) obj;
		return arg.equals(that.arg);
	}

	@Override
	public String toString() {
		if (arg instanceof False)
			return TextUtil.TOP; // "true"
		String result = TextUtil.NOT;
		if (arg instanceof BinaryFormula)
			result += "(" + arg + ")";
		else
			result += arg;
		return result;
	}

}
