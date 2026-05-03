package org.l4cs.pl.nd;

import java.io.PrintStream;
import java.util.Map;

import org.l4cs.util.TextUtil;

/**
 * An object used to print a derivation in "tuple" format.
 * 
 * @author Yuxin Zhou
 */
public class TuplePrinter {

	private PrintStream out;

	private boolean number;

	TuplePrinter(PrintStream out, Derivation d, boolean number) {
		this.out = out;
		this.number = number;
		Map<Derivation, Integer> stepNums = Derivation.getStepNumbers(d);
		printExpressionAux(d, stepNums, 0);
	}

	private void printExpressionAux(Derivation d, Map<Derivation, Integer> stepNums, int indent) {
		out.print("(");
		if (number) {
			int num = stepNums.get(d);
			out.print(TextUtil.blueOn() + TextUtil.getCircled(num) + TextUtil.colorOff() + " ");
		}
		out.print(d.conclusion);
		out.print(", ");
		out.print(TextUtil.ruleColorOn() + d.rule + TextUtil.colorOff());
		out.print(", (");

		boolean first = true;
		for (Derivation sub : d.subderivations) {
			if (!first) {
				out.print(", ");
			}
			out.print("\n" + "  ".repeat(indent + 1));
			printExpressionAux(sub, stepNums, indent + 1);
			first = false;
		}
		out.print("))");
	}

}
