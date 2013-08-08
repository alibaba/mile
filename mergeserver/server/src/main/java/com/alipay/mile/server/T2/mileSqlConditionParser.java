// $ANTLR 3.4 E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g 2012-05-15 17:23:39

package com.alipay.mile.server.T2;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.antlr.runtime.BitSet;
import org.antlr.runtime.IntStream;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.MismatchedTokenException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.Parser;
import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.TreeAdaptor;

import com.alipay.mile.Constants;
import com.alipay.mile.Expression;
import com.alipay.mile.FieldDesc;
import com.alipay.mile.mileexception.SQLException;
import com.alipay.mile.server.query.ColumnExp;
import com.alipay.mile.server.query.FieldValuePair;
import com.alipay.mile.server.query.Statement;
import com.alipay.mile.server.query.TimeHint;
import com.alipay.mile.server.query.ValueDesc;
import com.alipay.mile.server.query.special.SpecifyQueryStatement;


@SuppressWarnings({"all", "warnings", "unchecked"})
public class mileSqlConditionParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "A", "AND", "ASTERISK", "B", "BACKSLASH", "BETWEEN", "BLOB", "C", "COMMA", "COMMENT", "CURRENT_TIMESTAMP", "D", "DOT", "E", "EQUALS", "F", "FLOAT", "FLOAT_EXP", "G", "GREATER", "GREATER_OR_EQ", "H", "I", "ID", "ID_CORE", "ID_PLAIN", "ID_START", "INDEXWHERE", "INTEGER", "J", "K", "L", "LESS", "LESS_OR_EQ", "LINE_COMMENT", "LPAREN", "LPAREN_SQUARE", "M", "N", "NOT_EQUALS2", "O", "OR", "P", "Q", "QUESTION", "QUOTE_DOUBLE", "QUOTE_SINGLE", "R", "RPAREN", "RPAREN_SQUARE", "S", "SEGHINT", "SEMI", "STRING", "STRING_CORE", "STRING_CORE_DOUBLE", "STRING_CORE_SINGLE", "STRING_DOUBLE", "STRING_ESCAPE_DOUBLE", "STRING_ESCAPE_SINGLE", "STRING_SINGLE", "T", "U", "UNDERSCORE", "UNIONHASH", "V", "W", "WHERE", "WS", "X", "Y", "Z"
    };

    public static final int EOF=-1;
    public static final int A=4;
    public static final int AND=5;
    public static final int ASTERISK=6;
    public static final int B=7;
    public static final int BACKSLASH=8;
    public static final int BETWEEN=9;
    public static final int BLOB=10;
    public static final int C=11;
    public static final int COMMA=12;
    public static final int COMMENT=13;
    public static final int CURRENT_TIMESTAMP=14;
    public static final int D=15;
    public static final int DOT=16;
    public static final int E=17;
    public static final int EQUALS=18;
    public static final int F=19;
    public static final int FLOAT=20;
    public static final int FLOAT_EXP=21;
    public static final int G=22;
    public static final int GREATER=23;
    public static final int GREATER_OR_EQ=24;
    public static final int H=25;
    public static final int I=26;
    public static final int ID=27;
    public static final int ID_CORE=28;
    public static final int ID_PLAIN=29;
    public static final int ID_START=30;
    public static final int INDEXWHERE=31;
    public static final int INTEGER=32;
    public static final int J=33;
    public static final int K=34;
    public static final int L=35;
    public static final int LESS=36;
    public static final int LESS_OR_EQ=37;
    public static final int LINE_COMMENT=38;
    public static final int LPAREN=39;
    public static final int LPAREN_SQUARE=40;
    public static final int M=41;
    public static final int N=42;
    public static final int NOT_EQUALS2=43;
    public static final int O=44;
    public static final int OR=45;
    public static final int P=46;
    public static final int Q=47;
    public static final int QUESTION=48;
    public static final int QUOTE_DOUBLE=49;
    public static final int QUOTE_SINGLE=50;
    public static final int R=51;
    public static final int RPAREN=52;
    public static final int RPAREN_SQUARE=53;
    public static final int S=54;
    public static final int SEGHINT=55;
    public static final int SEMI=56;
    public static final int STRING=57;
    public static final int STRING_CORE=58;
    public static final int STRING_CORE_DOUBLE=59;
    public static final int STRING_CORE_SINGLE=60;
    public static final int STRING_DOUBLE=61;
    public static final int STRING_ESCAPE_DOUBLE=62;
    public static final int STRING_ESCAPE_SINGLE=63;
    public static final int STRING_SINGLE=64;
    public static final int T=65;
    public static final int U=66;
    public static final int UNDERSCORE=67;
    public static final int UNIONHASH=68;
    public static final int V=69;
    public static final int W=70;
    public static final int WHERE=71;
    public static final int WS=72;
    public static final int X=73;
    public static final int Y=74;
    public static final int Z=75;

    // delegates
    public Parser[] getDelegates() {
        return new Parser[] {};
    }

    // delegators


    public mileSqlConditionParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }
    public mileSqlConditionParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

protected TreeAdaptor adaptor = new CommonTreeAdaptor();

public void setTreeAdaptor(TreeAdaptor adaptor) {
    this.adaptor = adaptor;
}
public TreeAdaptor getTreeAdaptor() {
    return adaptor;
}
    public String[] getTokenNames() { return mileSqlConditionParser.tokenNames; }
    public String getGrammarFileName() { return "E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g"; }


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
    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:51:1: sql_stmt returns [Statement statement] : sql_stmt_core EOF !;
    public final mileSqlConditionParser.sql_stmt_return sql_stmt() throws RecognitionException {
        mileSqlConditionParser.sql_stmt_return retval = new mileSqlConditionParser.sql_stmt_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token EOF2=null;
        mileSqlConditionParser.sql_stmt_core_return sql_stmt_core1 =null;


        Object EOF2_tree=null;

        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:52:2: ( sql_stmt_core EOF !)
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:52:4: sql_stmt_core EOF !
            {
            root_0 = (Object)adaptor.nil();


            pushFollow(FOLLOW_sql_stmt_core_in_sql_stmt77);
            sql_stmt_core1=sql_stmt_core();

            state._fsp--;

            adaptor.addChild(root_0, sql_stmt_core1.getTree());

            EOF2=(Token)match(input,EOF,FOLLOW_EOF_in_sql_stmt79); 

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
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "sql_stmt_core"
    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:55:1: sql_stmt_core : condition_stmt[st] ;
    public final mileSqlConditionParser.sql_stmt_core_return sql_stmt_core() throws RecognitionException {
        mileSqlConditionParser.sql_stmt_core_return retval = new mileSqlConditionParser.sql_stmt_core_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        mileSqlConditionParser.condition_stmt_return condition_stmt3 =null;



        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:56:2: ( condition_stmt[st] )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:57:2: condition_stmt[st]
            {
            root_0 = (Object)adaptor.nil();



            		SpecifyQueryStatement st = new SpecifyQueryStatement();
            		statement = st;
            	

            pushFollow(FOLLOW_condition_stmt_in_sql_stmt_core95);
            condition_stmt3=condition_stmt(st);

            state._fsp--;

            adaptor.addChild(root_0, condition_stmt3.getTree());

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


    public static class condition_stmt_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "condition_stmt"
    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:64:1: condition_stmt[SpecifyQueryStatement specifyQueryStatement] : ( SEGHINT seg_hint= seghint_expr )? INDEXWHERE hash_Where= expr ( WHERE filterWhere= expr )? ;
    public final mileSqlConditionParser.condition_stmt_return condition_stmt(SpecifyQueryStatement specifyQueryStatement) throws RecognitionException {
        mileSqlConditionParser.condition_stmt_return retval = new mileSqlConditionParser.condition_stmt_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token SEGHINT4=null;
        Token INDEXWHERE5=null;
        Token WHERE6=null;
        mileSqlConditionParser.seghint_expr_return seg_hint =null;

        mileSqlConditionParser.expr_return hash_Where =null;

        mileSqlConditionParser.expr_return filterWhere =null;


        Object SEGHINT4_tree=null;
        Object INDEXWHERE5_tree=null;
        Object WHERE6_tree=null;

        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:65:2: ( ( SEGHINT seg_hint= seghint_expr )? INDEXWHERE hash_Where= expr ( WHERE filterWhere= expr )? )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:66:2: ( SEGHINT seg_hint= seghint_expr )? INDEXWHERE hash_Where= expr ( WHERE filterWhere= expr )?
            {
            root_0 = (Object)adaptor.nil();


            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:66:2: ( SEGHINT seg_hint= seghint_expr )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==SEGHINT) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:66:3: SEGHINT seg_hint= seghint_expr
                    {
                    SEGHINT4=(Token)match(input,SEGHINT,FOLLOW_SEGHINT_in_condition_stmt112); 
                    SEGHINT4_tree = 
                    (Object)adaptor.create(SEGHINT4)
                    ;
                    adaptor.addChild(root_0, SEGHINT4_tree);


                    pushFollow(FOLLOW_seghint_expr_in_condition_stmt118);
                    seg_hint=seghint_expr();

                    state._fsp--;

                    adaptor.addChild(root_0, seg_hint.getTree());

                    specifyQueryStatement.hint = seg_hint.hint;

                    }
                    break;

            }


            INDEXWHERE5=(Token)match(input,INDEXWHERE,FOLLOW_INDEXWHERE_in_condition_stmt125); 
            INDEXWHERE5_tree = 
            (Object)adaptor.create(INDEXWHERE5)
            ;
            adaptor.addChild(root_0, INDEXWHERE5_tree);


            pushFollow(FOLLOW_expr_in_condition_stmt132);
            hash_Where=expr();

            state._fsp--;

            adaptor.addChild(root_0, hash_Where.getTree());

            specifyQueryStatement.hashWhere = hash_Where.expression;

            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:68:2: ( WHERE filterWhere= expr )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==WHERE) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:68:3: WHERE filterWhere= expr
                    {
                    WHERE6=(Token)match(input,WHERE,FOLLOW_WHERE_in_condition_stmt137); 
                    WHERE6_tree = 
                    (Object)adaptor.create(WHERE6)
                    ;
                    adaptor.addChild(root_0, WHERE6_tree);


                    pushFollow(FOLLOW_expr_in_condition_stmt143);
                    filterWhere=expr();

                    state._fsp--;

                    adaptor.addChild(root_0, filterWhere.getTree());

                    specifyQueryStatement.filterWhere = filterWhere.expression;

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
    // $ANTLR end "condition_stmt"


    public static class seghint_expr_return extends ParserRuleReturnScope {
        public TimeHint hint = new TimeHint();
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "seghint_expr"
    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:72:1: seghint_expr returns [TimeHint hint = new TimeHint()] : LPAREN startCreateTime= INTEGER COMMA endCreateTime= INTEGER COMMA startUpdateTime= INTEGER COMMA endUpdateTime= INTEGER RPAREN ;
    public final mileSqlConditionParser.seghint_expr_return seghint_expr() throws RecognitionException {
        mileSqlConditionParser.seghint_expr_return retval = new mileSqlConditionParser.seghint_expr_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token startCreateTime=null;
        Token endCreateTime=null;
        Token startUpdateTime=null;
        Token endUpdateTime=null;
        Token LPAREN7=null;
        Token COMMA8=null;
        Token COMMA9=null;
        Token COMMA10=null;
        Token RPAREN11=null;

        Object startCreateTime_tree=null;
        Object endCreateTime_tree=null;
        Object startUpdateTime_tree=null;
        Object endUpdateTime_tree=null;
        Object LPAREN7_tree=null;
        Object COMMA8_tree=null;
        Object COMMA9_tree=null;
        Object COMMA10_tree=null;
        Object RPAREN11_tree=null;

        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:73:2: ( LPAREN startCreateTime= INTEGER COMMA endCreateTime= INTEGER COMMA startUpdateTime= INTEGER COMMA endUpdateTime= INTEGER RPAREN )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:73:4: LPAREN startCreateTime= INTEGER COMMA endCreateTime= INTEGER COMMA startUpdateTime= INTEGER COMMA endUpdateTime= INTEGER RPAREN
            {
            root_0 = (Object)adaptor.nil();


            LPAREN7=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_seghint_expr162); 
            LPAREN7_tree = 
            (Object)adaptor.create(LPAREN7)
            ;
            adaptor.addChild(root_0, LPAREN7_tree);


            startCreateTime=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_seghint_expr166); 
            startCreateTime_tree = 
            (Object)adaptor.create(startCreateTime)
            ;
            adaptor.addChild(root_0, startCreateTime_tree);


            COMMA8=(Token)match(input,COMMA,FOLLOW_COMMA_in_seghint_expr168); 
            COMMA8_tree = 
            (Object)adaptor.create(COMMA8)
            ;
            adaptor.addChild(root_0, COMMA8_tree);


            endCreateTime=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_seghint_expr172); 
            endCreateTime_tree = 
            (Object)adaptor.create(endCreateTime)
            ;
            adaptor.addChild(root_0, endCreateTime_tree);


            COMMA9=(Token)match(input,COMMA,FOLLOW_COMMA_in_seghint_expr174); 
            COMMA9_tree = 
            (Object)adaptor.create(COMMA9)
            ;
            adaptor.addChild(root_0, COMMA9_tree);


            startUpdateTime=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_seghint_expr178); 
            startUpdateTime_tree = 
            (Object)adaptor.create(startUpdateTime)
            ;
            adaptor.addChild(root_0, startUpdateTime_tree);


            COMMA10=(Token)match(input,COMMA,FOLLOW_COMMA_in_seghint_expr180); 
            COMMA10_tree = 
            (Object)adaptor.create(COMMA10)
            ;
            adaptor.addChild(root_0, COMMA10_tree);


            endUpdateTime=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_seghint_expr184); 
            endUpdateTime_tree = 
            (Object)adaptor.create(endUpdateTime)
            ;
            adaptor.addChild(root_0, endUpdateTime_tree);


            RPAREN11=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_seghint_expr186); 
            RPAREN11_tree = 
            (Object)adaptor.create(RPAREN11)
            ;
            adaptor.addChild(root_0, RPAREN11_tree);



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
    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:83:1: expr returns [Expression expression] : or_sub= or_subexpr ( OR or_sub= or_subexpr )* ;
    public final mileSqlConditionParser.expr_return expr() throws RecognitionException {
        mileSqlConditionParser.expr_return retval = new mileSqlConditionParser.expr_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token OR12=null;
        mileSqlConditionParser.or_subexpr_return or_sub =null;


        Object OR12_tree=null;

        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:84:2: (or_sub= or_subexpr ( OR or_sub= or_subexpr )* )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:84:4: or_sub= or_subexpr ( OR or_sub= or_subexpr )*
            {
            root_0 = (Object)adaptor.nil();


            pushFollow(FOLLOW_or_subexpr_in_expr209);
            or_sub=or_subexpr();

            state._fsp--;

            adaptor.addChild(root_0, or_sub.getTree());


            		retval.expression =or_sub.expression;
            	

            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:88:2: ( OR or_sub= or_subexpr )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==OR) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:88:3: OR or_sub= or_subexpr
            	    {
            	    OR12=(Token)match(input,OR,FOLLOW_OR_in_expr216); 
            	    OR12_tree = 
            	    (Object)adaptor.create(OR12)
            	    ;
            	    adaptor.addChild(root_0, OR12_tree);


            	    pushFollow(FOLLOW_or_subexpr_in_expr221);
            	    or_sub=or_subexpr();

            	    state._fsp--;

            	    adaptor.addChild(root_0, or_sub.getTree());


            	    		retval.expression = retval.expression.orExp(or_sub.expression);
            	    	

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
    // $ANTLR end "expr"


    public static class or_subexpr_return extends ParserRuleReturnScope {
        public Expression expression;
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "or_subexpr"
    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:94:1: or_subexpr returns [Expression expression] : and_sub= and_subexpr ( AND and_sub= and_subexpr )* ;
    public final mileSqlConditionParser.or_subexpr_return or_subexpr() throws RecognitionException {
        mileSqlConditionParser.or_subexpr_return retval = new mileSqlConditionParser.or_subexpr_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token AND13=null;
        mileSqlConditionParser.and_subexpr_return and_sub =null;


        Object AND13_tree=null;

        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:95:2: (and_sub= and_subexpr ( AND and_sub= and_subexpr )* )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:95:4: and_sub= and_subexpr ( AND and_sub= and_subexpr )*
            {
            root_0 = (Object)adaptor.nil();


            pushFollow(FOLLOW_and_subexpr_in_or_subexpr246);
            and_sub=and_subexpr();

            state._fsp--;

            adaptor.addChild(root_0, and_sub.getTree());


            		retval.expression =and_sub.expression;
            	

            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:99:2: ( AND and_sub= and_subexpr )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==AND) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:99:3: AND and_sub= and_subexpr
            	    {
            	    AND13=(Token)match(input,AND,FOLLOW_AND_in_or_subexpr253); 
            	    AND13_tree = 
            	    (Object)adaptor.create(AND13)
            	    ;
            	    adaptor.addChild(root_0, AND13_tree);


            	    pushFollow(FOLLOW_and_subexpr_in_or_subexpr257);
            	    and_sub=and_subexpr();

            	    state._fsp--;

            	    adaptor.addChild(root_0, and_sub.getTree());


            	    		retval.expression = retval.expression.andExp(and_sub.expression);
            	    	

            	    }
            	    break;

            	default :
            	    break loop4;
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
    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:105:1: and_subexpr returns [Expression expression] : (eq_sub= eq_subexpr | LPAREN expr_sub= expr RPAREN );
    public final mileSqlConditionParser.and_subexpr_return and_subexpr() throws RecognitionException {
        mileSqlConditionParser.and_subexpr_return retval = new mileSqlConditionParser.and_subexpr_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token LPAREN14=null;
        Token RPAREN15=null;
        mileSqlConditionParser.eq_subexpr_return eq_sub =null;

        mileSqlConditionParser.expr_return expr_sub =null;


        Object LPAREN14_tree=null;
        Object RPAREN15_tree=null;

        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:106:2: (eq_sub= eq_subexpr | LPAREN expr_sub= expr RPAREN )
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==ID) ) {
                alt5=1;
            }
            else if ( (LA5_0==LPAREN) ) {
                alt5=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;

            }
            switch (alt5) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:106:4: eq_sub= eq_subexpr
                    {
                    root_0 = (Object)adaptor.nil();


                    pushFollow(FOLLOW_eq_subexpr_in_and_subexpr280);
                    eq_sub=eq_subexpr();

                    state._fsp--;

                    adaptor.addChild(root_0, eq_sub.getTree());


                    		retval.expression = eq_sub.expression;
                    	

                    }
                    break;
                case 2 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:110:4: LPAREN expr_sub= expr RPAREN
                    {
                    root_0 = (Object)adaptor.nil();


                    LPAREN14=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_and_subexpr288); 
                    LPAREN14_tree = 
                    (Object)adaptor.create(LPAREN14)
                    ;
                    adaptor.addChild(root_0, LPAREN14_tree);


                    pushFollow(FOLLOW_expr_in_and_subexpr292);
                    expr_sub=expr();

                    state._fsp--;

                    adaptor.addChild(root_0, expr_sub.getTree());

                    RPAREN15=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_and_subexpr294); 
                    RPAREN15_tree = 
                    (Object)adaptor.create(RPAREN15)
                    ;
                    adaptor.addChild(root_0, RPAREN15_tree);



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


    public static class eq_subexpr_return extends ParserRuleReturnScope {
        public Expression expression;
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "eq_subexpr"
    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:117:1: eq_subexpr returns [Expression expression] : (column_name= ID leq= ( LESS | LESS_OR_EQ | GREATER | GREATER_OR_EQ | EQUALS ) lv= literal_value |column_name2= ID BETWEEN ll= ( LPAREN | LPAREN_SQUARE ) lv1= literal_value COMMA lv2= literal_value rr= ( RPAREN | RPAREN_SQUARE ) );
    public final mileSqlConditionParser.eq_subexpr_return eq_subexpr() throws RecognitionException {
        mileSqlConditionParser.eq_subexpr_return retval = new mileSqlConditionParser.eq_subexpr_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token column_name=null;
        Token leq=null;
        Token column_name2=null;
        Token ll=null;
        Token rr=null;
        Token BETWEEN16=null;
        Token COMMA17=null;
        mileSqlConditionParser.literal_value_return lv =null;

        mileSqlConditionParser.literal_value_return lv1 =null;

        mileSqlConditionParser.literal_value_return lv2 =null;


        Object column_name_tree=null;
        Object leq_tree=null;
        Object column_name2_tree=null;
        Object ll_tree=null;
        Object rr_tree=null;
        Object BETWEEN16_tree=null;
        Object COMMA17_tree=null;

        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:118:2: (column_name= ID leq= ( LESS | LESS_OR_EQ | GREATER | GREATER_OR_EQ | EQUALS ) lv= literal_value |column_name2= ID BETWEEN ll= ( LPAREN | LPAREN_SQUARE ) lv1= literal_value COMMA lv2= literal_value rr= ( RPAREN | RPAREN_SQUARE ) )
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==ID) ) {
                int LA6_1 = input.LA(2);

                if ( (LA6_1==EQUALS||(LA6_1 >= GREATER && LA6_1 <= GREATER_OR_EQ)||(LA6_1 >= LESS && LA6_1 <= LESS_OR_EQ)) ) {
                    alt6=1;
                }
                else if ( (LA6_1==BETWEEN) ) {
                    alt6=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 1, input);

                    throw nvae;

                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;

            }
            switch (alt6) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:118:4: column_name= ID leq= ( LESS | LESS_OR_EQ | GREATER | GREATER_OR_EQ | EQUALS ) lv= literal_value
                    {
                    root_0 = (Object)adaptor.nil();


                    column_name=(Token)match(input,ID,FOLLOW_ID_in_eq_subexpr317); 
                    column_name_tree = 
                    (Object)adaptor.create(column_name)
                    ;
                    adaptor.addChild(root_0, column_name_tree);


                    leq=(Token)input.LT(1);

                    if ( input.LA(1)==EQUALS||(input.LA(1) >= GREATER && input.LA(1) <= GREATER_OR_EQ)||(input.LA(1) >= LESS && input.LA(1) <= LESS_OR_EQ) ) {
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


                    pushFollow(FOLLOW_literal_value_in_eq_subexpr347);
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
                    break;
                case 2 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:140:4: column_name2= ID BETWEEN ll= ( LPAREN | LPAREN_SQUARE ) lv1= literal_value COMMA lv2= literal_value rr= ( RPAREN | RPAREN_SQUARE )
                    {
                    root_0 = (Object)adaptor.nil();


                    column_name2=(Token)match(input,ID,FOLLOW_ID_in_eq_subexpr357); 
                    column_name2_tree = 
                    (Object)adaptor.create(column_name2)
                    ;
                    adaptor.addChild(root_0, column_name2_tree);


                    BETWEEN16=(Token)match(input,BETWEEN,FOLLOW_BETWEEN_in_eq_subexpr360); 
                    BETWEEN16_tree = 
                    (Object)adaptor.create(BETWEEN16)
                    ;
                    adaptor.addChild(root_0, BETWEEN16_tree);


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


                    pushFollow(FOLLOW_literal_value_in_eq_subexpr372);
                    lv1=literal_value();

                    state._fsp--;

                    adaptor.addChild(root_0, lv1.getTree());

                    COMMA17=(Token)match(input,COMMA,FOLLOW_COMMA_in_eq_subexpr374); 
                    COMMA17_tree = 
                    (Object)adaptor.create(COMMA17)
                    ;
                    adaptor.addChild(root_0, COMMA17_tree);


                    pushFollow(FOLLOW_literal_value_in_eq_subexpr378);
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
    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:164:1: eq_sequence returns [List<FieldValuePair> fieldValues = new ArrayList<FieldValuePair>()] : column_name= ID EQUALS lv= literal_value ( COMMA column_name2= ID EQUALS lv2= literal_value )* ;
    public final mileSqlConditionParser.eq_sequence_return eq_sequence() throws RecognitionException {
        mileSqlConditionParser.eq_sequence_return retval = new mileSqlConditionParser.eq_sequence_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token column_name=null;
        Token column_name2=null;
        Token EQUALS18=null;
        Token COMMA19=null;
        Token EQUALS20=null;
        mileSqlConditionParser.literal_value_return lv =null;

        mileSqlConditionParser.literal_value_return lv2 =null;


        Object column_name_tree=null;
        Object column_name2_tree=null;
        Object EQUALS18_tree=null;
        Object COMMA19_tree=null;
        Object EQUALS20_tree=null;

        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:165:2: (column_name= ID EQUALS lv= literal_value ( COMMA column_name2= ID EQUALS lv2= literal_value )* )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:165:4: column_name= ID EQUALS lv= literal_value ( COMMA column_name2= ID EQUALS lv2= literal_value )*
            {
            root_0 = (Object)adaptor.nil();


            column_name=(Token)match(input,ID,FOLLOW_ID_in_eq_sequence408); 
            column_name_tree = 
            (Object)adaptor.create(column_name)
            ;
            adaptor.addChild(root_0, column_name_tree);


            EQUALS18=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_eq_sequence410); 
            EQUALS18_tree = 
            (Object)adaptor.create(EQUALS18)
            ;
            adaptor.addChild(root_0, EQUALS18_tree);


            pushFollow(FOLLOW_literal_value_in_eq_sequence416);
            lv=literal_value();

            state._fsp--;

            adaptor.addChild(root_0, lv.getTree());


            		FieldValuePair fvp = new FieldValuePair();
            		FieldDesc fd = new FieldDesc();
            		fd.fieldName = column_name.getText().trim();
            		fvp.field = fd;
            		fvp.value = lv.valueDesc;
            		retval.fieldValues.add(fvp);
            	

            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:174:2: ( COMMA column_name2= ID EQUALS lv2= literal_value )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( (LA7_0==COMMA) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:174:4: COMMA column_name2= ID EQUALS lv2= literal_value
            	    {
            	    COMMA19=(Token)match(input,COMMA,FOLLOW_COMMA_in_eq_sequence424); 
            	    COMMA19_tree = 
            	    (Object)adaptor.create(COMMA19)
            	    ;
            	    adaptor.addChild(root_0, COMMA19_tree);


            	    column_name2=(Token)match(input,ID,FOLLOW_ID_in_eq_sequence430); 
            	    column_name2_tree = 
            	    (Object)adaptor.create(column_name2)
            	    ;
            	    adaptor.addChild(root_0, column_name2_tree);


            	    EQUALS20=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_eq_sequence432); 
            	    EQUALS20_tree = 
            	    (Object)adaptor.create(EQUALS20)
            	    ;
            	    adaptor.addChild(root_0, EQUALS20_tree);


            	    pushFollow(FOLLOW_literal_value_in_eq_sequence438);
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
            	    break loop7;
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


    public static class literal_value_return extends ParserRuleReturnScope {
        public ValueDesc valueDesc = new ValueDesc();
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "literal_value"
    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:187:1: literal_value returns [ValueDesc valueDesc = new ValueDesc()] : (rs= INTEGER |rs= FLOAT |rs= STRING |rs= QUESTION ) ;
    public final mileSqlConditionParser.literal_value_return literal_value() throws RecognitionException {
        mileSqlConditionParser.literal_value_return retval = new mileSqlConditionParser.literal_value_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token rs=null;

        Object rs_tree=null;

        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:188:2: ( (rs= INTEGER |rs= FLOAT |rs= STRING |rs= QUESTION ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:189:2: (rs= INTEGER |rs= FLOAT |rs= STRING |rs= QUESTION )
            {
            root_0 = (Object)adaptor.nil();


            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:189:2: (rs= INTEGER |rs= FLOAT |rs= STRING |rs= QUESTION )
            int alt8=4;
            switch ( input.LA(1) ) {
            case INTEGER:
                {
                alt8=1;
                }
                break;
            case FLOAT:
                {
                alt8=2;
                }
                break;
            case STRING:
                {
                alt8=3;
                }
                break;
            case QUESTION:
                {
                alt8=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;

            }

            switch (alt8) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:189:4: rs= INTEGER
                    {
                    rs=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_literal_value469); 
                    rs_tree = 
                    (Object)adaptor.create(rs)
                    ;
                    adaptor.addChild(root_0, rs_tree);



                    		String strTemp = rs.getText().trim();
                    		retval.valueDesc.valueDesc = strTemp;
                    	

                    }
                    break;
                case 2 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:194:4: rs= FLOAT
                    {
                    rs=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_literal_value481); 
                    rs_tree = 
                    (Object)adaptor.create(rs)
                    ;
                    adaptor.addChild(root_0, rs_tree);



                    		String strTemp = rs.getText().trim();
                    			retval.valueDesc.valueDesc = strTemp;
                    	

                    }
                    break;
                case 3 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:199:4: rs= STRING
                    {
                    rs=(Token)match(input,STRING,FOLLOW_STRING_in_literal_value493); 
                    rs_tree = 
                    (Object)adaptor.create(rs)
                    ;
                    adaptor.addChild(root_0, rs_tree);



                    		String strTemp = rs.getText().trim();
                    		   retval.valueDesc.valueDesc = strTemp.substring(1, strTemp.length()-1);

                    	

                    }
                    break;
                case 4 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:205:4: rs= QUESTION
                    {
                    rs=(Token)match(input,QUESTION,FOLLOW_QUESTION_in_literal_value505); 
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
    public static final BitSet FOLLOW_EOF_in_sql_stmt79 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_condition_stmt_in_sql_stmt_core95 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEGHINT_in_condition_stmt112 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_seghint_expr_in_condition_stmt118 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_INDEXWHERE_in_condition_stmt125 = new BitSet(new long[]{0x0000008008000000L});
    public static final BitSet FOLLOW_expr_in_condition_stmt132 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_WHERE_in_condition_stmt137 = new BitSet(new long[]{0x0000008008000000L});
    public static final BitSet FOLLOW_expr_in_condition_stmt143 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_seghint_expr162 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_INTEGER_in_seghint_expr166 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_COMMA_in_seghint_expr168 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_INTEGER_in_seghint_expr172 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_COMMA_in_seghint_expr174 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_INTEGER_in_seghint_expr178 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_COMMA_in_seghint_expr180 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_INTEGER_in_seghint_expr184 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_RPAREN_in_seghint_expr186 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_or_subexpr_in_expr209 = new BitSet(new long[]{0x0000200000000002L});
    public static final BitSet FOLLOW_OR_in_expr216 = new BitSet(new long[]{0x0000008008000000L});
    public static final BitSet FOLLOW_or_subexpr_in_expr221 = new BitSet(new long[]{0x0000200000000002L});
    public static final BitSet FOLLOW_and_subexpr_in_or_subexpr246 = new BitSet(new long[]{0x0000000000000022L});
    public static final BitSet FOLLOW_AND_in_or_subexpr253 = new BitSet(new long[]{0x0000008008000000L});
    public static final BitSet FOLLOW_and_subexpr_in_or_subexpr257 = new BitSet(new long[]{0x0000000000000022L});
    public static final BitSet FOLLOW_eq_subexpr_in_and_subexpr280 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_and_subexpr288 = new BitSet(new long[]{0x0000008008000000L});
    public static final BitSet FOLLOW_expr_in_and_subexpr292 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_RPAREN_in_and_subexpr294 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_eq_subexpr317 = new BitSet(new long[]{0x0000003001840000L});
    public static final BitSet FOLLOW_set_in_eq_subexpr323 = new BitSet(new long[]{0x0201000100100000L});
    public static final BitSet FOLLOW_literal_value_in_eq_subexpr347 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_eq_subexpr357 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_BETWEEN_in_eq_subexpr360 = new BitSet(new long[]{0x0000018000000000L});
    public static final BitSet FOLLOW_set_in_eq_subexpr364 = new BitSet(new long[]{0x0201000100100000L});
    public static final BitSet FOLLOW_literal_value_in_eq_subexpr372 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_COMMA_in_eq_subexpr374 = new BitSet(new long[]{0x0201000100100000L});
    public static final BitSet FOLLOW_literal_value_in_eq_subexpr378 = new BitSet(new long[]{0x0030000000000000L});
    public static final BitSet FOLLOW_set_in_eq_subexpr382 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_eq_sequence408 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_EQUALS_in_eq_sequence410 = new BitSet(new long[]{0x0201000100100000L});
    public static final BitSet FOLLOW_literal_value_in_eq_sequence416 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_COMMA_in_eq_sequence424 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_ID_in_eq_sequence430 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_EQUALS_in_eq_sequence432 = new BitSet(new long[]{0x0201000100100000L});
    public static final BitSet FOLLOW_literal_value_in_eq_sequence438 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_INTEGER_in_literal_value469 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal_value481 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal_value493 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUESTION_in_literal_value505 = new BitSet(new long[]{0x0000000000000002L});

}