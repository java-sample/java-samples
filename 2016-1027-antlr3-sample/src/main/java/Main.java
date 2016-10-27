
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.DOTTreeGenerator;

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
        //ANTLRInputStream in = new ANTLRInputStream(bais, "UTF-8");
        ANTLRStringStream in = new ANTLRStringStream(javaText);
        //ANTLRInputStream in = new ANTLRInputStream(bais);
        JavaLexer lexer = new JavaLexer(in);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JavaParser parser = new JavaParser(tokens);

        CommonTree root = parser.compilationUnit().tree;
        System.out.println("xxx: " + root.toStringTree());
        Print(root, "");
        
        //DOTTreeGenerator gen = new DOTTreeGenerator();
        ///*StringTemplate st =*/ gen.toDOT(root);
        //System.out.println(st);
    }

    static void Print(CommonTree tree, String indent) {
        System.out.println(indent + tree.toString());

        if (tree.getChildren() != null) {
            indent += "\t";

            for (Object child : tree.getChildren()) {
                CommonTree childTree = (CommonTree) child;

                if (!childTree.getText().equals("\r\n")) {
                    Print(childTree, indent);
                }
            }
        }
    }
}
