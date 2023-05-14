import io.lisa.fab.AbstractRuntime;
import io.lisa.fab.Runtime;
import io.lisa.fab.config.Config;
import io.lisa.fab.io.ProjectFolder;

import java.nio.file.Path;

public class Testing {

    private static final String CONFIG_PATH = "./test/";

    public static void main(String[] args) {

        Runtime runtime = new AbstractRuntime(new ProjectFolder("test", "test")) {
            @Override
            public void processLexer() {

            }

            @Override
            public void processParser() {

            }

            @Override
            public void processPreCompiler() {

            }

            @Override
            public void processCompiler(String param) {

            }

            @Override
            public void processFinalize() {

            }

            @Override
            public Config getConfig() {
                return null;
            }

            @Override
            public void runTest(String[] args) {

            }

            @Override
            public ProjectFolder getProjectFolder() {
                return null;
            }

            @Override
            public Path getSourcePath() {
                return null;
            }
        };

        runtime.init();

    }
}
