package common;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class FileHandler {

    // This class should not be instantiated
    private FileHandler() {}

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

    public static void deletePathAndContent(File file) throws IOException {
        deletePathAndContent(file.toPath());
    }

    public static void deletePathAndContent(Path path) throws IOException {
        if (!Files.exists(path)) {
            return;
        }
        //noinspection ResultOfMethodCallIgnored
        Files.walk(path)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }
}
