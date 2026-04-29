package org.l4cs.cli;

import static org.l4cs.util.TextUtil.Hilight.ANSI;
import static org.l4cs.util.TextUtil.Hilight.NONE;
import static org.l4cs.util.TextUtil.Hilight.TEX;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.l4cs.fol.nd.FOLDerivation;
import org.l4cs.fol.nd.FOLDerivationFactory;
import org.l4cs.fol.nd.FOLViolation;
import org.l4cs.fol.parse.FOLParser;
import org.l4cs.fol.syntax.FOLFormula;
import org.l4cs.fol.syntax.FOLFormulaFactory;
import org.l4cs.pl.nd.Derivation;
import org.l4cs.pl.nd.DerivationFactory;
import org.l4cs.pl.nd.Violation;
import org.l4cs.pl.parse.PLParser;
import org.l4cs.pl.parse.ParseException;
import org.l4cs.pl.parse.TokenMgrError;
import org.l4cs.pl.semantics.Semantics.SATAlgorithm;
import org.l4cs.pl.syntax.Formula;
import org.l4cs.pl.syntax.FormulaFactory;
import org.l4cs.util.TextUtil;

public class CommandLine {

	public final static PrintStream err = System.err;
	public final static PrintStream out = System.out;

	/**
	 * Two languages (so far): Propositional Logic and First Order Logic.
	 */
	public static enum Language {
		PL, FOL
	}

	/**
	 * Ways to display a derivation.
	 */
	public static enum View {
		NONE, LINEAR, TREE, HIERARCHY, FITCH, TUPLE, ALL
	}

	/**
	 * Three possible sources for formulas: a file, a string, and stdin.
	 */
	public static enum InputSource {
		FILE, STRING, IN
	}

	FormulaFactory fac = new FormulaFactory();
	DerivationFactory df = new DerivationFactory(fac);

	FOLFormulaFactory FOLfac = new FOLFormulaFactory();
	FOLDerivationFactory FOLdf = new FOLDerivationFactory(FOLfac);

	/**
	 * An input object (such as a formula or a derivation) is specified by its
	 * source kind and a string. For a FILE, the string is the filename. For a
	 * STRING, the string is the string representation of the object. For IN, string
	 * is not used.
	 */
	class InputSpec {
		InputSource source;
		String str;

		InputSpec(InputSource source, String spec) {
			this.source = source;
			this.str = spec;
		}
	}

	String command;
	boolean verbose = false;
	ArrayList<InputSpec> inputSpecs = new ArrayList<>();
	boolean in = false;
	boolean plain = false;
	View view = View.NONE;
	SATAlgorithm alg = SATAlgorithm.BRUTE_FORCE;
	boolean model = false;
	Language lang = Language.PL;
	boolean help = false;
	String helpCommand = null;
	boolean number = false;

	public CommandLine(String[] args) {
		parse(args);
	}

	public void clErr(String msg) {
		err.println("Command line error: " + msg);
		err.println("Type \"lap help\" for usage information.");
		System.exit(1);
	}

	public void ioErr(String msg) {
		err.println("I/O error: " + msg);
		System.exit(2);
	}

	public void parseErr(String msg) {
		err.println("Parse error: " + msg);
		System.exit(3);
	}

	public void cnfErr(String msg) {
		err.println("CNF error: " + msg);
		System.exit(4);
	}

	private void parse(String[] args) {
		int n = args.length;
		if (n < 1)
			clErr("missing command");
		command = args[0];
		if (command.equals("help")) {
			help = true;
		}
		for (int i = 1; i < n; i++) {
			String arg = args[i];
			if (arg.equals("-in")) {
				in = true;
				inputSpecs.add(new InputSpec(InputSource.IN, null));
			} else if (arg.equals("-v")) {
				verbose = true;
				number = true;
				view = View.ALL;
			} else if (arg.equals("-number")) {
				number = true;
			} else if (arg.equals("-plain")) {
				plain = true;
				TextUtil.setHighlighting(NONE);
			} else if (arg.equals("-tex")) {
				// secret option to output for latex fancyvrb
				TextUtil.setHighlighting(TEX);
			} else if (arg.equals("-ansi")) {
				TextUtil.setHighlighting(ANSI);
			} else if (arg.equals("-f")) {
				if (i == n - 1)
					clErr("Expected string argument after -f");
				String formulaString = args[++i];
				inputSpecs.add(new InputSpec(InputSource.STRING, formulaString));
			} else if (arg.equals("-alg")) {
				if (i == n - 1)
					clErr("Expected algorithm name after -alg");
				String algString = args[++i];
				if (algString.equals("brute"))
					alg = SATAlgorithm.BRUTE_FORCE;
				else if (algString.equals("dpll"))
					alg = SATAlgorithm.DPLL;
				else
					clErr("Unknown algorithm: " + algString);
			} else if (arg.equals("-model")) {
				model = true;
			} else if (arg.equals("-view")) {
				if (i == n - 1)
					clErr("Expected view name after -view");
				String viewString = args[++i];
				switch (viewString) {
				case "none":
					view = View.NONE;
					break;
				case "linear":
					view = View.LINEAR;
					break;
				case "tree":
					view = View.TREE;
					break;
				case "hierarchy":
					view = View.HIERARCHY;
					break;
				case "fitch":
					view = View.FITCH;
					break;
				case "tuple":
					view = View.TUPLE;
					break;
				case "all":
					view = View.ALL;
					break;
				default:
					clErr("unknown view: " + viewString);
				}
			} else if (arg.equals("-lang")) {
				if (i == n - 1)
					clErr("Expected language name after -lang");
				String langString = args[++i];
				if (langString.equals("pl"))
					lang = Language.PL;
				else if (langString.equals("fol"))
					lang = Language.FOL;
				else
					clErr("Unknown language: " + langString);
			} else { // a stand-alone argument
				if (help) {
					if (helpCommand != null)
						clErr("saw two commands for help: \"" + helpCommand + "\" and \"" + arg
								+ "\"\nhelp syntax: \"lap help [options] command\"");
					helpCommand = arg;
				} else
					inputSpecs.add(new InputSpec(InputSource.FILE, arg));
			}
		}

	}

	int getNumInputs() {
		return inputSpecs.size();
	}

	Formula getPLFormulaInput(int i) {
		InputSpec spec = inputSpecs.get(i);
		Formula f = null;
		Reader reader = null;
		PLParser parser;

		if (spec.source == InputSource.IN) {
			parser = new PLParser(fac, df, System.in);
			if (System.console() != null)
				out.print("Enter formula followed by <END>: ");
		} else {
			if (spec.source == InputSource.FILE) {
				try {
					reader = Files.newBufferedReader(Paths.get(spec.str));
				} catch (IOException ioe) {
					ioErr("failed to read file: " + ioe.getMessage());
				}
			} else { // STRING
				reader = new StringReader(spec.str);
			}
			parser = new PLParser(fac, df, reader);
		}
		try {
			if (spec.source == InputSource.IN)
				f = parser.FormulaOnly();
			else
				f = parser.Formula();
		} catch (ParseException e) {
			parseErr("parsing formula: " + e.getMessage());
		} catch (TokenMgrError e) {
			parseErr("parsing formula: " + e.getMessage());
		}
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				ioErr("failed to close input stream: " + e.getMessage());
			}
		}
		return f;
	}

	Derivation getPLDerivationInput(int i) throws Violation {
		InputSpec spec = inputSpecs.get(i);
		Reader reader = null;
		switch (spec.source) {
		case FILE: {
			try {
				reader = Files.newBufferedReader(Paths.get(spec.str));
			} catch (IOException ioe) {
				ioErr("failed to read file: " + ioe.getMessage());
			}
			break;
		}
		case IN: {
			InputStreamReader isr = new InputStreamReader(System.in);
			reader = new BufferedReader(isr);
			break;
		}
		case STRING: {
			reader = new StringReader(spec.str);
			break;
		}
		default:
			throw new RuntimeException("unreachable");
		}
		Derivation der = null;
		PLParser parser = new PLParser(fac, df, reader);
		try {
			der = parser.Derivation();
		} catch (ParseException e) {
			parseErr("parsing derivation: " + e.getMessage());
		} catch (TokenMgrError e) {
			parseErr("parsing derivation: " + e.getMessage());
		}
		if (spec.source != InputSource.IN) {
			try {
				reader.close();
			} catch (IOException e) {
				ioErr("failed to close input reader: " + e.getMessage());
			}
		}
		return der;
	}

	FOLFormula getFOLFormulaInput(int i) {
		InputSpec spec = inputSpecs.get(i);
		FOLFormula f = null;
		Reader reader = null;
		FOLParser parser;

		if (spec.source == InputSource.IN) {
			parser = new FOLParser(FOLfac, FOLdf, System.in);
			if (System.console() != null)
				out.print("Enter formula followed by <END>: ");
		} else {
			if (spec.source == InputSource.FILE) {
				try {
					reader = Files.newBufferedReader(Paths.get(spec.str));
				} catch (IOException ioe) {
					ioErr("failed to read file: " + ioe.getMessage());
				}
			} else { // STRING
				reader = new StringReader(spec.str);
			}
			parser = new FOLParser(FOLfac, FOLdf, reader);
		}
		try {
			if (spec.source == InputSource.IN)
				f = parser.FormulaOnly();
			else
				f = parser.Formula();
		} catch (org.l4cs.fol.parse.ParseException e) {
			parseErr("parsing formula: " + e.getMessage());
		} catch (org.l4cs.fol.parse.TokenMgrError e) {
			parseErr("parsing formula: " + e.getMessage());
		}
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				ioErr("failed to close input stream: " + e.getMessage());
			}
		}
		return f;
	}

	FOLDerivation getFOLDerivationInput(int i) throws FOLViolation {
		InputSpec spec = inputSpecs.get(i);
		Reader reader = null;
		switch (spec.source) {
		case FILE: {
			try {
				reader = Files.newBufferedReader(Paths.get(spec.str));
			} catch (IOException ioe) {
				ioErr("failed to read file: " + ioe.getMessage());
			}
			break;
		}
		case IN: {
			InputStreamReader isr = new InputStreamReader(System.in);
			reader = new BufferedReader(isr);
			break;
		}
		case STRING: {
			reader = new StringReader(spec.str);
			break;
		}
		default:
			throw new RuntimeException("unreachable");
		}
		FOLDerivation der = null;
		FOLParser parser = new FOLParser(FOLfac, FOLdf, reader);
		try {
			der = parser.Derivation();
		} catch (org.l4cs.fol.parse.ParseException e) {
			parseErr("parsing derivation: " + e.getMessage());
		} catch (org.l4cs.fol.parse.TokenMgrError e) {
			parseErr("parsing derivation: " + e.getMessage());
		}
		if (spec.source != InputSource.IN) {
			try {
				reader.close();
			} catch (IOException e) {
				ioErr("failed to close input reader: " + e.getMessage());
			}
		}
		return der;
	}
}
