package org.l4cs.util;

public class TxtBlock extends Block {

	private StringBuilder body;

	private boolean wrap = true;

	public TxtBlock(StringBuilder body) {
		this.body = body;
	}

	public TxtBlock(String... strings) {
		body = new StringBuilder();
		for (String str : strings) {
			body.append(str);
		}
	}

	public void setWrap(boolean val) {
		this.wrap = val;
	}

	public boolean getWrap() {
		return wrap;
	}

	@Override
	public StringBuilder render(int indent) {
		if (wrap) {
			return TextUtil.wrap(indent, body);
		} else {
			// every line should be indented
			StringBuilder result = new StringBuilder();
			int n = body.length();
			boolean newline = true;
			for (int i = 0; i < n; i++) {
				char c = body.charAt(i);
				if (newline) {
					result.append(" ".repeat(indent));
					newline = false;
				}
				result.append(c);
				if (c == '\n')
					newline = true;
			}
			if (!newline)
				result.append('\n');
			return result;
		}
	}

}
