package test.others;

import test.Utils;


public class TestSearchGen extends TestSearch {
	
	public static void main(String[] args) throws Exception
	{
		TestSearch test = new TestSearch();
		int n = 50, y = 10;
		GenRunnable gen = test.new GenRunnable(n, y);
		gen.run();
		System.out.println(Utils.toInt(gen.z));
		//next line assume i have value of x, which is in fact cannot be true
		System.out.println(TestSearch.search(n, y, 5));
	}
}