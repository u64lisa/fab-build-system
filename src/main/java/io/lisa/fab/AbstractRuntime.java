package io.lisa.fab;

import io.lisa.fab.config.Config;
import io.lisa.fab.io.ProjectFolder;
import io.lisa.fab.io.walker.FileWalker;

import java.nio.file.Path;

public abstract class AbstractRuntime implements Runtime {

    private final FileWalker walker = FileWalker.create();
    private final ProjectFolder projectFolder;
    private final Config config;

    private Path sourcePath;

    public AbstractRuntime(final ProjectFolder projectFolder) {
        this.projectFolder = projectFolder;
        this.config = new Config(projectFolder.projectRoot(), DEFAULT_CONFIG_NAME);
    }

    @Override
    public int init() {
        walker.walk(this.projectFolder, STRUCTURE);
        config.load();

        return 1; // todo implement codes for create, alread there and such
    }
}
