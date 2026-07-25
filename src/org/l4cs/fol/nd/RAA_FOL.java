package org.l4cs.fol.nd;

import java.io.PrintStream;

import org.l4cs.fol.syntax.FOLFormula;
import org.l4cs.fol.syntax.FOLFormulaFactory;
import org.l4cs.util.TextUtil;

/**
 * The inference rule "reductio ad absurdum" in natural deduction for
 * first-order logic.
 * 
 * @author Stephen Siegel
 */
public class RAA_FOL extends FOLRule {

	public RAA_FOL(FOLFormulaFactory fac) {
		super(fac);
	}

	@Override
	public int arity() {
		return 1;
	}

	@Override
	public FOLViolation check(FOLSequent conclusion, FOLSequent... premises) {
		FOLViolation v = super.check(conclusion, premises);
		if (v != null)
			return v;
		FOLSequent premise = premises[0];
		FOLFormula f = premise.succedent(), a = conclusion.succedent();
		if (!fac.isFalse(f))
			return violation(conclusion, premises,
					fill("The premise's succedent should be " + TextUtil.bot() + ", but instead was " + f + "."));
		if (!isUnionWith(premise.antecedent(), conclusion.antecedent(), fac.not(a)))
			return violation(conclusion, premises,
					fill("The premise's antecedent must be the union of the " + "conclusion's antecedent, "
							+ conclusion.antecedent() + ", and the formula " + fac.not(a) + ", but was instead "
							+ premise.antecedent() + "."));
		return null;
	}

	@Override
	public String toString() {
		return "RAA";
	}

	@Override
	public void printDescription(PrintStream out) {
		out.println("Rule RAA (\"reductio ad absurdum\"):");
		String s1 = GAMMA + "," + TextUtil.not() + "A " + TextUtil.infers() + " " + TextUtil.bot();
		String s2 = GAMMA + " " + TextUtil.infers() + " A";
		TextUtil.printFrac(out, 5, s1, s2);
		StringBuilder buf = new StringBuilder();
		buf.append("Rule RAA says that if you start in some context ");
		buf.append(GAMMA + ", and assume " + TextUtil.not() + "A, ");
		buf.append("and from there you derive " + TextUtil.bot() + ", ");
		buf.append("then it must be the case that A holds in ");
		buf.append(GAMMA + ". The rule has one premise.");
		out.print(TextUtil.wrap(buf));
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof RAA_FOL;
	}

	@Override
	public int hashCode() {
		return RAA_FOL.class.hashCode();
	}

}
