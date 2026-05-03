package org.l4cs.pl.nd;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.l4cs.util.TextUtil;

/**
 * A class used to print this derivation in a linear format. There is one step
 * per line and no indentation. The lines are numbered, and each line ends with
 * the rule and the numbers of the premises. The premises must precede where
 * they are used. A step may be used as a premise of multiple future steps,
 * i.e., there can be "sharing" of premises. Hence the steps may be thought of
 * as a "DAG" rather than a tree.
 * 
 * @author Stephen Siegel
 */
class LinearPrinter {

	int lineno = 0;
	PrintStream out;
	ArrayList<String[]> table = new ArrayList<>();
	Map<Derivation, Integer> seen = new HashMap<>();

	LinearPrinter(PrintStream out, Derivation d) {
		this.out = out;
		write(d);
		TextUtil.printTable(out, table, "rrcll");
	}

	int write(Derivation d) {
		Integer oldStep = seen.get(d);
		if (oldStep != null)
			return oldStep;
		int n = d.subderivations.length;
		int[] premiseLines = new int[n];
		for (int i = 0; i < n; i++)
			premiseLines[i] = write(d.subderivations[i]);
		lineno++;
		String[] row = new String[5];
		row[0] = TextUtil.blueOn() + lineno + "." + TextUtil.colorOff();
		row[1] = d.conclusion.anteString();
		row[2] = TextUtil.infers();
		row[3] = d.conclusion.succString();
		StringBuilder sb = new StringBuilder();
		sb.append(TextUtil.ruleColorOn() + "(" + d.rule + ")" + TextUtil.colorOff());
		sb.append(TextUtil.blueOn());
		for (int i = 0; i < n; i++) {
			if (i > 0)
				sb.append(',');
			sb.append(premiseLines[i]);
		}
		sb.append("." + TextUtil.colorOff());
		row[4] = sb.toString();
		table.add(row);
		seen.put(d, lineno);
		return lineno;
	}
}