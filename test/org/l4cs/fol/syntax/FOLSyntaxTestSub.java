package org.l4cs.fol.syntax;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
//import static org.junit.Assert.assertTrue;

import java.io.PrintStream;

import org.junit.Test;

public class FOLSyntaxTestSub {

	private static FOLFormulaFactory fac = new FOLFormulaFactory();
	private static TermFactory tfac = fac.termFactory();
	private static PrintStream out = System.out;

	// Terms and Symbols
	private static Variable x = tfac.variable("x");
	private static Variable y = tfac.variable("y");
	private static Constant a = tfac.constant("a");
	private static Constant b = tfac.constant("b");
	private static FunctionSymbol f1 = tfac.functionSymbol("f", 1);
	private static Term fx = tfac.functionApplication(f1, new Term[] { x });
	private static Term fa = tfac.functionApplication(f1, new Term[] { a });
	private static PredicateSymbol p1 = fac.predicateSymbol("P", 1);
	private static PredicateSymbol q2 = fac.predicateSymbol("Q", 2);

	/*
	 * Helper method to test the substitution finding logic.
	 * aFormula: The original formula (body of Exists x A).
	 * varX: The variable being substituted (quantified variable).
	 * bFormula: The alleged substitution instance (the premise A[t/x]).
	 * expectedTerm: The unique term t expected to be found, or null if none/inconsistent.
	 */
	private void testFindSubstitutionTerm(FOLFormula aFormula, Variable varX, FOLFormula bFormula, Term expectedTerm) {
		Term actualTerm = fac.findSubstitutionTerm(aFormula, varX, bFormula);
		
		out.println("Test Case:");
		out.println("  A (original)  : " + aFormula);
		out.println("  x (variable)  : " + varX);
		out.println("  B (instance)  : " + bFormula);
		out.println("  t (expected)  : " + (expectedTerm == null ? "null (Inconsistent/Mismatch)" : expectedTerm));
		out.println("  t (actual)    : " + (actualTerm == null ? "null (Inconsistent/Mismatch)" : actualTerm));
		
		if (expectedTerm == null) {
			assertNull("Expected no consistent substitution term (null), but found " + actualTerm, actualTerm);
		} else {
			assertEquals("Expected substitution term '" + expectedTerm + "'", expectedTerm, actualTerm);
			// Also check that B[x/t] is A -- or, more correctly, that B is A[t/x]
			// The primary test is the consistent finding of t.
		}
		out.println("---");
	}

	// -----------------------------------------------------------\
	// SUCCESSFUL CASES (Term 't' is found consistently)
	// -----------------------------------------------------------\

	/* Test Case 1: Simple constant substitution: P(x) -> P(a). Term t=a. */
	@Test
	public void findSubstOK1_Constant() {
		FOLFormula px = fac.predicateApplication(p1, new Term[] { x });
		FOLFormula pa = fac.predicateApplication(p1, new Term[] { a });
		testFindSubstitutionTerm(px, x, pa, a);
	}
	
	/* Test Case 2: Complex term substitution: P(x) -> P(f(a)). Term t=f(a). */
	@Test
	public void findSubstOK2_FunctionTerm() {
		FOLFormula px = fac.predicateApplication(p1, new Term[] { x });
		FOLFormula pfa = fac.predicateApplication(p1, new Term[] { fa });
		testFindSubstitutionTerm(px, x, pfa, fa);
	}

	/* Test Case 3: Multiple occurrences of x: Q(x, f(x)) -> Q(a, f(a)). Term t=a. */
	@Test
	public void findSubstOK3_MultipleOccurrences() {
		FOLFormula qxFx = fac.predicateApplication(q2, new Term[] { x, fx });
		FOLFormula qaFa = fac.predicateApplication(q2, new Term[] { a, fa });
		testFindSubstitutionTerm(qxFx, x, qaFa, a);
	}
	
	/* Test Case 4: No substitution necessary: P(y) -> P(y). Term t=x (or null, depending on spec). */
	@Test
	public void findSubstOK4_NoSubstitution() {
		// If x is not free in A, then A[t/x] = A for all t. The consistent term is x itself.
		FOLFormula py = fac.predicateApplication(p1, new Term[] { y });
		Term expectedT = x; // The identity substitution [x/x] is expected.
		testFindSubstitutionTerm(py, x, py, expectedT);
	}
	
	/* Test Case 5: Substitution inside a logical connective: NOT(P(x)) -> NOT(P(a)). Term t=a. */
	@Test
	public void findSubstOK5_InNot() {
		FOLFormula notPx = fac.not(fac.predicateApplication(p1, new Term[] { x }));
		FOLFormula notPa = fac.not(fac.predicateApplication(p1, new Term[] { a }));
		testFindSubstitutionTerm(notPx, x, notPa, a);
	}
	
	/* Test Case 6: Substitution in a conjunction: P(x) & Q(y, x) -> P(f(x)) & Q(y, f(x)). Term t=f(x). */
	@Test
	public void findSubstOK6_InConjunction() {
		FOLFormula px = fac.predicateApplication(p1, new Term[] { x });
		FOLFormula qyx = fac.predicateApplication(q2, new Term[] { y, x });
		FOLFormula aFormula = fac.and(px, qyx); 

		Term t = fx; // t = f(x)
		FOLFormula pfx = fac.predicateApplication(p1, new Term[] { fx });
		FOLFormula qyFx = fac.predicateApplication(q2, new Term[] { y, fx });
		FOLFormula bFormula = fac.and(pfx, qyFx);
		
		testFindSubstitutionTerm(aFormula, x, bFormula, t);
	}

	// -----------------------------------------------------------\
	// VIOLATION CASES (Term 't' is inconsistent or B is not a structural match)
	// -----------------------------------------------------------\
	
	/* Violation 1: Inconsistent substitution: Q(x, x) -> Q(a, b). */
	@Test
	public void findSubstViolation1_InconsistentTerms() {
		FOLFormula qxx = fac.predicateApplication(q2, new Term[] { x, x });
		FOLFormula qab = fac.predicateApplication(q2, new Term[] { a, b }); // First x=a, second x=b. Inconsistent.
		testFindSubstitutionTerm(qxx, x, qab, null);
	}
	
	/* Violation 2: Structural Mismatch: P(x) -> P(a) & Q(b). */
	@Test
	public void findSubstViolation2_StructuralMismatch() {
		FOLFormula px = fac.predicateApplication(p1, new Term[] { x });
		FOLFormula paAndQb = fac.and(fac.predicateApplication(p1, new Term[] { a }), fac.predicateApplication(p1, new Term[] { b }));
		testFindSubstitutionTerm(px, x, paAndQb, null);
	}

	/* Violation 3: Structural Mismatch in Predicate: P(x) -> Q(a). */
	@Test
	public void findSubstViolation3_WrongPredicate() {
		FOLFormula px = fac.predicateApplication(p1, new Term[] { x });
		PredicateSymbol r1 = fac.predicateSymbol("R", 1);
		FOLFormula ra = fac.predicateApplication(r1, new Term[] { a });
		testFindSubstitutionTerm(px, x, ra, null);
	}
	
	/* Violation 4: Part of A is not substituted, but it should be: Q(x, y) -> Q(a, a). */
	@Test
	public void findSubstViolation4_WrongTermAtNonXLocation() {
		FOLFormula qxy = fac.predicateApplication(q2, new Term[] { x, y });
		// x is replaced by 'a'. The term 'y' must not be replaced.
		FOLFormula qaa = fac.predicateApplication(q2, new Term[] { a, a }); 
		testFindSubstitutionTerm(qxy, x, qaa, null); // y in Q(x, y) must match y in Q(a, a)
	}

	/* Violation 5: Quantified formula mismatch (assuming substitution logic handles this). */
	@Test
	public void findSubstViolation5_QuantifiedFormulaMismatch() {
		// A = P(x)
		FOLFormula px = fac.predicateApplication(p1, new Term[] { x });
		// B = Exists y P(y)
		FOLFormula exPy = fac.exists(fac.predicateApplication(p1, new Term[] { y }), y);
		testFindSubstitutionTerm(px, x, exPy, null);
	}
}