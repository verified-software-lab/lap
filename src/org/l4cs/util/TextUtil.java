package org.l4cs.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class TextUtil {

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

	// ANSI Escape Sequences...
	private static final String ANSI_RESET = "\u001B[0m";
	private static final String ANSI_BOLD_ON = "\u001B[1m";
	private static final String ANSI_BOLD_OFF = "\u001B[22m";
	private static final String ANSI_UNDERLINE_ON = "\u001B[4m";
	private static final String ANSI_UNDERLINE_OFF = "\u001B[24m";
	private static final String ANSI_BLACK_ON = "\u001B[30m";
	private static final String ANSI_RED_ON = "\u001B[31m";
	private static final String ANSI_GREEN_ON = "\u001B[32m";
	private static final String ANSI_BLUE_ON = "\u001B[34m";
	private static final String ANSI_PURPLE_ON = "\u001B[35m";
	private static final String ANSI_CYAN_ON = "\u001B[36m";
	private static final String ANSI_COLOR_OFF = "\u001B[39m";

	/**
	 * Character encoding to use for output.
	 */
	public static enum Encoding {
		/**
		 * ASCII text only.
		 */
		ASCII,

		/**
		 * Unicode characters using UTF-8.
		 */
		UNICODE
	}

	/**
	 * How to highlight text using colors, etc.
	 */
	public static enum Highlight {
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
	 * Width of the terminal (in characters).
	 */
	private static int terminalWidth;

	/**
	 * How characters are encoded for the output. A default value is set in the
	 * static initializer but this can be changed using
	 * {@link #setEncoding(Encoding)}.
	 */
	private static Encoding encoding;

	/**
	 * The kind of highlighting (for colors, subscripts, etc.) to use. This variable
	 * is initialized to a default value based on a best guess, but it can be set
	 * explicitly using method {@link #setHighlighting(Highlight)}.
	 */
	private static Highlight highlighting;

	static {
		String lang = System.getenv("LANG");
		boolean supportsUnicode = lang != null && lang.toUpperCase().contains("UTF-8");
		String term = System.getenv("TERM");
		boolean supportsAnsi = term != null
				&& (term.contains("color") || term.contains("xterm") || term.contains("ansi"));
		encoding = supportsUnicode ? Encoding.UNICODE : Encoding.ASCII;
		highlighting = supportsAnsi ? Highlight.ANSI : Highlight.NONE;
		// try to get the Terminal width...
		ProcessBuilder pb = new ProcessBuilder("sh", "-c", "tput cols < /dev/tty");
		try {
			Process p = pb.start();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
				String line = reader.readLine();
				if (line != null)
					terminalWidth = Integer.parseInt(line.trim());
			}
			p.waitFor();
		} catch (Exception e) {
			terminalWidth = 80;
		}
	}

	public static int terminalWidth() {
		return terminalWidth;
	}

	public static void setEncoding(Encoding enc) {
		encoding = enc;
	}

	public static Encoding encoding() {
		return encoding;
	}

	public static void setHighlighting(Highlight hl) {
		highlighting = hl;
	}

	public static Highlight highlighting() {
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
		if (highlighting == Highlight.ANSI) {
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

	/**
	 * Returns the string which causes the output device to start using the color
	 * blue. This must later be "closed" by outputting the string returned by
	 * {@link #colorOff()}.
	 * 
	 * @return the string which starts a scope in which color will be blue
	 */
	public static String blueOn() {
		switch (highlighting) {
		case NONE:
			return "";
		case ANSI:
			return ANSI_BLUE_ON;
		case TEX:
			return "\\textcolor{blue}{";
		default:
			throw new RuntimeException("unreachable");
		}
	}

	/**
	 * Returns the string which causes the output device to start using the color
	 * cyan. This must later be "closed" by outputting the string returned by
	 * {@link #colorOff()}.
	 * 
	 * @return the string which starts a scope in which color will be cyan
	 */
	public static String cyanOn() {
		switch (highlighting) {
		case NONE:
			return "";
		case ANSI:
			return ANSI_CYAN_ON;
		case TEX:
			return "\\textcolor{cyan}{";
		default:
			throw new RuntimeException("unreachable");
		}
	}

	/**
	 * Returns the string which causes the output device to start using the color
	 * green. This must later be "closed" by outputting the string returned by
	 * {@link #colorOff()}.
	 * 
	 * @return the string which starts a scope in which color will be green
	 */
	public static String greenOn() {
		switch (highlighting) {
		case NONE:
			return "";
		case ANSI:
			return ANSI_GREEN_ON;
		case TEX:
			return "\\textcolor{green}{";
		default:
			throw new RuntimeException("unreachable");
		}
	}

	/**
	 * Returns the string which causes the output device to start using the color
	 * purple. This must later be "closed" by outputting the string returned by
	 * {@link #colorOff()}.
	 * 
	 * @return the string which starts a scope in which color will be purple
	 */
	public static String purpleOn() {
		switch (highlighting) {
		case NONE:
			return "";
		case ANSI:
			return ANSI_PURPLE_ON;
		case TEX:
			return "\\textcolor{purple}{";
		default:
			throw new RuntimeException("unreachable");
		}
	}

	/**
	 * Returns the string which causes the output device to start using the color
	 * red. This must later be "closed" by outputting the string returned by
	 * {@link #colorOff()}.
	 * 
	 * @return the string which starts a scope in which color will be red
	 */
	public static String redOn() {
		switch (highlighting) {
		case NONE:
			return "";
		case ANSI:
			return ANSI_RED_ON;
		case TEX:
			return "\\textcolor{red}{";
		default:
			throw new RuntimeException("unreachable");
		}
	}

	/**
	 * Returns the string which causes the output device to start using the color
	 * black. This must later be "closed" by outputting the string returned by
	 * {@link #colorOff()}.
	 * 
	 * @return the string which starts a scope in which color will be black
	 */
	public static String blackOn() {
		switch (highlighting) {
		case NONE:
			return "";
		case ANSI:
			return ANSI_BLACK_ON;
		case TEX:
			return "\\textcolor{black}{";
		default:
			throw new RuntimeException("unreachable");
		}
	}

	/**
	 * Returns the string which causes the output device to start underlining. This
	 * must later be "closed" by outputting the string returned by
	 * {@link #underlineOff()}.
	 * 
	 * @return the string which starts a scope in which underlining is on
	 */
	public static String underlineOn() {
		switch (highlighting) {
		case NONE:
			return "";
		case ANSI:
			return ANSI_UNDERLINE_ON;
		case TEX:
			return "\\underline{";
		default:
			throw new RuntimeException("unreachable");
		}
	}

	/**
	 * Returns the string which causes the output device to stop underlining. This
	 * closes the scope started by {@link #underlineOn().
	 * 
	 * @return the string which closes the underlining scope
	 */
	public static String underlineOff() {
		switch (highlighting) {
		case NONE:
			return "";
		case ANSI:
			return ANSI_UNDERLINE_OFF;
		case TEX:
			return "}";
		default:
			throw new RuntimeException("unreachable");
		}
	}

	/**
	 * Underlines the string.
	 * 
	 * @param str the string to be underlined
	 * @return the string, underlined
	 */
	public static String underline(String str) {
		switch (highlighting) {
		case NONE:
			return str;
		case ANSI:
			return ANSI_UNDERLINE_ON + str + ANSI_UNDERLINE_OFF;
		case TEX:
			return "\\underline{" + str + "}";
		default:
			throw new RuntimeException("unreachable");
		}
	}

	public static String boldOn() {
		switch (highlighting) {
		case NONE:
			return "";
		case ANSI:
			return ANSI_BOLD_ON;
		case TEX:
			return "\\textbf{";
		default:
			throw new RuntimeException("unreachable");
		}
	}

	public static String boldOff() {
		switch (highlighting) {
		case NONE:
			return "";
		case ANSI:
			return ANSI_BOLD_OFF;
		case TEX:
			return "}";
		default:
			throw new RuntimeException("unreachable");
		}
	}

	public static String bold(String str) {
		switch (highlighting) {
		case NONE:
			return str;
		case ANSI:
			return ANSI_BOLD_ON + str + ANSI_BOLD_OFF;
		case TEX:
			return "\\textbf{" + str + "}";
		default:
			throw new RuntimeException("unreachable");
		}
	}

	public static String ruleColorOn() {
		return redOn();
	}

	public static String numberColorOn() {
		return blueOn();
	}

	public static String infers() {
		switch (encoding) {
		case ASCII:
			return "|-";
		case UNICODE:
			return INFERS;
		default:
			throw new RuntimeException("unreachable");
		}
	}

	/**
	 * Stop using the current color. This is used to close a scope started by the
	 * previous "color on" command.
	 * 
	 * @return the string to turn off color
	 */
	public static String colorOff() {
		switch (highlighting) {
		case NONE:
			return "";
		case ANSI:
			return ANSI_COLOR_OFF;
		case TEX:
			return "}";
		default:
			throw new RuntimeException("unreachable");
		}
	}

	/**
	 * Turns off all highlighting, returns to default color, etc. Do not use this to
	 * close a scoped instruction.
	 * 
	 * @return the string to do a reset
	 */
	public static String reset() {
		switch (highlighting) {
		case NONE:
			return "";
		case ANSI:
			return ANSI_RESET;
		case TEX:
			return "";
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
	 * Wraps text to fit within a region. This method consumes a StringBuilder
	 * {@code in} containing the original text, and produces a new StringBuilder
	 * {@code out}, without modifying {@code in}. Within {@code out}, each line will
	 * start with {@code indent} spaces and the total length of the line will be at
	 * most {@code maxWidth} characters, with one exception described below. A
	 * newline in {@code out} can only occur where there was some form of white
	 * space (a sequence of tabs, spaces, and/or newlines) in the original text.
	 * Finally, {@code out} will always end with a newline: if a terminal newline
	 * was not present in the original text, one will be added.
	 * 
	 * <p>
	 * Exception: When it is impossible to meet these criteria, because {@code in}
	 * contains a long sequence of characters with no white space, then {@code out}
	 * may contain a line longer than {@code maxWidth}.
	 * </p>
	 * 
	 * 
	 */
	static StringBuilder wrap(StringBuilder in, int indent, int maxWidth) {
		StringBuilder out = new StringBuilder();

		// Standard Java regex for ANSI escape sequences
		Pattern ansiPattern = Pattern.compile("\\u001B\\[[0-9;]*[mK]");

		// Instead of trim(), we use a regex split that preserves the Escape characters
		// but still breaks the input into words based on whitespace.
		// We filter out empty strings in case of multiple spaces.
		String[] words = in.toString().split("\\s+");

		String indentation = " ".repeat(indent);
		StringBuilder currentLine = new StringBuilder();
		currentLine.append(indentation);
		int currentVisibleLength = indent;

		boolean firstWordProcessed = false;

		for (String word : words) {
			if (word.isEmpty())
				continue;

			int wordVisibleLength = ansiPattern.matcher(word).replaceAll("").length();

			// Check if this is the very first word of the entire input
			if (!firstWordProcessed) {
				currentLine.append(word);
				currentVisibleLength += wordVisibleLength;
				firstWordProcessed = true;
				continue;
			}

			// Logic for subsequent words
			if (currentVisibleLength + 1 + wordVisibleLength > maxWidth) {
				out.append(currentLine).append("\n");
				currentLine = new StringBuilder(indentation);
				currentVisibleLength = indent;
				currentLine.append(word);
				currentVisibleLength += wordVisibleLength;
			} else {
				currentLine.append(" ");
				currentLine.append(word);
				currentVisibleLength += 1 + wordVisibleLength;
			}
		}

		// Always ensure we append the last line and a terminal newline
		if (firstWordProcessed) {
			out.append(currentLine).append("\n");
		} else {
			// Handle case where input was only whitespace
			out.append(indentation).append("\n");
		}

		return out;
	}

	public static StringBuilder wrap(int indent, StringBuilder in) {
		return wrap(in, indent, terminalWidth);
	}

	public static StringBuilder wrap(int indent, String s) {
		return wrap(new StringBuilder(s), indent, terminalWidth);
	}

	public static StringBuilder wrap(StringBuilder in) {
		return wrap(in, 0, terminalWidth);
	}

	public static StringBuilder wrap(String s) {
		return wrap(new StringBuilder(s), 0, terminalWidth);
	}

	/**
	 * True length of a string, ignoring the ANSI characters and LaTeX color macros
	 * used for highlighting. This will work for encoding ASCII and UNICODE, but not
	 * yet for the TEX encoding. This will work
	 */
	public static int length(String string) {
		int result = string.length();
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			if (c == '\u001B') { // ANSI escape character
				// proceed just past first "m"...
				int newPos = string.indexOf("m", i);
				assert newPos > i;
				result -= newPos + 1 - i;
				i = newPos; // i will be incremented before next iteration
			} else if (c == '\\') {
				int latexStop;
				if (string.startsWith("underline{", i + 1) || string.startsWith("textbf{", i + 1)) {
					// single-argument latex macro
					latexStop = string.indexOf("{", i + 1);
				} else {
					// 2-argument latex macro, e.g., \textcolor{blue}{blah}
					latexStop = string.indexOf("}{", i + 1);
				}
				assert latexStop > i + 1;
				int newPos = latexStop + 1;
				result -= newPos + 1 - i;
				i = newPos; // i will be incremented before next iteration
			} else if (c == '}') { // end of latex macro
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
		if (encoding != Encoding.UNICODE)
			return "(" + n + ")";
		if (n >= 1 && n <= 20) // 2460-2473
			return String.valueOf((char) ('\u245F' + n));
		if (n >= 21 && n <= 35) // 3251-325F
			return String.valueOf((char) ('\u3251' + n - 21));
		if (n >= 36 && n <= 50) // 32B1-32BF
			return String.valueOf((char) ('\u32B1' + n - 36));
		return "(" + n + ")";
	}

	public static String getVLine() {
		return encoding == Encoding.UNICODE ? "\u2502 " : "| ";
	}

	public static String getHLine() {
		return encoding == Encoding.UNICODE ? "\u2500" : "-";
	}

	public static String getTBranch() {
		return encoding == Encoding.UNICODE ? "\u251C\u2500\u2500\u2500" : "|---";
	}

}
