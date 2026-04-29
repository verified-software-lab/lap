package org.l4cs.fol.syntax;

import static org.l4cs.fol.syntax.FOLFormula.FormulaKind.AND;
import static org.l4cs.fol.syntax.FOLFormula.FormulaKind.IMPLIES;
import static org.l4cs.fol.syntax.FOLFormula.FormulaKind.OR;

import org.l4cs.util.TextUtil;

public class FOLBinaryFormula extends FOLFormula {

	private FOLFormula arg0;

	private FOLFormula arg1;

	public FOLBinaryFormula(FormulaKind kind, FOLFormula arg0, FOLFormula arg1) {
		super(kind);
		assert kind == AND || kind == OR || kind == IMPLIES;
		this.arg0 = arg0;
		this.arg1 = arg1;
	}

	public FOLFormula arg0() {
		return arg0;
	}

	public FOLFormula arg1() {
		return arg1;
	}

	@Override
	public int hashCode() {
		return kind().hashCode() + arg0.hashCode() + arg1.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof FOLBinaryFormula))
			return false;
		FOLBinaryFormula that = (FOLBinaryFormula) obj;// why do this
		return kind().equals(that.kind()) && arg0.equals(that.arg0) && arg1.equals(that.arg1);
	}

	@Override
	public String toString() {
		String result;
		if (arg0 instanceof FOLBinaryFormula)
			result = "(" + arg0 + ")";// arg0.toString();？？？？？
		else
			result = arg0.toString();
		switch (kind()) {//
		case AND:
			result += TextUtil.AND;
			break;
		case OR:
			result += TextUtil.OR;
			break;
		case IMPLIES:
			result += TextUtil.IMPLIES;
			break;
		default:
			assert false;//
		}
		if (arg1 instanceof FOLBinaryFormula && arg1.kind() != kind())
			result += "(" + arg1 + ")";
		else
			result += arg1.toString();
		return result;
	}

}
