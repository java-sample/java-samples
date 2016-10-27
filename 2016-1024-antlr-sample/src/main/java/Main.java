
import java.io.StringReader;
import java.util.List;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

///import org.rpgleparser.utils.TreeUtils;
public class Main {

    public static void main(String[] args)
            throws Exception {

        //byte[] javaText = my.IOUTils.readAllBytesFromFileInClassPath("/ParseTest/ParseTest.java");
        //InputStream bais = new ByteArrayInputStream(javaText);
        String javaText = my.IOUTils.readUtf8StringFromFileInClassPath("/ParseTest/ParseTest.java");

        //InputStream bais = new ByteArrayInputStream(javaText.getBytes("utf-8"));
        //Reader r = new InputStreamReader(bais, "UTF-8"); // e.g., euc-jp or utf-8
        //ANTLRInputStream in = new ANTLRInputStream(r);
        //ANTLRStringStream in = new ANTLRStringStream(javaText);
        StringReader r = new StringReader(javaText);
        ANTLRInputStream in = new ANTLRInputStream(r);

        Java8Lexer lexer = new Java8Lexer(in);
        CommonTokenStream tokens
                = new CommonTokenStream(lexer);
        Java8Parser parser = new Java8Parser(tokens);
        ParseTree tree = parser.compilationUnit();
        AST.Context context = new AST.Context(parser, javaText);
        AST ast = new AST(context, tree);

        System.out.println(ast);

        System.out.println(Java8Parser.IMPORT);
        System.out.println(Java8Parser.EOF);

        MyVisitor visitor = new MyVisitor();
        visitor.visit(tree);

        System.out.println(tree.toStringTree(parser));
        ppParseTree(tree, parser);
    }

    private static void print(String s) {
        System.out.print(s);
    }

    private static void println(String s) {
        System.out.println(s);
    }

    private static void println() {
        System.out.println();
    }

    public static void ppParseTree(ParseTree tree, Parser parser) {
        ppParseTree(tree, parser, 0, true);
    }

    private static void ppParseTree(ParseTree tree, Parser parser, int level, boolean isLast) {
        for (int i = 0; i < level; i++) {
            if (i == 0) {
                print("   ");
            } else {
                print("|  ");
            }
        }
        if (isLast) {
            print("`- ");
        } else {
            print("|- ");
        }
        if (tree.getPayload() instanceof Token) {
            //System.out.println("is token");
            print("'");
            print(tree.getText());
            print("'");
            println();
            return;
        }
        //System.out.println("not token");
        String ruleName = tree.getClass().getSimpleName().replaceAll("Context$", "");
        ruleName = Character.toLowerCase(ruleName.charAt(0)) + ruleName.substring(1);
        println(ruleName);
        for (int i = 0; i < tree.getChildCount(); i++) {
            ParseTree child = tree.getChild(i);
            ppParseTree(child, parser, level + 1, i == tree.getChildCount() - 1);
        }
    }

}

class MyVisitor extends Java8BaseVisitor<Void> {

    @Override
    public Void visitSingleTypeImportDeclaration(Java8Parser.SingleTypeImportDeclarationContext ctx) {
        List<ParseTree> children = ctx.children;
        System.out.println("visitSingleTypeImportDeclaration: children.size()" + children.size());
        for (ParseTree child : children) {
            System.out.println(child.getClass().getName());
        }

        return super.visitSingleTypeImportDeclaration(ctx); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Void visitImportDeclaration(Java8Parser.ImportDeclarationContext ctx) {
        List<ParseTree> children = ctx.children;
        System.out.println("visitImportDeclaration: children.size()" + children.size());
        return super.visitImportDeclaration(ctx); //To change body of generated methods, choose Tools | Templates.
    }

}
