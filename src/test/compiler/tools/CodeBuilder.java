package test.compiler.tools;

public class CodeBuilder {
	private StringBuilder sb;
	private final static String newLine = System.getProperty("line.separator");
	
	public CodeBuilder(){
		sb = new StringBuilder();
	}
	
	public void append(String string){
		sb.append(string);
	}
	
	public void appendLine(String string){
		sb.append(newLine);
		sb.append(string);
	}
	
	@Override
	public String toString(){
		return sb.toString();
	}
}
