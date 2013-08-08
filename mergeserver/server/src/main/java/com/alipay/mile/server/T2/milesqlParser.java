// $ANTLR 3.4 E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g 2012-11-05 14:27:05

package com.alipay.mile.server.T2;
import com.alipay.mile.FieldDesc;
import com.alipay.mile.Expression;
import com.alipay.mile.Constants;
import com.alipay.mile.mileexception.SQLException;
import com.alipay.mile.server.query.*;
import java.util.concurrent.atomic.AtomicInteger;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

import org.antlr.runtime.tree.*;


@SuppressWarnings({"all", "warnings", "unchecked"})
public class milesqlParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "A", "ALL", "AND", "AS", "ASC", "ASTERISK", "AVG", "B", "BACKSLASH", "BETWEEN", "BLOB", "BY", "C", "COMMA", "COMMENT", "COUNT", "CURRENT_TIMESTAMP", "D", "DELETE", "DESC", "DISTINCT", "DOCHINT", "DOLLAR", "DOT", "E", "EQUALS", "EXPORT", "F", "FLOAT", "FLOAT_EXP", "FROM", "G", "GLIMIT", "GOFFSET", "GRANGE", "GREATER", "GREATER_OR_EQ", "GROUP", "GROUPORDER", "H", "HAVING", "I", "ID", "ID_CORE", "ID_PLAIN", "ID_START", "IN", "INDEXWHERE", "INSERT", "INTEGER", "INTERSECTION", "INTO", "J", "K", "L", "LEFT", "LESS", "LESS_OR_EQ", "LIMIT", "LINE_COMMENT", "LPAREN", "LPAREN_SQUARE", "M", "MATCH", "MAX", "MIN", "N", "NOT_EQUALS1", "NOT_EQUALS2", "NULL", "O", "OFFSET", "OR", "ORDER", "P", "Q", "QUESTION", "QUOTE_DOUBLE", "QUOTE_SINGLE", "R", "RPAREN", "RPAREN_SQUARE", "S", "SEGHINT", "SELECT", "SEMI", "SET", "SQUARESUM", "STDDEV", "STRING", "STRING_CORE", "STRING_CORE_DOUBLE", "STRING_CORE_SINGLE", "STRING_DOUBLE", "STRING_ESCAPE_DOUBLE", "STRING_ESCAPE_SINGLE", "STRING_SINGLE", "SUM", "T", "TO", "U", "UNDERSCORE", "UNIONHASH", "UNIONS", "UPDATE", "V", "VARIANCE", "W", "WHERE", "WITH", "WITHIN", "WORDSEG", "WS", "X", "Y", "Z"
    };

    public static final int EOF=-1;
    public static final int A=4;
    public static final int ALL=5;
    public static final int AND=6;
    public static final int AS=7;
    public static final int ASC=8;
    public static final int ASTERISK=9;
    public static final int AVG=10;
    public static final int B=11;
    public static final int BACKSLASH=12;
    public static final int BETWEEN=13;
    public static final int BLOB=14;
    public static final int BY=15;
    public static final int C=16;
    public static final int COMMA=17;
    public static final int COMMENT=18;
    public static final int COUNT=19;
    public static final int CURRENT_TIMESTAMP=20;
    public static final int D=21;
    public static final int DELETE=22;
    public static final int DESC=23;
    public static final int DISTINCT=24;
    public static final int DOCHINT=25;
    public static final int DOLLAR=26;
    public static final int DOT=27;
    public static final int E=28;
    public static final int EQUALS=29;
    public static final int EXPORT=30;
    public static final int F=31;
    public static final int FLOAT=32;
    public static final int FLOAT_EXP=33;
    public static final int FROM=34;
    public static final int G=35;
    public static final int GLIMIT=36;
    public static final int GOFFSET=37;
    public static final int GRANGE=38;
    public static final int GREATER=39;
    public static final int GREATER_OR_EQ=40;
    public static final int GROUP=41;
    public static final int GROUPORDER=42;
    public static final int H=43;
    public static final int HAVING=44;
    public static final int I=45;
    public static final int ID=46;
    public static final int ID_CORE=47;
    public static final int ID_PLAIN=48;
    public static final int ID_START=49;
    public static final int IN=50;
    public static final int INDEXWHERE=51;
    public static final int INSERT=52;
    public static final int INTEGER=53;
    public static final int INTERSECTION=54;
    public static final int INTO=55;
    public static final int J=56;
    public static final int K=57;
    public static final int L=58;
    public static final int LEFT=59;
    public static final int LESS=60;
    public static final int LESS_OR_EQ=61;
    public static final int LIMIT=62;
    public static final int LINE_COMMENT=63;
    public static final int LPAREN=64;
    public static final int LPAREN_SQUARE=65;
    public static final int M=66;
    public static final int MATCH=67;
    public static final int MAX=68;
    public static final int MIN=69;
    public static final int N=70;
    public static final int NOT_EQUALS1=71;
    public static final int NOT_EQUALS2=72;
    public static final int NULL=73;
    public static final int O=74;
    public static final int OFFSET=75;
    public static final int OR=76;
    public static final int ORDER=77;
    public static final int P=78;
    public static final int Q=79;
    public static final int QUESTION=80;
    public static final int QUOTE_DOUBLE=81;
    public static final int QUOTE_SINGLE=82;
    public static final int R=83;
    public static final int RPAREN=84;
    public static final int RPAREN_SQUARE=85;
    public static final int S=86;
    public static final int SEGHINT=87;
    public static final int SELECT=88;
    public static final int SEMI=89;
    public static final int SET=90;
    public static final int SQUARESUM=91;
    public static final int STDDEV=92;
    public static final int STRING=93;
    public static final int STRING_CORE=94;
    public static final int STRING_CORE_DOUBLE=95;
    public static final int STRING_CORE_SINGLE=96;
    public static final int STRING_DOUBLE=97;
    public static final int STRING_ESCAPE_DOUBLE=98;
    public static final int STRING_ESCAPE_SINGLE=99;
    public static final int STRING_SINGLE=100;
    public static final int SUM=101;
    public static final int T=102;
    public static final int TO=103;
    public static final int U=104;
    public static final int UNDERSCORE=105;
    public static final int UNIONHASH=106;
    public static final int UNIONS=107;
    public static final int UPDATE=108;
    public static final int V=109;
    public static final int VARIANCE=110;
    public static final int W=111;
    public static final int WHERE=112;
    public static final int WITH=113;
    public static final int WITHIN=114;
    public static final int WORDSEG=115;
    public static final int WS=116;
    public static final int X=117;
    public static final int Y=118;
    public static final int Z=119;

    // delegates
    public Parser[] getDelegates() {
        return new Parser[] {};
    }

    // delegators


    public milesqlParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }
    public milesqlParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

protected TreeAdaptor adaptor = new CommonTreeAdaptor();

public void setTreeAdaptor(TreeAdaptor adaptor) {
    this.adaptor = adaptor;
}
public TreeAdaptor getTreeAdaptor() {
    return adaptor;
}
    public String[] getTokenNames() { return milesqlParser.tokenNames; }
    public String getGrammarFileName() { return "E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g"; }


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




    public static class sql_stmt_return extends ParserRuleReturnScope {
        public Statement statement;
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "sql_stmt"
    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:53:1: sql_stmt returns [Statement statement] : ssc= sql_stmt_core EOF !;
    public final milesqlParser.sql_stmt_return sql_stmt() throws RecognitionException {
        milesqlParser.sql_stmt_return retval = new milesqlParser.sql_stmt_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token EOF1=null;
        milesqlParser.sql_stmt_core_return ssc =null;


        Object EOF1_tree=null;

        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:54:2: (ssc= sql_stmt_core EOF !)
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:54:4: ssc= sql_stmt_core EOF !
            {
            root_0 = (Object)adaptor.nil();


            pushFollow(FOLLOW_sql_stmt_core_in_sql_stmt77);
            ssc=sql_stmt_core();

            state._fsp--;

            adaptor.addChild(root_0, ssc.getTree());


            	retval.statement =ssc.statement;
            	

            EOF1=(Token)match(input,EOF,FOLLOW_EOF_in_sql_stmt84); 

            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

        catch (RecognitionException el) {
          throw el;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "sql_stmt"


    public static class sql_stmt_core_return extends ParserRuleReturnScope {
        public Statement statement;
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "sql_stmt_core"
    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:60:1: sql_stmt_core returns [Statement statement] : (us= update_stmt |ds= delete_stmt |is= insert_stmt |es= export_stmt |sse= select_stmt_expr );
    public final milesqlParser.sql_stmt_core_return sql_stmt_core() throws RecognitionException {
        milesqlParser.sql_stmt_core_return retval = new milesqlParser.sql_stmt_core_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        milesqlParser.update_stmt_return us =null;

        milesqlParser.delete_stmt_return ds =null;

        milesqlParser.insert_stmt_return is =null;

        milesqlParser.export_stmt_return es =null;

        milesqlParser.select_stmt_expr_return sse =null;



        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:61:2: (us= update_stmt |ds= delete_stmt |is= insert_stmt |es= export_stmt |sse= select_stmt_expr )
            int alt1=5;
            switch ( input.LA(1) ) {
            case UPDATE:
                {
                alt1=1;
                }
                break;
            case DELETE:
                {
                alt1=2;
                }
                break;
            case INSERT:
                {
                alt1=3;
                }
                break;
            case EXPORT:
                {
                alt1=4;
                }
                break;
            case SELECT:
                {
                alt1=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;

            }

            switch (alt1) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:61:4: us= update_stmt
                    {
                    root_0 = (Object)adaptor.nil();


                    pushFollow(FOLLOW_update_stmt_in_sql_stmt_core99);
                    us=update_stmt();

                    state._fsp--;

                    adaptor.addChild(root_0, us.getTree());


                    	retval.statement =us.updateStatement;
                    	

                    }
                    break;
                case 2 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:65:4: ds= delete_stmt
                    {
                    root_0 = (Object)adaptor.nil();


                    pushFollow(FOLLOW_delete_stmt_in_sql_stmt_core109);
                    ds=delete_stmt();

                    state._fsp--;

                    adaptor.addChild(root_0, ds.getTree());


                    	retval.statement =ds.deleteStatement;
                    	

                    }
                    break;
                case 3 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:69:4: is= insert_stmt
                    {
                    root_0 = (Object)adaptor.nil();


                    pushFollow(FOLLOW_insert_stmt_in_sql_stmt_core119);
                    is=insert_stmt();

                    state._fsp--;

                    adaptor.addChild(root_0, is.getTree());


                    	retval.statement =is.insertStatement;
                    	

                    }
                    break;
                case 4 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:73:4: es= export_stmt
                    {
                    root_0 = (Object)adaptor.nil();


                    pushFollow(FOLLOW_export_stmt_in_sql_stmt_core129);
                    es=export_stmt();

                    state._fsp--;

                    adaptor.addChild(root_0, es.getTree());


                    	retval.statement =es.exportStatement;
                    	

                    }
                    break;
                case 5 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:77:4: sse= select_stmt_expr
                    {
                    root_0 = (Object)adaptor.nil();


                    pushFollow(FOLLOW_select_stmt_expr_in_sql_stmt_core139);
                    sse=select_stmt_expr();

                    state._fsp--;

                    adaptor.addChild(root_0, sse.getTree());


                    	retval.statement =sse.queryStatementExpression;
                    	

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

        catch (RecognitionException el) {
          throw el;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "sql_stmt_core"


    public static class select_stmt_expr_return extends ParserRuleReturnScope {
        public QueryStatementExpression queryStatementExpression;
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "select_stmt_expr"
    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:82:1: select_stmt_expr returns [QueryStatementExpression queryStatementExpression] : use1= unionsection_expr ( UNIONS ^use2= unionsection_expr )* ;
    public final milesqlParser.select_stmt_expr_return select_stmt_expr() throws RecognitionException {
        milesqlParser.select_stmt_expr_return retval = new milesqlParser.select_stmt_expr_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token UNIONS2=null;
        milesqlParser.unionsection_expr_return use1 =null;

        milesqlParser.unionsection_expr_return use2 =null;


        Object UNIONS2_tree=null;

        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:83:2: (use1= unionsection_expr ( UNIONS ^use2= unionsection_expr )* )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:83:4: use1= unionsection_expr ( UNIONS ^use2= unionsection_expr )*
            {
            root_0 = (Object)adaptor.nil();


            pushFollow(FOLLOW_unionsection_expr_in_select_stmt_expr156);
            use1=unionsection_expr();

            state._fsp--;

            adaptor.addChild(root_0, use1.getTree());


            	retval.queryStatementExpression =use1.queryStatementExpression;
            	

            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:87:2: ( UNIONS ^use2= unionsection_expr )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==UNIONS) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:87:3: UNIONS ^use2= unionsection_expr
            	    {
            	    UNIONS2=(Token)match(input,UNIONS,FOLLOW_UNIONS_in_select_stmt_expr164); 
            	    UNIONS2_tree = 
            	    (Object)adaptor.create(UNIONS2)
            	    ;
            	    root_0 = (Object)adaptor.becomeRoot(UNIONS2_tree, root_0);


            	    pushFollow(FOLLOW_unionsection_expr_in_select_stmt_expr169);
            	    use2=unionsection_expr();

            	    state._fsp--;

            	    adaptor.addChild(root_0, use2.getTree());


            	    	retval.queryStatementExpression = retval.queryStatementExpression.unionSetExp(use2.queryStatementExpression);
            	    	

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

        catch (RecognitionException el) {
          throw el;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "select_stmt_expr"


    public static class unionsection_expr_return extends ParserRuleReturnScope {
        public QueryStatementExpression queryStatementExpression;
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "unionsection_expr"
    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:94:1: unionsection_expr returns [QueryStatementExpression queryStatementExpression] : ise1= intersection_expr ( INTERSECTION ^ise2= intersection_expr )* ;
    public final milesqlParser.unionsection_expr_return unionsection_expr() throws RecognitionException {
        milesqlParser.unionsection_expr_return retval = new milesqlParser.unionsection_expr_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token INTERSECTION3=null;
        milesqlParser.intersection_expr_return ise1 =null;

        milesqlParser.intersection_expr_return ise2 =null;


        Object INTERSECTION3_tree=null;

        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:95:2: (ise1= intersection_expr ( INTERSECTION ^ise2= intersection_expr )* )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:95:4: ise1= intersection_expr ( INTERSECTION ^ise2= intersection_expr )*
            {
            root_0 = (Object)adaptor.nil();


            pushFollow(FOLLOW_intersection_expr_in_unionsection_expr193);
            ise1=intersection_expr();

            state._fsp--;

            adaptor.addChild(root_0, ise1.getTree());


            	retval.queryStatementExpression =ise1.queryStatementExpression;
            	

            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:99:2: ( INTERSECTION ^ise2= intersection_expr )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==INTERSECTION) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:99:3: INTERSECTION ^ise2= intersection_expr
            	    {
            	    INTERSECTION3=(Token)match(input,INTERSECTION,FOLLOW_INTERSECTION_in_unionsection_expr200); 
            	    INTERSECTION3_tree = 
            	    (Object)adaptor.create(INTERSECTION3)
            	    ;
            	    root_0 = (Object)adaptor.becomeRoot(INTERSECTION3_tree, root_0);


            	    pushFollow(FOLLOW_intersection_expr_in_unionsection_expr205);
            	    ise2=intersection_expr();

            	    state._fsp--;

            	    adaptor.addChild(root_0, ise2.getTree());


            	    	retval.queryStatementExpression = retval.queryStatementExpression.intersectSetExp(ise2.queryStatementExpression);
            	    	

            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

        catch (RecognitionException el) {
          throw el;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "unionsection_expr"


    public static class intersection_expr_return extends ParserRuleReturnScope {
        public QueryStatementExpression queryStatementExpression;
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "intersection_expr"
    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:106:1: intersection_expr returns [QueryStatementExpression queryStatementExpression] : sst= select_stmt ;
    public final milesqlParser.intersection_expr_return intersection_expr() throws RecognitionException {
        milesqlParser.intersection_expr_return retval = new milesqlParser.intersection_expr_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        milesqlParser.select_stmt_return sst =null;



        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:107:2: (sst= select_stmt )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:107:4: sst= select_stmt
            {
            root_0 = (Object)adaptor.nil();


            pushFollow(FOLLOW_select_stmt_in_intersection_expr228);
            sst=select_stmt();

            state._fsp--;

            adaptor.addChild(root_0, sst.getTree());


            	retval.queryStatementExpression =sst.queryStatement;
            	

            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

        catch (RecognitionException el) {
          throw el;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "intersection_expr"


    public static class select_stmt_return extends ParserRuleReturnScope {
        public QueryStatement queryStatement  = new QueryStatement();
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "select_stmt"
    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:118:1: select_stmt returns [QueryStatement queryStatement = new QueryStatement()] : SELECT ( ALL | DISTINCT )? res= result_column ( COMMA res= result_column )* FROM table_name= ID ( DOCHINT doc_hint= dochint_expr )? ( SEGHINT seg_hint= seghint_expr )? ( INDEXWHERE hash_Where= expr )? ( WHERE filterWhere= expr )? ( GROUP BY group_t= group_term ( COMMA group_t= group_term )* ( HAVING having_ex= having_expr )? ( GROUPORDER BY gordert= gorder_term ( COMMA gordert= gorder_term )* )? ( GLIMIT limit= INTEGER )? ( GOFFSET offset= INTEGER )? )? ( ORDER BY ordert= ordering_term ( COMMA ordert= ordering_term )* )? ( LIMIT limit= INTEGER )? ( OFFSET offset= INTEGER )? ;
    public final milesqlParser.select_stmt_return select_stmt() throws RecognitionException {
        milesqlParser.select_stmt_return retval = new milesqlParser.select_stmt_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token table_name=null;
        Token limit=null;
        Token offset=null;
        Token SELECT4=null;
        Token ALL5=null;
        Token DISTINCT6=null;
        Token COMMA7=null;
        Token FROM8=null;
        Token DOCHINT9=null;
        Token SEGHINT10=null;
        Token INDEXWHERE11=null;
        Token WHERE12=null;
        Token GROUP13=null;
        Token BY14=null;
        Token COMMA15=null;
        Token HAVING16=null;
        Token GROUPORDER17=null;
        Token BY18=null;
        Token COMMA19=null;
        Token GLIMIT20=null;
        Token GOFFSET21=null;
        Token ORDER22=null;
        Token BY23=null;
        Token COMMA24=null;
        Token LIMIT25=null;
        Token OFFSET26=null;
        milesqlParser.result_column_return res =null;

        milesqlParser.dochint_expr_return doc_hint =null;

        milesqlParser.seghint_expr_return seg_hint =null;

        milesqlParser.expr_return hash_Where =null;

        milesqlParser.expr_return filterWhere =null;

        milesqlParser.group_term_return group_t =null;

        milesqlParser.having_expr_return having_ex =null;

        milesqlParser.gorder_term_return gordert =null;

        milesqlParser.ordering_term_return ordert =null;


        Object table_name_tree=null;
        Object limit_tree=null;
        Object offset_tree=null;
        Object SELECT4_tree=null;
        Object ALL5_tree=null;
        Object DISTINCT6_tree=null;
        Object COMMA7_tree=null;
        Object FROM8_tree=null;
        Object DOCHINT9_tree=null;
        Object SEGHINT10_tree=null;
        Object INDEXWHERE11_tree=null;
        Object WHERE12_tree=null;
        Object GROUP13_tree=null;
        Object BY14_tree=null;
        Object COMMA15_tree=null;
        Object HAVING16_tree=null;
        Object GROUPORDER17_tree=null;
        Object BY18_tree=null;
        Object COMMA19_tree=null;
        Object GLIMIT20_tree=null;
        Object GOFFSET21_tree=null;
        Object ORDER22_tree=null;
        Object BY23_tree=null;
        Object COMMA24_tree=null;
        Object LIMIT25_tree=null;
        Object OFFSET26_tree=null;

        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:119:2: ( SELECT ( ALL | DISTINCT )? res= result_column ( COMMA res= result_column )* FROM table_name= ID ( DOCHINT doc_hint= dochint_expr )? ( SEGHINT seg_hint= seghint_expr )? ( INDEXWHERE hash_Where= expr )? ( WHERE filterWhere= expr )? ( GROUP BY group_t= group_term ( COMMA group_t= group_term )* ( HAVING having_ex= having_expr )? ( GROUPORDER BY gordert= gorder_term ( COMMA gordert= gorder_term )* )? ( GLIMIT limit= INTEGER )? ( GOFFSET offset= INTEGER )? )? ( ORDER BY ordert= ordering_term ( COMMA ordert= ordering_term )* )? ( LIMIT limit= INTEGER )? ( OFFSET offset= INTEGER )? )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:119:4: SELECT ( ALL | DISTINCT )? res= result_column ( COMMA res= result_column )* FROM table_name= ID ( DOCHINT doc_hint= dochint_expr )? ( SEGHINT seg_hint= seghint_expr )? ( INDEXWHERE hash_Where= expr )? ( WHERE filterWhere= expr )? ( GROUP BY group_t= group_term ( COMMA group_t= group_term )* ( HAVING having_ex= having_expr )? ( GROUPORDER BY gordert= gorder_term ( COMMA gordert= gorder_term )* )? ( GLIMIT limit= INTEGER )? ( GOFFSET offset= INTEGER )? )? ( ORDER BY ordert= ordering_term ( COMMA ordert= ordering_term )* )? ( LIMIT limit= INTEGER )? ( OFFSET offset= INTEGER )?
            {
            root_0 = (Object)adaptor.nil();


            SELECT4=(Token)match(input,SELECT,FOLLOW_SELECT_in_select_stmt251); 
            SELECT4_tree = 
            (Object)adaptor.create(SELECT4)
            ;
            adaptor.addChild(root_0, SELECT4_tree);


            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:119:11: ( ALL | DISTINCT )?
            int alt4=3;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==ALL) ) {
                alt4=1;
            }
            else if ( (LA4_0==DISTINCT) ) {
                alt4=2;
            }
            switch (alt4) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:119:12: ALL
                    {
                    ALL5=(Token)match(input,ALL,FOLLOW_ALL_in_select_stmt254); 
                    ALL5_tree = 
                    (Object)adaptor.create(ALL5)
                    ;
                    adaptor.addChild(root_0, ALL5_tree);



                    	retval.queryStatement.accessType = Constants.QT_COMMON_QUERY;
                    	

                    }
                    break;
                case 2 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:123:4: DISTINCT
                    {
                    DISTINCT6=(Token)match(input,DISTINCT,FOLLOW_DISTINCT_in_select_stmt262); 
                    DISTINCT6_tree = 
                    (Object)adaptor.create(DISTINCT6)
                    ;
                    adaptor.addChild(root_0, DISTINCT6_tree);



                    	retval.queryStatement.accessType = Constants.QT_COMMON_DISTINCT;
                    	

                    }
                    break;

            }


            pushFollow(FOLLOW_result_column_in_select_stmt274);
            res=result_column();

            state._fsp--;

            adaptor.addChild(root_0, res.getTree());


                    retval.queryStatement.selectFields.add(res.fieldDesc);
            	

            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:132:3: ( COMMA res= result_column )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==COMMA) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:132:4: COMMA res= result_column
            	    {
            	    COMMA7=(Token)match(input,COMMA,FOLLOW_COMMA_in_select_stmt282); 
            	    COMMA7_tree = 
            	    (Object)adaptor.create(COMMA7)
            	    ;
            	    adaptor.addChild(root_0, COMMA7_tree);


            	    pushFollow(FOLLOW_result_column_in_select_stmt286);
            	    res=result_column();

            	    state._fsp--;

            	    adaptor.addChild(root_0, res.getTree());


            	    	 retval.queryStatement.selectFields.add(res.fieldDesc);
            	    	 

            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);


            FROM8=(Token)match(input,FROM,FOLLOW_FROM_in_select_stmt299); 
            FROM8_tree = 
            (Object)adaptor.create(FROM8)
            ;
            adaptor.addChild(root_0, FROM8_tree);


            table_name=(Token)match(input,ID,FOLLOW_ID_in_select_stmt303); 
            table_name_tree = 
            (Object)adaptor.create(table_name)
            ;
            adaptor.addChild(root_0, table_name_tree);


            retval.queryStatement.tableName = table_name.getText().trim();

            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:138:2: ( DOCHINT doc_hint= dochint_expr )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==DOCHINT) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:138:3: DOCHINT doc_hint= dochint_expr
                    {
                    DOCHINT9=(Token)match(input,DOCHINT,FOLLOW_DOCHINT_in_select_stmt309); 
                    DOCHINT9_tree = 
                    (Object)adaptor.create(DOCHINT9)
                    ;
                    adaptor.addChild(root_0, DOCHINT9_tree);


                    pushFollow(FOLLOW_dochint_expr_in_select_stmt315);
                    doc_hint=dochint_expr();

                    state._fsp--;

                    adaptor.addChild(root_0, doc_hint.getTree());

                    retval.queryStatement.dochint = doc_hint.dochint;

                    }
                    break;

            }


            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:139:2: ( SEGHINT seg_hint= seghint_expr )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==SEGHINT) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:139:3: SEGHINT seg_hint= seghint_expr
                    {
                    SEGHINT10=(Token)match(input,SEGHINT,FOLLOW_SEGHINT_in_select_stmt323); 
                    SEGHINT10_tree = 
                    (Object)adaptor.create(SEGHINT10)
                    ;
                    adaptor.addChild(root_0, SEGHINT10_tree);


                    pushFollow(FOLLOW_seghint_expr_in_select_stmt327);
                    seg_hint=seghint_expr();

                    state._fsp--;

                    adaptor.addChild(root_0, seg_hint.getTree());

                    retval.queryStatement.hint =seg_hint.hint;

                    }
                    break;

            }


            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:140:2: ( INDEXWHERE hash_Where= expr )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==INDEXWHERE) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:140:3: INDEXWHERE hash_Where= expr
                    {
                    INDEXWHERE11=(Token)match(input,INDEXWHERE,FOLLOW_INDEXWHERE_in_select_stmt335); 
                    INDEXWHERE11_tree = 
                    (Object)adaptor.create(INDEXWHERE11)
                    ;
                    adaptor.addChild(root_0, INDEXWHERE11_tree);


                    pushFollow(FOLLOW_expr_in_select_stmt340);
                    hash_Where=expr();

                    state._fsp--;

                    adaptor.addChild(root_0, hash_Where.getTree());

                    retval.queryStatement.hashWhere = hash_Where.expression;

                    }
                    break;

            }


            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:141:2: ( WHERE filterWhere= expr )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==WHERE) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:141:3: WHERE filterWhere= expr
                    {
                    WHERE12=(Token)match(input,WHERE,FOLLOW_WHERE_in_select_stmt347); 
                    WHERE12_tree = 
                    (Object)adaptor.create(WHERE12)
                    ;
                    adaptor.addChild(root_0, WHERE12_tree);


                    pushFollow(FOLLOW_expr_in_select_stmt351);
                    filterWhere=expr();

                    state._fsp--;

                    adaptor.addChild(root_0, filterWhere.getTree());

                    retval.queryStatement.filterWhere = filterWhere.expression;

                    }
                    break;

            }


            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:142:2: ( GROUP BY group_t= group_term ( COMMA group_t= group_term )* ( HAVING having_ex= having_expr )? ( GROUPORDER BY gordert= gorder_term ( COMMA gordert= gorder_term )* )? ( GLIMIT limit= INTEGER )? ( GOFFSET offset= INTEGER )? )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==GROUP) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:142:3: GROUP BY group_t= group_term ( COMMA group_t= group_term )* ( HAVING having_ex= having_expr )? ( GROUPORDER BY gordert= gorder_term ( COMMA gordert= gorder_term )* )? ( GLIMIT limit= INTEGER )? ( GOFFSET offset= INTEGER )?
                    {
                    GROUP13=(Token)match(input,GROUP,FOLLOW_GROUP_in_select_stmt358); 
                    GROUP13_tree = 
                    (Object)adaptor.create(GROUP13)
                    ;
                    adaptor.addChild(root_0, GROUP13_tree);


                    BY14=(Token)match(input,BY,FOLLOW_BY_in_select_stmt360); 
                    BY14_tree = 
                    (Object)adaptor.create(BY14)
                    ;
                    adaptor.addChild(root_0, BY14_tree);


                    pushFollow(FOLLOW_group_term_in_select_stmt366);
                    group_t=group_term();

                    state._fsp--;

                    adaptor.addChild(root_0, group_t.getTree());


                    		retval.queryStatement.groupByFields.add(group_t.fieldDesc);
                    		

                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:147:3: ( COMMA group_t= group_term )*
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);

                        if ( (LA10_0==COMMA) ) {
                            alt10=1;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:147:4: COMMA group_t= group_term
                    	    {
                    	    COMMA15=(Token)match(input,COMMA,FOLLOW_COMMA_in_select_stmt375); 
                    	    COMMA15_tree = 
                    	    (Object)adaptor.create(COMMA15)
                    	    ;
                    	    adaptor.addChild(root_0, COMMA15_tree);


                    	    pushFollow(FOLLOW_group_term_in_select_stmt379);
                    	    group_t=group_term();

                    	    state._fsp--;

                    	    adaptor.addChild(root_0, group_t.getTree());


                    	    		retval.queryStatement.groupByFields.add(group_t.fieldDesc);
                    	    		

                    	    }
                    	    break;

                    	default :
                    	    break loop10;
                        }
                    } while (true);


                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:152:3: ( HAVING having_ex= having_expr )?
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0==HAVING) ) {
                        alt11=1;
                    }
                    switch (alt11) {
                        case 1 :
                            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:152:4: HAVING having_ex= having_expr
                            {
                            HAVING16=(Token)match(input,HAVING,FOLLOW_HAVING_in_select_stmt393); 
                            HAVING16_tree = 
                            (Object)adaptor.create(HAVING16)
                            ;
                            adaptor.addChild(root_0, HAVING16_tree);


                            pushFollow(FOLLOW_having_expr_in_select_stmt397);
                            having_ex=having_expr();

                            state._fsp--;

                            adaptor.addChild(root_0, having_ex.getTree());

                            retval.queryStatement.having = having_ex.expression;

                            }
                            break;

                    }


                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:153:3: ( GROUPORDER BY gordert= gorder_term ( COMMA gordert= gorder_term )* )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);

                    if ( (LA13_0==GROUPORDER) ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:153:4: GROUPORDER BY gordert= gorder_term ( COMMA gordert= gorder_term )*
                            {
                            GROUPORDER17=(Token)match(input,GROUPORDER,FOLLOW_GROUPORDER_in_select_stmt405); 
                            GROUPORDER17_tree = 
                            (Object)adaptor.create(GROUPORDER17)
                            ;
                            adaptor.addChild(root_0, GROUPORDER17_tree);


                            BY18=(Token)match(input,BY,FOLLOW_BY_in_select_stmt407); 
                            BY18_tree = 
                            (Object)adaptor.create(BY18)
                            ;
                            adaptor.addChild(root_0, BY18_tree);


                            pushFollow(FOLLOW_gorder_term_in_select_stmt416);
                            gordert=gorder_term();

                            state._fsp--;

                            adaptor.addChild(root_0, gordert.getTree());


                                                    retval.queryStatement.groupOrderFields.add(gordert.orderDesc);
                            			

                            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:158:4: ( COMMA gordert= gorder_term )*
                            loop12:
                            do {
                                int alt12=2;
                                int LA12_0 = input.LA(1);

                                if ( (LA12_0==COMMA) ) {
                                    alt12=1;
                                }


                                switch (alt12) {
                            	case 1 :
                            	    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:158:5: COMMA gordert= gorder_term
                            	    {
                            	    COMMA19=(Token)match(input,COMMA,FOLLOW_COMMA_in_select_stmt427); 
                            	    COMMA19_tree = 
                            	    (Object)adaptor.create(COMMA19)
                            	    ;
                            	    adaptor.addChild(root_0, COMMA19_tree);


                            	    pushFollow(FOLLOW_gorder_term_in_select_stmt433);
                            	    gordert=gorder_term();

                            	    state._fsp--;

                            	    adaptor.addChild(root_0, gordert.getTree());


                            	    			retval.queryStatement.groupOrderFields.add(gordert.orderDesc);
                            	    			

                            	    }
                            	    break;

                            	default :
                            	    break loop12;
                                }
                            } while (true);


                            }
                            break;

                    }


                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:164:3: ( GLIMIT limit= INTEGER )?
                    int alt14=2;
                    int LA14_0 = input.LA(1);

                    if ( (LA14_0==GLIMIT) ) {
                        alt14=1;
                    }
                    switch (alt14) {
                        case 1 :
                            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:164:4: GLIMIT limit= INTEGER
                            {
                            GLIMIT20=(Token)match(input,GLIMIT,FOLLOW_GLIMIT_in_select_stmt454); 
                            GLIMIT20_tree = 
                            (Object)adaptor.create(GLIMIT20)
                            ;
                            adaptor.addChild(root_0, GLIMIT20_tree);


                            limit=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_select_stmt458); 
                            limit_tree = 
                            (Object)adaptor.create(limit)
                            ;
                            adaptor.addChild(root_0, limit_tree);


                            retval.queryStatement.groupLimit = Integer.parseInt(limit.getText().trim());

                            }
                            break;

                    }


                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:165:3: ( GOFFSET offset= INTEGER )?
                    int alt15=2;
                    int LA15_0 = input.LA(1);

                    if ( (LA15_0==GOFFSET) ) {
                        alt15=1;
                    }
                    switch (alt15) {
                        case 1 :
                            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:165:4: GOFFSET offset= INTEGER
                            {
                            GOFFSET21=(Token)match(input,GOFFSET,FOLLOW_GOFFSET_in_select_stmt467); 
                            GOFFSET21_tree = 
                            (Object)adaptor.create(GOFFSET21)
                            ;
                            adaptor.addChild(root_0, GOFFSET21_tree);


                            offset=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_select_stmt471); 
                            offset_tree = 
                            (Object)adaptor.create(offset)
                            ;
                            adaptor.addChild(root_0, offset_tree);


                            retval.queryStatement.groupOffset = Integer.parseInt(offset.getText().trim());

                            }
                            break;

                    }


                    }
                    break;

            }


            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:168:2: ( ORDER BY ordert= ordering_term ( COMMA ordert= ordering_term )* )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==ORDER) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:168:3: ORDER BY ordert= ordering_term ( COMMA ordert= ordering_term )*
                    {
                    ORDER22=(Token)match(input,ORDER,FOLLOW_ORDER_in_select_stmt484); 
                    ORDER22_tree = 
                    (Object)adaptor.create(ORDER22)
                    ;
                    adaptor.addChild(root_0, ORDER22_tree);


                    BY23=(Token)match(input,BY,FOLLOW_BY_in_select_stmt486); 
                    BY23_tree = 
                    (Object)adaptor.create(BY23)
                    ;
                    adaptor.addChild(root_0, BY23_tree);


                    pushFollow(FOLLOW_ordering_term_in_select_stmt490);
                    ordert=ordering_term();

                    state._fsp--;

                    adaptor.addChild(root_0, ordert.getTree());


                                    retval.queryStatement.orderFields.add(ordert.orderDesc);
                    		

                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:172:3: ( COMMA ordert= ordering_term )*
                    loop17:
                    do {
                        int alt17=2;
                        int LA17_0 = input.LA(1);

                        if ( (LA17_0==COMMA) ) {
                            alt17=1;
                        }


                        switch (alt17) {
                    	case 1 :
                    	    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:172:4: COMMA ordert= ordering_term
                    	    {
                    	    COMMA24=(Token)match(input,COMMA,FOLLOW_COMMA_in_select_stmt499); 
                    	    COMMA24_tree = 
                    	    (Object)adaptor.create(COMMA24)
                    	    ;
                    	    adaptor.addChild(root_0, COMMA24_tree);


                    	    pushFollow(FOLLOW_ordering_term_in_select_stmt503);
                    	    ordert=ordering_term();

                    	    state._fsp--;

                    	    adaptor.addChild(root_0, ordert.getTree());


                    	    		retval.queryStatement.orderFields.add(ordert.orderDesc);
                    	    		

                    	    }
                    	    break;

                    	default :
                    	    break loop17;
                        }
                    } while (true);


                    }
                    break;

            }


            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:178:2: ( LIMIT limit= INTEGER )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==LIMIT) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:178:3: LIMIT limit= INTEGER
                    {
                    LIMIT25=(Token)match(input,LIMIT,FOLLOW_LIMIT_in_select_stmt520); 
                    LIMIT25_tree = 
                    (Object)adaptor.create(LIMIT25)
                    ;
                    adaptor.addChild(root_0, LIMIT25_tree);


                    limit=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_select_stmt524); 
                    limit_tree = 
                    (Object)adaptor.create(limit)
                    ;
                    adaptor.addChild(root_0, limit_tree);


                    retval.queryStatement.limit = Integer.parseInt(limit.getText().trim());

                    }
                    break;

            }


            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:179:2: ( OFFSET offset= INTEGER )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==OFFSET) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:179:3: OFFSET offset= INTEGER
                    {
                    OFFSET26=(Token)match(input,OFFSET,FOLLOW_OFFSET_in_select_stmt532); 
                    OFFSET26_tree = 
                    (Object)adaptor.create(OFFSET26)
                    ;
                    adaptor.addChild(root_0, OFFSET26_tree);


                    offset=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_select_stmt536); 
                    offset_tree = 
                    (Object)adaptor.create(offset)
                    ;
                    adaptor.addChild(root_0, offset_tree);


                    retval.queryStatement.offset = Integer.parseInt(offset.getText().trim());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

        catch (RecognitionException el) {
          throw el;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "select_stmt"


    public static class group_term_return extends ParserRuleReturnScope {
        public FieldDesc fieldDesc = new FieldDesc();
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "group_term"
    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:183:1: group_term returns [FieldDesc fieldDesc = new FieldDesc()] : column_name= ( ID | INTEGER ) ;
    public final milesqlParser.group_term_return group_term() throws RecognitionException {
        milesqlParser.group_term_return retval = new milesqlParser.group_term_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token column_name=null;

        Object column_name_tree=null;

        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:184:2: (column_name= ( ID | INTEGER ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:184:4: column_name= ( ID | INTEGER )
            {
            root_0 = (Object)adaptor.nil();


            column_name=(Token)input.LT(1);

            if ( input.LA(1)==ID||input.LA(1)==INTEGER ) {
                input.consume();
                adaptor.addChild(root_0, 
                (Object)adaptor.create(column_name)
                );
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }



            	retval.fieldDesc.fieldName = column_name.getText().trim();
            	

            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

        catch (RecognitionException el) {
          throw el;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "group_term"


    public static class result_column_return extends ParserRuleReturnScope {
        public FieldDesc fieldDesc = new FieldDesc();
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "result_column"
    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:190:1: result_column returns [FieldDesc fieldDesc = new FieldDesc()] : ( ASTERISK |column_name= selectexpr ( AS aliseName= ( ID | INTEGER ) )? );
    public final milesqlParser.result_column_return result_column() throws RecognitionException {
        milesqlParser.result_column_return retval = new milesqlParser.result_column_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token aliseName=null;
        Token ASTERISK27=null;
        Token AS28=null;
        milesqlParser.selectexpr_return column_name =null;


        Object aliseName_tree=null;
        Object ASTERISK27_tree=null;
        Object AS28_tree=null;

        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:191:3: ( ASTERISK |column_name= selectexpr ( AS aliseName= ( ID | INTEGER ) )? )
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==ASTERISK) ) {
                alt22=1;
            }
            else if ( (LA22_0==AVG||LA22_0==COUNT||LA22_0==ID||LA22_0==INTEGER||(LA22_0 >= MAX && LA22_0 <= MIN)||(LA22_0 >= SQUARESUM && LA22_0 <= STDDEV)||LA22_0==SUM||LA22_0==VARIANCE) ) {
                alt22=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 22, 0, input);

                throw nvae;

            }
            switch (alt22) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:191:5: ASTERISK
                    {
                    root_0 = (Object)adaptor.nil();


                    ASTERISK27=(Token)match(input,ASTERISK,FOLLOW_ASTERISK_in_result_column578); 
                    ASTERISK27_tree = 
                    (Object)adaptor.create(ASTERISK27)
                    ;
                    adaptor.addChild(root_0, ASTERISK27_tree);



                      retval.fieldDesc.fieldName ="*";
                      

                    }
                    break;
                case 2 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:195:4: column_name= selectexpr ( AS aliseName= ( ID | INTEGER ) )?
                    {
                    root_0 = (Object)adaptor.nil();


                    pushFollow(FOLLOW_selectexpr_in_result_column589);
                    column_name=selectexpr();

                    state._fsp--;

                    adaptor.addChild(root_0, column_name.getTree());


                      retval.fieldDesc.fieldName = column_name.columnname;
                      retval.fieldDesc.refColumnName = column_name.refname;
                      retval.fieldDesc.functionId= column_name.functionId;
                      retval.fieldDesc.functionName= column_name.functionName;
                      retval.fieldDesc.withinExpr = column_name.withinExpr;
                      

                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:203:3: ( AS aliseName= ( ID | INTEGER ) )?
                    int alt21=2;
                    int LA21_0 = input.LA(1);

                    if ( (LA21_0==AS) ) {
                        alt21=1;
                    }
                    switch (alt21) {
                        case 1 :
                            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:203:4: AS aliseName= ( ID | INTEGER )
                            {
                            AS28=(Token)match(input,AS,FOLLOW_AS_in_result_column598); 
                            AS28_tree = 
                            (Object)adaptor.create(AS28)
                            ;
                            adaptor.addChild(root_0, AS28_tree);


                            aliseName=(Token)input.LT(1);

                            if ( input.LA(1)==ID||input.LA(1)==INTEGER ) {
                                input.consume();
                                adaptor.addChild(root_0, 
                                (Object)adaptor.create(aliseName)
                                );
                                state.errorRecovery=false;
                            }
                            else {
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                throw mse;
                            }



                              retval.fieldDesc.aliseName= aliseName.getText().trim();
                              

                            }
                            break;

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

        catch (RecognitionException el) {
          throw el;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "result_column"


    public static class ordering_term_return extends ParserRuleReturnScope {
        public OrderDesc orderDesc = new OrderDesc();
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "ordering_term"
    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:211:1: ordering_term returns [OrderDesc orderDesc = new OrderDesc()] : column_name= ( ID | INTEGER ) (ascd= ( ASC | DESC ) )? ;
    public final milesqlParser.ordering_term_return ordering_term() throws RecognitionException {
        milesqlParser.ordering_term_return retval = new milesqlParser.ordering_term_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token column_name=null;
        Token ascd=null;

        Object column_name_tree=null;
        Object ascd_tree=null;

        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:213:2: (column_name= ( ID | INTEGER ) (ascd= ( ASC | DESC ) )? )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:213:4: column_name= ( ID | INTEGER ) (ascd= ( ASC | DESC ) )?
            {
            root_0 = (Object)adaptor.nil();


            column_name=(Token)input.LT(1);

            if ( input.LA(1)==ID||input.LA(1)==INTEGER ) {
                input.consume();
                adaptor.addChild(root_0, 
                (Object)adaptor.create(column_name)
                );
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }



            	FieldDesc fd = new FieldDesc();
            	fd.fieldName = column_name.getText().trim();
            	retval.orderDesc.field = fd;
            	

            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:219:2: (ascd= ( ASC | DESC ) )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==ASC||LA23_0==DESC) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:219:3: ascd= ( ASC | DESC )
                    {
                    ascd=(Token)input.LT(1);

                    if ( input.LA(1)==ASC||input.LA(1)==DESC ) {
                        input.consume();
                        adaptor.addChild(root_0, 
                        (Object)adaptor.create(ascd)
                        );
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }



                    	if(ascd.getText().toUpperCase().trim().equals("ASC")){
                             retval.orderDesc.type = Constants.ORDER_TYPE_ASC;
                            }else if(ascd.getText().toUpperCase().trim().equals("DESC")){
                             retval.orderDesc.type = Constants.ORDER_TYPE_DESC;
                            }
                    	

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

        catch (RecognitionException el) {
          throw el;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "ordering_term"


    public static class gorder_term_return extends ParserRuleReturnScope {
        public OrderDesc orderDesc = new OrderDesc();
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "gorder_term"
    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:230:1: gorder_term returns [OrderDesc orderDesc = new OrderDesc()] : column_name= ( ID | INTEGER ) (ascd= ( ASC | DESC ) )? ;
    public final milesqlParser.gorder_term_return gorder_term() throws RecognitionException {
        milesqlParser.gorder_term_return retval = new milesqlParser.gorder_term_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token column_name=null;
        Token ascd=null;

        Object column_name_tree=null;
        Object ascd_tree=null;

        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:232:2: (column_name= ( ID | INTEGER ) (ascd= ( ASC | DESC ) )? )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:232:4: column_name= ( ID | INTEGER ) (ascd= ( ASC | DESC ) )?
            {
            root_0 = (Object)adaptor.nil();


            column_name=(Token)input.LT(1);

            if ( input.LA(1)==ID||input.LA(1)==INTEGER ) {
                input.consume();
                adaptor.addChild(root_0, 
                (Object)adaptor.create(column_name)
                );
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }



            	FieldDesc fd = new FieldDesc();
            	fd.fieldName = column_name.getText().trim();
            	retval.orderDesc.field = fd;
            	

            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:238:2: (ascd= ( ASC | DESC ) )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==ASC||LA24_0==DESC) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:238:3: ascd= ( ASC | DESC )
                    {
                    ascd=(Token)input.LT(1);

                    if ( input.LA(1)==ASC||input.LA(1)==DESC ) {
                        input.consume();
                        adaptor.addChild(root_0, 
                        (Object)adaptor.create(ascd)
                        );
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }



                    	if(ascd.getText().toUpperCase().trim().equals("ASC")){
                             retval.orderDesc.type = Constants.ORDER_TYPE_ASC;
                            }else if(ascd.getText().toUpperCase().trim().equals("DESC")){
                             retval.orderDesc.type = Constants.ORDER_TYPE_DESC;
                            }
                    	

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

        catch (RecognitionException el) {
          throw el;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "gorder_term"


    public static class insert_stmt_return extends ParserRuleReturnScope {
        public InsertStatement insertStatement = new InsertStatement();
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "insert_stmt"
    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:249:1: insert_stmt returns [InsertStatement insertStatement = new InsertStatement() ] : INSERT INTO table_name= ID (column_name= ( ID | INTEGER ) EQUALS lv= literal_value )* ( WITH ( WORDSEG LPAREN column_name= ( ID | INTEGER ) RPAREN EQUALS LPAREN lv3= literal_value ( COMMA lv4= literal_value )* RPAREN )* )? ;
    public final milesqlParser.insert_stmt_return insert_stmt() throws RecognitionException {
        milesqlParser.insert_stmt_return retval = new milesqlParser.insert_stmt_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token table_name=null;
        Token column_name=null;
        Token INSERT29=null;
        Token INTO30=null;
        Token EQUALS31=null;
        Token WITH32=null;
        Token WORDSEG33=null;
        Token LPAREN34=null;
        Token RPAREN35=null;
        Token EQUALS36=null;
        Token LPAREN37=null;
        Token COMMA38=null;
        Token RPAREN39=null;
        milesqlParser.literal_value_return lv =null;

        milesqlParser.literal_value_return lv3 =null;

        milesqlParser.literal_value_return lv4 =null;


        Object table_name_tree=null;
        Object column_name_tree=null;
        Object INSERT29_tree=null;
        Object INTO30_tree=null;
        Object EQUALS31_tree=null;
        Object WITH32_tree=null;
        Object WORDSEG33_tree=null;
        Object LPAREN34_tree=null;
        Object RPAREN35_tree=null;
        Object EQUALS36_tree=null;
        Object LPAREN37_tree=null;
        Object COMMA38_tree=null;
        Object RPAREN39_tree=null;

        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:250:2: ( INSERT INTO table_name= ID (column_name= ( ID | INTEGER ) EQUALS lv= literal_value )* ( WITH ( WORDSEG LPAREN column_name= ( ID | INTEGER ) RPAREN EQUALS LPAREN lv3= literal_value ( COMMA lv4= literal_value )* RPAREN )* )? )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:250:4: INSERT INTO table_name= ID (column_name= ( ID | INTEGER ) EQUALS lv= literal_value )* ( WITH ( WORDSEG LPAREN column_name= ( ID | INTEGER ) RPAREN EQUALS LPAREN lv3= literal_value ( COMMA lv4= literal_value )* RPAREN )* )?
            {
            root_0 = (Object)adaptor.nil();


            INSERT29=(Token)match(input,INSERT,FOLLOW_INSERT_in_insert_stmt718); 
            INSERT29_tree = 
            (Object)adaptor.create(INSERT29)
            ;
            adaptor.addChild(root_0, INSERT29_tree);


            INTO30=(Token)match(input,INTO,FOLLOW_INTO_in_insert_stmt720); 
            INTO30_tree = 
            (Object)adaptor.create(INTO30)
            ;
            adaptor.addChild(root_0, INTO30_tree);


            table_name=(Token)match(input,ID,FOLLOW_ID_in_insert_stmt724); 
            table_name_tree = 
            (Object)adaptor.create(table_name)
            ;
            adaptor.addChild(root_0, table_name_tree);



            	retval.insertStatement.tableName = table_name.getText().trim();
            	

            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:254:2: (column_name= ( ID | INTEGER ) EQUALS lv= literal_value )*
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( (LA25_0==ID||LA25_0==INTEGER) ) {
                    alt25=1;
                }


                switch (alt25) {
            	case 1 :
            	    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:254:3: column_name= ( ID | INTEGER ) EQUALS lv= literal_value
            	    {
            	    column_name=(Token)input.LT(1);

            	    if ( input.LA(1)==ID||input.LA(1)==INTEGER ) {
            	        input.consume();
            	        adaptor.addChild(root_0, 
            	        (Object)adaptor.create(column_name)
            	        );
            	        state.errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }


            	    EQUALS31=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_insert_stmt739); 
            	    EQUALS31_tree = 
            	    (Object)adaptor.create(EQUALS31)
            	    ;
            	    adaptor.addChild(root_0, EQUALS31_tree);


            	    pushFollow(FOLLOW_literal_value_in_insert_stmt743);
            	    lv=literal_value();

            	    state._fsp--;

            	    adaptor.addChild(root_0, lv.getTree());


            	    	FieldValuePair fvp = new FieldValuePair();
            	    	FieldDesc fd = new FieldDesc();
            	    	fd.fieldName = column_name.getText().trim();
            	    	fvp.field = fd;
            	    	fvp.value = lv.valueDesc;
            	    	retval.insertStatement.documentValue.add(fvp);
            	    	

            	    }
            	    break;

            	default :
            	    break loop25;
                }
            } while (true);


            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:264:2: ( WITH ( WORDSEG LPAREN column_name= ( ID | INTEGER ) RPAREN EQUALS LPAREN lv3= literal_value ( COMMA lv4= literal_value )* RPAREN )* )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==WITH) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:264:3: WITH ( WORDSEG LPAREN column_name= ( ID | INTEGER ) RPAREN EQUALS LPAREN lv3= literal_value ( COMMA lv4= literal_value )* RPAREN )*
                    {
                    WITH32=(Token)match(input,WITH,FOLLOW_WITH_in_insert_stmt754); 
                    WITH32_tree = 
                    (Object)adaptor.create(WITH32)
                    ;
                    adaptor.addChild(root_0, WITH32_tree);


                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:264:8: ( WORDSEG LPAREN column_name= ( ID | INTEGER ) RPAREN EQUALS LPAREN lv3= literal_value ( COMMA lv4= literal_value )* RPAREN )*
                    loop27:
                    do {
                        int alt27=2;
                        int LA27_0 = input.LA(1);

                        if ( (LA27_0==WORDSEG) ) {
                            alt27=1;
                        }


                        switch (alt27) {
                    	case 1 :
                    	    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:264:9: WORDSEG LPAREN column_name= ( ID | INTEGER ) RPAREN EQUALS LPAREN lv3= literal_value ( COMMA lv4= literal_value )* RPAREN
                    	    {
                    	    WORDSEG33=(Token)match(input,WORDSEG,FOLLOW_WORDSEG_in_insert_stmt757); 
                    	    WORDSEG33_tree = 
                    	    (Object)adaptor.create(WORDSEG33)
                    	    ;
                    	    adaptor.addChild(root_0, WORDSEG33_tree);


                    	    LPAREN34=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_insert_stmt759); 
                    	    LPAREN34_tree = 
                    	    (Object)adaptor.create(LPAREN34)
                    	    ;
                    	    adaptor.addChild(root_0, LPAREN34_tree);


                    	    column_name=(Token)input.LT(1);

                    	    if ( input.LA(1)==ID||input.LA(1)==INTEGER ) {
                    	        input.consume();
                    	        adaptor.addChild(root_0, 
                    	        (Object)adaptor.create(column_name)
                    	        );
                    	        state.errorRecovery=false;
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        throw mse;
                    	    }


                    	    RPAREN35=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_insert_stmt769); 
                    	    RPAREN35_tree = 
                    	    (Object)adaptor.create(RPAREN35)
                    	    ;
                    	    adaptor.addChild(root_0, RPAREN35_tree);


                    	    EQUALS36=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_insert_stmt771); 
                    	    EQUALS36_tree = 
                    	    (Object)adaptor.create(EQUALS36)
                    	    ;
                    	    adaptor.addChild(root_0, EQUALS36_tree);


                    	    LPAREN37=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_insert_stmt773); 
                    	    LPAREN37_tree = 
                    	    (Object)adaptor.create(LPAREN37)
                    	    ;
                    	    adaptor.addChild(root_0, LPAREN37_tree);


                    	    pushFollow(FOLLOW_literal_value_in_insert_stmt777);
                    	    lv3=literal_value();

                    	    state._fsp--;

                    	    adaptor.addChild(root_0, lv3.getTree());


                    	    	FieldValuePair fvp = new FieldValuePair();
                    	    	FieldDesc fd = new FieldDesc();
                    	    	fd.fieldName = "$" + column_name.getText().trim();
                    	    	fvp.field = fd;
                    	    	fvp.value = lv3.valueDesc;
                    	    	retval.insertStatement.documentValue.add(fvp);
                    	    	

                    	    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:273:2: ( COMMA lv4= literal_value )*
                    	    loop26:
                    	    do {
                    	        int alt26=2;
                    	        int LA26_0 = input.LA(1);

                    	        if ( (LA26_0==COMMA) ) {
                    	            alt26=1;
                    	        }


                    	        switch (alt26) {
                    	    	case 1 :
                    	    	    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:273:3: COMMA lv4= literal_value
                    	    	    {
                    	    	    COMMA38=(Token)match(input,COMMA,FOLLOW_COMMA_in_insert_stmt784); 
                    	    	    COMMA38_tree = 
                    	    	    (Object)adaptor.create(COMMA38)
                    	    	    ;
                    	    	    adaptor.addChild(root_0, COMMA38_tree);


                    	    	    pushFollow(FOLLOW_literal_value_in_insert_stmt788);
                    	    	    lv4=literal_value();

                    	    	    state._fsp--;

                    	    	    adaptor.addChild(root_0, lv4.getTree());


                    	    	    	FieldValuePair newFvp = new FieldValuePair();
                    	    	    	FieldDesc newFd = new FieldDesc();
                    	    	    	newFd.fieldName = "$" + column_name.getText().trim();
                    	    	    	newFvp.field = newFd;
                    	    	    	newFvp.value = lv4.valueDesc;
                    	    	    	retval.insertStatement.documentValue.add(newFvp);
                    	    	    	

                    	    	    }
                    	    	    break;

                    	    	default :
                    	    	    break loop26;
                    	        }
                    	    } while (true);


                    	    RPAREN39=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_insert_stmt797); 
                    	    RPAREN39_tree = 
                    	    (Object)adaptor.create(RPAREN39)
                    	    ;
                    	    adaptor.addChild(root_0, RPAREN39_tree);


                    	    }
                    	    break;

                    	default :
                    	    break loop27;
                        }
                    } while (true);


                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

        catch (RecognitionException el) {
          throw el;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "insert_stmt"


    public static class export_stmt_return extends ParserRuleReturnScope {
        public ExportStatement exportStatement = new ExportStatement();
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "export_stmt"
    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:288:1: export_stmt returns [ExportStatement exportStatement = new ExportStatement()] : EXPORT TO save_path= literal_value FROM table_name= ID ( SEGHINT seg_hint= seghint_expr )? ( INDEXWHERE hash_where= expr )? ( WHERE filter_where= expr )? ( LIMIT limit= INTEGER )? ;
    public final milesqlParser.export_stmt_return export_stmt() throws RecognitionException {
        milesqlParser.export_stmt_return retval = new milesqlParser.export_stmt_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token table_name=null;
        Token limit=null;
        Token EXPORT40=null;
        Token TO41=null;
        Token FROM42=null;
        Token SEGHINT43=null;
        Token INDEXWHERE44=null;
        Token WHERE45=null;
        Token LIMIT46=null;
        milesqlParser.literal_value_return save_path =null;

        milesqlParser.seghint_expr_return seg_hint =null;

        milesqlParser.expr_return hash_where =null;

        milesqlParser.expr_return filter_where =null;


        Object table_name_tree=null;
        Object limit_tree=null;
        Object EXPORT40_tree=null;
        Object TO41_tree=null;
        Object FROM42_tree=null;
        Object SEGHINT43_tree=null;
        Object INDEXWHERE44_tree=null;
        Object WHERE45_tree=null;
        Object LIMIT46_tree=null;

        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:289:2: ( EXPORT TO save_path= literal_value FROM table_name= ID ( SEGHINT seg_hint= seghint_expr )? ( INDEXWHERE hash_where= expr )? ( WHERE filter_where= expr )? ( LIMIT limit= INTEGER )? )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:289:4: EXPORT TO save_path= literal_value FROM table_name= ID ( SEGHINT seg_hint= seghint_expr )? ( INDEXWHERE hash_where= expr )? ( WHERE filter_where= expr )? ( LIMIT limit= INTEGER )?
            {
            root_0 = (Object)adaptor.nil();


            EXPORT40=(Token)match(input,EXPORT,FOLLOW_EXPORT_in_export_stmt819); 
            EXPORT40_tree = 
            (Object)adaptor.create(EXPORT40)
            ;
            adaptor.addChild(root_0, EXPORT40_tree);


            TO41=(Token)match(input,TO,FOLLOW_TO_in_export_stmt821); 
            TO41_tree = 
            (Object)adaptor.create(TO41)
            ;
            adaptor.addChild(root_0, TO41_tree);


            pushFollow(FOLLOW_literal_value_in_export_stmt825);
            save_path=literal_value();

            state._fsp--;

            adaptor.addChild(root_0, save_path.getTree());


            		retval.exportStatement.path = save_path.valueDesc;
            	

            FROM42=(Token)match(input,FROM,FOLLOW_FROM_in_export_stmt830); 
            FROM42_tree = 
            (Object)adaptor.create(FROM42)
            ;
            adaptor.addChild(root_0, FROM42_tree);


            table_name=(Token)match(input,ID,FOLLOW_ID_in_export_stmt834); 
            table_name_tree = 
            (Object)adaptor.create(table_name)
            ;
            adaptor.addChild(root_0, table_name_tree);


            retval.exportStatement.tableName = table_name.getText().trim(); 

            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:293:2: ( SEGHINT seg_hint= seghint_expr )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==SEGHINT) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:293:3: SEGHINT seg_hint= seghint_expr
                    {
                    SEGHINT43=(Token)match(input,SEGHINT,FOLLOW_SEGHINT_in_export_stmt840); 
                    SEGHINT43_tree = 
                    (Object)adaptor.create(SEGHINT43)
                    ;
                    adaptor.addChild(root_0, SEGHINT43_tree);


                    pushFollow(FOLLOW_seghint_expr_in_export_stmt844);
                    seg_hint=seghint_expr();

                    state._fsp--;

                    adaptor.addChild(root_0, seg_hint.getTree());

                    retval.exportStatement.hint = seg_hint.hint;

                    }
                    break;

            }


            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:294:2: ( INDEXWHERE hash_where= expr )?
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==INDEXWHERE) ) {
                alt30=1;
            }
            switch (alt30) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:294:3: INDEXWHERE hash_where= expr
                    {
                    INDEXWHERE44=(Token)match(input,INDEXWHERE,FOLLOW_INDEXWHERE_in_export_stmt853); 
                    INDEXWHERE44_tree = 
                    (Object)adaptor.create(INDEXWHERE44)
                    ;
                    adaptor.addChild(root_0, INDEXWHERE44_tree);


                    pushFollow(FOLLOW_expr_in_export_stmt857);
                    hash_where=expr();

                    state._fsp--;

                    adaptor.addChild(root_0, hash_where.getTree());

                    retval.exportStatement.hashWhere = hash_where.expression;

                    }
                    break;

            }


            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:295:2: ( WHERE filter_where= expr )?
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0==WHERE) ) {
                alt31=1;
            }
            switch (alt31) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:295:3: WHERE filter_where= expr
                    {
                    WHERE45=(Token)match(input,WHERE,FOLLOW_WHERE_in_export_stmt867); 
                    WHERE45_tree = 
                    (Object)adaptor.create(WHERE45)
                    ;
                    adaptor.addChild(root_0, WHERE45_tree);


                    pushFollow(FOLLOW_expr_in_export_stmt871);
                    filter_where=expr();

                    state._fsp--;

                    adaptor.addChild(root_0, filter_where.getTree());

                    retval.exportStatement.filterWhere = filter_where.expression;

                    }
                    break;

            }


            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:296:2: ( LIMIT limit= INTEGER )?
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==LIMIT) ) {
                alt32=1;
            }
            switch (alt32) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:296:3: LIMIT limit= INTEGER
                    {
                    LIMIT46=(Token)match(input,LIMIT,FOLLOW_LIMIT_in_export_stmt880); 
                    LIMIT46_tree = 
                    (Object)adaptor.create(LIMIT46)
                    ;
                    adaptor.addChild(root_0, LIMIT46_tree);


                    limit=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_export_stmt884); 
                    limit_tree = 
                    (Object)adaptor.create(limit)
                    ;
                    adaptor.addChild(root_0, limit_tree);


                    retval.exportStatement.limit = Long.parseLong(limit.getText().trim());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

        catch (RecognitionException el) {
          throw el;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "export_stmt"


    public static class delete_stmt_return extends ParserRuleReturnScope {
        public DeleteStatement deleteStatement = new DeleteStatement();
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "delete_stmt"
    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:299:1: delete_stmt returns [DeleteStatement deleteStatement = new DeleteStatement()] : DELETE FROM table_name= ID ( SEGHINT seg_hint= seghint_expr )? ( INDEXWHERE hash_Where= expr )? ( WHERE filterWhere= expr )? ;
    public final milesqlParser.delete_stmt_return delete_stmt() throws RecognitionException {
        milesqlParser.delete_stmt_return retval = new milesqlParser.delete_stmt_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token table_name=null;
        Token DELETE47=null;
        Token FROM48=null;
        Token SEGHINT49=null;
        Token INDEXWHERE50=null;
        Token WHERE51=null;
        milesqlParser.seghint_expr_return seg_hint =null;

        milesqlParser.expr_return hash_Where =null;

        milesqlParser.expr_return filterWhere =null;


        Object table_name_tree=null;
        Object DELETE47_tree=null;
        Object FROM48_tree=null;
        Object SEGHINT49_tree=null;
        Object INDEXWHERE50_tree=null;
        Object WHERE51_tree=null;

        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:300:2: ( DELETE FROM table_name= ID ( SEGHINT seg_hint= seghint_expr )? ( INDEXWHERE hash_Where= expr )? ( WHERE filterWhere= expr )? )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:300:4: DELETE FROM table_name= ID ( SEGHINT seg_hint= seghint_expr )? ( INDEXWHERE hash_Where= expr )? ( WHERE filterWhere= expr )?
            {
            root_0 = (Object)adaptor.nil();


            DELETE47=(Token)match(input,DELETE,FOLLOW_DELETE_in_delete_stmt902); 
            DELETE47_tree = 
            (Object)adaptor.create(DELETE47)
            ;
            adaptor.addChild(root_0, DELETE47_tree);


            FROM48=(Token)match(input,FROM,FOLLOW_FROM_in_delete_stmt904); 
            FROM48_tree = 
            (Object)adaptor.create(FROM48)
            ;
            adaptor.addChild(root_0, FROM48_tree);


            table_name=(Token)match(input,ID,FOLLOW_ID_in_delete_stmt908); 
            table_name_tree = 
            (Object)adaptor.create(table_name)
            ;
            adaptor.addChild(root_0, table_name_tree);


            retval.deleteStatement.tableName = table_name.getText().trim();

            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:301:2: ( SEGHINT seg_hint= seghint_expr )?
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==SEGHINT) ) {
                alt33=1;
            }
            switch (alt33) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:301:3: SEGHINT seg_hint= seghint_expr
                    {
                    SEGHINT49=(Token)match(input,SEGHINT,FOLLOW_SEGHINT_in_delete_stmt914); 
                    SEGHINT49_tree = 
                    (Object)adaptor.create(SEGHINT49)
                    ;
                    adaptor.addChild(root_0, SEGHINT49_tree);


                    pushFollow(FOLLOW_seghint_expr_in_delete_stmt918);
                    seg_hint=seghint_expr();

                    state._fsp--;

                    adaptor.addChild(root_0, seg_hint.getTree());

                    retval.deleteStatement.hint =seg_hint.hint;

                    }
                    break;

            }


            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:302:2: ( INDEXWHERE hash_Where= expr )?
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( (LA34_0==INDEXWHERE) ) {
                alt34=1;
            }
            switch (alt34) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:302:3: INDEXWHERE hash_Where= expr
                    {
                    INDEXWHERE50=(Token)match(input,INDEXWHERE,FOLLOW_INDEXWHERE_in_delete_stmt926); 
                    INDEXWHERE50_tree = 
                    (Object)adaptor.create(INDEXWHERE50)
                    ;
                    adaptor.addChild(root_0, INDEXWHERE50_tree);


                    pushFollow(FOLLOW_expr_in_delete_stmt931);
                    hash_Where=expr();

                    state._fsp--;

                    adaptor.addChild(root_0, hash_Where.getTree());

                    retval.deleteStatement.hashWhere = hash_Where.expression;

                    }
                    break;

            }


            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:303:2: ( WHERE filterWhere= expr )?
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( (LA35_0==WHERE) ) {
                alt35=1;
            }
            switch (alt35) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:303:3: WHERE filterWhere= expr
                    {
                    WHERE51=(Token)match(input,WHERE,FOLLOW_WHERE_in_delete_stmt938); 
                    WHERE51_tree = 
                    (Object)adaptor.create(WHERE51)
                    ;
                    adaptor.addChild(root_0, WHERE51_tree);


                    pushFollow(FOLLOW_expr_in_delete_stmt942);
                    filterWhere=expr();

                    state._fsp--;

                    adaptor.addChild(root_0, filterWhere.getTree());

                    retval.deleteStatement.filterWhere = filterWhere.expression;

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

        catch (RecognitionException el) {
          throw el;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "delete_stmt"


    public static class update_stmt_return extends ParserRuleReturnScope {
        public UpdateStatement updateStatement = new UpdateStatement();
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "update_stmt"
    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:306:1: update_stmt returns [UpdateStatement updateStatement = new UpdateStatement()] : UPDATE table_name= ID SET update_set[$updateStatement] ( SEGHINT seg_hint= seghint_expr )? ( INDEXWHERE hash_Where= expr )? ( WHERE filterWhere= expr )? ;
    public final milesqlParser.update_stmt_return update_stmt() throws RecognitionException {
        milesqlParser.update_stmt_return retval = new milesqlParser.update_stmt_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token table_name=null;
        Token UPDATE52=null;
        Token SET53=null;
        Token SEGHINT55=null;
        Token INDEXWHERE56=null;
        Token WHERE57=null;
        milesqlParser.seghint_expr_return seg_hint =null;

        milesqlParser.expr_return hash_Where =null;

        milesqlParser.expr_return filterWhere =null;

        milesqlParser.update_set_return update_set54 =null;


        Object table_name_tree=null;
        Object UPDATE52_tree=null;
        Object SET53_tree=null;
        Object SEGHINT55_tree=null;
        Object INDEXWHERE56_tree=null;
        Object WHERE57_tree=null;

        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:307:2: ( UPDATE table_name= ID SET update_set[$updateStatement] ( SEGHINT seg_hint= seghint_expr )? ( INDEXWHERE hash_Where= expr )? ( WHERE filterWhere= expr )? )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:307:4: UPDATE table_name= ID SET update_set[$updateStatement] ( SEGHINT seg_hint= seghint_expr )? ( INDEXWHERE hash_Where= expr )? ( WHERE filterWhere= expr )?
            {
            root_0 = (Object)adaptor.nil();


            UPDATE52=(Token)match(input,UPDATE,FOLLOW_UPDATE_in_update_stmt958); 
            UPDATE52_tree = 
            (Object)adaptor.create(UPDATE52)
            ;
            adaptor.addChild(root_0, UPDATE52_tree);


            table_name=(Token)match(input,ID,FOLLOW_ID_in_update_stmt962); 
            table_name_tree = 
            (Object)adaptor.create(table_name)
            ;
            adaptor.addChild(root_0, table_name_tree);


            retval.updateStatement.tableName = table_name.getText().trim();

            SET53=(Token)match(input,SET,FOLLOW_SET_in_update_stmt967); 
            SET53_tree = 
            (Object)adaptor.create(SET53)
            ;
            adaptor.addChild(root_0, SET53_tree);


            pushFollow(FOLLOW_update_set_in_update_stmt969);
            update_set54=update_set(retval.updateStatement);

            state._fsp--;

            adaptor.addChild(root_0, update_set54.getTree());

            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:309:2: ( SEGHINT seg_hint= seghint_expr )?
            int alt36=2;
            int LA36_0 = input.LA(1);

            if ( (LA36_0==SEGHINT) ) {
                alt36=1;
            }
            switch (alt36) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:309:3: SEGHINT seg_hint= seghint_expr
                    {
                    SEGHINT55=(Token)match(input,SEGHINT,FOLLOW_SEGHINT_in_update_stmt975); 
                    SEGHINT55_tree = 
                    (Object)adaptor.create(SEGHINT55)
                    ;
                    adaptor.addChild(root_0, SEGHINT55_tree);


                    pushFollow(FOLLOW_seghint_expr_in_update_stmt979);
                    seg_hint=seghint_expr();

                    state._fsp--;

                    adaptor.addChild(root_0, seg_hint.getTree());

                    retval.updateStatement.hint =seg_hint.hint;

                    }
                    break;

            }


            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:310:2: ( INDEXWHERE hash_Where= expr )?
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==INDEXWHERE) ) {
                alt37=1;
            }
            switch (alt37) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:310:3: INDEXWHERE hash_Where= expr
                    {
                    INDEXWHERE56=(Token)match(input,INDEXWHERE,FOLLOW_INDEXWHERE_in_update_stmt987); 
                    INDEXWHERE56_tree = 
                    (Object)adaptor.create(INDEXWHERE56)
                    ;
                    adaptor.addChild(root_0, INDEXWHERE56_tree);


                    pushFollow(FOLLOW_expr_in_update_stmt992);
                    hash_Where=expr();

                    state._fsp--;

                    adaptor.addChild(root_0, hash_Where.getTree());

                    retval.updateStatement.hashWhere = hash_Where.expression;

                    }
                    break;

            }


            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:311:2: ( WHERE filterWhere= expr )?
            int alt38=2;
            int LA38_0 = input.LA(1);

            if ( (LA38_0==WHERE) ) {
                alt38=1;
            }
            switch (alt38) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:311:3: WHERE filterWhere= expr
                    {
                    WHERE57=(Token)match(input,WHERE,FOLLOW_WHERE_in_update_stmt999); 
                    WHERE57_tree = 
                    (Object)adaptor.create(WHERE57)
                    ;
                    adaptor.addChild(root_0, WHERE57_tree);


                    pushFollow(FOLLOW_expr_in_update_stmt1003);
                    filterWhere=expr();

                    state._fsp--;

                    adaptor.addChild(root_0, filterWhere.getTree());

                    retval.updateStatement.filterWhere = filterWhere.expression;

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

        catch (RecognitionException el) {
          throw el;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "update_stmt"


    public static class dochint_expr_return extends ParserRuleReturnScope {
        public DocHint dochint = new DocHint();
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "dochint_expr"
    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:314:1: dochint_expr returns [DocHint dochint = new DocHint()] : doc_name= ( ID | INTEGER ) EQUALS doc_id= INTEGER ;
    public final milesqlParser.dochint_expr_return dochint_expr() throws RecognitionException {
        milesqlParser.dochint_expr_return retval = new milesqlParser.dochint_expr_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token doc_name=null;
        Token doc_id=null;
        Token EQUALS58=null;

        Object doc_name_tree=null;
        Object doc_id_tree=null;
        Object EQUALS58_tree=null;

        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:315:2: (doc_name= ( ID | INTEGER ) EQUALS doc_id= INTEGER )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:315:4: doc_name= ( ID | INTEGER ) EQUALS doc_id= INTEGER
            {
            root_0 = (Object)adaptor.nil();


            doc_name=(Token)input.LT(1);

            if ( input.LA(1)==ID||input.LA(1)==INTEGER ) {
                input.consume();
                adaptor.addChild(root_0, 
                (Object)adaptor.create(doc_name)
                );
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            EQUALS58=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_dochint_expr1028); 
            EQUALS58_tree = 
            (Object)adaptor.create(EQUALS58)
            ;
            adaptor.addChild(root_0, EQUALS58_tree);


            doc_id=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_dochint_expr1034); 
            doc_id_tree = 
            (Object)adaptor.create(doc_id)
            ;
            adaptor.addChild(root_0, doc_id_tree);



                    if(doc_name.getText().trim().toUpperCase().equals("mile_doc_id".trim().toUpperCase())){
                    retval.dochint.docId = Long.parseLong(doc_id.getText().trim().toString());
                    }
            	

            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

        catch (RecognitionException el) {
          throw el;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "dochint_expr"


    public static class update_set_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "update_set"
    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:323:1: update_set[UpdateStatement updateStatement] : column_name= ( ID | INTEGER ) EQUALS lv= literal_value ;
    public final milesqlParser.update_set_return update_set(UpdateStatement updateStatement) throws RecognitionException {
        milesqlParser.update_set_return retval = new milesqlParser.update_set_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token column_name=null;
        Token EQUALS59=null;
        milesqlParser.literal_value_return lv =null;


        Object column_name_tree=null;
        Object EQUALS59_tree=null;

        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:324:2: (column_name= ( ID | INTEGER ) EQUALS lv= literal_value )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:324:4: column_name= ( ID | INTEGER ) EQUALS lv= literal_value
            {
            root_0 = (Object)adaptor.nil();


            column_name=(Token)input.LT(1);

            if ( input.LA(1)==ID||input.LA(1)==INTEGER ) {
                input.consume();
                adaptor.addChild(root_0, 
                (Object)adaptor.create(column_name)
                );
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            EQUALS59=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_update_set1057); 
            EQUALS59_tree = 
            (Object)adaptor.create(EQUALS59)
            ;
            adaptor.addChild(root_0, EQUALS59_tree);


            pushFollow(FOLLOW_literal_value_in_update_set1061);
            lv=literal_value();

            state._fsp--;

            adaptor.addChild(root_0, lv.getTree());


            	    FieldValuePair fvp = new FieldValuePair();
                        FieldDesc fd  =  new FieldDesc();
                        fd.fieldName = column_name.getText().toString();
                        fvp.field = fd;
                        fvp.value = lv.valueDesc;
                        updateStatement.updateValue =fvp;
            	

            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

        catch (RecognitionException el) {
          throw el;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "update_set"


    public static class seghint_expr_return extends ParserRuleReturnScope {
        public TimeHint hint= new TimeHint();
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "seghint_expr"
    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:337:1: seghint_expr returns [TimeHint hint= new TimeHint()] : LPAREN startCreateTime= ( INTEGER | QUESTION ) COMMA endCreateTime= ( INTEGER | QUESTION ) COMMA startUpdateTime= ( INTEGER | QUESTION ) COMMA endUpdateTime= ( INTEGER | QUESTION ) RPAREN ;
    public final milesqlParser.seghint_expr_return seghint_expr() throws RecognitionException {
        milesqlParser.seghint_expr_return retval = new milesqlParser.seghint_expr_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token startCreateTime=null;
        Token endCreateTime=null;
        Token startUpdateTime=null;
        Token endUpdateTime=null;
        Token LPAREN60=null;
        Token COMMA61=null;
        Token COMMA62=null;
        Token COMMA63=null;
        Token RPAREN64=null;

        Object startCreateTime_tree=null;
        Object endCreateTime_tree=null;
        Object startUpdateTime_tree=null;
        Object endUpdateTime_tree=null;
        Object LPAREN60_tree=null;
        Object COMMA61_tree=null;
        Object COMMA62_tree=null;
        Object COMMA63_tree=null;
        Object RPAREN64_tree=null;

        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:338:2: ( LPAREN startCreateTime= ( INTEGER | QUESTION ) COMMA endCreateTime= ( INTEGER | QUESTION ) COMMA startUpdateTime= ( INTEGER | QUESTION ) COMMA endUpdateTime= ( INTEGER | QUESTION ) RPAREN )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:338:4: LPAREN startCreateTime= ( INTEGER | QUESTION ) COMMA endCreateTime= ( INTEGER | QUESTION ) COMMA startUpdateTime= ( INTEGER | QUESTION ) COMMA endUpdateTime= ( INTEGER | QUESTION ) RPAREN
            {
            root_0 = (Object)adaptor.nil();


            LPAREN60=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_seghint_expr1079); 
            LPAREN60_tree = 
            (Object)adaptor.create(LPAREN60)
            ;
            adaptor.addChild(root_0, LPAREN60_tree);


            startCreateTime=(Token)input.LT(1);

            if ( input.LA(1)==INTEGER||input.LA(1)==QUESTION ) {
                input.consume();
                adaptor.addChild(root_0, 
                (Object)adaptor.create(startCreateTime)
                );
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            COMMA61=(Token)match(input,COMMA,FOLLOW_COMMA_in_seghint_expr1089); 
            COMMA61_tree = 
            (Object)adaptor.create(COMMA61)
            ;
            adaptor.addChild(root_0, COMMA61_tree);


            endCreateTime=(Token)input.LT(1);

            if ( input.LA(1)==INTEGER||input.LA(1)==QUESTION ) {
                input.consume();
                adaptor.addChild(root_0, 
                (Object)adaptor.create(endCreateTime)
                );
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            COMMA62=(Token)match(input,COMMA,FOLLOW_COMMA_in_seghint_expr1099); 
            COMMA62_tree = 
            (Object)adaptor.create(COMMA62)
            ;
            adaptor.addChild(root_0, COMMA62_tree);


            startUpdateTime=(Token)input.LT(1);

            if ( input.LA(1)==INTEGER||input.LA(1)==QUESTION ) {
                input.consume();
                adaptor.addChild(root_0, 
                (Object)adaptor.create(startUpdateTime)
                );
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            COMMA63=(Token)match(input,COMMA,FOLLOW_COMMA_in_seghint_expr1109); 
            COMMA63_tree = 
            (Object)adaptor.create(COMMA63)
            ;
            adaptor.addChild(root_0, COMMA63_tree);


            endUpdateTime=(Token)input.LT(1);

            if ( input.LA(1)==INTEGER||input.LA(1)==QUESTION ) {
                input.consume();
                adaptor.addChild(root_0, 
                (Object)adaptor.create(endUpdateTime)
                );
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            RPAREN64=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_seghint_expr1119); 
            RPAREN64_tree = 
            (Object)adaptor.create(RPAREN64)
            ;
            adaptor.addChild(root_0, RPAREN64_tree);



                	retval.hint.startCreateTime = Long.parseLong(startCreateTime.getText().trim());

                    retval.hint.endCreateTime = Long.parseLong(endCreateTime.getText().trim());

                    retval.hint.startUpdateTime = Long.parseLong(startUpdateTime.getText().trim());

                    retval.hint.endUpdateTime = Long.parseLong(endUpdateTime.getText().trim());
            	

            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

        catch (RecognitionException el) {
          throw el;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "seghint_expr"


    public static class expr_return extends ParserRuleReturnScope {
        public Expression expression;
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "expr"
    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:351:1: expr returns [Expression expression] : or_sub= or_subexpr ( OR or_sub= or_subexpr )* ;
    public final milesqlParser.expr_return expr() throws RecognitionException {
        milesqlParser.expr_return retval = new milesqlParser.expr_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token OR65=null;
        milesqlParser.or_subexpr_return or_sub =null;


        Object OR65_tree=null;

        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:352:2: (or_sub= or_subexpr ( OR or_sub= or_subexpr )* )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:352:4: or_sub= or_subexpr ( OR or_sub= or_subexpr )*
            {
            root_0 = (Object)adaptor.nil();


            pushFollow(FOLLOW_or_subexpr_in_expr1142);
            or_sub=or_subexpr();

            state._fsp--;

            adaptor.addChild(root_0, or_sub.getTree());


            	retval.expression =or_sub.expression;
            	

            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:356:2: ( OR or_sub= or_subexpr )*
            loop39:
            do {
                int alt39=2;
                int LA39_0 = input.LA(1);

                if ( (LA39_0==OR) ) {
                    alt39=1;
                }


                switch (alt39) {
            	case 1 :
            	    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:356:3: OR or_sub= or_subexpr
            	    {
            	    OR65=(Token)match(input,OR,FOLLOW_OR_in_expr1149); 
            	    OR65_tree = 
            	    (Object)adaptor.create(OR65)
            	    ;
            	    adaptor.addChild(root_0, OR65_tree);


            	    pushFollow(FOLLOW_or_subexpr_in_expr1154);
            	    or_sub=or_subexpr();

            	    state._fsp--;

            	    adaptor.addChild(root_0, or_sub.getTree());


            	    	retval.expression = retval.expression.orExp(or_sub.expression);
            	    	

            	    }
            	    break;

            	default :
            	    break loop39;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

        catch (RecognitionException el) {
          throw el;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "expr"


    public static class or_subexpr_return extends ParserRuleReturnScope {
        public Expression expression;
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "or_subexpr"
    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:362:1: or_subexpr returns [Expression expression] : and_sub= and_subexpr ( AND and_sub= and_subexpr )* ;
    public final milesqlParser.or_subexpr_return or_subexpr() throws RecognitionException {
        milesqlParser.or_subexpr_return retval = new milesqlParser.or_subexpr_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token AND66=null;
        milesqlParser.and_subexpr_return and_sub =null;


        Object AND66_tree=null;

        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:363:2: (and_sub= and_subexpr ( AND and_sub= and_subexpr )* )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:363:4: and_sub= and_subexpr ( AND and_sub= and_subexpr )*
            {
            root_0 = (Object)adaptor.nil();


            pushFollow(FOLLOW_and_subexpr_in_or_subexpr1178);
            and_sub=and_subexpr();

            state._fsp--;

            adaptor.addChild(root_0, and_sub.getTree());


            	retval.expression =and_sub.expression;
            	

            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:367:2: ( AND and_sub= and_subexpr )*
            loop40:
            do {
                int alt40=2;
                int LA40_0 = input.LA(1);

                if ( (LA40_0==AND) ) {
                    alt40=1;
                }


                switch (alt40) {
            	case 1 :
            	    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:367:3: AND and_sub= and_subexpr
            	    {
            	    AND66=(Token)match(input,AND,FOLLOW_AND_in_or_subexpr1185); 
            	    AND66_tree = 
            	    (Object)adaptor.create(AND66)
            	    ;
            	    adaptor.addChild(root_0, AND66_tree);


            	    pushFollow(FOLLOW_and_subexpr_in_or_subexpr1189);
            	    and_sub=and_subexpr();

            	    state._fsp--;

            	    adaptor.addChild(root_0, and_sub.getTree());


            	    	retval.expression = retval.expression.andExp(and_sub.expression);
            	    	

            	    }
            	    break;

            	default :
            	    break loop40;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

        catch (RecognitionException el) {
          throw el;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "or_subexpr"


    public static class and_subexpr_return extends ParserRuleReturnScope {
        public Expression expression;
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "and_subexpr"
    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:373:1: and_subexpr returns [Expression expression] : (eq_sub= eq_subexpr | LPAREN expr_sub= expr RPAREN );
    public final milesqlParser.and_subexpr_return and_subexpr() throws RecognitionException {
        milesqlParser.and_subexpr_return retval = new milesqlParser.and_subexpr_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token LPAREN67=null;
        Token RPAREN68=null;
        milesqlParser.eq_subexpr_return eq_sub =null;

        milesqlParser.expr_return expr_sub =null;


        Object LPAREN67_tree=null;
        Object RPAREN68_tree=null;

        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:374:2: (eq_sub= eq_subexpr | LPAREN expr_sub= expr RPAREN )
            int alt41=2;
            int LA41_0 = input.LA(1);

            if ( (LA41_0==ID||LA41_0==INTEGER) ) {
                alt41=1;
            }
            else if ( (LA41_0==LPAREN) ) {
                alt41=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 41, 0, input);

                throw nvae;

            }
            switch (alt41) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:374:4: eq_sub= eq_subexpr
                    {
                    root_0 = (Object)adaptor.nil();


                    pushFollow(FOLLOW_eq_subexpr_in_and_subexpr1212);
                    eq_sub=eq_subexpr();

                    state._fsp--;

                    adaptor.addChild(root_0, eq_sub.getTree());


                    	retval.expression = eq_sub.expression;
                    	

                    }
                    break;
                case 2 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:378:4: LPAREN expr_sub= expr RPAREN
                    {
                    root_0 = (Object)adaptor.nil();


                    LPAREN67=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_and_subexpr1220); 
                    LPAREN67_tree = 
                    (Object)adaptor.create(LPAREN67)
                    ;
                    adaptor.addChild(root_0, LPAREN67_tree);


                    pushFollow(FOLLOW_expr_in_and_subexpr1224);
                    expr_sub=expr();

                    state._fsp--;

                    adaptor.addChild(root_0, expr_sub.getTree());

                    RPAREN68=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_and_subexpr1226); 
                    RPAREN68_tree = 
                    (Object)adaptor.create(RPAREN68)
                    ;
                    adaptor.addChild(root_0, RPAREN68_tree);



                    	retval.expression = expr_sub.expression;
                    	

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

        catch (RecognitionException el) {
          throw el;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "and_subexpr"


    public static class having_expr_return extends ParserRuleReturnScope {
        public Expression expression;
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "having_expr"
    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:385:1: having_expr returns [Expression expression] : or_sub= having_or_subexpr ( OR or_sub= having_or_subexpr )* ;
    public final milesqlParser.having_expr_return having_expr() throws RecognitionException {
        milesqlParser.having_expr_return retval = new milesqlParser.having_expr_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token OR69=null;
        milesqlParser.having_or_subexpr_return or_sub =null;


        Object OR69_tree=null;

        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:386:2: (or_sub= having_or_subexpr ( OR or_sub= having_or_subexpr )* )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:386:4: or_sub= having_or_subexpr ( OR or_sub= having_or_subexpr )*
            {
            root_0 = (Object)adaptor.nil();


            pushFollow(FOLLOW_having_or_subexpr_in_having_expr1249);
            or_sub=having_or_subexpr();

            state._fsp--;

            adaptor.addChild(root_0, or_sub.getTree());


            	retval.expression =or_sub.expression;
            	

            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:390:2: ( OR or_sub= having_or_subexpr )*
            loop42:
            do {
                int alt42=2;
                int LA42_0 = input.LA(1);

                if ( (LA42_0==OR) ) {
                    alt42=1;
                }


                switch (alt42) {
            	case 1 :
            	    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:390:3: OR or_sub= having_or_subexpr
            	    {
            	    OR69=(Token)match(input,OR,FOLLOW_OR_in_having_expr1256); 
            	    OR69_tree = 
            	    (Object)adaptor.create(OR69)
            	    ;
            	    adaptor.addChild(root_0, OR69_tree);


            	    pushFollow(FOLLOW_having_or_subexpr_in_having_expr1261);
            	    or_sub=having_or_subexpr();

            	    state._fsp--;

            	    adaptor.addChild(root_0, or_sub.getTree());


            	    	retval.expression = retval.expression.orExp(or_sub.expression);
            	    	

            	    }
            	    break;

            	default :
            	    break loop42;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

        catch (RecognitionException el) {
          throw el;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "having_expr"


    public static class having_or_subexpr_return extends ParserRuleReturnScope {
        public Expression expression;
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "having_or_subexpr"
    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:396:1: having_or_subexpr returns [Expression expression] : and_sub= having_and_subexpr ( AND and_sub= having_and_subexpr )* ;
    public final milesqlParser.having_or_subexpr_return having_or_subexpr() throws RecognitionException {
        milesqlParser.having_or_subexpr_return retval = new milesqlParser.having_or_subexpr_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token AND70=null;
        milesqlParser.having_and_subexpr_return and_sub =null;


        Object AND70_tree=null;

        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:397:2: (and_sub= having_and_subexpr ( AND and_sub= having_and_subexpr )* )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:397:4: and_sub= having_and_subexpr ( AND and_sub= having_and_subexpr )*
            {
            root_0 = (Object)adaptor.nil();


            pushFollow(FOLLOW_having_and_subexpr_in_having_or_subexpr1285);
            and_sub=having_and_subexpr();

            state._fsp--;

            adaptor.addChild(root_0, and_sub.getTree());


            	retval.expression =and_sub.expression;
            	

            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:401:2: ( AND and_sub= having_and_subexpr )*
            loop43:
            do {
                int alt43=2;
                int LA43_0 = input.LA(1);

                if ( (LA43_0==AND) ) {
                    alt43=1;
                }


                switch (alt43) {
            	case 1 :
            	    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:401:3: AND and_sub= having_and_subexpr
            	    {
            	    AND70=(Token)match(input,AND,FOLLOW_AND_in_having_or_subexpr1292); 
            	    AND70_tree = 
            	    (Object)adaptor.create(AND70)
            	    ;
            	    adaptor.addChild(root_0, AND70_tree);


            	    pushFollow(FOLLOW_having_and_subexpr_in_having_or_subexpr1296);
            	    and_sub=having_and_subexpr();

            	    state._fsp--;

            	    adaptor.addChild(root_0, and_sub.getTree());


            	    	retval.expression = retval.expression.andExp(and_sub.expression);
            	    	

            	    }
            	    break;

            	default :
            	    break loop43;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

        catch (RecognitionException el) {
          throw el;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "having_or_subexpr"


    public static class having_and_subexpr_return extends ParserRuleReturnScope {
        public Expression expression;
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "having_and_subexpr"
    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:407:1: having_and_subexpr returns [Expression expression] : (eq_sub= having_eq | LPAREN expr_sub= having_expr RPAREN );
    public final milesqlParser.having_and_subexpr_return having_and_subexpr() throws RecognitionException {
        milesqlParser.having_and_subexpr_return retval = new milesqlParser.having_and_subexpr_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token LPAREN71=null;
        Token RPAREN72=null;
        milesqlParser.having_eq_return eq_sub =null;

        milesqlParser.having_expr_return expr_sub =null;


        Object LPAREN71_tree=null;
        Object RPAREN72_tree=null;

        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:408:2: (eq_sub= having_eq | LPAREN expr_sub= having_expr RPAREN )
            int alt44=2;
            int LA44_0 = input.LA(1);

            if ( (LA44_0==AVG||LA44_0==COUNT||LA44_0==ID||LA44_0==INTEGER||(LA44_0 >= MAX && LA44_0 <= MIN)||(LA44_0 >= SQUARESUM && LA44_0 <= STDDEV)||LA44_0==SUM||LA44_0==VARIANCE) ) {
                alt44=1;
            }
            else if ( (LA44_0==LPAREN) ) {
                alt44=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 44, 0, input);

                throw nvae;

            }
            switch (alt44) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:408:4: eq_sub= having_eq
                    {
                    root_0 = (Object)adaptor.nil();


                    pushFollow(FOLLOW_having_eq_in_having_and_subexpr1319);
                    eq_sub=having_eq();

                    state._fsp--;

                    adaptor.addChild(root_0, eq_sub.getTree());


                    	retval.expression = eq_sub.columnExp;
                    	

                    }
                    break;
                case 2 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:412:4: LPAREN expr_sub= having_expr RPAREN
                    {
                    root_0 = (Object)adaptor.nil();


                    LPAREN71=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_having_and_subexpr1327); 
                    LPAREN71_tree = 
                    (Object)adaptor.create(LPAREN71)
                    ;
                    adaptor.addChild(root_0, LPAREN71_tree);


                    pushFollow(FOLLOW_having_expr_in_having_and_subexpr1331);
                    expr_sub=having_expr();

                    state._fsp--;

                    adaptor.addChild(root_0, expr_sub.getTree());

                    RPAREN72=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_having_and_subexpr1333); 
                    RPAREN72_tree = 
                    (Object)adaptor.create(RPAREN72)
                    ;
                    adaptor.addChild(root_0, RPAREN72_tree);



                    	retval.expression = expr_sub.expression;
                    	

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

        catch (RecognitionException el) {
          throw el;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "having_and_subexpr"


    public static class selectexpr_return extends ParserRuleReturnScope {
        public String columnname;
        public String refname;
        public byte functionId;
        public String functionName;
        public Expression withinExpr;
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "selectexpr"
    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:418:1: selectexpr returns [String columnname,String refname, byte functionId ,String functionName, Expression withinExpr] : (column_name= ( ID | INTEGER ) | (funcname= SUM LPAREN column_name= ( ID | INTEGER ) RPAREN |funcname= MAX LPAREN column_name= ( ID | INTEGER ) RPAREN |funcname= MIN LPAREN column_name= ( ID | INTEGER ) RPAREN |funcname= COUNT LPAREN column_name= ( ID | INTEGER | ASTERISK ) RPAREN |funcname= AVG LPAREN column_name= ( ID | INTEGER | ASTERISK ) RPAREN |funcname= SQUARESUM LPAREN column_name= ( ID | INTEGER | ASTERISK ) RPAREN |funcname= VARIANCE LPAREN column_name= ( ID | INTEGER | ASTERISK ) RPAREN |funcname= STDDEV LPAREN column_name= ( ID | INTEGER | ASTERISK ) RPAREN |funcname= COUNT LPAREN DISTINCT column_name= ( ID | INTEGER ) RPAREN ) ( WITHIN within_expr= expr )? );
    public final milesqlParser.selectexpr_return selectexpr() throws RecognitionException {
        milesqlParser.selectexpr_return retval = new milesqlParser.selectexpr_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token column_name=null;
        Token funcname=null;
        Token LPAREN73=null;
        Token RPAREN74=null;
        Token LPAREN75=null;
        Token RPAREN76=null;
        Token LPAREN77=null;
        Token RPAREN78=null;
        Token LPAREN79=null;
        Token RPAREN80=null;
        Token LPAREN81=null;
        Token RPAREN82=null;
        Token LPAREN83=null;
        Token RPAREN84=null;
        Token LPAREN85=null;
        Token RPAREN86=null;
        Token LPAREN87=null;
        Token RPAREN88=null;
        Token LPAREN89=null;
        Token DISTINCT90=null;
        Token RPAREN91=null;
        Token WITHIN92=null;
        milesqlParser.expr_return within_expr =null;


        Object column_name_tree=null;
        Object funcname_tree=null;
        Object LPAREN73_tree=null;
        Object RPAREN74_tree=null;
        Object LPAREN75_tree=null;
        Object RPAREN76_tree=null;
        Object LPAREN77_tree=null;
        Object RPAREN78_tree=null;
        Object LPAREN79_tree=null;
        Object RPAREN80_tree=null;
        Object LPAREN81_tree=null;
        Object RPAREN82_tree=null;
        Object LPAREN83_tree=null;
        Object RPAREN84_tree=null;
        Object LPAREN85_tree=null;
        Object RPAREN86_tree=null;
        Object LPAREN87_tree=null;
        Object RPAREN88_tree=null;
        Object LPAREN89_tree=null;
        Object DISTINCT90_tree=null;
        Object RPAREN91_tree=null;
        Object WITHIN92_tree=null;

        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:419:2: (column_name= ( ID | INTEGER ) | (funcname= SUM LPAREN column_name= ( ID | INTEGER ) RPAREN |funcname= MAX LPAREN column_name= ( ID | INTEGER ) RPAREN |funcname= MIN LPAREN column_name= ( ID | INTEGER ) RPAREN |funcname= COUNT LPAREN column_name= ( ID | INTEGER | ASTERISK ) RPAREN |funcname= AVG LPAREN column_name= ( ID | INTEGER | ASTERISK ) RPAREN |funcname= SQUARESUM LPAREN column_name= ( ID | INTEGER | ASTERISK ) RPAREN |funcname= VARIANCE LPAREN column_name= ( ID | INTEGER | ASTERISK ) RPAREN |funcname= STDDEV LPAREN column_name= ( ID | INTEGER | ASTERISK ) RPAREN |funcname= COUNT LPAREN DISTINCT column_name= ( ID | INTEGER ) RPAREN ) ( WITHIN within_expr= expr )? )
            int alt47=2;
            int LA47_0 = input.LA(1);

            if ( (LA47_0==ID||LA47_0==INTEGER) ) {
                alt47=1;
            }
            else if ( (LA47_0==AVG||LA47_0==COUNT||(LA47_0 >= MAX && LA47_0 <= MIN)||(LA47_0 >= SQUARESUM && LA47_0 <= STDDEV)||LA47_0==SUM||LA47_0==VARIANCE) ) {
                alt47=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 47, 0, input);

                throw nvae;

            }
            switch (alt47) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:419:4: column_name= ( ID | INTEGER )
                    {
                    root_0 = (Object)adaptor.nil();


                    column_name=(Token)input.LT(1);

                    if ( input.LA(1)==ID||input.LA(1)==INTEGER ) {
                        input.consume();
                        adaptor.addChild(root_0, 
                        (Object)adaptor.create(column_name)
                        );
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }



                    	retval.columnname = column_name.getText().trim();
                    	

                    }
                    break;
                case 2 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:424:2: (funcname= SUM LPAREN column_name= ( ID | INTEGER ) RPAREN |funcname= MAX LPAREN column_name= ( ID | INTEGER ) RPAREN |funcname= MIN LPAREN column_name= ( ID | INTEGER ) RPAREN |funcname= COUNT LPAREN column_name= ( ID | INTEGER | ASTERISK ) RPAREN |funcname= AVG LPAREN column_name= ( ID | INTEGER | ASTERISK ) RPAREN |funcname= SQUARESUM LPAREN column_name= ( ID | INTEGER | ASTERISK ) RPAREN |funcname= VARIANCE LPAREN column_name= ( ID | INTEGER | ASTERISK ) RPAREN |funcname= STDDEV LPAREN column_name= ( ID | INTEGER | ASTERISK ) RPAREN |funcname= COUNT LPAREN DISTINCT column_name= ( ID | INTEGER ) RPAREN ) ( WITHIN within_expr= expr )?
                    {
                    root_0 = (Object)adaptor.nil();


                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:424:2: (funcname= SUM LPAREN column_name= ( ID | INTEGER ) RPAREN |funcname= MAX LPAREN column_name= ( ID | INTEGER ) RPAREN |funcname= MIN LPAREN column_name= ( ID | INTEGER ) RPAREN |funcname= COUNT LPAREN column_name= ( ID | INTEGER | ASTERISK ) RPAREN |funcname= AVG LPAREN column_name= ( ID | INTEGER | ASTERISK ) RPAREN |funcname= SQUARESUM LPAREN column_name= ( ID | INTEGER | ASTERISK ) RPAREN |funcname= VARIANCE LPAREN column_name= ( ID | INTEGER | ASTERISK ) RPAREN |funcname= STDDEV LPAREN column_name= ( ID | INTEGER | ASTERISK ) RPAREN |funcname= COUNT LPAREN DISTINCT column_name= ( ID | INTEGER ) RPAREN )
                    int alt45=9;
                    switch ( input.LA(1) ) {
                    case SUM:
                        {
                        alt45=1;
                        }
                        break;
                    case MAX:
                        {
                        alt45=2;
                        }
                        break;
                    case MIN:
                        {
                        alt45=3;
                        }
                        break;
                    case COUNT:
                        {
                        int LA45_4 = input.LA(2);

                        if ( (LA45_4==LPAREN) ) {
                            int LA45_9 = input.LA(3);

                            if ( (LA45_9==ASTERISK||LA45_9==ID||LA45_9==INTEGER) ) {
                                alt45=4;
                            }
                            else if ( (LA45_9==DISTINCT) ) {
                                alt45=9;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 45, 9, input);

                                throw nvae;

                            }
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 45, 4, input);

                            throw nvae;

                        }
                        }
                        break;
                    case AVG:
                        {
                        alt45=5;
                        }
                        break;
                    case SQUARESUM:
                        {
                        alt45=6;
                        }
                        break;
                    case VARIANCE:
                        {
                        alt45=7;
                        }
                        break;
                    case STDDEV:
                        {
                        alt45=8;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 45, 0, input);

                        throw nvae;

                    }

                    switch (alt45) {
                        case 1 :
                            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:425:2: funcname= SUM LPAREN column_name= ( ID | INTEGER ) RPAREN
                            {
                            funcname=(Token)match(input,SUM,FOLLOW_SUM_in_selectexpr1374); 
                            funcname_tree = 
                            (Object)adaptor.create(funcname)
                            ;
                            adaptor.addChild(root_0, funcname_tree);


                            LPAREN73=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_selectexpr1376); 
                            LPAREN73_tree = 
                            (Object)adaptor.create(LPAREN73)
                            ;
                            adaptor.addChild(root_0, LPAREN73_tree);


                            column_name=(Token)input.LT(1);

                            if ( input.LA(1)==ID||input.LA(1)==INTEGER ) {
                                input.consume();
                                adaptor.addChild(root_0, 
                                (Object)adaptor.create(column_name)
                                );
                                state.errorRecovery=false;
                            }
                            else {
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                throw mse;
                            }


                            RPAREN74=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_selectexpr1386); 
                            RPAREN74_tree = 
                            (Object)adaptor.create(RPAREN74)
                            ;
                            adaptor.addChild(root_0, RPAREN74_tree);



                            	retval.columnname = "SUM ("+column_name.getText().trim()+")";
                            	retval.refname = column_name.getText().trim();
                            	retval.functionId = Constants.FUNC_SUM;
                            	retval.functionName = "SUM";
                            	

                            }
                            break;
                        case 2 :
                            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:432:4: funcname= MAX LPAREN column_name= ( ID | INTEGER ) RPAREN
                            {
                            funcname=(Token)match(input,MAX,FOLLOW_MAX_in_selectexpr1398); 
                            funcname_tree = 
                            (Object)adaptor.create(funcname)
                            ;
                            adaptor.addChild(root_0, funcname_tree);


                            LPAREN75=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_selectexpr1401); 
                            LPAREN75_tree = 
                            (Object)adaptor.create(LPAREN75)
                            ;
                            adaptor.addChild(root_0, LPAREN75_tree);


                            column_name=(Token)input.LT(1);

                            if ( input.LA(1)==ID||input.LA(1)==INTEGER ) {
                                input.consume();
                                adaptor.addChild(root_0, 
                                (Object)adaptor.create(column_name)
                                );
                                state.errorRecovery=false;
                            }
                            else {
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                throw mse;
                            }


                            RPAREN76=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_selectexpr1411); 
                            RPAREN76_tree = 
                            (Object)adaptor.create(RPAREN76)
                            ;
                            adaptor.addChild(root_0, RPAREN76_tree);



                            	retval.columnname ="MAX ("+column_name.getText().trim()+")";
                            	retval.refname = column_name.getText().trim();
                            	retval.functionId = Constants.FUNC_MAX;
                            	retval.functionName = "MAX";
                            	

                            }
                            break;
                        case 3 :
                            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:439:4: funcname= MIN LPAREN column_name= ( ID | INTEGER ) RPAREN
                            {
                            funcname=(Token)match(input,MIN,FOLLOW_MIN_in_selectexpr1423); 
                            funcname_tree = 
                            (Object)adaptor.create(funcname)
                            ;
                            adaptor.addChild(root_0, funcname_tree);


                            LPAREN77=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_selectexpr1426); 
                            LPAREN77_tree = 
                            (Object)adaptor.create(LPAREN77)
                            ;
                            adaptor.addChild(root_0, LPAREN77_tree);


                            column_name=(Token)input.LT(1);

                            if ( input.LA(1)==ID||input.LA(1)==INTEGER ) {
                                input.consume();
                                adaptor.addChild(root_0, 
                                (Object)adaptor.create(column_name)
                                );
                                state.errorRecovery=false;
                            }
                            else {
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                throw mse;
                            }


                            RPAREN78=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_selectexpr1436); 
                            RPAREN78_tree = 
                            (Object)adaptor.create(RPAREN78)
                            ;
                            adaptor.addChild(root_0, RPAREN78_tree);



                            	retval.columnname = "MIN ("+column_name.getText().trim()+")";
                            	retval.refname = column_name.getText().trim();
                            	retval.functionId = Constants.FUNC_MIN;
                            	retval.functionName = "MIN";
                            	

                            }
                            break;
                        case 4 :
                            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:446:4: funcname= COUNT LPAREN column_name= ( ID | INTEGER | ASTERISK ) RPAREN
                            {
                            funcname=(Token)match(input,COUNT,FOLLOW_COUNT_in_selectexpr1448); 
                            funcname_tree = 
                            (Object)adaptor.create(funcname)
                            ;
                            adaptor.addChild(root_0, funcname_tree);


                            LPAREN79=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_selectexpr1450); 
                            LPAREN79_tree = 
                            (Object)adaptor.create(LPAREN79)
                            ;
                            adaptor.addChild(root_0, LPAREN79_tree);


                            column_name=(Token)input.LT(1);

                            if ( input.LA(1)==ASTERISK||input.LA(1)==ID||input.LA(1)==INTEGER ) {
                                input.consume();
                                adaptor.addChild(root_0, 
                                (Object)adaptor.create(column_name)
                                );
                                state.errorRecovery=false;
                            }
                            else {
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                throw mse;
                            }


                            RPAREN80=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_selectexpr1462); 
                            RPAREN80_tree = 
                            (Object)adaptor.create(RPAREN80)
                            ;
                            adaptor.addChild(root_0, RPAREN80_tree);



                            	retval.columnname ="COUNT ("+column_name.getText().trim()+")";
                            	retval.refname = column_name.getText().trim();
                            	retval.functionId = Constants.FUNC_COUNT;
                            	retval.functionName = "COUNT";
                            	

                            }
                            break;
                        case 5 :
                            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:453:4: funcname= AVG LPAREN column_name= ( ID | INTEGER | ASTERISK ) RPAREN
                            {
                            funcname=(Token)match(input,AVG,FOLLOW_AVG_in_selectexpr1474); 
                            funcname_tree = 
                            (Object)adaptor.create(funcname)
                            ;
                            adaptor.addChild(root_0, funcname_tree);


                            LPAREN81=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_selectexpr1476); 
                            LPAREN81_tree = 
                            (Object)adaptor.create(LPAREN81)
                            ;
                            adaptor.addChild(root_0, LPAREN81_tree);


                            column_name=(Token)input.LT(1);

                            if ( input.LA(1)==ASTERISK||input.LA(1)==ID||input.LA(1)==INTEGER ) {
                                input.consume();
                                adaptor.addChild(root_0, 
                                (Object)adaptor.create(column_name)
                                );
                                state.errorRecovery=false;
                            }
                            else {
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                throw mse;
                            }


                            RPAREN82=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_selectexpr1488); 
                            RPAREN82_tree = 
                            (Object)adaptor.create(RPAREN82)
                            ;
                            adaptor.addChild(root_0, RPAREN82_tree);



                            	retval.columnname ="AVG ("+column_name.getText().trim()+")";
                            	retval.refname = column_name.getText().trim();
                            	retval.functionId = Constants.FUNC_AVG;
                            	retval.functionName = "AVG";
                            	

                            }
                            break;
                        case 6 :
                            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:460:4: funcname= SQUARESUM LPAREN column_name= ( ID | INTEGER | ASTERISK ) RPAREN
                            {
                            funcname=(Token)match(input,SQUARESUM,FOLLOW_SQUARESUM_in_selectexpr1500); 
                            funcname_tree = 
                            (Object)adaptor.create(funcname)
                            ;
                            adaptor.addChild(root_0, funcname_tree);


                            LPAREN83=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_selectexpr1502); 
                            LPAREN83_tree = 
                            (Object)adaptor.create(LPAREN83)
                            ;
                            adaptor.addChild(root_0, LPAREN83_tree);


                            column_name=(Token)input.LT(1);

                            if ( input.LA(1)==ASTERISK||input.LA(1)==ID||input.LA(1)==INTEGER ) {
                                input.consume();
                                adaptor.addChild(root_0, 
                                (Object)adaptor.create(column_name)
                                );
                                state.errorRecovery=false;
                            }
                            else {
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                throw mse;
                            }


                            RPAREN84=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_selectexpr1514); 
                            RPAREN84_tree = 
                            (Object)adaptor.create(RPAREN84)
                            ;
                            adaptor.addChild(root_0, RPAREN84_tree);



                            	retval.columnname ="SQUARESUM ("+column_name.getText().trim()+")";
                            	retval.refname = column_name.getText().trim();
                            	retval.functionId = Constants.FUNC_SQUARE_SUM;
                            	retval.functionName = "SQUARESUM";
                            	

                            }
                            break;
                        case 7 :
                            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:467:4: funcname= VARIANCE LPAREN column_name= ( ID | INTEGER | ASTERISK ) RPAREN
                            {
                            funcname=(Token)match(input,VARIANCE,FOLLOW_VARIANCE_in_selectexpr1526); 
                            funcname_tree = 
                            (Object)adaptor.create(funcname)
                            ;
                            adaptor.addChild(root_0, funcname_tree);


                            LPAREN85=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_selectexpr1528); 
                            LPAREN85_tree = 
                            (Object)adaptor.create(LPAREN85)
                            ;
                            adaptor.addChild(root_0, LPAREN85_tree);


                            column_name=(Token)input.LT(1);

                            if ( input.LA(1)==ASTERISK||input.LA(1)==ID||input.LA(1)==INTEGER ) {
                                input.consume();
                                adaptor.addChild(root_0, 
                                (Object)adaptor.create(column_name)
                                );
                                state.errorRecovery=false;
                            }
                            else {
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                throw mse;
                            }


                            RPAREN86=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_selectexpr1540); 
                            RPAREN86_tree = 
                            (Object)adaptor.create(RPAREN86)
                            ;
                            adaptor.addChild(root_0, RPAREN86_tree);



                            	retval.columnname ="VARIANCE ("+column_name.getText().trim()+")";
                            	retval.refname = column_name.getText().trim();
                            	retval.functionId = Constants.FUNC_VAR;
                            	retval.functionName = "VARIANCE";
                            	

                            }
                            break;
                        case 8 :
                            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:474:4: funcname= STDDEV LPAREN column_name= ( ID | INTEGER | ASTERISK ) RPAREN
                            {
                            funcname=(Token)match(input,STDDEV,FOLLOW_STDDEV_in_selectexpr1552); 
                            funcname_tree = 
                            (Object)adaptor.create(funcname)
                            ;
                            adaptor.addChild(root_0, funcname_tree);


                            LPAREN87=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_selectexpr1554); 
                            LPAREN87_tree = 
                            (Object)adaptor.create(LPAREN87)
                            ;
                            adaptor.addChild(root_0, LPAREN87_tree);


                            column_name=(Token)input.LT(1);

                            if ( input.LA(1)==ASTERISK||input.LA(1)==ID||input.LA(1)==INTEGER ) {
                                input.consume();
                                adaptor.addChild(root_0, 
                                (Object)adaptor.create(column_name)
                                );
                                state.errorRecovery=false;
                            }
                            else {
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                throw mse;
                            }


                            RPAREN88=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_selectexpr1566); 
                            RPAREN88_tree = 
                            (Object)adaptor.create(RPAREN88)
                            ;
                            adaptor.addChild(root_0, RPAREN88_tree);



                            	retval.columnname ="STDDEV ("+column_name.getText().trim()+")";
                            	retval.refname = column_name.getText().trim();
                            	retval.functionId = Constants.FUNC_STD;
                            	retval.functionName = "STDDEV";
                            	

                            }
                            break;
                        case 9 :
                            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:481:4: funcname= COUNT LPAREN DISTINCT column_name= ( ID | INTEGER ) RPAREN
                            {
                            funcname=(Token)match(input,COUNT,FOLLOW_COUNT_in_selectexpr1578); 
                            funcname_tree = 
                            (Object)adaptor.create(funcname)
                            ;
                            adaptor.addChild(root_0, funcname_tree);


                            LPAREN89=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_selectexpr1580); 
                            LPAREN89_tree = 
                            (Object)adaptor.create(LPAREN89)
                            ;
                            adaptor.addChild(root_0, LPAREN89_tree);


                            DISTINCT90=(Token)match(input,DISTINCT,FOLLOW_DISTINCT_in_selectexpr1582); 
                            DISTINCT90_tree = 
                            (Object)adaptor.create(DISTINCT90)
                            ;
                            adaptor.addChild(root_0, DISTINCT90_tree);


                            column_name=(Token)input.LT(1);

                            if ( input.LA(1)==ID||input.LA(1)==INTEGER ) {
                                input.consume();
                                adaptor.addChild(root_0, 
                                (Object)adaptor.create(column_name)
                                );
                                state.errorRecovery=false;
                            }
                            else {
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                throw mse;
                            }


                            RPAREN91=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_selectexpr1592); 
                            RPAREN91_tree = 
                            (Object)adaptor.create(RPAREN91)
                            ;
                            adaptor.addChild(root_0, RPAREN91_tree);



                            	retval.columnname = "COUNT (DISTINCT "+column_name.getText().trim()+")";
                            	retval.refname = column_name.getText().trim();
                            	retval.functionId = Constants.FUNC_DISTINCT_COUNT;
                            	retval.functionName = "COUNT DISTINCT";
                            	

                            }
                            break;

                    }


                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:489:2: ( WITHIN within_expr= expr )?
                    int alt46=2;
                    int LA46_0 = input.LA(1);

                    if ( (LA46_0==WITHIN) ) {
                        alt46=1;
                    }
                    switch (alt46) {
                        case 1 :
                            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:490:3: WITHIN within_expr= expr
                            {
                            WITHIN92=(Token)match(input,WITHIN,FOLLOW_WITHIN_in_selectexpr1605); 
                            WITHIN92_tree = 
                            (Object)adaptor.create(WITHIN92)
                            ;
                            adaptor.addChild(root_0, WITHIN92_tree);


                            pushFollow(FOLLOW_expr_in_selectexpr1611);
                            within_expr=expr();

                            state._fsp--;

                            adaptor.addChild(root_0, within_expr.getTree());


                            	 retval.withinExpr = within_expr.expression;
                            	 

                            }
                            break;

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

        catch (RecognitionException el) {
          throw el;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "selectexpr"


    public static class eq_subexpr_return extends ParserRuleReturnScope {
        public Expression expression;
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "eq_subexpr"
    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:498:1: eq_subexpr returns [Expression expression] : (column_name= ( ID | INTEGER ) leq= ( LESS | LESS_OR_EQ | GREATER | GREATER_OR_EQ | EQUALS | NOT_EQUALS1 | NOT_EQUALS2 ) lv= literal_value |column_name2= ( ID | INTEGER ) BETWEEN ll= ( LPAREN | LPAREN_SQUARE ) lv1= literal_value COMMA lv2= literal_value rr= ( RPAREN | RPAREN_SQUARE ) |column_name3= ( ID | INTEGER ) IN LPAREN lv3= literal_value ( COMMA lv4= literal_value )* RPAREN |column_name5= ( ID | INTEGER ) MATCH LPAREN lv5= literal_value ( COMMA lv6= literal_value )* RPAREN |column_name4= ( ID | INTEGER ) IN LPAREN ss= select_stmt RPAREN );
    public final milesqlParser.eq_subexpr_return eq_subexpr() throws RecognitionException {
        milesqlParser.eq_subexpr_return retval = new milesqlParser.eq_subexpr_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token column_name=null;
        Token leq=null;
        Token column_name2=null;
        Token ll=null;
        Token rr=null;
        Token column_name3=null;
        Token column_name5=null;
        Token column_name4=null;
        Token BETWEEN93=null;
        Token COMMA94=null;
        Token IN95=null;
        Token LPAREN96=null;
        Token COMMA97=null;
        Token RPAREN98=null;
        Token MATCH99=null;
        Token LPAREN100=null;
        Token COMMA101=null;
        Token RPAREN102=null;
        Token IN103=null;
        Token LPAREN104=null;
        Token RPAREN105=null;
        milesqlParser.literal_value_return lv =null;

        milesqlParser.literal_value_return lv1 =null;

        milesqlParser.literal_value_return lv2 =null;

        milesqlParser.literal_value_return lv3 =null;

        milesqlParser.literal_value_return lv4 =null;

        milesqlParser.literal_value_return lv5 =null;

        milesqlParser.literal_value_return lv6 =null;

        milesqlParser.select_stmt_return ss =null;


        Object column_name_tree=null;
        Object leq_tree=null;
        Object column_name2_tree=null;
        Object ll_tree=null;
        Object rr_tree=null;
        Object column_name3_tree=null;
        Object column_name5_tree=null;
        Object column_name4_tree=null;
        Object BETWEEN93_tree=null;
        Object COMMA94_tree=null;
        Object IN95_tree=null;
        Object LPAREN96_tree=null;
        Object COMMA97_tree=null;
        Object RPAREN98_tree=null;
        Object MATCH99_tree=null;
        Object LPAREN100_tree=null;
        Object COMMA101_tree=null;
        Object RPAREN102_tree=null;
        Object IN103_tree=null;
        Object LPAREN104_tree=null;
        Object RPAREN105_tree=null;

        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:499:2: (column_name= ( ID | INTEGER ) leq= ( LESS | LESS_OR_EQ | GREATER | GREATER_OR_EQ | EQUALS | NOT_EQUALS1 | NOT_EQUALS2 ) lv= literal_value |column_name2= ( ID | INTEGER ) BETWEEN ll= ( LPAREN | LPAREN_SQUARE ) lv1= literal_value COMMA lv2= literal_value rr= ( RPAREN | RPAREN_SQUARE ) |column_name3= ( ID | INTEGER ) IN LPAREN lv3= literal_value ( COMMA lv4= literal_value )* RPAREN |column_name5= ( ID | INTEGER ) MATCH LPAREN lv5= literal_value ( COMMA lv6= literal_value )* RPAREN |column_name4= ( ID | INTEGER ) IN LPAREN ss= select_stmt RPAREN )
            int alt50=5;
            int LA50_0 = input.LA(1);

            if ( (LA50_0==ID||LA50_0==INTEGER) ) {
                switch ( input.LA(2) ) {
                case EQUALS:
                case GREATER:
                case GREATER_OR_EQ:
                case LESS:
                case LESS_OR_EQ:
                case NOT_EQUALS1:
                case NOT_EQUALS2:
                    {
                    alt50=1;
                    }
                    break;
                case BETWEEN:
                    {
                    alt50=2;
                    }
                    break;
                case IN:
                    {
                    int LA50_4 = input.LA(3);

                    if ( (LA50_4==LPAREN) ) {
                        int LA50_6 = input.LA(4);

                        if ( (LA50_6==FLOAT||LA50_6==INTEGER||LA50_6==QUESTION||LA50_6==STRING) ) {
                            alt50=3;
                        }
                        else if ( (LA50_6==SELECT) ) {
                            alt50=5;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 50, 6, input);

                            throw nvae;

                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 50, 4, input);

                        throw nvae;

                    }
                    }
                    break;
                case MATCH:
                    {
                    alt50=4;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 50, 1, input);

                    throw nvae;

                }

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 50, 0, input);

                throw nvae;

            }
            switch (alt50) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:499:4: column_name= ( ID | INTEGER ) leq= ( LESS | LESS_OR_EQ | GREATER | GREATER_OR_EQ | EQUALS | NOT_EQUALS1 | NOT_EQUALS2 ) lv= literal_value
                    {
                    root_0 = (Object)adaptor.nil();


                    column_name=(Token)input.LT(1);

                    if ( input.LA(1)==ID||input.LA(1)==INTEGER ) {
                        input.consume();
                        adaptor.addChild(root_0, 
                        (Object)adaptor.create(column_name)
                        );
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    leq=(Token)input.LT(1);

                    if ( input.LA(1)==EQUALS||(input.LA(1) >= GREATER && input.LA(1) <= GREATER_OR_EQ)||(input.LA(1) >= LESS && input.LA(1) <= LESS_OR_EQ)||(input.LA(1) >= NOT_EQUALS1 && input.LA(1) <= NOT_EQUALS2) ) {
                        input.consume();
                        adaptor.addChild(root_0, 
                        (Object)adaptor.create(leq)
                        );
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    pushFollow(FOLLOW_literal_value_in_eq_subexpr1678);
                    lv=literal_value();

                    state._fsp--;

                    adaptor.addChild(root_0, lv.getTree());


                    	ColumnExp columnExp = new ColumnExp();
                    	retval.expression = columnExp;
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
                    break;
                case 2 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:525:4: column_name2= ( ID | INTEGER ) BETWEEN ll= ( LPAREN | LPAREN_SQUARE ) lv1= literal_value COMMA lv2= literal_value rr= ( RPAREN | RPAREN_SQUARE )
                    {
                    root_0 = (Object)adaptor.nil();


                    column_name2=(Token)input.LT(1);

                    if ( input.LA(1)==ID||input.LA(1)==INTEGER ) {
                        input.consume();
                        adaptor.addChild(root_0, 
                        (Object)adaptor.create(column_name2)
                        );
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    BETWEEN93=(Token)match(input,BETWEEN,FOLLOW_BETWEEN_in_eq_subexpr1695); 
                    BETWEEN93_tree = 
                    (Object)adaptor.create(BETWEEN93)
                    ;
                    adaptor.addChild(root_0, BETWEEN93_tree);


                    ll=(Token)input.LT(1);

                    if ( (input.LA(1) >= LPAREN && input.LA(1) <= LPAREN_SQUARE) ) {
                        input.consume();
                        adaptor.addChild(root_0, 
                        (Object)adaptor.create(ll)
                        );
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    pushFollow(FOLLOW_literal_value_in_eq_subexpr1707);
                    lv1=literal_value();

                    state._fsp--;

                    adaptor.addChild(root_0, lv1.getTree());

                    COMMA94=(Token)match(input,COMMA,FOLLOW_COMMA_in_eq_subexpr1709); 
                    COMMA94_tree = 
                    (Object)adaptor.create(COMMA94)
                    ;
                    adaptor.addChild(root_0, COMMA94_tree);


                    pushFollow(FOLLOW_literal_value_in_eq_subexpr1713);
                    lv2=literal_value();

                    state._fsp--;

                    adaptor.addChild(root_0, lv2.getTree());

                    rr=(Token)input.LT(1);

                    if ( (input.LA(1) >= RPAREN && input.LA(1) <= RPAREN_SQUARE) ) {
                        input.consume();
                        adaptor.addChild(root_0, 
                        (Object)adaptor.create(rr)
                        );
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }



                    	ColumnExp columnExp = new ColumnExp();
                    	retval.expression = columnExp;
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
                    break;
                case 3 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:547:4: column_name3= ( ID | INTEGER ) IN LPAREN lv3= literal_value ( COMMA lv4= literal_value )* RPAREN
                    {
                    root_0 = (Object)adaptor.nil();


                    column_name3=(Token)input.LT(1);

                    if ( input.LA(1)==ID||input.LA(1)==INTEGER ) {
                        input.consume();
                        adaptor.addChild(root_0, 
                        (Object)adaptor.create(column_name3)
                        );
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    IN95=(Token)match(input,IN,FOLLOW_IN_in_eq_subexpr1738); 
                    IN95_tree = 
                    (Object)adaptor.create(IN95)
                    ;
                    adaptor.addChild(root_0, IN95_tree);


                    LPAREN96=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_eq_subexpr1740); 
                    LPAREN96_tree = 
                    (Object)adaptor.create(LPAREN96)
                    ;
                    adaptor.addChild(root_0, LPAREN96_tree);


                    pushFollow(FOLLOW_literal_value_in_eq_subexpr1744);
                    lv3=literal_value();

                    state._fsp--;

                    adaptor.addChild(root_0, lv3.getTree());


                    	ColumnExp columnExp = new ColumnExp();
                    	retval.expression = columnExp;
                    	FieldDesc fd =new FieldDesc();
                    	fd.fieldName = column_name3.getText().trim();
                           	columnExp.values = new ArrayList();
                            columnExp.values.add(lv3.valueDesc);
                    	columnExp.column = fd;
                    	columnExp.comparetor = Constants.EXP_COMPARE_IN;
                    	

                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:558:2: ( COMMA lv4= literal_value )*
                    loop48:
                    do {
                        int alt48=2;
                        int LA48_0 = input.LA(1);

                        if ( (LA48_0==COMMA) ) {
                            alt48=1;
                        }


                        switch (alt48) {
                    	case 1 :
                    	    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:558:3: COMMA lv4= literal_value
                    	    {
                    	    COMMA97=(Token)match(input,COMMA,FOLLOW_COMMA_in_eq_subexpr1752); 
                    	    COMMA97_tree = 
                    	    (Object)adaptor.create(COMMA97)
                    	    ;
                    	    adaptor.addChild(root_0, COMMA97_tree);


                    	    pushFollow(FOLLOW_literal_value_in_eq_subexpr1756);
                    	    lv4=literal_value();

                    	    state._fsp--;

                    	    adaptor.addChild(root_0, lv4.getTree());


                    	    	columnExp.values.add(lv4.valueDesc);
                    	    	

                    	    }
                    	    break;

                    	default :
                    	    break loop48;
                        }
                    } while (true);


                    RPAREN98=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_eq_subexpr1764); 
                    RPAREN98_tree = 
                    (Object)adaptor.create(RPAREN98)
                    ;
                    adaptor.addChild(root_0, RPAREN98_tree);


                    }
                    break;
                case 4 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:562:4: column_name5= ( ID | INTEGER ) MATCH LPAREN lv5= literal_value ( COMMA lv6= literal_value )* RPAREN
                    {
                    root_0 = (Object)adaptor.nil();


                    column_name5=(Token)input.LT(1);

                    if ( input.LA(1)==ID||input.LA(1)==INTEGER ) {
                        input.consume();
                        adaptor.addChild(root_0, 
                        (Object)adaptor.create(column_name5)
                        );
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    MATCH99=(Token)match(input,MATCH,FOLLOW_MATCH_in_eq_subexpr1777); 
                    MATCH99_tree = 
                    (Object)adaptor.create(MATCH99)
                    ;
                    adaptor.addChild(root_0, MATCH99_tree);


                    LPAREN100=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_eq_subexpr1779); 
                    LPAREN100_tree = 
                    (Object)adaptor.create(LPAREN100)
                    ;
                    adaptor.addChild(root_0, LPAREN100_tree);


                    pushFollow(FOLLOW_literal_value_in_eq_subexpr1783);
                    lv5=literal_value();

                    state._fsp--;

                    adaptor.addChild(root_0, lv5.getTree());


                    	ColumnExp columnExp = new ColumnExp();
                    	retval.expression = columnExp;
                    	FieldDesc fd =new FieldDesc();
                    	fd.fieldName = "$" + column_name5.getText().trim();
                        columnExp.values = new ArrayList();
                        columnExp.values.add(lv5.valueDesc);
                    	columnExp.column = fd;
                    	columnExp.comparetor = Constants.EXP_COMPARE_MATCH;
                    	

                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:573:2: ( COMMA lv6= literal_value )*
                    loop49:
                    do {
                        int alt49=2;
                        int LA49_0 = input.LA(1);

                        if ( (LA49_0==COMMA) ) {
                            alt49=1;
                        }


                        switch (alt49) {
                    	case 1 :
                    	    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:573:3: COMMA lv6= literal_value
                    	    {
                    	    COMMA101=(Token)match(input,COMMA,FOLLOW_COMMA_in_eq_subexpr1791); 
                    	    COMMA101_tree = 
                    	    (Object)adaptor.create(COMMA101)
                    	    ;
                    	    adaptor.addChild(root_0, COMMA101_tree);


                    	    pushFollow(FOLLOW_literal_value_in_eq_subexpr1795);
                    	    lv6=literal_value();

                    	    state._fsp--;

                    	    adaptor.addChild(root_0, lv6.getTree());


                    	    	columnExp.values.add(lv6.valueDesc);
                    	    	

                    	    }
                    	    break;

                    	default :
                    	    break loop49;
                        }
                    } while (true);


                    RPAREN102=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_eq_subexpr1804); 
                    RPAREN102_tree = 
                    (Object)adaptor.create(RPAREN102)
                    ;
                    adaptor.addChild(root_0, RPAREN102_tree);


                    }
                    break;
                case 5 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:578:4: column_name4= ( ID | INTEGER ) IN LPAREN ss= select_stmt RPAREN
                    {
                    root_0 = (Object)adaptor.nil();


                    column_name4=(Token)input.LT(1);

                    if ( input.LA(1)==ID||input.LA(1)==INTEGER ) {
                        input.consume();
                        adaptor.addChild(root_0, 
                        (Object)adaptor.create(column_name4)
                        );
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    IN103=(Token)match(input,IN,FOLLOW_IN_in_eq_subexpr1818); 
                    IN103_tree = 
                    (Object)adaptor.create(IN103)
                    ;
                    adaptor.addChild(root_0, IN103_tree);


                    LPAREN104=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_eq_subexpr1820); 
                    LPAREN104_tree = 
                    (Object)adaptor.create(LPAREN104)
                    ;
                    adaptor.addChild(root_0, LPAREN104_tree);


                    pushFollow(FOLLOW_select_stmt_in_eq_subexpr1824);
                    ss=select_stmt();

                    state._fsp--;

                    adaptor.addChild(root_0, ss.getTree());

                    RPAREN105=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_eq_subexpr1826); 
                    RPAREN105_tree = 
                    (Object)adaptor.create(RPAREN105)
                    ;
                    adaptor.addChild(root_0, RPAREN105_tree);



                    	ColumnSubSelectExp columnSubSelectExp = new ColumnSubSelectExp();
                    	retval.expression = columnSubSelectExp;
                    	FieldDesc fd =new FieldDesc();
                    	fd.fieldName = column_name4.getText().trim();
                           	columnSubSelectExp.subQueryStatement = ss.queryStatement;
                    	columnSubSelectExp.column = fd;
                    	columnSubSelectExp.comparetor = Constants.EXP_COMPARE_IN;
                    	

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

        catch (RecognitionException el) {
          throw el;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "eq_subexpr"


    public static class eq_sequence_return extends ParserRuleReturnScope {
        public List<FieldValuePair> fieldValues = new ArrayList<FieldValuePair>();
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "eq_sequence"
    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:590:1: eq_sequence returns [List<FieldValuePair> fieldValues = new ArrayList<FieldValuePair>()] : column_name= ( ID | INTEGER ) EQUALS lv= literal_value ( COMMA column_name2= ( ID | INTEGER ) EQUALS lv2= literal_value )* ;
    public final milesqlParser.eq_sequence_return eq_sequence() throws RecognitionException {
        milesqlParser.eq_sequence_return retval = new milesqlParser.eq_sequence_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token column_name=null;
        Token column_name2=null;
        Token EQUALS106=null;
        Token COMMA107=null;
        Token EQUALS108=null;
        milesqlParser.literal_value_return lv =null;

        milesqlParser.literal_value_return lv2 =null;


        Object column_name_tree=null;
        Object column_name2_tree=null;
        Object EQUALS106_tree=null;
        Object COMMA107_tree=null;
        Object EQUALS108_tree=null;

        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:591:2: (column_name= ( ID | INTEGER ) EQUALS lv= literal_value ( COMMA column_name2= ( ID | INTEGER ) EQUALS lv2= literal_value )* )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:591:4: column_name= ( ID | INTEGER ) EQUALS lv= literal_value ( COMMA column_name2= ( ID | INTEGER ) EQUALS lv2= literal_value )*
            {
            root_0 = (Object)adaptor.nil();


            column_name=(Token)input.LT(1);

            if ( input.LA(1)==ID||input.LA(1)==INTEGER ) {
                input.consume();
                adaptor.addChild(root_0, 
                (Object)adaptor.create(column_name)
                );
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            EQUALS106=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_eq_sequence1854); 
            EQUALS106_tree = 
            (Object)adaptor.create(EQUALS106)
            ;
            adaptor.addChild(root_0, EQUALS106_tree);


            pushFollow(FOLLOW_literal_value_in_eq_sequence1860);
            lv=literal_value();

            state._fsp--;

            adaptor.addChild(root_0, lv.getTree());


            		FieldValuePair fvp = new FieldValuePair();
            		FieldDesc fd = new FieldDesc();
            		fd.fieldName = column_name.getText().trim();
            		fvp.field = fd;
            		fvp.value = lv.valueDesc;
            		retval.fieldValues.add(fvp);
            	

            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:600:2: ( COMMA column_name2= ( ID | INTEGER ) EQUALS lv2= literal_value )*
            loop51:
            do {
                int alt51=2;
                int LA51_0 = input.LA(1);

                if ( (LA51_0==COMMA) ) {
                    alt51=1;
                }


                switch (alt51) {
            	case 1 :
            	    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:600:4: COMMA column_name2= ( ID | INTEGER ) EQUALS lv2= literal_value
            	    {
            	    COMMA107=(Token)match(input,COMMA,FOLLOW_COMMA_in_eq_sequence1868); 
            	    COMMA107_tree = 
            	    (Object)adaptor.create(COMMA107)
            	    ;
            	    adaptor.addChild(root_0, COMMA107_tree);


            	    column_name2=(Token)input.LT(1);

            	    if ( input.LA(1)==ID||input.LA(1)==INTEGER ) {
            	        input.consume();
            	        adaptor.addChild(root_0, 
            	        (Object)adaptor.create(column_name2)
            	        );
            	        state.errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }


            	    EQUALS108=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_eq_sequence1880); 
            	    EQUALS108_tree = 
            	    (Object)adaptor.create(EQUALS108)
            	    ;
            	    adaptor.addChild(root_0, EQUALS108_tree);


            	    pushFollow(FOLLOW_literal_value_in_eq_sequence1886);
            	    lv2=literal_value();

            	    state._fsp--;

            	    adaptor.addChild(root_0, lv2.getTree());


            	    		FieldValuePair fvp2 = new FieldValuePair();
            	    		FieldDesc fd2 = new FieldDesc();
            	    		fd2.fieldName = column_name2.getText().trim();
            	    		fvp2.field = fd2;
            	    		fvp2.value = lv2.valueDesc;
            	    		retval.fieldValues.add(fvp2);
            	    	

            	    }
            	    break;

            	default :
            	    break loop51;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

        catch (RecognitionException el) {
          throw el;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "eq_sequence"


    public static class having_eq_return extends ParserRuleReturnScope {
        public ColumnExp columnExp = new ColumnExp();
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "having_eq"
    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:613:1: having_eq returns [ColumnExp columnExp = new ColumnExp()] : (column_name= selectexpr leq= ( LESS | LESS_OR_EQ | GREATER | GREATER_OR_EQ | EQUALS | NOT_EQUALS1 | NOT_EQUALS2 ) lv= literal_value |column_name2= ( ID | INTEGER ) BETWEEN ll= ( LPAREN | LPAREN_SQUARE ) lv1= literal_value COMMA lv2= literal_value rr= ( RPAREN | RPAREN_SQUARE ) |column_name3= ( ID | INTEGER ) IN LPAREN lv3= literal_value ( COMMA lv4= literal_value )* RPAREN |column_name4= ( ID | INTEGER ) IN LPAREN ss= select_stmt RPAREN );
    public final milesqlParser.having_eq_return having_eq() throws RecognitionException {
        milesqlParser.having_eq_return retval = new milesqlParser.having_eq_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token leq=null;
        Token column_name2=null;
        Token ll=null;
        Token rr=null;
        Token column_name3=null;
        Token column_name4=null;
        Token BETWEEN109=null;
        Token COMMA110=null;
        Token IN111=null;
        Token LPAREN112=null;
        Token COMMA113=null;
        Token RPAREN114=null;
        Token IN115=null;
        Token LPAREN116=null;
        Token RPAREN117=null;
        milesqlParser.selectexpr_return column_name =null;

        milesqlParser.literal_value_return lv =null;

        milesqlParser.literal_value_return lv1 =null;

        milesqlParser.literal_value_return lv2 =null;

        milesqlParser.literal_value_return lv3 =null;

        milesqlParser.literal_value_return lv4 =null;

        milesqlParser.select_stmt_return ss =null;


        Object leq_tree=null;
        Object column_name2_tree=null;
        Object ll_tree=null;
        Object rr_tree=null;
        Object column_name3_tree=null;
        Object column_name4_tree=null;
        Object BETWEEN109_tree=null;
        Object COMMA110_tree=null;
        Object IN111_tree=null;
        Object LPAREN112_tree=null;
        Object COMMA113_tree=null;
        Object RPAREN114_tree=null;
        Object IN115_tree=null;
        Object LPAREN116_tree=null;
        Object RPAREN117_tree=null;

        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:614:2: (column_name= selectexpr leq= ( LESS | LESS_OR_EQ | GREATER | GREATER_OR_EQ | EQUALS | NOT_EQUALS1 | NOT_EQUALS2 ) lv= literal_value |column_name2= ( ID | INTEGER ) BETWEEN ll= ( LPAREN | LPAREN_SQUARE ) lv1= literal_value COMMA lv2= literal_value rr= ( RPAREN | RPAREN_SQUARE ) |column_name3= ( ID | INTEGER ) IN LPAREN lv3= literal_value ( COMMA lv4= literal_value )* RPAREN |column_name4= ( ID | INTEGER ) IN LPAREN ss= select_stmt RPAREN )
            int alt53=4;
            int LA53_0 = input.LA(1);

            if ( (LA53_0==ID||LA53_0==INTEGER) ) {
                switch ( input.LA(2) ) {
                case BETWEEN:
                    {
                    alt53=2;
                    }
                    break;
                case IN:
                    {
                    int LA53_11 = input.LA(3);

                    if ( (LA53_11==LPAREN) ) {
                        int LA53_13 = input.LA(4);

                        if ( (LA53_13==FLOAT||LA53_13==INTEGER||LA53_13==QUESTION||LA53_13==STRING) ) {
                            alt53=3;
                        }
                        else if ( (LA53_13==SELECT) ) {
                            alt53=4;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 53, 13, input);

                            throw nvae;

                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 53, 11, input);

                        throw nvae;

                    }
                    }
                    break;
                case EQUALS:
                case GREATER:
                case GREATER_OR_EQ:
                case LESS:
                case LESS_OR_EQ:
                case NOT_EQUALS1:
                case NOT_EQUALS2:
                    {
                    alt53=1;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 53, 1, input);

                    throw nvae;

                }

            }
            else if ( (LA53_0==AVG||LA53_0==COUNT||(LA53_0 >= MAX && LA53_0 <= MIN)||(LA53_0 >= SQUARESUM && LA53_0 <= STDDEV)||LA53_0==SUM||LA53_0==VARIANCE) ) {
                alt53=1;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 53, 0, input);

                throw nvae;

            }
            switch (alt53) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:614:5: column_name= selectexpr leq= ( LESS | LESS_OR_EQ | GREATER | GREATER_OR_EQ | EQUALS | NOT_EQUALS1 | NOT_EQUALS2 ) lv= literal_value
                    {
                    root_0 = (Object)adaptor.nil();


                    pushFollow(FOLLOW_selectexpr_in_having_eq1913);
                    column_name=selectexpr();

                    state._fsp--;

                    adaptor.addChild(root_0, column_name.getTree());

                    leq=(Token)input.LT(1);

                    if ( input.LA(1)==EQUALS||(input.LA(1) >= GREATER && input.LA(1) <= GREATER_OR_EQ)||(input.LA(1) >= LESS && input.LA(1) <= LESS_OR_EQ)||(input.LA(1) >= NOT_EQUALS1 && input.LA(1) <= NOT_EQUALS2) ) {
                        input.consume();
                        adaptor.addChild(root_0, 
                        (Object)adaptor.create(leq)
                        );
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    pushFollow(FOLLOW_literal_value_in_having_eq1949);
                    lv=literal_value();

                    state._fsp--;

                    adaptor.addChild(root_0, lv.getTree());


                    	FieldDesc fd =new FieldDesc();
                    	fd.fieldName = column_name.columnname;
                    	fd.aliseName = fd.fieldName;
                      	fd.functionId= column_name.functionId;
                     	fd.functionName= column_name.functionName;
                     	fd.refColumnName = column_name.refname;
                           	retval.columnExp.values = new ArrayList();
                            retval.columnExp.values.add(lv.valueDesc);
                    	retval.columnExp.column = fd;
                    	String tag= leq.getText().trim();
                            if(tag.equals("=")){
                    		retval.columnExp.comparetor = Constants.EXP_COMPARE_EQUALS;
                    	}else if(tag.equals("<")){
                    		retval.columnExp.comparetor = Constants.EXP_COMPARE_LT;
                    	}else if(tag.equals("<=")){
                    		retval.columnExp.comparetor = Constants.EXP_COMPARE_LET;
                    	}else if(tag.equals(">")){
                    		retval.columnExp.comparetor = Constants.EXP_COMPARE_GT;
                    	}else if(tag.equals(">=")){
                    		retval.columnExp.comparetor = Constants.EXP_COMPARE_GET;
                    	}else if(tag.equals("<>")){
                    		retval.columnExp.comparetor = Constants.EXP_COMPARE_NOT_EQUALS;
                    	}else if(tag.equals("!=")){
                    		retval.columnExp.comparetor = Constants.EXP_COMPARE_NOT_EQUALS;
                    	}
                    	

                    }
                    break;
                case 2 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:642:4: column_name2= ( ID | INTEGER ) BETWEEN ll= ( LPAREN | LPAREN_SQUARE ) lv1= literal_value COMMA lv2= literal_value rr= ( RPAREN | RPAREN_SQUARE )
                    {
                    root_0 = (Object)adaptor.nil();


                    column_name2=(Token)input.LT(1);

                    if ( input.LA(1)==ID||input.LA(1)==INTEGER ) {
                        input.consume();
                        adaptor.addChild(root_0, 
                        (Object)adaptor.create(column_name2)
                        );
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    BETWEEN109=(Token)match(input,BETWEEN,FOLLOW_BETWEEN_in_having_eq1966); 
                    BETWEEN109_tree = 
                    (Object)adaptor.create(BETWEEN109)
                    ;
                    adaptor.addChild(root_0, BETWEEN109_tree);


                    ll=(Token)input.LT(1);

                    if ( (input.LA(1) >= LPAREN && input.LA(1) <= LPAREN_SQUARE) ) {
                        input.consume();
                        adaptor.addChild(root_0, 
                        (Object)adaptor.create(ll)
                        );
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    pushFollow(FOLLOW_literal_value_in_having_eq1978);
                    lv1=literal_value();

                    state._fsp--;

                    adaptor.addChild(root_0, lv1.getTree());

                    COMMA110=(Token)match(input,COMMA,FOLLOW_COMMA_in_having_eq1980); 
                    COMMA110_tree = 
                    (Object)adaptor.create(COMMA110)
                    ;
                    adaptor.addChild(root_0, COMMA110_tree);


                    pushFollow(FOLLOW_literal_value_in_having_eq1984);
                    lv2=literal_value();

                    state._fsp--;

                    adaptor.addChild(root_0, lv2.getTree());

                    rr=(Token)input.LT(1);

                    if ( (input.LA(1) >= RPAREN && input.LA(1) <= RPAREN_SQUARE) ) {
                        input.consume();
                        adaptor.addChild(root_0, 
                        (Object)adaptor.create(rr)
                        );
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }



                    	FieldDesc fd =new FieldDesc();
                    	fd.fieldName = column_name2.getText().trim();
                           	retval.columnExp.values = new ArrayList();
                            retval.columnExp.values.add(lv1.valueDesc);
                            retval.columnExp.values.add(lv2.valueDesc);
                    	retval.columnExp.column = fd;
                    	String tagl= ll.getText().trim();
                    	String tagr= rr.getText().trim();
                            if(tagl.equals("(")&&tagr.equals(")")){
                    		retval.columnExp.comparetor = Constants.EXP_COMPARE_BETWEEN_LG;
                    	}else if(tagl.equals("(")&&tagr.equals("]")){
                    		retval.columnExp.comparetor = Constants.EXP_COMPARE_BETWEEN_LGE;
                    	}else if(tagl.equals("[")&&tagr.equals("]")){
                    		retval.columnExp.comparetor = Constants.EXP_COMPARE_BETWEEN_LEGE;
                    	}else if(tagl.equals("[")&&tagr.equals(")")){
                    		retval.columnExp.comparetor = Constants.EXP_COMPARE_BETWEEN_LEG;
                    	}
                    	

                    }
                    break;
                case 3 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:662:4: column_name3= ( ID | INTEGER ) IN LPAREN lv3= literal_value ( COMMA lv4= literal_value )* RPAREN
                    {
                    root_0 = (Object)adaptor.nil();


                    column_name3=(Token)input.LT(1);

                    if ( input.LA(1)==ID||input.LA(1)==INTEGER ) {
                        input.consume();
                        adaptor.addChild(root_0, 
                        (Object)adaptor.create(column_name3)
                        );
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    IN111=(Token)match(input,IN,FOLLOW_IN_in_having_eq2009); 
                    IN111_tree = 
                    (Object)adaptor.create(IN111)
                    ;
                    adaptor.addChild(root_0, IN111_tree);


                    LPAREN112=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_having_eq2011); 
                    LPAREN112_tree = 
                    (Object)adaptor.create(LPAREN112)
                    ;
                    adaptor.addChild(root_0, LPAREN112_tree);


                    pushFollow(FOLLOW_literal_value_in_having_eq2015);
                    lv3=literal_value();

                    state._fsp--;

                    adaptor.addChild(root_0, lv3.getTree());

                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:662:59: ( COMMA lv4= literal_value )*
                    loop52:
                    do {
                        int alt52=2;
                        int LA52_0 = input.LA(1);

                        if ( (LA52_0==COMMA) ) {
                            alt52=1;
                        }


                        switch (alt52) {
                    	case 1 :
                    	    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:662:60: COMMA lv4= literal_value
                    	    {
                    	    COMMA113=(Token)match(input,COMMA,FOLLOW_COMMA_in_having_eq2018); 
                    	    COMMA113_tree = 
                    	    (Object)adaptor.create(COMMA113)
                    	    ;
                    	    adaptor.addChild(root_0, COMMA113_tree);


                    	    pushFollow(FOLLOW_literal_value_in_having_eq2022);
                    	    lv4=literal_value();

                    	    state._fsp--;

                    	    adaptor.addChild(root_0, lv4.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop52;
                        }
                    } while (true);


                    RPAREN114=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_having_eq2026); 
                    RPAREN114_tree = 
                    (Object)adaptor.create(RPAREN114)
                    ;
                    adaptor.addChild(root_0, RPAREN114_tree);



                    	
                    	

                    }
                    break;
                case 4 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:666:4: column_name4= ( ID | INTEGER ) IN LPAREN ss= select_stmt RPAREN
                    {
                    root_0 = (Object)adaptor.nil();


                    column_name4=(Token)input.LT(1);

                    if ( input.LA(1)==ID||input.LA(1)==INTEGER ) {
                        input.consume();
                        adaptor.addChild(root_0, 
                        (Object)adaptor.create(column_name4)
                        );
                        state.errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    IN115=(Token)match(input,IN,FOLLOW_IN_in_having_eq2043); 
                    IN115_tree = 
                    (Object)adaptor.create(IN115)
                    ;
                    adaptor.addChild(root_0, IN115_tree);


                    LPAREN116=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_having_eq2045); 
                    LPAREN116_tree = 
                    (Object)adaptor.create(LPAREN116)
                    ;
                    adaptor.addChild(root_0, LPAREN116_tree);


                    pushFollow(FOLLOW_select_stmt_in_having_eq2049);
                    ss=select_stmt();

                    state._fsp--;

                    adaptor.addChild(root_0, ss.getTree());

                    RPAREN117=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_having_eq2051); 
                    RPAREN117_tree = 
                    (Object)adaptor.create(RPAREN117)
                    ;
                    adaptor.addChild(root_0, RPAREN117_tree);



                    	
                    	

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

        catch (RecognitionException el) {
          throw el;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "having_eq"


    public static class literal_value_return extends ParserRuleReturnScope {
        public ValueDesc valueDesc = new ValueDesc();
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "literal_value"
    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:672:1: literal_value returns [ValueDesc valueDesc = new ValueDesc()] : (rs= INTEGER |rs= FLOAT |rs= STRING |rs= QUESTION ) ;
    public final milesqlParser.literal_value_return literal_value() throws RecognitionException {
        milesqlParser.literal_value_return retval = new milesqlParser.literal_value_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token rs=null;

        Object rs_tree=null;

        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:673:2: ( (rs= INTEGER |rs= FLOAT |rs= STRING |rs= QUESTION ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:673:3: (rs= INTEGER |rs= FLOAT |rs= STRING |rs= QUESTION )
            {
            root_0 = (Object)adaptor.nil();


            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:673:3: (rs= INTEGER |rs= FLOAT |rs= STRING |rs= QUESTION )
            int alt54=4;
            switch ( input.LA(1) ) {
            case INTEGER:
                {
                alt54=1;
                }
                break;
            case FLOAT:
                {
                alt54=2;
                }
                break;
            case STRING:
                {
                alt54=3;
                }
                break;
            case QUESTION:
                {
                alt54=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 54, 0, input);

                throw nvae;

            }

            switch (alt54) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:674:3: rs= INTEGER
                    {
                    rs=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_literal_value2075); 
                    rs_tree = 
                    (Object)adaptor.create(rs)
                    ;
                    adaptor.addChild(root_0, rs_tree);



                    	 String strTemp = rs.getText().trim();
                            retval.valueDesc.valueDesc = strTemp;

                    	 

                    }
                    break;
                case 2 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:680:5: rs= FLOAT
                    {
                    rs=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_literal_value2089); 
                    rs_tree = 
                    (Object)adaptor.create(rs)
                    ;
                    adaptor.addChild(root_0, rs_tree);



                    	 String strTemp = rs.getText().trim();
                            retval.valueDesc.valueDesc = strTemp;

                    	 

                    }
                    break;
                case 3 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:686:5: rs= STRING
                    {
                    rs=(Token)match(input,STRING,FOLLOW_STRING_in_literal_value2103); 
                    rs_tree = 
                    (Object)adaptor.create(rs)
                    ;
                    adaptor.addChild(root_0, rs_tree);



                    	 String strTemp = rs.getText().trim();
                            retval.valueDesc.valueDesc = strTemp.substring(1, strTemp.length()-1);

                    	 

                    }
                    break;
                case 4 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:692:5: rs= QUESTION
                    {
                    rs=(Token)match(input,QUESTION,FOLLOW_QUESTION_in_literal_value2117); 
                    rs_tree = 
                    (Object)adaptor.create(rs)
                    ;
                    adaptor.addChild(root_0, rs_tree);



                    	 String strTemp = rs.getText().trim();
                            retval.valueDesc.valueDesc = strTemp;
                    	retval.valueDesc.parmIndex = pamerIndex.incrementAndGet();
                    	 

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }

        catch (RecognitionException el) {
          throw el;
        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "literal_value"

    // Delegated rules


 

    public static final BitSet FOLLOW_sql_stmt_core_in_sql_stmt77 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_sql_stmt84 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_update_stmt_in_sql_stmt_core99 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_delete_stmt_in_sql_stmt_core109 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_insert_stmt_in_sql_stmt_core119 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_export_stmt_in_sql_stmt_core129 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_select_stmt_expr_in_sql_stmt_core139 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unionsection_expr_in_select_stmt_expr156 = new BitSet(new long[]{0x0000000000000002L,0x0000080000000000L});
    public static final BitSet FOLLOW_UNIONS_in_select_stmt_expr164 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_unionsection_expr_in_select_stmt_expr169 = new BitSet(new long[]{0x0000000000000002L,0x0000080000000000L});
    public static final BitSet FOLLOW_intersection_expr_in_unionsection_expr193 = new BitSet(new long[]{0x0040000000000002L});
    public static final BitSet FOLLOW_INTERSECTION_in_unionsection_expr200 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_intersection_expr_in_unionsection_expr205 = new BitSet(new long[]{0x0040000000000002L});
    public static final BitSet FOLLOW_select_stmt_in_intersection_expr228 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SELECT_in_select_stmt251 = new BitSet(new long[]{0x0020400001080620L,0x0000402018000030L});
    public static final BitSet FOLLOW_ALL_in_select_stmt254 = new BitSet(new long[]{0x0020400000080600L,0x0000402018000030L});
    public static final BitSet FOLLOW_DISTINCT_in_select_stmt262 = new BitSet(new long[]{0x0020400000080600L,0x0000402018000030L});
    public static final BitSet FOLLOW_result_column_in_select_stmt274 = new BitSet(new long[]{0x0000000400020000L});
    public static final BitSet FOLLOW_COMMA_in_select_stmt282 = new BitSet(new long[]{0x0020400000080600L,0x0000402018000030L});
    public static final BitSet FOLLOW_result_column_in_select_stmt286 = new BitSet(new long[]{0x0000000400020000L});
    public static final BitSet FOLLOW_FROM_in_select_stmt299 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_ID_in_select_stmt303 = new BitSet(new long[]{0x4008020002000002L,0x0001000000802800L});
    public static final BitSet FOLLOW_DOCHINT_in_select_stmt309 = new BitSet(new long[]{0x0020400000000000L});
    public static final BitSet FOLLOW_dochint_expr_in_select_stmt315 = new BitSet(new long[]{0x4008020000000002L,0x0001000000802800L});
    public static final BitSet FOLLOW_SEGHINT_in_select_stmt323 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_seghint_expr_in_select_stmt327 = new BitSet(new long[]{0x4008020000000002L,0x0001000000002800L});
    public static final BitSet FOLLOW_INDEXWHERE_in_select_stmt335 = new BitSet(new long[]{0x0020400000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_expr_in_select_stmt340 = new BitSet(new long[]{0x4000020000000002L,0x0001000000002800L});
    public static final BitSet FOLLOW_WHERE_in_select_stmt347 = new BitSet(new long[]{0x0020400000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_expr_in_select_stmt351 = new BitSet(new long[]{0x4000020000000002L,0x0000000000002800L});
    public static final BitSet FOLLOW_GROUP_in_select_stmt358 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_BY_in_select_stmt360 = new BitSet(new long[]{0x0020400000000000L});
    public static final BitSet FOLLOW_group_term_in_select_stmt366 = new BitSet(new long[]{0x4000143000020002L,0x0000000000002800L});
    public static final BitSet FOLLOW_COMMA_in_select_stmt375 = new BitSet(new long[]{0x0020400000000000L});
    public static final BitSet FOLLOW_group_term_in_select_stmt379 = new BitSet(new long[]{0x4000143000020002L,0x0000000000002800L});
    public static final BitSet FOLLOW_HAVING_in_select_stmt393 = new BitSet(new long[]{0x0020400000080400L,0x0000402018000031L});
    public static final BitSet FOLLOW_having_expr_in_select_stmt397 = new BitSet(new long[]{0x4000043000000002L,0x0000000000002800L});
    public static final BitSet FOLLOW_GROUPORDER_in_select_stmt405 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_BY_in_select_stmt407 = new BitSet(new long[]{0x0020400000000000L});
    public static final BitSet FOLLOW_gorder_term_in_select_stmt416 = new BitSet(new long[]{0x4000003000020002L,0x0000000000002800L});
    public static final BitSet FOLLOW_COMMA_in_select_stmt427 = new BitSet(new long[]{0x0020400000000000L});
    public static final BitSet FOLLOW_gorder_term_in_select_stmt433 = new BitSet(new long[]{0x4000003000020002L,0x0000000000002800L});
    public static final BitSet FOLLOW_GLIMIT_in_select_stmt454 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_INTEGER_in_select_stmt458 = new BitSet(new long[]{0x4000002000000002L,0x0000000000002800L});
    public static final BitSet FOLLOW_GOFFSET_in_select_stmt467 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_INTEGER_in_select_stmt471 = new BitSet(new long[]{0x4000000000000002L,0x0000000000002800L});
    public static final BitSet FOLLOW_ORDER_in_select_stmt484 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_BY_in_select_stmt486 = new BitSet(new long[]{0x0020400000000000L});
    public static final BitSet FOLLOW_ordering_term_in_select_stmt490 = new BitSet(new long[]{0x4000000000020002L,0x0000000000000800L});
    public static final BitSet FOLLOW_COMMA_in_select_stmt499 = new BitSet(new long[]{0x0020400000000000L});
    public static final BitSet FOLLOW_ordering_term_in_select_stmt503 = new BitSet(new long[]{0x4000000000020002L,0x0000000000000800L});
    public static final BitSet FOLLOW_LIMIT_in_select_stmt520 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_INTEGER_in_select_stmt524 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000800L});
    public static final BitSet FOLLOW_OFFSET_in_select_stmt532 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_INTEGER_in_select_stmt536 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_group_term556 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASTERISK_in_result_column578 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_selectexpr_in_result_column589 = new BitSet(new long[]{0x0000000000000082L});
    public static final BitSet FOLLOW_AS_in_result_column598 = new BitSet(new long[]{0x0020400000000000L});
    public static final BitSet FOLLOW_set_in_result_column602 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_ordering_term635 = new BitSet(new long[]{0x0000000000800102L});
    public static final BitSet FOLLOW_set_in_ordering_term648 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_gorder_term677 = new BitSet(new long[]{0x0000000000800102L});
    public static final BitSet FOLLOW_set_in_gorder_term692 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INSERT_in_insert_stmt718 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_INTO_in_insert_stmt720 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_ID_in_insert_stmt724 = new BitSet(new long[]{0x0020400000000002L,0x0002000000000000L});
    public static final BitSet FOLLOW_set_in_insert_stmt733 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_EQUALS_in_insert_stmt739 = new BitSet(new long[]{0x0020000100000000L,0x0000000020010000L});
    public static final BitSet FOLLOW_literal_value_in_insert_stmt743 = new BitSet(new long[]{0x0020400000000002L,0x0002000000000000L});
    public static final BitSet FOLLOW_WITH_in_insert_stmt754 = new BitSet(new long[]{0x0000000000000002L,0x0008000000000000L});
    public static final BitSet FOLLOW_WORDSEG_in_insert_stmt757 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_LPAREN_in_insert_stmt759 = new BitSet(new long[]{0x0020400000000000L});
    public static final BitSet FOLLOW_set_in_insert_stmt763 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RPAREN_in_insert_stmt769 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_EQUALS_in_insert_stmt771 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_LPAREN_in_insert_stmt773 = new BitSet(new long[]{0x0020000100000000L,0x0000000020010000L});
    public static final BitSet FOLLOW_literal_value_in_insert_stmt777 = new BitSet(new long[]{0x0000000000020000L,0x0000000000100000L});
    public static final BitSet FOLLOW_COMMA_in_insert_stmt784 = new BitSet(new long[]{0x0020000100000000L,0x0000000020010000L});
    public static final BitSet FOLLOW_literal_value_in_insert_stmt788 = new BitSet(new long[]{0x0000000000020000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RPAREN_in_insert_stmt797 = new BitSet(new long[]{0x0000000000000002L,0x0008000000000000L});
    public static final BitSet FOLLOW_EXPORT_in_export_stmt819 = new BitSet(new long[]{0x0000000000000000L,0x0000008000000000L});
    public static final BitSet FOLLOW_TO_in_export_stmt821 = new BitSet(new long[]{0x0020000100000000L,0x0000000020010000L});
    public static final BitSet FOLLOW_literal_value_in_export_stmt825 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_FROM_in_export_stmt830 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_ID_in_export_stmt834 = new BitSet(new long[]{0x4008000000000002L,0x0001000000800000L});
    public static final BitSet FOLLOW_SEGHINT_in_export_stmt840 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_seghint_expr_in_export_stmt844 = new BitSet(new long[]{0x4008000000000002L,0x0001000000000000L});
    public static final BitSet FOLLOW_INDEXWHERE_in_export_stmt853 = new BitSet(new long[]{0x0020400000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_expr_in_export_stmt857 = new BitSet(new long[]{0x4000000000000002L,0x0001000000000000L});
    public static final BitSet FOLLOW_WHERE_in_export_stmt867 = new BitSet(new long[]{0x0020400000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_expr_in_export_stmt871 = new BitSet(new long[]{0x4000000000000002L});
    public static final BitSet FOLLOW_LIMIT_in_export_stmt880 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_INTEGER_in_export_stmt884 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DELETE_in_delete_stmt902 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_FROM_in_delete_stmt904 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_ID_in_delete_stmt908 = new BitSet(new long[]{0x0008000000000002L,0x0001000000800000L});
    public static final BitSet FOLLOW_SEGHINT_in_delete_stmt914 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_seghint_expr_in_delete_stmt918 = new BitSet(new long[]{0x0008000000000002L,0x0001000000000000L});
    public static final BitSet FOLLOW_INDEXWHERE_in_delete_stmt926 = new BitSet(new long[]{0x0020400000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_expr_in_delete_stmt931 = new BitSet(new long[]{0x0000000000000002L,0x0001000000000000L});
    public static final BitSet FOLLOW_WHERE_in_delete_stmt938 = new BitSet(new long[]{0x0020400000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_expr_in_delete_stmt942 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UPDATE_in_update_stmt958 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_ID_in_update_stmt962 = new BitSet(new long[]{0x0000000000000000L,0x0000000004000000L});
    public static final BitSet FOLLOW_SET_in_update_stmt967 = new BitSet(new long[]{0x0020400000000000L});
    public static final BitSet FOLLOW_update_set_in_update_stmt969 = new BitSet(new long[]{0x0008000000000002L,0x0001000000800000L});
    public static final BitSet FOLLOW_SEGHINT_in_update_stmt975 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_seghint_expr_in_update_stmt979 = new BitSet(new long[]{0x0008000000000002L,0x0001000000000000L});
    public static final BitSet FOLLOW_INDEXWHERE_in_update_stmt987 = new BitSet(new long[]{0x0020400000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_expr_in_update_stmt992 = new BitSet(new long[]{0x0000000000000002L,0x0001000000000000L});
    public static final BitSet FOLLOW_WHERE_in_update_stmt999 = new BitSet(new long[]{0x0020400000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_expr_in_update_stmt1003 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_dochint_expr1022 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_EQUALS_in_dochint_expr1028 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_INTEGER_in_dochint_expr1034 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_update_set1051 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_EQUALS_in_update_set1057 = new BitSet(new long[]{0x0020000100000000L,0x0000000020010000L});
    public static final BitSet FOLLOW_literal_value_in_update_set1061 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_seghint_expr1079 = new BitSet(new long[]{0x0020000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_set_in_seghint_expr1083 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_COMMA_in_seghint_expr1089 = new BitSet(new long[]{0x0020000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_set_in_seghint_expr1093 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_COMMA_in_seghint_expr1099 = new BitSet(new long[]{0x0020000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_set_in_seghint_expr1103 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_COMMA_in_seghint_expr1109 = new BitSet(new long[]{0x0020000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_set_in_seghint_expr1113 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RPAREN_in_seghint_expr1119 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_or_subexpr_in_expr1142 = new BitSet(new long[]{0x0000000000000002L,0x0000000000001000L});
    public static final BitSet FOLLOW_OR_in_expr1149 = new BitSet(new long[]{0x0020400000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_or_subexpr_in_expr1154 = new BitSet(new long[]{0x0000000000000002L,0x0000000000001000L});
    public static final BitSet FOLLOW_and_subexpr_in_or_subexpr1178 = new BitSet(new long[]{0x0000000000000042L});
    public static final BitSet FOLLOW_AND_in_or_subexpr1185 = new BitSet(new long[]{0x0020400000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_and_subexpr_in_or_subexpr1189 = new BitSet(new long[]{0x0000000000000042L});
    public static final BitSet FOLLOW_eq_subexpr_in_and_subexpr1212 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_and_subexpr1220 = new BitSet(new long[]{0x0020400000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_expr_in_and_subexpr1224 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RPAREN_in_and_subexpr1226 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_having_or_subexpr_in_having_expr1249 = new BitSet(new long[]{0x0000000000000002L,0x0000000000001000L});
    public static final BitSet FOLLOW_OR_in_having_expr1256 = new BitSet(new long[]{0x0020400000080400L,0x0000402018000031L});
    public static final BitSet FOLLOW_having_or_subexpr_in_having_expr1261 = new BitSet(new long[]{0x0000000000000002L,0x0000000000001000L});
    public static final BitSet FOLLOW_having_and_subexpr_in_having_or_subexpr1285 = new BitSet(new long[]{0x0000000000000042L});
    public static final BitSet FOLLOW_AND_in_having_or_subexpr1292 = new BitSet(new long[]{0x0020400000080400L,0x0000402018000031L});
    public static final BitSet FOLLOW_having_and_subexpr_in_having_or_subexpr1296 = new BitSet(new long[]{0x0000000000000042L});
    public static final BitSet FOLLOW_having_eq_in_having_and_subexpr1319 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_having_and_subexpr1327 = new BitSet(new long[]{0x0020400000080400L,0x0000402018000031L});
    public static final BitSet FOLLOW_having_expr_in_having_and_subexpr1331 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RPAREN_in_having_and_subexpr1333 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_selectexpr1354 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUM_in_selectexpr1374 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_LPAREN_in_selectexpr1376 = new BitSet(new long[]{0x0020400000000000L});
    public static final BitSet FOLLOW_set_in_selectexpr1380 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RPAREN_in_selectexpr1386 = new BitSet(new long[]{0x0000000000000002L,0x0004000000000000L});
    public static final BitSet FOLLOW_MAX_in_selectexpr1398 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_LPAREN_in_selectexpr1401 = new BitSet(new long[]{0x0020400000000000L});
    public static final BitSet FOLLOW_set_in_selectexpr1405 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RPAREN_in_selectexpr1411 = new BitSet(new long[]{0x0000000000000002L,0x0004000000000000L});
    public static final BitSet FOLLOW_MIN_in_selectexpr1423 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_LPAREN_in_selectexpr1426 = new BitSet(new long[]{0x0020400000000000L});
    public static final BitSet FOLLOW_set_in_selectexpr1430 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RPAREN_in_selectexpr1436 = new BitSet(new long[]{0x0000000000000002L,0x0004000000000000L});
    public static final BitSet FOLLOW_COUNT_in_selectexpr1448 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_LPAREN_in_selectexpr1450 = new BitSet(new long[]{0x0020400000000200L});
    public static final BitSet FOLLOW_set_in_selectexpr1454 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RPAREN_in_selectexpr1462 = new BitSet(new long[]{0x0000000000000002L,0x0004000000000000L});
    public static final BitSet FOLLOW_AVG_in_selectexpr1474 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_LPAREN_in_selectexpr1476 = new BitSet(new long[]{0x0020400000000200L});
    public static final BitSet FOLLOW_set_in_selectexpr1480 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RPAREN_in_selectexpr1488 = new BitSet(new long[]{0x0000000000000002L,0x0004000000000000L});
    public static final BitSet FOLLOW_SQUARESUM_in_selectexpr1500 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_LPAREN_in_selectexpr1502 = new BitSet(new long[]{0x0020400000000200L});
    public static final BitSet FOLLOW_set_in_selectexpr1506 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RPAREN_in_selectexpr1514 = new BitSet(new long[]{0x0000000000000002L,0x0004000000000000L});
    public static final BitSet FOLLOW_VARIANCE_in_selectexpr1526 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_LPAREN_in_selectexpr1528 = new BitSet(new long[]{0x0020400000000200L});
    public static final BitSet FOLLOW_set_in_selectexpr1532 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RPAREN_in_selectexpr1540 = new BitSet(new long[]{0x0000000000000002L,0x0004000000000000L});
    public static final BitSet FOLLOW_STDDEV_in_selectexpr1552 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_LPAREN_in_selectexpr1554 = new BitSet(new long[]{0x0020400000000200L});
    public static final BitSet FOLLOW_set_in_selectexpr1558 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RPAREN_in_selectexpr1566 = new BitSet(new long[]{0x0000000000000002L,0x0004000000000000L});
    public static final BitSet FOLLOW_COUNT_in_selectexpr1578 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_LPAREN_in_selectexpr1580 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_DISTINCT_in_selectexpr1582 = new BitSet(new long[]{0x0020400000000000L});
    public static final BitSet FOLLOW_set_in_selectexpr1586 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RPAREN_in_selectexpr1592 = new BitSet(new long[]{0x0000000000000002L,0x0004000000000000L});
    public static final BitSet FOLLOW_WITHIN_in_selectexpr1605 = new BitSet(new long[]{0x0020400000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_expr_in_selectexpr1611 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_eq_subexpr1638 = new BitSet(new long[]{0x3000018020000000L,0x0000000000000180L});
    public static final BitSet FOLLOW_set_in_eq_subexpr1647 = new BitSet(new long[]{0x0020000100000000L,0x0000000020010000L});
    public static final BitSet FOLLOW_literal_value_in_eq_subexpr1678 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_eq_subexpr1688 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_BETWEEN_in_eq_subexpr1695 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000003L});
    public static final BitSet FOLLOW_set_in_eq_subexpr1699 = new BitSet(new long[]{0x0020000100000000L,0x0000000020010000L});
    public static final BitSet FOLLOW_literal_value_in_eq_subexpr1707 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_COMMA_in_eq_subexpr1709 = new BitSet(new long[]{0x0020000100000000L,0x0000000020010000L});
    public static final BitSet FOLLOW_literal_value_in_eq_subexpr1713 = new BitSet(new long[]{0x0000000000000000L,0x0000000000300000L});
    public static final BitSet FOLLOW_set_in_eq_subexpr1717 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_eq_subexpr1731 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_IN_in_eq_subexpr1738 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_LPAREN_in_eq_subexpr1740 = new BitSet(new long[]{0x0020000100000000L,0x0000000020010000L});
    public static final BitSet FOLLOW_literal_value_in_eq_subexpr1744 = new BitSet(new long[]{0x0000000000020000L,0x0000000000100000L});
    public static final BitSet FOLLOW_COMMA_in_eq_subexpr1752 = new BitSet(new long[]{0x0020000100000000L,0x0000000020010000L});
    public static final BitSet FOLLOW_literal_value_in_eq_subexpr1756 = new BitSet(new long[]{0x0000000000020000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RPAREN_in_eq_subexpr1764 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_eq_subexpr1771 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_MATCH_in_eq_subexpr1777 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_LPAREN_in_eq_subexpr1779 = new BitSet(new long[]{0x0020000100000000L,0x0000000020010000L});
    public static final BitSet FOLLOW_literal_value_in_eq_subexpr1783 = new BitSet(new long[]{0x0000000000020000L,0x0000000000100000L});
    public static final BitSet FOLLOW_COMMA_in_eq_subexpr1791 = new BitSet(new long[]{0x0020000100000000L,0x0000000020010000L});
    public static final BitSet FOLLOW_literal_value_in_eq_subexpr1795 = new BitSet(new long[]{0x0000000000020000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RPAREN_in_eq_subexpr1804 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_eq_subexpr1811 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_IN_in_eq_subexpr1818 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_LPAREN_in_eq_subexpr1820 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_select_stmt_in_eq_subexpr1824 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RPAREN_in_eq_subexpr1826 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_eq_sequence1848 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_EQUALS_in_eq_sequence1854 = new BitSet(new long[]{0x0020000100000000L,0x0000000020010000L});
    public static final BitSet FOLLOW_literal_value_in_eq_sequence1860 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_COMMA_in_eq_sequence1868 = new BitSet(new long[]{0x0020400000000000L});
    public static final BitSet FOLLOW_set_in_eq_sequence1874 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_EQUALS_in_eq_sequence1880 = new BitSet(new long[]{0x0020000100000000L,0x0000000020010000L});
    public static final BitSet FOLLOW_literal_value_in_eq_sequence1886 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_selectexpr_in_having_eq1913 = new BitSet(new long[]{0x3000018020000000L,0x0000000000000180L});
    public static final BitSet FOLLOW_set_in_having_eq1918 = new BitSet(new long[]{0x0020000100000000L,0x0000000020010000L});
    public static final BitSet FOLLOW_literal_value_in_having_eq1949 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_having_eq1959 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_BETWEEN_in_having_eq1966 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000003L});
    public static final BitSet FOLLOW_set_in_having_eq1970 = new BitSet(new long[]{0x0020000100000000L,0x0000000020010000L});
    public static final BitSet FOLLOW_literal_value_in_having_eq1978 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_COMMA_in_having_eq1980 = new BitSet(new long[]{0x0020000100000000L,0x0000000020010000L});
    public static final BitSet FOLLOW_literal_value_in_having_eq1984 = new BitSet(new long[]{0x0000000000000000L,0x0000000000300000L});
    public static final BitSet FOLLOW_set_in_having_eq1988 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_having_eq2002 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_IN_in_having_eq2009 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_LPAREN_in_having_eq2011 = new BitSet(new long[]{0x0020000100000000L,0x0000000020010000L});
    public static final BitSet FOLLOW_literal_value_in_having_eq2015 = new BitSet(new long[]{0x0000000000020000L,0x0000000000100000L});
    public static final BitSet FOLLOW_COMMA_in_having_eq2018 = new BitSet(new long[]{0x0020000100000000L,0x0000000020010000L});
    public static final BitSet FOLLOW_literal_value_in_having_eq2022 = new BitSet(new long[]{0x0000000000020000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RPAREN_in_having_eq2026 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_having_eq2036 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_IN_in_having_eq2043 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_LPAREN_in_having_eq2045 = new BitSet(new long[]{0x0000000000000000L,0x0000000001000000L});
    public static final BitSet FOLLOW_select_stmt_in_having_eq2049 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_RPAREN_in_having_eq2051 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INTEGER_in_literal_value2075 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal_value2089 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal_value2103 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUESTION_in_literal_value2117 = new BitSet(new long[]{0x0000000000000002L});

}