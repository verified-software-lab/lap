package org.l4cs.fol.nd;

import java.io.PrintStream;

/**
 * An object representing a violation of a Rule. When a rule is violated an
 * object of this type is created. It provides detailed, information explaining
 * exactly how the rule was violated
 */
public class FOLViolation extends Exception {

	private static final long serialVersionUID = 1L; // to satisfy Eclipse

	/**
	 * The rule that was violated.
	 */
	private FOLRule rule;

	/**
	 * Detailed explanation of the violation.
	 */
	private String explanation;

	/**
	 * Optional string identifying the location (e.g., line number) of the
	 * violation.
	 */
	private String location = null;

	private FOLSequent conclusion;

	private FOLSequent[] premises;

	public FOLViolation(FOLRule rule, FOLSequent conclusion, FOLSequent[] premises, String explanation) {
		this.rule = rule;
		this.explanation = explanation;
		this.conclusion = conclusion;
		this.premises = premises;
	}

	public FOLViolation(FOLRule rule, FOLSequent conclusion, FOLSequent[] premises, StringBuffer buf) {
		this(rule, conclusion, premises, buf.toString());
	}

	public FOLRule rule() {
		return rule;
	}

	public String explanation() {
		return explanation;
	}

	public FOLSequent conclusion() {
		return conclusion;
	}

	public FOLSequent[] premises() {
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
