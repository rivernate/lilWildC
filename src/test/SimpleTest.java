package test;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTreeNodeStream;

import test.lilWildCParser.program_return;

public class SimpleTest {
	public static void main(String[] args) throws RecognitionException{
		CharStream stream =	new ANTLRStringStream("" +
				"procedure main\r\n" + 
				"{\r\n" + 
				"\r\n" + 
				"	number num2sqrt;\r\n" + 
				"	number sqrt;\r\n" + 
				"	number lastSqrt;\r\n" + 
				"\r\n" + 
				"	print \"Enter the number you want to generate the square root of: \";\r\n" + 
				"	input num2sqrt;\r\n" + 
				"\r\n" + 
				"	sqrt = num2sqrt / 2.0;\r\n" + 
				" 	lastSqrt = 0.0; \r\n" + 
				" 	while ( sqrt != lastSqrt )  \r\n" + 
				"	{ \r\n" + 
				"		lastSqrt = sqrt;\r\n" + 
				"		sqrt = ( sqrt + ( num2sqrt / sqrt )) / 2.0; \r\n" + 
				" 	}\r\n" + 
				"\r\n" + 
				" 	print \"The square root of: \", num2sqrt, \" is \", sqrt, \"\\n\";\r\n" + 
				"	return;\r\n" + 
				"\r\n" + 
				"}"
				/*"number var1;\r\n" + 
				"number[5] var2;\r\n" + 
				"\r\n" + 
				"procedure main\r\n" + 
				"{\r\n" + 
				"\r\n" + 
				"number var3;" + 
				"number var4;" +
				"var3 = 0;" +
				"var4 = 5;" +
				//"var4[1] = 3;" +
				//"var4[0] = var4[1] -1;" +
				"while( var3 < var4){" + 
				"var3 = var3 + 1;" + 
				"print \"var3 = \", var3, \" \";" + 
				"}" +
				//"var4 = 3;" +
				//"var3 = 42 * (3+(4*1));" +
				//"var3 = var3 + 5;" +
				//"var3 = 42 / 5;" +
				//"var3 = 42 - 1;" +
				//"" +
				//"print var4[0];" +
				//"print 3+4;" +
				//"print var3;" +
				//"print \"New input for var1?\", var3;" +
				//"input var3;" +
				//"print \"This is the answer \", 3+4, \"test\";" +
				"return;\r\n" + 
				"}"*/
				);
			lilWildCLexer lexer = new lilWildCLexer(stream);
			TokenStream tokenStream = new CommonTokenStream(lexer);
			lilWildCParser parser = new lilWildCParser(tokenStream);
			program_return evaluator = parser.program();
			System.out.println(evaluator.tree.toStringTree());
			CommonTreeNodeStream nodeStream = new CommonTreeNodeStream(evaluator.tree);
			EvaluatorWalker walker = new EvaluatorWalker(nodeStream);
			String result = walker.evaluator();
			System.out.println(result);
	}
}

