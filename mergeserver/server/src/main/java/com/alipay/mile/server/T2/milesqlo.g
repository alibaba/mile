grammar milesqlo;

options {
  language = Java;
  output   = AST;
  k        = 4;
}


sql_stmt :  sql_stmt_core EOF! ;

sql_stmt_core  :  update_stmt  |   delete_stmt  |   insert_stmt  | getkv_stmt |  export_stmt | select_stmt_expr ;

select_stmt_expr: unionsection_expr (UNIONS^ unionsection_expr)*;
unionsection_expr: intersection_expr (INTERSECTION^ intersection_expr)*;
intersection_expr: select_stmt;

select_stmt  :
  SELECT  (ALL | DISTINCT)? res=result_column (COMMA res=result_column )*
   FROM table_name=ID
  (DOCHINT dochint_expr)?
  (SEGHINT seghint_expr)?
  (INDEXWHERE hash_Where=expr)?
  (WHERE where_expr=expr)?
  ( 	GROUP BY group_term (COMMA group_term )*
  	(HAVING having_expr)?
  	(GROUPORDER BY gordert=gorder_term (COMMA gordert=gorder_term)*)?
  	(GLIMIT limit=INTEGER)?
  	(GOFFSET offset=INTEGER)?
  )?
  (ORDER BY ordert=ordering_term(COMMA ordert=ordering_term)*)?
  (LIMIT limit=INTEGER)?
  (OFFSET offset=INTEGER)?
  ;
group_term : column_name=(ID|INTEGER) ;
result_column  : ASTERISK
  		| column_name=selectexpr(AS aliseName=(ID|INTEGER))? ;

ordering_term  :  column_name=(ID|INTEGER) (ascd=(ASC | DESC))?  ;

gorder_term  :  column_name=(ID|INTEGER)(ascd=(ASC | DESC ))?  ;

insert_stmt  :  INSERT INTO table_name=ID (column_name=(ID|INTEGER) EQUALS lv=literal_value )* (WITH (WORDSEG LPAREN column_name=(ID|INTEGER) RPAREN EQUALS LPAREN lv3=literal_value (COMMA lv4=literal_value)* RPAREN)* )? ;

export_stmt : EXPORT TO path=literal_value FROM table_name=ID
	(SEGHINT dochint_expr)?
	(INDEXWHERE expr )?
	(WHERE expr )?
	(LIMIT INTEGER)?
	;

delete_stmt  :  DELETE FROM table_name=ID   (SEGHINT seghint_expr)?   (INDEXWHERE expr)?   (WHERE where_expr=expr)?  ;

update_stmt  :  UPDATE table_name=ID SET update_set   (SEGHINT seghint_expr)?   (INDEXWHERE expr)?   (WHERE where_expr=expr)?  ;

getkv_stmt :   SELECT  res=result_column (COMMA res=result_column )*  FROM table_name=ID  DOCHINT dochint_expr ;

// delkv_stmt :   DELETE FROM table_name=ID  DOCHINT dochint_expr ;

// upkv_stmt : UPDATE table_name=ID SET update_set  DOCHINT dochint_expr) ;


dochint_expr  :  doc_name=(ID|INTEGER) EQUALS doc_id=INTEGER ;

update_set  :  column_name=(ID|INTEGER) EQUALS lv=literal_value ;

seghint_expr  :LPAREN (INTEGER|QUESTION) COMMA (INTEGER|QUESTION) COMMA (INTEGER|QUESTION) COMMA (INTEGER|QUESTION) RPAREN  ;

expr: or_subexpr (OR^ or_subexpr)*;
or_subexpr: and_subexpr (AND^ and_subexpr)*;
and_subexpr: eq_subexpr
	    | LPAREN expr RPAREN ;

having_expr: having_or_subexpr (OR  having_or_subexpr)*;
having_or_subexpr: having_and_subexpr (AND  having_and_subexpr)*;
having_and_subexpr: having_eq
	    | LPAREN having_expr RPAREN ;

selectexpr  :  column_name=(ID|INTEGER)
		| ( funcname=  SUM LPAREN column_name=(ID|INTEGER) RPAREN 
				| funcname=  MAX  LPAREN column_name=(ID|INTEGER) RPAREN
				| funcname=  MIN  LPAREN column_name=(ID|INTEGER) RPAREN
				| funcname=  AVG  LPAREN column_name=(ID|INTEGER) RPAREN
				| funcname=  SQUARESUM  LPAREN column_name=(ID|INTEGER) RPAREN
				| funcname=  VARIANCE  LPAREN column_name=(ID|INTEGER) RPAREN
				| funcname=  STDDEV  LPAREN column_name=(ID|INTEGER) RPAREN
				| funcname=  COUNT LPAREN (column_name=(ID|INTEGER)|ASTERISK | DISTINCT column_name=(ID|INTEGER)) RPAREN ) (WITHIN within_expr = expr )? ;

eq_subexpr  :  column_name=(ID|INTEGER) leq= ( LESS | LESS_OR_EQ | GREATER | GREATER_OR_EQ | EQUALS | NOT_EQUALS1 | NOT_EQUALS2) lv=literal_value
		| column_name=(ID|INTEGER)  BETWEEN (LPAREN|LPAREN_SQUARE) lv=literal_value COMMA lv=literal_value (RPAREN|RPAREN_SQUARE)
		| column_name3=(ID|INTEGER)  IN LPAREN lv3=literal_value (COMMA lv4=literal_value)* RPAREN
		| column_name4=(ID|INTEGER)  IN LPAREN ss=select_stmt RPAREN
		| column_name5=(ID|INTEGER) MATCH LPAREN lv=literal_value (COMMA lv=literal_value)* RPAREN
		| UNIONHASH LPAREN seq = eq_sequence RPAREN ;

eq_sequence : column_name = (ID|INTEGER) EQUALS lv = literal_value ( COMMA column_name2 = (ID|INTEGER) EQUALS lv2 = literal_value) * ;

having_eq  :  selectexpr eq= ( LESS | LESS_OR_EQ | GREATER | GREATER_OR_EQ | EQUALS | NOT_EQUALS1 | NOT_EQUALS2) lv=literal_value
		| column_name=(ID|INTEGER)  BETWEEN (LPAREN|LPAREN_SQUARE) lv=literal_value COMMA lv=literal_value (RPAREN|RPAREN_SQUARE)
		| column_name3=(ID|INTEGER)  IN LPAREN lv3=literal_value (COMMA lv4=literal_value)* RPAREN
		| column_name4=(ID|INTEGER)  IN LPAREN ss=select_stmt RPAREN;

literal_value : e=(INTEGER | FLOAT | STRING | QUESTION);


EQUALS:        '=';
NOT_EQUALS1:   '!=';
NOT_EQUALS2:   '<>';
LESS:          '<';
LESS_OR_EQ:    '<=';
GREATER:       '>';
GREATER_OR_EQ: '>=';
SEMI:          ';';
COMMA:         ',';
LPAREN:        '(';
RPAREN:        ')';
DOT:           '.';
UNDERSCORE:    '_';
DLLOAR:	       '$';
QUESTION:      '?';
QUOTE_DOUBLE:  '"';
QUOTE_SINGLE:  '\'';
BACKSLASH:     '\\';
ASTERISK:      '*';
LPAREN_SQUARE: '[';
RPAREN_SQUARE: ']';
// http://www.antlr.org/wiki/pages/viewpage.action?pageId=1782
fragment A:('a'|'A');
fragment B:('b'|'B');
fragment C:('c'|'C');
fragment D:('d'|'D');
fragment E:('e'|'E');
fragment F:('f'|'F');
fragment G:('g'|'G');
fragment H:('h'|'H');
fragment I:('i'|'I');
fragment J:('j'|'J');
fragment K:('k'|'K');
fragment L:('l'|'L');
fragment M:('m'|'M');
fragment N:('n'|'N');
fragment O:('o'|'O');
fragment P:('p'|'P');
fragment Q:('q'|'Q');
fragment R:('r'|'R');
fragment S:('s'|'S');
fragment T:('t'|'T');
fragment U:('u'|'U');
fragment V:('v'|'V');
fragment W:('w'|'W');
fragment X:('x'|'X');
fragment Y:('y'|'Y');
fragment Z:('z'|'Z');


AND: A N D;
ALL: A L L;
AS: A S;
ASC: A S C;
AVG: A V G;
BETWEEN	:B E T W E E N;
BY: B Y;
COUNT: C O U N T;
CURRENT_TIMESTAMP: C U R R E N T '_' T I M E S T A M P;
DELETE: D E L E T E;
DESC	:D E S C ;
DISTINCT: D I S T I N C T;
DOCHINT	: D O C H I N T;
FROM: F R O M;
GLIMIT: G L I M I T ;
GOFFSET : G O F F S E T;
GROUP: G R O U P;
GROUPORDER: G R O U P O R D E R;
GRANGE : G R A N G E;
HAVING: H A V I N G;
IN: I N;
INDEXWHERE: I N D E X W H E R E;
UNIONHASH: U N I O N H A S H;
INSERT: I N S E R T;
INTERSECTION: I N T E R S E C T I O N;
INTO: I N T O;
LEFT: L E F T;
LIMIT: L I M I T;
MATCH: M A T C H;
MAX: M A X;
MIN: M I N;
NULL: N U L L;
OFFSET:	O F F S E T;
OR: O R;
ORDER: O R D E R;
SEGHINT	: S E G H I N T;
SELECT: S E L E C T;
SET: S E T;
SQUARESUM: S Q U A R E S U M;
STDDEV: S T D D E V;
SUM: S U M;
UNIONS: U N I O N S;
UPDATE : U P D A T E;
VARIANCE : V A R I A N C E;
WHERE: W H E R E;
WITH: W I T H;
WITHIN: W I T H I N;
WORDSEG: W O R D S E G;
TO: T O;
EXPORT: E X P O R T;

fragment STRING_ESCAPE_SINGLE: (BACKSLASH QUOTE_SINGLE);
fragment STRING_ESCAPE_DOUBLE: (BACKSLASH QUOTE_DOUBLE);
fragment STRING_CORE: ~(QUOTE_SINGLE | QUOTE_DOUBLE);
fragment STRING_CORE_SINGLE: ( STRING_CORE | QUOTE_DOUBLE | STRING_ESCAPE_SINGLE )*;
fragment STRING_CORE_DOUBLE: ( STRING_CORE | QUOTE_SINGLE | STRING_ESCAPE_DOUBLE )*;
fragment STRING_SINGLE: (QUOTE_SINGLE STRING_CORE_SINGLE QUOTE_SINGLE);
fragment STRING_DOUBLE: (QUOTE_DOUBLE STRING_CORE_DOUBLE QUOTE_DOUBLE);
STRING: (STRING_SINGLE | STRING_DOUBLE);

fragment ID_START: ('a'..'z'|'A'..'Z'|UNDERSCORE|DOLLAR);
fragment ID_CORE: (ID_START|'0'..'9');
fragment ID_PLAIN: ID_START (ID_CORE)*;






ID: ID_PLAIN ;

//TCL_ID: ID_START (ID_START|'0'..'9'|'::')* (LPAREN ( options {greedy=false;} : . )* RPAREN)?;

INTEGER: ('0'..'9')+;
fragment FLOAT_EXP : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;
FLOAT
    :   ('0'..'9')+ DOT ('0'..'9')* FLOAT_EXP?
    |   DOT ('0'..'9')+ FLOAT_EXP?
    |   ('0'..'9')+ FLOAT_EXP
    ;
BLOB: ('x'|'X') QUOTE_SINGLE ('0'..'9'|'a'..'f'|'A'..'F')+ QUOTE_SINGLE;

fragment COMMENT: '/*' ( options {greedy=false;} : . )* '*/';
fragment LINE_COMMENT: '--' ~('\n'|'\r')* ('\r'? '\n'|EOF);
WS: (' '|'\r'|'\t'|'\u000C'|'\n'|COMMENT|LINE_COMMENT) {$channel=HIDDEN;};
