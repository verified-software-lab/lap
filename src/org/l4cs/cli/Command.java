package org.l4cs.cli;

import java.io.PrintStream;

import org.l4cs.util.Block;
import org.l4cs.util.TextUtil;

public abstract class Command {

	// Static fields...

	protected static PrintStream out = System.out;

	protected static PrintStream err = System.err;

	// Static methods...

	// bold face
	protected static String bf(String arg) {
		return TextUtil.bold(arg);
	}

	// underline
	protected static String ul(String arg) {
		return TextUtil.underline(arg);
	}

	// paragraph block, to be wrapped, newline added at end if not there.
	protected static Block par(String... strings) {
		return Block.par(strings);
	}

	// fixed block: do not wrap. Add newline if not there.
	protected static Block fix(String... strings) {
		return Block.fix(strings);
	}

	// sub-block: heading and indented body
	protected static Block sub(String heading, Block body) {
		return Block.sub(heading, body);
	}

	// sequence of blocks separated by newlines
	protected static Block seq(Block... blocks) {
		return Block.seq(blocks);
	}

	// tight sequence: no newlines between blocks
	protected static Block cat(Block... blocks) {
		return Block.cat(blocks);
	}

	protected static String opt0(String one, String two) {
		return bf(one) + ", " + bf(two);
	}

	protected static String opt1(String one, String arg, String two) {
		return bf(one) + " " + ul(arg) + ", " + bf(two + "=") + ul(arg);
	}

	protected static Block optModel(String extra) {
		return sub(opt0("-m", "--model"), par("Display a model " + extra));
	}

	protected static Block optVerbose(String extra) {
		return sub(opt0("-v", "--verbose"), par("Print verbose output.", extra));
	}

	protected static Block optVerbose() {
		return optVerbose("");
	}

	protected static Block optPlain() {
		return sub(opt0("-p", "--plain"), par("Output only ASCII text with no highlighting."));
	}

	protected static Block optHelp(String extra) {
		return sub(opt0("-h", "--help"), par("Display usage information.", extra));
	}

	protected static Block optHelp() {
		return optHelp("");
	}

	protected static Block optIn(String extra) {
		return sub(opt0("-i", "--in"), par("Take input from stdin rather than a file.", extra));
	}

	protected static Block optIn() {
		return optIn("");
	}

	protected static Block optNumber() {
		return sub(opt0("-n", "--number"), par("Number the steps or nodes in a derivation.  ",
				"Note steps in a linear format or Fitch diagram are always numbered."));
	}

	protected static Block optVersion(String extra) {
		return sub(bf("--version"), par("Print version information.", extra));
	}

	protected static Block optLang(String extra) {
		return sub(opt1("-l", "language", "--lang"),
				par("Specify the logical language. ", ul("language"), " is one of ", bf("pl"),
						" (propositional logic, the default), or ", bf("fol"), " (first order logic).", extra));
	}

	protected static Block optLang() {
		return optLang("");
	}

	protected static Block optAlg(String extra) {
		return sub(opt1("-a", "algorithm", "--alg"),
				par("Specify the algorithm. ", ul("algorithm"), " is one of ", bf("brute"),
						" (brute force, the default), or ", bf("dpll"),
						" (DPLL algorithm composed with Tseyin transform).", extra));
	}

	protected static Block optAlg() {
		return optAlg("");
	}

	protected static Block optView() {
		return sub(opt1("-V", "view", "--view"), //
				cat(par("Specify the format for the derivation.  " + ul("view") + " is one of: "), //
						fix(bf("none") + "      - display nothing"), //
						fix(bf("tuple") + "     - tuple format"), //
						fix(bf("tree") + "      - tree format, root on bottom"), //
						fix(bf("hierarchy") + " - directory hierarchy style, root on top"), //
						fix(bf("fitch") + "     - Fitch diagram"), //
						fix(bf("linear") + "    - linear format, conclusion at bottom"), //
						fix(bf("all") + "       - display all formats")));
	}

	protected static Block optFormula() {
		return sub(opt1("-f", "formula", "--formula"),
				par("read the formula from the string " + ul("formula") + " instead of a file"));
	}

	protected static Block optEncoding() {
		return sub(opt1("-e", "encoding", "--encoding"),
				par("Specify the character set. ", ul("encoding"), " is one of ", bf("ascii"),
						" (ASCII text only), or ", bf("unicode"), " (Unicode characters).  ",
						"The default is determined by the detected terminal characteristics."));
	}

	protected static Block optHighlight() {
		return sub(opt1("-H", "style", "--highlight"),
				par("Specify the method used to highlight characters, e.g., using colors. ", ul("style"),
						" is one of: ", bf("none"), ", ", bf("ansi"),
						" (use ANSI escape sequences supported by most modern terminals), or ", bf("tex"),
						" (use LaTeX macros suitable for inclusion in a Verbatim environment ",
						"using the fancyvrb package)."));
	}

	// Instance fields...

	protected CommandLine cl;

	// Constructor...

	public Command(CommandLine cl) {
		this.cl = cl;
	}

	// Instance methods ...

	public abstract String name();

	public abstract String shortDescription();

	public abstract String synopsis();

	public abstract Block description();

	public abstract void execute();

	public void man(PrintStream out) {
		out.print(helpBlock());
	}

	protected Block helpBlock() {
		Block nameBlock = sub(bf("NAME"), par(bf(name()) + " - " + shortDescription()));
		Block synopsisBlock = sub(bf("SYNOPSIS"), par(synopsis()));
		Block descriptionBlock = sub(bf("DESCRIPTION"), description());
		return seq(nameBlock, synopsisBlock, descriptionBlock);
	}
}
