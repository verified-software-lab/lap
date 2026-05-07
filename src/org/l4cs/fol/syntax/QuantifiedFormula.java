package org.l4cs.fol.syntax;

import static org.l4cs.fol.syntax.FOLFormula.FormulaKind.EXISTS;
import static org.l4cs.fol.syntax.FOLFormula.FormulaKind.FORALL;

import java.util.Objects;
import org.l4cs.util.TextUtil; // Assuming TextUtil provides logical symbols like EXISTS, FORALL

/**
 * Represents a quantified Formula (Exists x A or Forall x A) in First-Order
 * Logic (FOL). Its canonical identity is based on its structure: the quantifier
 * kind, the quantified variable, and the formula body.
 * 
 * @author Yuxin Zhou
 */
public class QuantifiedFormula extends FOLFormula {

	private final Variable quantifiedV;
	private final FOLFormula body;
	// The FormulaKind is already stored in the superclass (Formula.kind)
	// private FormulaKind kind; //is not needed.

	/**
	 * Constructs a new QuantifiedFormula. Should be called only by FormulaFactory.
	 */
	QuantifiedFormula(FormulaKind kind, FOLFormula body, Variable quantifiedV) {
		super(kind); // Pass kind to superclass

		// Enforce preconditions
		if (kind != EXISTS && kind != FORALL) {
			throw new IllegalArgumentException("QuantifiedFormula must be EXISTS or FORALL.");
		}
		if (quantifiedV == null || body == null) {
			throw new NullPointerException("Quantified variable and body must not be null.");
		}

		this.quantifiedV = quantifiedV;
		this.body = body;
	}

	public Variable quantifiedV() {
		return quantifiedV;
	}

	public FOLFormula body() {
		return body;
	}

	// -----------------------------------------------------------
	// Canonical Identity Methods
	// -----------------------------------------------------------

	@Override
	public int hashCode() {
		// Calculate hash based on the three components that define its identity:
		// kind (from superclass), quantifiedV, and body.
		// Using Objects.hash for safe, standard practice.
		return Objects.hash(kind(), quantifiedV, body);//
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		QuantifiedFormula that = (QuantifiedFormula) obj;

		// 1. Check Quantifier Kind (EXISTS vs FORALL)
		if (this.kind() != that.kind())
			return false;

		// 2. Check Quantified Variable: Must be the same canonical instance (by ID)
		if (!this.quantifiedV.equals(that.quantifiedV))
			return false;

		// 3. Check Body: Must be the same canonical instance (structurally equal)
		return this.body.equals(that.body);	
	}

	// -----------------------------------------------------------
	// Standard Methods
	// -----------------------------------------------------------

	@Override
	public String toString() {
		String quantifierSymbol = (kind() == EXISTS) ? TextUtil.EXISTS : TextUtil.FORALL;

		String b = body.toString();
		// Ensure inner formulas (especially binary ones) are parenthesized for clarity
		if (body instanceof FOLBinaryFormula || body instanceof QuantifiedFormula) {
			b = "(" + b + ")";
		}

		return quantifierSymbol + quantifiedV.toString() + "." + b;
	}
}