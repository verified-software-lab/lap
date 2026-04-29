package org.l4cs.pl.semantics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.PrintStream;
import java.util.Random;

import org.junit.BeforeClass;
import org.junit.Test;
import org.l4cs.pl.semantics.Semantics.SATAlgorithm;
import org.l4cs.pl.syntax.Formula;
import org.l4cs.pl.syntax.FormulaFactory;
import org.l4cs.pl.syntax.Proposition;

public class PLSemanticsTest {

	static FormulaFactory fac = new FormulaFactory();

	static Formula falseF = fac.falseFormula(), trueF = fac.trueFormula();

	static Semantics sem = new Semantics(fac);

	static SATSolver brute = sem.newSolver(SATAlgorithm.BRUTE_FORCE);

	static DPLLSATSolver dpll = (DPLLSATSolver) sem.newSolver(SATAlgorithm.DPLL);

	static PrintStream out = System.out;

	@BeforeClass
	public static void setUpOnce() {
		brute.setVerbose(true); // change to true to see more output
		dpll.setVerbose(true); // ditto
	}

	private void checkSatBruteForce(Formula f, boolean expected) {
		out.println("Test sat_bruteForce...");
		out.println("f = " + f);
		Model m = brute.sat(f);
		if (m != null) {
			out.println("SAT(f) = true.  Model: " + m);
		} else {
			out.println("SAT(f) = false.");
		}
		assertEquals(expected, (m != null));
		out.println();
	}

	@Test
	public void sat_bruteForce1() {
		out.println("Test sat_bruteForce1...");
		Formula p = fac.proposition("p0"), np = fac.not(p);
		checkSatBruteForce(p, true);
		checkSatBruteForce(np, true);
		Formula f1 = fac.and(p, np);
		checkSatBruteForce(f1, false);
		Formula f2 = fac.or(p, np);
		checkSatBruteForce(f2, true);
	}

	private void printCnfStruct(Formula f) {
		out.println("cnf(" + f + ") = " + CnfStruct.toString(fac, CnfStruct.make(fac, f)));
	}

	@Test
	public void cnfStruct1() {
		out.println("Test cnfStruct1...");
		printCnfStruct(falseF);
		printCnfStruct(fac.and(falseF, falseF));
		printCnfStruct(trueF);

		Formula[] p = new Formula[4];
		for (int i = 0; i < 4; i++)
			p[i] = fac.proposition("p" + i);

		printCnfStruct(fac.or(p[0], fac.not(p[0])));
		printCnfStruct(fac.and(p[0], fac.not(p[0])));
		printCnfStruct(fac.and(p[0], fac.and(p[1], falseF)));

		Formula b = fac.and(fac.or(p[0], p[2]),
				fac.and(fac.or(p[0], p[3]), fac.and(fac.or(p[1], p[2]), fac.or(p[1], p[3]))));
		printCnfStruct(b);
		out.println();
	}

	@Test
	public void cnfWithTrue1() {
		out.println("Test cnfWithTrue1...");
		Proposition[] p = new Proposition[4];
		for (int i = 0; i < 4; i++)
			p[i] = fac.proposition("p" + i);
		Formula b = fac.and(fac.or(fac.not(p[0]), fac.not(p[2])),
				fac.and(fac.or(p[0], p[3]), fac.and(fac.or(p[1], p[2]), fac.or(fac.not(p[1]), p[3]))));
		CnfStruct bs1 = CnfStruct.make(fac, b);
		out.println("A CNF struct ....... " + CnfStruct.toString(fac, bs1));
		CnfStruct bs2 = bs1.withPropSet(p[0], false);
		out.println("...with p0 false.... " + CnfStruct.toString(fac, bs2));
		CnfStruct bs3 = bs2.withPropSet(p[2], true);
		out.println("...with p2 true..... " + CnfStruct.toString(fac, bs3));
		CnfStruct bs4 = bs3.withPropSet(p[1], false);
		out.println("...with p1 false.... " + CnfStruct.toString(fac, bs4));
		CnfStruct bs5 = bs4.withPropSet(p[3], false);
		out.println("...with p3 false.... " + CnfStruct.toString(fac, bs5));
		CnfStruct bs6 = bs5.withPropSet(p[3], true);
		out.println("...with p3 true..... " + CnfStruct.toString(fac, bs6) + " (no change)");
		out.println("Original CNF struct. " + CnfStruct.toString(fac, bs1));
		out.println("...with p0 false.... " + CnfStruct.toString(fac, bs2));
		out.println("...with p2 true..... " + CnfStruct.toString(fac, bs3));
		out.println("...with p1 false.... " + CnfStruct.toString(fac, bs4));
		out.println("...with p3 false.... " + CnfStruct.toString(fac, bs5));
		out.println();
	}

	private void testDPLL(Formula cnf) {
		out.println("Calling DPLL on " + cnf);
		Model m = dpll.sat_cnf(cnf);
		out.print("Result: ");
		if (m == null)
			out.println("UNSAT");
		else
			out.println("SAT: model = " + m);
		out.println();
	}

	@Test
	public void dpll1() {
		out.println("Test dpll1...");
		Proposition[] p = new Proposition[4];
		for (int i = 0; i < 4; i++)
			p[i] = fac.proposition("p" + i);
		testDPLL(p[0]);
		testDPLL(fac.not(p[0]));
		testDPLL(fac.and(p[0], fac.not(p[0])));

		Formula a = fac.implies(p[0], p[1]);
		a = fac.and(a, fac.implies(p[1], p[2]));
		a = fac.and(a, fac.implies(p[2], p[3]));
		a = fac.and(a, fac.implies(p[3], p[0]));
		a = fac.and(a, fac.implies(p[1], fac.not(p[3])));
		a = fac.and(a, fac.implies(fac.not(p[0]), p[2]));
		a = fac.cnf(a);
		testDPLL(a);

		Formula b = fac.and(fac.or(fac.not(p[0]), fac.not(p[2])),
				fac.and(fac.or(p[0], p[3]), fac.and(fac.or(p[1], p[2]), fac.or(fac.not(p[1]), p[3]))));
		testDPLL(b);
	}

	/**
	 * Tests DPLL on many randomly generated CNF formulas.
	 */
	@Test
	public void dpll2() {
		out.println("Test dpll2...");
		int numVar = 5, numClause = 10, numRuns = 100;
		long seed = 10;
		Proposition[] p = new Proposition[numVar];
		Random random = new Random(seed);

		for (int run = 0; run < numRuns; run++) {
			for (int i = 0; i < numVar; i++)
				p[i] = fac.proposition("p" + i);
			Formula a = null;
			for (int i = 0; i < numClause; i++) {
				Formula c = null;
				// generate nonempty random clause...
				while (c == null) {
					for (int j = 0; j < numVar; j++) {
						int r = random.nextInt(3);
						// 0: do not use p[j]. 1: add p[j]. 2: add !p[j].
						if (r != 0) {
							Formula lit = r == 1 ? p[j] : fac.not(p[j]);
							c = c == null ? lit : fac.or(lit, c);
						}
					}
				}
				a = a == null ? c : fac.and(c, a);
			}
			assert fac.isCnf(a);
			testDPLL(a);
		}
	}

	// p!(q&r))
	@Test
	public void tseytin1() {
		out.println("Test tseytin1...");
		Proposition[] p = new Proposition[3];
		for (int i = 0; i < 3; i++)
			p[i] = fac.proposition("p" + i);
		Formula f = fac.or(p[0], fac.and(p[1], p[2]));
		out.println("Applying Tseytin to: " + f);
		Formula g = fac.tseytin(f);
		out.println("Result of Tseytin  : " + g);
		assertTrue(fac.isCnf(g));
		SATSolver solver = sem.newSolver(SATAlgorithm.BRUTE_FORCE);
		assertEquals(solver.isSat(f), solver.isSat(g));
		out.println();
	}

	@Test
	public void tseytin2() {
		out.println("Test tseytin2...");
		Proposition[] p = new Proposition[3];
		for (int i = 0; i < 3; i++)
			p[i] = fac.proposition("p" + i);
		Formula f = fac.not(fac.or(p[0], fac.implies(fac.not(p[1]), fac.implies(p[2], fac.not(p[0])))));
		out.println("Applying Tseytin to: " + f);
		Formula g = fac.tseytin(f);
		out.println("Result of Tseytin  : " + g);
		assertTrue(fac.isCnf(g));
		Model model1 = brute.sat(f), model2 = brute.sat(g);
		out.println("Model of f: " + model1);
		out.println("Model of g: " + model2);
		assertEquals(model1 == null, model2 == null);
		out.println();
	}

	@Test
	public void tseytin3() {
		out.println("Test tseytin3...");
		Proposition[] p = new Proposition[3];
		for (int i = 0; i < 3; i++)
			p[i] = fac.proposition("p" + i);
		Formula f = fac.not(fac.or(fac.implies(fac.not(p[0]), fac.not(p[1])), fac.not(fac.and(p[2], p[0]))));
		out.println("Applying Tseytin to: " + f);
		Formula g = fac.tseytin(f);
		out.println("Result of Tseytin  : " + g);
		assertTrue(fac.isCnf(g));
		Model model1 = brute.sat(f), model2 = brute.sat(g);
		out.println("Model of f: " + model1);
		out.println("Model of g: " + model2);
		assertEquals(model1 == null, model2 == null);
		out.println();
	}

}
