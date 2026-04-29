package org.l4cs.fol.syntax;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotSame;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class FOLSyntaxTest {

	static FOLFormulaFactory fac = new FOLFormulaFactory();
	static TermFactory tfac = fac.termFactory();

	// Canonical (Flyweighted) Base Elements
	static Variable x = tfac.variable("x");
	static Variable y = tfac.variable("y");
	static Constant a = tfac.constant("a");
	static Constant b = tfac.constant("b");
	static FunctionSymbol f1 = tfac.functionSymbol("f", 1);
	static PredicateSymbol p1 = fac.predicateSymbol("P", 1);
	static PredicateSymbol q2 = fac.predicateSymbol("Q", 2);

	// Canonical Boolean Elements
	static FOLFormula falseF = fac.falseFormula();
	static FOLFormula trueF = fac.trueFormula();

	static PrintStream out = System.out;

	// -----------------------------------------------------------\
	// TERM TESTS (Non-Flyweight of FunctionApp)
	// -----------------------------------------------------------\

	@Test
	public void testT01_CanonicalPrimitives() {
		out.println("Test 01: Canonical Primitives (Sanity Check)");
		// Test that canonical elements (Variable, Constant, FunctionSymbol) are
		// flyweighted
		assertTrue(x == tfac.variable("x"));
		assertTrue(a == tfac.constant("a"));
		assertTrue(f1 == tfac.functionSymbol("f", 1));
	}

	@Test
	public void testT03_FunctionAppEqualityAndHashCode() {
		out.println("Test 03: FunctionApp Equality (Structural Correctness)");
		Term fxA = tfac.functionApplication(f1, new Term[] { x });
		Term fxB = tfac.functionApplication(f1, new Term[] { x });
		Term fy = tfac.functionApplication(f1, new Term[] { y });

		// Test structural equality
		assertTrue("Structurally identical f(x) must be equal", fxA.equals(fxB));
		assertEquals("Structurally identical f(x) must have same hash code", fxA.hashCode(), fxB.hashCode());
		assertFalse("Structurally different f(x) and f(y) must not be equal", fxA.equals(fy));
	}

	@Test
	public void testT04_ComplexFunctionAppEquality() {
		out.println("Test 04: Nested FunctionApp");
		Term fa = tfac.functionApplication(f1, new Term[] { a });
		Term gfa1 = tfac.functionApplication(f1, new Term[] { fa }); // f(f(a)) instance 1
		Term gfa2 = tfac.functionApplication(f1, new Term[] { fa }); // f(f(a)) instance 2

		// Asserting non-canonical 
		assertNotSame("f(f(a)) instances must be different objects", gfa1, gfa2);
		assertTrue("Structurally identical f(f(a)) must be equal", gfa1.equals(gfa2));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testT05_FunctionAppArityViolation() {
		out.println("Test 05: FunctionApp Arity Violation");
		tfac.functionApplication(f1, new Term[] { a, b }); // f1 is arity 1
	}

	// -----------------------------------------------------------\
	// FORMULA TESTS (Non-Flyweight of Composite Formulas)
	// -----------------------------------------------------------\

	@Test
	public void testF09_NestedQuantifierEquality() {
		out.println("Test 09: Nested Quantifier Equality");
		FOLFormula qxy = fac.predicateApplication(q2, new Term[] { x, y });
		FOLFormula forallY = fac.forall(qxy, y);
		FOLFormula existsX = fac.exists(forallY, x); // Ex x. Forall y. Q(x, y)

		FOLFormula forallYb = fac.forall(qxy, y);
		FOLFormula existsXb = fac.exists(forallYb, x);// "same"

		assertNotSame("Nested quantified formula must be different objects", existsX, existsXb);
		assertTrue("Structurally identical nested quantifiers must be equal", existsX.equals(existsXb));
	}

	// -----------------------------------------------------------\
	// STRUCTURAL & NO-SIMPLIFICATION TESTS
	// -----------------------------------------------------------\

	@Test
	public void testS10_NoBooleanSimplification() {
		out.println("Test 10: No Simplification A -> A != True");
		FOLFormula pa = fac.predicateApplication(p1, new Term[] { a });
		FOLFormula implies = fac.implies(pa, pa); // is true

		// Asserting no simplification
		// We expect the result to be the complex formula itself, not trueF
		assertFalse("A -> A should NOT be simplified to TrueFormula (NotFormula(False))", implies.equals(trueF));
		assertTrue("A -> A should be of kind IMPLIES", implies.kind() == FOLFormula.FormulaKind.IMPLIES);
	}

	@Test
	public void testS11_NoFalseAndSimplification() {
		out.println("Test 11: No Simplification False & A != False");
		FOLFormula pa = fac.predicateApplication(p1, new Term[] { a });
		FOLFormula and = fac.and(falseF, pa);

		// Asserting no simplification
		// We expect the result to be the complex formula itself, not falseF
		assertFalse("False & A should NOT be simplified to FalseFormula", and.equals(falseF));
		assertTrue("False & A should be of kind AND", and.kind() == FOLFormula.FormulaKind.AND);
	}

	@Test
	public void testS12_ComplexFormulaStructuralEquality() {
		out.println("Test 12: Set behavior relies on Structural Equals/HashCode");

		// Create two complex formulas (A and B) that are structurally identical
		// but were constructed separately (and are therefore != due to
		// non-flyweighting)
		FOLFormula px = fac.predicateApplication(p1, new Term[] { x });
		FOLFormula qxy = fac.predicateApplication(q2, new Term[] { x, y });

		FOLFormula exPx = fac.exists(px, x);
		FOLFormula qxAndPx = fac.and(exPx, qxy); // A = (Ex x. P(x)) & Q(x, y)

		// Create formula B, structurally identical to A
		FOLFormula pxb = fac.predicateApplication(p1, new Term[] { x });
		FOLFormula qxyb = fac.predicateApplication(q2, new Term[] { x, y });
		FOLFormula exPxb = fac.exists(pxb, x);
		FOLFormula qxAndPxb = fac.and(exPxb, qxyb); // B = (Ex x. P(x)) & Q(x, y)

		// Check that structural equality holds
		assertTrue("Complex formulas must be structurally equal", qxAndPx.equals(qxAndPxb));
		assertNotSame("Complex formulas must be referentially unequal", qxAndPx, qxAndPxb);

		// Check if they work correctly in a Set (Set uses equals() and hashCode())
		Set<FOLFormula> formulaSet = new HashSet<>();
		formulaSet.add(qxAndPx);

		// The set should contain the structurally identical, but referentially
		// different, formula
		assertTrue("Set must find the structurally identical formula via equals/hashCode",
				formulaSet.contains(qxAndPxb));
		assertEquals("Set size must be 1, confirming correct structural equality", 1, formulaSet.size());
	}
}