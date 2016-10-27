
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class Main {

    public static void main(String[] args)
            throws Exception {
        //String javaText = "import java.lang.*;\nimport.java.util.Date;";
        //String javaText = "import.java.util.Date;";
        String javaText = "package test;\nimport.java.util.Date;\npublic class A extends B { int x=0; }";
        InputStream bais = new ByteArrayInputStream(javaText.getBytes("utf-8"));
        ANTLRInputStream in = new ANTLRInputStream(bais);
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
        /*String[] names = parser.getTokenNames();
        for (String name : names) {
            System.out.println(name);

        }*/
        //CommonTree tree = (CommonTree) parser.compilationUnit().getTree();
        //Java8Parser.CompilationUnitContext cunit = parser.compilationUnit();
        //CommonTree root = parser.compilationUnit().tree;
        //System.out.println(tree.toStringTree());
        //parser.compilationUnit();
    }

}
