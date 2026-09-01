package sunrise.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public final class FileStorageManager {

    private static final FileStorageManager INSTANCE = new FileStorageManager();

    private final Path dataDirectory;

    private FileStorageManager() {
        this.dataDirectory = Paths.get("data");
        try {
            Files.createDirectories(dataDirectory);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not create data directory", e);
        }
    }

    public static FileStorageManager getInstance() {
        return INSTANCE;
    }

    private Path fileFor(String fileName) {
        return dataDirectory.resolve(fileName);
    }

    public synchronized List<String> readAllLines(String fileName) {
        Path path = fileFor(fileName);
        List<String> result = new ArrayList<>();
        if (!Files.exists(path)) {
            return result;
        }
        try {
            for (String line : Files.readAllLines(path)) {
                if (!line.isBlank()) {
                    result.add(line);
                }
            }
            return result;
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read " + fileName, e);
        }
    }

    public synchronized void appendLine(String fileName, String line) {
        Path path = fileFor(fileName);
        try {
            Files.writeString(path, line + System.lineSeparator(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not write to " + fileName, e);
        }
    }

    public synchronized void rewriteAll(String fileName, List<String> lines) {
        Path path = fileFor(fileName);
        try {
            Files.write(path, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not rewrite " + fileName, e);
        }
    }

    public boolean exists(String fileName) {
        return Files.exists(fileFor(fileName));
    }
}
