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
 * <h3>Formal Structure:</h3>
 * <p>
 * If we can derive a conclusion $B$ in context $\Gamma$ by assuming $A(a)$ holds,
 * where $a$ is a constant **newly introduced** (and thus arbitrary) and not free
 * in $\Gamma$ or $B$, then we can conclude that $\forall x. B$ holds in the original context $\Gamma$.
 * </p>
 * <pre>
 * $\frac{\Gamma, A(a) \vdash B}{\Gamma \vdash \forall x. B}$
 * </pre>
 * Where $a$ is a fresh constant, and $\forall x$ is the universal quantifier.
 * <p>
 * **Crucial Constraint:** The derivation of $B$ must not depend on any assumption
 * that relies on the specific properties of the constant $a$ (otherwise, the
 * generalization is invalid).
 * </p>
 *
 * @author (Generated Example)
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
     * Checks if a derivation is valid under the $\forall$-Introduction rule.
     * Requires:
     * 1. The assumption A(a) must be present in the set of assumptions for the derivation of B.
     * 2. The conclusion B must be derivable from the assumptions minus A(a).
     * 3. The final conclusion must be $\forall x. B$.
     *
     * @param derivation The set of assumptions leading to the conclusion.
     * @param assumptionAssumption The specific assumption used for generalization (e.g., P(a)).
     * @param finalConclusion The overall desired conclusion ($\forall x. B$).
     * @return True if the rule is correctly applied.
     */
    @Override
    public FOLViolation check(FOLSequent conclusion, FOLSequent... premises) {
        FOLViolation v = super.check(conclusion, premises);
        if (v != null) return v;

        FOLSequent premise = premises[0];
        Set<FOLFormula> gamma = conclusion.antecedent();
        
        if (!gamma.equals(premise.antecedent())) {
            return violation(conclusion, premises, fill("The antecedents (assumptions) must match."));
        }

        FOLFormula f_conc = conclusion.succedent();
        FOLFormula f_prem = premise.succedent();

        if (f_conc.kind() != FOLFormula.FormulaKind.FORALL) {
            return violation(conclusion, premises, fill("The conclusion's succedent must be a universal formula."));
        }

        QuantifiedFormula qf = (QuantifiedFormula) f_conc;
        Variable x = qf.quantifiedV();
        FOLFormula body = qf.body();

        Term t = fac.findSubstitutionTerm(body, x, f_prem);
        if (t == null || !(t instanceof Variable)) {
            return violation(conclusion, premises, fill("The premise " + f_prem + " must be a substitution instance using a variable y."));
        }

        Variable y = (Variable) t;
        if (!fac.isFreeFor(y, x, body)) {
            return violation(conclusion, premises, fill("Side condition: " + y + " is not free for " + x + " in " + body));
        }
        for (FOLFormula ass : gamma) {
            if (fac.freeVars(ass).contains(y)) {
                return violation(conclusion, premises, fill("Side condition: " + y + " must not occur free in the assumptions: " + ass));
            }
        }
        if (y != x && fac.freeVars(body).contains(y)) {
            return violation(conclusion, premises, fill("Side condition: " + y + " must not occur free in the body of the conclusion."));
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
        String s1 = "Γ ⊢ A[y/x]";
        String s2 = "Γ ⊢ ∀x A";
        TextUtil.printFrac(out, 5, s1, s2);
        out.println("Side condition: y is not free in Γ or ∀x A.");
    }
}