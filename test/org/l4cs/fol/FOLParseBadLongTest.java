package org.l4cs.fol;

import static org.junit.Assert.fail;
import java.io.StringReader;
import org.junit.Test;
import org.l4cs.fol.nd.FOLDerivationFactory;
import org.l4cs.fol.nd.FOLViolation;
import org.l4cs.fol.parse.FOLParser;
import org.l4cs.fol.syntax.FOLFormulaFactory;

/**
 * Extensive "Bad" tests that use longer derivations with subtle logical errors.
 */
public class FOLParseBadLongTest {

    private FOLFormulaFactory fac = new FOLFormulaFactory();
    private FOLDerivationFactory df = new FOLDerivationFactory(fac);

    private void assertViolation(String input, String expectedKeyword) throws Exception {
        try {
            FOLParser parser = new FOLParser(fac, df, new StringReader(input));
            parser.Derivation();
            fail("Expected Violation (" + expectedKeyword + ") but it passed!");
        } catch (FOLViolation v) {
            String msg = v.toString();
            System.out.println("Detected expected violation: " + msg);
            if (expectedKeyword != null && !msg.toLowerCase().contains(expectedKeyword.toLowerCase())) {
                fail("Expected violation message to contain '" + expectedKeyword + "', but was: " + msg);
            }
        }
    }

    @Test
    public void testSyllogismBadEigenvariableIforall() throws Exception {
        // Correct proof: forall X (p(X) -> q(X)), p(a) |- q(a)
        // BAD: Trying to conclude forall X q(X) from q(a) when 'a' is a constant.
        String input = 
            "1. forall X (p(X)->q(X)), p(a) |- forall X (p(X)->q(X)) (Ax);\n" +
            "2. forall X (p(X)->q(X)), p(a) |- p(a)->q(a) (Eforall)1;\n" +
            "3. forall X (p(X)->q(X)), p(a) |- p(a) (Ax);\n" +
            "4. forall X (p(X)->q(X)), p(a) |- q(a) (EIMPLIES)3,2;\n" +
            "5. forall X (p(X)->q(X)), p(a) |- forall X q(X) (Iforall)4;"; // WRONG: 'a' is a constant, or 'a' is free in antecedent
        
        assertViolation(input, "variable"); 
    }

    @Test
    public void testDeMorganBadPremiseDischarge() throws Exception {
        // Trying to use INOT but the premise logic doesn't actually conclude False
        // ! (p & q) |- !p | !q (Fragment)
        String input = 
            "1. !(p & q), p, q |- p (Ax);\n" +
            "2. !(p & q), p, q |- q (Ax);\n" +
            "3. !(p & q), p, q |- p & q (IAND)1,2;\n" +
            "4. !(p & q), p, q |- !(p & q) (Ax);\n" +
            "5. !(p & q), p, q |- False (ENOT)3,4;\n" +
            "6. !(p & q), p |- !q (INOT)5;\n" + // Correct so far
            "7. !(p & q), p |- p (Ax);\n" +
            "8. !(p & q) |- p -> p (IIMPLIES)7;\n" +
            "9. !(p & q) |- !p (INOT)8;"; // WRONG: step 8 does not conclude False
            
        assertViolation(input, "False");
    }

    @Test
    public void testElimExistsBadEigenvariableLeak() throws Exception {
        // exists X p(X) |- p(Y) 
        // BAD: The eigenvariable Y "leaks" into the conclusion.
        String input = 
            "1. exists X p(X) |- exists X p(X) (Ax);\n" +
            "2. exists X p(X), p(Y) |- p(Y) (Ax);\n" +
            "3. exists X p(X) |- p(Y) (Eexists)1,2;"; // WRONG: Y is free in the conclusion p(Y)
            
        assertViolation(input, "free");
    }

    @Test
    public void testElimExistsBadAntecedentLeak() throws Exception {
        // exists X p(X), q(Y) |- exists X (p(X) & q(Y))
        // BAD: The eigenvariable Y is used in the antecedent of the sub-derivation.
        String input = 
            "1. exists X p(X), q(Y) |- exists X p(X) (Ax);\n" +
            "2. exists X p(X), q(Y), p(Y) |- p(Y) (Ax);\n" +
            "3. exists X p(X), q(Y), p(Y) |- q(Y) (Ax);\n" +
            "4. exists X p(X), q(Y), p(Y) |- p(Y) & q(Y) (IAND)2,3;\n" +
            "5. exists X p(X), q(Y), p(Y) |- exists X (p(X) & q(Y)) (Iexists)4;\n" +
            "6. exists X p(X), q(Y) |- exists X (p(X) & q(Y)) (Eexists)1,5;"; // WRONG: Y is free in the context {exists X p(X), q(Y)}
            
        assertViolation(input, "free");
    }

    @Test
    public void testBadIntroExistsSubstitution() throws Exception {
        // p(a, b) |- exists X p(X, X)
        // BAD: This requires a=b, but here they are different constants.
        String input = 
            "1. p(a,b) |- p(a,b) (Ax);\n" +
            "2. p(a,b) |- exists X p(X,X) (Iexists)1;"; // WRONG: p(a,b) is not an instance of p(X,X)
            
        assertViolation(input, "instance");
    }

    @Test
    public void testBadElimForallSubstitution() throws Exception {
        // forall X p(X, f(X)) |- p(a, f(b))
        // BAD: The substitution must be consistent.
        String input = 
            "1. forall X p(X, f(X)) |- forall X p(X, f(X)) (Ax);\n" +
            "2. forall X p(X, f(X)) |- p(a, f(b)) (Eforall)1;"; // WRONG: not a valid instance
            
        assertViolation(input, "instance");
    }

    @Test
    public void testBadElimOrDischarge() throws Exception {
        // p | q |- r
        // Case p |- r (Valid)
        // Case q |- s (Invalid because it doesn't match the conclusion r)
        String input = 
            "1. p | q |- p | q (Ax);\n" +
            "2. p | q, p |- r (Ax);\n" + // Assuming r is some valid derived formula
            "3. p | q, q |- s (Ax);\n" + 
            "4. p | q |- r (EOR)1,2,3;"; // WRONG: The third premise concludes 's', but the derivation claims 'r'
            
        assertViolation(input, "match");
    }

    @Test
    public void testNestedBadRAA() throws Exception {
        // |- p
        // Assumes !p |- False (then RAA gives p)
        // This example messes up the negation logic inside
        String input = 
            "1. !p |- !p (Ax);\n" +
            "2. !p |- p (Ax);\n" + // Invalid Ax
            "3. !p |- False (ENOT)2,1;\n" +
            "4. |- p (RAA)3;";
            
        assertViolation(input, "axiom"); // Caught at step 2
    }

    @Test
    public void testStepReferenceMismatch() throws Exception {
        // IAND using the same step twice when different ones were intended
        // p, q |- p & q
        String input = 
            "1. p, q |- p (Ax);\n" +
            "2. p, q |- q (Ax);\n" +
            "3. p, q |- p & q (IAND)1,1;"; // WRONG: Uses step 1 twice, resulting in p & p, not p & q
            
        assertViolation(input, "conjunction");
    }

    @Test
    public void testCircularReference() throws Exception {
        // Using a future step as a premise (should fail during parsing)
        String input = 
            "1. p |- p (IAND)1,2;\n" +
            "2. p |- p (Ax);";
            
        try {
            FOLParser parser = new FOLParser(fac, df, new StringReader(input));
            parser.Derivation();
            fail("Should fail due to missing step reference");
        } catch (Exception e) {
            System.out.println("Caught parser/reference error: " + e.getMessage());
        }
    }
}