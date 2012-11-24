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
				"var1 = 3;" +
				"var2[0] = 4;" +
				"call second;" + 
				"return;\r\n" + 
				"}" +
				"procedure second" +
				"{" +
				"print var1;" +
				"print var2[0];" +
				"return;" +
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

