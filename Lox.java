package com.craftinginterpreters.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox{
    static boolean hadError = false;

    public static void main(String[] args) throws IOException{
        if(args.length > 1){
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        }else if(args.length == 1){
            runFile(args[0]);
        }else{
            runPrompt();
        }
    }

    //starting lox from a cmd line and path, reads and executes file
    private static void runFile(String path) throws IOException{
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        //indicate error in the exit code
        if(hadError) System.exit(65);
    }

    //run interactively, prompt to enter and execute  code one line at a time
    //Ctrl-D to exit
    private static void runPrompt() throws IOException{
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        //infinite loop. Same as while(true)
        for(;;){
            System.out.print("> ");
            String line = reader.readLine();
            if(line == null) break;
            run(line);

            //reset flag so entire session is not killed if mistake is made
            hadError = false;
        }
    }

    //core function
    private static void run(String source){
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        for(Token token : tokens){
            System.out.println(token);
        }
    }

    //error handling
    //int line as line number
    static void error(int line, String message){
        report(line, "", message);
    }

    private static void report(int line, String where, String message){
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }
}
