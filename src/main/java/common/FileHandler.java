package common;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileHandler {

    public static void makeFile(File file) throws IOException { // TODO: test relative path creation
        makeFile(file.toPath());
    }

    public static void makeFile(Path path) throws IOException {
        String fileName = path.getFileName().toString();
        if (!fileName.contains(".")) {
            throw new IllegalArgumentException("The given path must point to a file");
        }

        Files.createDirectories(path.getParent());
        try {
            Files.createFile(path);
        } catch (FileAlreadyExistsException ignored) {}
    }
}
