package test.compiler.tools;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CilGenerator {
	private final static String newLine = System.getProperty("line.separator");
	private final String INIT_CODE;
	private final String END_CODE;
	private StringBuilder cBuilder;
	private StringBuilder lBuilder;
	private StringBuilder laBuilder;
	private boolean hasLocalVars;
	private Set<String> globalVars;
	private Map<String,Integer> globalVarArray;
	

	public CilGenerator() {
		INIT_CODE = String
				.format(".assembly extern mscorlib {auto}%s.assembly lilWildc { }%s.class public auto ansi lilWildc extends [mscorlib]System.Object {",
						newLine, newLine);
		END_CODE = String.format("}");
		cBuilder = new StringBuilder(
				".method private static  specialname  rtspecialname default void .cctor ()  cil managed {");
		lBuilder = new StringBuilder(".locals init (");
		laBuilder = new StringBuilder();
		hasLocalVars = false;
		globalVars = new HashSet<String>();
		globalVarArray = new HashMap<String, Integer>();
	}

	public String getInitCode() {
		return INIT_CODE;
	}

	public String getEndCode() {
		return END_CODE;
	}

	public String genConstructor() {
		cBuilder.append(String.format("%s}", newLine));
		return this.cBuilder.toString();
	}

	public String genGlobalVar(String var) {
		globalVars.add(var);
		return String.format(".field public static float32 %s", var);
	}

	public String genGlobalVarArray(String var, String size) {
		globalVarArray.put(var, Integer.parseInt(size));
		cBuilder.append(String.format("%sldc.i4 %s", newLine, size));
		cBuilder.append(String.format("%snewarr float32", newLine));
		cBuilder.append(String.format("%sstsfld float32[] lilWildc::%s", newLine,
				var));
		return String.format(".field public static float32[] %s", var);
	}

	public String genProcedureStart(String name) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format(".method static public void %s() il managed",
				name));
		sb.append(newLine);
		sb.append("{");
		sb.append(newLine);
		if (name.equals("main"))
			sb.append(".entrypoint");
		sb.append(newLine);
		sb.append(".maxstack 8");
		return sb.toString();
	}

	public void addLocalVar(String id) {
		if(!hasLocalVars){
			lBuilder.append(String.format("float32 %s ", id));
			hasLocalVars = true;
		}
		else{
			lBuilder.append(String.format(", float32 %s ", id));
		}
	}
	
	public void addLocalVarArray(String id, String size) {
		if(!hasLocalVars){
			lBuilder.append("class [mscorlib]System.Collections.Generic.List`1<object> list,");
			lBuilder.append(String.format("float32[] %s ", id));
			hasLocalVars = true;
			laBuilder.append(String.format("ldc.i4.s %d", size));
		}
		else{
			lBuilder.append(String.format(", float32[] %s ", id));
			laBuilder.append(String.format("%sldc.i4.s %s", newLine, size));
		}
		laBuilder.append(String.format("%snewarr [mscorlib]System.Single", newLine));
		laBuilder.append(String.format("%sstloc %s", newLine, id));
	}
	
	public String getLocalVars(){
		if(hasLocalVars) lBuilder.append(", ");
		lBuilder.append("class [mscorlib]System.Collections.Generic.List`1<object> printList");
		lBuilder.append(", int32 printCount");
		lBuilder.append(')');
		String output = lBuilder.toString();
		lBuilder = new StringBuilder(".locals init (");
		hasLocalVars = false;
		return output + getLocalArrayInit();
	}
	
	private String getLocalArrayInit(){
		String output = laBuilder.toString();
		laBuilder = new StringBuilder();
		return output;
	}	
	
	public String input(String var){
		StringBuilder sb = new StringBuilder("call string [mscorlib]System.Console::ReadLine()");
		sb.append(String.format("%scall float32 [mscorlib]System.Single::Parse(string)", newLine));
		if(globalVars.contains(var)){
			sb.append(String.format("%sstsfld float32 lilWildc::%s", newLine, var));
		}else{
			sb.append(String.format("%sstloc %s", newLine, var));
		}
		return sb.toString();
	}
	
	public String clearPrintList(){
		StringBuilder sb = new StringBuilder("newobj instance void class [mscorlib]System.Collections.Generic.List`1<object>::.ctor()");
		sb.append(String.format("%sstloc printList", newLine));
		return sb.toString();
	}
	
	public String addPrintString(String string){
		StringBuilder sb = new StringBuilder("ldloc printList");
		sb.append(String.format("%sldstr %s", newLine, string));
		sb.append(String.format("%scallvirt instance void class [mscorlib]System.Collections.Generic.List`1<object>::Add(!0)", newLine));
		return sb.toString();
	}
	
	public String addPrintExpr(){
		StringBuilder sb = new StringBuilder("box [mscorlib]System.Single");
		sb.append(String.format("%scallvirt instance void class [mscorlib]System.Collections.Generic.List`1<object>::Add(!0)", newLine));
		return sb.toString();
	}

	public String whileLoop(String innerCode){
		String startLoop = UUID.randomUUID().toString();
		String endLoop = UUID.randomUUID().toString();
		
		StringBuilder sb = new StringBuilder(String.format("%s:", startLoop));
		sb.append(String.format("%s%s:",newLine, endLoop));
		return sb.toString();		
	}
	
	public String printLoop(){
		String beginLoop = getLabel();
		String endLoop = getLabel();
		StringBuilder sb = new StringBuilder("ldc.i4 0");
		sb.append(String.format("%sstloc printCount", newLine));
		
		//Check to see if there are any elements in the list
		sb.append(String.format("%sldloc printList", newLine));
		sb.append(String.format("%scall int32 [System.Core]System.Linq.Enumerable::Count<object>(class [mscorlib]System.Collections.Generic.IEnumerable`1<!!0>)", newLine));
		sb.append(String.format("%sldc.i4.s 1", newLine));
		sb.append(String.format("%sblt.s %s", newLine, endLoop));
		//End the check of elements in the list
		
		//Start the loop
		sb.append(String.format("%s%s:", newLine, beginLoop));
		
		//Load an Item to be printed and print it
		sb.append(String.format("%sldloc printList", newLine));
		sb.append(String.format("%sldloc printCount", newLine));
		sb.append(String.format("%scallvirt instance !0 class [mscorlib]System.Collections.Generic.List`1<object>::get_Item(int32)", newLine));
		sb.append(String.format("%scall void [mscorlib]System.Console::Write(object)", newLine));

		
		sb.append(String.format("%sldloc printCount", newLine));
		sb.append(String.format("%sldloc printList", newLine));
		sb.append(String.format("%scall int32 [System.Core]System.Linq.Enumerable::Count<object>(class [mscorlib]System.Collections.Generic.IEnumerable`1<!!0>)", newLine));
		sb.append(String.format("%sldloc printCount", newLine));
		sb.append(String.format("%sldc.i4.s 1", newLine));
		sb.append(String.format("%sadd", newLine));
		sb.append(String.format("%sstloc printCount", newLine));
		sb.append(String.format("%sldc.i4.s 1", newLine));
		sb.append(String.format("%ssub", newLine));
		sb.append(String.format("%sblt.s %s", newLine, beginLoop));
		
		sb.append(String.format("%s%s:", newLine, endLoop));
		return sb.toString();
	}
	
	public String getLabel(){
		return "l" + UUID.randomUUID().toString().replace("-", "").substring(0, 6);
	}
	
	public String loadVar(String var){
		StringBuilder sb = new StringBuilder();
		if(globalVars.contains(var)){
			sb.append(String.format("ldsfld float32 lilWildc::%s", var));
		}else{
			sb.append(String.format("ldloc %s", var));
		}
		return sb.toString();
	}
	
	public String loadVarArrayElem(String var){
		StringBuilder sb;
		if(globalVarArray.containsKey(var)){
			sb = new StringBuilder(String.format("ldsfld float32[] lilWildc::%s", var));
		}else{
			sb = new StringBuilder(String.format("ldloc %s", var));
		}
		//sb.append(String.format("%sldc.i4.s %s", newLine, loc));
		//sb.append(String.format("%sldelem.r4", newLine));
		return sb.toString();
	}
	
	public String setVar(String var){
		if(globalVars.contains(var)){
			return String.format("stsfld float32 lilWildc::%s", var);
		}else{
			return String.format("stloc %s", var);
		}
	}
	
	public String setVarArray(String var){
		StringBuilder sb;
		if(globalVarArray.containsKey(var)){
			sb = new StringBuilder(String.format("ldsfld float32[] lilWildc::%s", var));
		}else{
			sb = new StringBuilder(String.format("ldloc %s", var));
		}
		return sb.toString();
	}
	
	public String storeVarArrayElem(String var){
		return "stelem.r4";
	}
	
	public String compareGreaterOrEquals(){
		StringBuilder sb = new StringBuilder("clt");
		sb.append(String.format("%sldc.i4.0", newLine));
		sb.append(String.format("%sceq", newLine));
		return sb.toString();
	}
	
	public String compareLessOrEquals(){
		StringBuilder sb = new StringBuilder("cgt");
		sb.append(String.format("%sldc.i4.0", newLine));
		sb.append(String.format("%sceq", newLine));
		return sb.toString();
	}
	
	public String compareNotEquals(){
		StringBuilder sb = new StringBuilder("ceq");
		sb.append(String.format("%sldc.i4.0", newLine));
		sb.append(String.format("%sceq", newLine));
		return sb.toString();
	}
	
	public String whileEnd(String beginLabel, String endLabel){
		StringBuilder sb = new StringBuilder(String.format("br %s", beginLabel));
		sb.append(String.format("%s%s:", newLine, endLabel));
		return sb.toString();
	}
	
	public String ifJumpToElse(String elseLabel){
		StringBuilder sb = new StringBuilder(String.format("%sbrfalse %s", newLine, elseLabel));
		return sb.toString();
	}
	
	public String ifJumpToEnd(String endLabel){
		StringBuilder sb = new StringBuilder(String.format("br %s", endLabel));
		return sb.toString();
	}
	
	public String callMethod(String method){
		StringBuilder sb = new StringBuilder(String.format("call void lilWildc::%s()", method));
		return sb.toString();
	}
	
}
