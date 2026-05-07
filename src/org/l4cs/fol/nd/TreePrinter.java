package org.l4cs.fol.nd;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.l4cs.util.TextUtil;

/**
 * An object used to print a derivation in "tree" format.
 * 
 * @author Yuxin Zhou
 */
public class TreePrinter {
	
	private boolean number = false;

	private static class TreeBlock {
		List<String> lines;
		int width;
		// int center;

		TreeBlock(List<String> lines, int width, int center) {
			this.lines = lines;
			this.width = width;
			// this.center = center;
		}
	}

	TreePrinter(PrintStream out, FOLDerivation d, boolean number) {
		this.number = number;
		Map<FOLDerivation, Integer> stepNums = FOLDerivation.getStepNumbers(d);
		TreeBlock tb = buildTreeBlock(d, stepNums);
		for (String line : tb.lines) {
			out.println(line);
		}
	}

	private TreeBlock buildTreeBlock(FOLDerivation d, Map<FOLDerivation, Integer> stepNums) {
		int num = stepNums.get(d);
		String marker = number ? TextUtil.getCircled(num) : "";
		String pureConcl = marker + " " + d.conclusion.toString();
		String ruleStr = "(" + d.rule.toString() + ") ";
		int pureConclLen = pureConcl.length();
		int ruleLen = ruleStr.length();
		String coloredRule = TextUtil.ruleColorOn() + "(" + d.rule.toString() + ")" + TextUtil.colorOff() + " ";
		String coloredConcl = TextUtil.blueOn() + marker + TextUtil.colorOff() + " " + d.conclusion.toString();

		if (d.subderivations.length == 0) {
			int barWidth = Math.max(pureConclLen, 6);
			String barLine = coloredRule + TextUtil.repeatStr(TextUtil.getHLine(), barWidth);
			int paddingLeft = (barWidth - pureConclLen) / 2;
			String conclLine = TextUtil.repeatStr(" ", ruleLen + paddingLeft) + coloredConcl;

			List<String> lines = new ArrayList<>();
			lines.add(TextUtil.repeatStr(" ", ruleLen + barWidth));
			lines.add(barLine);
			lines.add(conclLine);
			return new TreeBlock(lines, ruleLen + barWidth, ruleLen + barWidth / 2);
		}

		TreeBlock[] subBlocks = new TreeBlock[d.subderivations.length];
		for (int i = 0; i < d.subderivations.length; i++) {
			subBlocks[i] = buildTreeBlock(d.subderivations[i], stepNums);
		}

		int gap = 2;
		int subsWidth = 0;
		int maxHeight = 0;
		for (TreeBlock tb : subBlocks) {
			subsWidth += tb.width;
			if (tb.lines.size() > maxHeight)
				maxHeight = tb.lines.size();
		}
		subsWidth += gap * (subBlocks.length - 1);

		int barWidth = Math.max(subsWidth, pureConclLen);
		int totalWidth = ruleLen + barWidth;

		List<String> combinedSubs = new ArrayList<>();
		int subProofStartOffset = ruleLen + (barWidth - subsWidth) / 2;

		for (int h = 0; h < maxHeight; h++) {
			StringBuilder row = new StringBuilder();
			row.append(TextUtil.repeatStr(" ", subProofStartOffset));
			for (int i = 0; i < subBlocks.length; i++) {
				TreeBlock tb = subBlocks[i];
				int offset = maxHeight - tb.lines.size();
				if (h < offset) {
					row.append(TextUtil.repeatStr(" ", tb.width));
				} else {
					row.append(tb.lines.get(h - offset));
				}
				if (i < subBlocks.length - 1)
					row.append(TextUtil.repeatStr(" ", gap));
			}
			combinedSubs.add(row.toString());
		}

		String barLine = coloredRule + TextUtil.repeatStr(TextUtil.getHLine(), barWidth);
		int paddingLeft = (barWidth - pureConclLen) / 2;
		String conclLine = TextUtil.repeatStr(" ", ruleLen + paddingLeft) + coloredConcl;
		List<String> lines = new ArrayList<>();

		lines.addAll(combinedSubs);
		lines.add(barLine);
		lines.add(conclLine);
		return new TreeBlock(lines, totalWidth, ruleLen + barWidth / 2);
	}

}
