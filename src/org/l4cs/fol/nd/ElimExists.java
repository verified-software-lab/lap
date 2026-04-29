package org.l4cs.fol.nd;

import java.io.PrintStream;
import java.util.Set;

import org.l4cs.fol.syntax.FOLFormula;
import org.l4cs.fol.syntax.FOLFormulaFactory;
import org.l4cs.fol.syntax.QuantifiedFormula;
import org.l4cs.fol.syntax.Term;
import org.l4cs.fol.syntax.Variable;
import org.l4cs.util.TextUtil;

public class ElimExists extends FOLRule {

	public ElimExists(FOLFormulaFactory fac) {
		super(fac);
	}

	@Override
	public int arity() {
		return 2;
	}

//	public FOLFormula Get_phi_y (Set<FOLFormula> sub_gamma, Set<FOLFormula> gamma) {
//		FOLFormula phi_y = null;
//		for (FOLFormula f : sub_gamma) {
//			if (!gamma.contains(f)) {
//				if (phi_y != null) {
//					return null;//violation(conclusion, premises,
//							//fill("The sub-proof antecedent contains too many new assumptions."));
//				}
//				phi_y = f;
//			}
//		}
//		return phi_y;
//	}

	@Override
	public FOLViolation check(FOLSequent conclusion, FOLSequent... premises) {
		FOLViolation v = super.check(conclusion, premises);
		if (v != null)
			return v;

		FOLSequent prem_exists = premises[0];
		FOLSequent prem_subproof = premises[1];

		Set<FOLFormula> gamma = conclusion.antecedent();
		FOLFormula theta = conclusion.succedent();

		if (!prem_exists.antecedent().equals(gamma)) {
			return violation(conclusion, premises,
					fill("The first premise's antecedent must match the conclusion's antecedent Γ."));
		}
		if (prem_exists.succedent().kind() != FOLFormula.FormulaKind.EXISTS) {
			return violation(conclusion, premises, fill("The first premise must be an existential formula."));
		}

		QuantifiedFormula exists_phi = (QuantifiedFormula) prem_exists.succedent();
		Variable x = exists_phi.quantifiedV();
		FOLFormula phi = exists_phi.body();

		if (!prem_subproof.succedent().equals(theta)) {
			return violation(conclusion, premises, fill("The second premise must derive the same conclusion θ."));
		}

		Set<FOLFormula> sub_gamma = prem_subproof.antecedent();

		FOLFormula phi_y = null;

		if (fac.freeVars(phi).contains(x)) {

			for (FOLFormula f : sub_gamma) {
				if (!gamma.contains(f)) {
					if (phi_y != null) {
						return violation(conclusion, premises,
								fill("The sub-proof antecedent contains too many new assumptions."));
					}
					phi_y = f;
				}
			}
			if (phi_y == null) {
				return violation(conclusion, premises,
						fill("The antecedent of premise 2 is missing the assumption φ[y/x]."));
			}

		} else {
			phi_y = phi;
			if (!gamma.equals(sub_gamma))
				return violation(conclusion, premises,
						fill("The varible " + x + " does not not occur free in phi, therefore phi[y/x]=phi,"
								+ "so the sets Gamma and Gamma,phi[y/x] should be the same, but they are not."));
		}

		Term t = fac.findSubstitutionTerm(phi, x, phi_y);
		if (t == null || !(t instanceof Variable)) {
			return violation(conclusion, premises,
					fill("The assumption " + phi_y + " is not a valid instance of φ using a variable y."));
		}
		Variable y = (Variable) t;

		if (!fac.isFreeFor(y, x, phi)) {
			return violation(conclusion, premises,
					fill("This step violates Side condition 2 as " + y + " is not free for " + x + " in " + phi));
		}
		for (FOLFormula ass : gamma) {
			if (fac.freeVars(ass).contains(y)) {
				return violation(conclusion, premises,
						fill("This step violates Side condition 1 as " + y + " occurs free in assumption " + ass));
			}
		}
		if (fac.freeVars(theta).contains(y)) {
			return violation(conclusion, premises, fill("This step violates Side condition 1 as " + y
					+ " does occur free in the conclusion's succedent θ (" + theta + ")"));
		}
		if (fac.freeVars(phi).contains(y)) {
			return violation(conclusion, premises,
					fill("This step violates Side condition 1 as " + y + " occurs free in the body φ (" + phi + ")"));
		}

		return null;
	}

	@Override
	public String toString() {
		// [CHANGE] Requested by Advisor: Use subscript style E_∃
		return "E_∃";
	}

	@Override
	public void printDescription(PrintStream out) {
		out.println("Rule " + this + " (\"eliminate exists\") :");
		String s1 = "Γ ⊢ ∃x φ    Γ, φ[y/x] ⊢ θ";
		String s2 = "Γ ⊢ θ";
		TextUtil.printFrac(out, 5, s1, s2);
		out.println("Side condition 1: y must not occur free in Γ, φ, or θ;");
		out.println("Side condition 2: y must be free for x in φ.");
	}
}