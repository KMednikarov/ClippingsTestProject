package utils;

import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class PropertyFileReader {

    public static Map<String, String> readPropFileToHashMap(String filePath) throws IOException {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        FileReader reader = new FileReader(filePath);
        Properties p = new Properties();
        p.load(reader);

        for (String key : p.stringPropertyNames()) {
            String value = p.getProperty(key);
            map.put(key, value);
        }
        return map;
    }
}
