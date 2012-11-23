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
				"number var1;\r\n" + 
				"number[5] var2;\r\n" + 
				"\r\n" + 
				"procedure main\r\n" + 
				"{\r\n" + 
				"\r\n" + 
				"number var4;" +
				"input var3;" +
				"if (var3 < 5){" +
				"print \"less than 5\";" +
				"}" +
				"else {" +
				"print \"greater than or equal to 5\";" +
				"}" +
				"return;\r\n" + 
				"}");
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

