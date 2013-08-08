grammar mileSqlCondition;

options {
	language = Java;
	output = AST;
	k = 4;
}
@header {
package com.alipay.mile.server.T2;
import com.alipay.mile.FieldDesc;
import com.alipay.mile.Constants;
import com.alipay.mile.mileexception.SQLException;
import com.alipay.mile.server.query.*;
import com.alipay.mile.server.query.special.*;
import java.util.concurrent.atomic.AtomicInteger;
}
@lexer::header{
package com.alipay.mile.server.T2;
import com.alipay.mile.mileexception.SQLException;
}

@members{
	public Statement statement;
	private AtomicInteger pamerIndex = new AtomicInteger(-1);
	@Override
	protected Object recoverFromMismatchedToken(IntStream input, int ttype, BitSet follow) throws RecognitionException {
		RecognitionException e =new MismatchedTokenException(ttype, input);
		throw new SQLException("\t\n"+e.input.toString() +"\t\n"+getErrorHeader(e)+ " " + getErrorMessage(e, tokenNames) ) ;
	}

	@Override
	public Object recoverFromMismatchedSet(IntStream input, RecognitionException e, BitSet follow) throws RecognitionException {
		throw e;
	}
}

@rulecatch {
	catch (RecognitionException el) {
		throw el;
	}
}

@lexer::members {
	@Override
	public void reportError(RecognitionException e) {
		throw new SQLException("\t\n"+e.input.toString() +"\t\n"+getErrorHeader(e)+ " " + getErrorMessage(e, getTokenNames()) );
	}
}


sql_stmt returns [Statement statement]
	: sql_stmt_core EOF!
	;

sql_stmt_core
	:
	{
		SpecifyQueryStatement st = new SpecifyQueryStatement();
		statement = st;
	}
	condition_stmt [st]
	;

condition_stmt [SpecifyQueryStatement specifyQueryStatement]
	:
	(SEGHINT seg_hint = seghint_expr {specifyQueryStatement.hint = seg_hint.hint;})?
	INDEXWHERE  hash_Where = expr{specifyQueryStatement.hashWhere = hash_Where.expression;}
	(WHERE filterWhere = expr{specifyQueryStatement.filterWhere = filterWhere.expression;})?
	;


seghint_expr returns [TimeHint hint = new TimeHint()]
	: LPAREN startCreateTime=INTEGER COMMA endCreateTime=INTEGER COMMA startUpdateTime=INTEGER COMMA endUpdateTime=INTEGER RPAREN
	{
		$hint.startCreateTime = Long.parseLong(startCreateTime.getText().trim());
		$hint.endCreateTime = Long.parseLong(endCreateTime.getText().trim());
		$hint.startUpdateTime = Long.parseLong(startUpdateTime.getText().trim());
		$hint.endUpdateTime = Long.parseLong(endUpdateTime.getText().trim());
	}
	;


expr returns [Expression expression]
	: or_sub = or_subexpr
	{
		$expression=or_sub.expression;
	}
	(OR or_sub =or_subexpr
	{
		$expression = $expression.orExp(or_sub.expression);
	}
	)*
	;
or_subexpr returns [Expression expression]
	: and_sub = and_subexpr
	{
		$expression=and_sub.expression;
	}
	(AND and_sub=and_subexpr
	{
		$expression = $expression.andExp(and_sub.expression);
	}
	)*
	;
and_subexpr returns [Expression expression]
	: eq_sub=eq_subexpr
	{
		$expression = eq_sub.expression;
	}
	| LPAREN expr_sub=expr RPAREN
	{
		$expression = expr_sub.expression;
	}
	;


eq_subexpr returns [Expression expression]
	: column_name = ID leq = ( LESS | LESS_OR_EQ | GREATER | GREATER_OR_EQ | EQUALS ) lv=literal_value
	{
		ColumnExp columnExp = new ColumnExp();
		$expression = columnExp;
		FieldDesc fd =new FieldDesc();
		fd.fieldName = column_name.getText().trim();
		columnExp.values = new ArrayList();
		columnExp.values.add(lv.valueDesc);
		columnExp.column = fd;
		String tag = leq.getText().trim();
        if(tag.equals("=")){
			columnExp.comparetor = Constants.EXP_COMPARE_EQUALS;
		}else if(tag.equals("<")){
			columnExp.comparetor = Constants.EXP_COMPARE_LT;
		}else if(tag.equals("<=")){
			columnExp.comparetor = Constants.EXP_COMPARE_LET;
		}else if(tag.equals(">")){
			columnExp.comparetor = Constants.EXP_COMPARE_GT;
		}else if(tag.equals(">=")){
			columnExp.comparetor = Constants.EXP_COMPARE_GET;
		}
	}
	| column_name2=ID  BETWEEN ll=(LPAREN|LPAREN_SQUARE) lv1=literal_value COMMA lv2=literal_value rr=(RPAREN|RPAREN_SQUARE)
	{
		ColumnExp columnExp = new ColumnExp();
		$expression = columnExp;
		FieldDesc fd =new FieldDesc();
		fd.fieldName = column_name2.getText().trim();
		columnExp.values = new ArrayList();
		columnExp.values.add(lv1.valueDesc);
		columnExp.values.add(lv2.valueDesc);
		columnExp.column = fd;
		String tagl = ll.getText().trim();
		String tagr = rr.getText().trim();
		if(tagl.equals("(")&&tagr.equals(")")){
			columnExp.comparetor = Constants.EXP_COMPARE_BETWEEN_LG;
		}else if(tagl.equals("(")&&tagr.equals("]")){
			columnExp.comparetor = Constants.EXP_COMPARE_BETWEEN_LGE;
		}else if(tagl.equals("[")&&tagr.equals("]")){
			columnExp.comparetor = Constants.EXP_COMPARE_BETWEEN_LEGE;
		}else if(tagl.equals("[")&&tagr.equals(")")){
			columnExp.comparetor = Constants.EXP_COMPARE_BETWEEN_LEG;
		}
	}
	;

eq_sequence returns [List<FieldValuePair> fieldValues = new ArrayList<FieldValuePair>()]
	: column_name = ID EQUALS lv = literal_value
	{
		FieldValuePair fvp = new FieldValuePair();
		FieldDesc fd = new FieldDesc();
		fd.fieldName = column_name.getText().trim();
		fvp.field = fd;
		fvp.value = lv.valueDesc;
		$fieldValues.add(fvp);
	}
	( COMMA column_name2 = ID EQUALS lv2 = literal_value
	{
		FieldValuePair fvp2 = new FieldValuePair();
		FieldDesc fd2 = new FieldDesc();
		fd2.fieldName = column_name2.getText().trim();
		fvp2.field = fd2;
		fvp2.value = lv2.valueDesc;
		$fieldValues.add(fvp2);
	}
	)
	*
	;

literal_value returns [ValueDesc valueDesc = new ValueDesc()]
	:
	( rs = INTEGER
	{
		String strTemp = rs.getText().trim();
		$valueDesc.valueDesc = strTemp;
	}
	| rs = FLOAT
	{
		String strTemp = rs.getText().trim();
			$valueDesc.valueDesc = strTemp;
	}
	| rs = STRING
	{
		String strTemp = rs.getText().trim();
		   $valueDesc.valueDesc = strTemp.substring(1, strTemp.length()-1);

	}
	| rs = QUESTION
	{
		String strTemp = rs.getText().trim();
			$valueDesc.valueDesc = strTemp;
		$valueDesc.parmIndex = pamerIndex.incrementAndGet();
	}
	)
	;

EQUALS:        '=';
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
BETWEEN	:B E T W E E N;
CURRENT_TIMESTAMP: C U R R E N T '_' T I M E S T A M P;
INDEXWHERE: I N D E X W H E R E;
UNIONHASH: U N I O N H A S H;
OR: O R;
SEGHINT	:S E G H I N T;
WHERE: W H E R E;

fragment STRING_ESCAPE_SINGLE: (BACKSLASH QUOTE_SINGLE);
fragment STRING_ESCAPE_DOUBLE: (BACKSLASH QUOTE_DOUBLE);
fragment STRING_CORE: ~(QUOTE_SINGLE | QUOTE_DOUBLE);
fragment STRING_CORE_SINGLE: ( STRING_CORE | QUOTE_DOUBLE | STRING_ESCAPE_SINGLE )*;
fragment STRING_CORE_DOUBLE: ( STRING_CORE | QUOTE_SINGLE | STRING_ESCAPE_DOUBLE )*;
fragment STRING_SINGLE: (QUOTE_SINGLE STRING_CORE_SINGLE QUOTE_SINGLE);
fragment STRING_DOUBLE: (QUOTE_DOUBLE STRING_CORE_DOUBLE QUOTE_DOUBLE);
STRING: (STRING_SINGLE | STRING_DOUBLE);

fragment ID_START: ('a'..'z'|'A'..'Z'|UNDERSCORE);
fragment ID_CORE: (ID_START|'0'..'9');
fragment ID_PLAIN: ID_START (ID_CORE)*;

ID : ID_PLAIN ;

//TCL_ID: ID_START (ID_START|'0'..'9'|'::')* (LPAREN ( options {greedy=false;} : . )* RPAREN)?;

INTEGER: ('0'..'9')+;
fragment FLOAT_EXP : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;
FLOAT
    : ('0'..'9')+ DOT ('0'..'9')* FLOAT_EXP?
    | DOT ('0'..'9')+ FLOAT_EXP?
    | ('0'..'9')+ FLOAT_EXP
    ;
BLOB: ('x'|'X') QUOTE_SINGLE ('0'..'9'|'a'..'f'|'A'..'F')+ QUOTE_SINGLE;

fragment COMMENT: '/*' ( options {greedy=false;} : . )* '*/';
fragment LINE_COMMENT: '--' ~('\n'|'\r')* ('\r'? '\n'|EOF);

WS: (' '|'\r'|'\t'|'\u000C'|'\n'|COMMENT|LINE_COMMENT) {$channel=HIDDEN;};

