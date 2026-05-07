package org.l4cs.fol.nd;

import java.io.PrintStream;
import java.util.Set;

import org.l4cs.fol.syntax.FOLFormula;
import org.l4cs.fol.syntax.FOLFormulaFactory;
import org.l4cs.fol.syntax.QuantifiedFormula;
import org.l4cs.fol.syntax.Term;
import org.l4cs.fol.syntax.Variable;
import org.l4cs.util.TextUtil;

public class IntroExists extends FOLRule {

    public IntroExists(FOLFormulaFactory fac) {
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

        if (!gamma_c.equals(gamma_p)) {
            return violation(conclusion, premises,
                    fill("The premise's antecedent, " + gamma_p
                            + ", and the conclusion's antecedent, " + gamma_c
                            + ", should be equal, but are not."));
        }

        FOLFormula f_conclusion = conclusion.succedent();
        FOLFormula f_premise = premise.succedent();

        if (f_conclusion.kind() != FOLFormula.FormulaKind.EXISTS) {
            return violation(conclusion, premises,
                    fill("The conclusion's succedent, " + f_conclusion
                            + ", must be an existential formula (of the form ∃x.φ)."));
        }

        QuantifiedFormula qf_conclusion = (QuantifiedFormula) f_conclusion;
        Variable x = qf_conclusion.quantifiedV();
        FOLFormula body = qf_conclusion.body();

        Term t = fac.findSubstitutionTerm(body, x, f_premise);

        if (t == null) {
            return violation(conclusion, premises,
                    fill("The premise's succedent, " + f_premise
                            + ", is not a substitution instance of the conclusion's body, "
                            + body + ", with respect to variable " + x + "."));
        }

        if (!fac.isFreeFor(t, x, body)) {
            return violation(conclusion, premises,
                    fill("The term " + t + " is not free for the variable " + x
                            + " in the formula " + body + "."));
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
        out.println("Rule " + this
                + " says that if φ holds for a specific term t, then you can conclude ∃x.φ.");
    }
}