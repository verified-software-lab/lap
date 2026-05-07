package org.l4cs.fol.syntax;

import java.util.concurrent.atomic.AtomicInteger;
//import java.util.Objects;

/**
 * Base class for all canonical symbols in First-Order Logic (FOL), including
 * Variables, Constants, FunctionSymbols, and PredicateSymbols. It manages the
 * unique ID for the Flyweight Pattern and the optional name. (Base class for
 * all canonical symbols—Variables, Constants, Functions, Predicates—handling
 * the mandatory, unique ID.)
 * 
 * @author Yuxin Zhou
 */
public abstract class FOLAbstractSymbol {

	// Thread-safe counter for generating globally unique IDs+?+
	private static final AtomicInteger nextId = new AtomicInteger(0);

	private final int id;
	private final String name;

	/**
	 * Constructs a new AbstractSymbol, assigning it a unique ID.
	 * 
	 * @param name The human-readable name of the symbol (can be null).
	 */
	FOLAbstractSymbol(String name) {
		this.id = nextId.getAndIncrement(); // Assign next globally unique ID
		this.name = name;
	}

	public int id() {
		return id;
	}

	/**
	 * @return The human-readable name, or null if this symbol is anonymous.
	 */
	public String name() {
		return name;
	}

	/**
	 * Canonical identity is defined solely by the unique ID.
	 */
	@Override
	public final int hashCode() {
		return Integer.hashCode(id);
	}

	/**
	 * Two canonical symbols are equal if and only if they are the same instance,
	 * which is guaranteed by the unique ID (Flyweight Pattern).
	 */
	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		// This works because the ID is globally unique across all AbstractSymbols
		FOLAbstractSymbol other = (FOLAbstractSymbol) obj;
		return id == other.id;// now compare id
	}
}