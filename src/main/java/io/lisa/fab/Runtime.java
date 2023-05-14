package io.lisa.fab;

import io.lisa.fab.config.Config;
import io.lisa.fab.io.ProjectFolder;

import java.nio.file.Path;

public interface Runtime {

     String DEFAULT_CONFIG_NAME = "system.f";
     String DEFAULT_SOURCE_FOLDER = "/src/";
     String DEFAULT_BUILD_FOLDER = "/build/";
     String[] STRUCTURE = {
            "src/",
            "resources/",
            "build/"
    };

    int init();

    void processLexer();

    void processParser();

    void processPreCompiler();

    void processCompiler(final String param);

    void processFinalize();

    Config getConfig();

    void runTest(String[] args);

    ProjectFolder getProjectFolder();

    Path getSourcePath();

}
