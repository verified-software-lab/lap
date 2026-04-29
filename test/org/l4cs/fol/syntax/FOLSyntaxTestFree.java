package org.l4cs.fol.syntax;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.PrintStream;
import org.junit.Test;

public class FOLSyntaxTestFree {

	private static FOLFormulaFactory fac = new FOLFormulaFactory();
	// TermFactory is accessible via the getter FormulaFactory
	private static TermFactory tfac = fac.termFactory(); 
	private static PrintStream out = System.out;

	// Term/Variable/Symbol Setup
	private static Variable x = tfac.variable("x"), y = tfac.variable("y"), z = tfac.variable("z");
	private static Constant a = tfac.constant("a");
	
	// Function Symbols and Terms
	private static FunctionSymbol f1 = tfac.functionSymbol("f", 1);
	private static Term fx = tfac.functionApplication(f1, new Term[] { x }); // f(x)
	private static Term fy = tfac.functionApplication(f1, new Term[] { y }); // f(y)
	private static Term fa = tfac.functionApplication(f1, new Term[] { a }); // f(a) (Ground term)

	// Predicate Symbol P
	private static PredicateSymbol p1 = fac.predicateSymbol("P", 1);

	// Formula Setup
	private static FOLFormula px = fac.predicateApplication(p1, new Term[] { x }); // P(x)
	private static FOLFormula qx = fac.predicateApplication(p1, new Term[] { x }); // Q(x)
	
	// Quantified Formulas
	private static FOLFormula fyPx = fac.forall(px, y); // Forall y. P(x)
	private static FOLFormula eyQx = fac.exists(qx, y); // Exists y. Q(x)
	private static FOLFormula fxPy = fac.forall(fac.predicateApplication(p1, new Term[] { y }), x); // Forall x. P(y)


	private void testIsFreeFor(Term t, Variable varX, FOLFormula aFormula, boolean expected) {
		boolean actual = fac.isFreeFor(t, varX, aFormula);
		String result = actual == expected ? "PASS" : "FAIL";
		
		out.println("--- Test Case --- (" + result + ")");
		out.println("  t = " + t + ", x = " + varX + ", A = " + aFormula);
		out.println("  Expected: " + expected + ", Actual: " + actual);
		
		if (expected) {
			assertTrue("Test failed: " + t + " should be free for " + varX + " in " + aFormula, actual);
		} else {
			assertFalse("Test failed: " + t + " should NOT be free for " + varX + " in " + aFormula, actual);
		}
	}
	
	// --------------------------------------------------------------------------
	// FreeFor Tests
	// --------------------------------------------------------------------------
	
	@Test
	public void isFreeFor_Case1_GroundTerm() {
		out.println("--- Case 1: Ground Term (Always Free) ---");
		// 1. t is a constant 'a'
		testIsFreeFor(a, x, px, true);
		// 2. t is a ground function f(a)
		testIsFreeFor(fa, x, fyPx, true);
	}
	
	@Test
	public void isFreeFor_Case2_NoQuantifier() {
		out.println("--- Case 2: No Quantifier in Formula ---");
		// P(x): t=y, x=x. No capture possible.
		testIsFreeFor(y, x, px, true);
		// P(x): t=f(y), x=x. No capture possible.
		testIsFreeFor(fy, x, px, true);
		//similarly
		testIsFreeFor(fx, x, px, true);
	}
	
	@Test
	public void isFreeFor_Case3_SubstitutionVarIsBoundVar() {
		out.println("--- Case 3: Substitution Variable is Bound Variable ---");
		// t=y, x=x, A=Forall x. P(y).
		// Since x is the bound variable, substitution t/x does not happen within the scope.
		testIsFreeFor(y, x, fxPy, true);
	}
	
	@Test
	public void isFreeFor_Case4_CaptureViolation() {
		out.println("--- Case 4: Capture Violation (t contains binder y, x is free in body) ---");
		// ** VIOLATION CASE **
		// t = y (Vars(t) = {y})
		// x = x (substitution target)
		// A = Forall y. P(x)
		// 1. y is in Vars(t)? YES.
		// 2. x is free in P(x)? YES. -> CAPTURE
		testIsFreeFor(y, x, fyPx, false); 
		
		// ** VIOLATION CASE 2 (Function Term) **
		// t = f(y) (Vars(t) = {y})
		// x = x (substitution target)
		// A = Exists y. Q(x)
		// 1. y is in Vars(t)? YES.
		// 2. x is free in Q(x)? YES. -> CAPTURE
		testIsFreeFor(fy, x, eyQx, false); 
	}
	
	@Test
	public void isFreeFor_Case5_NoCapture_CorrectlyFree() {
		out.println("--- Case 5: Complex Term, No Capture ---");
		// t = f(z) (Vars(t) = {z})
		// x = x (substitution target)
		// A = Forall y. P(x)
		// 1. y is in Vars(t)? NO (z is not y). -> FREE
		testIsFreeFor(tfac.functionApplication(f1, new Term[] { z }), x, fyPx, true); 
	}
}