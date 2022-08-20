package basket.api.util;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;

public class Util {

    public static String pathToJavaString(Path path) {
        return path.toString().replace(File.separatorChar, '/');
    }

    public static String pathToJavaString(String path) {
        return path.replace(File.separatorChar, '/');
    }

    public static String addParamsToUri(String uri, Map<String, Object> params) {
        var builder = new StringBuilder();
        builder.append(uri);

        if (!params.isEmpty()) {
            for (String key : params.keySet()) {

                char preChar;
                if (builder.indexOf("?") == -1) {
                    preChar = '?';
                } else {
                    preChar = '&';
                }

                builder.append("%s%s=%s".formatted(preChar, key, params.get(key)));
            }
        }

        return builder.toString();
    }
}
