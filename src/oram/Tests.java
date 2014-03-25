package oram;

import static org.junit.Assert.*;

import oram.PathORamBasic.Utils;

import org.junit.Test;

public class Tests {

	@Test
	public void testRefineKyes() {
		int[] keys = new int[]{1, 1, 2, 2, 2, 2, 2, 3, 4, 4};
		PathORamBasic.Utils.refineKeys(keys, 10, 4);
		assertArrayEquals(new int[]{1, 2, 5, 6, 7, 8, 9, 10, 13, 14}, keys);
		
		keys = new int[]{1, 1, 2, 2, 2, 2, 2, 3, 4, 4};
		PathORamBasic.Utils.refineKeys(keys, 10, 3);
		assertArrayEquals(new int[]{1, 2, 4, 5, 6, 7, 8, 9, 10, 11}, keys);
		
		keys = new int[]{1, 1, 2, 2, 2, 2, 2, 3, 4, 4};
		PathORamBasic.Utils.refineKeys(keys, 10, 5);
		assertArrayEquals(new int[]{1, 2, 6, 7, 8, 9, 10, 11, 16, 17}, keys);
	}
}
