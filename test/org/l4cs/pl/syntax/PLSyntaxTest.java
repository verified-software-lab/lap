package org.l4cs.pl.syntax;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.PrintStream;

import org.junit.Test;

public class PLSyntaxTest {

	static FormulaFactory fac = new FormulaFactory();

	static Formula falseF = fac.falseFormula(), trueF = fac.trueFormula();

	static PrintStream out = System.out;

	@Test
	public void print() {
		out.println("Test print...");
		Formula p0 = fac.proposition("p0");
		Formula np0 = fac.not(p0);
		out.println(np0);
		out.println(fac.not(np0));
		Formula p1 = fac.proposition("p1");
		Formula a = fac.and(np0, p1);
		out.println(a);
		Formula b = fac.and(p0, p1);
		Formula c = fac.not(b);
		out.println(c);
		out.println(fac.or(p0, a));
		out.println(fac.and(p1, a));
		out.println(fac.and(a, p1));
		out.println(fac.implies(a, p1));
		out.println();
	}

	private void testNnf(Formula a, Formula expected) {
		out.println("a = " + a);
		Formula b = fac.nnf(a);
		out.println("nnf(a) = " + b);
		assertTrue(fac.isNnf(b));
		assertEquals(expected, b);
	}

	@Test
	public void nnf1() {
		out.println("Test nnf1...");
		testNnf(falseF, falseF);
		testNnf(trueF, trueF);
		Formula p0 = fac.proposition("p0");
		testNnf(p0, p0);
		Formula np0 = fac.not(p0);
		testNnf(np0, np0);
		testNnf(fac.not(np0), p0);
		testNnf(fac.not(fac.not(np0)), np0);
		Formula p1 = fac.proposition("p1");
		Formula f1 = fac.and(p0, p1);
		testNnf(f1, f1);
		testNnf(fac.not(f1), fac.or(fac.not(p0), fac.not(p1)));
		Formula f2 = fac.not(fac.or(p0, fac.not(p1)));
		testNnf(f2, fac.and(fac.not(p0), p1));
		Formula f3 = fac.implies(p0, p1);
		testNnf(f3, fac.or(fac.not(p0), p1));
		out.println();
	}

	private void testCnf(Formula a, Formula expected) {
		out.println("a = " + a);
		Formula b = fac.cnf(a);
		out.println("cnf(a) = " + b);
		assertTrue(fac.isCnf(b));
		assertEquals(expected, b);
	}

	@Test
	public void cnf1() {
		out.println("Test cnf1...");
		testCnf(trueF, trueF);
		testCnf(falseF, falseF);
		testCnf(fac.or(falseF, falseF), falseF);
		Formula[] p = new Formula[4];
		for (int i = 0; i < 4; i++)
			p[i] = fac.proposition("p" + i);
		testCnf(p[0], p[0]);
		testCnf(fac.not(p[1]), fac.not(p[1]));
		Formula a = fac.or(fac.and(p[0], p[1]), fac.and(p[2], p[3]));
		Formula b = fac.and(fac.or(p[0], p[2]),
				fac.and(fac.or(p[0], p[3]), fac.and(fac.or(p[1], p[2]), fac.or(p[1], p[3]))));
		testCnf(a, b);
		testCnf(fac.or(trueF, a), trueF);
		testCnf(fac.or(a, trueF), trueF);
		testCnf(fac.or(a, falseF), b);
		testCnf(fac.or(falseF, a), b);
		out.println();
	}

	private void testBigCnf(int n) {
		out.println("Test BigCnf (n=" + n + ")...");
		assert n >= 1;
		Formula[] p = new Formula[n], q = new Formula[n];
		for (int i = 0; i < n; i++) {
			p[i] = fac.proposition("p" + i);
			q[i] = fac.proposition("p" + (n + i));
		}
		Formula a = fac.and(p[n - 1], q[n - 1]);
		for (int i = n - 2; i >= 0; i--)
			a = fac.or(fac.and(p[i], q[i]), a);
		out.println("a = " + a);
		Formula b = fac.cnf(a);
		out.println("cnf(a) = " + b);
		assertTrue(fac.isCnf(b));
		// could do more to test that b is correct
		out.println();
	}

	@Test
	public void bigCnf5() {
		testBigCnf(5);
	}

}
