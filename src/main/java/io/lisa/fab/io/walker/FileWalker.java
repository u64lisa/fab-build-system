package io.lisa.fab.io.walker;

import io.lisa.fab.io.ProjectFolder;

public abstract class FileWalker {

    public abstract void walk(ProjectFolder folder, String[] structure);

    public static FileWalker create() {
        return new DefaultFileWalker();
    }

}
