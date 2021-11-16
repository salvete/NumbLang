package REPL;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


import lexer.Lexer;
import token.Token;
import parser.Parser;

public class REPL {
    
    public static void main(String[] args) throws IOException {
        String filePath = "input/parserTest.NumbLang";
        Path path = Paths.get(filePath);
        byte[] data = Files.readAllBytes(path);
        String input = new String(data);

        Lexer lexer = new Lexer(input);

        Parser parser = new parser.Parser(lexer);

        while (!parser.isEnd())
        {
            System.out.println(parser.getNextStatement().getString(""));
        }

    }

}
