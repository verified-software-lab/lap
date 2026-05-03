package org.l4cs.fol.nd;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.l4cs.fol.syntax.FOLBinaryFormula;
import org.l4cs.fol.syntax.FOLFormula;
import org.l4cs.fol.syntax.FOLNotFormula;
import org.l4cs.util.TextUtil;

/**
 * Prints a derivation in Fitch format.
 */
public class FitchPrinter {

	private int lineno = 0;
	private ArrayList<String[]> table = new ArrayList<>();
	private Map<FOLDerivation, Integer> seen = new HashMap<>();

	FitchPrinter(PrintStream out, FOLDerivation derivation) {
		// first write the context, one formula per line:
		writeContext(1, derivation.conclusion.antecedent());
		write(1, derivation);
		TextUtil.printTable(out, table, "rll");
	}

	private void writeNumberedLine(int depth, FOLFormula formula, FOLRule r, int[] plines) {
		String[] newLine = new String[3];
		lineno++;
		newLine[0] = TextUtil.blueOn() + lineno + "." + TextUtil.colorOff();
		StringBuilder sb = new StringBuilder();
		sb.append(TextUtil.blueOn());
		for (int i = 0; i < depth; i++)
			sb.append(TextUtil.getVLine());
		sb.append(TextUtil.colorOff());
		sb.append(formula);
		newLine[1] = sb.toString();
		sb = new StringBuilder();
		sb.append(TextUtil.ruleColorOn() + "(" + r + ")" + TextUtil.colorOff());
		sb.append(TextUtil.blueOn());
		for (int i = 0; i < plines.length; i++) {
			if (i > 0)
				sb.append(",");
			sb.append(plines[i]);
		}
		sb.append(TextUtil.colorOff());
		newLine[2] = sb.toString();
		table.add(newLine);
	}

	private void writeUnnumberedLine(int depth, FOLFormula formula) {
		String[] newLine = new String[3];
		newLine[0] = "";
		StringBuilder sb = new StringBuilder();
		sb.append(TextUtil.blueOn());
		for (int i = 0; i < depth; i++)
			sb.append(TextUtil.getVLine());
		sb.append(TextUtil.colorOff());
		sb.append(formula);
		newLine[1] = sb.toString();
		newLine[2] = "";
		table.add(newLine);
	}

	private void writeHorizontalLine(int depth) {
		String[] newLine = new String[3];
		newLine[0] = "";
		StringBuilder sb = new StringBuilder();
		sb.append(TextUtil.blueOn());
		for (int i = 0; i < depth - 1; i++)
			sb.append(TextUtil.getVLine());
		sb.append(TextUtil.getTBranch());
		sb.append(TextUtil.colorOff());
		newLine[1] = sb.toString();
		newLine[2] = "";
		table.add(newLine);
	}

	private void writeBlankLine(int depth) {
		String[] newLine = new String[3];
		newLine[0] = "";
		StringBuilder sb = new StringBuilder();
		sb.append(TextUtil.blueOn());
		for (int i = 0; i < depth; i++)
			sb.append(TextUtil.getVLine());
		sb.append(TextUtil.colorOff());
		newLine[1] = sb.toString();
		newLine[2] = "";
		table.add(newLine);
	}

	private void writeContext(int depth, Set<FOLFormula> formulas) {
		if (formulas.isEmpty())
			return;
		for (FOLFormula f : formulas)
			writeUnnumberedLine(depth, f);
		writeHorizontalLine(depth);
	}

	private int write(int depth, FOLDerivation d) {
		Integer oldStep = seen.get(d);
		if (oldStep != null)
			return oldStep;
		int n = d.subderivations.length;
		int[] premiseLines = new int[n];
		if (d.rule instanceof IntroImplies_FOL) {
			FOLFormula a = ((FOLBinaryFormula) d.conclusion.succedent()).arg0();
			writeUnnumberedLine(depth + 1, a);
			writeHorizontalLine(depth + 1);
			premiseLines[0] = write(depth + 1, d.subderivations[0]);
		} else if (d.rule instanceof IntroNot_FOL) {
			FOLFormula a = ((FOLNotFormula) d.conclusion.succedent()).arg();
			writeUnnumberedLine(depth + 1, a);
			writeHorizontalLine(depth + 1);
			premiseLines[0] = write(depth + 1, d.subderivations[0]);
		} else if (d.rule instanceof RAA_FOL) {
			FOLFormula a = d.conclusion.succedent();
			writeUnnumberedLine(depth + 1, new FOLNotFormula(a));
			writeHorizontalLine(depth + 1);
			premiseLines[0] = write(depth + 1, d.subderivations[0]);
		} else if (d.rule instanceof ElimOr_FOL) {
			FOLBinaryFormula or = (FOLBinaryFormula) d.subderivations[0].conclusion.succedent();
			FOLFormula a = or.arg0(), b = or.arg1();
			premiseLines[0] = write(depth, d.subderivations[0]);
			writeUnnumberedLine(depth + 1, a);
			writeHorizontalLine(depth + 1);
			premiseLines[1] = write(depth + 1, d.subderivations[1]);
			writeBlankLine(depth);
			writeUnnumberedLine(depth + 1, b);
			writeHorizontalLine(depth + 1);
			premiseLines[2] = write(depth + 1, d.subderivations[2]);
		} else if (d.rule instanceof ElimExists) {
			// [CHANGE] Implementation for Existential Elimination (Fitch style)
			// Premise 0 is the existential formula: exists x. P(x)
			// Premise 1 is the sub-derivation from [P(y)] to the conclusion
			premiseLines[0] = write(depth, d.subderivations[0]);

			// Extract the witness formula (the assumption of the sub-derivation)
			// In natural deduction, the sub-derivation's antecedent contains the new
			// witness.
			// We find the formula that was added to the antecedent in subderivations[1].
			Set<FOLFormula> ant0 = d.subderivations[0].conclusion.antecedent();
			Set<FOLFormula> ant1 = d.subderivations[1].conclusion.antecedent();
			FOLFormula witnessAssumption = null;
			for (FOLFormula f : ant1) {
				if (!ant0.contains(f)) {
					witnessAssumption = f;
					break;
				}
			}

			// If we couldn't find a new formula,
			// we fallback to showing the conclusion of the first step of the sub-derivation
			if (witnessAssumption == null) {
				witnessAssumption = d.subderivations[1].conclusion.succedent();
			}

			writeUnnumberedLine(depth + 1, witnessAssumption);
			writeHorizontalLine(depth + 1);
			premiseLines[1] = write(depth + 1, d.subderivations[1]);
		} else {
			for (int i = 0; i < n; i++)
				premiseLines[i] = write(depth, d.subderivations[i]);
		}
		writeNumberedLine(depth, d.conclusion.succedent(), d.rule, premiseLines);
		seen.put(d, lineno);
		return lineno;
	}
}
