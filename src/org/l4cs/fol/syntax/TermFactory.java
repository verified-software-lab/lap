package org.l4cs.fol.syntax;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.l4cs.fol.syntax.Term.TermKind;

/**
 * Factory class responsible for creating and managing all Term objects
 * (Variables, Constants, FunctionApplications, FunctionSymbols) in FOL. *
 * Implements the Flyweight pattern for Variables, Constants, FunctionSymbols,
 * but not FunctionApplications to ensure unique instances for structurally
 * identical objects.
 * 
 * @author Yuxin Zhou
 * */
public class TermFactory {

	/**
	 * Let # = FunctionSymbol || Constant || Variable. 
	 * Flyweight Pattern for #. Every # ever requested has
	 * an entry in this map, which maps the #'s unique ID number to
	 * the unique instance of # with that ID.
	 */
	private final Map<Integer, FunctionSymbol> functionSymbolIdMap = new HashMap<>();
	private final Map<Integer, Constant> constantIdMap = new HashMap<>();
	private final Map<Integer, Variable> variableIdMap = new HashMap<>();
	/**
	 * Map from # names to #s. A # may or may not have a name. Each # that has a
	 * name has an entry in this map. This map maps a name to the # with that name.
	 * The name of a # must be a non-null String and unique.
	 */
	private final Map<String, FunctionSymbol> functionSymbolNameMap = new HashMap<>();
	private final Map<String, Constant> constantNameMap = new HashMap<>();
	private final Map<String, Variable> variableNameMap = new HashMap<>();

	public TermFactory() {
		
	}

	// -----------------------------------------------------------\
	// Function Symbol Management
	// -----------------------------------------------------------\

	/**
	 * Returns the canonical FunctionSymbol with the given name and arity, creating
	 * it if necessary.
	 * @param name  The name of the function symbol.
	 * @param arity The arity (number of arguments) of the function symbol.
	 * @return The canonical FunctionSymbol instance.
	 * @throws IllegalArgumentException if a symbol with the same name but different
	 * arity already exists.
	 */
	public FunctionSymbol functionSymbol(String name, int arity) {
		FunctionSymbol symbol = functionSymbolNameMap.get(name);
		if (symbol == null) {
			symbol = new FunctionSymbol(name, arity);
			functionSymbolNameMap.put(name, symbol);
			functionSymbolIdMap.put(symbol.id(), symbol);//ah auto get symbol.id
		} else if (symbol.arity() != arity) {
			throw new IllegalArgumentException("Function symbol '" + name + "' already defined with arity " + symbol.arity()
					+ ", cannot redefine with arity " + arity);
		}
		return symbol;
	}

	public FunctionSymbol functionSymbol(int id) {
		return functionSymbolIdMap.get(id);
	}

	// -----------------------------------------------------------\
	// Constant Management
	// -----------------------------------------------------------\

	/**
	 * Returns the canonical Constant with the given name, creating it if necessary.
	 * Constants are 0-ary functions.
	 * @param name The name of the constant.
	 * @return The canonical Constant instance.
	 */
	public Constant constant(String name) {
		Constant constant = constantNameMap.get(name);
		if (constant == null) {
			constant = new Constant(name);
			constantNameMap.put(name, constant);
			constantIdMap.put(constant.id(), constant);
		}
		return constant;
	}

	public Constant constant(int id) {
		return constantIdMap.get(id);
	}

	// -----------------------------------------------------------\
	// Variable Management
	// -----------------------------------------------------------\

	/**
	 * Returns the canonical Variable with the given name, creating it if necessary.
	 * @param name The name of the variable.
	 * @return The canonical Variable instance.
	 */
	public Variable variable(String name) {
		Variable variable = variableNameMap.get(name);
		if (variable == null) {
			variable = new Variable(name);
			variableNameMap.put(name, variable);
			variableIdMap.put(variable.id(), variable);
		}
		return variable;
	}

	public Variable variable(int id) {
		return variableIdMap.get(id);
	}

	// -----------------------------------------------------------\
	// Function Application Management
	// -----------------------------------------------------------\

	/**
	 * Creates a FunctionApplication term.
	 * @param symbol The function symbol.
	 * @param args   The list of Terms that are arguments to the function.
	 * @return The FunctionApplication Term.
	 * @throws IllegalArgumentException if the number of arguments doesn't match the
	 * symbol's arity.
	 */
	public FunctionApp functionApplication(FunctionSymbol symbol, Term[] args) {
		if (args.length != symbol.arity()) {
			throw new IllegalArgumentException("Function symbol '" + symbol.toString() + "' requires " + symbol.arity()
					+ " arguments, but received " + args.length);
		}

		return new FunctionApp(symbol, args);
	}

	// -----------------------------------------------------------\
	// Utility Methods
	// -----------------------------------------------------------\

	public boolean isVar(Term a) {
		return a.kind() == TermKind.VAR;
	}

	public boolean isConst(Term a) {
		return a.kind() == TermKind.CONST;
	}

	public boolean isFunctionApp(Term a) {
		return a.kind() == TermKind.FUNCTIONAPP;
	}

	public boolean isGround(Term a) {
		return vars(a).isEmpty();
	}

	// recursively, get a set of variables from a term.
	public Set<Variable> vars(Term a) {
		Set<Variable> set = new HashSet<>();
		varsAux(a, set);
		return set;
	}
	
	public void varsAux(Term a, Set<Variable> set) {
		if (isVar(a))
			set.add((Variable) a);
		else if (isFunctionApp(a)) {
			for (Term t : ((FunctionApp) a).arguments()) {
				varsAux(t, set);// recursively go over each Term of the function application
			}
		}
		// Constants have no variables, so we do nothing if isConst(a) is true.
	}
}