package org.l4cs.pl.syntax;

import static org.l4cs.pl.syntax.Formula.FormulaKind.AND;
import static org.l4cs.pl.syntax.Formula.FormulaKind.IMPLIES;
import static org.l4cs.pl.syntax.Formula.FormulaKind.OR;

import org.l4cs.util.TextUtil;

/**
 * A binary formula is a formula of the form (arg0 AND arg1), (arg0 OR arg1) or
 * (arg0 IMPLIES arg1).
 *
 * @author Stephen Siegel
 *
 */
public class BinaryFormula extends Formula {

	private Formula arg0;

	private Formula arg1;

	public BinaryFormula(FormulaKind kind, Formula arg0, Formula arg1) {
		super(kind);
		assert kind == AND || kind == OR || kind == IMPLIES;
		this.arg0 = arg0;
		this.arg1 = arg1;
	}

	public Formula arg0() {
		return arg0;
	}

	public Formula arg1() {
		return arg1;
	}

	@Override
	public int hashCode() {
		return kind().hashCode() + arg0.hashCode() + arg1.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BinaryFormula))
			return false;
		BinaryFormula that = (BinaryFormula) obj;
		return kind().equals(that.kind()) && arg0.equals(that.arg0)
				&& arg1.equals(that.arg1);
	}

	@Override
	public String toString() {
		String result;
		if (arg0 instanceof BinaryFormula)
			result = "(" + arg0 + ")";
		else
			result = arg0.toString();
		switch (kind()) {
			case AND :
				result += TextUtil.AND;
				break;
			case OR :
				result += TextUtil.OR;
				break;
			case IMPLIES :
				result += TextUtil.IMPLIES;
				break;
			default :
				assert false;
		}
		if (arg1 instanceof BinaryFormula && arg1.kind() != kind())
			result += "(" + arg1 + ")";
		else
			result += arg1.toString();
		return result;
	}

}
