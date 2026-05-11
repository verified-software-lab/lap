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
 * Represents the natural deduction inference rule $\forall$-Introduction (IntroForall) for
 * First-Order Logic (FOL).
 * <p>
 * This rule allows the generalization of a conclusion from a specific instance to a
 * universal statement.
 * </p>
 *
 * @author Yuxin Zhou
 */
public class IntroForall extends FOLRule {

    /**
     * Constructs the IntroForall rule.
     *
     * @param fac The factory used to create {@link FOLFormula} instances.
     */
    public IntroForall(FOLFormulaFactory fac) {
        super(fac);
    }

    /**
     * The rule requires one premise: the derivation from the assumption instance.
     *
     * @return The arity of the rule, which is 1.
     */
    @Override
    public int arity() {
        return 1;
    }

    /**
     * Checks if the given conclusion can be derived from the premises using the ∀-Introduction rule.
     * The rule states: From Γ ⊢ φ[y/x], conclude Γ ⊢ ∀x φ, where y is free for x in φ, and y does not occur free in Γ or φ.
     * This method performs the following validations:
     * 1. Antecedent Check: Γ must match
     * 2. Conclusion must be a universal formula (∀x φ)
     * 3. Premise must be a substitution instance φ[y/x] using some variable y
     * 4. y must be free for x in φ
     * 5. y must not occur free in Γ
     * 6. y must not occur free in φ (if y != x)
     *
     * @param conclusion the conclusion sequent
     * @param premises the premise sequents (should contain exactly one premise)
     * @return null if the rule application is valid, otherwise a FOLViolation describing the issue
     */
    @Override
    public FOLViolation check(FOLSequent conclusion, FOLSequent... premises) {
        FOLViolation v = super.check(conclusion, premises);
        if (v != null) return v;

        FOLSequent premise = premises[0];
        Set<FOLFormula> gamma = conclusion.antecedent();

		// 1. Antecedent Check: Γ must match
        if (!gamma.equals(premise.antecedent())) {
            return violation(conclusion, premises, fill("The antecedents (assumptions) must match."));
        }

        FOLFormula f_conc = conclusion.succedent();
        FOLFormula f_prem = premise.succedent();

        // 2. The conclusion's succedent must be a universal formula.
        if (f_conc.kind() != FOLFormula.FormulaKind.FORALL) {
            return violation(conclusion, premises, fill("The conclusion's succedent must be a universal formula."));
        }

        QuantifiedFormula qf = (QuantifiedFormula) f_conc;
        Variable x = qf.quantifiedV();
        FOLFormula body = qf.body();

		// 3. Substitution Check: Is premise a substitution instance A[y/x] using some variable y.?
        Term t = fac.findSubstitutionTerm(body, x, f_prem);
        if (t == null || !(t instanceof Variable)) {
            return violation(conclusion, premises, fill("The premise " + f_prem + " must be a substitution instance using a variable y."));
        }

        Variable y = (Variable) t;
        // Side condition 1: y is free for x in φ.
        if (!fac.isFreeFor(y, x, body)) {
            return violation(conclusion, premises, fill("Side condition 1: " + y + " is not free for " + x + " in " + body));
        }
        // Side condition 2: y does not occur free in Γ (assumptions).
        for (FOLFormula ass : gamma) {
            if (fac.freeVars(ass).contains(y)) {
                return violation(conclusion, premises, fill("Side condition 2: " + y + " must not occur free in the assumptions: " + ass));
            }
        }
        // Side condition 2: y does not occur free in φ (the body).
        // Note: If y != x, we must ensure y isn't already free in φ before substitution.
        if (y != x && fac.freeVars(body).contains(y)) {
            return violation(conclusion, premises, fill("Side condition 2: " + y + " must not occur free in the body of the conclusion" + body));
        }
        return null;
    }

    @Override
    public String toString() {
        return "I_∀";
    }

    @Override
    public void printDescription(PrintStream out) {
        out.println("Rule " + this + " (\"introduce forall\") :");
        String s1 = "Γ ⊢ φ[y/x]";
        String s2 = "Γ ⊢ ∀x φ";
        TextUtil.printFrac(out, 5, s1, s2);
        out.println("Side condition 1: y is free for x in φ.");
        out.println("Side condition 2: y does not occur free in Γ or φ.");//other textbook: y is not free in Γ or ∀x φ.
    }
}