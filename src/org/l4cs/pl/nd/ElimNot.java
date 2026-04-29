package org.l4cs.pl.nd;

import java.io.PrintStream;
import java.util.Set;

import org.l4cs.pl.syntax.Formula;
import org.l4cs.pl.syntax.FormulaFactory;
import org.l4cs.util.TextUtil;

/**
 * Inference rule eliminate-not in natural deduction for propositional logic.
 * 
 * @author Stephen Siegel
 */
public class ElimNot extends Rule {

	public ElimNot(FormulaFactory fac) {
		super(fac);
	}

	@Override
	public int arity() {
		return 2;
	}

	@Override
	public Violation check(Sequent conclusion, Sequent... premises) {
		Violation v = super.check(conclusion, premises);
		if (v != null)
			return v;
		Sequent s0 = premises[0], s1 = premises[1];
		Set<Formula> gamma0 = s0.antecedent(), gamma1 = s1.antecedent(), gamma = conclusion.antecedent();
		if (!gamma.equals(gamma0))
			return violation(conclusion, premises, fill("The first premise's antecedent, " + gamma0
					+ ", and the conclusion's antecedent, " + gamma + ", should be equal, but are not."));
		if (!gamma.equals(gamma1))
			return violation(conclusion, premises, fill("The second premise's antecedent, " + gamma1
					+ ", and the conclusion's antecedent, " + gamma + ", should be equal, but are not."));
		Formula a = s0.succedent(), not = s1.succedent();
		if (!not.equals(fac.not(a)))
			return violation(conclusion, premises,
					fill("The second premise's succedent, " + not
							+ " should be the negation of the first premise's succedent," + " i.e., it should be "
							+ fac.not(a) + ". But it is not."));
		return null;
	}

	@Override
	public String toString() {
		return "E" + NOT;
	}

	@Override
	public void printDescription(PrintStream out) {
		out.println("Rule " + this + " (\"eliminate not\"):");
		String s1 = GAMMA + " " + TextUtil.infers() + " A     " + GAMMA + " " + TextUtil.infers() + " " + NOT + "A";
		String s2 = GAMMA + " " + TextUtil.infers() + " B";
		TextUtil.printFrac(out, 5, s1, s2);
		StringBuffer buf = new StringBuffer();
		buf.append("Rule " + this + " says that if in some context " + GAMMA + ", you can dervive both A and " + NOT
				+ "A, then you can derive anything in " + GAMMA + ".");
		out.print(TextUtil.fill(buf, DEFAULT_WIDTH));
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ElimNot;
	}

	@Override
	public int hashCode() {
		return ElimNot.class.hashCode();
	}
}
