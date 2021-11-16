package REPL;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import lexer.Lexer;
import token.Token;
import token.TokenType;

public class LexerTest {
    public static void main(String[] args) throws IOException {
        String filePath = "input/lexerTest.NumbLang";
        Path path = Paths.get(filePath);
        byte[] data = Files.readAllBytes(path);
        String input = new String(data);

        Lexer lexer = new Lexer(input);

        Token t = lexer.nextToken();

        while (!Objects.equals(t.type, TokenType.EOF))
        {
            System.out.println(t.toString());
            t = lexer.nextToken();
        }

    }
}
