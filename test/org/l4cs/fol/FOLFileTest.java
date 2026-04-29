package org.l4cs.fol;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.l4cs.fol.nd.FOLDerivation;
import org.l4cs.fol.nd.FOLDerivationFactory;
//import org.l4cs.fol.nd.Sequent;
import org.l4cs.fol.nd.FOLViolation;
import org.l4cs.fol.parse.FOLParser;
import org.l4cs.fol.parse.ParseException;
import org.l4cs.fol.syntax.FOLFormula;
import org.l4cs.fol.syntax.FOLFormulaFactory;
import org.l4cs.fol.syntax.Term;
import org.l4cs.fol.syntax.TermFactory;

public class FOLFileTest {

	private static File dir = new File(new File("examples"), "fol");
	private static FOLFormulaFactory fac = new FOLFormulaFactory();
	private static TermFactory tfac = fac.termFactory();
	private static FOLDerivationFactory df = new FOLDerivationFactory(fac);
	private static PrintStream out = System.out;

	@BeforeClass
	public static void setUp() {
		if (!dir.exists()) dir.mkdirs();
	}

	/**
	 * Helper to parse a file and compare it to a hardcoded derivation.
	 */
	private void checkDerivationFile(String filename, FOLDerivation expected)
			throws FileNotFoundException, FOLViolation, ParseException {
		File file = new File(dir, filename);
		if (!file.exists()) {
			out.println("Skipping " + filename + " (file not found: " + file.getAbsolutePath() + ")");
			return;
		}
		Reader reader = new FileReader(file);
		FOLParser parser = new FOLParser(fac, df, reader);
		try {
			FOLDerivation result = parser.Derivation();
			assertNotNull(result);
			out.println("Verified " + filename + ":");
			result.printLinear(out);
			assertTrue("Derivation in " + filename + " does not match expected logic", 
				expected.equiv(result));
		} catch (FOLViolation v) {
			out.println("Violation in " + filename + ": " + v.getMessage());
			throw v;
		}
	}

	@Test
	public void testDeMorganFile() throws FOLViolation, FileNotFoundException, ParseException {
		// Manual construction of: forall X !p(X) |- !exists X p(X)
		// Fix: Use new Term[] { ... } for predicateApplication
		FOLFormula pX = fac.predicateApplication(fac.predicateSymbol("p", 1), new Term[] { tfac.variable("X") });
		FOLFormula nPX = fac.not(pX);
		FOLFormula f1 = fac.forall(nPX, tfac.variable("X"));
		FOLFormula f2 = fac.exists(pX, tfac.variable("X"));
		
		FOLFormula pY = fac.predicateApplication(fac.predicateSymbol("p", 1), new Term[] { tfac.variable("Y") });
		FOLFormula nPY = fac.not(pY);
		FOLFormula False = fac.falseFormula();

		// Contexts
		Set<FOLFormula> ctx1 = Set.of(f1, f2, pY);
		Set<FOLFormula> ctx2 = Set.of(f1, f2);
		Set<FOLFormula> ctx3 = Set.of(f1);

		// Steps
		FOLDerivation d1 = df.axDerivation(df.sequent(ctx1, f1));
		FOLDerivation d2 = df.derivation(df.elimForall(), df.sequent(ctx1, nPY), d1);
		FOLDerivation d3 = df.axDerivation(df.sequent(ctx1, pY));
		FOLDerivation d4 = df.derivation(df.elimNot(), df.sequent(ctx1, False), d3, d2);
		FOLDerivation d5 = df.axDerivation(df.sequent(ctx2, f2));
		FOLDerivation d6 = df.derivation(df.elimExists(), df.sequent(ctx2, False), d5, d4);
		FOLDerivation d7 = df.derivation(df.introNot(), df.sequent(ctx3, fac.not(f2)), d6);

		checkDerivationFile("demorgan_fol.txt", d7);
	}

}