// $ANTLR 3.4 E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g 2012-11-05 14:27:05

package com.alipay.mile.server.T2;
import com.alipay.mile.mileexception.SQLException;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class milesqlLexer extends Lexer {
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

    @Override
    public void reportError(RecognitionException e) {
      throw new SQLException("\t\n"+e.input.toString() +"\t\n"+getErrorHeader(e)+ " " + getErrorMessage(e, getTokenNames()) );
    }



    // delegates
    // delegators
    public Lexer[] getDelegates() {
        return new Lexer[] {};
    }

    public milesqlLexer() {} 
    public milesqlLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public milesqlLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);
    }
    public String getGrammarFileName() { return "E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g"; }

    // $ANTLR start "EQUALS"
    public final void mEQUALS() throws RecognitionException {
        try {
            int _type = EQUALS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:701:7: ( '=' )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:701:16: '='
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

    // $ANTLR start "NOT_EQUALS1"
    public final void mNOT_EQUALS1() throws RecognitionException {
        try {
            int _type = NOT_EQUALS1;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:702:12: ( '!=' )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:702:16: '!='
            {
            match("!="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NOT_EQUALS1"

    // $ANTLR start "NOT_EQUALS2"
    public final void mNOT_EQUALS2() throws RecognitionException {
        try {
            int _type = NOT_EQUALS2;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:703:12: ( '<>' )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:703:16: '<>'
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:704:5: ( '<' )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:704:16: '<'
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:705:11: ( '<=' )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:705:16: '<='
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:706:8: ( '>' )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:706:16: '>'
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:707:14: ( '>=' )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:707:16: '>='
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:708:5: ( ';' )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:708:16: ';'
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:709:6: ( ',' )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:709:16: ','
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:710:7: ( '(' )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:710:16: '('
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:711:7: ( ')' )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:711:16: ')'
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:712:4: ( '.' )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:712:16: '.'
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:713:11: ( '_' )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:713:16: '_'
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

    // $ANTLR start "DOLLAR"
    public final void mDOLLAR() throws RecognitionException {
        try {
            int _type = DOLLAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:714:7: ( '$' )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:714:13: '$'
            {
            match('$'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "DOLLAR"

    // $ANTLR start "QUESTION"
    public final void mQUESTION() throws RecognitionException {
        try {
            int _type = QUESTION;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:715:9: ( '?' )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:715:16: '?'
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:716:13: ( '\"' )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:716:16: '\"'
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:717:13: ( '\\'' )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:717:16: '\\''
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:718:10: ( '\\\\' )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:718:16: '\\\\'
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:719:9: ( '*' )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:719:16: '*'
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:720:14: ( '[' )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:720:16: '['
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:721:14: ( ']' )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:721:16: ']'
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:724:11: ( ( 'a' | 'A' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:725:11: ( ( 'b' | 'B' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:726:11: ( ( 'c' | 'C' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:727:11: ( ( 'd' | 'D' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:728:11: ( ( 'e' | 'E' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:729:11: ( ( 'f' | 'F' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:730:11: ( ( 'g' | 'G' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:731:11: ( ( 'h' | 'H' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:732:11: ( ( 'i' | 'I' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:733:11: ( ( 'j' | 'J' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:734:11: ( ( 'k' | 'K' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:735:11: ( ( 'l' | 'L' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:736:11: ( ( 'm' | 'M' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:737:11: ( ( 'n' | 'N' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:738:11: ( ( 'o' | 'O' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:739:11: ( ( 'p' | 'P' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:740:11: ( ( 'q' | 'Q' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:741:11: ( ( 'r' | 'R' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:742:11: ( ( 's' | 'S' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:743:11: ( ( 't' | 'T' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:744:11: ( ( 'u' | 'U' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:745:11: ( ( 'v' | 'V' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:746:11: ( ( 'w' | 'W' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:747:11: ( ( 'x' | 'X' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:748:11: ( ( 'y' | 'Y' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:749:11: ( ( 'z' | 'Z' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:
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

    // $ANTLR start "ALL"
    public final void mALL() throws RecognitionException {
        try {
            int _type = ALL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:751:4: ( A L L )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:751:6: A L L
            {
            mA(); 


            mL(); 


            mL(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ALL"

    // $ANTLR start "AND"
    public final void mAND() throws RecognitionException {
        try {
            int _type = AND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:752:4: ( A N D )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:752:6: A N D
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

    // $ANTLR start "AS"
    public final void mAS() throws RecognitionException {
        try {
            int _type = AS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:753:3: ( A S )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:753:5: A S
            {
            mA(); 


            mS(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "AS"

    // $ANTLR start "ASC"
    public final void mASC() throws RecognitionException {
        try {
            int _type = ASC;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:754:4: ( A S C )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:754:6: A S C
            {
            mA(); 


            mS(); 


            mC(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ASC"

    // $ANTLR start "AVG"
    public final void mAVG() throws RecognitionException {
        try {
            int _type = AVG;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:755:4: ( A V G )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:755:6: A V G
            {
            mA(); 


            mV(); 


            mG(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "AVG"

    // $ANTLR start "BETWEEN"
    public final void mBETWEEN() throws RecognitionException {
        try {
            int _type = BETWEEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:756:9: ( B E T W E E N )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:756:10: B E T W E E N
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

    // $ANTLR start "BY"
    public final void mBY() throws RecognitionException {
        try {
            int _type = BY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:757:3: ( B Y )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:757:5: B Y
            {
            mB(); 


            mY(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "BY"

    // $ANTLR start "COUNT"
    public final void mCOUNT() throws RecognitionException {
        try {
            int _type = COUNT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:758:6: ( C O U N T )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:758:8: C O U N T
            {
            mC(); 


            mO(); 


            mU(); 


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
    // $ANTLR end "COUNT"

    // $ANTLR start "CURRENT_TIMESTAMP"
    public final void mCURRENT_TIMESTAMP() throws RecognitionException {
        try {
            int _type = CURRENT_TIMESTAMP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:759:18: ( C U R R E N T '_' T I M E S T A M P )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:759:20: C U R R E N T '_' T I M E S T A M P
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

    // $ANTLR start "DELETE"
    public final void mDELETE() throws RecognitionException {
        try {
            int _type = DELETE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:760:7: ( D E L E T E )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:760:9: D E L E T E
            {
            mD(); 


            mE(); 


            mL(); 


            mE(); 


            mT(); 


            mE(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "DELETE"

    // $ANTLR start "DESC"
    public final void mDESC() throws RecognitionException {
        try {
            int _type = DESC;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:761:6: ( D E S C )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:761:8: D E S C
            {
            mD(); 


            mE(); 


            mS(); 


            mC(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "DESC"

    // $ANTLR start "DISTINCT"
    public final void mDISTINCT() throws RecognitionException {
        try {
            int _type = DISTINCT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:762:10: ( D I S T I N C T )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:762:12: D I S T I N C T
            {
            mD(); 


            mI(); 


            mS(); 


            mT(); 


            mI(); 


            mN(); 


            mC(); 


            mT(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "DISTINCT"

    // $ANTLR start "DOCHINT"
    public final void mDOCHINT() throws RecognitionException {
        try {
            int _type = DOCHINT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:763:9: ( D O C H I N T )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:763:11: D O C H I N T
            {
            mD(); 


            mO(); 


            mC(); 


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
    // $ANTLR end "DOCHINT"

    // $ANTLR start "FROM"
    public final void mFROM() throws RecognitionException {
        try {
            int _type = FROM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:764:5: ( F R O M )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:764:7: F R O M
            {
            mF(); 


            mR(); 


            mO(); 


            mM(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "FROM"

    // $ANTLR start "GLIMIT"
    public final void mGLIMIT() throws RecognitionException {
        try {
            int _type = GLIMIT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:765:7: ( G L I M I T )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:765:9: G L I M I T
            {
            mG(); 


            mL(); 


            mI(); 


            mM(); 


            mI(); 


            mT(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "GLIMIT"

    // $ANTLR start "GOFFSET"
    public final void mGOFFSET() throws RecognitionException {
        try {
            int _type = GOFFSET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:766:9: ( G O F F S E T )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:766:11: G O F F S E T
            {
            mG(); 


            mO(); 


            mF(); 


            mF(); 


            mS(); 


            mE(); 


            mT(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "GOFFSET"

    // $ANTLR start "GRANGE"
    public final void mGRANGE() throws RecognitionException {
        try {
            int _type = GRANGE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:767:9: ( G R A N G E )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:767:11: G R A N G E
            {
            mG(); 


            mR(); 


            mA(); 


            mN(); 


            mG(); 


            mE(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "GRANGE"

    // $ANTLR start "GROUP"
    public final void mGROUP() throws RecognitionException {
        try {
            int _type = GROUP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:768:6: ( G R O U P )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:768:8: G R O U P
            {
            mG(); 


            mR(); 


            mO(); 


            mU(); 


            mP(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "GROUP"

    // $ANTLR start "GROUPORDER"
    public final void mGROUPORDER() throws RecognitionException {
        try {
            int _type = GROUPORDER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:769:11: ( G R O U P O R D E R )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:769:13: G R O U P O R D E R
            {
            mG(); 


            mR(); 


            mO(); 


            mU(); 


            mP(); 


            mO(); 


            mR(); 


            mD(); 


            mE(); 


            mR(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "GROUPORDER"

    // $ANTLR start "HAVING"
    public final void mHAVING() throws RecognitionException {
        try {
            int _type = HAVING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:770:7: ( H A V I N G )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:770:9: H A V I N G
            {
            mH(); 


            mA(); 


            mV(); 


            mI(); 


            mN(); 


            mG(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "HAVING"

    // $ANTLR start "IN"
    public final void mIN() throws RecognitionException {
        try {
            int _type = IN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:771:3: ( I N )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:771:5: I N
            {
            mI(); 


            mN(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "IN"

    // $ANTLR start "INDEXWHERE"
    public final void mINDEXWHERE() throws RecognitionException {
        try {
            int _type = INDEXWHERE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:772:11: ( I N D E X W H E R E )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:772:13: I N D E X W H E R E
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:773:10: ( U N I O N H A S H )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:773:12: U N I O N H A S H
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

    // $ANTLR start "INSERT"
    public final void mINSERT() throws RecognitionException {
        try {
            int _type = INSERT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:774:7: ( I N S E R T )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:774:9: I N S E R T
            {
            mI(); 


            mN(); 


            mS(); 


            mE(); 


            mR(); 


            mT(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "INSERT"

    // $ANTLR start "INTERSECTION"
    public final void mINTERSECTION() throws RecognitionException {
        try {
            int _type = INTERSECTION;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:775:13: ( I N T E R S E C T I O N )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:775:15: I N T E R S E C T I O N
            {
            mI(); 


            mN(); 


            mT(); 


            mE(); 


            mR(); 


            mS(); 


            mE(); 


            mC(); 


            mT(); 


            mI(); 


            mO(); 


            mN(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "INTERSECTION"

    // $ANTLR start "INTO"
    public final void mINTO() throws RecognitionException {
        try {
            int _type = INTO;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:776:5: ( I N T O )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:776:7: I N T O
            {
            mI(); 


            mN(); 


            mT(); 


            mO(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "INTO"

    // $ANTLR start "LEFT"
    public final void mLEFT() throws RecognitionException {
        try {
            int _type = LEFT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:777:5: ( L E F T )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:777:7: L E F T
            {
            mL(); 


            mE(); 


            mF(); 


            mT(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LEFT"

    // $ANTLR start "LIMIT"
    public final void mLIMIT() throws RecognitionException {
        try {
            int _type = LIMIT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:778:6: ( L I M I T )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:778:8: L I M I T
            {
            mL(); 


            mI(); 


            mM(); 


            mI(); 


            mT(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LIMIT"

    // $ANTLR start "MATCH"
    public final void mMATCH() throws RecognitionException {
        try {
            int _type = MATCH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:779:6: ( M A T C H )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:779:8: M A T C H
            {
            mM(); 


            mA(); 


            mT(); 


            mC(); 


            mH(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "MATCH"

    // $ANTLR start "MAX"
    public final void mMAX() throws RecognitionException {
        try {
            int _type = MAX;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:780:4: ( M A X )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:780:6: M A X
            {
            mM(); 


            mA(); 


            mX(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "MAX"

    // $ANTLR start "MIN"
    public final void mMIN() throws RecognitionException {
        try {
            int _type = MIN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:781:4: ( M I N )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:781:6: M I N
            {
            mM(); 


            mI(); 


            mN(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "MIN"

    // $ANTLR start "NULL"
    public final void mNULL() throws RecognitionException {
        try {
            int _type = NULL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:782:5: ( N U L L )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:782:7: N U L L
            {
            mN(); 


            mU(); 


            mL(); 


            mL(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NULL"

    // $ANTLR start "OFFSET"
    public final void mOFFSET() throws RecognitionException {
        try {
            int _type = OFFSET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:783:7: ( O F F S E T )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:783:9: O F F S E T
            {
            mO(); 


            mF(); 


            mF(); 


            mS(); 


            mE(); 


            mT(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "OFFSET"

    // $ANTLR start "OR"
    public final void mOR() throws RecognitionException {
        try {
            int _type = OR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:784:3: ( O R )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:784:5: O R
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

    // $ANTLR start "ORDER"
    public final void mORDER() throws RecognitionException {
        try {
            int _type = ORDER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:785:6: ( O R D E R )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:785:8: O R D E R
            {
            mO(); 


            mR(); 


            mD(); 


            mE(); 


            mR(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ORDER"

    // $ANTLR start "SEGHINT"
    public final void mSEGHINT() throws RecognitionException {
        try {
            int _type = SEGHINT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:786:9: ( S E G H I N T )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:786:10: S E G H I N T
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

    // $ANTLR start "SELECT"
    public final void mSELECT() throws RecognitionException {
        try {
            int _type = SELECT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:787:7: ( S E L E C T )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:787:9: S E L E C T
            {
            mS(); 


            mE(); 


            mL(); 


            mE(); 


            mC(); 


            mT(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SELECT"

    // $ANTLR start "SET"
    public final void mSET() throws RecognitionException {
        try {
            int _type = SET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:788:4: ( S E T )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:788:6: S E T
            {
            mS(); 


            mE(); 


            mT(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SET"

    // $ANTLR start "SQUARESUM"
    public final void mSQUARESUM() throws RecognitionException {
        try {
            int _type = SQUARESUM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:789:10: ( S Q U A R E S U M )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:789:12: S Q U A R E S U M
            {
            mS(); 


            mQ(); 


            mU(); 


            mA(); 


            mR(); 


            mE(); 


            mS(); 


            mU(); 


            mM(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SQUARESUM"

    // $ANTLR start "SUM"
    public final void mSUM() throws RecognitionException {
        try {
            int _type = SUM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:790:4: ( S U M )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:790:6: S U M
            {
            mS(); 


            mU(); 


            mM(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SUM"

    // $ANTLR start "STDDEV"
    public final void mSTDDEV() throws RecognitionException {
        try {
            int _type = STDDEV;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:791:7: ( S T D D E V )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:791:9: S T D D E V
            {
            mS(); 


            mT(); 


            mD(); 


            mD(); 


            mE(); 


            mV(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "STDDEV"

    // $ANTLR start "UNIONS"
    public final void mUNIONS() throws RecognitionException {
        try {
            int _type = UNIONS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:792:7: ( U N I O N S )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:792:9: U N I O N S
            {
            mU(); 


            mN(); 


            mI(); 


            mO(); 


            mN(); 


            mS(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "UNIONS"

    // $ANTLR start "UPDATE"
    public final void mUPDATE() throws RecognitionException {
        try {
            int _type = UPDATE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:793:7: ( U P D A T E )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:793:9: U P D A T E
            {
            mU(); 


            mP(); 


            mD(); 


            mA(); 


            mT(); 


            mE(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "UPDATE"

    // $ANTLR start "VARIANCE"
    public final void mVARIANCE() throws RecognitionException {
        try {
            int _type = VARIANCE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:794:9: ( V A R I A N C E )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:794:11: V A R I A N C E
            {
            mV(); 


            mA(); 


            mR(); 


            mI(); 


            mA(); 


            mN(); 


            mC(); 


            mE(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "VARIANCE"

    // $ANTLR start "WHERE"
    public final void mWHERE() throws RecognitionException {
        try {
            int _type = WHERE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:795:6: ( W H E R E )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:795:8: W H E R E
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

    // $ANTLR start "WITH"
    public final void mWITH() throws RecognitionException {
        try {
            int _type = WITH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:796:5: ( W I T H )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:796:7: W I T H
            {
            mW(); 


            mI(); 


            mT(); 


            mH(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "WITH"

    // $ANTLR start "WITHIN"
    public final void mWITHIN() throws RecognitionException {
        try {
            int _type = WITHIN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:797:7: ( W I T H I N )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:797:9: W I T H I N
            {
            mW(); 


            mI(); 


            mT(); 


            mH(); 


            mI(); 


            mN(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "WITHIN"

    // $ANTLR start "WORDSEG"
    public final void mWORDSEG() throws RecognitionException {
        try {
            int _type = WORDSEG;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:798:8: ( W O R D S E G )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:798:10: W O R D S E G
            {
            mW(); 


            mO(); 


            mR(); 


            mD(); 


            mS(); 


            mE(); 


            mG(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "WORDSEG"

    // $ANTLR start "TO"
    public final void mTO() throws RecognitionException {
        try {
            int _type = TO;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:799:3: ( T O )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:799:5: T O
            {
            mT(); 


            mO(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "TO"

    // $ANTLR start "EXPORT"
    public final void mEXPORT() throws RecognitionException {
        try {
            int _type = EXPORT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:800:7: ( E X P O R T )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:800:9: E X P O R T
            {
            mE(); 


            mX(); 


            mP(); 


            mO(); 


            mR(); 


            mT(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "EXPORT"

    // $ANTLR start "STRING_ESCAPE_SINGLE"
    public final void mSTRING_ESCAPE_SINGLE() throws RecognitionException {
        try {
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:809:30: ( ( BACKSLASH QUOTE_SINGLE ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:809:32: ( BACKSLASH QUOTE_SINGLE )
            {
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:809:32: ( BACKSLASH QUOTE_SINGLE )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:809:33: BACKSLASH QUOTE_SINGLE
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:810:30: ( ( BACKSLASH QUOTE_DOUBLE ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:810:32: ( BACKSLASH QUOTE_DOUBLE )
            {
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:810:32: ( BACKSLASH QUOTE_DOUBLE )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:810:33: BACKSLASH QUOTE_DOUBLE
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:811:21: (~ ( QUOTE_SINGLE | QUOTE_DOUBLE ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:812:28: ( ( STRING_CORE | QUOTE_DOUBLE | STRING_ESCAPE_SINGLE )* )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:812:30: ( STRING_CORE | QUOTE_DOUBLE | STRING_ESCAPE_SINGLE )*
            {
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:812:30: ( STRING_CORE | QUOTE_DOUBLE | STRING_ESCAPE_SINGLE )*
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
            	    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:812:32: STRING_CORE
            	    {
            	    mSTRING_CORE(); 


            	    }
            	    break;
            	case 2 :
            	    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:812:46: QUOTE_DOUBLE
            	    {
            	    mQUOTE_DOUBLE(); 


            	    }
            	    break;
            	case 3 :
            	    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:812:61: STRING_ESCAPE_SINGLE
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:813:28: ( ( STRING_CORE | QUOTE_SINGLE | STRING_ESCAPE_DOUBLE )* )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:813:30: ( STRING_CORE | QUOTE_SINGLE | STRING_ESCAPE_DOUBLE )*
            {
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:813:30: ( STRING_CORE | QUOTE_SINGLE | STRING_ESCAPE_DOUBLE )*
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
            	    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:813:32: STRING_CORE
            	    {
            	    mSTRING_CORE(); 


            	    }
            	    break;
            	case 2 :
            	    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:813:46: QUOTE_SINGLE
            	    {
            	    mQUOTE_SINGLE(); 


            	    }
            	    break;
            	case 3 :
            	    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:813:61: STRING_ESCAPE_DOUBLE
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:814:23: ( ( QUOTE_SINGLE STRING_CORE_SINGLE QUOTE_SINGLE ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:814:25: ( QUOTE_SINGLE STRING_CORE_SINGLE QUOTE_SINGLE )
            {
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:814:25: ( QUOTE_SINGLE STRING_CORE_SINGLE QUOTE_SINGLE )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:814:26: QUOTE_SINGLE STRING_CORE_SINGLE QUOTE_SINGLE
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:815:23: ( ( QUOTE_DOUBLE STRING_CORE_DOUBLE QUOTE_DOUBLE ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:815:25: ( QUOTE_DOUBLE STRING_CORE_DOUBLE QUOTE_DOUBLE )
            {
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:815:25: ( QUOTE_DOUBLE STRING_CORE_DOUBLE QUOTE_DOUBLE )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:815:26: QUOTE_DOUBLE STRING_CORE_DOUBLE QUOTE_DOUBLE
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:816:7: ( ( STRING_SINGLE | STRING_DOUBLE ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:816:9: ( STRING_SINGLE | STRING_DOUBLE )
            {
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:816:9: ( STRING_SINGLE | STRING_DOUBLE )
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
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:816:10: STRING_SINGLE
                    {
                    mSTRING_SINGLE(); 


                    }
                    break;
                case 2 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:816:26: STRING_DOUBLE
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:818:18: ( ( 'a' .. 'z' | 'A' .. 'Z' | UNDERSCORE | DOLLAR ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:
            {
            if ( input.LA(1)=='$'||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:819:17: ( ( ID_START | '0' .. '9' ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:
            {
            if ( input.LA(1)=='$'||(input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:820:18: ( ID_START ( ID_CORE )* )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:820:20: ID_START ( ID_CORE )*
            {
            mID_START(); 


            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:820:29: ( ID_CORE )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0=='$'||(LA4_0 >= '0' && LA4_0 <= '9')||(LA4_0 >= 'A' && LA4_0 <= 'Z')||LA4_0=='_'||(LA4_0 >= 'a' && LA4_0 <= 'z')) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:
            	    {
            	    if ( input.LA(1)=='$'||(input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:822:3: ( ID_PLAIN )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:822:5: ID_PLAIN
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:826:8: ( ( '0' .. '9' )+ )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:826:10: ( '0' .. '9' )+
            {
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:826:10: ( '0' .. '9' )+
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
            	    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:827:20: ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:827:22: ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+
            {
            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:827:32: ( '+' | '-' )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0=='+'||LA6_0=='-') ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:
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


            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:827:43: ( '0' .. '9' )+
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
            	    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:829:5: ( ( '0' .. '9' )+ DOT ( '0' .. '9' )* ( FLOAT_EXP )? | DOT ( '0' .. '9' )+ ( FLOAT_EXP )? | ( '0' .. '9' )+ FLOAT_EXP )
            int alt14=3;
            alt14 = dfa14.predict(input);
            switch (alt14) {
                case 1 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:829:9: ( '0' .. '9' )+ DOT ( '0' .. '9' )* ( FLOAT_EXP )?
                    {
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:829:9: ( '0' .. '9' )+
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
                    	    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:
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


                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:829:25: ( '0' .. '9' )*
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);

                        if ( ((LA9_0 >= '0' && LA9_0 <= '9')) ) {
                            alt9=1;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:
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


                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:829:37: ( FLOAT_EXP )?
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0=='E'||LA10_0=='e') ) {
                        alt10=1;
                    }
                    switch (alt10) {
                        case 1 :
                            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:829:37: FLOAT_EXP
                            {
                            mFLOAT_EXP(); 


                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:830:9: DOT ( '0' .. '9' )+ ( FLOAT_EXP )?
                    {
                    mDOT(); 


                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:830:13: ( '0' .. '9' )+
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
                    	    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:
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


                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:830:25: ( FLOAT_EXP )?
                    int alt12=2;
                    int LA12_0 = input.LA(1);

                    if ( (LA12_0=='E'||LA12_0=='e') ) {
                        alt12=1;
                    }
                    switch (alt12) {
                        case 1 :
                            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:830:25: FLOAT_EXP
                            {
                            mFLOAT_EXP(); 


                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:831:9: ( '0' .. '9' )+ FLOAT_EXP
                    {
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:831:9: ( '0' .. '9' )+
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
                    	    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:833:5: ( ( 'x' | 'X' ) QUOTE_SINGLE ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )+ QUOTE_SINGLE )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:833:7: ( 'x' | 'X' ) QUOTE_SINGLE ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )+ QUOTE_SINGLE
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


            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:833:30: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )+
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
            	    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:835:17: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:835:19: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); 



            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:835:24: ( options {greedy=false; } : . )*
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
            	    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:835:52: .
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:836:22: ( '--' (~ ( '\\n' | '\\r' ) )* ( ( '\\r' )? '\\n' | EOF ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:836:24: '--' (~ ( '\\n' | '\\r' ) )* ( ( '\\r' )? '\\n' | EOF )
            {
            match("--"); 



            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:836:29: (~ ( '\\n' | '\\r' ) )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( ((LA17_0 >= '\u0000' && LA17_0 <= '\t')||(LA17_0 >= '\u000B' && LA17_0 <= '\f')||(LA17_0 >= '\u000E' && LA17_0 <= '\uFFFF')) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:
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


            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:836:43: ( ( '\\r' )? '\\n' | EOF )
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
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:836:44: ( '\\r' )? '\\n'
                    {
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:836:44: ( '\\r' )?
                    int alt18=2;
                    int LA18_0 = input.LA(1);

                    if ( (LA18_0=='\r') ) {
                        alt18=1;
                    }
                    switch (alt18) {
                        case 1 :
                            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:836:44: '\\r'
                            {
                            match('\r'); 

                            }
                            break;

                    }


                    match('\n'); 

                    }
                    break;
                case 2 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:836:55: EOF
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
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:837:3: ( ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' | COMMENT | LINE_COMMENT ) )
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:837:5: ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' | COMMENT | LINE_COMMENT )
            {
            // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:837:5: ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' | COMMENT | LINE_COMMENT )
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
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:837:6: ' '
                    {
                    match(' '); 

                    }
                    break;
                case 2 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:837:10: '\\r'
                    {
                    match('\r'); 

                    }
                    break;
                case 3 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:837:15: '\\t'
                    {
                    match('\t'); 

                    }
                    break;
                case 4 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:837:20: '\\u000C'
                    {
                    match('\f'); 

                    }
                    break;
                case 5 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:837:29: '\\n'
                    {
                    match('\n'); 

                    }
                    break;
                case 6 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:837:34: COMMENT
                    {
                    mCOMMENT(); 


                    }
                    break;
                case 7 :
                    // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:837:42: LINE_COMMENT
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
        // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:8: ( EQUALS | NOT_EQUALS1 | NOT_EQUALS2 | LESS | LESS_OR_EQ | GREATER | GREATER_OR_EQ | SEMI | COMMA | LPAREN | RPAREN | DOT | UNDERSCORE | DOLLAR | QUESTION | QUOTE_DOUBLE | QUOTE_SINGLE | BACKSLASH | ASTERISK | LPAREN_SQUARE | RPAREN_SQUARE | ALL | AND | AS | ASC | AVG | BETWEEN | BY | COUNT | CURRENT_TIMESTAMP | DELETE | DESC | DISTINCT | DOCHINT | FROM | GLIMIT | GOFFSET | GRANGE | GROUP | GROUPORDER | HAVING | IN | INDEXWHERE | UNIONHASH | INSERT | INTERSECTION | INTO | LEFT | LIMIT | MATCH | MAX | MIN | NULL | OFFSET | OR | ORDER | SEGHINT | SELECT | SET | SQUARESUM | SUM | STDDEV | UNIONS | UPDATE | VARIANCE | WHERE | WITH | WITHIN | WORDSEG | TO | EXPORT | STRING | ID | INTEGER | FLOAT | BLOB | WS )
        int alt21=77;
        alt21 = dfa21.predict(input);
        switch (alt21) {
            case 1 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:10: EQUALS
                {
                mEQUALS(); 


                }
                break;
            case 2 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:17: NOT_EQUALS1
                {
                mNOT_EQUALS1(); 


                }
                break;
            case 3 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:29: NOT_EQUALS2
                {
                mNOT_EQUALS2(); 


                }
                break;
            case 4 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:41: LESS
                {
                mLESS(); 


                }
                break;
            case 5 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:46: LESS_OR_EQ
                {
                mLESS_OR_EQ(); 


                }
                break;
            case 6 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:57: GREATER
                {
                mGREATER(); 


                }
                break;
            case 7 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:65: GREATER_OR_EQ
                {
                mGREATER_OR_EQ(); 


                }
                break;
            case 8 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:79: SEMI
                {
                mSEMI(); 


                }
                break;
            case 9 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:84: COMMA
                {
                mCOMMA(); 


                }
                break;
            case 10 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:90: LPAREN
                {
                mLPAREN(); 


                }
                break;
            case 11 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:97: RPAREN
                {
                mRPAREN(); 


                }
                break;
            case 12 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:104: DOT
                {
                mDOT(); 


                }
                break;
            case 13 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:108: UNDERSCORE
                {
                mUNDERSCORE(); 


                }
                break;
            case 14 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:119: DOLLAR
                {
                mDOLLAR(); 


                }
                break;
            case 15 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:126: QUESTION
                {
                mQUESTION(); 


                }
                break;
            case 16 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:135: QUOTE_DOUBLE
                {
                mQUOTE_DOUBLE(); 


                }
                break;
            case 17 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:148: QUOTE_SINGLE
                {
                mQUOTE_SINGLE(); 


                }
                break;
            case 18 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:161: BACKSLASH
                {
                mBACKSLASH(); 


                }
                break;
            case 19 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:171: ASTERISK
                {
                mASTERISK(); 


                }
                break;
            case 20 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:180: LPAREN_SQUARE
                {
                mLPAREN_SQUARE(); 


                }
                break;
            case 21 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:194: RPAREN_SQUARE
                {
                mRPAREN_SQUARE(); 


                }
                break;
            case 22 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:208: ALL
                {
                mALL(); 


                }
                break;
            case 23 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:212: AND
                {
                mAND(); 


                }
                break;
            case 24 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:216: AS
                {
                mAS(); 


                }
                break;
            case 25 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:219: ASC
                {
                mASC(); 


                }
                break;
            case 26 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:223: AVG
                {
                mAVG(); 


                }
                break;
            case 27 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:227: BETWEEN
                {
                mBETWEEN(); 


                }
                break;
            case 28 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:235: BY
                {
                mBY(); 


                }
                break;
            case 29 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:238: COUNT
                {
                mCOUNT(); 


                }
                break;
            case 30 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:244: CURRENT_TIMESTAMP
                {
                mCURRENT_TIMESTAMP(); 


                }
                break;
            case 31 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:262: DELETE
                {
                mDELETE(); 


                }
                break;
            case 32 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:269: DESC
                {
                mDESC(); 


                }
                break;
            case 33 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:274: DISTINCT
                {
                mDISTINCT(); 


                }
                break;
            case 34 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:283: DOCHINT
                {
                mDOCHINT(); 


                }
                break;
            case 35 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:291: FROM
                {
                mFROM(); 


                }
                break;
            case 36 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:296: GLIMIT
                {
                mGLIMIT(); 


                }
                break;
            case 37 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:303: GOFFSET
                {
                mGOFFSET(); 


                }
                break;
            case 38 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:311: GRANGE
                {
                mGRANGE(); 


                }
                break;
            case 39 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:318: GROUP
                {
                mGROUP(); 


                }
                break;
            case 40 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:324: GROUPORDER
                {
                mGROUPORDER(); 


                }
                break;
            case 41 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:335: HAVING
                {
                mHAVING(); 


                }
                break;
            case 42 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:342: IN
                {
                mIN(); 


                }
                break;
            case 43 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:345: INDEXWHERE
                {
                mINDEXWHERE(); 


                }
                break;
            case 44 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:356: UNIONHASH
                {
                mUNIONHASH(); 


                }
                break;
            case 45 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:366: INSERT
                {
                mINSERT(); 


                }
                break;
            case 46 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:373: INTERSECTION
                {
                mINTERSECTION(); 


                }
                break;
            case 47 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:386: INTO
                {
                mINTO(); 


                }
                break;
            case 48 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:391: LEFT
                {
                mLEFT(); 


                }
                break;
            case 49 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:396: LIMIT
                {
                mLIMIT(); 


                }
                break;
            case 50 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:402: MATCH
                {
                mMATCH(); 


                }
                break;
            case 51 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:408: MAX
                {
                mMAX(); 


                }
                break;
            case 52 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:412: MIN
                {
                mMIN(); 


                }
                break;
            case 53 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:416: NULL
                {
                mNULL(); 


                }
                break;
            case 54 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:421: OFFSET
                {
                mOFFSET(); 


                }
                break;
            case 55 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:428: OR
                {
                mOR(); 


                }
                break;
            case 56 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:431: ORDER
                {
                mORDER(); 


                }
                break;
            case 57 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:437: SEGHINT
                {
                mSEGHINT(); 


                }
                break;
            case 58 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:445: SELECT
                {
                mSELECT(); 


                }
                break;
            case 59 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:452: SET
                {
                mSET(); 


                }
                break;
            case 60 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:456: SQUARESUM
                {
                mSQUARESUM(); 


                }
                break;
            case 61 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:466: SUM
                {
                mSUM(); 


                }
                break;
            case 62 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:470: STDDEV
                {
                mSTDDEV(); 


                }
                break;
            case 63 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:477: UNIONS
                {
                mUNIONS(); 


                }
                break;
            case 64 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:484: UPDATE
                {
                mUPDATE(); 


                }
                break;
            case 65 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:491: VARIANCE
                {
                mVARIANCE(); 


                }
                break;
            case 66 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:500: WHERE
                {
                mWHERE(); 


                }
                break;
            case 67 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:506: WITH
                {
                mWITH(); 


                }
                break;
            case 68 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:511: WITHIN
                {
                mWITHIN(); 


                }
                break;
            case 69 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:518: WORDSEG
                {
                mWORDSEG(); 


                }
                break;
            case 70 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:526: TO
                {
                mTO(); 


                }
                break;
            case 71 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:529: EXPORT
                {
                mEXPORT(); 


                }
                break;
            case 72 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:536: STRING
                {
                mSTRING(); 


                }
                break;
            case 73 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:543: ID
                {
                mID(); 


                }
                break;
            case 74 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:546: INTEGER
                {
                mINTEGER(); 


                }
                break;
            case 75 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:554: FLOAT
                {
                mFLOAT(); 


                }
                break;
            case 76 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:560: BLOB
                {
                mBLOB(); 


                }
                break;
            case 77 :
                // E:\\workcode\\mile\\ctumile\\ctumile_v6\\mergeserver\\server\\src\\main\\java\\com\\alipay\\mile\\server\\T2\\milesql.g:1:565: WS
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
            return "828:1: FLOAT : ( ( '0' .. '9' )+ DOT ( '0' .. '9' )* ( FLOAT_EXP )? | DOT ( '0' .. '9' )+ ( FLOAT_EXP )? | ( '0' .. '9' )+ FLOAT_EXP );";
        }
    }
    static final String DFA21_eotS =
        "\3\uffff\1\53\1\55\4\uffff\1\56\1\60\1\61\1\uffff\1\62\1\64\4\uffff"+
        "\23\47\1\132\16\uffff\2\47\1\135\2\47\1\141\12\47\1\156\10\47\1"+
        "\173\10\47\1\u0087\1\47\2\uffff\1\u0089\1\u008a\1\uffff\1\u008b"+
        "\1\u008c\1\47\1\uffff\14\47\1\uffff\10\47\1\u00a3\1\u00a4\2\47\1"+
        "\uffff\3\47\1\u00aa\1\47\1\u00ac\5\47\1\uffff\1\47\4\uffff\4\47"+
        "\1\u00b7\2\47\1\u00ba\10\47\1\u00c3\2\47\1\u00c6\2\47\2\uffff\1"+
        "\u00c9\4\47\1\uffff\1\47\1\uffff\3\47\1\u00d2\3\47\1\u00d7\2\47"+
        "\1\uffff\2\47\1\uffff\3\47\1\u00df\4\47\1\uffff\2\47\1\uffff\1\u00e8"+
        "\1\u00e9\1\uffff\1\47\1\u00eb\5\47\1\u00f1\1\uffff\4\47\1\uffff"+
        "\1\47\1\u00f7\2\47\1\u00fa\1\47\1\u00fc\1\uffff\1\47\1\u00fe\1\47"+
        "\1\u0100\2\47\1\u0103\1\u0104\2\uffff\1\u0105\1\uffff\1\47\1\u0107"+
        "\1\47\1\u0109\1\47\1\uffff\1\u010b\1\47\1\u010d\1\u010e\1\47\1\uffff"+
        "\1\47\1\u0111\1\uffff\1\u0112\1\uffff\1\47\1\uffff\1\47\1\uffff"+
        "\2\47\3\uffff\1\u0117\1\uffff\1\47\1\uffff\1\47\1\uffff\1\u011a"+
        "\2\uffff\1\47\1\u011c\2\uffff\4\47\1\uffff\1\47\1\u0122\1\uffff"+
        "\1\47\1\uffff\3\47\1\u0127\1\u0128\1\uffff\1\47\1\u012a\1\u012b"+
        "\1\47\2\uffff\1\47\2\uffff\2\47\1\u0130\1\47\1\uffff\3\47\1\u0135"+
        "\1\uffff";
    static final String DFA21_eofS =
        "\u0136\uffff";
    static final String DFA21_minS =
        "\1\11\2\uffff\2\75\4\uffff\1\60\2\44\1\uffff\2\0\4\uffff\1\114\1"+
        "\105\1\117\1\105\1\122\1\114\1\101\2\116\1\105\1\101\1\125\1\106"+
        "\1\105\1\101\1\110\1\117\1\130\1\47\1\56\16\uffff\1\114\1\104\1"+
        "\44\1\107\1\124\1\44\1\125\1\122\1\114\1\123\1\103\1\117\1\111\1"+
        "\106\1\101\1\126\1\44\1\111\1\104\1\106\1\115\1\124\1\116\1\114"+
        "\1\106\1\44\1\107\1\125\1\115\1\104\1\122\1\105\1\124\1\122\1\44"+
        "\1\120\2\uffff\2\44\1\uffff\2\44\1\127\1\uffff\1\116\1\122\1\105"+
        "\1\103\1\124\1\110\2\115\1\106\1\116\1\125\1\111\1\uffff\3\105\1"+
        "\117\1\101\1\124\1\111\1\103\2\44\1\114\1\123\1\uffff\1\105\1\110"+
        "\1\105\1\44\1\101\1\44\1\104\1\111\1\122\1\110\1\104\1\uffff\1\117"+
        "\4\uffff\1\105\1\124\1\105\1\124\1\44\2\111\1\44\1\111\1\123\1\107"+
        "\1\120\1\116\1\130\2\122\1\44\1\116\1\124\1\44\1\124\1\110\2\uffff"+
        "\1\44\1\105\1\122\1\111\1\103\1\uffff\1\122\1\uffff\1\105\1\101"+
        "\1\105\1\44\1\123\1\122\1\105\1\44\1\116\1\105\1\uffff\2\116\1\uffff"+
        "\1\124\2\105\1\44\1\107\1\127\1\124\1\123\1\uffff\1\110\1\105\1"+
        "\uffff\2\44\1\uffff\1\124\1\44\1\116\1\124\1\105\1\126\1\116\1\44"+
        "\1\uffff\1\116\1\105\1\124\1\116\1\uffff\1\124\1\44\1\103\1\124"+
        "\1\44\1\124\1\44\1\uffff\1\122\1\44\1\110\1\44\1\105\1\101\2\44"+
        "\2\uffff\1\44\1\uffff\1\124\1\44\1\123\1\44\1\103\1\uffff\1\44\1"+
        "\107\2\44\1\137\1\uffff\1\124\1\44\1\uffff\1\44\1\uffff\1\104\1"+
        "\uffff\1\105\1\uffff\1\103\1\123\3\uffff\1\44\1\uffff\1\125\1\uffff"+
        "\1\105\1\uffff\1\44\2\uffff\1\124\1\44\2\uffff\1\105\1\122\1\124"+
        "\1\110\1\uffff\1\115\1\44\1\uffff\1\111\1\uffff\1\122\1\105\1\111"+
        "\2\44\1\uffff\1\115\2\44\1\117\2\uffff\1\105\2\uffff\1\116\1\123"+
        "\1\44\1\124\1\uffff\1\101\1\115\1\120\1\44\1\uffff";
    static final String DFA21_maxS =
        "\1\172\2\uffff\1\76\1\75\4\uffff\1\71\2\172\1\uffff\2\uffff\4\uffff"+
        "\1\166\1\171\1\165\1\157\2\162\1\141\1\156\1\160\2\151\1\165\1\162"+
        "\1\165\1\141\2\157\1\170\1\47\1\145\16\uffff\1\154\1\144\1\172\1"+
        "\147\1\164\1\172\1\165\1\162\2\163\1\143\1\157\1\151\1\146\1\157"+
        "\1\166\1\172\1\151\1\144\1\146\1\155\1\170\1\156\1\154\1\146\1\172"+
        "\1\164\1\165\1\155\1\144\1\162\1\145\1\164\1\162\1\172\1\160\2\uffff"+
        "\2\172\1\uffff\2\172\1\167\1\uffff\1\156\1\162\1\145\1\143\1\164"+
        "\1\150\2\155\1\146\1\156\1\165\1\151\1\uffff\2\145\2\157\1\141\1"+
        "\164\1\151\1\143\2\172\1\154\1\163\1\uffff\1\145\1\150\1\145\1\172"+
        "\1\141\1\172\1\144\1\151\1\162\1\150\1\144\1\uffff\1\157\4\uffff"+
        "\1\145\1\164\1\145\1\164\1\172\2\151\1\172\1\151\1\163\1\147\1\160"+
        "\1\156\1\170\2\162\1\172\1\156\1\164\1\172\1\164\1\150\2\uffff\1"+
        "\172\1\145\1\162\1\151\1\143\1\uffff\1\162\1\uffff\1\145\1\141\1"+
        "\145\1\172\1\163\1\162\1\145\1\172\1\156\1\145\1\uffff\2\156\1\uffff"+
        "\1\164\2\145\1\172\1\147\1\167\1\164\1\163\1\uffff\1\163\1\145\1"+
        "\uffff\2\172\1\uffff\1\164\1\172\1\156\1\164\1\145\1\166\1\156\1"+
        "\172\1\uffff\1\156\1\145\1\164\1\156\1\uffff\1\164\1\172\1\143\1"+
        "\164\1\172\1\164\1\172\1\uffff\1\162\1\172\1\150\1\172\1\145\1\141"+
        "\2\172\2\uffff\1\172\1\uffff\1\164\1\172\1\163\1\172\1\143\1\uffff"+
        "\1\172\1\147\2\172\1\137\1\uffff\1\164\1\172\1\uffff\1\172\1\uffff"+
        "\1\144\1\uffff\1\145\1\uffff\1\143\1\163\3\uffff\1\172\1\uffff\1"+
        "\165\1\uffff\1\145\1\uffff\1\172\2\uffff\1\164\1\172\2\uffff\1\145"+
        "\1\162\1\164\1\150\1\uffff\1\155\1\172\1\uffff\1\151\1\uffff\1\162"+
        "\1\145\1\151\2\172\1\uffff\1\155\2\172\1\157\2\uffff\1\145\2\uffff"+
        "\1\156\1\163\1\172\1\164\1\uffff\1\141\1\155\1\160\1\172\1\uffff";
    static final String DFA21_acceptS =
        "\1\uffff\1\1\1\2\2\uffff\1\10\1\11\1\12\1\13\3\uffff\1\17\2\uffff"+
        "\1\22\1\23\1\24\1\25\24\uffff\1\111\1\115\1\3\1\5\1\4\1\7\1\6\1"+
        "\14\1\113\1\15\1\16\1\20\1\110\1\21\44\uffff\1\114\1\112\2\uffff"+
        "\1\30\3\uffff\1\34\14\uffff\1\52\14\uffff\1\67\13\uffff\1\106\1"+
        "\uffff\1\26\1\27\1\31\1\32\26\uffff\1\63\1\64\5\uffff\1\73\1\uffff"+
        "\1\75\12\uffff\1\40\2\uffff\1\43\10\uffff\1\57\2\uffff\1\60\2\uffff"+
        "\1\65\10\uffff\1\103\4\uffff\1\35\7\uffff\1\47\10\uffff\1\61\1\62"+
        "\1\uffff\1\70\5\uffff\1\102\5\uffff\1\37\2\uffff\1\44\1\uffff\1"+
        "\46\1\uffff\1\51\1\uffff\1\55\2\uffff\1\77\1\100\1\66\1\uffff\1"+
        "\72\1\uffff\1\76\1\uffff\1\104\1\uffff\1\107\1\33\2\uffff\1\42\1"+
        "\45\4\uffff\1\71\2\uffff\1\105\1\uffff\1\41\5\uffff\1\101\4\uffff"+
        "\1\54\1\74\1\uffff\1\50\1\53\4\uffff\1\56\4\uffff\1\36";
    static final String DFA21_specialS =
        "\15\uffff\1\0\1\1\u0127\uffff}>";
    static final String[] DFA21_transitionS = {
            "\2\50\1\uffff\2\50\22\uffff\1\50\1\2\1\15\1\uffff\1\13\2\uffff"+
            "\1\16\1\7\1\10\1\20\1\uffff\1\6\1\50\1\11\1\50\12\46\1\uffff"+
            "\1\5\1\3\1\1\1\4\1\14\1\uffff\1\23\1\24\1\25\1\26\1\44\1\27"+
            "\1\30\1\31\1\32\2\47\1\34\1\35\1\36\1\37\3\47\1\40\1\43\1\33"+
            "\1\41\1\42\1\45\2\47\1\21\1\17\1\22\1\uffff\1\12\1\uffff\1\23"+
            "\1\24\1\25\1\26\1\44\1\27\1\30\1\31\1\32\2\47\1\34\1\35\1\36"+
            "\1\37\3\47\1\40\1\43\1\33\1\41\1\42\1\45\2\47",
            "",
            "",
            "\1\52\1\51",
            "\1\54",
            "",
            "",
            "",
            "",
            "\12\57",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "",
            "\0\63",
            "\0\63",
            "",
            "",
            "",
            "",
            "\1\65\1\uffff\1\66\4\uffff\1\67\2\uffff\1\70\25\uffff\1\65"+
            "\1\uffff\1\66\4\uffff\1\67\2\uffff\1\70",
            "\1\71\23\uffff\1\72\13\uffff\1\71\23\uffff\1\72",
            "\1\73\5\uffff\1\74\31\uffff\1\73\5\uffff\1\74",
            "\1\75\3\uffff\1\76\5\uffff\1\77\25\uffff\1\75\3\uffff\1\76"+
            "\5\uffff\1\77",
            "\1\100\37\uffff\1\100",
            "\1\101\2\uffff\1\102\2\uffff\1\103\31\uffff\1\101\2\uffff\1"+
            "\102\2\uffff\1\103",
            "\1\104\37\uffff\1\104",
            "\1\105\37\uffff\1\105",
            "\1\106\1\uffff\1\107\35\uffff\1\106\1\uffff\1\107",
            "\1\110\3\uffff\1\111\33\uffff\1\110\3\uffff\1\111",
            "\1\112\7\uffff\1\113\27\uffff\1\112\7\uffff\1\113",
            "\1\114\37\uffff\1\114",
            "\1\115\13\uffff\1\116\23\uffff\1\115\13\uffff\1\116",
            "\1\117\13\uffff\1\120\2\uffff\1\122\1\121\17\uffff\1\117\13"+
            "\uffff\1\120\2\uffff\1\122\1\121",
            "\1\123\37\uffff\1\123",
            "\1\124\1\125\5\uffff\1\126\30\uffff\1\124\1\125\5\uffff\1\126",
            "\1\127\37\uffff\1\127",
            "\1\130\37\uffff\1\130",
            "\1\131",
            "\1\57\1\uffff\12\46\13\uffff\1\57\37\uffff\1\57",
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
            "",
            "\1\133\37\uffff\1\133",
            "\1\134\37\uffff\1\134",
            "\1\47\13\uffff\12\47\7\uffff\2\47\1\136\27\47\4\uffff\1\47"+
            "\1\uffff\2\47\1\136\27\47",
            "\1\137\37\uffff\1\137",
            "\1\140\37\uffff\1\140",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "\1\142\37\uffff\1\142",
            "\1\143\37\uffff\1\143",
            "\1\144\6\uffff\1\145\30\uffff\1\144\6\uffff\1\145",
            "\1\146\37\uffff\1\146",
            "\1\147\37\uffff\1\147",
            "\1\150\37\uffff\1\150",
            "\1\151\37\uffff\1\151",
            "\1\152\37\uffff\1\152",
            "\1\153\15\uffff\1\154\21\uffff\1\153\15\uffff\1\154",
            "\1\155\37\uffff\1\155",
            "\1\47\13\uffff\12\47\7\uffff\3\47\1\157\16\47\1\160\1\161\6"+
            "\47\4\uffff\1\47\1\uffff\3\47\1\157\16\47\1\160\1\161\6\47",
            "\1\162\37\uffff\1\162",
            "\1\163\37\uffff\1\163",
            "\1\164\37\uffff\1\164",
            "\1\165\37\uffff\1\165",
            "\1\166\3\uffff\1\167\33\uffff\1\166\3\uffff\1\167",
            "\1\170\37\uffff\1\170",
            "\1\171\37\uffff\1\171",
            "\1\172\37\uffff\1\172",
            "\1\47\13\uffff\12\47\7\uffff\3\47\1\174\26\47\4\uffff\1\47"+
            "\1\uffff\3\47\1\174\26\47",
            "\1\175\4\uffff\1\176\7\uffff\1\177\22\uffff\1\175\4\uffff\1"+
            "\176\7\uffff\1\177",
            "\1\u0080\37\uffff\1\u0080",
            "\1\u0081\37\uffff\1\u0081",
            "\1\u0082\37\uffff\1\u0082",
            "\1\u0083\37\uffff\1\u0083",
            "\1\u0084\37\uffff\1\u0084",
            "\1\u0085\37\uffff\1\u0085",
            "\1\u0086\37\uffff\1\u0086",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "\1\u0088\37\uffff\1\u0088",
            "",
            "",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "\1\u008d\37\uffff\1\u008d",
            "",
            "\1\u008e\37\uffff\1\u008e",
            "\1\u008f\37\uffff\1\u008f",
            "\1\u0090\37\uffff\1\u0090",
            "\1\u0091\37\uffff\1\u0091",
            "\1\u0092\37\uffff\1\u0092",
            "\1\u0093\37\uffff\1\u0093",
            "\1\u0094\37\uffff\1\u0094",
            "\1\u0095\37\uffff\1\u0095",
            "\1\u0096\37\uffff\1\u0096",
            "\1\u0097\37\uffff\1\u0097",
            "\1\u0098\37\uffff\1\u0098",
            "\1\u0099\37\uffff\1\u0099",
            "",
            "\1\u009a\37\uffff\1\u009a",
            "\1\u009b\37\uffff\1\u009b",
            "\1\u009c\11\uffff\1\u009d\25\uffff\1\u009c\11\uffff\1\u009d",
            "\1\u009e\37\uffff\1\u009e",
            "\1\u009f\37\uffff\1\u009f",
            "\1\u00a0\37\uffff\1\u00a0",
            "\1\u00a1\37\uffff\1\u00a1",
            "\1\u00a2\37\uffff\1\u00a2",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "\1\u00a5\37\uffff\1\u00a5",
            "\1\u00a6\37\uffff\1\u00a6",
            "",
            "\1\u00a7\37\uffff\1\u00a7",
            "\1\u00a8\37\uffff\1\u00a8",
            "\1\u00a9\37\uffff\1\u00a9",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "\1\u00ab\37\uffff\1\u00ab",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "\1\u00ad\37\uffff\1\u00ad",
            "\1\u00ae\37\uffff\1\u00ae",
            "\1\u00af\37\uffff\1\u00af",
            "\1\u00b0\37\uffff\1\u00b0",
            "\1\u00b1\37\uffff\1\u00b1",
            "",
            "\1\u00b2\37\uffff\1\u00b2",
            "",
            "",
            "",
            "",
            "\1\u00b3\37\uffff\1\u00b3",
            "\1\u00b4\37\uffff\1\u00b4",
            "\1\u00b5\37\uffff\1\u00b5",
            "\1\u00b6\37\uffff\1\u00b6",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "\1\u00b8\37\uffff\1\u00b8",
            "\1\u00b9\37\uffff\1\u00b9",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "\1\u00bb\37\uffff\1\u00bb",
            "\1\u00bc\37\uffff\1\u00bc",
            "\1\u00bd\37\uffff\1\u00bd",
            "\1\u00be\37\uffff\1\u00be",
            "\1\u00bf\37\uffff\1\u00bf",
            "\1\u00c0\37\uffff\1\u00c0",
            "\1\u00c1\37\uffff\1\u00c1",
            "\1\u00c2\37\uffff\1\u00c2",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "\1\u00c4\37\uffff\1\u00c4",
            "\1\u00c5\37\uffff\1\u00c5",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "\1\u00c7\37\uffff\1\u00c7",
            "\1\u00c8\37\uffff\1\u00c8",
            "",
            "",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "\1\u00ca\37\uffff\1\u00ca",
            "\1\u00cb\37\uffff\1\u00cb",
            "\1\u00cc\37\uffff\1\u00cc",
            "\1\u00cd\37\uffff\1\u00cd",
            "",
            "\1\u00ce\37\uffff\1\u00ce",
            "",
            "\1\u00cf\37\uffff\1\u00cf",
            "\1\u00d0\37\uffff\1\u00d0",
            "\1\u00d1\37\uffff\1\u00d1",
            "\1\47\13\uffff\12\47\7\uffff\10\47\1\u00d3\21\47\4\uffff\1"+
            "\47\1\uffff\10\47\1\u00d3\21\47",
            "\1\u00d4\37\uffff\1\u00d4",
            "\1\u00d5\37\uffff\1\u00d5",
            "\1\u00d6\37\uffff\1\u00d6",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "\1\u00d8\37\uffff\1\u00d8",
            "\1\u00d9\37\uffff\1\u00d9",
            "",
            "\1\u00da\37\uffff\1\u00da",
            "\1\u00db\37\uffff\1\u00db",
            "",
            "\1\u00dc\37\uffff\1\u00dc",
            "\1\u00dd\37\uffff\1\u00dd",
            "\1\u00de\37\uffff\1\u00de",
            "\1\47\13\uffff\12\47\7\uffff\16\47\1\u00e0\13\47\4\uffff\1"+
            "\47\1\uffff\16\47\1\u00e0\13\47",
            "\1\u00e1\37\uffff\1\u00e1",
            "\1\u00e2\37\uffff\1\u00e2",
            "\1\u00e3\37\uffff\1\u00e3",
            "\1\u00e4\37\uffff\1\u00e4",
            "",
            "\1\u00e5\12\uffff\1\u00e6\24\uffff\1\u00e5\12\uffff\1\u00e6",
            "\1\u00e7\37\uffff\1\u00e7",
            "",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "",
            "\1\u00ea\37\uffff\1\u00ea",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "\1\u00ec\37\uffff\1\u00ec",
            "\1\u00ed\37\uffff\1\u00ed",
            "\1\u00ee\37\uffff\1\u00ee",
            "\1\u00ef\37\uffff\1\u00ef",
            "\1\u00f0\37\uffff\1\u00f0",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "",
            "\1\u00f2\37\uffff\1\u00f2",
            "\1\u00f3\37\uffff\1\u00f3",
            "\1\u00f4\37\uffff\1\u00f4",
            "\1\u00f5\37\uffff\1\u00f5",
            "",
            "\1\u00f6\37\uffff\1\u00f6",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "\1\u00f8\37\uffff\1\u00f8",
            "\1\u00f9\37\uffff\1\u00f9",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "\1\u00fb\37\uffff\1\u00fb",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "",
            "\1\u00fd\37\uffff\1\u00fd",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "\1\u00ff\37\uffff\1\u00ff",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "\1\u0101\37\uffff\1\u0101",
            "\1\u0102\37\uffff\1\u0102",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "",
            "",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "",
            "\1\u0106\37\uffff\1\u0106",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "\1\u0108\37\uffff\1\u0108",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "\1\u010a\37\uffff\1\u010a",
            "",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "\1\u010c\37\uffff\1\u010c",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "\1\u010f",
            "",
            "\1\u0110\37\uffff\1\u0110",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "",
            "\1\u0113\37\uffff\1\u0113",
            "",
            "\1\u0114\37\uffff\1\u0114",
            "",
            "\1\u0115\37\uffff\1\u0115",
            "\1\u0116\37\uffff\1\u0116",
            "",
            "",
            "",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "",
            "\1\u0118\37\uffff\1\u0118",
            "",
            "\1\u0119\37\uffff\1\u0119",
            "",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "",
            "",
            "\1\u011b\37\uffff\1\u011b",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "",
            "",
            "\1\u011d\37\uffff\1\u011d",
            "\1\u011e\37\uffff\1\u011e",
            "\1\u011f\37\uffff\1\u011f",
            "\1\u0120\37\uffff\1\u0120",
            "",
            "\1\u0121\37\uffff\1\u0121",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "",
            "\1\u0123\37\uffff\1\u0123",
            "",
            "\1\u0124\37\uffff\1\u0124",
            "\1\u0125\37\uffff\1\u0125",
            "\1\u0126\37\uffff\1\u0126",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "",
            "\1\u0129\37\uffff\1\u0129",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "\1\u012c\37\uffff\1\u012c",
            "",
            "",
            "\1\u012d\37\uffff\1\u012d",
            "",
            "",
            "\1\u012e\37\uffff\1\u012e",
            "\1\u012f\37\uffff\1\u012f",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
            "\1\u0131\37\uffff\1\u0131",
            "",
            "\1\u0132\37\uffff\1\u0132",
            "\1\u0133\37\uffff\1\u0133",
            "\1\u0134\37\uffff\1\u0134",
            "\1\47\13\uffff\12\47\7\uffff\32\47\4\uffff\1\47\1\uffff\32"+
            "\47",
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
            return "1:1: Tokens : ( EQUALS | NOT_EQUALS1 | NOT_EQUALS2 | LESS | LESS_OR_EQ | GREATER | GREATER_OR_EQ | SEMI | COMMA | LPAREN | RPAREN | DOT | UNDERSCORE | DOLLAR | QUESTION | QUOTE_DOUBLE | QUOTE_SINGLE | BACKSLASH | ASTERISK | LPAREN_SQUARE | RPAREN_SQUARE | ALL | AND | AS | ASC | AVG | BETWEEN | BY | COUNT | CURRENT_TIMESTAMP | DELETE | DESC | DISTINCT | DOCHINT | FROM | GLIMIT | GOFFSET | GRANGE | GROUP | GROUPORDER | HAVING | IN | INDEXWHERE | UNIONHASH | INSERT | INTERSECTION | INTO | LEFT | LIMIT | MATCH | MAX | MIN | NULL | OFFSET | OR | ORDER | SEGHINT | SELECT | SET | SQUARESUM | SUM | STDDEV | UNIONS | UPDATE | VARIANCE | WHERE | WITH | WITHIN | WORDSEG | TO | EXPORT | STRING | ID | INTEGER | FLOAT | BLOB | WS );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA21_13 = input.LA(1);

                        s = -1;
                        if ( ((LA21_13 >= '\u0000' && LA21_13 <= '\uFFFF')) ) {s = 51;}

                        else s = 50;

                        if ( s>=0 ) return s;
                        break;

                    case 1 : 
                        int LA21_14 = input.LA(1);

                        s = -1;
                        if ( ((LA21_14 >= '\u0000' && LA21_14 <= '\uFFFF')) ) {s = 51;}

                        else s = 52;

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