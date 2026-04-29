package org.l4cs.util;

import static org.l4cs.util.TextUtil.Hilight.NONE;

import java.io.PrintStream;
import java.util.ArrayList;

public class TextUtil {

	public static final int DEFAULT_WIDTH = 75; // number of columns
	public static final String GAMMA = "\u0393"; // latex \Gamma
	private static final String INFERS = "\u22A2"; // latex \vdash
	public static final String AND = "\u2227"; // latex \wedge
	public static final String OR = "\u2228"; // latex \vee
	public static final String IMPLIES = "\u2192"; // latex \rightarrow
	public static final String NOT = "\u00AC"; // latex \neg
	public static final String TOP = "\u22A4"; // latex \top
	public static final String BOT = "\u22A5"; // latex \bot
	public static final String BAR = "\u007C"; // vertical bar

	public static final String FORALL = "\u2200"; // latex \forall
	public static final String EXISTS = "\u2203"; // latex \exists

	private static final String ANSI_RESET = "\u001B[0m";
//	private static final String ANSI_BOLD = "\u001B[1m";
	private static final String ANSI_BLACK = "\u001B[30m";
	private static final String ANSI_RED = "\u001B[31m";
	private static final String ANSI_GREEN = "\u001B[32m";
//	private static final String ANSI_YELLOW = "\u001B[33m";
	private static final String ANSI_BLUE = "\u001B[34m";
	private static final String ANSI_PURPLE = "\u001B[35m";
	private static final String ANSI_CYAN = "\u001B[36m";
//	private static final String ANSI_WHITE = "\u001B[37m";

	private static String term = System.getenv("TERM");

	/**
	 * How to highlight text using colors, etc.
	 */
	public static enum Hilight {
		/**
		 * Do no highlighting.
		 */
		NONE,
		/**
		 * Use ANSI special characters, supported by most modern terminals.
		 */
		ANSI,
		/**
		 * Use LaTeX xcolor commands, suitable for inserting in a LaTeX document using
		 * the fancyvrb package.
		 */
		TEX
	}

	/**
	 * The kind of highlighting (colors, subscripts, etc.) to use. This variable is
	 * initialized to a default value based on a best guess, but it can be see
	 * explicitly using method {@link #setHighlighting(Hilight)}.
	 */
	private static Hilight highlighting = System.console() != null && !term.equalsIgnoreCase("dumb") ? Hilight.ANSI
			: NONE;

	public static void setHighlighting(Hilight val) {
		highlighting = val;
	}

	public static Hilight highlighting() {
		return highlighting;
	}

	/**
	 * Produces a string representation of a nonnegative integer n as subscripts.
	 * This uses Unicode characters for the subscripted digits.
	 * 
	 * @param n nonnegative integer
	 * @return string consisting of digits of n in subscript position
	 */
	public static String subscript(int n) {
		assert n >= 0;
		if (highlighting != NONE) {
			if (n == 0)
				return "\u2080";
			String result = "";
			while (n > 0) {
				int i = n % 10;
				char c = (char) ('\u2080' + i);
				result = c + result;
				n /= 10;
			}
			return result;
		} else {
			return Integer.toString(n);
		}
	}

	public static String blue() {
		switch (highlighting) {
		case NONE:
			return "";
		case ANSI:
			return ANSI_BLUE;
		case TEX:
			return "\\textcolor{blue}{";
		default:
			throw new RuntimeException("unreachable");
		}
	}

	public static String cyan() {
		switch (highlighting) {
		case NONE:
			return "";
		case ANSI:
			return ANSI_CYAN;
		case TEX:
			return "\\textcolor{cyan}{";
		default:
			throw new RuntimeException("unreachable");
		}
	}

	public static String green() {
		switch (highlighting) {
		case NONE:
			return "";
		case ANSI:
			return ANSI_GREEN;
		case TEX:
			return "\\textcolor{green}{";
		default:
			throw new RuntimeException("unreachable");
		}
	}

	public static String purple() {
		switch (highlighting) {
		case NONE:
			return "";
		case ANSI:
			return ANSI_PURPLE;
		case TEX:
			return "\\textcolor{purple}{";
		default:
			throw new RuntimeException("unreachable");
		}
	}

	public static String red() {
		switch (highlighting) {
		case NONE:
			return "";
		case ANSI:
			return ANSI_RED;
		case TEX:
			return "\\textcolor{red}{";
		default:
			throw new RuntimeException("unreachable");
		}
	}

	public static String black() {
		switch (highlighting) {
		case NONE:
			return "";
		case ANSI:
			return ANSI_BLACK;
		case TEX:
			return "\\textcolor{black}{";
		default:
			throw new RuntimeException("unreachable");
		}
	}

	public static String ruleColor() {
		return red();
	}

	public static String numberColor() {
		return blue();
	}

	public static String infersColor() {
		return black();
	}

	public static String infers() {
		if (highlighting == NONE) {
			return "|-";
		} else {
			// return infersColor() + INFERS + reset();
			return INFERS;
		}
	}

	public static String reset() {
		switch (highlighting) {
		case NONE:
			return "";
		case ANSI:
			return ANSI_RESET;
		case TEX:
			return "}";
		default:
			throw new RuntimeException("unreachable");
		}
	}

	private static void space(PrintStream out, int n) {
		for (int i = 0; i < n; i++)
			out.print(" ");
	}

	public static void printFrac(PrintStream out, int indent, String s1, String s2) {
		int n1 = s1.length(), n2 = s2.length();
		int n = n1 >= n2 ? n1 : n2;
		space(out, indent);
		space(out, (n - n1) / 2);
		out.println(s1);
		space(out, indent);
		for (int i = 0; i < n; i++)
			// replaced out.print("-") with getHLine() to support solid lines
			out.print(getHLine());
		out.println();
		space(out, indent);
		space(out, (n - n2) / 2);
		out.println(s2);
	}

	/**
	 * Tries to print a string over multiple lines, wrapping to keep each line at
	 * most maxWidth columns. If it is not possible to keep a line within that
	 * number of columns (because there is a sequence of non-whitespace characters
	 * greater than maxWidth) then a line may exceed maxWidth.
	 * 
	 * @param out      string buffer to which to print
	 * @param s        the string to print
	 * @param maxWidth maximum length of a line, if possible
	 */
	public static StringBuffer fill(StringBuffer in, int maxWidth) {
		StringBuffer out = new StringBuffer();
		int len = in.length();
		int idx = 0; // next index to be printed
		int ws1 = -1; // index of last white space character after idx
		for (int i = 0; i < len; i++) {
			if (i - idx > maxWidth && ws1 > idx) { // print idx..ws1-1
				while (idx < ws1)
					out.append(in.charAt(idx++));
				out.append('\n'); // print newline instead of white space
				idx++;
				ws1 = -1; // have not seen white space since last print
			}
			char c = in.charAt(i);
			if (c == ' ' || c == '\t' || c == '\n')
				ws1 = i;
		}
		if (idx < len) { // print remainder...
			if (len - idx <= maxWidth || ws1 <= idx) {
				while (idx < len)
					out.append(in.charAt(idx++));
				out.append('\n');
			} else {
				while (idx < ws1)
					out.append(in.charAt(idx++));
				out.append('\n');
				idx++;
				while (idx < len)
					out.append(in.charAt(idx++));
				out.append('\n');
			}
		}
		return out;
	}

	public static StringBuffer fill(String s, int maxWidth) {
		return fill(new StringBuffer(s), maxWidth);
	}

	/** True length of a string, ignoring the ANSI characters. */
	public static int length(String string) {
		int result = string.length();
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			if (c == '\u001B') {
				int skip = string.charAt(i + 2) == '0' ? 3 : 4;
				i += skip;
				result -= (skip + 1);
			} else if (c == '\\') {
				// latex command of form \textcolor{...}{
				// consume until "}{"
				int latexStop = string.indexOf("}{", i);
				assert latexStop > 0;
				int newPos = latexStop + 2;
				result -= newPos - i;
				i = newPos;
			} else if (c == '}') {
				result--;
			}
		}
		return result;
	}

	/**
	 * Prints a table. A table is represented as a sequence of n rows. Each row is a
	 * sequence of m columns. The number of columns is the same for every row. An
	 * entry in a table is a string. Each column can be either left- right- or
	 * center-aligned. The syntax for specifying this is the same as latex, i.e.,
	 * format is a string of length m; each character in this string is either 'l',
	 * 'r', or 'c'.
	 * 
	 * @param out    where to print
	 * @param table  the table
	 * @param format the format string
	 */
	public static void printTable(PrintStream out, ArrayList<String[]> table, String format) {
		int m = format.length();
		int maxWidths[] = new int[m];
		for (int i = 0; i < m; i++)
			maxWidths[i] = 0;
		for (String[] row : table) {
			for (int i = 0; i < m; i++)
				if (length(row[i]) > maxWidths[i])
					maxWidths[i] = length(row[i]);
		}
		for (String[] row : table) {
			for (int i = 0; i < m; i++) {
				int len = length(row[i]);
				int s1 = 0, s2 = 0;
				switch (format.charAt(i)) {
				case 'l':
					s2 = maxWidths[i] - len;
					break;
				case 'r':
					s1 = maxWidths[i] - len;
					break;
				case 'c':
					s1 = (maxWidths[i] - len) / 2;
					s2 = maxWidths[i] - s1 - len;
					break;
				default:
					throw new IllegalArgumentException("Format character must be l, r, or c: " + format.charAt(i));
				}
				space(out, s1);
				out.print(row[i]);
				space(out, s2);
				if (i < m - 1)
					out.print(" ");
			}
			out.println();
		}
	}

	public static String repeatStr(String s, int n) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < n; i++)
			sb.append(s);
		return sb.toString();
	}

	public static String getCircled(int n) {
		if (highlighting == NONE)
			return "(" + n + ")";
		if (n >= 1 && n <= 20)
			return String.valueOf((char) ('\u245F' + n));
		if (n >= 21 && n <= 35)
			return String.valueOf((char) ('\u3251' + n - 21));
		if (n >= 36 && n <= 50)
			return String.valueOf((char) ('\u32B1' + n - 36));
		return "(" + n + ")";
	}

	public static String getVLine() {
		return highlighting != NONE ? "\u2502 " : "| ";
//		return highlighting == ANSI ? "\u2502 " : "| ";
	}

	public static String getHLine() {
		return highlighting != NONE ? "\u2500" : "-";
	}

	public static String getTBranch() {
		return highlighting != NONE ? "\u251C\u2500\u2500\u2500" : "|---";
//		return highlighting == ANSI ? "\u251C\u2500\u2500\u2500" : "|---";
	}

}
