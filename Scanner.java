package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.craftinginterpreters.lox.TokenType.*;

class Scanner{
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    
    //fields to keep track where the scanner is
    private int start = 0;
    private int current = 0;
    private int line = 1;

    private static final Map<String, TokenType> keywords;

    //create a map to map words to identifiers for identifier()
    static{
        keywords = new HashMap<>();
        keywords.put("and", AND);
        keywords.put("class", CLASS);
        keywords.put("else", ELSE);
        keywords.put("false", FALSE);
        keywords.put("fun", FUN);
        keywords.put("for", FOR);
        keywords.put("if", IF);
        keywords.put("nil", NIL);
        keywords.put("or", OR);
        keywords.put("print", PRINT);
        keywords.put("return", RETURN);
        keywords.put("super", SUPER);
        keywords.put("this", THIS);
        keywords.put("true", TRUE);
        keywords.put("var", VAR);
        keywords.put("while", WHILE);
    }

    //storing raw source code as a simple string
    Scanner(String source){
        this.source = source;
    }

    List<Token> scanTokens(){
        while(!isAtEnd()){
            start = current;
            scanToken();
        }
        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    //scanning to recognize lexemes aka switch cases
    private void scanToken(){
        char c = advance();
        switch(c){
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case '+': addToken(PLUS); break;
            case '-': addToken(MINUS); break;
            case '*': addToken(STAR); break;
            case '.': addToken(DOT); break;
            case ';': addToken(SEMI_COLON); break;
            case ',': addToken(COMMA); break;

            //cases which can be !=, ==, ....
            // if ! matches = , != else !
            case '!':
                addToken(match('=') ? EXCAIM_EQUAL : EXCLAIM);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case '/':
                if(match('/')){
                    while(peek() != '\n' && !isAtEnd()) advance();
                }else{
                    addToken(SLASH);
                }
                break;
            //skipping whitespace and newlines
            case ' ':
            case '\r':
            case '\t':   
                //ignore cases with whitespace
                break;
            case '\n':
                line++;
                break;
            //string literals, search for quotes "
            case '"': string(); break;
            /*
            but this way, words like orchid will also be considered OR
            //for reserved words and identifiers, OR
            case 'o':
                if(match('r')){
                    addToken(OR);
                }
                break;
            */
            //for cases where the character is not recognised
            default:
                //number literals, look for digits
                if(isDigit(c)){
                    number();
                }else if(isAlpha()){
                    //if alphanumeric, it is an identifier
                    indentifier();
                }else{
                    Lox.error(line, "Unexpected character.");
                    break;
                }
        }
    }

    private void string(){
        //while inside the quotes
        while(peek() != '"' && !isAtEnd()){
            if(peek() == '\n') line++;
            advance();
        }
        //if end and no closing "
        if(isAtEnd()){
            Lox.error(line, "Unterminated string");
            return;
        }
        advance();
        //value is the string with ' removed
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }
    
    private void number(){
        //while it is a number, advance through the number 'string'
        while(isDigit(peek())) advance();

        if(peek() == '.' && isDigit(peekNext())){
            //consume . as num.num is a valid format
            advance();

            //continue down the numbers after .
            while(isDigit(peek())) advance();
        }

        //parsing the number as a double, start and current as there is no '
        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private void identifier(){
        while(isAlphaNumeric(peek())) advance();
        
        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if(type == null) type = IDENTIFIER;
        addToken(type);
    }

    //helper method that tells us if all characters are consumed
    private boolean isAtEnd(){

        //when current pointer has bypassed stored string
        return current >= source.length();
    }

    //helper methods for scanToken
    private char advance(){
        return source.charAt(current++);
    }

    private void addToken(TokenType type){
        addToken(type,null);
    }

    private void addToken(TokenType type, Object literal){
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private boolean match(char expected){

        //if at end of string or current token is not the same as entered
        if(isAtEnd()) return false;
        if(source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    private char peek(){
        if(isAtEnd()){
            return '\0';
        }return source.charAt(current);
    }

    private boolean isDigit(char c){
        return c >= '0' && c <= '9';
    }

    private char peekNext(){
        if(current + 1 >= source.length()) return '\0';
        //peek the next value after peek() thus current + 1
        return source.charAt(current + 1);
    }

    private boolean isAlpha(char c){
        return(c >= 'a' && c <= 'z') ||
              (c >= 'A' && c <= 'Z') ||
              c == '_';
    }

    private boolean isAlphaNumeric(char c){
        return isAlpha(c) || isDigit(c);
    }
}
