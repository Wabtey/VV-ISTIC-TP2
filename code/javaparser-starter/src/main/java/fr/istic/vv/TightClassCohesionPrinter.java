package fr.istic.vv;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TightClassCohesionPrinter extends VoidVisitorWithDefaults<Void> {
    // Class,Method,Complexity
    private Map<String, Integer> methodComplexity = new HashMap<>();
    // Package,Class,TCC
    private Map<String, Double> classTCC = new HashMap<>();
    // DOT Graphviz format to represent class' method connections 
    private Map<String, String> classGraphs = new HashMap<>();

    private String className = "";
    private String packageName = "";

    @Override
    public void visit(CompilationUnit unit, Void arg) {
        unit.getPackageDeclaration().ifPresentOrElse(packageDecl -> packageName = packageDecl.getName().asString(),
                () -> packageName = "[No Package]");
        for (TypeDeclaration<?> type : unit.getTypes()) {
            type.accept(this, arg);
        }
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration declaration, Void arg) {
        className = declaration.getNameAsString();
        for (MethodDeclaration method : declaration.getMethods()) {
            method.accept(this, arg);
        }

        TightClassCohesionVisitor tccVisitor = new TightClassCohesionVisitor();
        declaration.accept(tccVisitor, null);
        // double tcc = (tccVisitor.getTCC() * 100);
        double tcc = tccVisitor.getTCC() * 100;
        classTCC.put(packageName + "," + className, tcc);
        classGraphs.put(packageName + "." + className, tccVisitor.generateTCCGraph(declaration));
    }

    @Override
    public void visit(MethodDeclaration method, Void arg) {
        String methodName = method.getNameAsString();
        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        method.accept(visitor, null);
        int complexity = visitor.getComplexity();
        methodComplexity.put(className + "," + methodName, complexity);
    }

    /* ---------------------------------- Utils --------------------------------- */
    public void generateReports(String outputPath) throws IOException {
        Path outputDir = Paths.get(outputPath);
        if (!Files.exists(outputDir)) {
            Files.createDirectories(outputDir);
        }

        /* ---------------------------- CSV Classes' TCC ---------------------------- */
        String tccReportPath = outputPath + "fullSourceTCCReport.csv";
        try (FileWriter writer = new FileWriter(tccReportPath)) {
            writer.write("Package,Class,TCC\n");

            List<Map.Entry<String, Double>> sortedEntries = new ArrayList<>(classTCC.entrySet());
            sortedEntries.sort(Map.Entry.comparingByKey());
            for (Map.Entry<String, Double> entry : sortedEntries) {
                writer.write(entry.getKey() + "," + entry.getValue() + "\n");
            }
        } catch (IOException e) {
            System.err.println("Can't write into '" + tccReportPath + "'");
            System.err.println("    " + e);
        }

        /* ----------------- DOT Class' internal method connections ----------------- */
        for (Map.Entry<String, String> entry : classGraphs.entrySet()) {
            String graphPath = outputPath + entry.getKey() + ".dot";
            try (FileWriter writer = new FileWriter(graphPath)) {
                writer.write(entry.getValue());
            } catch (IOException e) {
                System.err.println("Can't write into '" + graphPath + "'");
                System.err.println("    " + e);
            }
        }
    }
}
