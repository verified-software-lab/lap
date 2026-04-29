package org.l4cs.fol.nd;

import java.io.PrintStream;
import java.util.Map;

import org.l4cs.util.TextUtil;

public class HierarchyPrinter {

	private PrintStream out;

	private int numDigits;

	private boolean number = false;

	/**
	 * Prints this derivation in a standard tree format.
	 * 
	 * @param out    where to print
	 * @param d      the derivation to print
	 * @param number add line numbers?
	 */
	HierarchyPrinter(PrintStream out, FOLDerivation d, boolean number) {
		this.out = out;
		this.number = number;
		int maxStep = d.size() - 1;
		this.numDigits = Integer.toString(maxStep).length();
		Map<FOLDerivation, Integer> stepNums = FOLDerivation.getStepNumbers(d);
		printHierarchyAux(d, "", stepNums);
	}

	/**
	 * Recursive function to write a standard tree presentation of this derivation.
	 */
	private void printHierarchyAux(FOLDerivation d, String pretext, Map<FOLDerivation, Integer> stepNums) {
		if (number) {
			int num = stepNums.get(d);
			int numPadding = numDigits - Integer.toString(num).length();
			for (int i = 0; i < numPadding; i++)
				out.print(" ");
			out.print(TextUtil.blue() + num + ". " + TextUtil.reset());
		}
		out.print(TextUtil.blue() + pretext + TextUtil.reset());
		out.println(d.conclusion + TextUtil.ruleColor() + "  (" + d.rule + ")" + TextUtil.reset());
		for (int i = 0; i < d.subderivations.length; i++) {
			printHierarchyAux(d.subderivations[i], pretext + TextUtil.getVLine(), stepNums);
		}
	}

}
