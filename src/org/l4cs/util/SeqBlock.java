package org.l4cs.util;

public class SeqBlock extends Block {

	private Block[] members;

	/**
	 * Add a newline between blocks?
	 */
	private boolean separate = true;

	public SeqBlock(Block... members) {
		this.members = members;
	}

	public boolean getSeparate() {
		return separate;
	}

	public void setSeparate(boolean val) {
		this.separate = val;
	}

	@Override
	public StringBuilder render(int level) {
		StringBuilder result = new StringBuilder();
		boolean first = true;
		for (Block member : members) {
			if (separate) {
				if (first)
					first = false;
				else
					result.append("\n");
			}
			result.append(member.render(level));
		}
		return result;
	}

	// characteristics: newline between blocks or not?

}
