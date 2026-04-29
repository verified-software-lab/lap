package org.l4cs.pl.nd;

import java.io.PrintStream;
import java.util.Set;

import org.l4cs.pl.syntax.Formula;
import org.l4cs.pl.syntax.FormulaFactory;
import org.l4cs.util.TextUtil;

/**
 * Inference rule Ax of natural deduction for propositional logic.
 * 
 * @author Stephen Siegel
 */
public class Ax extends Rule {

	public Ax(FormulaFactory fac) {
		super(fac);
	}

	@Override
	public int arity() {
		return 0;
	}

	@Override
	public Violation check(Sequent conclusion, Sequent... premises) {
		Violation v = super.check(conclusion, premises);
		if (v != null)
			return v;
		Set<Formula> context = conclusion.antecedent();
		Formula a = conclusion.succedent();
		if (!context.contains(a))
			return violation(conclusion, premises, fill(
					"The conclusion's antecedent, " + context + ", must contain the formula " + a + " but does not."));
		return null;
	}

	@Override
	public String toString() {
		return "Ax";
	}

	@Override
	public void printDescription(PrintStream out) {
		out.println("Rule Ax:");
		String s1 = "";
		String s2 = GAMMA + "," + "A " + TextUtil.infers() + " A";
		TextUtil.printFrac(out, 5, s1, s2);
		StringBuffer buf = new StringBuffer();
		buf.append("Rule Ax says that at any time, you may conclude any "
				+ "formula that is currently being assumed to hold. ");
		buf.append("The Ax rule has no premises and holds whenever the "
				+ "conclusion's succedent belongs to the set of formulas " + "which form the conclusion's antecedent.");
		out.print(TextUtil.fill(buf, DEFAULT_WIDTH));
		out.println();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Ax;
	}

	@Override
	public int hashCode() {
		return Ax.class.hashCode();
	}
}
