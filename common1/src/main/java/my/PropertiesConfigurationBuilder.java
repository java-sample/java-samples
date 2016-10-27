package my;

import java.io.File;
import java.net.URL;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class PropertiesConfigurationBuilder {

    public static PropertiesConfiguration getConfiguration(String fileName) throws ConfigurationException {
        return PropertiesConfigurationBuilder.getConfiguration(new File(fileName));
    }

    public static PropertiesConfiguration getConfiguration(File file) throws ConfigurationException {
        File absFile = file.getAbsoluteFile();
        if (!absFile.exists()) {
            absFile.getParentFile().mkdirs();
            long timestamp = System.currentTimeMillis();
            absFile.setLastModified(timestamp);
        }
        final PropertiesConfiguration config = new PropertiesConfiguration(absFile);
        config.setEncoding("UTF-8");
        return config;
    }

    public static PropertiesConfiguration getConfiguration(URL url) throws ConfigurationException {
        final PropertiesConfiguration config = new PropertiesConfiguration(url);
        config.setEncoding("UTF-8");
        return config;
    }

}
