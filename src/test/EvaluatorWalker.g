tree grammar EvaluatorWalker;

options {
  language = Java;
  tokenVocab = lilWildC;
  ASTLabelType = CommonTree;
}

@header {
  package test;
  import test.compiler.tools.CodeBuilder;
  import test.compiler.tools.CilGenerator;
}
@members {
  CodeBuilder cb = new CodeBuilder();
  CilGenerator cg = new CilGenerator();
}

evaluator returns [String result]
  :{cb.append(cg.getInitCode());}
  globalVars* {cb.appendLine(cg.genConstructor());} program
  {cb.appendLine(cg.getEndCode());}
  {result = cb.toString();}  
  ;
 
globalVars
  : ^('number' e=ID){cb.appendLine(cg.genGlobalVar($e.text));}
  | ^('number' s=A_NUMBER k=ID){cb.appendLine(cg.genGlobalVarArray($k.text, $s.text));}
  ;
  
 
program
  : ^('procedure' id=ID {cb.appendLine(cg.genProcedureStart($id.text));} localVars* {cb.appendLine(cg.getLocalVars());} statement* 'return') {cb.appendLine(cg.getEndCode());}
  ;

statement
  : ^('=' id=ID expr ) {cb.appendLine("stloc "+$id.text);}
  | ^('print' {cb.appendLine(cg.clearPrintList());} out_item+) {cb.appendLine(cg.printLoop());}
  | ^('input' e=ID) {cb.appendLine(cg.input($e.text));}
  ;
  
out_item
  : e=A_STRING {cb.appendLine(cg.addPrintString($e.text));}
  | {cb.appendLine("ldloc printList");}expr {cb.appendLine(cg.addPrintExpr());}
  ;
  
expr
  : ^('+' op1=expr op2=expr) {cb.appendLine("add");}
  | ^('-' op1=expr op2=expr) {cb.appendLine("sub");}
  | ^('/' op1=expr op2=expr) {cb.appendLine("div");}
  | ^('*' op1=expr op2=expr) {cb.appendLine("mul");}
  | s=A_NUMBER {cb.appendLine("ldc.i4.s " + $s.text);}
  ;
  
localVars
  : ^('number' e=ID){cg.addLocalVar($e.text);}
  | ^('number' s=A_NUMBER id=ID){cg.addLocalVarArray($id.text, $s.text);}
  ;