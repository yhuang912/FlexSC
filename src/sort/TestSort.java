package sort;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

public class TestSort {

	@Test
	public void testBitonicSorter() {
		Random rnd = new Random();
		
		int len = 1024;
		int[] nums = new int[len];
		for(int i = 0; i < len; i++)
			nums[i] = rnd.nextInt();
		
		int[] expected = Arrays.copyOf(nums, len);
		Assert.assertArrayEquals(expected, nums);
		
		Arrays.sort(expected);
//		System.out.println("expected:" + Arrays.toString(expected));
		
//		System.out.println(Arrays.toString(nums));
		
		Sorter s = new BitonicSorter();
		s.sort(nums);
//		System.out.println(Arrays.toString(nums));
		
		Assert.assertArrayEquals(expected, nums);
		
	}

	@Test
	public void testBitonicSorterArbitraryN() {
		Random rnd = new Random();
		
		int len = 10;
		int[] nums = new int[len];
		for(int i = 0; i < len; i++)
			nums[i] = rnd.nextInt();
		
		int[] expected = Arrays.copyOf(nums, len);
		Assert.assertArrayEquals(expected, nums);
		
		Arrays.sort(expected);
		System.out.println("expected:" + Arrays.toString(expected));
		
		System.out.println(Arrays.toString(nums));
		
		Sorter s = new BitonicSorterForArbitraryN();
		s.sort(nums);
		System.out.println(Arrays.toString(nums));
		
		Assert.assertArrayEquals(expected, nums);
		
	}
}
