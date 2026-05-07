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
 * Represents the natural deduction inference rule $\exists$-Elimination (ElimExists) for
 * First-Order Logic (FOL).
 * <p>
 * This rule allows the deduction of a conclusion $C$ from the assumption that
 * a certain property $A(x)$ holds (or rather, from a proof assuming $A(x)$),
 * provided that we know that the property $\exists x. A(x)$ holds in the main context.
 * </p>
 * <h3>Formal Structure:</h3>
 * <p>
 * The rule states that if we know $\exists x. A(x)$ in context $\Gamma$, and we can
 * derive $C$ assuming an instance of $A(x)$ (say, $A(c)$), then $C$ is derivable
 * in the original context $\Gamma$.
 * <pre>
 * $\frac{\Gamma \vdash \exists x. A(x) \quad [\text{Assumption: } A(c)] \vdash C}{\Gamma \vdash C}$
 * </pre>
 * Where $c$ is a fresh variable not free in $\Gamma$ or $C$.
 * </p>
 *
 * @author Yuxin Zhou
 */
public class ElimExists extends FOLRule {

    /**
     * Constructs the ElimExists rule.
     *
     * @param fac The factory used to create {@link FOLFormula} instances.
     */
    public ElimExists(FOLFormulaFactory fac) {
        super(fac);
    }

    /**
     * The rule requires two premises: the existence premise and the assumption premise.
     *
     * @return The arity of the rule, which is 2.
     */
    @Override
    public int arity() {
        return 2;
    }

    /**
     * Checks if a given {@link FOLSequent} conclusion correctly follows from
     * two premises using the $\exists$-Elimination rule.
     * <p>
     * This method validates that:
     * 1. The conclusion's context matches the initial context of the premises.
     * 2. The first premise must conclude an existential formula ($\exists x. A(x)$).
     * 3. The second premise must contain the assumption $A(c)$ as part of its context.
     * 4. The assumption variable $c$ must be fresh relative to the overall context.
     * 5. The conclusion derived from the second premise must hold in the original context.
     * </p>
     *
     * @param conclusion The final sequent being checked.
     * @param premises The two premises required by the rule:
     *                 {@code premises[0]}: Must conclude the existential statement.
     *                 {@code premises[1]}: The sequent derived under the assumption instance.
     * @return {@code null} if the rule applies correctly, or a {@link FOLViolation} detailing the inconsistency.
     */
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