
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class Main {

    public static void main(String[] args)
            throws Exception {
        //InputStream is = Main.class.getResourceAsStream("/ParseTest/ParseTest.java");
        //String javaText = org.apache.commons.io.IOUtils.toString(is, "UTF-8");
        //InputStream bais = new ByteArrayInputStream(javaText.getBytes("utf-8"));
        
        String javaText = my.IOUTils.readUtf8StringFromFileInClassPath("/ParseTest/ParseTest.java");
        InputStream bais = new ByteArrayInputStream(javaText.getBytes("utf-8"));
        
        //byte[] javaText = my.IOUTils.readAllBytesFromFileInClassPath("/ParseTest/ParseTest.java");
        //InputStream bais = new ByteArrayInputStream(javaText);
        
        Reader r = new InputStreamReader(bais, "UTF-8"); // e.g., euc-jp or utf-8
        ANTLRInputStream in = new ANTLRInputStream(r);
        //ANTLRInputStream in = new ANTLRInputStream(bais);
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
