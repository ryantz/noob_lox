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

            //for cases where the character is not recognised
            default:
                Lox.error(line, "Unexpected character.");
                break;
        }
    }

    //helper function that tells us if all characters are consumed
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
}
