package org.l4cs.fol.nd;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.PrintStream;
import java.util.Set;

import org.junit.Test;
import org.l4cs.fol.syntax.Constant;
import org.l4cs.fol.syntax.FOLFormula;
import org.l4cs.fol.syntax.FOLFormulaFactory;
import org.l4cs.fol.syntax.PredicateSymbol;
import org.l4cs.fol.syntax.Term;
import org.l4cs.fol.syntax.TermFactory;
import org.l4cs.fol.syntax.Variable;

public class ElimExistsTest {

    private static FOLFormulaFactory fac = new FOLFormulaFactory();
    private static TermFactory tfac = fac.termFactory();
    private static PrintStream out = System.out;
    private static Set<FOLFormula> empty = Set.of();

    private static Variable x = tfac.variable("x");
    private static Variable y = tfac.variable("y");
    private static Variable z = tfac.variable("z");
    private static Constant a = tfac.constant("a");
    private static PredicateSymbol p1 = fac.predicateSymbol("P", 1);
    private static PredicateSymbol q2 = fac.predicateSymbol("Q", 2);

    private FOLRule elim_exists = new ElimExists(fac);

    private void log(String msg) {
        out.println(msg.replace("|-", "⊢").replace("forall", "∀").replace("exists", "∃"));
    }

    @Test
    public void testEExists_Standard_OK() {
        out.println("--- Test: E∃ Standard (∃x P(x) ⊢ P(z) ⊢ Q(a,a)) ---");
        // Γ ⊢ ∃x P(x)
        FOLFormula px = fac.predicateApplication(p1, new Term[]{x});
        FOLFormula ex_px = fac.exists(px, x);
        FOLSequent p1_seq = new FOLSequent(empty, ex_px);

        // Γ, P(z) ⊢ Q(a,a) (z is eigenvariable)
        FOLFormula pz = fac.predicateApplication(p1, new Term[]{z});
        FOLFormula qaa = fac.predicateApplication(q2, new Term[]{a, a});
        FOLSequent p2_seq = new FOLSequent(Set.of(pz), qaa);

        // Conclusion: Γ ⊢ Q(a,a)
        FOLSequent conc = new FOLSequent(empty, qaa);

        log("Premise 1: " + p1_seq);
        log("Premise 2: " + p2_seq);
        log("Conclusion: " + conc);

        assertNull(elim_exists.check(conc, p1_seq, p2_seq));
        out.println("Result: PASS\n");
    }

    @Test
    public void testEExists_Violation_FreeInConclusion() {
        out.println("--- Test: E∃ Violation (y is free in θ) ---");
        // ∃x P(x), P(y) ⊢ P(y)
        // Conclusion ⊢ P(y) -- ERROR: y is the eigenvariable and is free in conclusion!
        FOLFormula px = fac.predicateApplication(p1, new Term[]{x});
        FOLFormula ex_px = fac.exists(px, x);
        FOLFormula py = fac.predicateApplication(p1, new Term[]{y});

        FOLSequent p1_seq = new FOLSequent(empty, ex_px);
        FOLSequent p2_seq = new FOLSequent(Set.of(py), py);
        FOLSequent conc = new FOLSequent(empty, py);

        log("Premise 1: " + p1_seq);
        log("Premise 2: " + p2_seq);
        log("Conclusion: " + conc);

        FOLViolation v = elim_exists.check(conc, p1_seq, p2_seq);
        assertNotNull(v);
        out.println("Result: CAUGHT VIOLATION (Free in θ)");
        v.print(out);
        out.println();
    }

    @Test
    public void testEExists_Violation_FreeInGamma() {
        out.println("--- Test: E∃ Violation (y is free in Γ) ---");
        // Γ = {Q(y, a)}
        // Γ ⊢ ∃x P(x)
        // Γ, P(y) ⊢ Q(a,a) -- ERROR: y is free in Γ!
        FOLFormula qya = fac.predicateApplication(q2, new Term[]{y, a});
        Set<FOLFormula> gamma = Set.of(qya);

        FOLFormula px = fac.predicateApplication(p1, new Term[]{x});
        FOLFormula ex_px = fac.exists(px, x);
        FOLFormula py = fac.predicateApplication(p1, new Term[]{y});
        FOLFormula qaa = fac.predicateApplication(q2, new Term[]{a, a});

        FOLSequent p1_seq = new FOLSequent(gamma, ex_px);
        FOLSequent p2_seq = new FOLSequent(Set.of(qya, py), qaa);
        FOLSequent conc = new FOLSequent(gamma, qaa);

        log("Premise 1: " + p1_seq);
        log("Premise 2: " + p2_seq);
        
        FOLViolation v = elim_exists.check(conc, p1_seq, p2_seq);
        assertNotNull(v);
        out.println("Result: CAUGHT VIOLATION (Free in Γ)");
        v.print(out);
        out.println();
    }

    @Test
    public void testEExists_Complex_Capture() {
        out.println("--- Test: E∃ Violation (Variable Capture) ---");
        // φ = ∀y Q(x, y)
        // Premise 1: ⊢ ∃x ∀y Q(x, y)
        // Assumption in P2: φ[y/x] = ∀y Q(y, y) -- ERROR: Capture!
        FOLFormula qxy = fac.predicateApplication(q2, new Term[]{x, y});
        FOLFormula ay_qxy = fac.forall(qxy, y); // φ
        FOLFormula ex_ay_qxy = fac.exists(ay_qxy, x); // ∃x φ

        FOLFormula qyy = fac.predicateApplication(q2, new Term[]{y, y});
        FOLFormula ay_qyy = fac.forall(qyy, y); // φ[y/x] captured
        
        FOLFormula truth = fac.implies(fac.falseFormula(), fac.falseFormula());

        FOLSequent p1_seq = new FOLSequent(empty, ex_ay_qxy);
        FOLSequent p2_seq = new FOLSequent(Set.of(ay_qyy), truth);
        FOLSequent conc = new FOLSequent(empty, truth);

        FOLViolation v = elim_exists.check(conc, p1_seq, p2_seq);
        assertNotNull(v);
        out.println("Result: CAUGHT VIOLATION (Capture)");
        v.print(out);
        out.println();
    }
}