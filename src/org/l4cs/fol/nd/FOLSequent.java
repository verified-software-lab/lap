package org.l4cs.fol.nd;

import java.util.Set;

import org.l4cs.fol.syntax.FOLFormula;
import org.l4cs.util.TextUtil;

public class FOLSequent {

	private Set<FOLFormula> antecedent;

	private FOLFormula succedent;

	public FOLSequent(Set<FOLFormula> antecedent, FOLFormula succedent) {
		this.antecedent = antecedent;
		this.succedent = succedent;
	}

	public Set<FOLFormula> antecedent() {
		return antecedent;
	}

	public FOLFormula succedent() {
		return succedent;
	}

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
		for (FOLFormula c : antecedent)
			result += c.hashCode();
		result += succedent.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FOLSequent) {
			FOLSequent that = (FOLSequent) obj;
			return antecedent.equals(that.antecedent) && succedent.equals(that.succedent);
		}
		return false;
	}

}
