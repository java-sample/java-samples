// https://gist.github.com/bkiers/91827bdfa2b97679568c
/*
 * Copyright (c) 2014 by Bart Kiers
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
//import org.antlr.v4.runtime.ANTLRInputStream;
//import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.misc.Interval;
import org.bouncycastle.util.Arrays;

/**
 * A small class that flattens an ANTLR4 {@code ParseTree}. Given the
 * {@code ParseTree}:
 *
 * <pre>
 * <code>
 * a
 * '-- b
 * |   |
 * |   '-- d
 * |       |
 * |       '-- e
 * |           |
 * |           '-- f
 * |
 * '-- c
 * </code>
 * </pre>
 *
 * This class will flatten this structure as follows:
 *
 * <pre>
 * <code>
 * a
 * '-- b
 * |   |
 * |   '-- f
 * |
 * '-- c
 * </code>
 * </pre>
 *
 * In other word: all inner nodes that have a single child are removed from the
 * AST.
 */
public class AST {

    public static class Context {

        private final Parser parser;
        private final String code;
        //private final byte[] code;

        public Context(Parser parser, String code) {
        //public Context(Parser parser, byte[] code) {
            this.parser = parser;
            this.code = code;
        }
    }

    private final Context context;
    public final ParseTree node;
    /**
     * The payload will either be the name of the parser rule, or the token of a
     * leaf in the tree.
     */
    private final Object payload;

    /**
     * All child nodes of this AST.
     */
    private final List<AST> children;

    public AST(Context context, ParseTree tree) {
        this(null, context, tree);
    }

    private AST(AST ast, Context context, ParseTree tree) {
        this(ast, context, tree, new ArrayList<AST>());
    }

    private AST(AST parent, Context context, ParseTree tree, List<AST> children) {

        this.context = context;
        this.node = tree;
        this.payload = getPayload(tree);
        this.children = children;

        if (parent == null) {
            // We're at the root of the AST, traverse down the parse tree to fill
            // this AST with nodes.
            walk(tree, this);
        } else {
            parent.children.add(this);
        }
    }

    public Object getPayload() {
        return payload;
    }

    public List<AST> getChildren() {
        return new ArrayList<>(children);
    }

    // Determines the payload of this AST: a string in case it's an inner node (which
    // is the name of the parser rule), or a Token in case it is a leaf node.
    private /*static*/ Object getPayload(ParseTree tree) {
        /*if (tree.getChildCount() == 0) {
            // A leaf node: return the tree's payload, which is a Token.
            return tree.getPayload();
        }*/
        if (tree.getPayload() instanceof Token) {
            // A leaf node: return the tree's payload, which is a Token.
            return tree.getPayload();
        } else {
            // The name for parser rule `foo` will be `FooContext`. Strip `Context` and
            // lower case the first character.
            String ruleName = tree.getClass().getSimpleName().replace("Context", "");
            return Character.toLowerCase(ruleName.charAt(0)) + ruleName.substring(1);
        }
    }

    private static boolean containsTokens(ParseTree node) {
        if (node.getPayload() instanceof Token) {
            return true;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            ParseTree child = node.getChild(i);
            if (containsTokens(child)) {
                return true;
            }
        }
        return false;
    }

    // Fills this AST based on the parse tree.
    private static void walk(ParseTree tree, AST ast) {

        //System.out.println(String.valueOf(AST.getPayload(tree)) + ":" + tree.getText() + " = " + tree.getPayload().getClass().getName() + " @" + (containsTokens(tree) ? "true" : "false"));
        if (tree.getChildCount() == 0) {
            // We've reached a leaf. We must create a new instance of an AST because
            // the constructor will make sure this new instance is added to its parent's
            // child nodes.
            new AST(ast, null, tree);
        } /*else if (tree.getChildCount() == 1) {
            // We've reached an inner node with a single child: we don't include this in
            // our AST.
            walk(tree.getChild(0), ast);
        } */ else /*if (tree.getChildCount() > 1)*/ {

            for (int i = 0; i < tree.getChildCount(); i++) {

                ParseTree child = tree.getChild(i);

                if (!containsTokens(child)) {
                    continue;
                }

                //System.out.println(String.valueOf(AST.getPayload(child)) + ":" + child.getText() + " = " + child.getPayload().getClass().getName() + " @" + (containsTokens(child) ? "true" : "false"));
                AST temp = new AST(ast, null, tree.getChild(i));

                if (!(temp.payload instanceof Token)) {
                    //if ((temp.payload instanceof String)) {
                    // Only traverse down if the payload is not a Token.
                    walk(tree.getChild(i), temp);
                }
            }
        }
    }

    private static List<Token> getAllTokens(AST ast, List<Token> tokens) {
        if (tokens == null) {
            tokens = new ArrayList<>();
        }
        if (ast.payload instanceof Token) {
            tokens.add((Token) ast.payload);
        } else {
            for (AST child : ast.children) {
                tokens = getAllTokens(child, tokens);
            }
        }
        return tokens;
    }

    private static int getTokenStart(AST ast) {
        List<Token> tokens = getAllTokens(ast, null);
        if(tokens.isEmpty()) return 0;
        return tokens.get(0).getStartIndex();
    }

    private static int getTokenStop(AST ast) {
        List<Token> tokens = getAllTokens(ast, null);
        if(tokens.isEmpty()) return 0;
        return tokens.get(tokens.size()-1).getStopIndex();
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();

        AST ast = this;
        List<AST> firstStack = new ArrayList<>();
        firstStack.add(ast);

        List<List<AST>> childListStack = new ArrayList<>();
        childListStack.add(firstStack);

        while (!childListStack.isEmpty()) {

            List<AST> childStack = childListStack.get(childListStack.size() - 1);

            if (childStack.isEmpty()) {
                childListStack.remove(childListStack.size() - 1);
            } else {
                ast = childStack.remove(0);
                String caption;

                if (ast.payload instanceof Token) {
                    Token token = (Token) ast.payload;
                    /*
                    caption = String.format("TOKEN[type: %s, text: %s]",
                            token.getType(), token.getText().replace("\n", "\\n"));
                     */
                    String symbolicName = "" + token.getType();
                    if (token.getType() < 0) {
                        symbolicName = "EOF";
                    } else //tokenName = this.parser.getTokenNames()[token.getType()];
                    {
                        symbolicName = this.context.parser.getVocabulary().getSymbolicName(token.getType());
                    }
                    caption = String.format("TOKEN[type: %s, text: %s]", symbolicName, token.getText().replace("\n", "\\n"));
                    //caption = String.format("TOKEN[type: %s, text: %s, %d-%d]", symbolicName, token.getText().replace("\n", "\\n"), token.getStartIndex(), token.getStopIndex());
                    //String text = this.context.code.substring(token.getStartIndex(), token.getStopIndex() + 1);
                    //caption = String.format("TOKEN[type: %s, text: %s, %d-%d=%s]", symbolicName, token.getText().replace("\n", "\\n"), token.getStartIndex(), token.getStopIndex(), text);
                } else {
                    caption = String.valueOf(ast.payload);
                    ////caption = String.valueOf(ast.payload) + " [" + ast.payload.getClass().getName() + "]";
                    //Interval interval = ast.node.getSourceInterval();
                    //caption = String.valueOf(ast.payload) + " <== " + ast.node.getText() + " " + interval.a + "->" + interval.b; //.replace("\n","\\n");
                    caption = String.valueOf(ast.payload) + " <== " + this.context.code.substring(AST.getTokenStart(ast), AST.getTokenStop(ast)+1).replace("\r\n", "\n").replace("\n", "\\n");
                    /*
                    caption = String.valueOf(ast.payload) + " <== "
                            + my.IOUTils.newUtf8String(
                                    Arrays.copyOfRange(this.context.code, AST.getTokenStart(ast), AST.getTokenStop(ast)+1))
                                    .replace("\r\n", "\n").replace("\n", "\\n")
                            ;*/
                }

                String indent = "";

                for (int i = 0; i < childListStack.size() - 1; i++) {
                    indent += (childListStack.get(i).size() > 0) ? "|  " : "   ";
                }

                builder.append(indent)
                        .append(childStack.isEmpty() ? "`- " : "|- ")
                        .append(caption)
                        .append("\n");

                if (ast.children.size() > 0) {
                    List<AST> children = new ArrayList<>();
                    for (int i = 0; i < ast.children.size(); i++) {
                        children.add(ast.children.get(i));
                    }
                    childListStack.add(children);
                }
            }
        }

        return builder.toString();
    }

    /*
    public static void main(String[] args) {

        // Generate the parser and lexer classes below using the grammar available here:
        // https://github.com/bkiers/python3-parser
        Python3Lexer lexer = new Python3Lexer(new ANTLRInputStream("f(arg1='1')\n"));
        Python3Parser parser = new Python3Parser(new CommonTokenStream(lexer));

        ParseTree tree = parser.file_input();
        AST ast = new AST(tree);

        System.out.println(ast);

        // Output:
        //
        //    '- file_input
        //       |- stmt
        //       |  |- small_stmt
        //       |  |  |- atom
        //       |  |  |  '- TOKEN[type: 35, text: f]
        //       |  |  '- trailer
        //       |  |     |- TOKEN[type: 47, text: (]
        //       |  |     |- arglist
        //       |  |     |  |- test
        //       |  |     |  |  '- TOKEN[type: 35, text: arg1]
        //       |  |     |  |- TOKEN[type: 53, text: =]
        //       |  |     |  '- test
        //       |  |     |     '- TOKEN[type: 36, text: '1']
        //       |  |     '- TOKEN[type: 48, text: )]
        //       |  '- TOKEN[type: 34, text: \n]
        //       '- TOKEN[type: -1, text: <EOF>]
    }*/
}
