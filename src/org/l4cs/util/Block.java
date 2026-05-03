package org.l4cs.util;

/**
 * Blocks are used to create structured textual output, e.g., for a help message
 * or man page. Text is organized into blocks. Blocks may themselves contain
 * blocks. Hence they are an inductive data structure. When rendered, every
 * block starts on a new line and ends with a newline.
 */
public abstract class Block {

	/**
	 * Number of spaces to indent.
	 */
	public static int tab = 5;

	public abstract StringBuilder render(int indent);

	@Override
	public String toString() {
		return render(0).toString();
	}

	/**
	 * Constructs a paragraph block. The content of the paragraph is obtained from
	 * the given strings, which are concatenated. When rendered, the paragraph is
	 * wrapped to fit within the terminal width, and indented appropriately. A
	 * newline is added at the end if one is not already present.
	 * 
	 * @param strings the content of the paragraph
	 * @return the paragraph block obtained from the given content
	 */
	public static Block par(String... strings) {
		TxtBlock block = new TxtBlock(strings);
		block.setWrap(true);
		return block;
	}

	/**
	 * Constructs a fixed block. This is similar to a paragraph block, except the
	 * text is not wrapped. Still, the newline is added at end if not already
	 * present.
	 * 
	 * @param strings the content of the fixed block
	 * @return the fixed block obtained by concatenation of the given strings
	 */
	public static Block fix(String... strings) {
		TxtBlock block = new TxtBlock(strings);
		block.setWrap(false);
		return block;
	}

	/**
	 * Constructs a sub-block. This is a block consisting of a heading, and a body.
	 * The heading appears on a line by itself. The body follows immediately, but
	 * the entire block is indented one level to the right.
	 */
	public static Block sub(String heading, Block body) {
		SubBlock sub = new SubBlock(heading, body);
		sub.setAlign(false);
		return sub;
	}

	/**
	 * Constructs a sequence block: a block comprising a sequence of blocks,
	 * separated by blank lines.
	 */
	public static Block seq(Block... blocks) {
		return new SeqBlock(blocks);
	}

	/**
	 * Constructs a concatenation block: a block consisting of a sequence of blocks
	 * with no blank lines separating them.
	 * 
	 * @param blocks
	 * @return
	 */
	public static Block cat(Block... blocks) {
		SeqBlock block = new SeqBlock(blocks);
		block.setSeparate(false);
		return block;
	}

	public static Block def(String heading, Block body) {
		SubBlock sub = new SubBlock(heading, body);
		sub.setAlign(true);
		return sub;
	}

}
