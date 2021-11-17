package REPL;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.sound.sampled.SourceDataLine;

import evaluator.Eval;
import evaluator.enviroment.Enviroment;
import lexer.Lexer;
import parser.Parser;
import evaluator.Eval;

public class EvalTest {
    public static void main(String[] args) throws IOException {
        String filePath = "input/evalTest.NumbLang";
        Path path = Paths.get(filePath);
        byte[] data = Files.readAllBytes(path);
        String input = new String(data);

        Lexer lexer = new Lexer(input);

        Parser parser = new parser.Parser(lexer);

        Enviroment env = new Enviroment(null);

        while (!parser.isEnd())
        {
            // System.out.println(parser.getNextStatement().getString(""));
            System.out.println(Eval.Eval(parser.getNextStatement(),env).Inspect());
        }

    }
}
