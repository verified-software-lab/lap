package org.l4cs.pl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.l4cs.pl.nd.Derivation;
import org.l4cs.pl.nd.DerivationFactory;
import org.l4cs.pl.nd.Sequent;
import org.l4cs.pl.nd.Violation;
import org.l4cs.pl.parse.PLParser;
import org.l4cs.pl.parse.ParseException;
import org.l4cs.pl.syntax.Formula;
import org.l4cs.pl.syntax.FormulaFactory;
import org.l4cs.pl.syntax.Proposition;
import org.l4cs.util.TextUtil;

public class ParseTest {

	private static File dir = new File(new File("examples"), "pl");

	private static FormulaFactory fac = new FormulaFactory();

	private static DerivationFactory df = new DerivationFactory(fac);

	private static Proposition p0, p1, p2, p, q; // , r;

	private static PrintStream out = System.out;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		p0 = fac.proposition("p0");
		p1 = fac.proposition("p1");
		p2 = fac.proposition("p2");
		p = fac.proposition("p");
		q = fac.proposition("q");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	private void parse(String s, Formula expected) throws ParseException {
		out.print("Result of parsing \"" + s + "\": ");
		Reader reader = new StringReader(s);
		PLParser parser = new PLParser(fac, df, reader);
		Formula result = parser.Formula();
		out.println(result);
		assertEquals(expected, result);
	}

	@Test
	public void notOr() throws ParseException {
		parse("NOT p0 OR p1", fac.or(fac.not(p0), p1));
	}

	@Test
	public void notOr2() throws ParseException {
		parse("NOT(p0 OR p1)", fac.not(fac.or(p0, p1)));
	}

	@Test
	public void and3() throws ParseException {
		parse("p0&p1&p2", fac.and(p0, fac.and(p1, p2)));
	}

	@Test
	public void and2() throws ParseException {
		parse("p0" + TextUtil.AND + "p1", fac.and(p0, p1));
	}

	@Test
	public void implies1() throws ParseException {
		parse("p0 IMPLIES p1 AND p2 IMPLIES p0", fac.implies(p0, fac.implies(fac.and(p1, p2), p0)));
	}

	private void parseseq(String s, Sequent expected) throws ParseException {
		out.print("Result of parsing \"" + s + "\": ");
		Reader reader = new StringReader(s);
		PLParser parser = new PLParser(fac, df, reader);
		Sequent result = parser.Sequent();
		out.println(result);
		assertEquals(expected, result);
	}

	@Test
	public void sequent1() throws ParseException {
		parseseq("|-true", df.sequent(Set.of(), fac.trueFormula()));
	}

	@Test
	public void sequent2() throws ParseException {
		parseseq("p0|-p1", df.sequent(Set.of(p0), p1));
	}

	@Test
	public void sequent3() throws ParseException {
		parseseq("p0&p1,p2|-p2", df.sequent(Set.of(fac.and(p0, p1), p2), p2));
	}

	private void checkDerivation(String s, Derivation expected) throws ParseException, Violation {
		Reader reader = new StringReader(s);
		PLParser parser = new PLParser(fac, df, reader);
		Derivation result = parser.Derivation();
		result.printLinear(out);
		if (!expected.equiv(result)) {
			out.println();
			out.println("Expected:");
			expected.printLinear(out);
			assertTrue(false);
		}
	}

	@Test
	public void derivation1() throws ParseException, Violation {
		String s = "2. p0,NOT p0 |- p0 (Ax);";
		Derivation d = df.axDerivation(df.sequent(Set.of(p0, fac.not(p0)), p0));
		checkDerivation(s, d);
	}

	@Test
	public void derivationColor() throws ParseException, Violation {
		String s = "[34m1.[0m ¬p0,p0 ⊢ p0 [31m(Ax)[0m;";
		Derivation d = df.axDerivation(df.sequent(Set.of(p0, fac.not(p0)), p0));
		checkDerivation(s, d);
	}

	@Test
	public void deriveEx46() throws ParseException, Violation {
		String s = "1.   p|q |- p|q (Ax);      \n";
		s += "2. p|q,p |- p   (Ax);      \n";
		s += "3. p|q,p |- q|p (IOR2)2;   \n";
		s += "4. p|q,q |- q   (Ax);      \n";
		s += "5. p|q,q |- q|p (IOR1)4;   \n";
		s += "6.   p|q |- q|p (EOR)1,3,5;\n";
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
		checkDerivation(s, d6);
	}

	private void checkDerivationFile(String filename, Derivation expected)
			throws FileNotFoundException, ParseException, Violation {
		File file = new File(dir, filename);
		Reader reader = new FileReader(file);
		try {
			PLParser parser = new PLParser(fac, df, reader);
			Derivation result = parser.Derivation();
			result.printLinear(out);
			assertTrue(expected.equiv(result));
		} catch (Violation v) {
			v.print(out);
			throw v;
		}
	}

	@Test
	public void deriveEx46File() throws Violation, FileNotFoundException, ParseException {
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
		checkDerivationFile("ex46.txt", d6);
	}

}
