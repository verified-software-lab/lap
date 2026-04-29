package org.l4cs.fol.syntax;

import static org.l4cs.fol.syntax.FOLFormula.FormulaKind.AND;
import static org.l4cs.fol.syntax.FOLFormula.FormulaKind.EXISTS;
import static org.l4cs.fol.syntax.FOLFormula.FormulaKind.FALSE;
import static org.l4cs.fol.syntax.FOLFormula.FormulaKind.FORALL;
import static org.l4cs.fol.syntax.FOLFormula.FormulaKind.IMPLIES;
import static org.l4cs.fol.syntax.FOLFormula.FormulaKind.NOT;
import static org.l4cs.fol.syntax.FOLFormula.FormulaKind.OR;

import java.util.HashMap;
import java.util.HashSet;
// BEGIN MODIFICATION: REMOVING ARRAY/OBJECTS IMPORTS FOR FLYWEIGHT KEYS
import java.util.Map;
import java.util.Set;
// END MODIFICATION: REMOVING ARRAY/OBJECTS IMPORTS FOR FLYWEIGHT KEYS

//import org.l4cs.fol.syntax.Formula.FormulaKind;

/**
 * This factory class manages all First-Order Logic formulas and supports basic
 * operations on them. It relies on the correct implementation of equals() and
 * hashCode() for structural equivalence checks.
 */
public class FOLFormulaFactory {
	// Canonical representation is maintained for Predicate and Function Symbols.
	private final Map<Integer, PredicateSymbol> predicateSymbolIdMap = new HashMap<>();
	private final Map<String, PredicateSymbol> predicateSymbolNameMap = new HashMap<>();

	// BEGIN MODIFICATION: REMOVING ALL FORMULA FLYWEIGHT CACHES
	// REMOVED: Caches for BinaryFormula, NotFormula, QuantifiedFormula,
	// PredicateApp
	// ----------------------------------------------------------------------
	// CANONICAL REPRESENTATION MAPS (Flyweight Pattern) ARE REMOVED
	// ----------------------------------------------------------------------
	// END MODIFICATION: REMOVING ALL FORMULA FLYWEIGHT CACHES

	// Fields...

	private FOLFalse falseFormula = new FOLFalse();
	// BEGIN MODIFICATION: REMOVING TRUE FORMULA SIMPLIFICATION AND FLYWEIGHT
	// Formula trueFormula = not(falseFormula); is removed to avoid simplification.
	private FOLFormula trueFormula = new FOLNotFormula(falseFormula); // NOT(FALSE) instance
	// END MODIFICATION: REMOVING TRUE FORMULA SIMPLIFICATION AND FLYWEIGHT

	/**
	 * Map from proposition names to propositions. Each proposition can have an
	 * optional name. This map maps a name to the proposition with that name. The
	 * name of a proposition must be unique. Not every proposition has to have have
	 * a name, but each has a unique ID number.
	 */
	// private Map<String, Proposition> propMap = new HashMap<>();//do it with
	// variables + constant, here?

	// New Field: TermFactory instance
	private final TermFactory termFactory; // final

	// Constructor Update
	public FOLFormulaFactory() {
		this.termFactory = new TermFactory(); // Initialize the new factory
	}

	// Accessor for the TermFactory
	public TermFactory termFactory() {
		return termFactory;
	}

	// ----------------------------------------------------------------------
	// Formula Factory Methods
	// ----------------------------------------------------------------------

	public FOLFalse falseFormula() {
		return falseFormula;
	}

	public FOLFormula trueFormula() {
		return trueFormula;
	}

	// BEGIN MODIFICATION: REMOVING INNER KEY CLASSES
	// REMOVED: Private static classes BinaryFormulaKey, NotFormulaKey,
	// QuantifiedFormulaKey, PredicateAppKey
	// END MODIFICATION: REMOVING INNER KEY CLASSES

	// ----------------------------------------------------------------------
	// Predicate Symbol Management
	// ----------------------------------------------------------------------

	/**
	 * Returns the canonical PredicateSymbol with the given name and arity, creating
	 * it if necessary. * @param name The name of the predicate symbol.
	 * 
	 * @param arity The arity (number of arguments) of the predicate symbol.
	 * @return The canonical PredicateSymbol instance.
	 * @throws IllegalArgumentException if a symbol with the same name but different
	 *                                  arity already exists.
	 */
	public PredicateSymbol predicateSymbol(String name, int arity) {
		PredicateSymbol symbol = predicateSymbolNameMap.get(name);
		if (symbol == null) {
			symbol = new PredicateSymbol(name, arity);
			predicateSymbolNameMap.put(name, symbol);
			predicateSymbolIdMap.put(symbol.id(), symbol);
		} else if (symbol.arity() != arity) {
			throw new IllegalArgumentException("Predicate symbol '" + name + "' already defined with arity "
					+ symbol.arity() + ", cannot redefine with arity " + arity);
		}
		return symbol;
	}

	public PredicateSymbol predicateSymbol(int id) {
		return predicateSymbolIdMap.get(id);
	}

	// ----------------------------------------------------------------------
	// Predicate Application (Atomic Formula) Management
	// ----------------------------------------------------------------------

	/**
	 * Creates a Predicate Application Formula. * @param predicate The predicate
	 * symbol.
	 * 
	 * @param arguments The list of Terms that are arguments to the predicate.
	 * @return The PredicateApp Formula.
	 * @throws IllegalArgumentException if the number of arguments doesn't match the
	 *                                  symbol's arity.
	 */
	public PredicateApp predicateApplication(PredicateSymbol predicate, Term[] arguments) {
		if (arguments.length != predicate.arity()) {
			throw new IllegalArgumentException("Predicate symbol '" + predicate.toString() + "' requires "
					+ predicate.arity() + " arguments, but received " + arguments.length);
		}

		// BEGIN MODIFICATION: PredicateApp is no longer flyweighted (canonical)
		return new PredicateApp(predicate, arguments);
		// END MODIFICATION: PredicateApp is no longer flyweighted (canonical)
	}

	// ----------------------------------------------------------------------
	// Binary Connectives
	// ----------------------------------------------------------------------

	public FOLFormula and(FOLFormula a, FOLFormula b) {
		if (a == null || b == null)
			throw new IllegalArgumentException("null formula");

		// BEGIN MODIFICATION: Removed Flyweighting and Simplification
		// Simplification logic (a.equals(falseFormula), etc.) is removed.
		return new FOLBinaryFormula(AND, a, b);
		// END MODIFICATION: Removed Flyweighting and Simplification
	}

	public FOLFormula or(FOLFormula a, FOLFormula b) {
		if (a == null || b == null)
			throw new IllegalArgumentException("null formula");

		// BEGIN MODIFICATION: Removed Flyweighting and Simplification
		// Simplification logic is removed.
		return new FOLBinaryFormula(OR, a, b);
		// END MODIFICATION: Removed Flyweighting and Simplification
	}

	public FOLFormula implies(FOLFormula a, FOLFormula b) {
		if (a == null || b == null)
			throw new IllegalArgumentException("null formula");

		// BEGIN MODIFICATION: Removed Flyweighting and Simplification
		// Simplification logic (a.equals(falseFormula), etc.) is removed.
		return new FOLBinaryFormula(IMPLIES, a, b);
		// END MODIFICATION: Removed Flyweighting and Simplification
	}

	   
    /**
     * A <-> B is shorthand for (A -> B) & (B -> A).
     * @param arg0 Left side of iff
     * @param arg1 Right side of iff
     * @return A binary formula representing the bi-implication
     */
    public FOLFormula iff(FOLFormula arg0, FOLFormula arg1) {
        return and(implies(arg0, arg1), implies(arg1, arg0));
    }
    
	// ----------------------------------------------------------------------
	// Unary Connective (NOT)
	// ----------------------------------------------------------------------

	public FOLFormula not(FOLFormula a) {
		if (a == null)
			throw new IllegalArgumentException("null formula");

		// BEGIN MODIFICATION: Removed Flyweighting and Simplification
		// Simplification logic (a.equals(falseFormula)) is removed.
		return new FOLNotFormula(a);
		// END MODIFICATION: Removed Flyweighting and Simplification
	}

	// ----------------------------------------------------------------------
	// Quantifiers
	// ----------------------------------------------------------------------

	public FOLFormula exists(FOLFormula body, Variable v) {
		if (body == null || v == null)
			throw new IllegalArgumentException("null formula or variable");

		// BEGIN MODIFICATION: QuantifiedFormula is no longer flyweighted
		return new QuantifiedFormula(EXISTS, body, v);
		// END MODIFICATION: QuantifiedFormula is no longer flyweighted
	}

	public FOLFormula forall(FOLFormula body, Variable v) {
		if (body == null || v == null)
			throw new IllegalArgumentException("null formula or variable");

		// BEGIN MODIFICATION: QuantifiedFormula is no longer flyweighted
		return new QuantifiedFormula(FORALL, body, v);
		// END MODIFICATION: QuantifiedFormula is no longer flyweighted
	}

	// ----------------------------------------------------------------------
	// Utility Accessors (Required for NNF/CNF/etc. to navigate the structure)
	// ----------------------------------------------------------------------

	public FOLFormula arg0(FOLFormula a) {
		assert a.kind() == AND || a.kind() == OR || a.kind() == IMPLIES;
		return ((FOLBinaryFormula) a).arg0();
	}

	public FOLFormula arg1(FOLFormula a) {
		assert a.kind() == AND || a.kind() == OR || a.kind() == IMPLIES;
		return ((FOLBinaryFormula) a).arg1();
	}

	public FOLFormula arg(FOLFormula a) {
		assert a.kind() == NOT;
		return ((FOLNotFormula) a).arg();
	}

	// Quantifier Accessors
	public FOLFormula body(FOLFormula a) {
		assert a.kind() == EXISTS || a.kind() == FORALL;
		return ((QuantifiedFormula) a).body();
	}

	public Variable quantifiedV(FOLFormula a) {
		assert a.kind() == EXISTS || a.kind() == FORALL;
		return ((QuantifiedFormula) a).quantifiedV();
	}

	// Formula getters ...

	public boolean isTrue(FOLFormula a) {
		return trueFormula.equals(a);
	}

	public boolean isFalse(FOLFormula a) {
		return falseFormula.equals(a);
	}

	public boolean isNot(FOLFormula a) {
		return a.kind() == NOT;
	}

	public boolean isBinary(FOLFormula a) {
		return a instanceof FOLBinaryFormula;
	}

	public boolean isAnd(FOLFormula a) {
		return a.kind() == AND;
	}

	public boolean isOr(FOLFormula a) {
		return a.kind() == OR;
	}

	public boolean isImplies(FOLFormula a) {
		return a.kind() == IMPLIES;
	}

	public boolean isExists(FOLFormula a) {
		return a.kind() == EXISTS;
	}

	public boolean isForall(FOLFormula a) {
		return a.kind() == FORALL;
	}

	public boolean isQuantifiedF(FOLFormula a) {
		return a instanceof QuantifiedFormula;
	}

	public boolean isPredicateApp(FOLFormula a) {
		return a instanceof PredicateApp;// wait
	}

	private boolean isAtomic(FOLFormula arg) {
		return isPredicateApp(arg) || arg.kind() == FALSE;
	}

	public boolean isGround(FOLFormula a) {
		return vars(a).isEmpty();
	}

	public boolean isSentence(FOLFormula a) {
		return freeVars(a).isEmpty();
	}

	/**
	 * Is formula a a literal, i.e., either a Variable// or NOT a proposition.
	 * 
	 * @param a any formula
	 * @return true iff a is a literal
	 */
	public boolean isTerm(FOLFormula a) {
		return false;// nah
	}

	private void varsAux(FOLFormula a, Set<Variable> set) {
		if (isPredicateApp(a))
//nah
			for (Term t : ((PredicateApp) a).arguments()) {
				termFactory.varsAux(t, set);// double check@@
			}
		else if (isNot(a)) {
			varsAux(arg(a), set);
		} else if (isBinary(a)) {
			varsAux(arg0(a), set);
			varsAux(arg1(a), set);
		}
	}

	public Set<Variable> vars(FOLFormula a) {
		Set<Variable> result = new HashSet<>();
		varsAux(a, result);
		return result;
	}

	private void freeVarsAux(FOLFormula a, Set<Variable> set) {
		if (isPredicateApp(a))
			set.addAll(vars(a));

		else if (isNot(a)) {
			freeVarsAux(arg(a), set);
		} else if (isBinary(a)) {
			freeVarsAux(arg0(a), set);
			freeVarsAux(arg1(a), set);
		} else if (isQuantifiedF(a))
			set.remove(((QuantifiedFormula) a).quantifiedV());
	}

	public Set<Variable> freeVars(FOLFormula a) {
		Set<Variable> result = new HashSet<>();
		freeVarsAux(a, result);
		return result;
	}

	private void boundVarsAux(FOLFormula a, Set<Variable> set) {
		if (isNot(a)) {
			boundVarsAux(arg(a), set);
		} else if (isBinary(a)) {
			boundVarsAux(arg0(a), set);
			boundVarsAux(arg1(a), set);

		} else if (isQuantifiedF(a))
			set.add(((QuantifiedFormula) a).quantifiedV());
	}

	public Set<Variable> boundVars(FOLFormula a) {
		Set<Variable> result = new HashSet<>();
		boundVarsAux(a, result);
		return result;
	}

	// NNF (Negation Normal Form) ...

	/**
	 * Determines whether a formula is in negation normal form. In NNF, the the
	 * IMPLIES operator cannot occur, and NOT can occur only immediately before
	 * FALSE or a proposition.
	 * 
	 * @param a a formula
	 * @return true iff a is in NNF
	 */
	public boolean isNnf(FOLFormula a) {
		switch (a.kind()) {
		case AND:
		case OR:
			return isNnf(arg0(a)) && isNnf(arg1(a));
		case FALSE:
			return true;
		case IMPLIES:
			return false;
		case NOT:
			return isFalse(arg(a)) || isAtomic(arg(a));
		default:
			throw new RuntimeException("unreachable");
		}
	}

	/**
	 * Converts a formula to an equivalent NNF formula in linear time.
	 * 
	 * @param a a formula
	 * @return an equivalent formula in NNF
	 */
	public FOLFormula nnf(FOLFormula a) {
		switch (a.kind()) {
		case FALSE:
		case PREDICATEAPP:
			return a;// continue
		case FORALL:
			return a;// continue
		case EXISTS:
			return a;// continue
		case AND:
			return and(nnf(arg0(a)), nnf(arg1(a)));
		case OR:
			return or(nnf(arg0(a)), nnf(arg1(a)));
		case IMPLIES:
			return or(nnf(not(arg0(a))), nnf(arg1(a)));
		case NOT:
			FOLFormula b = arg(a);
			switch (b.kind()) {
			case FALSE:
			case PREDICATEAPP:
				return a;// continue
			case FORALL:
				return a;// continue
			case EXISTS:
				return a;// continue
			case NOT:
				return nnf(arg(b));
			case AND:
				return or(nnf(not(arg0(b))), nnf(not(arg1(b))));
			case OR:
				return and(nnf(not(arg0(b))), nnf(not(arg1(b))));
			case IMPLIES:
				return and(nnf(arg0(b)), nnf(not(arg1(b))));
			default:
				assert false;
			}
		default:
			throw new RuntimeException("unreachable");
		}
	}

	// CNF (Conjunctive Normal Form) ...

	/**
	 * Given a literal a, return \bar{a}. If a is a proposition, returns a, if a is
	 * !p, returns p.
	 * 
	 * @param a a literal
	 * @return bar(a)
	 */
	public FOLFormula bar(FOLFormula a) {
		return isAtomic(a) ? not(a) : arg(a);// do we keep this?
	}

	///////// new
	/**
	 * Tries to find a unique term 't' such that formula 'B_subst' is the result of
	 * substituting 't' for 'x_var' in 'A_body'.
	 * 
	 * @param A_body  The original formula A (the body of the Exists formula).
	 * @param B_subst The premise formula A[t/x].
	 * @param x_var   The variable x being quantified.
	 * @return The unique term t if found, or null if no unique substitution exists.
	 */
	public Term findSubstitutionTerm(FOLFormula A, Variable x, FOLFormula B) {
		// Case 1: Trivial Substitution (A = B). The simplest t is x itself (A[x/x] =
		// A).
		if (A.equals(B)) {
			return x;
		}

		// t is a one-element array used to store the unique term found.
		// This ensures all substitution sites yield the same term t.
		Term[] t = new Term[1];// why[]??

		boolean success = findSubstitutionTermAux(A, x, B, t);

		// Success means the structures matched and consistency was maintained.
		// t[0] must be non-null if A != B and the substitution was non-trivial.
		if (success && t[0] != null) {
			return t[0];
		}

		return null;
	}

	/**
	 * Checks if term $t$ is free for variable $x$ in formula $A$. * @param t The
	 * substituting term.
	 * 
	 * @param x The variable being substituted.
	 * @param A The formula where substitution is occurring ($\phi$).
	 * @return true if $t$ is free for $x$ in $A$, false otherwise.
	 */
	public boolean isFreeFor(Term t, Variable x, FOLFormula A) {
		// Get the set of variables that occur in the term t.
		Set<Variable> varsInT = termFactory.vars(t);

		// If t is a constant or ground function, it is always free for any variable x
		// in any formula A.
		if (varsInT.isEmpty()) {
			return true;
		}

		return isFreeForAux(t, x, A, varsInT);
	}

	/**
	 * Recursive helper for the freeness check. This is the crucial part that checks
	 * for variable capture.
	 */
	private boolean isFreeForAux(Term t, Variable x, FOLFormula A, Set<Variable> varsInT) {
		switch (A.kind()) {
		case FALSE:
		case PREDICATEAPP:
			// Atomic formulas have no quantifiers, so no capture is possible.
			return true;

		case NOT:
			return isFreeForAux(t, x, arg(A), varsInT);

		case AND:
		case OR:
		case IMPLIES:
			// Check both subformulas recursively.
			FOLBinaryFormula bA = (FOLBinaryFormula) A;
			return isFreeForAux(t, x, bA.arg0(), varsInT) && isFreeForAux(t, x, bA.arg1(), varsInT);

		case EXISTS:
		case FORALL:
			QuantifiedFormula qA = (QuantifiedFormula) A;
			Variable y = qA.quantifiedV(); // The binding variable ($y$)
			FOLFormula A_body = qA.body();

			// Condition for capture: (y $\in$ Vars(t)) AND (x $\in$ FreeVars(A_body))
			if (varsInT.contains(y)) {
				// If y is in Vars(t), it means a variable in our substituting term t
				// is about to be bound by the quantifier Qy. We must check if x occurs
				// free inside the body.

				// Assuming freeVars(A_body) exists and is correct (as per your note)
				if (freeVars(A_body).contains(x)) {
					// VIOLATION: Variable capture occurs.
					return false;
				}
			}

			// If the substitution variable x is the same as the bound variable y,
			// the substitution t/x doesn't happen *under* the scope of Qy.
			if (x.equals(y)) {
				return true;
			}

			// Recursive step: check the body A_body
			return isFreeForAux(t, x, A_body, varsInT);

		default:
			return true;
		}
	}

	/**
	 * Recursive helper to find the substitution term t at the Formula level.
	 * @param A The original formula.
	 * 
	 * @param x  The variable being substituted.
	 * @param f2 The substituted formula.
	 * @param t  An array of size 1 to store the unique term found so far.
	 * @return True if A and B are structurally compatible and the substitution term
	 *         is consistent, False otherwise.
	 */
	private boolean findSubstitutionTermAux(FOLFormula f1, Variable x, FOLFormula f2, Term[] t) {
		// Structural mismatch check
		if (f1.kind() != f2.kind()) {
			return false;
		}

		// Recurse based on formula kind
		switch (f1.kind()) {
		case FALSE:
			return true;

		case PREDICATEAPP:
			// Base case: Substitution happens on terms within the predicate.
			PredicateApp pa1 = (PredicateApp) f1, pa2 = (PredicateApp) f2;// shorter

			// 1. Predicate symbols must match.
			if (!pa1.predicateSymbol().equals(pa2.predicateSymbol())) {
				return false;
			}

			Term[] argsA = pa1.arguments(), argsB = pa2.arguments();
			assert argsA.length == argsB.length;

			// 2. Recurse on all term pairs.
			for (int i = 0; i < argsA.length; i++)
				if (!findTermSubstitutionAux(argsA[i], x, argsB[i], t))
					return false; // Found inconsistency
			return true;

		case NOT:
			// NOT(A0) -> NOT(B0)
			return findSubstitutionTermAux(arg(f1), x, arg(f2), t);

		case AND:
		case OR:
		case IMPLIES:
			// Binary Formula (A0 op A1) -> (B0 op B1)
			FOLBinaryFormula bf1 = (FOLBinaryFormula) f1, bf2 = (FOLBinaryFormula) f2;

			// Recurse on left side
			if (!findSubstitutionTermAux(bf1.arg0(), x, bf2.arg0(), t))
				return false;
			// Recurse on right side
			if (!findSubstitutionTermAux(bf1.arg1(), x, bf2.arg1(), t))
				return false;
			return true;

		case FORALL:
		case EXISTS:
			// there already must be the same quantifier
			// Quantifier Formula (Qy A0) -> (Qy B0)
			QuantifiedFormula qf1 = (QuantifiedFormula) f1, qf2 = (QuantifiedFormula) f2;

			// The quantified variable must be the same (no $\alpha$-conversion here)
			if (!qf1.quantifiedV().equals(qf2.quantifiedV())) {
				return false;
			}

			// If the variable being substituted (x) is the same as the bound variable (y),
			// the substitution A[t/x] should not have changed the formula, meaning A=B.
			if (qf1.quantifiedV().equals(x)) {
				return qf1.body().equals(qf2.body());
			} else {
				// x is not bound by this quantifier, substitution passes through.
				return findSubstitutionTermAux(qf1.body(), x, qf2.body(), t);
			}

		default:
			// Should not happen if all kinds are covered.
			throw new RuntimeException("Unrecognized FormulaKind: " + f1.kind());
		}
	}

	/// /////////new
	/**
	 * Recursive helper to find the substitution term t at the ~Term level~.
	 * * @param t1 The original term.
	 * 
	 * @param x  The variable being substituted.
	 * @param t2 The substituted term.
	 * @param t  An in/out parameter. An array of size 1 such that t[0] stores the
	 *           unique term found so far or null if not yet found. Later!!
	 * @return {@code true} if {@code t1} and {@code t2} are structurally compatible
	 *         and the substitution term is consistent, {@code false} otherwise.
	 */
	private boolean findTermSubstitutionAux(Term t1, Variable x, Term t2, Term[] t) {
		// 1. Base case: A is the variable x.
		if (t1.equals(x)) {
			// A = x, B = t. B is the candidate substitution term t.
			if (t[0] == null) {
				t[0] = t2; // First time we find the term t. Store it.
				return true;
			} else {
				// We've found t before. Check for consistency.
				return t[0].equals(t2);
			}
		}

		// 2. Base case: A is a Constant or a Variable other than x.
		if (termFactory.isVar(t1) || termFactory.isConst(t1)) {
			// Substitution does not happen here, so A must equal B.
			return t1.equals(t2);
		}

		// 3. Recursive case: A is a Function Application.
		if (termFactory.isFunctionApp(t1)) {
			if (!termFactory.isFunctionApp(t2)) {
				return false; // Structural mismatch -- wait
			}
			FunctionApp fA = (FunctionApp) t1;
			FunctionApp fB = (FunctionApp) t2;
			// Function symbols must match.
			if (!fA.functionSymbol().equals(fB.functionSymbol())) {
				return false;
			}
			Term[] argsA = fA.arguments();
			Term[] argsB = fB.arguments();

			assert argsA.length == argsB.length; // Should happen if function symbols match
			// Recurse on all arguments.
			for (int i = 0; i < argsA.length; i++) {
				if (!findTermSubstitutionAux(argsA[i], x, argsB[i], t)) {
					return false; // Found inconsistency
				}
			}
			return true;
		}
		throw new RuntimeException("unreachable");
	}
	
	////new
	/// 
	// Add to FormulaFactory.java

	/** Checks if term 'target' appears anywhere inside formula 'f' */
	public boolean occursIn(Term target, FOLFormula f) {
	    switch (f.kind()) {
	        case PREDICATEAPP:
	            for (Term arg : ((org.l4cs.fol.syntax.PredicateApp) f).arguments()) {
	                if (occursIn(target, arg)) return true;
	            }
	            return false;
	        case NOT: return occursIn(target, arg(f));
	        case AND:
	        case OR:
	        case IMPLIES:
	            return occursIn(target, ((org.l4cs.fol.syntax.FOLBinaryFormula) f).arg0()) ||
	                   occursIn(target, ((org.l4cs.fol.syntax.FOLBinaryFormula) f).arg1());
	        case EXISTS:
	        case FORALL:
	            return occursIn(target, ((QuantifiedFormula) f).body());
	        default: return false;
	    }
	}

	/** Checks if term 'target' appears inside term 'searchIn' */
	private boolean occursIn(Term target, Term searchIn) {
	    if (target.equals(searchIn)) return true;
	    if (searchIn instanceof FunctionApp) {
	        for (Term arg : ((FunctionApp) searchIn).arguments()) {
	            if (occursIn(target, arg)) return true;
	        }
	    }
	    return false;
	}
}
