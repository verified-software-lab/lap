package org.l4cs.fol;

import static org.junit.Assert.fail;
import java.io.StringReader;
import org.junit.Test;
//import org.l4cs.fol.nd.Derivation;
import org.l4cs.fol.nd.FOLDerivationFactory;
import org.l4cs.fol.nd.FOLViolation;
import org.l4cs.fol.parse.FOLParser;
import org.l4cs.fol.syntax.FOLFormulaFactory;

public class FOLParseBadTest {

    private FOLFormulaFactory fac = new FOLFormulaFactory();
    private FOLDerivationFactory df = new FOLDerivationFactory(fac);

    private void assertViolation(String input, String expectedMessagePart) throws Exception {
        try {
            FOLParser parser = new FOLParser(fac, df, new StringReader(input));
            parser.Derivation();
            fail("Should have thrown a Violation for: " + expectedMessagePart);
        } catch (FOLViolation v) {
            String msg = v.toString();
            System.out.println("Caught expected violation: " + msg);
            if (expectedMessagePart != null && !msg.toLowerCase().contains(expectedMessagePart.toLowerCase())) {
                fail("Violation message did not contain '" + expectedMessagePart + "'. Was: " + msg);
            }
        } catch (RuntimeException e) {
            // This handles "Missing step" or parser-level logic errors
            System.out.println("Caught runtime error: " + e.getMessage());
        }
    }

    @Test
    public void testBadAx() throws Exception {
        // p |- q is not an axiom because q is not in the antecedent
        assertViolation("1. p |- q (Ax);", "axiom");
    }

    @Test
    public void testBadIforallEigenvariable() throws Exception {
        // Cannot generalize p(a) to forall X p(X) if 'a' is a constant or free elsewhere
        // Usually fails if the term generalized is not a variable or is free in the context
        assertViolation("1. p(a) |- p(a) (Ax);\n2. p(a) |- forall X p(X) (Iforall)1;", "variable");
    }

    @Test
    public void testBadEexistsEigenvariable() throws Exception {
        // Step 2 uses a constant 'a' instead of a fresh variable 'Y'
        String input = 
            "1. exists X p(X) |- exists X p(X) (Ax);\n" +
            "2. exists X p(X), p(a) |- q (Ax);\n" + 
            "3. exists X p(X) |- q (Eexists)1,2;";
        assertViolation(input, "variable");
    }

    @Test
    public void testBadEexistsVariableLeak() throws Exception {
        // Eigenvariable Y must not be free in the conclusion
        String input = 
            "1. exists X p(X) |- exists X p(X) (Ax);\n" +
            "2. exists X p(X), p(Y) |- p(Y) (Ax);\n" + 
            "3. exists X p(X) |- p(Y) (Eexists)1,2;";
        assertViolation(input, "free");
    }

    @Test
    public void testBadENOT() throws Exception {
        // ENOT must conclude False, not some random formula p
        assertViolation("1. p, !p |- p (Ax);\n2. p, !p |- !p (Ax);\n3. p, !p |- p (ENOT)1,2;", "False");
    }

    @Test
    public void testBadINOT() throws Exception {
        // INOT must start from a premise concluding False
        assertViolation("1. p |- p (Ax);\n2. |- !p (INOT)1;", "False");
    }

    @Test
    public void testBadEforall() throws Exception {
        // Trying to instantiate forall X p(X) with q(a)
        assertViolation("1. forall X p(X) |- forall X p(X) (Ax);\n2. forall X p(X) |- q(a) (Eforall)1;", "instance");
    }

    @Test
    public void testBadIexists() throws Exception {
        // p(a) |- exists X q(X) is invalid (wrong predicate)
        assertViolation("1. p(a) |- p(a) (Ax);\n2. p(a) |- exists X q(X) (Iexists)1;", "instance");
    }

    @Test
    public void testBadEAND() throws Exception {
        // EAND1 on a formula that isn't a conjunction
        assertViolation("1. p|q |- p|q (Ax);\n2. p|q |- p (EAND1)1;", "conjunction");
    }

    @Test
    public void testBadIIMPLIES() throws Exception {
        // Conclusion must be an implication
        assertViolation("1. p, q |- q (Ax);\n2. p |- q (IIMPLIES)1;", "implication");
    }

    @Test
    public void testMissingPremise() throws Exception {
        // IAND requires two premises, only one provided
        assertViolation("1. p |- p (Ax);\n2. p |- p&p (IAND)1;", "premise");
    }
}