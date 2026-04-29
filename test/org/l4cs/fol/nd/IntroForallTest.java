package org.l4cs.fol.nd;

import static org.junit.Assert.*;
import java.io.PrintStream;
import java.util.Set;
import org.junit.Test;
import org.l4cs.fol.syntax.*;
/**
 * 
 * @author Yuxin Zhou
 */
public class IntroForallTest {
	private static FOLFormulaFactory fac = new FOLFormulaFactory();
	private static TermFactory tfac = fac.termFactory();
	private static PrintStream out = System.out;
	
	private static Variable x = tfac.variable("x"), a = tfac.variable("a"); //y = tfac.variable("y");
	private static PredicateSymbol p1 = fac.predicateSymbol("P", 1);
	private static PredicateSymbol q1 = fac.predicateSymbol("Q", 1);
	private static Set<FOLFormula> empty = Set.of();
	private static FOLRule intro_forall = new IntroForall(fac);

	@Test
	public void introForall_ok1_basic() {
		out.println("Test 1: I∀ basic success (P(a) ⊢ ∀x P(x)) where Gamma is empty");
		FOLFormula pa = fac.predicateApplication(p1, new Term[] { a });
		FOLFormula px = fac.predicateApplication(p1, new Term[] { x });
		FOLFormula all_px = fac.forall(px, x);

		FOLSequent prem = new FOLSequent(empty, pa);
		FOLSequent conc = new FOLSequent(empty, all_px);

		FOLViolation v = intro_forall.check(conc, prem);
		assertNull(v);
		out.println("  Result: PASS (No violation) \n");
	}

	@Test
	public void introForall_notok2_identity() {
		out.println("Test 2: I∀ identity (P(x) ⊢ ∀x P(x)) should fail according to our notes");
		FOLFormula px = fac.predicateApplication(p1, new Term[] { x });
		FOLFormula all_px = fac.forall(px, x);

		FOLSequent prem = new FOLSequent(empty, px);
		FOLSequent conc = new FOLSequent(empty, all_px);

		FOLViolation v = intro_forall.check(conc, prem);
		assertNotNull(v);
		//out.println("  Result: PASS (t=x case) \n");
		out.println("  Caught Expected Violation:");
		v.print(out);
		out.println("");
	}

	@Test
	public void introForall_violation_eigenvariable_in_gamma() {
		out.println("Test 3: I∀ Violation (Eigenvariable free in Gamma)");
		FOLFormula pa = fac.predicateApplication(p1, new Term[] { a });
		FOLFormula qa = fac.predicateApplication(q1, new Term[] { a }); // 'a' is free here
		FOLFormula qx = fac.predicateApplication(q1, new Term[] { x });
		FOLFormula all_qx = fac.forall(qx, x);

		// Gamma = { P(a) }, Premise = P(a) |- Q(a), Conclusion = P(a) |- ∀x Q(x)
		FOLSequent prem = new FOLSequent(Set.of(pa), qa);
		FOLSequent conc = new FOLSequent(Set.of(pa), all_qx);

		FOLViolation v = intro_forall.check(conc, prem);
		assertNotNull(v);
		out.println("  Caught Expected Violation:");
		v.print(out);
		out.println("");
		
	}

	@Test
	public void introForall_violation_not_a_variable() {
		out.println("Test 4: I∀ Violation (Substituting a constant instead of variable)");
		Constant c = tfac.constant("c");
		FOLFormula pc = fac.predicateApplication(p1, new Term[] { c });
		FOLFormula px = fac.predicateApplication(p1, new Term[] { x });
		FOLFormula all_px = fac.forall(px, x);

		FOLSequent prem = new FOLSequent(empty, pc);
		FOLSequent conc = new FOLSequent(empty, all_px);

		FOLViolation v = intro_forall.check(conc, prem);
		assertNotNull(v);
		out.println("  Caught Expected Violation (Not a variable):");
		v.print(out);
		out.println(); 
	}
//	out.print(TextUtil.fill(buf, DEFAULT_WIDTH)); later
}