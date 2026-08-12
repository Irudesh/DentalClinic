package sunrise.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton pattern: a single, shared point of access for reading and
 * writing the plain-text data files used as this system's persistence
 * layer (the brief explicitly permits "appropriate data structures and
 * text files to store information").
 *
 * Centralising file access here also gives a single place to synchronise
 * reads/writes, which matters because the HTTP server (sunrise.server)
 * may handle several requests concurrently on different threads.
 */
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

    /** Reads all non-blank lines from the given data file. Returns an empty list if it does not exist yet. */
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

    /** Appends a single line to the given data file, creating it if necessary. */
    public synchronized void appendLine(String fileName, String line) {
        Path path = fileFor(fileName);
        try {
            Files.writeString(path, line + System.lineSeparator(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not write to " + fileName, e);
        }
    }

    /** Overwrites the given data file with the supplied lines (used for update/delete operations). */
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
