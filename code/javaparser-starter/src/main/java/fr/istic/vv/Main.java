package fr.istic.vv;

import com.github.javaparser.utils.SourceRoot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("Should provide the path to the source code");
            System.exit(1);
        }

        File file = new File(args[0]);
        if (!file.exists() || !file.isDirectory() || !file.canRead()) {
            System.err.println("Provide a path to an existing readable directory");
            System.exit(2);
        }

        SourceRoot root = new SourceRoot(file.toPath());
        /* -------------------------- Find public elements -------------------------- */
        System.out.println("----- Find public methods -----");
        PublicElementsPrinter printer = new PublicElementsPrinter();
        root.parse("", (localPath, absolutePath, result) -> {
            result.ifSuccessful(unit -> unit.accept(printer, null));
            return SourceRoot.Callback.Result.DONT_SAVE;
        });

        /* ------------------------------- Exercise IV ------------------------------ */
        /* -------------------- Find unaccessible private fields -------------------- */
        System.out.println("-- Find unaccessible fields ---");
        ForgottenPrivateFieldsPrinter forgottenPrinter = new ForgottenPrivateFieldsPrinter();
        root.parse("", (localPath, absolutePath, result) -> {
            result.ifSuccessful(unit -> unit.accept(forgottenPrinter, null));
            return SourceRoot.Callback.Result.DONT_SAVE;
        });

        /* ------------------------------- Exercise V ------------------------------- */
        if (args.length == 2) {
            System.out.println("- report Cyclomatic Complexity -");
            CyclomaticComplexityPrinter ccPrinter = new CyclomaticComplexityPrinter();
            root.parse("", (localPath, absolutePath, result) -> {
                result.ifSuccessful(unit -> unit.accept(ccPrinter, null));
                return SourceRoot.Callback.Result.DONT_SAVE;
            });
            String outputPath = args[1];
            ccPrinter.generateReport(outputPath);
            try {
                Path path = Paths.get(outputPath);
                String content = new String(java.nio.file.Files.readAllBytes(path));
                System.out.println(content);
            } catch (IOException e) {
                System.err.println("Error reading the output file: " + e.getMessage());
            }
        }

    }
}
