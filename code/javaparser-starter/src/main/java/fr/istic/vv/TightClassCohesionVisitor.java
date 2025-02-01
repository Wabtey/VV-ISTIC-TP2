package fr.istic.vv;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;

import java.util.HashSet;
import java.util.Set;

public class TightClassCohesionVisitor extends VoidVisitorWithDefaults<Void> {
    private int methodPairs = 0;
    private int connectedMethodPairs = 0;
    private Set<String> classFields = new HashSet<>();
    private Set<String> methodFields = new HashSet<>();

    @Override
    public void visit(ClassOrInterfaceDeclaration declaration, Void arg) {
        classFields.clear();
        methodPairs = 0;
        connectedMethodPairs = 0;

        declaration.getFields().forEach(
                field -> field.getVariables().forEach(variable -> classFields.add(variable.getNameAsString())));

        for (MethodDeclaration method1 : declaration.getMethods()) {
            methodFields.clear();
            method1.accept(this, arg);
            for (MethodDeclaration method2 : declaration.getMethods()) {
                if (!method1.equals(method2)) {
                    methodPairs++;
                    method2.accept(this, arg);
                    if (!methodFields.isEmpty()) {
                        connectedMethodPairs++;
                    }
                }
            }
        }

        System.out.println(generateTCCGraph(declaration));
    }

    @Override
    public void visit(MethodDeclaration method, Void arg) {
        methodFields.clear();
        method.findAll(VariableDeclarator.class).forEach(variable -> {
            if (classFields.contains(variable.getNameAsString())) {
                methodFields.add(variable.getNameAsString());
            }
        });
    }

    public double getTCC() {
        return methodPairs == 0 ? 1 : (double) connectedMethodPairs / methodPairs;
    }

    public String generateTCCGraph(ClassOrInterfaceDeclaration declaration) {
        StringBuilder graph = new StringBuilder();
        graph.append("digraph G {\n");
        graph.append("  node [shape=ellipse];\n");

        for (MethodDeclaration method1 : declaration.getMethods()) {
            for (MethodDeclaration method2 : declaration.getMethods()) {
                if (!method1.equals(method2)) {
                    methodFields.clear();
                    method1.accept(this, null);
                    method2.accept(this, null);
                    if (!methodFields.isEmpty()) {
                        graph.append("  \"").append(method1.getNameAsString()).append("\" -> \"")
                                .append(method2.getNameAsString()).append("\";\n");
                    }
                }
            }
        }

        graph.append("}\n");
        return graph.toString();
    }
}
