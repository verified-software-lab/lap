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

public class IntroExistsTest {

	private static FOLFormulaFactory fac = new FOLFormulaFactory();
	private static TermFactory tfac = fac.termFactory();
	private static PrintStream out = System.out;
	private static Set<FOLFormula> empty = Set.of();
	
	// FOL Term Elements 
	private static Variable x = tfac.variable("x");
	private static Variable y = tfac.variable("y");
	private static Constant a = tfac.constant("a");
	private static FunctionSymbol f1 = tfac.functionSymbol("f", 1);
	private static PredicateSymbol p1 = fac.predicateSymbol("P", 1);
	private static PredicateSymbol q2 = fac.predicateSymbol("Q", 2);

	// FOL Rule Initialization
	private static FOLRule introexists = new IntroExists(fac);

	/* Prints the rule description for manual inspection. */
	private void printruledescription(FOLRule rule) {
		out.println("Test print " + rule.toString() + "...");
		rule.printDescription(out);
		out.println();
	}

	// -----------------------------------------------------------\
	// INTRO EXISTS (IEXISTS) TESTS: Γ ⊢ A[t/x] / Γ ⊢ ∃x A
	// -----------------------------------------------------------\

	@Test
	public void printintroexists() {
		printruledescription(introexists);
	}
	
	/* Test Case 1: Simple constant substitution (t=a for x in P(x)). */
	@Test
	public void introexistsok1_constantsubst() {
		out.println("--- Test introexistsok1_constantsubst ---");
		// a_body = P(x), t = a. Quantified variable is x.
		FOLFormula px = fac.predicateApplication(p1, new Term[] { x });
		// Premise succedent: A[a/x] = P(a)
		FOLFormula pa = fac.predicateApplication(p1, new Term[] { a });
		// Conclusion succedent: Exists x A = ∃x P(x)
		FOLFormula ex_px = fac.exists(px, x);

		FOLSequent s0 = new FOLSequent(empty, pa);
		FOLSequent c = new FOLSequent(empty, ex_px);

		out.println("Rule: " + introexists.toString());
		out.println("Premise succedent (A[t/x]): " + pa);
		out.println("Conclusion body (A): " + px);
		out.println("Quantified variable (x): " + x);
		out.println("Premise sequent: " + s0);
		out.println("Conclusion sequent: " + c);
		
		assertNull("Expected valid IEXISTS: ⊢ P(a) / ⊢ ∃x P(x)", introexists.check(c, s0));
		out.println("Test Passed.");
		out.println("-----------------------------------------");
	}

	/* Test Case 2: Function term substitution (t=f(a) for x in P(x)). */
	@Test
	public void introexistsok2_functionsubst() {
		// a_body = P(x), t = f(a). Premise succedent: P(f(a)). Conclusion succedent: ∃x P(x).
		Term fa = tfac.functionApplication(f1, new Term[] { a });
		FOLFormula px = fac.predicateApplication(p1, new Term[] { x });
		FOLFormula pfa = fac.predicateApplication(p1, new Term[] { fa });
		FOLFormula ex_px = fac.exists(px, x);

		FOLSequent s0 = new FOLSequent(Set.of(pfa), pfa); // Use P(f(a)) as an assumption in Γ
		FOLSequent c = new FOLSequent(Set.of(pfa), ex_px);

		assertNull("Expected valid IEXISTS with function term: P(f(a)) ⊢ P(f(a)) / P(f(a)) ⊢ ∃x P(x)", introexists.check(c, s0));
	}
	
	/* Test Case 3: Substitution into a complex formula (t=a for x in (P(x) ∧ Q(y, x))). */
	@Test
	public void introexistsok3_complexsubst() {
		// a_body = P(x) ∧ Q(y, x), t = a. Quantified variable is x.
		FOLFormula px = fac.predicateApplication(p1, new Term[] { x });
		FOLFormula qyx = fac.predicateApplication(q2, new Term[] { y, x });
		FOLFormula body = fac.and(px, qyx); 

		// Premise succedent: A[a/x] = P(a) ∧ Q(y, a)
		FOLFormula pa = fac.predicateApplication(p1, new Term[] { a });
		FOLFormula qya = fac.predicateApplication(q2, new Term[] { y, a });
		FOLFormula premisesucc = fac.and(pa, qya);
		
		// Conclusion succedent: ∃x (P(x) ∧ Q(y, x))
		FOLFormula ex_body = fac.exists(body, x);

		FOLSequent s0 = new FOLSequent(empty, premisesucc);
		FOLSequent c = new FOLSequent(empty, ex_body);

		assertNull("Expected valid IEXISTS for complex substitution: ⊢ P(a) ∧ Q(y, a) / ⊢ ∃x (P(x) ∧ Q(y, x))", introexists.check(c, s0));
	}

	/* Violation Case 4: Antecedent Mismatch (Γ != Γ'). */
	@Test
	public void introexistsviolation1_antecedentmismatch() {
		// Premise Γ' = {P(a)}, Conclusion Γ = {}
		FOLFormula px = fac.predicateApplication(p1, new Term[] { x });
		FOLFormula pa = fac.predicateApplication(p1, new Term[] { a });
		FOLFormula ex_px = fac.exists(px, x);

		FOLSequent s0 = new FOLSequent(Set.of(pa), pa);
		FOLSequent c = new FOLSequent(empty, ex_px);

		FOLViolation v = introexists.check(c, s0);
		assertNotNull(v);
		out.println("Expected Violation (Antecedent Mismatch):");
		v.print(out);
	}

	/* Violation Case 5: Conclusion is not an Existential Formula. */
	@Test
	public void introexistsviolation2_conclusionnotexists() {
		// Conclusion succedent is P(a), not ∃x A.
		FOLFormula pa = fac.predicateApplication(p1, new Term[] { a });

		FOLSequent s0 = new FOLSequent(empty, pa);
		FOLSequent c = new FOLSequent(empty, pa); 

		FOLViolation v = introexists.check(c, s0);
		assertNotNull(v);
		out.println("Expected Violation (Conclusion Not ∃):");
		v.print(out);
	}

	/* Violation Case 6: Premise is not a substitution instance of the conclusion body. */
	@Test
	public void introexistsviolation3_wrongsubstitution() {
		// Conclusion: ∃x P(x). Body: P(x).
		// Premise succedent: S(a). S(a) is not a substitution instance of P(x).
		FOLFormula px = fac.predicateApplication(p1, new Term[] { x });
		PredicateSymbol s1 = fac.predicateSymbol("S", 1); 
		FOLFormula sa = fac.predicateApplication(s1, new Term[] { a });
		FOLFormula ex_px = fac.exists(px, x);

		FOLSequent s0 = new FOLSequent(empty, sa);
		FOLSequent c = new FOLSequent(empty, ex_px);

		FOLViolation v = introexists.check(c, s0);
		assertNotNull(v);
		out.println("Expected Violation (Wrong Substitution):");
		v.print(out);
	}
}