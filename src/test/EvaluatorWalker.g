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
  globalVars* {cb.appendLine(cg.genConstructor());} 
  program {cb.appendLine(cg.getEndCode());}
  {result = cb.toString();}  
  ;
 
globalVars
  : ^('number' e=ID){cb.appendLine(cg.genGlobalVar($e.text));}
  | ^('number' s=A_NUMBER k=ID){cb.appendLine(cg.genGlobalVarArray($k.text, $s.text));}
  ;
  
 
program
  : ^('procedure' id=ID {cb.appendLine(cg.genProcedureStart($id.text));} 
       localVars* {cb.appendLine(cg.getLocalVars());} 
       statement* 
       'return' {cb.appendLine("ret");}) 
       {cb.appendLine(cg.getEndCode());}
  ;

statement
  : ^('=' set_var ) 
  | ^('print' {cb.appendLine(cg.clearPrintList());} out_item+) {cb.appendLine(cg.printLoop());}
  | ^('input' e=ID) {cb.appendLine(cg.input($e.text));}
  | while_loop
  | if_statement
  | ^('else' if_statement statement)
  ;
  
if_statement
  :  ^('if' condition statement+)
  ;  
  
while_loop
  @init{
    String beginLabel = cg.getLabel();
    String endLabel = cg.getLabel();
  }
  :^( 'while' 
      {cb.appendLine(beginLabel + ':');}
      condition 
      {cb.appendLine("brfalse "+ endLabel);} 
      statement+ 
      {cb.appendLine(cg.whileEnd(beginLabel, endLabel));})
  ;
  
condition
  : ^('<' e1=expr e2=expr ) {cb.appendLine("clt");}
  | ^('>' e1=expr e2=expr ) {cb.appendLine("cgt");}
  | ^('>=' e1=expr e2=expr ) {cb.appendLine(cg.compareGreaterOrEquals());}
  | ^('<=' e1=expr e2=expr ) {cb.appendLine(cg.compareLessOrEquals());}
  | ^('==' e1=expr e2=expr ) {cb.appendLine("ceq");}
  | ^('!=' e1=expr e2=expr ) {cb.appendLine(cg.compareNotEquals());}
  | ^('&&' e1=expr e2=expr ) {cb.appendLine("and");}
  | ^('||' e1=expr e2=expr ) {cb.appendLine("or");}
  ;
  
set_var
  : id=ID expr {cb.appendLine(cg.setVar($id.text));}
  | ^(ELEMENT id=ID l=A_NUMBER){cb.appendLine(cg.setVarArray($id.text, $l.text));} expr {cb.appendLine(cg.storeVarArrayElem($id.text));}
  ;
  
out_item
  : e=A_STRING {cb.appendLine(cg.addPrintString($e.text));}
  | {cb.appendLine("ldloc printList");}expr {cb.appendLine(cg.addPrintExpr());}  
  ;
  
expr
  : ^('+' expr expr) {cb.appendLine("add");}
  | ^('-' expr expr) {cb.appendLine("sub");}
  | ^('/' expr expr) {cb.appendLine("div");}
  | ^('*' expr expr) {cb.appendLine("mul");}
  | s=A_NUMBER {cb.appendLine("ldc.r4 " + $s.text);}
  | e=ID {cb.appendLine(cg.loadVar($e.text));}
  | ^(ELEMENT id=ID l=A_NUMBER) {cb.appendLine(cg.loadVarArrayElem($id.text, $l.text));}
  ;
  
localVars
  : ^('number' e=ID){cg.addLocalVar($e.text);}
  | ^('number' s=A_NUMBER id=ID){cg.addLocalVarArray($id.text, $s.text);}
  ;
