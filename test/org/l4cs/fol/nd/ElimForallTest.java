package org.l4cs.fol.nd;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.PrintStream;
import java.util.Set;

import org.junit.Test;
import org.l4cs.fol.syntax.Constant;
import org.l4cs.fol.syntax.FOLFormula;
import org.l4cs.fol.syntax.FOLFormulaFactory;
import org.l4cs.fol.syntax.FunctionSymbol;
import org.l4cs.fol.syntax.PredicateSymbol;
import org.l4cs.fol.syntax.Term;
import org.l4cs.fol.syntax.TermFactory;
import org.l4cs.fol.syntax.Variable;

public class ElimForallTest {

    private static FOLFormulaFactory fac = new FOLFormulaFactory();
    private static TermFactory tfac = fac.termFactory();
    private static PrintStream out = System.out;
    private static Set<FOLFormula> empty = Set.of();

    private static Variable x = tfac.variable("x");
    private static Variable y = tfac.variable("y");
    private static Constant a = tfac.constant("a");
    private static FunctionSymbol f1 = tfac.functionSymbol("f", 1);
    private static PredicateSymbol p1 = fac.predicateSymbol("P", 1);
    private static PredicateSymbol q2 = fac.predicateSymbol("Q", 2);

    private FOLRule elim_forall = new ElimForall(fac);

    @Test
    public void testEAll_Basic_OK() {
        out.println("Test: E∀ Basic (∀x P(x) ⊢ P(a))");
        FOLFormula px = fac.predicateApplication(p1, new Term[] { x });
        FOLFormula pa = fac.predicateApplication(p1, new Term[] { a });
        FOLFormula all_px = fac.forall(px, x);

        FOLSequent prem = new FOLSequent(empty, all_px);
        FOLSequent conc = new FOLSequent(empty, pa);

        assertNull(elim_forall.check(conc, prem));
    }

    @Test
    public void testEAll_Identity_OK() {
        out.println("Test: E∀ Identity (∀x P(x) ⊢ P(x))");
        FOLFormula px = fac.predicateApplication(p1, new Term[] { x });
        FOLFormula all_px = fac.forall(px, x);

        FOLSequent prem = new FOLSequent(empty, all_px);
        FOLSequent conc = new FOLSequent(empty, px);

        assertNull(elim_forall.check(conc, prem));
    }

    @Test
    public void testEAll_ComplexTerm_OK() {
        out.println("Test: E∀ Complex Term (∀x P(x) ⊢ P(f(a)))");
        FOLFormula px = fac.predicateApplication(p1, new Term[] { x });
        Term fa = tfac.functionApplication(f1, new Term[] { a });
        FOLFormula pfa = fac.predicateApplication(p1, new Term[] { fa });
        FOLFormula all_px = fac.forall(px, x);

        FOLSequent prem = new FOLSequent(empty, all_px);
        FOLSequent conc = new FOLSequent(empty, pfa);

        assertNull(elim_forall.check(conc, prem));
    }

    @Test
    public void testEAll_Nested_OK() {
        out.println("Test: E∀ Nested (∀x Q(x, y) ⊢ Q(f(x), y))");
        FOLFormula qxy = fac.predicateApplication(q2, new Term[] { x, y });
        Term fx = tfac.functionApplication(f1, new Term[] { x });
        FOLFormula qfxy = fac.predicateApplication(q2, new Term[] { fx, y });
        FOLFormula all_qxy = fac.forall(qxy, x);

        FOLSequent prem = new FOLSequent(empty, all_qxy);
        FOLSequent conc = new FOLSequent(empty, qfxy);

        assertNull(elim_forall.check(conc, prem));
    }

    @Test
    public void testEAll_Capture_Violation() {
        out.println("Test: E∀ Capture Violation (∀x ∃y Q(x, y) ⊢ ∃y Q(y, y))");
        // Body φ is ∃y Q(x, y). Substitution [y/x].
        // Since y is bound in φ, substituting y for x causes capture.
        FOLFormula qxy = fac.predicateApplication(q2, new Term[] { x, y });
        FOLFormula exists_qxy = fac.exists(qxy, y);
        FOLFormula all_exists_qxy = fac.forall(exists_qxy, x);

        // Intended conclusion: ∃y Q(y, y)
        FOLFormula qyy = fac.predicateApplication(q2, new Term[] { y, y });
        FOLFormula exists_qyy = fac.exists(qyy, y);

        FOLSequent prem = new FOLSequent(empty, all_exists_qxy);
        FOLSequent conc = new FOLSequent(empty, exists_qyy);

        FOLViolation v = elim_forall.check(conc, prem);
        assertNotNull("Should fail due to variable capture", v);
        out.println("  Result: FAIL as expected. Message: " + v);
    }

    @Test
    public void testEAll_Mismatch_Violation() {
        out.println("Test: E∀ Mismatch (∀x P(x) ⊢ Q(a, a))");
        FOLFormula px = fac.predicateApplication(p1, new Term[] { x });
        FOLFormula qaa = fac.predicateApplication(q2, new Term[] { a, a });
        FOLFormula all_px = fac.forall(px, x);

        FOLSequent prem = new FOLSequent(empty, all_px);
        FOLSequent conc = new FOLSequent(empty, qaa);

        FOLViolation v = elim_forall.check(conc, prem);
        assertNotNull("Should fail due to structural mismatch", v);
    }
}