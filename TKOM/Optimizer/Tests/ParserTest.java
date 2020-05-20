import Lexer.Lexer;
import Parser.Parser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import java.io.ByteArrayOutputStream;
import Lexer.Token;
import java.io.PrintStream;

import static Lexer.Token.TokenType.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

class ParserTest {

    private class MockLexer0 extends Lexer {
        int i = 0;
        MockToken[] tab = {new MockToken(Int), new MockToken(Main), new MockToken(OpenBrace), new MockToken(ClosedBrace),
                new MockToken(OpenCurlyBrace), new MockToken(Return), new MockToken(Number), new MockToken(Semicolon), new MockToken(ClosedCurlyBrace)};
        public Token nextToken(){
            if(i < tab.length)i++;
            return tab[i-1];
        }
    }

    private class MockLexer1 extends Lexer {
        int i = 0;
        MockToken[] tab = {new MockToken(Int), new MockToken(Main), new MockToken(OpenBrace), new MockToken(ClosedBrace),
                new MockToken(OpenCurlyBrace), new MockToken(Number), new MockToken(Semicolon), new MockToken(ClosedCurlyBrace)};
        public Token nextToken(){
            if(i < tab.length)i++;
            return tab[i-1];
        }
    }

    private class MockLexer2 extends Lexer {
        int i = 0;
        MockToken[] tab = {new MockToken(Int), new MockToken(Main), new MockToken(OpenBrace), new MockToken(ClosedBrace),
                new MockToken(OpenCurlyBrace), new MockToken(Int), new MockToken(Identifier), new MockToken(Semicolon),
                new MockToken(Return), new MockToken(Number), new MockToken(Semicolon), new MockToken(ClosedCurlyBrace)};
        public Token nextToken(){
            if(i < tab.length)i++;
            return tab[i-1];
        }
    }

    private class MockLexer3 extends Lexer {
        int i = 0;
        MockToken[] tab = {new MockToken(Int), new MockToken(Main), new MockToken(OpenBrace), new MockToken(ClosedBrace),
                new MockToken(OpenCurlyBrace),
                new MockToken(Int), new MockToken(Identifier), new MockToken(Assign), new MockToken(Number),
                new MockToken(Semicolon), new MockToken(Return), new MockToken(Number), new MockToken(Semicolon), new MockToken(ClosedCurlyBrace)};
        public Token nextToken(){
            if(i < tab.length)i++;
            return tab[i-1];
        }
    }

    private class MockLexer4 extends Lexer {
        int i = 0;
        MockToken[] tab = {new MockToken(Int), new MockToken(Main), new MockToken(OpenBrace), new MockToken(ClosedBrace),
                new MockToken(OpenCurlyBrace),
                new MockToken(Int), new MockToken(Identifier),
                new MockToken(OpenSquareBrace), new MockToken(Number), new MockToken(ClosedSquareBrace),
                new MockToken(Semicolon), new MockToken(Return), new MockToken(Number), new MockToken(Semicolon), new MockToken(ClosedCurlyBrace)};
        public Token nextToken(){
            if(i < tab.length)i++;
            return tab[i-1];
        }
    }

    private class MockLexer5 extends Lexer {
        int i = 0;
        MockToken[] tab = {new MockToken(Int), new MockToken(Main), new MockToken(OpenBrace), new MockToken(ClosedBrace),
                new MockToken(OpenCurlyBrace),
                new MockToken(Int), new MockToken(Identifier), new MockToken(OpenSquareBrace), new MockToken(Number), new MockToken(ClosedSquareBrace),
                new MockToken(Assign),
                new MockToken(Number),new MockToken(Semicolon),
                new MockToken(Return), new MockToken(Number), new MockToken(Semicolon), new MockToken(ClosedCurlyBrace)};
        public Token nextToken(){
            if(i < tab.length)i++;
            return tab[i-1];
        }
    }

    private class MockLexer6 extends Lexer {
        int i = 0;
        MockToken[] tab = {new MockToken(Int), new MockToken(Main), new MockToken(OpenBrace), new MockToken(ClosedBrace),
                new MockToken(OpenCurlyBrace),
                new MockToken(Int), new MockToken(Identifier), new MockToken(OpenSquareBrace), new MockToken(Number), new MockToken(ClosedSquareBrace),
                new MockToken(Assign),
                new MockToken(OpenCurlyBrace), new MockToken(Number), new MockToken(ClosedCurlyBrace), new MockToken(Semicolon),
                new MockToken(Return), new MockToken(Number), new MockToken(Semicolon), new MockToken(ClosedCurlyBrace)};
        public Token nextToken(){
            if(i < tab.length)i++;
            return tab[i-1];
        }
    }

    private class MockLexer7 extends Lexer {
        int i = 0;
        MockToken[] tab = {new MockToken(Int), new MockToken(Main), new MockToken(OpenBrace), new MockToken(ClosedBrace),
                new MockToken(OpenCurlyBrace),
                new MockToken(Int), new MockToken(Identifier),
                new MockToken(Assign), new MockToken(Number), new MockToken(Plus), new MockToken(Number),
                new MockToken(Semicolon), new MockToken(Return), new MockToken(Number), new MockToken(Semicolon), new MockToken(ClosedCurlyBrace)};
        public Token nextToken(){
            if(i < tab.length)i++;
            return tab[i-1];
        }
    }

    private class MockLexer8 extends Lexer {
        int i = 0;
        MockToken[] tab = {new MockToken(Int), new MockToken(Main), new MockToken(OpenBrace), new MockToken(ClosedBrace),
                new MockToken(OpenCurlyBrace),
                new MockToken(Int), new MockToken(Identifier),
                new MockToken(Assign), new MockToken(Number), new MockToken(Plus), new MockToken(Number), new MockToken(Multiply), new MockToken(FiniteNumber),
                new MockToken(Semicolon), new MockToken(Return), new MockToken(Number), new MockToken(Semicolon), new MockToken(ClosedCurlyBrace)};
        public Token nextToken(){
            if(i < tab.length)i++;
            return tab[i-1];
        }
    }

    private class MockLexer9 extends Lexer {
        int i = 0;
        MockToken[] tab = {new MockToken(Int), new MockToken(Main), new MockToken(OpenBrace), new MockToken(ClosedBrace),
                new MockToken(OpenCurlyBrace),
                new MockToken(Int), new MockToken(Identifier),
                new MockToken(Assign), new MockToken(OpenBrace), new MockToken(Number), new MockToken(Plus), new MockToken(Number), new MockToken(ClosedBrace),
                new MockToken(Semicolon), new MockToken(Return), new MockToken(Number), new MockToken(Semicolon), new MockToken(ClosedCurlyBrace)};
        public Token nextToken(){
            if(i < tab.length)i++;
            return tab[i-1];
        }
    }

    private class MockLexer10 extends Lexer {
        int i = 0;
        MockToken[] tab = {new MockToken(Int), new MockToken(Main), new MockToken(OpenBrace), new MockToken(ClosedBrace),
                new MockToken(OpenCurlyBrace),
                new MockToken(Int), new MockToken(Identifier),
                new MockToken(Assign), new MockToken(OpenBrace), new MockToken(OpenBrace), new MockToken(Number), new MockToken(Plus), new MockToken(Number), new MockToken(ClosedBrace),
                new MockToken(Semicolon), new MockToken(Return), new MockToken(Number), new MockToken(Semicolon), new MockToken(ClosedCurlyBrace)};
        public Token nextToken(){
            if(i < tab.length)i++;
            return tab[i-1];
        }
    }

    private class MockLexer11 extends Lexer {
        int i = 0;
        MockToken[] tab = {new MockToken(Int), new MockToken(Main), new MockToken(OpenBrace), new MockToken(ClosedBrace),
                new MockToken(OpenCurlyBrace),
                new MockToken(Int), new MockToken(Identifier),
                new MockToken(Assign), new MockToken(ClosedBrace), new MockToken(Number), new MockToken(Plus), new MockToken(Number), new MockToken(OpenBrace),
                new MockToken(Semicolon), new MockToken(Return), new MockToken(Number), new MockToken(Semicolon), new MockToken(ClosedCurlyBrace)};
        public Token nextToken(){
            if(i < tab.length)i++;
            return tab[i-1];
        }
    }

    private class MockLexer12 extends Lexer {
        int i = 0;
        MockToken[] tab = {new MockToken(Int), new MockToken(Main), new MockToken(OpenBrace), new MockToken(ClosedBrace),
                new MockToken(OpenCurlyBrace),
                new MockToken(For), new MockToken(OpenBrace), new MockToken(Semicolon), new MockToken(Semicolon), new MockToken(ClosedBrace),
                new MockToken(OpenCurlyBrace), new MockToken(ClosedCurlyBrace),
                new MockToken(Return), new MockToken(Number), new MockToken(Semicolon), new MockToken(ClosedCurlyBrace)};
        public Token nextToken(){
            if(i < tab.length)i++;
            return tab[i-1];
        }
    }

    private class MockLexer13 extends Lexer {
        int i = 0;
        MockToken[] tab = {new MockToken(Int), new MockToken(Main), new MockToken(OpenBrace), new MockToken(ClosedBrace),
                new MockToken(OpenCurlyBrace),
                new MockToken(For), new MockToken(OpenBrace),
                new MockToken(Int), new MockToken(Identifier),
                new MockToken(Assign), new MockToken(Number),new MockToken(Semicolon),
                new MockToken(Identifier), new MockToken(Equal), new MockToken(Number), new MockToken(Semicolon),
                new MockToken(Identifier), new MockToken(Assign), new MockToken(Number), new MockToken(Plus), new MockToken(Number),
                new MockToken(ClosedBrace),
                new MockToken(OpenCurlyBrace), new MockToken(ClosedCurlyBrace),
                new MockToken(Return), new MockToken(Number), new MockToken(Semicolon), new MockToken(ClosedCurlyBrace)};
        public Token nextToken(){
            if(i < tab.length)i++;
            return tab[i-1];
        }
    }

    private class MockLexer14 extends Lexer {
        int i = 0;
        MockToken[] tab = {new MockToken(Int), new MockToken(Main), new MockToken(OpenBrace), new MockToken(ClosedBrace),
                new MockToken(OpenCurlyBrace),
                new MockToken(Bool), new MockToken(Identifier), new MockToken(Assign), new MockToken(Negation), new MockToken(True),
                new MockToken(Semicolon), new MockToken(Return), new MockToken(Number), new MockToken(Semicolon), new MockToken(ClosedCurlyBrace)};
        public Token nextToken(){
            if(i < tab.length)i++;
            return tab[i-1];
        }
    }

    private class MockLexer15 extends Lexer {
        int i = 0;
        MockToken[] tab = {new MockToken(Int), new MockToken(Main), new MockToken(OpenBrace), new MockToken(ClosedBrace),
                new MockToken(OpenCurlyBrace),
                new MockToken(Bool), new MockToken(Identifier), new MockToken(Assign), new MockToken(Negation), new MockToken(Multiply), new MockToken(True),
                new MockToken(Semicolon), new MockToken(Return), new MockToken(Number), new MockToken(Semicolon), new MockToken(ClosedCurlyBrace)};
        public Token nextToken(){
            if(i < tab.length)i++;
            return tab[i-1];
        }
    }

    private class MockLexer16 extends Lexer {
        int i = 0;
        MockToken[] tab = {new MockToken(Int), new MockToken(Main), new MockToken(OpenBrace), new MockToken(ClosedBrace),
                new MockToken(OpenCurlyBrace),
                new MockToken(Bool), new MockToken(Identifier), new MockToken(Assign), new MockToken(Minus), new MockToken(Minus), new MockToken(True),
                new MockToken(Semicolon), new MockToken(Return), new MockToken(Number), new MockToken(Semicolon), new MockToken(ClosedCurlyBrace)};
        public Token nextToken(){
            if(i < tab.length)i++;
            return tab[i-1];
        }
    }

    private class MockLexer17 extends Lexer {
        int i = 0;
        MockToken[] tab = {new MockToken(Int), new MockToken(Main), new MockToken(OpenBrace), new MockToken(ClosedBrace),
                new MockToken(OpenCurlyBrace),
                new MockToken(If), new MockToken(OpenBrace), new MockToken(True), new MockToken(ClosedBrace),
                new MockToken(Identifier), new MockToken(Assign), new MockToken(Minus), new MockToken(Number),
                new MockToken(Semicolon), new MockToken(Return), new MockToken(Number), new MockToken(Semicolon), new MockToken(ClosedCurlyBrace)};
        public Token nextToken(){
            if(i < tab.length)i++;
            return tab[i-1];
        }
    }

    private class MockLexer18 extends Lexer {
        int i = 0;
        MockToken[] tab = {new MockToken(Int), new MockToken(Main), new MockToken(OpenBrace), new MockToken(ClosedBrace),
                new MockToken(OpenCurlyBrace),
                new MockToken(If), new MockToken(OpenBrace),new MockToken(ClosedBrace),
                new MockToken(Identifier), new MockToken(Assign), new MockToken(Minus), new MockToken(Number),
                new MockToken(Semicolon), new MockToken(Return), new MockToken(Number), new MockToken(Semicolon), new MockToken(ClosedCurlyBrace)};
        public Token nextToken(){
            if(i < tab.length)i++;
            return tab[i-1];
        }
    }

    private class MockLexer19 extends Lexer {
        int i = 0;
        MockToken[] tab = {new MockToken(Int), new MockToken(Main), new MockToken(OpenBrace), new MockToken(ClosedBrace),
                new MockToken(OpenCurlyBrace),
                new MockToken(If), new MockToken(OpenBrace), new MockToken(True),new MockToken(ClosedBrace),
                new MockToken(OpenCurlyBrace),
                new MockToken(Identifier), new MockToken(Assign), new MockToken(Minus), new MockToken(Number), new MockToken(Semicolon),
                new MockToken(ClosedCurlyBrace),
                new MockToken(Return), new MockToken(Number), new MockToken(Semicolon), new MockToken(ClosedCurlyBrace)};
        public Token nextToken(){
            if(i < tab.length)i++;
            return tab[i-1];
        }
    }

    private class MockLexer20 extends Lexer {
        int i = 0;
        MockToken[] tab = {new MockToken(Int), new MockToken(Main), new MockToken(OpenBrace), new MockToken(ClosedBrace),
                new MockToken(OpenCurlyBrace),
                new MockToken(For), new MockToken(OpenBrace), new MockToken(Semicolon), new MockToken(Semicolon), new MockToken(ClosedBrace),
                new MockToken(OpenCurlyBrace),new MockToken(Break), new MockToken(Semicolon), new MockToken(ClosedCurlyBrace),
                new MockToken(Return), new MockToken(Number), new MockToken(Semicolon), new MockToken(ClosedCurlyBrace)};
        public Token nextToken(){
            if(i < tab.length)i++;
            return tab[i-1];
        }
    }

    private class MockLexer21 extends Lexer {
        int i = 0;
        MockToken[] tab = {new MockToken(Int), new MockToken(Main), new MockToken(OpenBrace), new MockToken(ClosedBrace),
                new MockToken(OpenCurlyBrace),
                new MockToken(For), new MockToken(OpenBrace), new MockToken(Semicolon), new MockToken(Semicolon), new MockToken(ClosedBrace),
                new MockToken(OpenCurlyBrace), new MockToken(ClosedCurlyBrace),
                new MockToken(Break), new MockToken(Semicolon),
                new MockToken(Return), new MockToken(Number), new MockToken(Semicolon), new MockToken(ClosedCurlyBrace)};
        public Token nextToken(){
            if(i < tab.length)i++;
            return tab[i-1];
        }
    }

    private class MockLexer22 extends Lexer {
        int i = 0;
        MockToken[] tab = {new MockToken(Int), new MockToken(Main), new MockToken(OpenBrace), new MockToken(ClosedBrace),
                new MockToken(OpenCurlyBrace),
                new MockToken(Double), new MockToken(Identifier), new MockToken(Assign), new MockToken(Number),
                new MockToken(Multiply), new MockToken(Minus), new MockToken(FiniteNumber),
                new MockToken(Semicolon), new MockToken(Return), new MockToken(Number), new MockToken(Semicolon), new MockToken(ClosedCurlyBrace)};
        public Token nextToken(){
            if(i < tab.length)i++;
            return tab[i-1];
        }
    }

    private class MockLexer23 extends Lexer {
        int i = 0;
        MockToken[] tab = {new MockToken(Int), new MockToken(Main), new MockToken(OpenBrace), new MockToken(ClosedBrace),
                new MockToken(OpenCurlyBrace),
                new MockToken(Double), new MockToken(Identifier), new MockToken(Assign), new MockToken(Number),
                new MockToken(Minus), new MockToken(Minus), new MockToken(FiniteNumber),
                new MockToken(Semicolon), new MockToken(Return), new MockToken(Number), new MockToken(Semicolon), new MockToken(ClosedCurlyBrace)};
        public Token nextToken(){
            if(i < tab.length)i++;
            return tab[i-1];
        }
    }

    private class MockLexer24 extends Lexer {
        int i = 0;
        MockToken[] tab = {new MockToken(Int), new MockToken(Main), new MockToken(OpenBrace), new MockToken(ClosedBrace),
                new MockToken(OpenCurlyBrace),
                new MockToken(For), new MockToken(OpenBrace), new MockToken(Semicolon), new MockToken(Semicolon), new MockToken(ClosedBrace),
                new MockToken(OpenCurlyBrace),
                new MockToken(For), new MockToken(OpenBrace), new MockToken(Semicolon), new MockToken(Semicolon), new MockToken(ClosedBrace),
                new MockToken(OpenCurlyBrace),
                new MockToken(Break), new MockToken(Semicolon),
                new MockToken(ClosedCurlyBrace),
                new MockToken(ClosedCurlyBrace),
                new MockToken(Return), new MockToken(Number), new MockToken(Semicolon), new MockToken(ClosedCurlyBrace)};
        public Token nextToken(){
            if(i < tab.length)i++;
            return tab[i-1];
        }
    }

    private class MockToken extends Token
    {
        private TokenType type;
        MockToken(TokenType tokenType){
            this.type = tokenType;
        }

        @Override
        public TokenType getType() {
            return type;
        }
    }
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    @DisplayName("Empty")
    void test0(){
        MockLexer0 lexer = new MockLexer0();
        Parser parser = new Parser(lexer);
        parser.parseProgram();
        assertEquals("", outContent.toString());
    }

    @Test
    @DisplayName("No Return error")
    void test1(){
        MockLexer1 lexer = new MockLexer1();
        Parser parser = new Parser(lexer);
        parser.parseProgram();
        assertNotEquals("", outContent.toString());
    }

    @Test
    @DisplayName("Initialization")
    void test2(){
        MockLexer2 lexer = new MockLexer2();
        Parser parser = new Parser(lexer);
        parser.parseProgram();
        assertEquals("", outContent.toString());
    }

    @Test
    @DisplayName("Initialization with assign")
    void test3(){
        MockLexer3 lexer = new MockLexer3();
        Parser parser = new Parser(lexer);
        parser.parseProgram();
        assertEquals("", outContent.toString());
    }

    @Test
    @DisplayName("Initialization for array")
    void test4(){
        MockLexer4 lexer = new MockLexer4();
        Parser parser = new Parser(lexer);
        parser.parseProgram();
        assertEquals("", outContent.toString());
    }

    @Test
    @DisplayName("Initialization for array with normal assign error")
    void test5(){
        MockLexer5 lexer = new MockLexer5();
        Parser parser = new Parser(lexer);
        parser.parseProgram();
        assertNotEquals("", outContent.toString());
    }

    @Test
    @DisplayName("Initialization for array with group assign")
    void test6(){
        MockLexer6 lexer = new MockLexer6();
        Parser parser = new Parser(lexer);
        parser.parseProgram();
        assertEquals("", outContent.toString());
    }

    @Test
    @DisplayName("Initialization with expression assign")
    void test7(){
        MockLexer7 lexer = new MockLexer7();
        Parser parser = new Parser(lexer);
        parser.parseProgram();
        assertEquals("", outContent.toString());
    }

    @Test
    @DisplayName("Initialization with expression assign2")
    void test8(){
        MockLexer8 lexer = new MockLexer8();
        Parser parser = new Parser(lexer);
        parser.parseProgram();
        assertEquals("", outContent.toString());
    }

    @Test
    @DisplayName("Initialization with expression with braces")
    void test9(){
        MockLexer9 lexer = new MockLexer9();
        Parser parser = new Parser(lexer);
        parser.parseProgram();
        assertEquals("", outContent.toString());
    }

    @Test
    @DisplayName("Initialization with expression with wrong braces error")
    void test10(){
        MockLexer10 lexer = new MockLexer10();
        Parser parser = new Parser(lexer);
        parser.parseProgram();
        assertNotEquals("", outContent.toString());
    }

    @Test
    @DisplayName("Initialization with expression with reversed braces error")
    void test11(){
        MockLexer11 lexer = new MockLexer11();
        Parser parser = new Parser(lexer);
        parser.parseProgram();
        assertNotEquals("", outContent.toString());
    }

    @Test
    @DisplayName("Empty for")
    void test12(){
        MockLexer12 lexer = new MockLexer12();
        Parser parser = new Parser(lexer);
        parser.parseProgram();
        assertEquals("", outContent.toString());
    }

    @Test
    @DisplayName("Full for")
    void test13(){
        MockLexer13 lexer = new MockLexer13();
        Parser parser = new Parser(lexer);
        parser.parseProgram();
        assertEquals("", outContent.toString());
    }

    @Test
    @DisplayName("Negation")
    void test14(){
        MockLexer14 lexer = new MockLexer14();
        Parser parser = new Parser(lexer);
        parser.parseProgram();
        assertEquals("", outContent.toString());
    }

    @Test
    @DisplayName("Negation of operator error")
    void test15(){
        MockLexer15 lexer = new MockLexer15();
        Parser parser = new Parser(lexer);
        parser.parseProgram();
        assertNotEquals("", outContent.toString());
    }

    @Test
    @DisplayName("Double operator error")
    void test16(){
        MockLexer16 lexer = new MockLexer16();
        Parser parser = new Parser(lexer);
        parser.parseProgram();
        assertNotEquals("", outContent.toString());
    }

    @Test
    @DisplayName("If")
    void test17(){
        MockLexer17 lexer = new MockLexer17();
        Parser parser = new Parser(lexer);
        parser.parseProgram();
        assertEquals("", outContent.toString());
    }

    @Test
    @DisplayName("No expression if error")
    void test18(){
        MockLexer18 lexer = new MockLexer18();
        Parser parser = new Parser(lexer);
        parser.parseProgram();
        assertNotEquals("", outContent.toString());
    }

    @Test
    @DisplayName("Block instruction if")
    void test19(){
        MockLexer19 lexer = new MockLexer19();
        Parser parser = new Parser(lexer);
        parser.parseProgram();
        assertEquals("", outContent.toString());
    }

    @Test
    @DisplayName("Break")
    void test20(){
        MockLexer20 lexer = new MockLexer20();
        Parser parser = new Parser(lexer);
        parser.parseProgram();
        assertEquals("", outContent.toString());
    }

    @Test
    @DisplayName("Outside for break error")
    void test21(){
        MockLexer21 lexer = new MockLexer21();
        Parser parser = new Parser(lexer);
        parser.parseProgram();
        assertNotEquals("", outContent.toString());
    }

    @Test
    @DisplayName("Minus value")
    void test22(){
        MockLexer22 lexer = new MockLexer22();
        Parser parser = new Parser(lexer);
        parser.parseProgram();
        assertEquals("", outContent.toString());
    }

    @Test
    @DisplayName("Minus minus value error")
    void test23(){
        MockLexer23 lexer = new MockLexer23();
        Parser parser = new Parser(lexer);
        parser.parseProgram();
        assertNotEquals("", outContent.toString());
    }

    @Test
    @DisplayName("Double For")
    void test24(){
        MockLexer24 lexer = new MockLexer24();
        Parser parser = new Parser(lexer);
        parser.parseProgram();
        assertEquals("", outContent.toString());
    }
}

