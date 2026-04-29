package org.l4cs.pl.nd;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.PrintStream;
import java.util.Set;

import org.junit.Test;
import org.l4cs.pl.syntax.Formula;
import org.l4cs.pl.syntax.FormulaFactory;

public class PLNDTest {

	private static FormulaFactory fac = new FormulaFactory();
	private static PrintStream out = System.out;
	private static Formula p0 = fac.proposition("p0"), p1 = fac.proposition("p1"), p2 = fac.proposition("p2");
	private static Formula T = fac.trueFormula(), F = fac.falseFormula();
	private static Set<Formula> empty = Set.of();
	private static Rule ax = new Ax(fac), raa = new RAA(fac), introImplies = new IntroImplies(fac),
			elimImplies = new ElimImplies(fac), introAnd = new IntroAnd(fac), elimAnd1 = new ElimAnd(fac, 1),
			elimAnd2 = new ElimAnd(fac, 2), introOr1 = new IntroOr(fac, 1), introOr2 = new IntroOr(fac, 2),
			elimOr = new ElimOr(fac), introNot = new IntroNot(fac), elimNot = new ElimNot(fac);

	// Rule Ax...

	/* Eyeball test */
	@Test
	public void printAx() {
		out.println("Test printAx...");
		ax.printDescription(out);
		out.println();
	}

	@Test
	public void AxOK() {
		assertNull(ax.check(new Sequent(Set.of(p0, fac.and(p1, p2)), fac.and(p1, p2))));
	}

	@Test
	public void AxViolation() {
		out.println("Test AxViolation...");
		Violation v = ax.check(new Sequent(Set.of(p0, fac.and(p1, p2)), p2));
		assertNotNull(v);
		v.print(out);
		out.println();
	}

	@Test
	public void AxViolationPremise() {
		out.println("Test AxViolationPremise...");
		Violation v = ax.check(new Sequent(Set.of(p0, fac.and(p1, p2)), p2), new Sequent(empty, p2));
		assertNotNull(v);
		v.print(out);
		out.println();
	}

	// Rule RAA...

	/* Eyeball test */
	@Test
	public void printRAA() {
		out.println("Test printRAA...");
		raa.printDescription(out);
		out.println();
	}

	@Test
	public void RAAviolation1() {
		out.println("Test RAAviolation1...");
		Sequent premise = new Sequent(Set.of(fac.not(p0)), T);
		Sequent conclusion = new Sequent(empty, p0);
		Violation v = raa.check(conclusion, premise);
		assertNotNull(v);
		v.print(out);
		out.println();
	}

	@Test
	public void RAAok() {
		out.println("Test RAAok...");
		Sequent premise = new Sequent(Set.of(fac.not(p0)), F);
		Sequent conclusion = new Sequent(empty, p0);
		Violation v = raa.check(conclusion, premise);
		assertNull(v);
		out.println();
	}

	@Test
	public void RAAviolation2() {
		out.println("Test RAAviolation2...");
		Violation v = raa.check(new Sequent(empty, p0), new Sequent(empty, F));
		assertNotNull(v);
		v.print(out);
		out.println();
	}

	// Rule IntroAnd...

	@Test
	public void printIntroAnd() {
		out.println("Test printIntroAnd...");
		introAnd.printDescription(out);
		out.println();
	}

	// Rule ElimAnd...

	@Test
	public void elimAndViolation1() {
		out.println("Test elimAndViolation1...");
		Sequent s0 = new Sequent(empty, fac.and(p0, p1)), s1 = new Sequent(Set.of(p2), p0);
		Violation v = elimAnd1.check(s1, s0);
		assertNotNull(v);
		v.print(out);
		out.println();
	}

	@Test
	public void elimAndViolation2() {
		out.println("Test elimAndViolation2...");
		Sequent s0 = new Sequent(Set.of(p2), fac.and(p0, p1)), s1 = new Sequent(Set.of(p2), p0);
		Violation v = elimAnd2.check(s1, s0);
		assertNotNull(v);
		v.print(out);
		out.println();
	}

	@Test
	public void elimAndViolation3() {
		out.println("Test elimAndViolation3...");
		Sequent s0 = new Sequent(Set.of(p2), fac.or(p2, fac.and(p0, p1))), s1 = new Sequent(Set.of(p2), p0);
		Violation v = elimAnd2.check(s1, s0);
		assertNotNull(v);
		v.print(out);
		out.println();
	}

	@Test
	public void elimAndOK1() {
		Sequent s0 = new Sequent(Set.of(p2), fac.and(p0, p1)), s1 = new Sequent(Set.of(p2), p0);
		assertNull(elimAnd1.check(s1, s0));
	}

	@Test
	public void elimAndOK2() {
		Sequent s0 = new Sequent(Set.of(p2), fac.and(p0, p1)), s1 = new Sequent(Set.of(p2), p1);
		assertNull(elimAnd2.check(s1, s0));
	}

	// Rule IntroOr...

	@Test
	public void introOrViolation1() {
		out.println("Test introOrViolation1...");
		Sequent s0 = new Sequent(empty, p0), s1 = new Sequent(Set.of(p2), fac.or(p0, p1));
		Violation v = introOr1.check(s1, s0);
		assertNotNull(v);
		v.print(out);
		out.println();
	}

	@Test
	public void introOrViolation2() {
		out.println("Test introOrViolation2...");
		Sequent s0 = new Sequent(Set.of(p2), p0), s1 = new Sequent(Set.of(p2), fac.or(p0, p1));
		Violation v = introOr2.check(s1, s0);
		assertNotNull(v);
		v.print(out);
		out.println();
	}

	@Test
	public void introOrViolation3() {
		out.println("Test introOrViolation3...");
		Sequent s0 = new Sequent(Set.of(p2), p0), s1 = new Sequent(Set.of(p2), fac.and(p0, p1));
		Violation v = introOr1.check(s1, s0);
		assertNotNull(v);
		v.print(out);
		out.println();
	}

	@Test
	public void introOrOK1() {
		Sequent s0 = new Sequent(Set.of(p2), p0), s1 = new Sequent(Set.of(p2), fac.or(p0, p1));
		assertNull(introOr1.check(s1, s0));
	}

	@Test
	public void introOrOK2() {
		Sequent s0 = new Sequent(Set.of(p2), p1), s1 = new Sequent(Set.of(p2), fac.or(p0, p1));
		assertNull(introOr2.check(s1, s0));
	}

	// Rule ElimOr...

	@Test
	public void elimOrOK() {
		Sequent s0 = new Sequent(empty, fac.or(p0, p1));
		Sequent s1 = new Sequent(Set.of(p0), p2);
		Sequent s2 = new Sequent(Set.of(p1), p2);
		Sequent c = new Sequent(empty, p2);
		assertNull(elimOr.check(c, s0, s1, s2));
	}

	@Test
	public void elimOrBad1() {
		Sequent s0 = new Sequent(Set.of(p0), fac.or(p0, p1));
		Sequent s1 = new Sequent(Set.of(p0), p2);
		Sequent s2 = new Sequent(Set.of(p1), p2);
		Sequent c = new Sequent(empty, p2);
		Violation v = elimOr.check(c, s0, s1, s2);
		assertNotNull(v);
		v.print(out);
		out.println();
	}

	// Rule IntroImplies...

	@Test
	public void printIntroImplies() {
		out.println("Test printIntroImplies...");
		introImplies.printDescription(out);
		out.println();
	}

	@Test
	public void introImpliesViolation1() {
		out.println("Test introImpliesViolation1...");
		Sequent s1 = new Sequent(Set.of(p0), p1), s2 = new Sequent(empty, p0);
		Sequent c1 = new Sequent(empty, fac.implies(p0, p1));
		Violation v1 = introImplies.check(c1, s1, s2);
		assertNotNull(v1);
		v1.print(out);
		out.println();
		Sequent c2 = new Sequent(empty, fac.and(p0, p1));
		Violation v2 = introImplies.check(c2, s1);
		assertNotNull(v2);
		v2.print(out);
		out.println();
	}

	// Rule ElimImplies...

	@Test
	public void printElimImplies() {
		out.println("Test printElimImplies...");
		elimImplies.printDescription(out);
		out.println();
	}

	// Rule IntroNot...

	@Test
	public void introNotOK1() {
		Sequent s0 = new Sequent(Set.of(p0), F);
		Sequent c = new Sequent(empty, fac.not(p0));
		assertNull(introNot.check(c, s0));
	}

	/* This is also correct: Gamma contains A. */
	@Test
	public void introNotOK2() {
		Sequent s0 = new Sequent(Set.of(p0), F);
		Sequent c = new Sequent(Set.of(p0), fac.not(p0));
		assertNull(introNot.check(c, s0));
	}

	@Test
	public void introNotViolation1() {
		Sequent s0 = new Sequent(empty, F);
		Sequent c = new Sequent(Set.of(p0), fac.not(p0));
		Violation v = introNot.check(c, s0);
		assertNotNull(v);
		v.print(out);
		out.println();
	}

	@Test
	public void introNotViolation2() {
		Sequent s0 = new Sequent(Set.of(p0, p1), F);
		Sequent c = new Sequent(Set.of(p0), fac.not(p2));
		Violation v = introNot.check(c, s0);
		assertNotNull(v);
		v.print(out);
		out.println();
	}

	// Rule ElimNot...

	@Test
	public void elimNotOK() {
		Sequent s0 = new Sequent(Set.of(p0), p1);
		Sequent s1 = new Sequent(Set.of(p0), fac.not(p1));
		Sequent c = new Sequent(Set.of(p0), p2);
		assertNull(elimNot.check(c, s0, s1));
	}

	@Test
	public void elimNotViolation1() {
		Sequent s0 = new Sequent(Set.of(p0), p1);
		Sequent s1 = new Sequent(Set.of(p0), fac.not(p1));
		Sequent c = new Sequent(Set.of(p0), p2);
		Violation v = elimNot.check(c, s1, s0);
		assertNotNull(v);
		v.print(out);
		out.println();
	}

}
