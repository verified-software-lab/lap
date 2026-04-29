package org.l4cs.pl.semantics;

import java.util.BitSet;

import org.l4cs.pl.syntax.Formula;
import org.l4cs.pl.syntax.FormulaFactory;
import org.l4cs.pl.syntax.Proposition;

/**
 * A PL Model, i.e., a function from Propositions to B={True, False}. Note
 * propositions are identified with natural numbers.
 * 
 * <p>
 * A Model is mutable.
 * </p>
 * 
 * @author Stephen Siegel
 */
public class Model {

	protected FormulaFactory fac;

	/**
	 * The data specifying the model. The set consists of the ID numbers of the
	 * propositions mapped to True. All other propositions are assumed to map to
	 * False.
	 */
	protected BitSet bs;

	/**
	 * Creates a new model in which ever proposition maps to False.
	 */
	public Model(FormulaFactory fact) {
		this.fac = fact;
		this.bs = new BitSet();
	}

	/**
	 * Creates a new model initialized with the state of the given model. The given
	 * model is not modified, nor will the new model retain any reference to it.
	 * 
	 * @param that another model
	 */
	public Model(FormulaFactory fact, Model that) {
		this.fac = fact;
		this.bs = (BitSet) that.bs.clone();
	}

	public boolean eval(Formula f) {
		switch (f.kind()) {
		case AND:
			return eval(fac.arg0(f)) && eval(fac.arg1(f));
		case FALSE:
			return false;
		case IMPLIES:
			return !eval(fac.arg0(f)) || eval(fac.arg1(f));
		case NOT:
			return !eval(fac.arg(f));
		case OR:
			return eval(fac.arg0(f)) || eval(fac.arg1(f));
		case PROP:
			return bs.get(fac.id(f));
		default:
			throw new RuntimeException("unreachable");
		}
	}

	public void set(Proposition prop, boolean val) {
		bs.set(prop.id(), val);
	}

	public void set(int propID, boolean val) {
		bs.set(propID, val);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		boolean first = true;
		for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
			if (first)
				first = false;
			else
				sb.append(", ");
			sb.append(fac.getProposition(i).toString());
			if (i == Integer.MAX_VALUE) {
				break; // or (i+1) would overflow
			}
		}
		sb.append("}");
		return sb.toString();
	}

}
