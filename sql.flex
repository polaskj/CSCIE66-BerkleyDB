/**
 * sql.flex - JFlex specification for a lexical analyzer for
 * individual SQL statements from the subset of SQL supported by our parser
 *
 * Portions of this file are based on the examples in the
 * JFlex distribution.
 */

import java_cup.runtime.*;

%%

%public
%class Lexer

/* We're scanning text. */
%unicode

/* Turn on character counting. */
%char

/* Use CUP compatibility mode. */
%cup

/* Create a main() method for debugging purposes. */
%cupdebug

/* Ignore case when matching keywords */
%ignorecase

/* Code to include in the generated Scanner class. */
%{
  private Symbol symbol(int type) {
      return new Symbol(type, yychar, yychar + yylength());
  }

  private Symbol symbol(int type, Object value) {
      return new Symbol(type, yychar, yychar + yylength(), value);
  }
%}

/* Abbreviations for regular expressions. */
alpha = [A-Za-z]
digit = [0-9]
integer = [+\-]?{digit}+
real = [+\-]?{digit}+\.{digit}+
id = ({alpha}|{digit})*{alpha}({alpha}|{digit})*
white_space = [\ \t\b\012\n]

%%

/* keywords */
"all"	      { return symbol(sym.ALL); }
"and"	      { return symbol(sym.AND); }
"as"	      { return symbol(sym.AS); }
"begin"	      { return symbol(sym.BEGIN); }
"char"	      { return symbol(sym.CHAR); }
"clike"	      { return symbol(sym.CLIKE); }
"commit"      { return symbol(sym.COMMIT); }
"create"      { return symbol(sym.CREATE); }
"delete"      { return symbol(sym.DELETE); }
"distinct"    { return symbol(sym.DISTINCT); }
"drop"	      { return symbol(sym.DROP); }
"from"	      { return symbol(sym.FROM); }
"insert"      { return symbol(sym.INSERT); }
"int"	      { return symbol(sym.INTEGER); }
"integer"     { return symbol(sym.INTEGER); }
"into"	      { return symbol(sym.INTO); }
"is"	      { return symbol(sym.IS); }
"key"	      { return symbol(sym.KEY); }
"like"	      { return symbol(sym.LIKE); }
"limit"	      { return symbol(sym.LIMIT); }
"not"	      { return symbol(sym.NOT); }
"null"	      { return symbol(sym.NULL); }
"or"	      { return symbol(sym.OR); }
"primary"     { return symbol(sym.PRIMARY); }
"real"	      { return symbol(sym.REAL); }
"rollback"    { return symbol(sym.ROLLBACK); }
"select"      { return symbol(sym.SELECT); }
"set"	      { return symbol(sym.SET); }
"table"	      { return symbol(sym.TABLE); }
"update"      { return symbol(sym.UPDATE); }
"values"      { return symbol(sym.VALUES); }
"varchar"     { return symbol(sym.VARCHAR); }
"where"	      { return symbol(sym.WHERE); }
"work"	      { return symbol(sym.WORK); }
  
/* separators and other special symbols */
"("	      { return symbol(sym.LPAREN); }
")"	      { return symbol(sym.RPAREN); }
","	      { return symbol(sym.COMMA); }
"."	      { return symbol(sym.DOT); }
"*"           { return symbol(sym.STAR); }
";"	      { return symbol(sym.SEMICOLON); }
  
/* operators -- see also CLIKE, LIKE, IS, and NOT under keywords */
"="	      { return symbol(sym.EQ); }
">"	      { return symbol(sym.GT); }
"<"	      { return symbol(sym.LT); }
"!"           { return symbol(sym.NOT); }
"=="	      { return symbol(sym.EQ); }
"<="	      { return symbol(sym.LTEQ); }
">="	      { return symbol(sym.GTEQ); }
"!="          { return symbol(sym.NOTEQ); }
"<>"          { return symbol(sym.NOTEQ); }
  
/* 
 * string literal -- 0 or more characters surrounded by double or
 * single quotes
 */
\"[^\"]*\" |
\'[^\']*\'    {
        String str = yytext().substring(1, yylength() - 1);
	return symbol(sym.STRING, str);
}

{integer}     { return symbol(sym.INT_VAL, new Integer(yytext())); }
{real}        { return symbol(sym.REAL_VAL, new Double(yytext())); }
{id}          { return symbol(sym.ID, yytext()); }
{white_space} { /* skip it */ }

/* No token found. */
[^]           { 
	throw new RuntimeException("Illegal character \"" + 
	  yytext() + "\""); 
}