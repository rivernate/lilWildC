grammar lilWildC;
options
{
    language = Java;
    output=AST;
    ASTLabelType=CommonTree;
    backtrack=true;
    memoize=true;
    k=2;
}

tokens {
  ELEMENT;
}

@header
{
package test;
import java.util.HashMap;

}

@lexer::header
{
package test;
}

@members
{
/** Map variable name symbol object */
HashMap memory = new HashMap();
}

program		:	global_vars procedure+
		;

global_vars	:	var_def*
		;

var_def		:	'number'^ ID ';'!
		|	'number'^ '['! A_NUMBER ']'! ID ';'!
		;

procedure	:	'procedure'^ ID '{'! block '}'!		// ID == "main" will be the start procedure
		;

block		:	local_vars statement+
		;

local_vars	:	var_def*
		;

statement	:	var_ref '='^ num_expr ';'!
		|	'call'^ ID ';'!
		|	'if'^ '('! condition ')'! '{'! statement+ '}'! ('else'^ '{'! statement+ '}'!)?
		|	'while'^ '('! condition ')'! '{'! statement+ '}'!
		|	'input'^ var_ref ';'!
		|	'print'^ output ';'!
		|	'return'^ ';'!
		;

var_ref		:	ID
		|	ID '[' num_expr ']' -> ^(ELEMENT ID num_expr)
		;

output		:	out_item (','! out_item)*
		;

out_item	:	num_expr
		|	A_STRING
		;

num_expr	:	multExpr
			( '+'^ multExpr
			| '-'^ multExpr
			)*
		;

multExpr	:	negFactor
			( '*'^ negFactor
			| '/'^ negFactor
			)*
		;

negFactor	:	'-'^? factor
		;

factor		:	A_NUMBER
		|	var_ref
		|	'('! num_expr ')'!
		;

condition	:	and_expr
			( '||'^ and_expr
			)*
		;

and_expr	:	rel_expr
			( '&&'^ rel_expr
			)*
		;

rel_expr	:	num_expr '<'^ num_expr
		|	num_expr '<='^ num_expr
		|	num_expr '>'^ num_expr
		|	num_expr '>='^ num_expr
		|	num_expr '=='^ num_expr
		|	num_expr '!='^ num_expr
		|	'(' condition ')'
		;

ID		:	('a'..'z'|'A'..'Z')('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
		;

A_NUMBER	:	(('0'..'9')+'.'?)|(('0'..'9')*'.'('0'..'9')+)
		;

A_STRING	:	'"'(~'"')*'"'
		;

WS		:	(' '|'\t'|('\r'?'\n'))+ {skip();}
		;
