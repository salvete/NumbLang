package REPL;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import ast.interfaces.Statement;
import evaluator.Eval;
import evaluator.enviroment.Enviroment;
import lexer.Lexer;
import token.Token;
import parser.Parser;
import java.util.*;


public class REPL {
    
    public static void main(String[] args) throws IOException {

        if (args.length == 2 && Objects.equals(args[0], "-f"))
        {
            try
            {
                Path path = Paths.get(args[1]);
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
            }catch(Exception e)
            {
                System.out.println(e);
            }
        }
        else{
            Scanner sc = new Scanner(System.in);
        
            Lexer l = new Lexer();

            Parser parser = new Parser(l);

            Enviroment env = new Enviroment(null);
            
            System.out.print(">> ");
            while (sc.hasNext())
            {
                try
                {
                    
                    String code = sc.nextLine();
                    // System.out.println("code:"+code);
                    l.addCode(code);
                    Statement stmt = parser.getNextStatement();

                    System.out.println(Eval.Eval(stmt, env).Inspect());
                }
                catch(Exception e)
                {
                    System.out.println(e);
                }

                System.out.print(">> ");
            }
        } 
    }

}
