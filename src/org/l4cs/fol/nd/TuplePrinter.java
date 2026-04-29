package org.l4cs.fol.nd;

import java.io.PrintStream;
import java.util.Map;

import org.l4cs.util.TextUtil;

public class TuplePrinter {

	private PrintStream out;

	private boolean number;

	TuplePrinter(PrintStream out, FOLDerivation d, boolean number) {
		this.out = out;
		this.number = number;
		Map<FOLDerivation, Integer> stepNums = FOLDerivation.getStepNumbers(d);
		printExpressionAux(d, stepNums, 0);
	}

	private void printExpressionAux(FOLDerivation d, Map<FOLDerivation, Integer> stepNums, int indent) {
		out.print("(");
		if (number) {
			int num = stepNums.get(d);
			out.print(TextUtil.blue() + TextUtil.getCircled(num) + TextUtil.reset() + " ");
		}
		out.print(d.conclusion);
		out.print(", ");
		out.print(TextUtil.ruleColor() + d.rule + TextUtil.reset());
		out.print(", (");
		boolean first = true;
		for (FOLDerivation sub : d.subderivations) {
			if (!first)
				out.print(", ");
			out.print("\n" + "  ".repeat(indent + 1));
			printExpressionAux(sub, stepNums, indent + 1);
			first = false;
		}
		out.print("))");
	}

}
