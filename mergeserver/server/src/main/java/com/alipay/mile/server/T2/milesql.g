grammar milesql;

options {
	language = Java;
	output = AST;
	k = 4;
}
@header {
package com.alipay.mile.server.T2;
import com.alipay.mile.FieldDesc;
import com.alipay.mile.Expression;
import com.alipay.mile.Constants;
import com.alipay.mile.mileexception.SQLException;
import com.alipay.mile.server.query.*;
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


sql_stmt returns[Statement statement]
	: ssc=sql_stmt_core
	{
	$statement=ssc.statement;
	} 
	EOF!;

sql_stmt_core returns[Statement statement]
	: us=update_stmt
	{
	$statement=us.updateStatement;
	}
	| ds=delete_stmt
	{
	$statement=ds.deleteStatement;
	}
	| is=insert_stmt
	{
	$statement=is.insertStatement;
	}
	| es=export_stmt
	{
	$statement=es.exportStatement;
	}
	| sse=select_stmt_expr
	{
	$statement=sse.queryStatementExpression;
	}
;
select_stmt_expr returns[QueryStatementExpression queryStatementExpression]
	: use1=unionsection_expr
	{
	$queryStatementExpression=use1.queryStatementExpression;
	} 
	(UNIONS^ use2=unionsection_expr
	{
	$queryStatementExpression= $queryStatementExpression.unionSetExp(use2.queryStatementExpression);
	}
	)*
	;

unionsection_expr  returns[QueryStatementExpression queryStatementExpression]
	: ise1=intersection_expr
	{
	$queryStatementExpression=ise1.queryStatementExpression;
	}
	(INTERSECTION^ ise2=intersection_expr
	{
	$queryStatementExpression= $queryStatementExpression.intersectSetExp(ise2.queryStatementExpression);
	}
	)*
	;

intersection_expr returns[QueryStatementExpression queryStatementExpression]
	: sst=select_stmt
	{
	$queryStatementExpression=sst.queryStatement;
	} ;







select_stmt returns [QueryStatement queryStatement  = new QueryStatement()]
	: SELECT (ALL
	{
	$queryStatement.accessType = Constants.QT_COMMON_QUERY;
	}
	| DISTINCT
	{
	$queryStatement.accessType = Constants.QT_COMMON_DISTINCT;
	}
	)?
	res=result_column
	{
        $queryStatement.selectFields.add(res.fieldDesc);
	}
	 (COMMA res=result_column
	 {
	 $queryStatement.selectFields.add(res.fieldDesc);
	 }
	 )*
	 FROM table_name=ID {$queryStatement.tableName = table_name.getText().trim();}
	(DOCHINT doc_hint = dochint_expr {$queryStatement.dochint = doc_hint.dochint;})?
	(SEGHINT seg_hint=seghint_expr {$queryStatement.hint =seg_hint.hint;})?
	(INDEXWHERE  hash_Where=expr{$queryStatement.hashWhere = hash_Where.expression;})?
	(WHERE filterWhere=expr{$queryStatement.filterWhere = filterWhere.expression;})?
	(GROUP BY
		group_t=group_term
		{
		$queryStatement.groupByFields.add(group_t.fieldDesc);
		}
		(COMMA group_t=group_term
		{
		$queryStatement.groupByFields.add(group_t.fieldDesc);
		}
		)*
		(HAVING having_ex=having_expr{$queryStatement.having = having_ex.expression;})?
		(GROUPORDER BY
			gordert = gorder_term
			{
                        $queryStatement.groupOrderFields.add(gordert.orderDesc);
			}
			(COMMA gordert = gorder_term
			{
			$queryStatement.groupOrderFields.add(gordert.orderDesc);
			}
			)*
		)?
		(GLIMIT limit=INTEGER {$queryStatement.groupLimit = Integer.parseInt(limit.getText().trim());})?
		(GOFFSET offset=INTEGER {$queryStatement.groupOffset = Integer.parseInt(offset.getText().trim());})?

	)?
	(ORDER BY ordert=ordering_term
		{
                $queryStatement.orderFields.add(ordert.orderDesc);
		}
		(COMMA ordert=ordering_term
		{
		$queryStatement.orderFields.add(ordert.orderDesc);
		}
		)*
	)?
	(LIMIT limit=INTEGER {$queryStatement.limit = Integer.parseInt(limit.getText().trim());})?
	(OFFSET offset=INTEGER {$queryStatement.offset = Integer.parseInt(offset.getText().trim());})?

;

group_term returns[FieldDesc fieldDesc = new FieldDesc()]
	: column_name=(ID|INTEGER)
	{
	$fieldDesc.fieldName = column_name.getText().trim();
	}
	;

result_column returns[FieldDesc fieldDesc = new FieldDesc()]
  : ASTERISK
  {
  $fieldDesc.fieldName ="*";
  }
  |column_name=selectexpr
  {
  $fieldDesc.fieldName = column_name.columnname;
  $fieldDesc.refColumnName = column_name.refname;
  $fieldDesc.functionId= column_name.functionId;
  $fieldDesc.functionName= column_name.functionName;
  $fieldDesc.withinExpr = column_name.withinExpr;
  }
  (AS aliseName=(ID|INTEGER)
  {
  $fieldDesc.aliseName= aliseName.getText().trim();
  }
  )?
   ;


ordering_term
	returns[OrderDesc orderDesc = new OrderDesc()]
	: column_name=(ID|INTEGER)
	{
	FieldDesc fd = new FieldDesc();
	fd.fieldName = column_name.getText().trim();
	$orderDesc.field = fd;
	}
	(ascd=(ASC | DESC)
	{
	if(ascd.getText().toUpperCase().trim().equals("ASC")){
         $orderDesc.type = Constants.ORDER_TYPE_ASC;
        }else if(ascd.getText().toUpperCase().trim().equals("DESC")){
         $orderDesc.type = Constants.ORDER_TYPE_DESC;
        }
	}
	)?
;

gorder_term
	returns[OrderDesc orderDesc = new OrderDesc()]
	: column_name=(ID|INTEGER)
	{
	FieldDesc fd = new FieldDesc();
	fd.fieldName = column_name.getText().trim();
	$orderDesc.field = fd;
	}
	(ascd = (ASC | DESC)
	{
	if(ascd.getText().toUpperCase().trim().equals("ASC")){
         $orderDesc.type = Constants.ORDER_TYPE_ASC;
        }else if(ascd.getText().toUpperCase().trim().equals("DESC")){
         $orderDesc.type = Constants.ORDER_TYPE_DESC;
        }
	}
	)?
;

insert_stmt returns[InsertStatement insertStatement = new InsertStatement() ]
	: INSERT INTO table_name=ID
	{
	$insertStatement.tableName = table_name.getText().trim();
	}
	(column_name=(ID|INTEGER) EQUALS lv=literal_value
	{
	FieldValuePair fvp = new FieldValuePair();
	FieldDesc fd = new FieldDesc();
	fd.fieldName = column_name.getText().trim();
	fvp.field = fd;
	fvp.value = lv.valueDesc;
	$insertStatement.documentValue.add(fvp);
	}
	)*
	(WITH (WORDSEG LPAREN column_name=(ID|INTEGER) RPAREN EQUALS LPAREN lv3=literal_value
	{
	FieldValuePair fvp = new FieldValuePair();
	FieldDesc fd = new FieldDesc();
	fd.fieldName = "$" + column_name.getText().trim();
	fvp.field = fd;
	fvp.value = lv3.valueDesc;
	$insertStatement.documentValue.add(fvp);
	}
	(COMMA lv4=literal_value
	{
	FieldValuePair newFvp = new FieldValuePair();
	FieldDesc newFd = new FieldDesc();
	newFd.fieldName = "$" + column_name.getText().trim();
	newFvp.field = newFd;
	newFvp.value = lv4.valueDesc;
	$insertStatement.documentValue.add(newFvp);
	}
	)* RPAREN
	)*
	)?
;


export_stmt returns[ExportStatement exportStatement = new ExportStatement()]
	: EXPORT TO save_path=literal_value {
		$exportStatement.path = save_path.valueDesc;
	}
	FROM table_name=ID {$exportStatement.tableName = table_name.getText().trim(); }
	(SEGHINT seg_hint=seghint_expr {$exportStatement.hint = seg_hint.hint;} )?
	(INDEXWHERE hash_where=expr  {$exportStatement.hashWhere = hash_where.expression;} )?
	(WHERE filter_where=expr {$exportStatement.filterWhere = filter_where.expression;} )?
	(LIMIT limit=INTEGER {$exportStatement.limit = Long.parseLong(limit.getText().trim());})?
	;

delete_stmt returns[DeleteStatement deleteStatement = new DeleteStatement()]
	: DELETE FROM table_name=ID {$deleteStatement.tableName = table_name.getText().trim();}
	(SEGHINT seg_hint=seghint_expr {$deleteStatement.hint =seg_hint.hint;})?
	(INDEXWHERE  hash_Where=expr{$deleteStatement.hashWhere = hash_Where.expression;})?
	(WHERE filterWhere=expr{$deleteStatement.filterWhere = filterWhere.expression;})?
;

update_stmt returns[UpdateStatement updateStatement = new UpdateStatement()]
	: UPDATE table_name=ID {$updateStatement.tableName = table_name.getText().trim();}
	SET update_set [$updateStatement]
	(SEGHINT seg_hint=seghint_expr {$updateStatement.hint =seg_hint.hint;})?
	(INDEXWHERE  hash_Where=expr{$updateStatement.hashWhere = hash_Where.expression;})?
	(WHERE filterWhere=expr{$updateStatement.filterWhere = filterWhere.expression;})?
;

dochint_expr returns [DocHint dochint = new DocHint()]
	: doc_name=(ID|INTEGER) EQUALS doc_id = INTEGER
	{
        if(doc_name.getText().trim().toUpperCase().equals("mile_doc_id".trim().toUpperCase())){
        $dochint.docId = Long.parseLong(doc_id.getText().trim().toString());
        }
	}
;

update_set [UpdateStatement updateStatement]
	: column_name=(ID|INTEGER) EQUALS lv=literal_value
	{
	    FieldValuePair fvp = new FieldValuePair();
            FieldDesc fd  =  new FieldDesc();
            fd.fieldName = column_name.getText().toString();
            fvp.field = fd;
            fvp.value = lv.valueDesc;
            updateStatement.updateValue =fvp;
	}
;



seghint_expr returns[TimeHint hint= new TimeHint()]
	: LPAREN startCreateTime=(INTEGER|QUESTION) COMMA endCreateTime=(INTEGER|QUESTION) COMMA startUpdateTime=(INTEGER|QUESTION) COMMA endUpdateTime=(INTEGER|QUESTION) RPAREN
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
	$expression= $expression.orExp(or_sub.expression);
	}
	)*
	;
or_subexpr returns [Expression expression]
	: and_sub= and_subexpr
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


having_expr returns [Expression expression]
	: or_sub = having_or_subexpr
	{
	$expression=or_sub.expression;
	}
	(OR or_sub =having_or_subexpr
	{
	$expression= $expression.orExp(or_sub.expression);
	}
	)*
	;
having_or_subexpr returns [Expression expression]
	: and_sub= having_and_subexpr
	{
	$expression=and_sub.expression;
	}
	(AND and_sub=having_and_subexpr
	{
	$expression = $expression.andExp(and_sub.expression);
	}
	)*
	;
having_and_subexpr returns [Expression expression]
	: eq_sub=having_eq
	{
	$expression = eq_sub.columnExp;
	}
	| LPAREN expr_sub=having_expr RPAREN
	{
	$expression = expr_sub.expression;
	}
	;

selectexpr returns [String columnname,String refname, byte functionId ,String functionName, Expression withinExpr]
	: column_name= (ID|INTEGER)
	{
	$columnname = column_name.getText().trim();
	}
	|
	(
	funcname=  SUM LPAREN column_name=(ID|INTEGER) RPAREN
	{
	$columnname = "SUM ("+column_name.getText().trim()+")";
	$refname = column_name.getText().trim();
	$functionId = Constants.FUNC_SUM;
	$functionName = "SUM";
	}
	| funcname=  MAX  LPAREN column_name=(ID|INTEGER) RPAREN
	{
	$columnname ="MAX ("+column_name.getText().trim()+")";
	$refname = column_name.getText().trim();
	$functionId = Constants.FUNC_MAX;
	$functionName = "MAX";
	}
	| funcname=  MIN  LPAREN column_name=(ID|INTEGER) RPAREN
	{
	$columnname = "MIN ("+column_name.getText().trim()+")";
	$refname = column_name.getText().trim();
	$functionId = Constants.FUNC_MIN;
	$functionName = "MIN";
	}
	| funcname=  COUNT LPAREN column_name=(ID|INTEGER|ASTERISK) RPAREN
	{
	$columnname ="COUNT ("+column_name.getText().trim()+")";
	$refname = column_name.getText().trim();
	$functionId = Constants.FUNC_COUNT;
	$functionName = "COUNT";
	}
	| funcname=  AVG LPAREN column_name=(ID|INTEGER|ASTERISK) RPAREN
	{
	$columnname ="AVG ("+column_name.getText().trim()+")";
	$refname = column_name.getText().trim();
	$functionId = Constants.FUNC_AVG;
	$functionName = "AVG";
	}
	| funcname=  SQUARESUM LPAREN column_name=(ID|INTEGER|ASTERISK) RPAREN
	{
	$columnname ="SQUARESUM ("+column_name.getText().trim()+")";
	$refname = column_name.getText().trim();
	$functionId = Constants.FUNC_SQUARE_SUM;
	$functionName = "SQUARESUM";
	}
	| funcname=  VARIANCE LPAREN column_name=(ID|INTEGER|ASTERISK) RPAREN
	{
	$columnname ="VARIANCE ("+column_name.getText().trim()+")";
	$refname = column_name.getText().trim();
	$functionId = Constants.FUNC_VAR;
	$functionName = "VARIANCE";
	}
	| funcname=  STDDEV LPAREN column_name=(ID|INTEGER|ASTERISK) RPAREN
	{
	$columnname ="STDDEV ("+column_name.getText().trim()+")";
	$refname = column_name.getText().trim();
	$functionId = Constants.FUNC_STD;
	$functionName = "STDDEV";
	}	
	| funcname= COUNT LPAREN DISTINCT column_name=(ID|INTEGER) RPAREN
	{
	$columnname = "COUNT (DISTINCT "+column_name.getText().trim()+")";
	$refname = column_name.getText().trim();
	$functionId = Constants.FUNC_DISTINCT_COUNT;
	$functionName = "COUNT DISTINCT";
	}
	)
	(
	 WITHIN within_expr = expr
	 {
	 $withinExpr = within_expr.expression;
	 }
	)?
	;


eq_subexpr returns [Expression expression]
	: column_name= (ID|INTEGER) leq= ( LESS | LESS_OR_EQ | GREATER | GREATER_OR_EQ | EQUALS | NOT_EQUALS1 | NOT_EQUALS2) lv=literal_value
	{
	ColumnExp columnExp = new ColumnExp();
	$expression = columnExp;
	FieldDesc fd =new FieldDesc();
	fd.fieldName = column_name.getText().trim();
	columnExp.values = new ArrayList();
        columnExp.values.add(lv.valueDesc);
       	columnExp.column = fd;
	String tag= leq.getText().trim();
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
	}else if(tag.equals("<>")){
		columnExp.comparetor = Constants.EXP_COMPARE_NOT_EQUALS;
	}else if(tag.equals("!=")){
		columnExp.comparetor = Constants.EXP_COMPARE_NOT_EQUALS;
	}
	}
	| column_name2=(ID|INTEGER)  BETWEEN ll=(LPAREN|LPAREN_SQUARE) lv1=literal_value COMMA lv2=literal_value rr=(RPAREN|RPAREN_SQUARE)
	{
	ColumnExp columnExp = new ColumnExp();
	$expression = columnExp;
	FieldDesc fd =new FieldDesc();
	fd.fieldName = column_name2.getText().trim();
       	columnExp.values = new ArrayList();
        columnExp.values.add(lv1.valueDesc);
        columnExp.values.add(lv2.valueDesc);
	columnExp.column = fd;
	String tagl= ll.getText().trim();
	String tagr= rr.getText().trim();
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
	| column_name3=(ID|INTEGER)  IN LPAREN lv3=literal_value
	{
	ColumnExp columnExp = new ColumnExp();
	$expression = columnExp;
	FieldDesc fd =new FieldDesc();
	fd.fieldName = column_name3.getText().trim();
       	columnExp.values = new ArrayList();
        columnExp.values.add(lv3.valueDesc);
	columnExp.column = fd;
	columnExp.comparetor = Constants.EXP_COMPARE_IN;
	} 
	(COMMA lv4=literal_value 
	{
	columnExp.values.add(lv4.valueDesc);
	})* RPAREN
	| column_name5=(ID|INTEGER) MATCH LPAREN lv5=literal_value 
	{
	ColumnExp columnExp = new ColumnExp();
	$expression = columnExp;
	FieldDesc fd =new FieldDesc();
	fd.fieldName = "$" + column_name5.getText().trim();
    columnExp.values = new ArrayList();
    columnExp.values.add(lv5.valueDesc);
	columnExp.column = fd;
	columnExp.comparetor = Constants.EXP_COMPARE_MATCH;
	}
	(COMMA lv6=literal_value
	{
	columnExp.values.add(lv6.valueDesc);
	}
	)* RPAREN
	| column_name4=(ID|INTEGER)  IN LPAREN ss=select_stmt RPAREN
	{
	ColumnSubSelectExp columnSubSelectExp = new ColumnSubSelectExp();
	$expression = columnSubSelectExp;
	FieldDesc fd =new FieldDesc();
	fd.fieldName = column_name4.getText().trim();
       	columnSubSelectExp.subQueryStatement = ss.queryStatement;
	columnSubSelectExp.column = fd;
	columnSubSelectExp.comparetor = Constants.EXP_COMPARE_IN;
	}
	;

eq_sequence returns [List<FieldValuePair> fieldValues = new ArrayList<FieldValuePair>()]
	: column_name = (ID|INTEGER) EQUALS lv = literal_value
	{
		FieldValuePair fvp = new FieldValuePair();
		FieldDesc fd = new FieldDesc();
		fd.fieldName = column_name.getText().trim();
		fvp.field = fd;
		fvp.value = lv.valueDesc;
		$fieldValues.add(fvp);
	}
	( COMMA column_name2 = (ID|INTEGER) EQUALS lv2 = literal_value
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

having_eq returns [ColumnExp columnExp = new ColumnExp()]
	:  column_name=selectexpr leq= ( LESS | LESS_OR_EQ | GREATER | GREATER_OR_EQ | EQUALS | NOT_EQUALS1 | NOT_EQUALS2) lv=literal_value
	{
	FieldDesc fd =new FieldDesc();
	fd.fieldName = column_name.columnname;
	fd.aliseName = fd.fieldName;
  	fd.functionId= column_name.functionId;
 	fd.functionName= column_name.functionName;
 	fd.refColumnName = column_name.refname;
       	$columnExp.values = new ArrayList();
        $columnExp.values.add(lv.valueDesc);
	$columnExp.column = fd;
	String tag= leq.getText().trim();
        if(tag.equals("=")){
		$columnExp.comparetor = Constants.EXP_COMPARE_EQUALS;
	}else if(tag.equals("<")){
		$columnExp.comparetor = Constants.EXP_COMPARE_LT;
	}else if(tag.equals("<=")){
		$columnExp.comparetor = Constants.EXP_COMPARE_LET;
	}else if(tag.equals(">")){
		$columnExp.comparetor = Constants.EXP_COMPARE_GT;
	}else if(tag.equals(">=")){
		$columnExp.comparetor = Constants.EXP_COMPARE_GET;
	}else if(tag.equals("<>")){
		$columnExp.comparetor = Constants.EXP_COMPARE_NOT_EQUALS;
	}else if(tag.equals("!=")){
		$columnExp.comparetor = Constants.EXP_COMPARE_NOT_EQUALS;
	}
	}
	| column_name2=(ID|INTEGER)  BETWEEN ll=(LPAREN|LPAREN_SQUARE) lv1=literal_value COMMA lv2=literal_value rr=(RPAREN|RPAREN_SQUARE)
	{
	FieldDesc fd =new FieldDesc();
	fd.fieldName = column_name2.getText().trim();
       	$columnExp.values = new ArrayList();
        $columnExp.values.add(lv1.valueDesc);
        $columnExp.values.add(lv2.valueDesc);
	$columnExp.column = fd;
	String tagl= ll.getText().trim();
	String tagr= rr.getText().trim();
        if(tagl.equals("(")&&tagr.equals(")")){
		$columnExp.comparetor = Constants.EXP_COMPARE_BETWEEN_LG;
	}else if(tagl.equals("(")&&tagr.equals("]")){
		$columnExp.comparetor = Constants.EXP_COMPARE_BETWEEN_LGE;
	}else if(tagl.equals("[")&&tagr.equals("]")){
		$columnExp.comparetor = Constants.EXP_COMPARE_BETWEEN_LEGE;
	}else if(tagl.equals("[")&&tagr.equals(")")){
		$columnExp.comparetor = Constants.EXP_COMPARE_BETWEEN_LEG;
	}
	}
	| column_name3=(ID|INTEGER)  IN LPAREN lv3=literal_value (COMMA lv4=literal_value)* RPAREN
	{
	
	}
	| column_name4=(ID|INTEGER)  IN LPAREN ss=select_stmt RPAREN
	{
	
	}
	;

literal_value returns [ValueDesc valueDesc = new ValueDesc()]
	:(
	 rs= INTEGER
	 {
	 String strTemp = rs.getText().trim();
        $valueDesc.valueDesc = strTemp;

	 }
	 | rs=  FLOAT
	 {
	 String strTemp = rs.getText().trim();
        $valueDesc.valueDesc = strTemp;

	 }
	 | rs=  STRING
	 {
	 String strTemp = rs.getText().trim();
        $valueDesc.valueDesc = strTemp.substring(1, strTemp.length()-1);

	 }
	 | rs=  QUESTION
	 {
	 String strTemp = rs.getText().trim();
        $valueDesc.valueDesc = strTemp;
	$valueDesc.parmIndex = pamerIndex.incrementAndGet();
	 }
	 )
;

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
DOLLAR:		   '$';
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

ALL: A L L;
AND: A N D;
AS: A S;
ASC: A S C;
AVG: A V G;
BETWEEN	:B E T W E E N;
BY: B Y;
COUNT: C O U N T;
CURRENT_TIMESTAMP: C U R R E N T '_' T I M E S T A M P;
DELETE: D E L E T E;
DESC : D E S C ;
DISTINCT : D I S T I N C T;
DOCHINT	: D O C H I N T;
FROM: F R O M;
GLIMIT: G L I M I T ;
GOFFSET : G O F F S E T;
GRANGE 	: G R A N G E;
GROUP: G R O U P;
GROUPORDER: G R O U P O R D E R;
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
SEGHINT	:S E G H I N T;
SELECT: S E L E C T;
SET: S E T;
SQUARESUM: S Q U A R E S U M;
SUM: S U M;
STDDEV: S T D D E V;
UNIONS: U N I O N S;
UPDATE: U P D A T E;
VARIANCE: V A R I A N C E;
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
