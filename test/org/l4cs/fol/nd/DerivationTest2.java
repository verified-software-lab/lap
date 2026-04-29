package org.l4cs.fol.nd;

import static org.junit.Assert.fail;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.l4cs.fol.syntax.Constant;
import org.l4cs.fol.syntax.FOLFormula;
import org.l4cs.fol.syntax.FOLFormulaFactory;
import org.l4cs.fol.syntax.PredicateSymbol;
import org.l4cs.fol.syntax.Term;
import org.l4cs.fol.syntax.TermFactory;
import org.l4cs.fol.syntax.Variable;

/**
 * Comprehensive Test Suite for First-Order Logic (FOL) Natural Deduction. *
 * Logic Convention: - Variables: Uppercase (X, Y, Z) - Constants/Functions:
 * Lowercase (a, b, f) * Java Convention: - Java variables are camelCase
 * starting with lowercase.
 */
public class DerivationTest2 {

	private static FOLFormulaFactory fac = new FOLFormulaFactory();
	private static TermFactory tfac = fac.termFactory();
	private static FOLDerivationFactory df = new FOLDerivationFactory(fac);
	private static PrintStream out = System.out;

	// Logical Symbols used in tests
	private static Variable varX = tfac.variable("X");
	private static Variable varY = tfac.variable("Y");
	private static Variable varZ = tfac.variable("Z");
	private static Constant conA = tfac.constant("a");
	private static Constant conB = tfac.constant("b");

	private static PredicateSymbol predP = fac.predicateSymbol("P", 1);
	// private static PredicateSymbol predQ = fac.predicateSymbol("Q", 1);
	private static PredicateSymbol predR = fac.predicateSymbol("R", 2);
	private static PredicateSymbol predS = fac.predicateSymbol("S", 2); // Shaves

	private static FOLFormula falseF = fac.falseFormula();
	// private static Set<Formula> emptyGamma = Set.of();

	@BeforeClass
	public static void init() {
		out.println("Starting Comprehensive FOL Derivation Tests...");
		out.println("Prolog Convention: Variables=Uppercase, Constants=Lowercase.\n");
	}

	private void write(FOLDerivation d) {
		out.println("--- Tree ---");
		d.printTree(out, true);
		out.println("\n--- Linear ---");
		d.printLinear(out);
		out.println("\n--- Fitch ---");
		d.printFitch(out);
		out.println("==================================================\n");
	}

	// =========================================================================
	// 1. POSITIVE RULE TESTS (Correct Usage)
	// =========================================================================

	@Test
	public void testElimForall_Basic() throws FOLViolation {
		out.println("TEST: EAll Basic (forall X P(X) |- P(a))");
		FOLFormula px = fac.predicateApplication(predP, new Term[] { varX });
		FOLFormula allXPx = fac.forall(px, varX);
		FOLFormula pa = fac.predicateApplication(predP, new Term[] { conA });

		Set<FOLFormula> gamma = Set.of(allXPx);
		FOLDerivation d1 = df.axDerivation(df.sequent(gamma, allXPx));
		FOLDerivation d2 = df.derivation(df.elimForall(), df.sequent(gamma, pa), d1);
		write(d2);
	}

	@Test
	public void testIntroExists_Basic() throws FOLViolation {
		out.println("TEST: IExists Basic (P(a) |- exists X P(X))");
		FOLFormula pa = fac.predicateApplication(predP, new Term[] { conA });
		FOLFormula px = fac.predicateApplication(predP, new Term[] { varX });
		FOLFormula exXPx = fac.exists(px, varX);

		Set<FOLFormula> gamma = Set.of(pa);
		FOLDerivation d1 = df.axDerivation(df.sequent(gamma, pa));
		FOLDerivation d2 = df.derivation(df.introExists(), df.sequent(gamma, exXPx), d1);
		write(d2);
	}

	@Test
	public void testIntroForall_Basic() throws FOLViolation {
		out.println("TEST: IAll Basic (forall X P(X) |- forall Y P(Y)) via P(Z)");
		// Eigenvariable Z is not free in Gamma={forall X P(X)}
		FOLFormula px = fac.predicateApplication(predP, new Term[] { varX });
		FOLFormula py = fac.predicateApplication(predP, new Term[] { varY });
		FOLFormula pz = fac.predicateApplication(predP, new Term[] { varZ });
		FOLFormula allXPx = fac.forall(px, varX);
		FOLFormula allYPy = fac.forall(py, varY);

		Set<FOLFormula> gamma = Set.of(allXPx);
		FOLDerivation d1 = df.axDerivation(df.sequent(gamma, allXPx));
		// EAll: forall X P(X) |- P(Z)
		FOLDerivation d2 = df.derivation(df.elimForall(), df.sequent(gamma, pz), d1);
		// IAll: forall X P(X) |- forall Y P(Y) [Z is fresh]
		FOLDerivation d3 = df.derivation(df.introForall(), df.sequent(gamma, allYPy), d2);
		write(d3);
	}

	// =========================================================================
	// 2. FAMOUS FOL DERIVATIONS
	// =========================================================================

	@Test
	public void testBarberLogic() throws FOLViolation {
		out.println("TEST: Barber Paradox Logic (S(b,b) <-> !S(b,b) |- False)");
		FOLFormula sbb = fac.predicateApplication(predS, new Term[] { conB, conB });
		FOLFormula nsbb = fac.not(sbb);
		// (S(b,b) -> !S(b,b)) & (!S(b,b) -> S(b,b))
		FOLFormula bi = fac.and(fac.implies(sbb, nsbb), fac.implies(nsbb, sbb));

		Set<FOLFormula> gamma = Set.of(bi);
		FOLDerivation dAx = df.axDerivation(df.sequent(gamma, bi));
		// Split bi into two directions
		//// Derivation dI1 = df.derivation(df.elimAnd1(), df.sequent(gamma,
		// fac.implies(sbb, nsbb)), dAx);
		FOLDerivation dI2 = df.derivation(df.elimAnd2(), df.sequent(gamma, fac.implies(nsbb, sbb)), dAx);

		// RAA Subproof: Assume S(b,b)
		Set<FOLFormula> gS = new HashSet<>(gamma);
		gS.add(sbb);
		FOLDerivation dAs = df.axDerivation(df.sequent(gS, sbb));

		// In context gS, we still have the implication. We need to derive it there for
		// ElimImplies.
		FOLDerivation dImpCtx = df.axDerivation(df.sequent(gS, bi));
		FOLDerivation dI1Ctx = df.derivation(df.elimAnd1(), df.sequent(gS, fac.implies(sbb, nsbb)), dImpCtx);

		// Order: dAs is the 'A', dI1Ctx is 'A -> B'. ElimImplies(conclusion, dAs,
		// dI1Ctx)
		FOLDerivation dNs = df.derivation(df.elimImplies(), df.sequent(gS, nsbb), dAs, dI1Ctx);
		FOLDerivation dF1 = df.derivation(df.elimNot(), df.sequent(gS, falseF), dAs, dNs);

		// From contradiction in gS, conclude !S(b,b) in gamma
		FOLDerivation dNotS = df.derivation(df.introNot(), df.sequent(gamma, nsbb), dF1);

		// Now S(b,b) from !S(b,b) and dI2
		FOLDerivation dFinalS = df.derivation(df.elimImplies(), df.sequent(gamma, sbb), dNotS, dI2);

		// Final Contradiction in gamma
		FOLDerivation dFinalF = df.derivation(df.elimNot(), df.sequent(gamma, falseF), dFinalS, dNotS);
		write(dFinalF);
	}

	// =========================================================================
	// 3. NEGATIVE TESTS (Intentional Violations)
	// =========================================================================

	@Test
	public void testViolation_IForall_VariableInGamma() {
		out.println("TEST: Violation IAll (P(X) |- forall X P(X))");
		FOLFormula px = fac.predicateApplication(predP, new Term[] { varX });
		FOLFormula allX = fac.forall(px, varX);

		try {
			// X is free in {P(X)}, violation of eigenvariable rule
			FOLDerivation d1 = df.axDerivation(df.sequent(Set.of(px), px));
			df.derivation(df.introForall(), df.sequent(Set.of(px), allX), d1);
			fail("Violation expected: X is free in Gamma");
		} catch (FOLViolation v) {
			out.println("Caught expected violation: " + v.getMessage());
			v.print(out);// added
		}
	}

	@Test
	public void testViolation_EForall_Capture() {
		out.println("TEST: Violation EAll (forall X exists Y R(X,Y) |- exists Y R(Y,Y)) - Capture");
		// Substituting Y for X in ∃Y R(X,Y) captures the new Y
		FOLFormula rxy = fac.predicateApplication(predR, new Term[] { varX, varY });
		FOLFormula exY = fac.exists(rxy, varY);
		FOLFormula allXexY = fac.forall(exY, varX);

		FOLFormula ryy = fac.predicateApplication(predR, new Term[] { varY, varY });
		FOLFormula exY_captured = fac.exists(ryy, varY);

		try {
			FOLDerivation d1 = df.axDerivation(df.sequent(Set.of(allXexY), allXexY));
			df.derivation(df.elimForall(), df.sequent(Set.of(allXexY), exY_captured), d1);
			fail("Violation expected: Variable Y captured during substitution");
		} catch (FOLViolation v) {
			out.println("Caught expected violation: " + v.getMessage());
		}
	}

	@Test
	public void testViolation_EExists_Escape() {
		out.println("TEST: Violation EExists (exists X P(X) |- P(Z)) - Eigenvariable Z escapes");
		FOLFormula px = fac.predicateApplication(predP, new Term[] { varX });
		FOLFormula pz = fac.predicateApplication(predP, new Term[] { varZ });
		FOLFormula exXPx = fac.exists(px, varX);

		try {
			FOLDerivation d1 = df.axDerivation(df.sequent(Set.of(exXPx), exXPx));
			// Subproof: P(Z) |- P(Z). This is valid.
			FOLDerivation s1 = df.axDerivation(df.sequent(Set.of(exXPx, pz), pz));
			// But Eexists says Gamma |- P(Z) is valid ONLY if Z is NOT free in the
			// conclusion P(Z)!
			df.derivation(df.elimExists(), df.sequent(Set.of(exXPx), pz), d1, s1);
			fail("Violation expected: Eigenvariable Z escapes to conclusion");
		} catch (FOLViolation v) {
			out.println("Caught expected violation: " + v.getMessage());
		}
	}
}