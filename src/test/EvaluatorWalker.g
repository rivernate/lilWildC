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
  program+ {cb.appendLine(cg.getEndCode());}
  {result = cb.toString();}  
  ;
 
globalVars
  : ^('number' e=ID){cb.appendLine(cg.genGlobalVar($e.text));}
  | ^('number' s=A_NUMBER k=ID){cb.appendLine(cg.genGlobalVarArray($k.text, $s.text));}
  ;
  
 
program
  : ^('procedure' id=ID {cb.appendLine(cg.genProcedureStart($id.text));} 
       localVars* {cb.appendLine(cg.getLocalVars());} 
       statement+ 
       ) 
       {cb.appendLine(cg.getEndCode());}
  ;

statement
  : ^('=' set_var ) 
  | ^('print' {cb.appendLine(cg.clearPrintList());} out_item+) {cb.appendLine(cg.printLoop());}
  | ^('input' e=ID) {cb.appendLine(cg.input($e.text));}
  | ^('call' e=ID) {cb.appendLine(cg.callMethod($e.text));}
  | while_loop
  | {String endLabel = cg.getLabel();} if_statement[endLabel, endLabel]
    {cb.appendLine(endLabel + ":");}
  | if_else_statement
  | 'return' {cb.appendLine("ret");}
  ;
  
if_else_statement
  @init{
    String elseLabel = cg.getLabel();
    String endLabel = cg.getLabel();
  }
  : ^('else' 
    if_statement[elseLabel, endLabel]
    {cb.appendLine(elseLabel + ":");}
    statement
    {cb.appendLine(endLabel + ":");}
    )
  ;
  
if_statement[String elseLabel, String endLabel]
  : ^('if' 
    condition
    {cb.appendLine(cg.ifJumpToElse(elseLabel));}
    statement+
    {cb.appendLine(cg.ifJumpToEnd(endLabel));}
    )
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
  : ^('<' expr expr ) {cb.appendLine("clt");}
  | ^('>' expr expr ) {cb.appendLine("cgt");}
  | ^('>=' expr expr ) {cb.appendLine(cg.compareGreaterOrEquals());}
  | ^('<=' expr expr ) {cb.appendLine(cg.compareLessOrEquals());}
  | ^('==' expr expr ) {cb.appendLine("ceq");}
  | ^('!=' expr expr ) {cb.appendLine(cg.compareNotEquals());}
  | ^('&&' condition condition ) {cb.appendLine("and");}
  | ^('||' condition condition ) {cb.appendLine("or");}
  ;
  
set_var
  : id=ID expr {cb.appendLine(cg.setVar($id.text));}
  | ^(ELEMENT id=ID {cb.appendLine(cg.setVarArray($id.text));} expr) expr {cb.appendLine(cg.storeVarArrayElem($id.text));}
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
  | ^(NEG expr) {cb.appendLine("neg");}
  | s=A_NUMBER {cb.appendLine("ldc.r4 " + $s.text);}
  | e=ID {cb.appendLine(cg.loadVar($e.text));}
  | ^(ELEMENT id=ID {cb.appendLine(cg.loadVarArrayElem($id.text));} expr {cb.appendLine("ldelem.r4");}) 
  ;
  
localVars
  : ^('number' e=ID){cg.addLocalVar($e.text);}
  | ^('number' s=A_NUMBER id=ID){cg.addLocalVarArray($id.text, $s.text);}
  ;
