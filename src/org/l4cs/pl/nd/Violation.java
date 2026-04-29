package org.l4cs.pl.nd;

import java.io.PrintStream;

/**
 * An object representing a violation of a Rule. When a rule is violated an
 * object of this type is created. It provides detailed, information explaining
 * exactly how the rule was violated.
 * 
 * @author Stephen Siegel
 */
public class Violation extends Exception {

	private static final long serialVersionUID = 1L; // to satisfy Eclipse

	/**
	 * The rule that was violated.
	 */
	private Rule rule;

	/**
	 * Detailed explanation of the violation.
	 */
	private String explanation;

	/**
	 * Optional string identifying the location (e.g., line number) of the
	 * violation.
	 */
	private String location = null;

	private Sequent conclusion;

	private Sequent[] premises;

	public Violation(Rule rule, Sequent conclusion, Sequent[] premises, String explanation) {
		this.rule = rule;
		this.explanation = explanation;
		this.conclusion = conclusion;
		this.premises = premises;
	}

	public Violation(Rule rule, Sequent conclusion, Sequent[] premises, StringBuffer buf) {
		this(rule, conclusion, premises, buf.toString());
	}

	public Rule rule() {
		return rule;
	}

	public String explanation() {
		return explanation;
	}

	public Sequent conclusion() {
		return conclusion;
	}

	public Sequent[] premises() {
		return premises;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String location() {
		return location;
	}

	public void print(PrintStream out) {
		out.print("Violation of rule " + rule);
		if (location != null)
			out.print(" at " + location);
		out.println(":");
		for (int i = 0; i < premises.length; i++)
			out.println("  Premise " + (i + 1) + "  : " + premises[i]);
		out.println("  Conclusion : " + conclusion);
		out.println(explanation);
		rule.printDescription(out);
	}

	@Override
	public String toString() {
		return "Violation of rule " + rule;
	}

}
