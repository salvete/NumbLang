package parser;


import java.util.LinkedList;
import java.util.Objects;
import ast.BlockStatement;
import ast.CallStatement;
import ast.ExpressionStatement;
import ast.FunctionStatement;
import ast.IdentifierStatement;
import ast.IfStatement;
import ast.LetStatement;
import ast.ReturnStatement;
import ast.interfaces.Statement;
import lexer.Lexer;
import token.Token;
import token.TokenType;

public class Parser {
    private Lexer lexer;
    private Token curToken;
    private Token peekToken;

    public Parser(Lexer l)
    {
        lexer = l;
        nextToken();
        nextToken();
    }

    private void nextToken()
    {
        curToken = peekToken;
        peekToken = lexer.nextToken();
    }

    public boolean isEnd()
    {
        return lexer.isEnd();
    }


    /**
    * The grammer of NumLang:
    * 
    * program ::= <program-block>
    * program-block ::= <block>
    * block ::= {sigle-statement}*
    * sigle-statement =[ [<let-statementn> | <callFunction-Statement> | 
    *   <function-definition>(和callFunction有冲突) | <expression-statement> | <return-statement> | <identifier> ';' (和callFunction可能有冲突)]
    *   | [<if-statment>]] 
    * 
    *  Let Statement
    * let-Statement ::= 'let' <identifier> '=' [callFunctio-Statement | function-definition | expression-statement | identifier]
    * 
    *  Function Invacation:
    * callFunction-Statement ::= [<identifier> | <function-definition> ] '('<parameters>')'
    * parameters ::= {<expresson-statement> | callFunction}*
    * 
    *  If-else Statement:
    * if-statement ::= 'if' '{' <program-block> '}'  'else' '{' <program-block> '}'
    * 
    *  Function Definition Statement:
    * function-definition ::= 'fn' '(' <arguments> ')' '{' <function-body> '}' 
    * <arguments> ::= [<identifier> | expression-statament | callFunction-statement] {',' [<identifier> | expression-statament | callFunction-statement] }*
    * <function-body> ::= <program-block>
    * 
    *  Return Statement:
    * <return-statment> := 'return' [function]
    *  Expression Statement:
    * <expression-statement> ::= <compare> '==' <expression-statement> | <compare>  '!=' <expression-statement> | <compare>
    * <compare> ::=  <arithmetic> '>' <compare> | <arithmetic> '<' <compare> | <arithmetic> 
    * <arithmetic> ::= <priority-arithmetic> '+' <arithmetic> | <priority-arithmetic> '-' <arithmetic> | <priority-arithmetic> 
    * <priority-arithmetic> ::= <number> '*' <priority-arithmetic> | <number> '/' <priority-arithmetic> | <number>
    * <number> ::= {~}[<integer> | <float> | true | false | (<expression-statement>)] | identifier | callfunction-statement
    * 
    *  Number or Identifier:
    * <unsigned integer> ::= <digit> | <unsigned integer> <digit>
    * <integer> ::= +<unsigned integer>  | ~<unsigned integer> | <unsigned integer>
    * <identifier> ::= <letter> | <idenfitier> < digit> | <identifier> <letter>
    * <float> := <digit>'.'<digit> 
    *
     @return Statement
    */

    public Statement getNextStatement()
    {
        
        if (lexer.isEnd())
            return null;
        else
        {
            Statement s = null;

            // Comment
            if (checkType(curToken.type, TokenType.COMMENT))
                nextToken();

            if (checkType(curToken.type, TokenType.LET))
            {
                s = parseLetStatement();
                
                /**
                 * To check ';'
                 */
                if (!checkSemicolon())
                    s = null;
                
            }
            else if (checkType(curToken.type, TokenType.RETURN))
            {
                s = parseReturnStatement();

                if (!checkSemicolon())
                    s = null;

            }
            else if (checkType(curToken.type, TokenType.IF))
            {
                s = parseIfStatement();
            }
            else
            {
                s = parseExpressionStatement();
                
                if (!checkSemicolon())
                    s = null;
            }

            return s;
        }

    }

    /**
     * To parse Let Statement
     * @return
     */
    private Statement parseLetStatement()
    {
        /**
         * To make sure curToken is the keyword 'Let'.
         */
        if (!checkType(curToken.type, TokenType.LET))
        {
            error(curToken.type, TokenType.LET, curToken.pos);
            return null;
        }

        LetStatement letStatement = new LetStatement();

        letStatement.token = new Token(curToken);

        nextToken();

        if (!checkType(curToken.type, TokenType.IDEN))
        {
            error(curToken.type, TokenType.IDEN, curToken.pos);
            letStatement = null;
            return null;
        }

        /**
         * let a = b, so the name is a.
         */
        letStatement.name = new Token(curToken);

        nextToken();

        if (! checkType(curToken.type, TokenType.ASSIGN))
        {
            error(curToken.type, TokenType.IDEN, curToken.pos);
            letStatement = null;
            return null;
        }

        nextToken();

        Statement value = null;

        switch(curToken.type)
        {
            case TokenType.LET : value = parseLetStatement();break;
            case TokenType.IF: value = parseIfStatement();break;
            case TokenType.RETURN : value = parseReturnStatement();break;
            default: value = parseExpressionStatement(); 
        }


        letStatement.value = value;

        return letStatement;
    }

    /**
     * Parse identifier.
     * @return
     */
    private Statement parseIdentifier()
    {
        if (!checkType(curToken.type, TokenType.IDEN))
        {
            error(curToken.type, TokenType.IDEN, curToken.pos);
            return null;
        }

    
        IdentifierStatement identifierStatement = new IdentifierStatement(curToken);

        Statement res = identifierStatement;

        nextToken();

        if (checkType(curToken.type, TokenType.LPAREN))
        {
            CallStatement callStatement = new CallStatement();
            callStatement.function = identifierStatement;
            callStatement.arguments = parseArguments();
            res = callStatement;
        }

        return res;
    }

    /**
     * Parse arguments like '(1,2,3,a,b,c)'.
     * @return
     */
    private LinkedList<Statement> parseArguments()
    {
        if (!checkType(curToken.type, TokenType.LPAREN))
        {
            error(curToken.type, TokenType.LPAREN, curToken.pos);
            return null;
        }

        LinkedList<Statement>  res = new LinkedList<>();

        nextToken();

        while (!checkType(curToken.type, TokenType.RPAREN))
        {
            Statement s = parseExpressionStatement();

            if (!(s instanceof IdentifierStatement || s instanceof ExpressionStatement || s instanceof CallStatement))
            {
                error(s.getType(), "Identifier, Expression or Function Invocation", curToken.pos);
                res = null;
                return null;
            }

            res.add(s);

            if (checkType(curToken.type, TokenType.RPAREN))
                break;

            if (!checkType(curToken.type, TokenType.COMMA))
            {
                error(curToken.type, TokenType.SEMICOLON, curToken.pos);
                res = null;
                return null;
            }

            nextToken();

        }

        nextToken();

        return res;
    }

    /**
     * Parse IfStatement like:
     * if (a > b)
     * {
     *   ......
     * }
     * else
     * {
     *  .......
     * }
     * 
     * @return
     */
    private Statement parseIfStatement()
    {
        if (!checkType(curToken.type, TokenType.IF))
        {
            error(curToken.type, TokenType.IF, curToken.pos);
            return null;
        }

        IfStatement ifStatement = new IfStatement();

        ifStatement.token = new Token(curToken);

        nextToken();

        if (!checkType(curToken.type, TokenType.LPAREN))
        {
            error(curToken.type, TokenType.LPAREN, curToken.pos);
            ifStatement = null;
            return null;
        }

        ifStatement.condiction = parseExpressionStatement();

        ifStatement.ifCondiction = parseBlockStatement();

        if (checkType(curToken.type, TokenType.ELSE))
        {
            nextToken();
            ifStatement.elseCondition = parseBlockStatement();
        }
        else
        {
            ifStatement.elseCondition = null;
        }

        return ifStatement;

    }

    /**
     * Parse BlockStatements like:
     * {
     *    let a = b;
     *    let c = 5;
     *    ......
     * }
     * @return
     */
    private BlockStatement parseBlockStatement()
    {
        if (!checkType(curToken.type, TokenType.LBRACE))
        {
            error(curToken.type, TokenType.LBRACE, curToken.pos);
            return null;
        }

        nextToken();

        BlockStatement bls = new BlockStatement();

        while (!checkType(curToken.type, TokenType.RBRACE))
        {
            Statement s = getNextStatement();
            bls.statements.add(s);
        }

        // Make sure that curToken is '}'
        if (!checkType(curToken.type, TokenType.RBRACE))
        {
            error(curToken.type, TokenType.RBRACE, curToken.pos);
            bls = null;
            return null;
        }

        nextToken();

        return bls;
    }

    /**
     * Parse FunctionStatement like:
     * fn(x,y)
     * {
     *    ......
     * }
     * @return
     */
    private Statement parseFunctionStatement()
    {
        if (!checkType(curToken.type, TokenType.FUNCTION))
        {
            error(curToken.type, TokenType.FUNCTION, curToken.pos);
            return null;
        }

        Statement res = null;
        FunctionStatement functionStatement = new FunctionStatement();

        nextToken();

        functionStatement.parameters = parseArguments();

        // According to the gramma.
        if (!checkType(curToken.type, TokenType.LBRACE))
        {
            error(curToken.type, TokenType.LBRACE, curToken.pos);
            functionStatement = null;
            return null;
        }

        functionStatement.body = parseBlockStatement();

        res = functionStatement;

        /**
         * To check whether it is a function invocation statement.
         */
        if (checkType(curToken.type, TokenType.LPAREN))
        {
            CallStatement callStatement = new CallStatement();
            callStatement.function = functionStatement;
            callStatement.arguments = parseArguments();
            res = callStatement;
        }

        return res;
    }

    /**
     * Parse ReturnStatement like:
     * return a + b * c;
     * @return
     */
    private Statement parseReturnStatement()
    {
        if (!checkType(curToken.type, TokenType.RETURN))
        {
            error(curToken.type, TokenType.RETURN, curToken.pos);
            return null;
        }

        ReturnStatement returnStatement = new ReturnStatement();
        returnStatement.token = new Token(curToken);
        nextToken();

        returnStatement.returnValue = parseExpressionStatement();

        return returnStatement;
    }

    /**
     * Parse Expressions.
     * In the following patern:
     * A  := B A'
     * A' := == B A' | != BA' | null
     * B  := C B'
     * B' := > C B' | < C B' | null
     * C  := D C'
     * C' := +DC' | -DC' | null
     * D  := ED'
     * D' := *ED' | /ED' | null
     * E  := (A) | num | iden | ~E
     * @return
     */

    private Statement parseExpressionStatement()
    {
        return A();
    }

    /**
     * Top layer.
     * @return
     */
    private Statement A()
    {
        ExpressionStatement root = new ExpressionStatement();
        root.left = B();

        Statement tmp = A_1(root);

        return tmp == null? root.left : tmp;

    }

    /**
     * Second top.
     * @param root 
     * @return
     */
    private Statement A_1(ExpressionStatement root)
    {
        if (checkType(curToken.type, TokenType.EQU) || checkType(curToken.type, TokenType.NEQU))
        {
            root.opr = new Token(curToken);
            nextToken();
            root.right = B();
            ExpressionStatement root1 = new ExpressionStatement();
            root1.left = root;
            Statement tmp  = A_1(root1);

            return tmp == null ? root : tmp;
        }
        else
            return null;
    }

    private Statement B()
    {
        ExpressionStatement root = new ExpressionStatement();
        root.left = C();
        Statement tmp = B_1(root);

        return tmp == null ? root.left : tmp;

    }

    private Statement B_1(ExpressionStatement root)
    {
        if (checkType(curToken.type, TokenType.GT) || checkType(curToken.type, TokenType.LT))
        {
            root.opr = new Token(curToken);
            nextToken();
            root.right = C();
            ExpressionStatement root1 = new ExpressionStatement();
            root1.left = root;
            Statement tmp  = B_1(root1);

            return tmp == null ? root : tmp;

        }
        else
            return null;
    }

    private Statement C()
    {
        ExpressionStatement root = new ExpressionStatement();
        root.left = D();
        Statement tmp = C_1(root);

        return tmp == null ? root.left : tmp;
    }

    private Statement C_1(ExpressionStatement root)
    {
        if (checkType(curToken.type, TokenType.ADD) || checkType(curToken.type, TokenType.SUB))
        {
            root.opr = new Token(curToken);
            nextToken();
            root.right = D();
            ExpressionStatement root1 = new ExpressionStatement();
            root1.left = root;
            Statement tmp  = C_1(root1);

            return tmp == null ? root : tmp;
        }
        else
            return null;
    }

    private Statement D()
    {
        ExpressionStatement root = new ExpressionStatement();
        root.left = E();
        Statement tmp = D_1(root);

        return tmp == null ? root.left : tmp;
    }

    private Statement D_1(ExpressionStatement root)
    {
        if (checkType(curToken.type, TokenType.MUL) || checkType(curToken.type, TokenType.DIV))
        {
            root.opr = new Token(curToken);
            nextToken();
            root.right = E();
            ExpressionStatement root1 = new ExpressionStatement();
            root1.left = root;
            Statement tmp  = D_1(root1);

            return tmp == null ? root : tmp;
        }
        else
            return null;
    }


    private Statement E()
    {
        Statement res = null;

        if (checkType(curToken.type, TokenType.OPPOSITE))
        {
            ExpressionStatement ops = new ExpressionStatement();
            ops.opr = new Token(curToken);
            nextToken();
            ops.left = E();
            ops.right = null;
            res = ops;
        }
        else 
        {
            if (checkType(curToken.type, TokenType.NUMBER) || checkType(curToken.type, TokenType.FLOAT)||
                checkType(curToken.type, TokenType.TRUE) || checkType(curToken.type, TokenType.FALSE))
            {
                ExpressionStatement expressionStatement = new ExpressionStatement();
                expressionStatement.opr = new Token(curToken);
                nextToken();
                expressionStatement.left = expressionStatement.right = null;
                expressionStatement.isLeaf = true;
                res = expressionStatement;
            }
            else if (checkType(curToken.type, TokenType.IDEN))
            {
                res = parseIdentifier();
            }
            else if (checkType(curToken.type, TokenType.FUNCTION))
            {
                res = parseFunctionStatement();
            }
            else if (checkType(curToken.type, TokenType.LPAREN))
            {
                nextToken();

                res = A();

                if (!checkType(curToken.type, TokenType.RPAREN))
                {
                    error(curToken.type, TokenType.RPAREN, curToken.pos);
                    res = null;
                }
                
                nextToken();
            }
            else
                error(curToken.type, "Number, Identifier or '('", curToken.pos);

        }

        

        return res;
    }


    /**
     * To check wether one type equals to another.
     * @param typeA  Type one.
     * @param typeB  Type two.
     * @return
     */
    private boolean checkType(String typeA, String typeB)
    {
        return Objects.equals(typeA, typeB);
    }

    private void error(String curType, String expectType, int pos)
    {
        
        System.out.println("[ERROR] Line " + pos + ": " + "Current type:" + curType + " Expect type:" + expectType);
    }

    private boolean checkSemicolon()
    {
        if (!checkType(curToken.type,TokenType.SEMICOLON))
        {
            error(curToken.type, TokenType.SEMICOLON, curToken.pos);
            return false;
        }
        nextToken();
        return true;
    }

}




