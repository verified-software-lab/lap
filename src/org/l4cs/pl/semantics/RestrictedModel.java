package org.l4cs.pl.semantics;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.l4cs.pl.syntax.FormulaFactory;
import org.l4cs.pl.syntax.Proposition;

/**
 * A restricted model is a model for some specific finite set of propositions.
 * All propositions not in that set are always mapped to false. The set of
 * restricted models for a fixed set are ordered in a chain. The least model is
 * the one that maps every proposition to False. The greatest element is the one
 * mapping every proposition in the specified set to True, and all other
 * propositions to false. There are 2^n models in this chain, where n is the
 * size of the specified set of propositions.
 * 
 * @author Stephen Siegel
 */
public class RestrictedModel extends Model {

	/**
	 * The ID numbers of the propositions in the specified set. The ID numbers occur
	 * in strictly increasing order in this array.
	 */
	private int[] ids;

	/**
	 * Creates new restricted model based on the given set of propositions.
	 * 
	 * @param fac   the formula factory used to manipulate formulas
	 * @param props some collection of propositions
	 */
	public RestrictedModel(FormulaFactory fac, Collection<Proposition> props) {
		super(fac);
		// get the unique set of IDs, sorted...
		Set<Integer> idSet = new HashSet<>();
		for (Proposition p : props)
			idSet.add(p.id());
		int n = idSet.size();
		ids = new int[n];
		int c = 0;
		for (int id : idSet)
			ids[c++] = id;
		Arrays.sort(ids);
	}

	/**
	 * Creates new restricted model by copying the data from the given one.
	 * 
	 * @param that another restricted model
	 */
	public RestrictedModel(RestrictedModel that) {
		super(that.fac);
		this.ids = Arrays.copyOf(that.ids, that.ids.length);
	}

	/**
	 * Is there a model after this one in the chain?
	 * 
	 * @return {@code true} iff this model is not the model that maps every member
	 *         of the specified set to True.
	 */
	public boolean hasNext() {
		int n = ids.length;
		for (int i = 0; i < n; i++) {
			if (!bs.get(ids[i]))
				return true;
		}
		return false;
	}

	/**
	 * Modifies this model to make it the next model in the model chain. If this is
	 * the last model in the chain, this method is a no-op.
	 * 
	 * @return {@code true} iff the model was changed, i.e., this model was not the
	 *         last in the chain.
	 */
	public boolean next() {
		int n = ids.length;
		for (int i = 0; i < n; i++) {
			if (!bs.get(ids[i])) {
				bs.set(ids[i]);
				for (int j = 0; j < i; j++)
					bs.clear(ids[j]);
				return true;
			}
		}
		return false;
	}

}
