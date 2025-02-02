# Code of [[../exercises/jp-cc]]

run the main class with this two arguments `code/javaparser-starter/assets/tests/ code/javaparser-starter/assets/tests/report.csv` to generate:

```csv
Class,Method,Complexity
ComplexPerson,canVote,1
ComplexPerson,countPlasticCups,4
ComplexPerson,getName,1
ComplexPerson,isAdult,1
ComplexPerson,printDetails,3
ComplexPerson,showAllBelongings,2
ComplexPerson,updateDetails,3
Person,getName,1
Person,isAdult,1
PersonMultiVar,getKnowAge,1
PersonMultiVar,getName,1
PersonMultiVar,isAdult,1
UntestableCode,complexMethod,37
```

From all java test classes: [../javaparser-starter/assets/tests/](../javaparser-starter/assets/tests/)

The code of [`CyclomaticComplexityPrinter.java`](../javaparser-starter/src/main/java/fr/istic/vv/CyclomaticComplexityPrinter.java):

```java
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
        try (FileWriter writer = new FileWriter(outputPath)) {
            writer.write("Class,Method,Complexity\n");

            List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(methodComplexity.entrySet());
            sortedEntries.sort(Map.Entry.comparingByKey());
            for (Map.Entry<String, Integer> entry : sortedEntries) {
                writer.write(entry.getKey() + "," + entry.getValue() + "\n");
            }
        }
    }
}

/* -------------------------------------------------------------------------- */
/*                             Complexity Counter                             */
/* -------------------------------------------------------------------------- */

class CyclomaticComplexityVisitor extends VoidVisitorWithDefaults<Void> {
    private int complexity = 0;

    @Override
    public void visit(MethodDeclaration method, Void arg) {
        complexity++;
        method.getBody().ifPresent(body -> {
            for (Statement stmt : body.getStatements()) {
                stmt.accept(this, arg);
            }
        });
    }

    @Override
    public void visit(BlockStmt block, Void arg) {
        for (Statement stmt : block.getStatements()) {
            stmt.accept(this, arg);
        }
    }

    @Override
    public void visit(IfStmt n, Void arg) {
        complexity++;
        n.getThenStmt().accept(this, arg);
        n.getElseStmt().ifPresent(elseStmt -> elseStmt.accept(this, arg));
    }

    @Override
    public void visit(ForStmt n, Void arg) {
        // System.out.println("FOR");
        complexity++;
        n.getBody().accept(this, arg);
    }

    @Override
    public void visit(ForEachStmt n, Void arg) {
        // System.out.println("FOREACH");
        complexity++;
        n.getBody().accept(this, arg);
    }

    @Override
    public void visit(WhileStmt n, Void arg) {
        complexity++;
        n.getBody().accept(this, arg);
    }

    @Override
    public void visit(DoStmt n, Void arg) {
        complexity++;
        n.getBody().accept(this, arg);
    }

    @Override
    public void visit(SwitchEntry n, Void arg) {
        if (!n.getLabels().isEmpty()) {
            complexity++;
        }
        for (Statement switchStmt : n.getStatements())
            switchStmt.accept(this, arg);
    }

    @Override
    public void visit(CatchClause n, Void arg) {
        complexity++;
        n.getBody().accept(this, arg);
    }

    public int getComplexity() {
        return complexity;
    }
}

```
