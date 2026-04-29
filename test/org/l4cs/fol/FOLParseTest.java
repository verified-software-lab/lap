package org.l4cs.fol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Set;

import org.junit.Before;
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

/**
 * Test class for FOLParser, mirroring the advisor's PL ParseTest style.
 * Ensures FOL syntax (Quantifiers, Terms) and Derivations parse correctly.
 */
public class FOLParseTest {

    private static File dir = new File(new File("examples"), "fol");
    private FOLFormulaFactory fac;
    private TermFactory tfac;
    private FOLDerivationFactory df;
    private PrintStream out = System.out;

    @Before
    public void setUp() {
        fac = new FOLFormulaFactory();
        tfac = fac.termFactory();
        df = new FOLDerivationFactory(fac);
    }

    private FOLFormula parseFormula(String s) throws ParseException {
        FOLParser parser = new FOLParser(fac, df, new StringReader(s));
        return parser.Formula();
    }

    private FOLDerivation parseDerivation(String s) throws ParseException, FOLViolation {
        FOLParser parser = new FOLParser(fac, df, new StringReader(s));
        return parser.Derivation();
    }

    @Test
    public void testBasicQuantifiers() throws ParseException {
        out.println("Testing basic quantifier parsing...");
        FOLFormula f1 = parseFormula("forall X p(X)");
        FOLFormula expected1 = fac.forall(fac.predicateApplication(fac.predicateSymbol("p", 1), 
                            new Term[]{tfac.variable("X")}), tfac.variable("X"));
        assertEquals(expected1, f1);

        FOLFormula f2 = parseFormula("exists Y (p(a) & q(Y))");
        out.println("Parsed: " + f2);
    }

    @Test
    public void testNestedTerms() throws ParseException {
        out.println("Testing nested function terms...");
        // f(g(X, a))
        FOLFormula f = parseFormula("p(f(g(X, a)))");
        assertTrue(f.toString().contains("f"));
        assertTrue(f.toString().contains("g"));
    }

    @Test
    public void testBarberFormula() throws ParseException {
        out.println("Testing Barber Paradox formula parsing...");
        // forall X (shaves(b, X) <-> !shaves(X, X))
        String barber = "forall X (s(b, X) <-> !s(X, X))";
        FOLFormula f = parseFormula(barber);
        assertNotNull(f);
    }

	@Test
	public void testSimpleFOLDerivation() throws Exception {
		String input = "1. forall X p(X) |- forall X p(X) (Ax);\n" +
		               "2. forall X p(X) |- p(a) (Eforall) 1;";
		FOLDerivation result = parseDerivation(input);
		
		FOLFormula px = fac.predicateApplication(fac.predicateSymbol("p", 1), new Term[]{tfac.variable("X")});
		FOLFormula allXp = fac.forall(px, tfac.variable("X"));
		FOLFormula pa = fac.predicateApplication(fac.predicateSymbol("p", 1), new Term[]{tfac.constant("a")});
		
		FOLDerivation d1 = df.axDerivation(df.sequent(Set.of(allXp), allXp));
		FOLDerivation expected = df.derivation(df.elimForall(), df.sequent(Set.of(allXp), pa), d1);
		
		assertTrue("FOL Eforall derivation should be correct", expected.equiv(result));
	}

    /**
     * Helper to check derivation files, similar to advisor's checkDerivationFile.
     */
	private void checkDerivationFile(String filename, FOLDerivation expected)
			throws FileNotFoundException, ParseException, FOLViolation {
		File file = new File(dir, filename);
		if (!file.exists()) {
			out.println("Warning: " + filename + " not found, skipping deep comparison.");
			return;
		}
		Reader reader = new FileReader(file);
		try {
			FOLParser parser = new FOLParser(fac, df, reader);
			FOLDerivation result = parser.Derivation();
			assertNotNull(result);
			result.printLinear(out);
			if (expected != null) {
				assertTrue("Parsed derivation not equivalent to expected", expected.equiv(result));
			}
		} catch (FOLViolation v) {
			v.print(out);
			throw v;
		}
	}

    @Test
    public void testIdentityDerivation() throws FOLViolation, ParseException {
        // Correctly tests the Iforall rule in a derivation string
        String der = 
            "1. p(Z) |- p(Z) (Ax);\n" +
            "2. |- p(Z) -> p(Z) (IIMPLIES) 1;\n" +
            "3. |- forall X (p(X) -> p(X)) (Iforall) 2;";
        
        FOLDerivation d = parseDerivation(der);
        out.println("Parsed Fitch for Identity:");
        d.printFitch(out);
    }

    private void assertNotNull(Object obj) {
        if (obj == null) throw new AssertionError("Object is null");
    }
    

	@Test
	public void testBarberLogicParsing() throws Exception {
		// Testing contradiction and basic logic rules in FOL
		String input = 
            "1. s(b,b) & !s(b,b) |- s(b,b) & !s(b,b) (Ax);\n" +
            "2. s(b,b) & !s(b,b) |- s(b,b) (EAND1)1;\n" +
            "3. s(b,b) & !s(b,b), s(b,b) |- s(b,b) (Ax);\n" +
            "4. s(b,b) & !s(b,b), s(b,b) |- s(b,b) & !s(b,b) (Ax);\n" + 
            "5. s(b,b) & !s(b,b), s(b,b) |- !s(b,b) (EAND2)4;\n" +
            "6. s(b,b) & !s(b,b), s(b,b) |- False (ENOT)3,5;\n" +
            "7. s(b,b) & !s(b,b) |- !s(b,b) (INOT)6;";
        
        FOLDerivation d = parseDerivation(input);
        assertNotNull(d);
        //System.out.println(d.conclusion().succedent() + "#@$%!");
        assertTrue("Conclusion should be a negation", d.conclusion().succedent().toString().contains("¬"));
	}

    @Test
    public void testQuantifierParsing() throws ParseException, FOLViolation {
        ////String input = "1. forall X p(X) |- p(a) (Eforall)1;";
        // This should fail because step 1 is missing? No, we need 1 to be defined.
        String correctInput = "1. forall X p(X) |- forall X p(X) (Ax);\n2. forall X p(X) |- p(a) (Eforall)1;";
        FOLDerivation d = parseDerivation(correctInput);
        assertNotNull(d);
    }
    
    @Test
    public void testUniversalElimination() throws Exception {
        String input = "1. forall X p(X) |- forall X p(X) (Ax);\n" +
                       "2. forall X p(X) |- p(a) (Eforall)1;";
        FOLDerivation d = parseDerivation(input);
        assertNotNull(d);
        assertTrue(d.conclusion().succedent().toString().contains("p(a)"));
    }

    @Test
    public void testExistentialIntroduction() throws Exception {
        String input = "1. p(a) |- p(a) (Ax);\n" +
                       "2. p(a) |- exists X p(X) (Iexists)1;";
        FOLDerivation d = parseDerivation(input);
        assertNotNull(d);
        assertTrue(d.conclusion().succedent().toString().contains("∃") || d.conclusion().succedent().toString().contains("exists"));
    }

	@Test
	public void deriveFolEx1File() throws Exception {
		// forall X p(X) |- p(a)
		FOLFormula px = fac.predicateApplication(fac.predicateSymbol("p", 1), new Term[]{tfac.variable("X")});
		FOLFormula allXp = fac.forall(px, tfac.variable("X"));
		FOLFormula pa = fac.predicateApplication(fac.predicateSymbol("p", 1), new Term[]{tfac.constant("a")});
		FOLFormula impl = fac.implies(allXp, pa);

		FOLDerivation d1 = df.axDerivation(df.sequent(Set.of(allXp), allXp));
		FOLDerivation d2 = df.derivation(df.elimForall(), df.sequent(Set.of(allXp), pa), d1);
		FOLDerivation d3 = df.derivation(df.introImplies(), df.sequent(Set.of(), impl), d2);
		
		checkDerivationFile("fol_ex1.txt", d3);
	}


    @Test
    public void testDeMorganFOL() throws Exception {
        // Corrected: Use variable 'Y' instead of constant 'a' to satisfy ElimExists eigenvariable rule
        String input = 
            "1. forall X !p(X), exists X p(X), p(Y) |- forall X !p(X) (Ax);\n" +
            "2. forall X !p(X), exists X p(X), p(Y) |- !p(Y) (Eforall)1;\n" +
            "3. forall X !p(X), exists X p(X), p(Y) |- p(Y) (Ax);\n" +
            "4. forall X !p(X), exists X p(X), p(Y) |- False (ENOT)3,2;\n" +
            "5. forall X !p(X), exists X p(X) |- exists X p(X) (Ax);\n" +
            "6. forall X !p(X), exists X p(X) |- False (Eexists)5,4;\n" +
            "7. forall X !p(X) |- !exists X p(X) (INOT)6;";
        
        FOLDerivation d = parseDerivation(input);
        assertNotNull(d);
        String suc = d.conclusion().succedent().toString();
        assertTrue(suc.contains("¬") || suc.contains("!"));
    }
}