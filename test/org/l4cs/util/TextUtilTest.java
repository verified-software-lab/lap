package org.l4cs.util;

import java.io.PrintStream;

import org.junit.Test;

public class TextUtilTest {

	private static PrintStream out = System.out;

	@Test
	public void test() {
		StringBuilder in = new StringBuilder("abc defg hijkl.  mnopqr stuvwxyz.");
		for (int i = 1; i <= in.length(); i++) {
			out.println("--------------- wrap=" + i + " ---------------");
			StringBuilder result = TextUtil.wrap(in, 0, i);
			out.println(result);
			out.println();
		}
	}

	@Test
	public void subscript() {
		out.println("x" + TextUtil.subscript(5390));
	}

}
