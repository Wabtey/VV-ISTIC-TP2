package fr.istic.vv;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class CyclomaticComplexityPrinter extends VoidVisitorWithDefaults<Void> {
    private Map<String, Integer> methodComplexity = new HashMap<>();
    private String className = "";

    @Override
    public void visit(CompilationUnit unit, Void arg) {
        for (TypeDeclaration<?> type : unit.getTypes()) {
            type.accept(this, arg);
        }
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration declaration, Void arg) {
        className = declaration.getFullyQualifiedName().orElse("[Anonymous]");
        for (MethodDeclaration method : declaration.getMethods()) {
            method.accept(this, arg);
        }
    }

    @Override
    public void visit(MethodDeclaration declaration, Void arg) {
        String methodName = declaration.getNameAsString();
        int complexity = calculateCyclomaticComplexity(declaration);
        methodComplexity.put(className + "," + methodName, complexity);
    }

    private int calculateCyclomaticComplexity(MethodDeclaration method) {
        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        method.accept(visitor, null);
        return visitor.getComplexity();
    }

    public void generateReport(String outputPath) throws IOException {
        Path outputDir = Paths.get(outputPath);
        if (!Files.exists(outputDir)) {
            Files.createDirectories(outputDir);
        }

        String ccReportPath = outputPath + "cc.csv";
        try (FileWriter writer = new FileWriter(ccReportPath)) {
            writer.write("Class,Method,Complexity\n");

            List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(methodComplexity.entrySet());
            sortedEntries.sort(Map.Entry.comparingByKey());
            for (Map.Entry<String, Integer> entry : sortedEntries) {
                writer.write(entry.getKey() + "," + entry.getValue() + "\n");
            }
        } catch (IOException e) {
            System.err.println("Can't write into '" + ccReportPath + "'");
            System.err.println("    " + e);
        }
    }
}