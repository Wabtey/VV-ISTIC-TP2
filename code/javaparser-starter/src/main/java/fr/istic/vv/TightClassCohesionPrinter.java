package fr.istic.vv;

import java.util.HashMap;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;

public class TightClassCohesionPrinter extends VoidVisitorWithDefaults<Void> {
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
    public void visit(MethodDeclaration method, Void arg) {
        String methodName = method.getNameAsString();
        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        method.accept(visitor, null);
        int complexity = visitor.getComplexity();
        methodComplexity.put(className + "," + methodName, complexity);
    }
}
