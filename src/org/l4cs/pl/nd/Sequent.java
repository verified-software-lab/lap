package org.l4cs.pl.nd;

import java.util.Set;

import org.l4cs.pl.syntax.Formula;
import org.l4cs.util.TextUtil;

/**
 * A sequent is a judgment in the proof system natural deduction for
 * propositional logic. It consists of an antecedent, which is a set of
 * formulas, and a conclusion, which is a single formula.
 * 
 * @author Stephen Siegel
 */
public class Sequent {

	/**
	 * The antecedent, or left hand side, of this sequent. Also known as the
	 * "context".
	 */
	private Set<Formula> antecedent;

	/**
	 * The succedent, or right hand side, of this sequent. Also referred to as the
	 * "conclusion."
	 */
	private Formula succedent;

	public Sequent(Set<Formula> antecedent, Formula succedent) {
		this.antecedent = antecedent;
		this.succedent = succedent;
	}

	public Set<Formula> antecedent() {
		return antecedent;
	}

	public Formula succedent() {
		return succedent;
	}

	public String anteString() {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Formula f : antecedent) {
			if (first)
				first = false;
			else
				sb.append(',');
			sb.append(f.toString());
		}
		return sb.toString();
	}

	public String succString() {
		return succedent.toString();
	}

	@Override
	public String toString() {
		return anteString() + " " + TextUtil.infers() + " " + succString();
	}

	@Override
	public int hashCode() {
		int result = 0;
		for (Formula c : antecedent)
			result += c.hashCode();
		result += succedent.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Sequent) {
			Sequent that = (Sequent) obj;
			return antecedent.equals(that.antecedent) && succedent.equals(that.succedent);
		}
		return false;
	}

}
