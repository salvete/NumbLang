package lexer;

import java.util.Objects;

import token.Token;
import token.TokenType;

public class Lexer {
    // 输入的源代码
    public String input;
    // 当前位置
    public int pos;
    // 下一个字符位置
    public int readPos;
    // 当前字符内容
    public char ch;

    private int line = 0;
    
    public Lexer(String in)
    {
        input = in;
        ch = 0;
        pos = readPos = 0;
        line = 0;
        readChar();
    }

    public boolean isEnd()
    {
        return pos >= input.length();
    }


    public void readChar()
    {
        if (readPos >= input.length())
        {
            ch = 0;
        }
        else 
        {
            ch = input.charAt(readPos);
        }

        if (ch == '\n')
            line ++;

        pos = readPos;
        readPos += 1;
    }


    public Token nextToken()
    {
        Token res = null;
        
        //跳过所有空格
        skipWhiteSpace();


        // 已经结束
        if (isEnd())
            return new Token(TokenType.EOF, TokenType.EOF);

        String chString = String.valueOf(ch);

        // 如果出现在types或twoOpr中，那么则说明这个字符为
        if (TokenType.types.containsKey(chString) || TokenType.twoOpr.contains(chString))
        {
            readChar();

            //判断是否出现类似 ==, >=, <=, ||, && 之类的两个字节操作符

            String candidate = chString + String.valueOf(ch);

            // System.out.println(candidate);


            // 为注释的情况
            if (Objects.equals(candidate, "//"))
            {
                singleLine();
                res = new Token(TokenType.COMMENT, TokenType.COMMENT,line);
            }
            else if (Objects.equals(candidate, "/*"))
            {
                readCommentsStateOne();
                res = new Token(TokenType.COMMENT,TokenType.COMMENT,line);
            }
            // 为其他关键字或者非法的情况
            else
            {
                if (TokenType.types.containsKey(candidate))
                {
                    chString = candidate;
                    readChar();
                }    

                if (TokenType.types.containsKey(chString))
                    res = new Token(TokenType.types.get(chString),chString,line);
                // 可能出现 |&, &|, !&之类的错误
                else
                    res = new Token(TokenType.ILLEGAL, chString,line);
            }
        }   
        else
        {
            // 关键字或者标识符
            if (isLetter(ch))
            {
               String word = readIdentifier();

               if (TokenType.types.containsKey(word))
               {
                   res = new Token(TokenType.types.get(word), word,line);
               }
               else
               {
                   res = new Token(TokenType.IDEN, word,line);
               }
            }
            // 数字
            else if (isDigit(ch))
            {
                String[] ans = readNumber();

                res = new Token(ans[1], ans[0],line);
            }
            // 字符串
            else if (ch == '\"')
            {
                String[] ans = readString();
                res = new Token(ans[1], ans[0],line);
            }
            else
            {
                res = new Token(TokenType.ILLEGAL, chString,line);
                readChar();
            }
        }   

        return res;
    }


    
    // 工具函数
    private boolean isLetter(char ch)
    {
        return ('a'<= ch && ch <='z') || ('A' <= ch && ch <= 'Z') || ch == '_';
    }

    /**
     * @return 读取的标识符或者关键字
     */
    private String readIdentifier()
    {
        int beginPos = pos;
        while (isLetter(ch))
            readChar();
        return input.substring(beginPos,pos);
    }

    private void skipWhiteSpace()
    {
        if ((ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r'))
        {
           

            while (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r')
            {
                readChar();
            }
        }
        
    }

    private boolean isDigit(char ch)
    {
        return '0' <= ch && ch <= '9';
    }

    private String[] readNumber()
    {
        String[] ans = new String[2];

        int begin = pos;
        int end = -1;
        boolean isInteger = true;

        while (isDigit(ch))
        {
            readChar();

            if (isInteger &&  ch == '.')
            {
                readChar();
                isInteger = false;
            }
        }
        
        end = pos;

        ans[0] = input.substring(begin,end);
        if (isInteger)
            ans[1] = TokenType.NUMBER;
        else
            ans[1] = TokenType.FLOAT;

        return ans;
    }


    private String[] readString()
    {

        String[] ans = new String[2];

        readChar();

        int begin = pos;
        int end = -1;

        while (ch != '\"')
        {
            readChar();
        }


        end = pos;

        ans[0] = input.substring(begin,end);
        ans[1] = TokenType.STRING;


        // 把 " 读掉
        readChar();

        return ans;
    }

    private char peekChar()
    {
        if (readPos >= input.length())
            return 0;
        else
            return input.charAt(readPos);
    }


    // 单行注释
    private void singleLine()
    {
        while ( ch != '\n')
            readChar();
    }


    //多行注释的第一个状态
    private void readCommentsStateOne()
    {

        if (isEnd())
            return;

        while (!isEnd() && ch != '*')
        {
            readChar();
        }
        readCommentsStateTwo();
    }
    // 多行注释的第二个状态
    private void readCommentsStateTwo()
    {
        if (isEnd())
            return;

        while (ch == '*')
            readChar();
        if (ch == '/')
        {
            readChar();
            return;
        }
        else
            readCommentsStateOne();
    }
}
