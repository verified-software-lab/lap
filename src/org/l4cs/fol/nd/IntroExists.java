package org.l4cs.fol.nd;

import java.io.PrintStream;
import java.util.Set;

import org.l4cs.fol.syntax.FOLFormula;
import org.l4cs.fol.syntax.FOLFormulaFactory;
import org.l4cs.fol.syntax.QuantifiedFormula;
import org.l4cs.fol.syntax.Term;
import org.l4cs.fol.syntax.Variable;
import org.l4cs.util.TextUtil;

/**
 * Represents the natural deduction inference rule ∃-Introduction (IntroExists)
 * for First-Order Logic (FOL). This rule say that if φ holds for a specific
 * term t, then you can conclude ∃x.φ.
 *
 * @author Yuxin Zhou
 */
public class IntroExists extends FOLRule {

	/**
	 * Constructs the IntroExists rule.
	 *
	 * @param fac The factory used to create {@link FOLFormula} instances.
	 */
	public IntroExists(FOLFormulaFactory fac) {
		super(fac);
	}

	/**
	 * The rule requires two premises: the existence premise and the assumption
	 * premise.
	 *
	 * @return The arity of the rule, which is 1.
	 */
	@Override
	public int arity() {
		return 1;
	}

	/**
	 * Checks if the given conclusion can be derived from the premises using the
	 * ∃-Introduction rule. The rule states: From Γ ⊢ φ[t/x], conclude Γ ⊢ ∃x.φ,
	 * where t is free for x in φ. This method performs the following validations:
	 * <ol>
	 * <li>Antecedent Check: Γ must match</li>
	 * <li>Conclusion must be an existential formula (∃x.φ)</li>
	 * <li>Premise must be a substitution instance φ[t/x] for some t</li>
	 * <li>t must be free for x in φ</li>
	 * </ol>
	 *
	 * @param conclusion the conclusion sequent
	 * @param premises   the premise sequents (should contain exactly one premise)
	 * @return null if the rule application is valid, otherwise a FOLViolation
	 *         describing the issue
	 */
	@Override
	public FOLViolation check(FOLSequent conclusion, FOLSequent... premises) {
		FOLViolation v = super.check(conclusion, premises);
		if (v != null)
			return v;

		FOLSequent premise = premises[0];
		Set<FOLFormula> gamma_c = conclusion.antecedent();
		Set<FOLFormula> gamma_p = premise.antecedent();

		// 1. Antecedent Check: Γ must match
		if (!gamma_c.equals(gamma_p)) {
			return violation(conclusion, premises, fill("The premise's antecedent, " + gamma_p
					+ ", and the conclusion's antecedent, " + gamma_c + ", should be equal, but are not."));
		}

		FOLFormula f_conclusion = conclusion.succedent();
		FOLFormula f_premise = premise.succedent();

		if (f_conclusion.kind() != FOLFormula.FormulaKind.EXISTS) {
			return violation(conclusion, premises, fill("The conclusion's succedent, " + f_conclusion
					+ ", must be an existential formula (of the form ∃x.φ)."));
		}

		QuantifiedFormula qf_conclusion = (QuantifiedFormula) f_conclusion;
		Variable x = qf_conclusion.quantifiedV();
		FOLFormula body = qf_conclusion.body();

		Term t = fac.findSubstitutionTerm(body, x, f_premise);

		if (t == null) {
			return violation(conclusion, premises,
					fill("The premise's succedent, " + f_premise
							+ ", is not a substitution instance of the conclusion's body, " + body
							+ ", with respect to variable " + x + "."));
		}

		if (!fac.isFreeFor(t, x, body)) {
			return violation(conclusion, premises,
					fill("The term " + t + " is not free for the variable " + x + " in the formula " + body + "."));
		}

		return null;
	}

	@Override
	public String toString() {
		return "∃_I";
	}

	@Override
	public void printDescription(PrintStream out) {
		out.println("Rule " + this + " (\"introduce exists\") :");
		String s1 = "Γ ⊢ φ[t/x]";
		String s2 = "Γ ⊢ ∃x.φ";
		TextUtil.printFrac(out, 5, s1, s2);
		out.println("Side condition: t is free for x in φ.");
	}
}