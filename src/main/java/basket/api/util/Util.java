package basket.api.util;

import java.io.File;
import java.nio.file.Path;

public class Util {

    public static String pathToJavaPath(Path path) {
        return path.toString().replace(File.separatorChar, '/');
    }
}
