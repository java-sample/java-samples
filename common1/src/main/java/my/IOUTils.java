package my;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IOUTils {
    public static String newUtf8String(byte[] bytes)
    {
        try {
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(IOUTils.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }
    public static byte[] readAllBytesFromFileInClassPath(String path) {
        InputStream is = IOUTils.class.getResourceAsStream(path);
        try {
            return org.apache.commons.io.IOUtils.toByteArray(is);
        } catch (IOException ex) {
            return null;
        }
    }
    public static String readUtf8StringFromFileInClassPath(String path) {
        InputStream is = IOUTils.class.getResourceAsStream(path);
        try {
            return org.apache.commons.io.IOUtils.toString(is, "UTF-8");
        } catch (IOException ex) {
            return null;
        }
    }
}
