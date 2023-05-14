package io.lisa.fab.source;


import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Scanner;

public class SourceFile {

    private final Path javaPath;
    private final String path;

    private String source;
    private byte[] bytes;
    private int length;

    public SourceFile(String path) {
        this.path = path;

        if (path == null)
            throw new EmptySourceFileException("path is null");

        this.javaPath = Path.of(path);
    }

    public void initialize() {
        final Path currentPath = Path.of(this.path);
        final File ioFile = currentPath.toFile();

        if (!ioFile.exists())
            throw new EmptySourceFileException(path);

        final Optional<String> extension = resolveExtension(path);

        if (extension.isEmpty())
            throw new FileWithoutSuffixException(this.path);

        try {
            Scanner scanner = new Scanner(ioFile)
                    .useDelimiter("\\A");

            if (!scanner.hasNext())
                return;

            this.source = scanner.next();
            this.bytes = source.getBytes(StandardCharsets.UTF_8);
            this.length = bytes.length;

            scanner.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public String getFileName() {
        final String[] pathSplit = path.split("/");
        return pathSplit[pathSplit.length - 1].split("\\.")[0];
    }

    private Optional<String> resolveExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    public String getPath() {
        return path;
    }

    public String getSource() {
        return source;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public int getLength() {
        return length;
    }

    public Path getJavaPath() {
        return javaPath;
    }
}


