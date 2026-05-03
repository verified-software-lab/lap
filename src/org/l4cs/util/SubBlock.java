package org.l4cs.util;

public class SubBlock extends Block {

	private String heading;

	private Block body;

	/**
	 * With this option true, the heading and first line of body will appear on the
	 * same line, and body will continue in the block indented by include heading.
	 */
	private boolean align = false;

	public SubBlock(String heading, Block body) {
		this.heading = heading;
		this.body = body;
	}

	public void setAlign(boolean val) {
		this.align = val;
	}

	public boolean getAlign() {
		return align;
	}

	@Override
	public StringBuilder render(int indent) {
		if (align) {
			int len = TextUtil.visibleLength(heading), newIndent = indent + len;
			StringBuilder newBody = body.render(newIndent);
			newBody.replace(indent, newIndent, heading);
			return newBody;
		} else {
			StringBuilder result = new StringBuilder(" ".repeat(indent));
			result.append(heading);
			result.append("\n");
			result.append(body.render(indent + Block.tab));
			return result;
		}
	}
}
