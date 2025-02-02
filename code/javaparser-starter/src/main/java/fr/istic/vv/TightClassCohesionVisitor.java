package fr.istic.vv;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;
import com.github.javaparser.ast.expr.NameExpr;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TightClassCohesionVisitor extends VoidVisitorWithDefaults<Void> {
    // binomial coefficient of the # of connectedMethodPairs in the # of methods
    private int methodPairs = 0;
    private int connectedMethodPairs = 0;
    private Set<String> classFields = new HashSet<>();
    // OPTIMIZE: have only a `Set<String>` that you reset after each method visited used to compare with other potential connected methods
    private Map<String, Set<String>> methodsFields = new HashMap<>();

    @Override
    public void visit(ClassOrInterfaceDeclaration declaration, Void arg) {
        // System.out.println("---------- " + declaration.getNameAsString() + " ----------");
        methodPairs = 0;
        connectedMethodPairs = 0;

        // retrieve all instance variables (but the statics)
        declaration.getFields().forEach(field -> {
            if (!field.isStatic()) {
                field.getVariables().forEach(variable -> classFields.add(variable.getNameAsString()));
            }
        });

        // retrieve all methods all the instance variables they are using
        for (MethodDeclaration method : declaration.getMethods()) {
            method.accept(this, arg);
        }

        // System.out.println(methodsFields);

        for (MethodDeclaration method1 : declaration.getMethods()) {
            for (MethodDeclaration method2 : declaration.getMethods()) {
                if (!method1.equals(method2)) {
                    methodPairs++;
                    Set<String> variablesFromM1 = methodsFields.get(method1.getNameAsString());
                    Set<String> variablesFromM2 = methodsFields.get(method2.getNameAsString());
                    if (unionNotEmpty(variablesFromM1, variablesFromM2)) {
                        connectedMethodPairs++;
                    }
                }
            }
        }

        // System.out.println(generateTCCGraph(declaration));
    }

    /**
     * For each method visited, fill the `methodsFields` with `(method,
     * allTheInstanceVariablesItUses)`
     */
    @Override
    public void visit(MethodDeclaration method, Void arg) {
        // System.out.println("----- " + method.getNameAsString() + " -----");
        /* ----------------------- init each set of variables ----------------------- */
        Set<String> emptyVariableSet = methodsFields.get(method.getNameAsString());
        if (emptyVariableSet == null) {
            emptyVariableSet = new HashSet<>();
        }
        methodsFields.put(method.getNameAsString(), emptyVariableSet);

        // for each variable in the method, check if there are any instance variable
        method.findAll(NameExpr.class).forEach(variable -> {
            // System.out.println("  * " + variable.getNameAsString());
            Set<String> instanceVariablesUsed = methodsFields.get(method.getNameAsString());
            if (classFields.contains(variable.getNameAsString())) {
                instanceVariablesUsed.add(variable.getNameAsString());
            }
            methodsFields.put(method.getNameAsString(), instanceVariablesUsed);
        });
    }

    public double getTCC() {
        return methodPairs == 0 ? 0 : (double) connectedMethodPairs / methodPairs;
    }

    public String generateTCCGraph(ClassOrInterfaceDeclaration declaration) {
        StringBuilder graph = new StringBuilder();
        graph.append("strict graph {\n");
        graph.append("  label=\"");
        graph.append(declaration.getNameAsString());
        graph.append("\"\n");

        Set<String> addedEdges = new HashSet<>();

        for (MethodDeclaration method1 : declaration.getMethods()) {
            for (MethodDeclaration method2 : declaration.getMethods()) {
                if (!method1.equals(method2)) {
                    Set<String> variablesFromM1 = methodsFields.get(method1.getNameAsString());
                    Set<String> variablesFromM2 = methodsFields.get(method2.getNameAsString());
                    Set<String> sharedVariables = new HashSet<>(variablesFromM1);
                    sharedVariables.retainAll(variablesFromM2);
                    if (!sharedVariables.isEmpty()) {
                        String edge = method1.getNameAsString().compareTo(method2.getNameAsString()) < 0
                                ? "\"" + method1.getNameAsString() + "\" -- \"" + method2.getNameAsString() + "\""
                                : "\"" + method2.getNameAsString() + "\" -- \"" + method1.getNameAsString() + "\"";
                        if (!addedEdges.contains(edge)) {
                            graph.append("  ").append(edge).append(" [label=\"")
                                    .append(String.join(", ", sharedVariables)).append("\"];\n");
                            addedEdges.add(edge);
                        }
                    }
                }
            }
        }

        graph.append("}\n");
        return graph.toString();
    }

    /* ---------------------------------- Utils --------------------------------- */
    private boolean unionNotEmpty(Set<String> variablesM1, Set<String> variablesM2) {
        for (String variableM1 : variablesM1) {
            boolean union = variablesM2.contains(variableM1);
            if (union) {
                return true;
            }
        }
        return false;

    }
}
