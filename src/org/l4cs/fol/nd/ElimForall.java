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
 * Implements the Universal Elimination (E∀) rule. Rule: From Γ ⊢ ∀x φ, conclude
 * Γ ⊢ φ[t/x].
 */
public class ElimForall extends FOLRule {

	public ElimForall(FOLFormulaFactory fac) {
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
		Set<FOLFormula> gamma_c = conclusion.antecedent();
		Set<FOLFormula> gamma_p = premise.antecedent();

		// 1. Antecedent Check: Γ must match
		if (!gamma_c.equals(gamma_p)) {
			return violation(conclusion, premises,
					fill("The antecedents must match. Premise has " + gamma_p + ", conclusion has " + gamma_c));
		}

		FOLFormula f_prem = premise.succedent();
		FOLFormula f_conc = conclusion.succedent();

		// 2. Premise must be a Universal Quantifier (∀x φ)
		if (f_prem.kind() != FOLFormula.FormulaKind.FORALL) {
			return violation(conclusion, premises,
					fill("The premise's succedent " + f_prem + " must be a universal formula (∀x φ)."));
		}

		QuantifiedFormula qf = (QuantifiedFormula) f_prem;
		Variable x = qf.quantifiedV();
		FOLFormula phi = qf.body();

		// 3. Substitution Check: Is conclusion φ[t/x] for some t?
		Term t = fac.findSubstitutionTerm(phi, x, f_conc);

		if (t == null) {
			return violation(conclusion, premises, fill("The conclusion " + f_conc
					+ " is not a valid substitution instance of the body " + phi + " for variable " + x));
		}

		// 4. Capture Check: Is t free for x in φ?
		if (!fac.isFreeFor(t, x, phi)) {
			return violation(conclusion, premises, fill("Substitution failed: The term " + t + " is not free for " + x
					+ " in " + phi + " (variable capture would occur)."));
		}

		return null;
	}

	@Override
	public String toString() {
		return "E∀";
	}

	@Override
	public void printDescription(PrintStream out) {
		out.println("Rule " + this + " (\"eliminate forall\") :");
		String s1 = "Γ ⊢ ∀x φ";
		String s2 = "Γ ⊢ φ[t/x]";
		TextUtil.printFrac(out, 5, s1, s2);
		out.println("Where t is free for x in φ.");
	}
}