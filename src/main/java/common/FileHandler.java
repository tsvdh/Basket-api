package common;

import java.io.File;
import java.io.IOException;

public class FileHandler {

    public static void makeFile(File file) throws IOException {
        if (!file.exists()) {
            if (!file.getParentFile().mkdirs()) {
                throw new IOException("Could not create directories");
            } else {
                if (!file.createNewFile()) {
                    throw new IOException("Could not create the file");
                }
            }
        }
    }
}
