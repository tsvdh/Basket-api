package common;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class FileHandler {

    // This class should not be instantiated
    private FileHandler() {}

    public static void makeFile(File file) throws IOException { // TODO: test relative path creation
        makeFile(file.toPath());
    }

    public static void makeFile(Path path) throws IOException {
        if (!Files.isRegularFile(path)) {
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

    public static void deletePathAndContent(Path toDelete) throws IOException {
        if (!Files.exists(toDelete)) {
            return;
        }

        Files.walkFileTree(toDelete, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (exc == null) {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                } else {
                    throw exc;
                }
            }
        });
    }

    public static void copyPathAndContent(Path source, Path destination) throws IOException {
        if (!Files.exists(source)) {
            return;
        }

        deletePathAndContent(destination);

        Files.walkFileTree(source, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Files.createDirectory(destination.resolve(source.relativize(dir)));
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.copy(file, destination.resolve(source.relativize(file)));
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
