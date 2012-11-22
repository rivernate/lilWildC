package test;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;

import test.lilWildCParser.program_return;

public class FullTest {
	
	public static void main(String[] args) throws RecognitionException{
		CharStream stream =	new ANTLRStringStream("\r\n" + 
				"number var1;\r\n" + 
				"number[5] var2;\r\n" + 
				"\r\n" + 
				"procedure main\r\n" + 
				"{\r\n" + 
				"\r\n" + 
				"	number var3;\r\n" + 
				"	number[10] var4;\r\n" + 
				"\r\n" + 
				"	var1 = 42.0 * 5;\r\n" + 
				"	var3 = var1;\r\n" + 
				"	var4[ var1 * 2 - 3] = -5 * -5.5 / -.123;\r\n" + 
				"\r\n" + 
				"	var2[var1] = var3;\r\n" + 
				"	var4[2] = var2[var1];\r\n" + 
				"\r\n" + 
				"	if ( var1 >= var4[ var3] )\r\n" + 
				"	{\r\n" + 
				"		print \"This is the answer \", var2[var1], \"\\n\\n\";\r\n" + 
				"	}\r\n" + 
				"\r\n" + 
				"	print \"New input for var1?\";\r\n" + 
				"	input var1;\r\n" + 
				"\r\n" + 
				"	call proc2;\r\n" + 
				"	return;\r\n" + 
				"}\r\n" + 
				"\r\n" + 
				"procedure proc2\r\n" + 
				"{\r\n" + 
				"	number var5;\r\n" + 
				"\r\n" + 
				"	var5 = 123.456;\r\n" + 
				"\r\n" + 
				"	if ( var5 > var1 )\r\n" + 
				"	{\r\n" + 
				"		var1 = -var5;\r\n" + 
				"	}\r\n" + 
				"	else\r\n" + 
				"	{\r\n" + 
				"		var5 = -var1;\r\n" + 
				"	}\r\n" + 
				"\r\n" + 
				"	while( var1 < var5)\r\n" + 
				"	{\r\n" + 
				"		var1 = var1 + 1;\r\n" + 
				"		print \"var1 = \", var1, \"\\n\";\r\n" + 
				"	}\r\n" + 
				"\r\n" + 
				"	return;\r\n" + 
				"\r\n" + 
				"}\r\n" + 
				"");
			lilWildCLexer lexer = new lilWildCLexer(stream);
			TokenStream tokenStream = new CommonTokenStream(lexer);
			lilWildCParser parser = new lilWildCParser(tokenStream);
			program_return evaluator = parser.program();
			System.out.println(evaluator.tree.toStringTree());
	}

}
