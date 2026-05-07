package org.l4cs.fol.nd;

import java.util.Set;

import org.l4cs.fol.syntax.FOLFormula;
import org.l4cs.util.TextUtil;

/**
 * A sequent is a judgment in the proof system natural deduction for
 * first-order logic. It consists of an antecedent, which is a set of
 * formulas, and a conclusion, which is a single formula.
 * 
 * @author Yuxin Zhou
 */
public class FOLSequent {

	/**
	 * The antecedent, or left hand side, of this sequent. Also known as the
	 * "context".
	 */
	private Set<FOLFormula> antecedent;

	/**
	 * The succedent, or right hand side, of this sequent. Also referred to as the
	 * "conclusion."
	 */
	private FOLFormula succedent;

	/**
	 * Constructs a sequent with the specified antecedent and succedent.
	 * 
	 * @param antecedent the set of formulas on the left side (context)
	 * @param succedent  the formula on the right side (conclusion)
	 */
	public FOLSequent(Set<FOLFormula> antecedent, FOLFormula succedent) {
		this.antecedent = antecedent;
		this.succedent = succedent;
	}

	/**
	 * Returns the antecedent (context) of this sequent.
	 * 
	 * @return the set of formulas in the antecedent
	 */
	public Set<FOLFormula> antecedent() {
		return antecedent;
	}

	/**
	 * Returns the succedent (conclusion) of this sequent.
	 * 
	 * @return the formula in the succedent
	 */
	public FOLFormula succedent() {
		return succedent;
	}

	/**
	 * Returns a string representation of the antecedent formulas.
	 * 
	 * @return comma-separated string of antecedent formulas
	 */
	public String anteString() {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (FOLFormula f : antecedent) {
			if (first)
				first = false;
			else
				sb.append(',');
			sb.append(f.toString());
		}
		return sb.toString();
	}

	/**
	 * Returns a string representation of the succedent formula.
	 * 
	 * @return string representation of the succedent
	 */
	public String succString() {
		return succedent.toString();
	}

	/**
	 * Returns a string representation of this sequent in the form
	 * "antecedent ⊢ succedent".
	 * 
	 * @return string representation of the sequent
	 */
	@Override
	public String toString() {
		return anteString() + " " + TextUtil.infers() + " " + succString();
	}

	/**
	 * Returns a hash code for this sequent based on its antecedent and succedent.
	 * 
	 * @return the hash code
	 */
	@Override
	public int hashCode() {
		int result = 0;
		for (FOLFormula c : antecedent)
			result += c.hashCode();
		result += succedent.hashCode();
		return result;
	}

	/**
	 * Determines whether this sequent is equal to the given object. Two sequents
	 * are equal if they have the same antecedent and succedent.
	 * 
	 * @param obj the object to compare with
	 * @return {@code true} if obj is a FOLSequent with the same antecedent and
	 *         succedent, {@code false} otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FOLSequent) {
			FOLSequent that = (FOLSequent) obj;
			return antecedent.equals(that.antecedent) && succedent.equals(that.succedent);
		}
		return false;
	}

}
