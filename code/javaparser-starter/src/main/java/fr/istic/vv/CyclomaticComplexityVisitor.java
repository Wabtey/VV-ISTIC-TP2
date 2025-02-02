package fr.istic.vv;

import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;

/* -------------------------------------------------------------------------- */
/*                             Complexity Counter                             */
/* -------------------------------------------------------------------------- */

public class CyclomaticComplexityVisitor extends VoidVisitorWithDefaults<Void> {
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
