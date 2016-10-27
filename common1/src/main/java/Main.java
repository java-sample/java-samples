
import com.google.common.base.Charsets;
import org.apache.commons.configuration.PropertiesConfiguration;

public class Main {

    public static void main(String[] args) throws Exception {
        PropertiesConfiguration props = my.PropertiesConfigurationBuilder.getConfiguration("/home/javagame/store/temp/temp.props");
        props.setProperty("a", "b");
        props.setProperty("x", "漢字");
        props.setProperty("y", "漢字".getBytes(Charsets.UTF_8));
        props.save();
        Runtime.getRuntime().exec("explorer C:\\home\\javagame\\store");
    }

}
