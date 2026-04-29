package org.l4cs.pl.nd;

import java.io.PrintStream;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.l4cs.pl.syntax.Formula;
import org.l4cs.pl.syntax.FormulaFactory;
import org.l4cs.pl.syntax.Proposition;

public class DerivationTest {

	private static FormulaFactory fac = new FormulaFactory();
	private static DerivationFactory df = new DerivationFactory(fac);

	private static PrintStream out = System.out;
	private static Proposition p = fac.proposition("p"), q = fac.proposition("q"), r = fac.proposition("r");
	private static Formula F = fac.falseFormula();
	private static Set<Formula> empty = Set.of();

	@BeforeClass
	public static void init() {
		// TextUtil.setHighlighting(false);
	}

	/* Prints the derivation in all 3 formats. */
	private void write(Derivation d) {
		d.printTree(out, true);
		out.println();
		d.printLinear(out);
		out.println();
		d.printFitch(out);
		out.println();
	}

	@Test
	public void ax1() throws Violation {
		out.println("Test ax1...");
		Sequent s1 = df.sequent(Set.of(p, q, r), p);
		Derivation d = df.derivation(df.ax(), s1, new Derivation[0]);
		write(d);
	}

	@Test(expected = Violation.class)
	public void ax1Bad() throws Violation {
		out.println("Test ax1Bad...");
		Sequent s1 = df.sequent(Set.of(q, r), p);
		try {
			df.derivation(df.ax(), s1, new Derivation[0]);
		} catch (Violation v) {
			v.print(out);
			throw v;
		}
	}

	/* Example 4.6 of book: p∨q ⊢ q∨p */
	@Test
	public void ex46() throws Violation {
		out.println("Test ex46...");
		Sequent s1 = df.sequent(Set.of(fac.or(p, q)), fac.or(p, q));
		Sequent s2 = df.sequent(Set.of(fac.or(p, q), p), p);
		Sequent s3 = df.sequent(Set.of(fac.or(p, q), p), fac.or(q, p));
		Sequent s4 = df.sequent(Set.of(fac.or(p, q), q), q);
		Sequent s5 = df.sequent(Set.of(fac.or(p, q), q), fac.or(q, p));
		Sequent s6 = df.sequent(Set.of(fac.or(p, q)), fac.or(q, p));
		Derivation d1 = df.axDerivation(s1);
		Derivation d2 = df.axDerivation(s2);
		Derivation d3 = df.derivation(df.introOr2(), s3, d2);
		Derivation d4 = df.axDerivation(s4);
		Derivation d5 = df.derivation(df.introOr1(), s5, d4);
		Derivation d6 = df.derivation(df.elimOr(), s6, d1, d3, d5);
		write(d6);
	}

	/* Put the premises in the wrong order in d6. */
	@Test(expected = Violation.class)
	public void ex46Bad() throws Violation {
		out.println("Test ex46Bad...");
		Sequent s1 = df.sequent(Set.of(fac.or(p, q)), fac.or(p, q));
		Sequent s2 = df.sequent(Set.of(fac.or(p, q), p), p);
		Sequent s3 = df.sequent(Set.of(fac.or(p, q), p), fac.or(q, p));
		Sequent s4 = df.sequent(Set.of(fac.or(p, q), q), q);
		Sequent s5 = df.sequent(Set.of(fac.or(p, q), q), fac.or(q, p));
		Sequent s6 = df.sequent(Set.of(fac.or(p, q)), fac.or(q, p));
		try {
			Derivation d1 = df.axDerivation(s1);
			Derivation d2 = df.axDerivation(s2);
			Derivation d3 = df.derivation(df.introOr2(), s3, d2);
			Derivation d4 = df.axDerivation(s4);
			Derivation d5 = df.derivation(df.introOr1(), s5, d4);
			Derivation d6 = df.derivation(df.elimOr(), s6, d1, d5, d3);
			write(d6);
		} catch (Violation v) {
			v.print(out);
			throw v;
		}
	}

	/* Example 4.7 of book. ⊢ p → p ∨ q */
	@Test
	public void ex47() throws Violation {
		out.println("Test ex47...");
		Sequent s1 = df.sequent(Set.of(p), p), s2 = df.sequent(Set.of(p), fac.or(p, q)),
				s3 = df.sequent(empty, fac.implies(p, fac.or(p, q)));
		Derivation d1 = df.axDerivation(s1), d2 = df.derivation(df.introOr1(), s2, d1),
				d3 = df.derivation(df.introImplies(), s3, d2);
		write(d3);
	}

	/* Introduced an error in s3's antecedent. */
	@Test(expected = Violation.class)
	public void ex47Bad() throws Violation {
		out.println("Test ex47Bad...");
		Sequent s1 = df.sequent(Set.of(p), p), s2 = df.sequent(Set.of(p), fac.or(p, q)),
				s3 = df.sequent(Set.of(q), fac.implies(p, fac.or(p, q)));
		try {
			Derivation d1 = df.axDerivation(s1), d2 = df.derivation(df.introOr1(), s2, d1),
					d3 = df.derivation(df.introImplies(), s3, d2);
			write(d3);
		} catch (Violation v) {
			v.print(out);
			throw v;
		}
	}

	/* Example 4.8 of book. ⊢ (p → q) ∧ p → q. */
	@Test
	public void ex48() throws Violation {
		out.println("Test ex48...");
		Formula f1 = fac.implies(p, q), f2 = fac.and(f1, p);
		Sequent s1 = df.sequent(Set.of(f2), f2), s2 = df.sequent(Set.of(f2), f1), s3 = df.sequent(Set.of(f2), p),
				s4 = df.sequent(Set.of(f2), q), s5 = df.sequent(empty, fac.implies(f2, q));
		Derivation d1 = df.axDerivation(s1), d2 = df.derivation(df.elimAnd1(), s2, d1),
				d3 = df.derivation(df.elimAnd2(), s3, d1), d4 = df.derivation(df.elimImplies(), s4, d3, d2),
				d5 = df.derivation(df.introImplies(), s5, d4);
		write(d5);
	}

	/* Example 4.9 from book: ¬(p ∧ q) ⊢ ¬p ∨ ¬q */
	@Test
	public void ex49() throws Violation {
		out.println("Test ex49...");
		Formula np = fac.not(p), nq = fac.not(q), npq = fac.not(fac.and(p, q)), f1 = fac.or(np, nq), nf1 = fac.not(f1);
		Set<Formula> g1 = Set.of(npq, nf1, np), g2 = Set.of(npq, nf1, nq), g3 = Set.of(npq, nf1);
		Sequent s1 = df.sequent(g1, np), s2 = df.sequent(g1, f1), s3 = df.sequent(g1, nf1), s4 = df.sequent(g1, F),
				s5 = df.sequent(g3, p), s6 = df.sequent(g2, nq), s7 = df.sequent(g2, f1), s8 = df.sequent(g2, nf1),
				s9 = df.sequent(g2, F), s10 = df.sequent(g3, q), s11 = df.sequent(g3, fac.and(p, q)),
				s12 = df.sequent(g3, npq), s13 = df.sequent(g3, F), s14 = df.sequent(Set.of(npq), f1);
		Derivation d1 = df.axDerivation(s1), d2 = df.derivation(df.introOr1(), s2, d1), d3 = df.axDerivation(s3),
				d4 = df.derivation(df.elimNot(), s4, d2, d3), d5 = df.derivation(df.raa(), s5, d4),
				d6 = df.axDerivation(s6), d7 = df.derivation(df.introOr2(), s7, d6), d8 = df.axDerivation(s8),
				d9 = df.derivation(df.elimNot(), s9, d7, d8), d10 = df.derivation(df.raa(), s10, d9),
				d11 = df.derivation(df.introAnd(), s11, d5, d10), d12 = df.axDerivation(s12),
				d13 = df.derivation(df.elimNot(), s13, d11, d12), d14 = df.derivation(df.raa(), s14, d13);
		write(d14);
	}

	@Test
	public void work1_5() throws Violation {
		Formula np = fac.not(p);
		Sequent s1 = df.sequent(Set.of(p, np), np), s2 = df.sequent(Set.of(p, np), p),
				s3 = df.sequent(Set.of(p, np), F), s4 = df.sequent(Set.of(p), fac.not(np));
		Derivation d1 = df.axDerivation(s1), d2 = df.axDerivation(s2), d3 = df.derivation(df.elimNot(), s3, d2, d1),
				d4 = df.derivation(df.introNot(), s4, d3);
		write(d4);
	}

	@Test
	public void work1_6() throws Violation {
		Formula np = fac.not(p), pq = fac.or(p, q), f = fac.and(pq, np);
		Sequent s1 = df.sequent(Set.of(f), f), s2 = df.sequent(Set.of(f), pq), s3 = df.sequent(Set.of(f, p), p),
				s4 = df.sequent(Set.of(f, p), f), s5 = df.sequent(Set.of(f, p), np), s6 = df.sequent(Set.of(f, p), q),
				s7 = df.sequent(Set.of(f, q), q), s8 = df.sequent(Set.of(f), q);
		Derivation d1 = df.axDerivation(s1), d2 = df.derivation(df.elimAnd1(), s2, d1), d3 = df.axDerivation(s3),
				d4 = df.axDerivation(s4), d5 = df.derivation(df.elimAnd2(), s5, d4),
				d6 = df.derivation(df.elimNot(), s6, d3, d5), d7 = df.axDerivation(s7),
				d8 = df.derivation(df.elimOr(), s8, d2, d6, d7);
		write(d8);
	}
}
