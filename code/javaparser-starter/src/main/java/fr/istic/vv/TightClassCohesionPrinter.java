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

public class TightClassCohesionPrinter extends VoidVisitorWithDefaults<Void> {
    // Class,Method,Complexity
    private Map<String, Integer> methodComplexity = new HashMap<>();
    // Package,Class,TCC
    private Map<String, Integer> classTCC = new HashMap<>();

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
        int tcc = (int) (tccVisitor.getTCC() * 100);
        classTCC.put(packageName + "," + className, tcc);
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
        try (FileWriter writer = new FileWriter(outputPath + "fullSourceTCCReport.csv")) {
            writer.write("Package,Class,TCC\n");

            List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(classTCC.entrySet());
            sortedEntries.sort(Map.Entry.comparingByKey());
            for (Map.Entry<String, Integer> entry : sortedEntries) {
                writer.write(entry.getKey() + "," + entry.getValue() + "\n");
            }
        }
    }
}
