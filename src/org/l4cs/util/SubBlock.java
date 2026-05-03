package org.l4cs.util;

public class SubBlock extends Block {

	private String heading;

	private Block body;

	public SubBlock(String heading, Block body) {
		this.heading = heading;
		this.body = body;
	}

	@Override
	public StringBuilder render(int level) {
		StringBuilder result = new StringBuilder(TextUtil.repeatStr(" ", level * Block.tab));
		result.append(heading);
		result.append("\n");
		result.append(body.render(level + 1));
		return result;
	}

}
