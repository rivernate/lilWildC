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
				"number n;" + 
				"number[1001] isPrime;" + 
				"procedure main" + 
				"{" + 
				"	number i;" + 
				"	print \"Enter the number to which you want to generate prime numbers (2<num<1000): \";" + 
				"	input n;" + 
				"	if ( n <= 1 || n > 1000 )" + 
				"	{" + 
				"		print \"\\nERROR: Number '\", n, \"' out of range!\\n\";" + 
				"		return;" + 
				"	}" + 
				"	" + 
				"	i = 2;" + 
				"	while ( i <= n )" + 
				"	{" + 
				"		isPrime[i] = 1;" + 
				"		i = i + 1;" + 
				"	}" + 
				"	call sieveOfErathosthenes;" + 
				"	print \"\\nPrime numbers from 2 to \", n, \":\\n\";" + 
				"	i = 2;" + 
				"	while( i <= n )" + 
				"	{" + 
				"		if ( isPrime[i] == 1 )" + 
				"		{" + 
				"			print \"  \", i;" + 
				"		}" + 
				"		i = i + 1;" + 
				"	}" + 
				"	print \"\\n\";" + 
				"	return;" + 
				"	" + 
				"}" + 
				"procedure sieveOfErathosthenes" + 
				"{" + 
				"	number i;" + 
				"	number j;" + 
				"	i = 2;" + 
				"	while ( i * i <= n )" + 
				"	{" + 
				"		if ( isPrime[i] == 1 )" + 
				"		{" + 
				"			j = i;" + 
				"			while ( i * j <= n )" + 
				"			{" + 
				"				isPrime[i*j] = 0;" + 
				"				j = j + 1;" + 
				"			}" + 
				"		}" + 
				"		i = i + 1;" + 
				"	}" + 
				"	" + 
				"	return;" + 
				"}"+
				"");
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

