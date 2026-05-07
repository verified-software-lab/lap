package org.l4cs.fol.syntax;

import static org.l4cs.fol.syntax.FOLFormula.FormulaKind.NOT;

import org.l4cs.util.TextUtil;

/**
 * The class NotFormula represents a formula of the form "not phi", where phi is any
 *
 * @author Stephen Siegel
 */
public class FOLNotFormula extends FOLFormula {

	private FOLFormula arg;

	public FOLNotFormula(FOLFormula arg) {
		super(NOT);
		assert arg != null;
		this.arg = arg;
	}

	public FOLFormula arg() {
		return arg;
	}

	@Override
	public int hashCode() {
		return NOT.hashCode() + arg.hashCode();//wait
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof FOLNotFormula))
			return false;
		FOLNotFormula that = (FOLNotFormula) obj;
		return arg.equals(that.arg);
	}

	@Override
	public String toString() {
		if (arg instanceof FOLFalse)
			return TextUtil.TOP; // "true"
		String result = TextUtil.NOT;
		if (arg instanceof FOLBinaryFormula)
			result += "(" + arg + ")";
		else
			result += arg;//no need toSting
		return result;
	}

}
