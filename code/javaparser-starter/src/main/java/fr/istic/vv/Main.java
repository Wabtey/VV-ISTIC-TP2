package fr.istic.vv;

import com.github.javaparser.utils.SourceRoot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length != 0 && args.length != 2) {
            System.err.println("Should provide the path to the source code and the path to the report file");
            System.exit(1);
        }

        Scanner scanner = new Scanner(System.in);
        String projectPath = "";
        String reportPath = "";

        if (args.length == 0) {

            while (projectPath.isEmpty()) {
                System.out.println("Do you want to use the test assets or your own project?");
                System.out.println("1. Test assets");
                System.out.println("2. Own project");

                int choice = scanner.nextInt();
                switch (choice) {
                case 1:
                    projectPath = "code/javaparser-starter/assets/tests/";
                    break;
                case 2:
                    System.out.println("Enter the path to your project:");
                    projectPath = scanner.next();
                    break;
                default:
                    System.out.println("Invalid choice");
                    System.exit(1);
                }
            }

            while (reportPath.isEmpty()) {
                System.out.println("Enter the path for the generated report:");
                reportPath = scanner.next();
            }
        } else {
            projectPath = args[0];
            reportPath = args[1];
        }

        File file = new File(projectPath);
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
        System.out.println("- report Cyclomatic Complexity -");
        CyclomaticComplexityPrinter ccPrinter = new CyclomaticComplexityPrinter();
        root.parse("", (localPath, absolutePath, result) -> {
            result.ifSuccessful(unit -> unit.accept(ccPrinter, null));
            return SourceRoot.Callback.Result.DONT_SAVE;
        });
        ccPrinter.generateReport(reportPath);

        String answer = "";
        while (!answer.equalsIgnoreCase("y") && !answer.equalsIgnoreCase("n")) {
            System.out.print("Print the CC of every methods (y/N): ");
            answer = scanner.nextLine();

            if (answer.isEmpty()) {
                answer = "n";
            }
        }

        if (answer.equalsIgnoreCase("y")) {
            try {
                Path path = Paths.get(reportPath);
                String content = new String(Files.readAllBytes(path));
                System.out.print(content);
            } catch (IOException e) {
                System.err.println("Error reading the output file: " + e.getMessage());
            }
        }

        /* ------------------------------- Exercise VI ------------------------------ */
        /* -------------------------- Tight Class Cohesion -------------------------- */
        System.out.println("----- Tight Class Cohesion -----");

        scanner.close();
    }
}
