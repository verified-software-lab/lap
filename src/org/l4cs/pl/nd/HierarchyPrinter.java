package org.l4cs.pl.nd;

import java.io.PrintStream;
import java.util.Map;

import org.l4cs.util.TextUtil;

/**
 * An object used to print a derivation in hierarchical format. This is a
 * textual representation of a tree with one node on each line, root at top, and
 * the text for the children nodes indented from the parent.
 * 
 * @author Stephen Siegel
 */
public class HierarchyPrinter {

	private PrintStream out;

	private int numDigits;

	private boolean number = false;

	/**
	 * Prints this derivation in a standard tree format.
	 * 
	 * @param out where to print
	 */
	HierarchyPrinter(PrintStream out, Derivation d, boolean number) {
		this.out = out;
		this.number = number;
		int maxStep = d.size() - 1;
		this.numDigits = Integer.toString(maxStep).length();
		Map<Derivation, Integer> stepNums = Derivation.getStepNumbers(d);
		printHierarchyAux(d, "", stepNums);
	}

	/**
	 * Recursive function to write a standard tree presentation of this derivation.
	 */
	private void printHierarchyAux(Derivation d, String pretext, Map<Derivation, Integer> stepNums) {
		if (number) {
			int num = stepNums.get(d);
			int numPadding = numDigits - Integer.toString(num).length();
			for (int i = 0; i < numPadding; i++)
				out.print(" ");
			out.print(TextUtil.blueOn() + num + ". " + TextUtil.colorOff());
		}
		out.print(TextUtil.blueOn() + pretext + TextUtil.colorOff());
		out.println(d.conclusion + TextUtil.ruleColorOn() + "  (" + d.rule + ")" + TextUtil.colorOff());
		for (int i = 0; i < d.subderivations.length; i++) {
			printHierarchyAux(d.subderivations[i], pretext + TextUtil.getVLine(), stepNums);
		}
	}

}
