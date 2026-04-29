package org.l4cs.fol.nd;

import static org.junit.Assert.assertEquals;
import java.util.Set;
//import org.junit.BeforeClass;
import org.junit.Test;
import org.l4cs.fol.syntax.FOLFormula;
import org.l4cs.fol.syntax.FOLFormulaFactory;
import org.l4cs.fol.syntax.Term;
import org.l4cs.fol.syntax.TermFactory;

public class DerivationTest3 {

    private static FOLFormulaFactory fac = new FOLFormulaFactory();
    private static TermFactory tfac = fac.termFactory();
    private static FOLDerivationFactory df = new FOLDerivationFactory(fac);

    @Test
    public void testAxIdentity() throws FOLViolation {
        // Valid Ax: p(a) |- p(a)
        FOLFormula pa = fac.predicateApplication(fac.predicateSymbol("p", 1), new Term[]{tfac.constant("a")});
        FOLSequent s = df.sequent(Set.of(pa), pa);
        FOLDerivation d = df.axDerivation(s);
        assertEquals(pa, d.conclusion().succedent());
    }

    @Test(expected = FOLViolation.class)
    public void testAxInvalid() throws FOLViolation {
        // Invalid Ax: |- p(a) -> p(a) (antecedent is empty)
        FOLFormula pa = fac.predicateApplication(fac.predicateSymbol("p", 1), new Term[]{tfac.constant("a")});
        FOLFormula imp = fac.implies(pa, pa);
        FOLSequent s = df.sequent(Set.of(), imp);
        df.axDerivation(s); // Should throw Violation
    }

    @Test
    public void testBarberElimAnd() throws FOLViolation {
        // Barber logic: S(b,b) & !S(b,b) |- S(b,b) (EAND1)
        FOLFormula sbb = fac.predicateApplication(fac.predicateSymbol("s", 2), 
                        new Term[]{tfac.constant("b"), tfac.constant("b")});
        FOLFormula notSbb = fac.not(sbb);
        FOLFormula contra = fac.and(sbb, notSbb);
        
        FOLSequent s1 = df.sequent(Set.of(contra), contra);
        FOLDerivation d1 = df.axDerivation(s1);
        
        FOLSequent s2 = df.sequent(Set.of(contra), sbb);
        FOLDerivation d2 = df.derivation(df.elimAnd1(), s2, d1);
        
        assertEquals(sbb, d2.conclusion().succedent());
    }
}