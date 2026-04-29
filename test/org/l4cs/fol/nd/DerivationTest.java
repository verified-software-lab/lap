package org.l4cs.fol.nd;

//import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

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

/**
 * Comprehensive tests for First-Order Logic Derivations. Uses Prolog
 * convention: Uppercase variables, Lowercase constants.
 */
public class DerivationTest {

	private static FOLFormulaFactory fac = new FOLFormulaFactory();
	private static TermFactory tfac = fac.termFactory();
	private static FOLDerivationFactory df = new FOLDerivationFactory(fac);
	private static PrintStream out = System.out;

	// Common Variables (Uppercase)
	private static Variable x_cap = tfac.variable("X");
	private static Variable y_cap = tfac.variable("Y");
	// private static Variable z_cap = tfac.variable("Z");

	// Common Constants (Lowercase)
	private static Constant a_con = tfac.constant("a");
	// private static Constant b_con = tfac.constant("b");

	// Predicates
	private static PredicateSymbol p1 = fac.predicateSymbol("P", 1);
	private static PredicateSymbol q2 = fac.predicateSymbol("Q", 2);

	// private static Set<Formula> empty = Set.of();

	/* Prints the derivation in all 3 formats. */
	private void write(FOLDerivation d) {
		out.println("--- Tree Format ---");
		d.printTree(out, true);
		out.println("\n--- Linear Format ---");
		d.printLinear(out);
		out.println("\n--- Fitch Format ---");
		d.printFitch(out);
		out.println("----------------------------------------\n");
	}

	@Test
	public void testElimForall_Basic() throws FOLViolation {
		out.println("Test 1: Basic E∀ (∀X P(X) ⊢ P(a))");
		FOLFormula px = fac.predicateApplication(p1, new Term[] { x_cap });
		FOLFormula pa = fac.predicateApplication(p1, new Term[] { a_con });
		FOLFormula all_px = fac.forall(px, x_cap);

		FOLSequent s1 = df.sequent(Set.of(all_px), all_px);
		FOLSequent s2 = df.sequent(Set.of(all_px), pa);

		FOLDerivation d1 = df.axDerivation(s1);
		FOLDerivation d2 = df.derivation(df.elimForall(), s2, d1);
		write(d2);
	}

	@Test
	public void testIntroExists_Basic() throws FOLViolation {
		out.println("Test 2: Basic I∃ (P(a) ⊢ ∃X P(X))");
		FOLFormula pa = fac.predicateApplication(p1, new Term[] { a_con });
		FOLFormula px = fac.predicateApplication(p1, new Term[] { x_cap });
		FOLFormula ex_px = fac.exists(px, x_cap);

		FOLSequent s1 = df.sequent(Set.of(pa), pa);
		FOLSequent s2 = df.sequent(Set.of(pa), ex_px);

		FOLDerivation d1 = df.axDerivation(s1);
		FOLDerivation d2 = df.derivation(df.introExists(), s2, d1);
		write(d2);
	}

	@Test
	public void testFamous_ForallExists() throws FOLViolation {
		out.println("Test 4: Theorem (∀X P(X) ⊢ ∃X P(X))");
		FOLFormula px = fac.predicateApplication(p1, new Term[] { x_cap });
		FOLFormula pa = fac.predicateApplication(p1, new Term[] { a_con });
		FOLFormula all_px = fac.forall(px, x_cap);
		FOLFormula ex_px = fac.exists(px, x_cap);

		Set<FOLFormula> gamma = Set.of(all_px);
		FOLDerivation d1 = df.axDerivation(df.sequent(gamma, all_px));
		FOLDerivation d2 = df.derivation(df.elimForall(), df.sequent(gamma, pa), d1);
		FOLDerivation d3 = df.derivation(df.introExists(), df.sequent(gamma, ex_px), d2);
		write(d3);
	}

	@Test
	public void testFamous_QuantifierSwap() throws FOLViolation {
		out.println("Test 5: Quantifier Swap (∃X ∀Y Q(X,Y) ⊢ ∀Y ∃X Q(X,Y))");
		FOLFormula qxy = fac.predicateApplication(q2, new Term[] { x_cap, y_cap });
		FOLFormula ay_qxy = fac.forall(qxy, y_cap);
		FOLFormula ex_ay_qxy = fac.exists(ay_qxy, x_cap); // Premise

		FOLFormula ex_qxy = fac.exists(qxy, x_cap);
		FOLFormula ay_ex_qxy = fac.forall(ex_qxy, y_cap); // Goal

		// Subproof assumption: ∀Y Q(Z, Y) (Z is fresh)
		Variable z_eigen = tfac.variable("Z");
		FOLFormula ay_qzy = fac.forall(fac.predicateApplication(q2, new Term[] { z_eigen, y_cap }), y_cap);

		// Inside subproof: ∀Y Q(Z, Y) |- Q(Z, Y) |- ∃X Q(X, Y) |- ∀Y ∃X Q(X, Y)
		Set<FOLFormula> sub_gamma = Set.of(ay_qzy);
		FOLDerivation s1 = df.axDerivation(df.sequent(sub_gamma, ay_qzy));

		FOLFormula qzy = fac.predicateApplication(q2, new Term[] { z_eigen, y_cap });
		FOLDerivation s2 = df.derivation(df.elimForall(), df.sequent(sub_gamma, qzy), s1);
		FOLDerivation s3 = df.derivation(df.introExists(), df.sequent(sub_gamma, ex_qxy), s2);
		// Note: y_cap is not free in sub_gamma {∀Y Q(Z, Y)}, so we can introForall
		FOLDerivation s4 = df.derivation(df.introForall(), df.sequent(sub_gamma, ay_ex_qxy), s3);

		// Outer proof: ∃X ∀Y Q(X,Y) |- ∀Y ∃X Q(X,Y) via E∃
		FOLDerivation p1 = df.axDerivation(df.sequent(Set.of(ex_ay_qxy), ex_ay_qxy));
		FOLDerivation p2 = df.derivation(df.elimExists(), df.sequent(Set.of(ex_ay_qxy), ay_ex_qxy), p1, s4);

		write(p2);
	}

	@Test
	public void testBad_EigenvariableViolation() throws FOLViolation {
		out.println("Test 6: BAD DERIVATION (P(X) ⊢ ∀X P(X)) - Should fail in Rule check");
		FOLFormula px = fac.predicateApplication(p1, new Term[] { x_cap });
		FOLFormula all_px = fac.forall(px, x_cap);

		Set<FOLFormula> gamma = Set.of(px);
		FOLDerivation d1 = df.axDerivation(df.sequent(gamma, px));

		try {
			// This should fail because X is free in Gamma {P(X)}
			df.derivation(df.introForall(), df.sequent(gamma, all_px), d1);
			fail("Rule should have thrown Violation: Eigenvariable X is free in Gamma.");
		} catch (FOLViolation v) {
			out.println("Caught Expected Violation: " + v.getMessage());
		}
	}

	@Test
	public void testBad_CaptureViolation() throws FOLViolation {
		out.println("Test 7: BAD DERIVATION (∀X ∃Y Q(X,Y) ⊢ ∃Y Q(Y,Y)) - Should fail");
		FOLFormula qxy = fac.predicateApplication(q2, new Term[] { x_cap, y_cap });
		FOLFormula ex_qxy = fac.exists(qxy, y_cap);
		FOLFormula all_ex_qxy = fac.forall(ex_qxy, x_cap);

		FOLFormula qyy = fac.predicateApplication(q2, new Term[] { y_cap, y_cap });
		FOLFormula ex_qyy = fac.exists(qyy, y_cap);

		Set<FOLFormula> gamma = Set.of(all_ex_qxy);
		FOLDerivation d1 = df.axDerivation(df.sequent(gamma, all_ex_qxy));

		try {
			// This should fail because substituting Y for X in ∃Y Q(X,Y) results in
			// capture.
			df.derivation(df.elimForall(), df.sequent(gamma, ex_qyy), d1);
			fail("Rule should have thrown Violation: Variable capture.");
		} catch (FOLViolation v) {
			out.println("Caught Expected Violation: " + v.getMessage());
		}
	}
}