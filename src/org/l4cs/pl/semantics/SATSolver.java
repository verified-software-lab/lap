package org.l4cs.pl.semantics;

import java.io.PrintStream;

import org.l4cs.pl.syntax.Formula;
import org.l4cs.pl.syntax.FormulaFactory;

/**
 * A boolean satisfiability solver, which provides methods for determining
 * satisfiability, validity, and equivalence for propositional formulas.
 * 
 * <p>
 * A concrete class extending this abstract base class must implement the method
 * {@link #sat(Formula)}. The other methods are given generic implementations
 * that use {@link #sat(Formula)}, but the subclass may override these
 * implementations if it has more efficient ways of implementing them.
 * </p>
 * 
 * @author Stephen Siegel
 */
public abstract class SATSolver {

	/**
	 * Where the verbose output will go.
	 */
	protected final static PrintStream out = System.out;

	/**
	 * Factory that will be used to extract information from formulas.
	 */
	protected FormulaFactory fac;

	/**
	 * Should we print lots of information about the state of the DPLL search?
	 */
	protected boolean verbose = false;

	/**
	 * Constructs new solver based on the given formula factory
	 * 
	 * @param fac factory that will be used to extract information from formulas and
	 *            possibly construct new formulas
	 */
	protected SATSolver(FormulaFactory fac) {
		this.fac = fac;
	}

	/**
	 * Determines whether formula {@code f} is satisfiable and computes a model
	 * satisfying {@code f} if it is.
	 * 
	 * @param f any (non-{@code null}) propositional formula
	 * @return a model satisfying {@code f}, if {@code f} is satisfiable, or
	 *         {@code null} if {@code f} is unsatisfiable
	 */
	public abstract Model sat(Formula f);

	/**
	 * Determines whether formula {@code f} is valid and computes a model refuting
	 * {@code f} if it is not.
	 * 
	 * @param f any (non-{@code null}) propositional formula
	 * @return a model refuting {@code f}, if {@code f} is not valid, or
	 *         {@code null} if {@code f} is valid
	 */
	public Model valid(Formula f) {
		return sat(fac.not(f));
	}

	/**
	 * Determines whether formula {@code f} is satisfiable.
	 * 
	 * @param f any (non-{@code null}) propositional formula
	 * @return {@code true} iff {@code f} is satisfiable
	 */
	public boolean isSat(Formula f) {
		return sat(f) != null;
	}

	/**
	 * Determines whether formula {@code f} is valid.
	 * 
	 * @param f any (non-{@code null}) propositional formula
	 * @return {@code true} iff {@code f} is valid
	 */
	public boolean isValid(Formula f) {
		return !isSat(fac.not(f));

	}

	/**
	 * Determines whether two formulas are equivalent.
	 * 
	 * @param f1 any (non-{@code null}) propositional formula
	 * @param f2 any (non-{@code null}) propositional formula
	 * @return {@code true} iff {@code f1} and {@code f2} are equivalent
	 */
	public boolean equiv(Formula f1, Formula f2) {
		if (verbose) {
			out.println("Checking equivalence of formulas:");
			out.println("f1 = " + f1);
			out.println("f2 = " + f2);
		}
		return isValid(fac.or(fac.and(f1, f2), fac.and(fac.not(f1), fac.not(f2))));
	}

	/**
	 * Sets the verbose bit to the given value. If {@code true}, the SAT solver will
	 * print information about the state of its algorithm as it executes.
	 * 
	 * @param val new value for the verbose bit
	 */
	public void setVerbose(boolean val) {
		this.verbose = val;
	}

	/**
	 * Gets the current value of the verbose bit. If {@code true}, the SAT solver
	 * will print information about the state of its algorithm as it executes.
	 * 
	 * @return current value of this solver's verbose bit
	 */
	public boolean getVerbose() {
		return verbose;
	}

	/**
	 * Prints the message if {@code verbose} is {@code true}. If {@code verbose} is
	 * {@code false}, does nothing.
	 * 
	 * @param msg message to print
	 */
	protected void say(String msg) {
		if (verbose)
			out.print(msg);
	}

	/**
	 * Prints the message and adds a newline, if {@code verbose} is {@code true}. If
	 * {@code verbose} is {@code false}, does nothing.
	 * 
	 * @param msg message to print
	 */
	protected void sayLine(String msg) {
		if (verbose)
			out.println(msg);
	}

}
