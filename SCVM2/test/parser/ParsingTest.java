package parser;

import java.io.FileInputStream;

public class ParsingTest {
	
	public static void main(String[] args) throws Exception {
//		System.out.println(Integer.decode("0xffff0000"));
		CParser parser = new CParser(new FileInputStream("input3/leadingZero.lcc"));
		System.out.println(parser.TranslationUnit().toString());
		
	}

}
