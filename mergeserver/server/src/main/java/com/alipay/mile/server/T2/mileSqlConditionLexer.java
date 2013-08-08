// $ANTLR 3.4 E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g 2012-05-15 17:23:39

package com.alipay.mile.server.T2;
import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.DFA;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.IntStream;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;

import com.alipay.mile.mileexception.SQLException;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class mileSqlConditionLexer extends Lexer {
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

    	@Override
    	public void reportError(RecognitionException e) {
    		throw new SQLException("\t\n"+e.input.toString() +"\t\n"+getErrorHeader(e)+ " " + getErrorMessage(e, getTokenNames()) );
    	}


    // delegates
    // delegators
    public Lexer[] getDelegates() {
        return new Lexer[] {};
    }

    public mileSqlConditionLexer() {} 
    public mileSqlConditionLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public mileSqlConditionLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);
    }
    public String getGrammarFileName() { return "E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g"; }

    // $ANTLR start "EQUALS"
    public final void mEQUALS() throws RecognitionException {
        try {
            int _type = EQUALS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:214:7: ( '=' )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:214:16: '='
            {
            match('='); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "EQUALS"

    // $ANTLR start "NOT_EQUALS2"
    public final void mNOT_EQUALS2() throws RecognitionException {
        try {
            int _type = NOT_EQUALS2;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:215:12: ( '<>' )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:215:16: '<>'
            {
            match("<>"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NOT_EQUALS2"

    // $ANTLR start "LESS"
    public final void mLESS() throws RecognitionException {
        try {
            int _type = LESS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:216:5: ( '<' )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:216:16: '<'
            {
            match('<'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LESS"

    // $ANTLR start "LESS_OR_EQ"
    public final void mLESS_OR_EQ() throws RecognitionException {
        try {
            int _type = LESS_OR_EQ;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:217:11: ( '<=' )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:217:16: '<='
            {
            match("<="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LESS_OR_EQ"

    // $ANTLR start "GREATER"
    public final void mGREATER() throws RecognitionException {
        try {
            int _type = GREATER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:218:8: ( '>' )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:218:16: '>'
            {
            match('>'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "GREATER"

    // $ANTLR start "GREATER_OR_EQ"
    public final void mGREATER_OR_EQ() throws RecognitionException {
        try {
            int _type = GREATER_OR_EQ;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:219:14: ( '>=' )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:219:16: '>='
            {
            match(">="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "GREATER_OR_EQ"

    // $ANTLR start "SEMI"
    public final void mSEMI() throws RecognitionException {
        try {
            int _type = SEMI;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:220:5: ( ';' )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:220:16: ';'
            {
            match(';'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SEMI"

    // $ANTLR start "COMMA"
    public final void mCOMMA() throws RecognitionException {
        try {
            int _type = COMMA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:221:6: ( ',' )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:221:16: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "COMMA"

    // $ANTLR start "LPAREN"
    public final void mLPAREN() throws RecognitionException {
        try {
            int _type = LPAREN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:222:7: ( '(' )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:222:16: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LPAREN"

    // $ANTLR start "RPAREN"
    public final void mRPAREN() throws RecognitionException {
        try {
            int _type = RPAREN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:223:7: ( ')' )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:223:16: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "RPAREN"

    // $ANTLR start "DOT"
    public final void mDOT() throws RecognitionException {
        try {
            int _type = DOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:224:4: ( '.' )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:224:16: '.'
            {
            match('.'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "DOT"

    // $ANTLR start "UNDERSCORE"
    public final void mUNDERSCORE() throws RecognitionException {
        try {
            int _type = UNDERSCORE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:225:11: ( '_' )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:225:16: '_'
            {
            match('_'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "UNDERSCORE"

    // $ANTLR start "QUESTION"
    public final void mQUESTION() throws RecognitionException {
        try {
            int _type = QUESTION;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:226:9: ( '?' )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:226:16: '?'
            {
            match('?'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "QUESTION"

    // $ANTLR start "QUOTE_DOUBLE"
    public final void mQUOTE_DOUBLE() throws RecognitionException {
        try {
            int _type = QUOTE_DOUBLE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:227:13: ( '\"' )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:227:16: '\"'
            {
            match('\"'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "QUOTE_DOUBLE"

    // $ANTLR start "QUOTE_SINGLE"
    public final void mQUOTE_SINGLE() throws RecognitionException {
        try {
            int _type = QUOTE_SINGLE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:228:13: ( '\\'' )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:228:16: '\\''
            {
            match('\''); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "QUOTE_SINGLE"

    // $ANTLR start "BACKSLASH"
    public final void mBACKSLASH() throws RecognitionException {
        try {
            int _type = BACKSLASH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:229:10: ( '\\\\' )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:229:16: '\\\\'
            {
            match('\\'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "BACKSLASH"

    // $ANTLR start "ASTERISK"
    public final void mASTERISK() throws RecognitionException {
        try {
            int _type = ASTERISK;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:230:9: ( '*' )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:230:16: '*'
            {
            match('*'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ASTERISK"

    // $ANTLR start "LPAREN_SQUARE"
    public final void mLPAREN_SQUARE() throws RecognitionException {
        try {
            int _type = LPAREN_SQUARE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:231:14: ( '[' )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:231:16: '['
            {
            match('['); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LPAREN_SQUARE"

    // $ANTLR start "RPAREN_SQUARE"
    public final void mRPAREN_SQUARE() throws RecognitionException {
        try {
            int _type = RPAREN_SQUARE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:232:14: ( ']' )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:232:16: ']'
            {
            match(']'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "RPAREN_SQUARE"

    // $ANTLR start "A"
    public final void mA() throws RecognitionException {
        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:235:11: ( ( 'a' | 'A' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:
            {
            if ( input.LA(1)=='A'||input.LA(1)=='a' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "A"

    // $ANTLR start "B"
    public final void mB() throws RecognitionException {
        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:236:11: ( ( 'b' | 'B' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:
            {
            if ( input.LA(1)=='B'||input.LA(1)=='b' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "B"

    // $ANTLR start "C"
    public final void mC() throws RecognitionException {
        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:237:11: ( ( 'c' | 'C' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:
            {
            if ( input.LA(1)=='C'||input.LA(1)=='c' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "C"

    // $ANTLR start "D"
    public final void mD() throws RecognitionException {
        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:238:11: ( ( 'd' | 'D' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:
            {
            if ( input.LA(1)=='D'||input.LA(1)=='d' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "D"

    // $ANTLR start "E"
    public final void mE() throws RecognitionException {
        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:239:11: ( ( 'e' | 'E' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:
            {
            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "E"

    // $ANTLR start "F"
    public final void mF() throws RecognitionException {
        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:240:11: ( ( 'f' | 'F' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:
            {
            if ( input.LA(1)=='F'||input.LA(1)=='f' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "F"

    // $ANTLR start "G"
    public final void mG() throws RecognitionException {
        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:241:11: ( ( 'g' | 'G' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:
            {
            if ( input.LA(1)=='G'||input.LA(1)=='g' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "G"

    // $ANTLR start "H"
    public final void mH() throws RecognitionException {
        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:242:11: ( ( 'h' | 'H' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:
            {
            if ( input.LA(1)=='H'||input.LA(1)=='h' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "H"

    // $ANTLR start "I"
    public final void mI() throws RecognitionException {
        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:243:11: ( ( 'i' | 'I' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:
            {
            if ( input.LA(1)=='I'||input.LA(1)=='i' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "I"

    // $ANTLR start "J"
    public final void mJ() throws RecognitionException {
        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:244:11: ( ( 'j' | 'J' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:
            {
            if ( input.LA(1)=='J'||input.LA(1)=='j' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "J"

    // $ANTLR start "K"
    public final void mK() throws RecognitionException {
        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:245:11: ( ( 'k' | 'K' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:
            {
            if ( input.LA(1)=='K'||input.LA(1)=='k' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "K"

    // $ANTLR start "L"
    public final void mL() throws RecognitionException {
        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:246:11: ( ( 'l' | 'L' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:
            {
            if ( input.LA(1)=='L'||input.LA(1)=='l' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "L"

    // $ANTLR start "M"
    public final void mM() throws RecognitionException {
        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:247:11: ( ( 'm' | 'M' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:
            {
            if ( input.LA(1)=='M'||input.LA(1)=='m' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "M"

    // $ANTLR start "N"
    public final void mN() throws RecognitionException {
        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:248:11: ( ( 'n' | 'N' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:
            {
            if ( input.LA(1)=='N'||input.LA(1)=='n' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "N"

    // $ANTLR start "O"
    public final void mO() throws RecognitionException {
        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:249:11: ( ( 'o' | 'O' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:
            {
            if ( input.LA(1)=='O'||input.LA(1)=='o' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "O"

    // $ANTLR start "P"
    public final void mP() throws RecognitionException {
        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:250:11: ( ( 'p' | 'P' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:
            {
            if ( input.LA(1)=='P'||input.LA(1)=='p' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "P"

    // $ANTLR start "Q"
    public final void mQ() throws RecognitionException {
        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:251:11: ( ( 'q' | 'Q' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:
            {
            if ( input.LA(1)=='Q'||input.LA(1)=='q' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "Q"

    // $ANTLR start "R"
    public final void mR() throws RecognitionException {
        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:252:11: ( ( 'r' | 'R' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:
            {
            if ( input.LA(1)=='R'||input.LA(1)=='r' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "R"

    // $ANTLR start "S"
    public final void mS() throws RecognitionException {
        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:253:11: ( ( 's' | 'S' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:
            {
            if ( input.LA(1)=='S'||input.LA(1)=='s' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "S"

    // $ANTLR start "T"
    public final void mT() throws RecognitionException {
        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:254:11: ( ( 't' | 'T' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:
            {
            if ( input.LA(1)=='T'||input.LA(1)=='t' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T"

    // $ANTLR start "U"
    public final void mU() throws RecognitionException {
        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:255:11: ( ( 'u' | 'U' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:
            {
            if ( input.LA(1)=='U'||input.LA(1)=='u' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "U"

    // $ANTLR start "V"
    public final void mV() throws RecognitionException {
        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:256:11: ( ( 'v' | 'V' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:
            {
            if ( input.LA(1)=='V'||input.LA(1)=='v' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "V"

    // $ANTLR start "W"
    public final void mW() throws RecognitionException {
        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:257:11: ( ( 'w' | 'W' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:
            {
            if ( input.LA(1)=='W'||input.LA(1)=='w' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "W"

    // $ANTLR start "X"
    public final void mX() throws RecognitionException {
        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:258:11: ( ( 'x' | 'X' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:
            {
            if ( input.LA(1)=='X'||input.LA(1)=='x' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "X"

    // $ANTLR start "Y"
    public final void mY() throws RecognitionException {
        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:259:11: ( ( 'y' | 'Y' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:
            {
            if ( input.LA(1)=='Y'||input.LA(1)=='y' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "Y"

    // $ANTLR start "Z"
    public final void mZ() throws RecognitionException {
        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:260:11: ( ( 'z' | 'Z' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:
            {
            if ( input.LA(1)=='Z'||input.LA(1)=='z' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "Z"

    // $ANTLR start "AND"
    public final void mAND() throws RecognitionException {
        try {
            int _type = AND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:262:4: ( A N D )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:262:6: A N D
            {
            mA(); 


            mN(); 


            mD(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "AND"

    // $ANTLR start "BETWEEN"
    public final void mBETWEEN() throws RecognitionException {
        try {
            int _type = BETWEEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:263:9: ( B E T W E E N )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:263:10: B E T W E E N
            {
            mB(); 


            mE(); 


            mT(); 


            mW(); 


            mE(); 


            mE(); 


            mN(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "BETWEEN"

    // $ANTLR start "CURRENT_TIMESTAMP"
    public final void mCURRENT_TIMESTAMP() throws RecognitionException {
        try {
            int _type = CURRENT_TIMESTAMP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:264:18: ( C U R R E N T '_' T I M E S T A M P )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:264:20: C U R R E N T '_' T I M E S T A M P
            {
            mC(); 


            mU(); 


            mR(); 


            mR(); 


            mE(); 


            mN(); 


            mT(); 


            match('_'); 

            mT(); 


            mI(); 


            mM(); 


            mE(); 


            mS(); 


            mT(); 


            mA(); 


            mM(); 


            mP(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "CURRENT_TIMESTAMP"

    // $ANTLR start "INDEXWHERE"
    public final void mINDEXWHERE() throws RecognitionException {
        try {
            int _type = INDEXWHERE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:265:11: ( I N D E X W H E R E )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:265:13: I N D E X W H E R E
            {
            mI(); 


            mN(); 


            mD(); 


            mE(); 


            mX(); 


            mW(); 


            mH(); 


            mE(); 


            mR(); 


            mE(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "INDEXWHERE"

    // $ANTLR start "UNIONHASH"
    public final void mUNIONHASH() throws RecognitionException {
        try {
            int _type = UNIONHASH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:266:10: ( U N I O N H A S H )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:266:12: U N I O N H A S H
            {
            mU(); 


            mN(); 


            mI(); 


            mO(); 


            mN(); 


            mH(); 


            mA(); 


            mS(); 


            mH(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "UNIONHASH"

    // $ANTLR start "OR"
    public final void mOR() throws RecognitionException {
        try {
            int _type = OR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:267:3: ( O R )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:267:5: O R
            {
            mO(); 


            mR(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "OR"

    // $ANTLR start "SEGHINT"
    public final void mSEGHINT() throws RecognitionException {
        try {
            int _type = SEGHINT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:268:9: ( S E G H I N T )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:268:10: S E G H I N T
            {
            mS(); 


            mE(); 


            mG(); 


            mH(); 


            mI(); 


            mN(); 


            mT(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SEGHINT"

    // $ANTLR start "WHERE"
    public final void mWHERE() throws RecognitionException {
        try {
            int _type = WHERE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:269:6: ( W H E R E )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:269:8: W H E R E
            {
            mW(); 


            mH(); 


            mE(); 


            mR(); 


            mE(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "WHERE"

    // $ANTLR start "STRING_ESCAPE_SINGLE"
    public final void mSTRING_ESCAPE_SINGLE() throws RecognitionException {
        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:271:30: ( ( BACKSLASH QUOTE_SINGLE ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:271:32: ( BACKSLASH QUOTE_SINGLE )
            {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:271:32: ( BACKSLASH QUOTE_SINGLE )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:271:33: BACKSLASH QUOTE_SINGLE
            {
            mBACKSLASH(); 


            mQUOTE_SINGLE(); 


            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "STRING_ESCAPE_SINGLE"

    // $ANTLR start "STRING_ESCAPE_DOUBLE"
    public final void mSTRING_ESCAPE_DOUBLE() throws RecognitionException {
        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:272:30: ( ( BACKSLASH QUOTE_DOUBLE ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:272:32: ( BACKSLASH QUOTE_DOUBLE )
            {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:272:32: ( BACKSLASH QUOTE_DOUBLE )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:272:33: BACKSLASH QUOTE_DOUBLE
            {
            mBACKSLASH(); 


            mQUOTE_DOUBLE(); 


            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "STRING_ESCAPE_DOUBLE"

    // $ANTLR start "STRING_CORE"
    public final void mSTRING_CORE() throws RecognitionException {
        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:273:21: (~ ( QUOTE_SINGLE | QUOTE_DOUBLE ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:
            {
            if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '!')||(input.LA(1) >= '#' && input.LA(1) <= '&')||(input.LA(1) >= '(' && input.LA(1) <= '\uFFFF') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "STRING_CORE"

    // $ANTLR start "STRING_CORE_SINGLE"
    public final void mSTRING_CORE_SINGLE() throws RecognitionException {
        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:274:28: ( ( STRING_CORE | QUOTE_DOUBLE | STRING_ESCAPE_SINGLE )* )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:274:30: ( STRING_CORE | QUOTE_DOUBLE | STRING_ESCAPE_SINGLE )*
            {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:274:30: ( STRING_CORE | QUOTE_DOUBLE | STRING_ESCAPE_SINGLE )*
            loop1:
            do {
                int alt1=4;
                int LA1_0 = input.LA(1);

                if ( (LA1_0=='\\') ) {
                    int LA1_2 = input.LA(2);

                    if ( (LA1_2=='\'') ) {
                        alt1=3;
                    }

                    else {
                        alt1=1;
                    }


                }
                else if ( (LA1_0=='\"') ) {
                    alt1=2;
                }
                else if ( ((LA1_0 >= '\u0000' && LA1_0 <= '!')||(LA1_0 >= '#' && LA1_0 <= '&')||(LA1_0 >= '(' && LA1_0 <= '[')||(LA1_0 >= ']' && LA1_0 <= '\uFFFF')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:274:32: STRING_CORE
            	    {
            	    mSTRING_CORE(); 


            	    }
            	    break;
            	case 2 :
            	    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:274:46: QUOTE_DOUBLE
            	    {
            	    mQUOTE_DOUBLE(); 


            	    }
            	    break;
            	case 3 :
            	    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:274:61: STRING_ESCAPE_SINGLE
            	    {
            	    mSTRING_ESCAPE_SINGLE(); 


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "STRING_CORE_SINGLE"

    // $ANTLR start "STRING_CORE_DOUBLE"
    public final void mSTRING_CORE_DOUBLE() throws RecognitionException {
        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:275:28: ( ( STRING_CORE | QUOTE_SINGLE | STRING_ESCAPE_DOUBLE )* )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:275:30: ( STRING_CORE | QUOTE_SINGLE | STRING_ESCAPE_DOUBLE )*
            {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:275:30: ( STRING_CORE | QUOTE_SINGLE | STRING_ESCAPE_DOUBLE )*
            loop2:
            do {
                int alt2=4;
                int LA2_0 = input.LA(1);

                if ( (LA2_0=='\\') ) {
                    int LA2_2 = input.LA(2);

                    if ( (LA2_2=='\"') ) {
                        alt2=3;
                    }

                    else {
                        alt2=1;
                    }


                }
                else if ( (LA2_0=='\'') ) {
                    alt2=2;
                }
                else if ( ((LA2_0 >= '\u0000' && LA2_0 <= '!')||(LA2_0 >= '#' && LA2_0 <= '&')||(LA2_0 >= '(' && LA2_0 <= '[')||(LA2_0 >= ']' && LA2_0 <= '\uFFFF')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:275:32: STRING_CORE
            	    {
            	    mSTRING_CORE(); 


            	    }
            	    break;
            	case 2 :
            	    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:275:46: QUOTE_SINGLE
            	    {
            	    mQUOTE_SINGLE(); 


            	    }
            	    break;
            	case 3 :
            	    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:275:61: STRING_ESCAPE_DOUBLE
            	    {
            	    mSTRING_ESCAPE_DOUBLE(); 


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "STRING_CORE_DOUBLE"

    // $ANTLR start "STRING_SINGLE"
    public final void mSTRING_SINGLE() throws RecognitionException {
        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:276:23: ( ( QUOTE_SINGLE STRING_CORE_SINGLE QUOTE_SINGLE ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:276:25: ( QUOTE_SINGLE STRING_CORE_SINGLE QUOTE_SINGLE )
            {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:276:25: ( QUOTE_SINGLE STRING_CORE_SINGLE QUOTE_SINGLE )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:276:26: QUOTE_SINGLE STRING_CORE_SINGLE QUOTE_SINGLE
            {
            mQUOTE_SINGLE(); 


            mSTRING_CORE_SINGLE(); 


            mQUOTE_SINGLE(); 


            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "STRING_SINGLE"

    // $ANTLR start "STRING_DOUBLE"
    public final void mSTRING_DOUBLE() throws RecognitionException {
        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:277:23: ( ( QUOTE_DOUBLE STRING_CORE_DOUBLE QUOTE_DOUBLE ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:277:25: ( QUOTE_DOUBLE STRING_CORE_DOUBLE QUOTE_DOUBLE )
            {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:277:25: ( QUOTE_DOUBLE STRING_CORE_DOUBLE QUOTE_DOUBLE )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:277:26: QUOTE_DOUBLE STRING_CORE_DOUBLE QUOTE_DOUBLE
            {
            mQUOTE_DOUBLE(); 


            mSTRING_CORE_DOUBLE(); 


            mQUOTE_DOUBLE(); 


            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "STRING_DOUBLE"

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:278:7: ( ( STRING_SINGLE | STRING_DOUBLE ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:278:9: ( STRING_SINGLE | STRING_DOUBLE )
            {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:278:9: ( STRING_SINGLE | STRING_DOUBLE )
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0=='\'') ) {
                alt3=1;
            }
            else if ( (LA3_0=='\"') ) {
                alt3=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;

            }
            switch (alt3) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:278:10: STRING_SINGLE
                    {
                    mSTRING_SINGLE(); 


                    }
                    break;
                case 2 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:278:26: STRING_DOUBLE
                    {
                    mSTRING_DOUBLE(); 


                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "STRING"

    // $ANTLR start "ID_START"
    public final void mID_START() throws RecognitionException {
        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:280:18: ( ( 'a' .. 'z' | 'A' .. 'Z' | UNDERSCORE ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:
            {
            if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ID_START"

    // $ANTLR start "ID_CORE"
    public final void mID_CORE() throws RecognitionException {
        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:281:17: ( ( ID_START | '0' .. '9' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:
            {
            if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ID_CORE"

    // $ANTLR start "ID_PLAIN"
    public final void mID_PLAIN() throws RecognitionException {
        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:282:18: ( ID_START ( ID_CORE )* )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:282:20: ID_START ( ID_CORE )*
            {
            mID_START(); 


            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:282:29: ( ID_CORE )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( ((LA4_0 >= '0' && LA4_0 <= '9')||(LA4_0 >= 'A' && LA4_0 <= 'Z')||LA4_0=='_'||(LA4_0 >= 'a' && LA4_0 <= 'z')) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ID_PLAIN"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:284:4: ( ID_PLAIN )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:284:6: ID_PLAIN
            {
            mID_PLAIN(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ID"

    // $ANTLR start "INTEGER"
    public final void mINTEGER() throws RecognitionException {
        try {
            int _type = INTEGER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:288:8: ( ( '0' .. '9' )+ )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:288:10: ( '0' .. '9' )+
            {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:288:10: ( '0' .. '9' )+
            int cnt5=0;
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( ((LA5_0 >= '0' && LA5_0 <= '9')) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt5 >= 1 ) break loop5;
                        EarlyExitException eee =
                            new EarlyExitException(5, input);
                        throw eee;
                }
                cnt5++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "INTEGER"

    // $ANTLR start "FLOAT_EXP"
    public final void mFLOAT_EXP() throws RecognitionException {
        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:289:20: ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:289:22: ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+
            {
            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:289:32: ( '+' | '-' )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0=='+'||LA6_0=='-') ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:
                    {
                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    }
                    break;

            }


            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:289:43: ( '0' .. '9' )+
            int cnt7=0;
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( ((LA7_0 >= '0' && LA7_0 <= '9')) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt7 >= 1 ) break loop7;
                        EarlyExitException eee =
                            new EarlyExitException(7, input);
                        throw eee;
                }
                cnt7++;
            } while (true);


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "FLOAT_EXP"

    // $ANTLR start "FLOAT"
    public final void mFLOAT() throws RecognitionException {
        try {
            int _type = FLOAT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:291:5: ( ( '0' .. '9' )+ DOT ( '0' .. '9' )* ( FLOAT_EXP )? | DOT ( '0' .. '9' )+ ( FLOAT_EXP )? | ( '0' .. '9' )+ FLOAT_EXP )
            int alt14=3;
            alt14 = dfa14.predict(input);
            switch (alt14) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:291:7: ( '0' .. '9' )+ DOT ( '0' .. '9' )* ( FLOAT_EXP )?
                    {
                    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:291:7: ( '0' .. '9' )+
                    int cnt8=0;
                    loop8:
                    do {
                        int alt8=2;
                        int LA8_0 = input.LA(1);

                        if ( ((LA8_0 >= '0' && LA8_0 <= '9')) ) {
                            alt8=1;
                        }


                        switch (alt8) {
                    	case 1 :
                    	    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:
                    	    {
                    	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt8 >= 1 ) break loop8;
                                EarlyExitException eee =
                                    new EarlyExitException(8, input);
                                throw eee;
                        }
                        cnt8++;
                    } while (true);


                    mDOT(); 


                    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:291:23: ( '0' .. '9' )*
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);

                        if ( ((LA9_0 >= '0' && LA9_0 <= '9')) ) {
                            alt9=1;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:
                    	    {
                    	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop9;
                        }
                    } while (true);


                    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:291:35: ( FLOAT_EXP )?
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0=='E'||LA10_0=='e') ) {
                        alt10=1;
                    }
                    switch (alt10) {
                        case 1 :
                            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:291:35: FLOAT_EXP
                            {
                            mFLOAT_EXP(); 


                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:292:7: DOT ( '0' .. '9' )+ ( FLOAT_EXP )?
                    {
                    mDOT(); 


                    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:292:11: ( '0' .. '9' )+
                    int cnt11=0;
                    loop11:
                    do {
                        int alt11=2;
                        int LA11_0 = input.LA(1);

                        if ( ((LA11_0 >= '0' && LA11_0 <= '9')) ) {
                            alt11=1;
                        }


                        switch (alt11) {
                    	case 1 :
                    	    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:
                    	    {
                    	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt11 >= 1 ) break loop11;
                                EarlyExitException eee =
                                    new EarlyExitException(11, input);
                                throw eee;
                        }
                        cnt11++;
                    } while (true);


                    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:292:23: ( FLOAT_EXP )?
                    int alt12=2;
                    int LA12_0 = input.LA(1);

                    if ( (LA12_0=='E'||LA12_0=='e') ) {
                        alt12=1;
                    }
                    switch (alt12) {
                        case 1 :
                            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:292:23: FLOAT_EXP
                            {
                            mFLOAT_EXP(); 


                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:293:7: ( '0' .. '9' )+ FLOAT_EXP
                    {
                    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:293:7: ( '0' .. '9' )+
                    int cnt13=0;
                    loop13:
                    do {
                        int alt13=2;
                        int LA13_0 = input.LA(1);

                        if ( ((LA13_0 >= '0' && LA13_0 <= '9')) ) {
                            alt13=1;
                        }


                        switch (alt13) {
                    	case 1 :
                    	    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:
                    	    {
                    	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt13 >= 1 ) break loop13;
                                EarlyExitException eee =
                                    new EarlyExitException(13, input);
                                throw eee;
                        }
                        cnt13++;
                    } while (true);


                    mFLOAT_EXP(); 


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "FLOAT"

    // $ANTLR start "BLOB"
    public final void mBLOB() throws RecognitionException {
        try {
            int _type = BLOB;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:295:5: ( ( 'x' | 'X' ) QUOTE_SINGLE ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )+ QUOTE_SINGLE )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:295:7: ( 'x' | 'X' ) QUOTE_SINGLE ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )+ QUOTE_SINGLE
            {
            if ( input.LA(1)=='X'||input.LA(1)=='x' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            mQUOTE_SINGLE(); 


            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:295:30: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )+
            int cnt15=0;
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( ((LA15_0 >= '0' && LA15_0 <= '9')||(LA15_0 >= 'A' && LA15_0 <= 'F')||(LA15_0 >= 'a' && LA15_0 <= 'f')) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'F')||(input.LA(1) >= 'a' && input.LA(1) <= 'f') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt15 >= 1 ) break loop15;
                        EarlyExitException eee =
                            new EarlyExitException(15, input);
                        throw eee;
                }
                cnt15++;
            } while (true);


            mQUOTE_SINGLE(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "BLOB"

    // $ANTLR start "COMMENT"
    public final void mCOMMENT() throws RecognitionException {
        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:297:17: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:297:19: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); 



            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:297:24: ( options {greedy=false; } : . )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( (LA16_0=='*') ) {
                    int LA16_1 = input.LA(2);

                    if ( (LA16_1=='/') ) {
                        alt16=2;
                    }
                    else if ( ((LA16_1 >= '\u0000' && LA16_1 <= '.')||(LA16_1 >= '0' && LA16_1 <= '\uFFFF')) ) {
                        alt16=1;
                    }


                }
                else if ( ((LA16_0 >= '\u0000' && LA16_0 <= ')')||(LA16_0 >= '+' && LA16_0 <= '\uFFFF')) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:297:52: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop16;
                }
            } while (true);


            match("*/"); 



            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "COMMENT"

    // $ANTLR start "LINE_COMMENT"
    public final void mLINE_COMMENT() throws RecognitionException {
        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:298:22: ( '--' (~ ( '\\n' | '\\r' ) )* ( ( '\\r' )? '\\n' | EOF ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:298:24: '--' (~ ( '\\n' | '\\r' ) )* ( ( '\\r' )? '\\n' | EOF )
            {
            match("--"); 



            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:298:29: (~ ( '\\n' | '\\r' ) )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( ((LA17_0 >= '\u0000' && LA17_0 <= '\t')||(LA17_0 >= '\u000B' && LA17_0 <= '\f')||(LA17_0 >= '\u000E' && LA17_0 <= '\uFFFF')) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:
            	    {
            	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '\t')||(input.LA(1) >= '\u000B' && input.LA(1) <= '\f')||(input.LA(1) >= '\u000E' && input.LA(1) <= '\uFFFF') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop17;
                }
            } while (true);


            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:298:43: ( ( '\\r' )? '\\n' | EOF )
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0=='\n'||LA19_0=='\r') ) {
                alt19=1;
            }
            else {
                alt19=2;
            }
            switch (alt19) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:298:44: ( '\\r' )? '\\n'
                    {
                    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:298:44: ( '\\r' )?
                    int alt18=2;
                    int LA18_0 = input.LA(1);

                    if ( (LA18_0=='\r') ) {
                        alt18=1;
                    }
                    switch (alt18) {
                        case 1 :
                            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:298:44: '\\r'
                            {
                            match('\r'); 

                            }
                            break;

                    }


                    match('\n'); 

                    }
                    break;
                case 2 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:298:55: EOF
                    {
                    match(EOF); 


                    }
                    break;

            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LINE_COMMENT"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:300:3: ( ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' | COMMENT | LINE_COMMENT ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:300:5: ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' | COMMENT | LINE_COMMENT )
            {
            // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:300:5: ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' | COMMENT | LINE_COMMENT )
            int alt20=7;
            switch ( input.LA(1) ) {
            case ' ':
                {
                alt20=1;
                }
                break;
            case '\r':
                {
                alt20=2;
                }
                break;
            case '\t':
                {
                alt20=3;
                }
                break;
            case '\f':
                {
                alt20=4;
                }
                break;
            case '\n':
                {
                alt20=5;
                }
                break;
            case '/':
                {
                alt20=6;
                }
                break;
            case '-':
                {
                alt20=7;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 20, 0, input);

                throw nvae;

            }

            switch (alt20) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:300:6: ' '
                    {
                    match(' '); 

                    }
                    break;
                case 2 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:300:10: '\\r'
                    {
                    match('\r'); 

                    }
                    break;
                case 3 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:300:15: '\\t'
                    {
                    match('\t'); 

                    }
                    break;
                case 4 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:300:20: '\\u000C'
                    {
                    match('\f'); 

                    }
                    break;
                case 5 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:300:29: '\\n'
                    {
                    match('\n'); 

                    }
                    break;
                case 6 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:300:34: COMMENT
                    {
                    mCOMMENT(); 


                    }
                    break;
                case 7 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:300:42: LINE_COMMENT
                    {
                    mLINE_COMMENT(); 


                    }
                    break;

            }


            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "WS"

    public void mTokens() throws RecognitionException {
        // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:1:8: ( EQUALS | NOT_EQUALS2 | LESS | LESS_OR_EQ | GREATER | GREATER_OR_EQ | SEMI | COMMA | LPAREN | RPAREN | DOT | UNDERSCORE | QUESTION | QUOTE_DOUBLE | QUOTE_SINGLE | BACKSLASH | ASTERISK | LPAREN_SQUARE | RPAREN_SQUARE | AND | BETWEEN | CURRENT_TIMESTAMP | INDEXWHERE | UNIONHASH | OR | SEGHINT | WHERE | STRING | ID | INTEGER | FLOAT | BLOB | WS )
        int alt21=33;
        alt21 = dfa21.predict(input);
        switch (alt21) {
            case 1 :
                // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:1:10: EQUALS
                {
                mEQUALS(); 


                }
                break;
            case 2 :
                // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:1:17: NOT_EQUALS2
                {
                mNOT_EQUALS2(); 


                }
                break;
            case 3 :
                // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:1:29: LESS
                {
                mLESS(); 


                }
                break;
            case 4 :
                // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:1:34: LESS_OR_EQ
                {
                mLESS_OR_EQ(); 


                }
                break;
            case 5 :
                // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:1:45: GREATER
                {
                mGREATER(); 


                }
                break;
            case 6 :
                // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:1:53: GREATER_OR_EQ
                {
                mGREATER_OR_EQ(); 


                }
                break;
            case 7 :
                // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:1:67: SEMI
                {
                mSEMI(); 


                }
                break;
            case 8 :
                // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:1:72: COMMA
                {
                mCOMMA(); 


                }
                break;
            case 9 :
                // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:1:78: LPAREN
                {
                mLPAREN(); 


                }
                break;
            case 10 :
                // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:1:85: RPAREN
                {
                mRPAREN(); 


                }
                break;
            case 11 :
                // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:1:92: DOT
                {
                mDOT(); 


                }
                break;
            case 12 :
                // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:1:96: UNDERSCORE
                {
                mUNDERSCORE(); 


                }
                break;
            case 13 :
                // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:1:107: QUESTION
                {
                mQUESTION(); 


                }
                break;
            case 14 :
                // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:1:116: QUOTE_DOUBLE
                {
                mQUOTE_DOUBLE(); 


                }
                break;
            case 15 :
                // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:1:129: QUOTE_SINGLE
                {
                mQUOTE_SINGLE(); 


                }
                break;
            case 16 :
                // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:1:142: BACKSLASH
                {
                mBACKSLASH(); 


                }
                break;
            case 17 :
                // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:1:152: ASTERISK
                {
                mASTERISK(); 


                }
                break;
            case 18 :
                // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:1:161: LPAREN_SQUARE
                {
                mLPAREN_SQUARE(); 


                }
                break;
            case 19 :
                // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:1:175: RPAREN_SQUARE
                {
                mRPAREN_SQUARE(); 


                }
                break;
            case 20 :
                // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:1:189: AND
                {
                mAND(); 


                }
                break;
            case 21 :
                // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:1:193: BETWEEN
                {
                mBETWEEN(); 


                }
                break;
            case 22 :
                // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:1:201: CURRENT_TIMESTAMP
                {
                mCURRENT_TIMESTAMP(); 


                }
                break;
            case 23 :
                // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:1:219: INDEXWHERE
                {
                mINDEXWHERE(); 


                }
                break;
            case 24 :
                // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:1:230: UNIONHASH
                {
                mUNIONHASH(); 


                }
                break;
            case 25 :
                // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:1:240: OR
                {
                mOR(); 


                }
                break;
            case 26 :
                // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:1:243: SEGHINT
                {
                mSEGHINT(); 


                }
                break;
            case 27 :
                // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:1:251: WHERE
                {
                mWHERE(); 


                }
                break;
            case 28 :
                // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:1:257: STRING
                {
                mSTRING(); 


                }
                break;
            case 29 :
                // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:1:264: ID
                {
                mID(); 


                }
                break;
            case 30 :
                // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:1:267: INTEGER
                {
                mINTEGER(); 


                }
                break;
            case 31 :
                // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:1:275: FLOAT
                {
                mFLOAT(); 


                }
                break;
            case 32 :
                // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:1:281: BLOB
                {
                mBLOB(); 


                }
                break;
            case 33 :
                // E:\\workcode\\mile\\ctumile\\ctumile_killbug\\src\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\mileSqlCondition.g:1:286: WS
                {
                mWS(); 


                }
                break;

        }

    }


    protected DFA14 dfa14 = new DFA14(this);
    protected DFA21 dfa21 = new DFA21(this);
    static final String DFA14_eotS =
        "\5\uffff";
    static final String DFA14_eofS =
        "\5\uffff";
    static final String DFA14_minS =
        "\2\56\3\uffff";
    static final String DFA14_maxS =
        "\1\71\1\145\3\uffff";
    static final String DFA14_acceptS =
        "\2\uffff\1\2\1\1\1\3";
    static final String DFA14_specialS =
        "\5\uffff}>";
    static final String[] DFA14_transitionS = {
            "\1\2\1\uffff\12\1",
            "\1\3\1\uffff\12\1\13\uffff\1\4\37\uffff\1\4",
            "",
            "",
            ""
    };

    static final short[] DFA14_eot = DFA.unpackEncodedString(DFA14_eotS);
    static final short[] DFA14_eof = DFA.unpackEncodedString(DFA14_eofS);
    static final char[] DFA14_min = DFA.unpackEncodedStringToUnsignedChars(DFA14_minS);
    static final char[] DFA14_max = DFA.unpackEncodedStringToUnsignedChars(DFA14_maxS);
    static final short[] DFA14_accept = DFA.unpackEncodedString(DFA14_acceptS);
    static final short[] DFA14_special = DFA.unpackEncodedString(DFA14_specialS);
    static final short[][] DFA14_transition;

    static {
        int numStates = DFA14_transitionS.length;
        DFA14_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA14_transition[i] = DFA.unpackEncodedString(DFA14_transitionS[i]);
        }
    }

    class DFA14 extends DFA {

        public DFA14(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 14;
            this.eot = DFA14_eot;
            this.eof = DFA14_eof;
            this.min = DFA14_min;
            this.max = DFA14_max;
            this.accept = DFA14_accept;
            this.special = DFA14_special;
            this.transition = DFA14_transition;
        }
        public String getDescription() {
            return "290:1: FLOAT : ( ( '0' .. '9' )+ DOT ( '0' .. '9' )* ( FLOAT_EXP )? | DOT ( '0' .. '9' )+ ( FLOAT_EXP )? | ( '0' .. '9' )+ FLOAT_EXP );";
        }
    }
    static final String DFA21_eotS =
        "\2\uffff\1\37\1\41\4\uffff\1\42\1\44\1\uffff\1\45\1\47\4\uffff\11"+
        "\33\1\61\15\uffff\5\33\1\67\2\33\2\uffff\1\72\4\33\1\uffff\2\33"+
        "\1\uffff\13\33\1\114\5\33\1\uffff\1\122\3\33\1\126\1\uffff\3\33"+
        "\1\uffff\2\33\1\134\1\33\1\136\1\uffff\1\33\1\uffff\5\33\1\145\1"+
        "\uffff";
    static final String DFA21_eofS =
        "\146\uffff";
    static final String DFA21_minS =
        "\1\11\1\uffff\2\75\4\uffff\2\60\1\uffff\2\0\4\uffff\1\116\1\105"+
        "\1\125\2\116\1\122\1\105\1\110\1\47\1\56\15\uffff\1\104\1\124\1"+
        "\122\1\104\1\111\1\60\1\107\1\105\2\uffff\1\60\1\127\1\122\1\105"+
        "\1\117\1\uffff\1\110\1\122\1\uffff\2\105\1\130\1\116\1\111\2\105"+
        "\1\116\1\127\1\110\1\116\1\60\1\116\1\124\1\110\1\101\1\124\1\uffff"+
        "\1\60\1\137\1\105\1\123\1\60\1\uffff\1\124\1\122\1\110\1\uffff\1"+
        "\111\1\105\1\60\1\115\1\60\1\uffff\1\105\1\uffff\1\123\1\124\1\101"+
        "\1\115\1\120\1\60\1\uffff";
    static final String DFA21_maxS =
        "\1\172\1\uffff\1\76\1\75\4\uffff\1\71\1\172\1\uffff\2\uffff\4\uffff"+
        "\1\156\1\145\1\165\2\156\1\162\1\145\1\150\1\47\1\145\15\uffff\1"+
        "\144\1\164\1\162\1\144\1\151\1\172\1\147\1\145\2\uffff\1\172\1\167"+
        "\1\162\1\145\1\157\1\uffff\1\150\1\162\1\uffff\2\145\1\170\1\156"+
        "\1\151\2\145\1\156\1\167\1\150\1\156\1\172\1\156\1\164\1\150\1\141"+
        "\1\164\1\uffff\1\172\1\137\1\145\1\163\1\172\1\uffff\1\164\1\162"+
        "\1\150\1\uffff\1\151\1\145\1\172\1\155\1\172\1\uffff\1\145\1\uffff"+
        "\1\163\1\164\1\141\1\155\1\160\1\172\1\uffff";
    static final String DFA21_acceptS =
        "\1\uffff\1\1\2\uffff\1\7\1\10\1\11\1\12\2\uffff\1\15\2\uffff\1\20"+
        "\1\21\1\22\1\23\12\uffff\1\35\1\41\1\2\1\4\1\3\1\6\1\5\1\13\1\37"+
        "\1\14\1\16\1\34\1\17\10\uffff\1\40\1\36\5\uffff\1\31\2\uffff\1\24"+
        "\21\uffff\1\33\5\uffff\1\25\3\uffff\1\32\5\uffff\1\30\1\uffff\1"+
        "\27\6\uffff\1\26";
    static final String DFA21_specialS =
        "\13\uffff\1\0\1\1\131\uffff}>";
    static final String[] DFA21_transitionS = {
            "\2\34\1\uffff\2\34\22\uffff\1\34\1\uffff\1\13\4\uffff\1\14\1"+
            "\6\1\7\1\16\1\uffff\1\5\1\34\1\10\1\34\12\32\1\uffff\1\4\1\2"+
            "\1\1\1\3\1\12\1\uffff\1\21\1\22\1\23\5\33\1\24\5\33\1\26\3\33"+
            "\1\27\1\33\1\25\1\33\1\30\1\31\2\33\1\17\1\15\1\20\1\uffff\1"+
            "\11\1\uffff\1\21\1\22\1\23\5\33\1\24\5\33\1\26\3\33\1\27\1\33"+
            "\1\25\1\33\1\30\1\31\2\33",
            "",
            "\1\36\1\35",
            "\1\40",
            "",
            "",
            "",
            "",
            "\12\43",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\32\33",
            "",
            "\0\46",
            "\0\46",
            "",
            "",
            "",
            "",
            "\1\50\37\uffff\1\50",
            "\1\51\37\uffff\1\51",
            "\1\52\37\uffff\1\52",
            "\1\53\37\uffff\1\53",
            "\1\54\37\uffff\1\54",
            "\1\55\37\uffff\1\55",
            "\1\56\37\uffff\1\56",
            "\1\57\37\uffff\1\57",
            "\1\60",
            "\1\43\1\uffff\12\32\13\uffff\1\43\37\uffff\1\43",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\62\37\uffff\1\62",
            "\1\63\37\uffff\1\63",
            "\1\64\37\uffff\1\64",
            "\1\65\37\uffff\1\65",
            "\1\66\37\uffff\1\66",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\32\33",
            "\1\70\37\uffff\1\70",
            "\1\71\37\uffff\1\71",
            "",
            "",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\32\33",
            "\1\73\37\uffff\1\73",
            "\1\74\37\uffff\1\74",
            "\1\75\37\uffff\1\75",
            "\1\76\37\uffff\1\76",
            "",
            "\1\77\37\uffff\1\77",
            "\1\100\37\uffff\1\100",
            "",
            "\1\101\37\uffff\1\101",
            "\1\102\37\uffff\1\102",
            "\1\103\37\uffff\1\103",
            "\1\104\37\uffff\1\104",
            "\1\105\37\uffff\1\105",
            "\1\106\37\uffff\1\106",
            "\1\107\37\uffff\1\107",
            "\1\110\37\uffff\1\110",
            "\1\111\37\uffff\1\111",
            "\1\112\37\uffff\1\112",
            "\1\113\37\uffff\1\113",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\32\33",
            "\1\115\37\uffff\1\115",
            "\1\116\37\uffff\1\116",
            "\1\117\37\uffff\1\117",
            "\1\120\37\uffff\1\120",
            "\1\121\37\uffff\1\121",
            "",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\32\33",
            "\1\123",
            "\1\124\37\uffff\1\124",
            "\1\125\37\uffff\1\125",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\32\33",
            "",
            "\1\127\37\uffff\1\127",
            "\1\130\37\uffff\1\130",
            "\1\131\37\uffff\1\131",
            "",
            "\1\132\37\uffff\1\132",
            "\1\133\37\uffff\1\133",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\32\33",
            "\1\135\37\uffff\1\135",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\32\33",
            "",
            "\1\137\37\uffff\1\137",
            "",
            "\1\140\37\uffff\1\140",
            "\1\141\37\uffff\1\141",
            "\1\142\37\uffff\1\142",
            "\1\143\37\uffff\1\143",
            "\1\144\37\uffff\1\144",
            "\12\33\7\uffff\32\33\4\uffff\1\33\1\uffff\32\33",
            ""
    };

    static final short[] DFA21_eot = DFA.unpackEncodedString(DFA21_eotS);
    static final short[] DFA21_eof = DFA.unpackEncodedString(DFA21_eofS);
    static final char[] DFA21_min = DFA.unpackEncodedStringToUnsignedChars(DFA21_minS);
    static final char[] DFA21_max = DFA.unpackEncodedStringToUnsignedChars(DFA21_maxS);
    static final short[] DFA21_accept = DFA.unpackEncodedString(DFA21_acceptS);
    static final short[] DFA21_special = DFA.unpackEncodedString(DFA21_specialS);
    static final short[][] DFA21_transition;

    static {
        int numStates = DFA21_transitionS.length;
        DFA21_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA21_transition[i] = DFA.unpackEncodedString(DFA21_transitionS[i]);
        }
    }

    class DFA21 extends DFA {

        public DFA21(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 21;
            this.eot = DFA21_eot;
            this.eof = DFA21_eof;
            this.min = DFA21_min;
            this.max = DFA21_max;
            this.accept = DFA21_accept;
            this.special = DFA21_special;
            this.transition = DFA21_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( EQUALS | NOT_EQUALS2 | LESS | LESS_OR_EQ | GREATER | GREATER_OR_EQ | SEMI | COMMA | LPAREN | RPAREN | DOT | UNDERSCORE | QUESTION | QUOTE_DOUBLE | QUOTE_SINGLE | BACKSLASH | ASTERISK | LPAREN_SQUARE | RPAREN_SQUARE | AND | BETWEEN | CURRENT_TIMESTAMP | INDEXWHERE | UNIONHASH | OR | SEGHINT | WHERE | STRING | ID | INTEGER | FLOAT | BLOB | WS );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA21_11 = input.LA(1);

                        s = -1;
                        if ( ((LA21_11 >= '\u0000' && LA21_11 <= '\uFFFF')) ) {s = 38;}

                        else s = 37;

                        if ( s>=0 ) return s;
                        break;

                    case 1 : 
                        int LA21_12 = input.LA(1);

                        s = -1;
                        if ( ((LA21_12 >= '\u0000' && LA21_12 <= '\uFFFF')) ) {s = 38;}

                        else s = 39;

                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 21, _s, input);
            error(nvae);
            throw nvae;
        }

    }
 

}