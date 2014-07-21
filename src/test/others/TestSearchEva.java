package test.others;


public class TestSearchEva extends TestSearch {
	
	public static void main(String[] args) throws Exception
	{
		TestSearch test = new TestSearch();
		int n = 50, x = 5;
		EvaRunnable eva = test.new EvaRunnable(n, x);
		eva.run();
	}
}