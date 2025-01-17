/** Exercise IV */
package fr.istic.vv;

import java.util.HashSet;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;

// This class visits a compilation unit and
// prints all private variables that don't have a public getter 
public class ForgottenPrivateFieldsPrinter extends VoidVisitorWithDefaults<Void> {
    @Override
    public void visit(CompilationUnit unit, Void arg) {
        for (TypeDeclaration<?> type : unit.getTypes()) {
            type.accept(this, null);
        }
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration declaration, Void arg) {
        visitTypeDeclaration(declaration, arg);
    }

    public void visitTypeDeclaration(TypeDeclaration<?> declaration, Void arg) {
        if (!declaration.isPublic())
            return;

        System.out.println(declaration.getFullyQualifiedName().orElse("[Anonymous]"));
        Set<String> publicGetters = new HashSet<>();

        /* ---------------------------- Find all getters ---------------------------- */
        for (BodyDeclaration<?> member : declaration.getMembers()) {
            if (member instanceof MethodDeclaration) {
                MethodDeclaration method = (MethodDeclaration) member;
                if (method.isPublic() && method.getNameAsString().startsWith("get")
                        && method.getParameters().isEmpty()) {
                    publicGetters.add(method.getNameAsString().substring(3).toLowerCase());
                }
            }
        }

        // for (String getter : publicGetters) {
        //     System.out.println("- getter: " + getter);
        // }

        /* ---------------- Accept all private fields without getters --------------- */
        for (BodyDeclaration<?> member : declaration.getMembers()) {
            if (member instanceof FieldDeclaration) {
                FieldDeclaration field = (FieldDeclaration) member;
                if (field.isPrivate()) {
                    for (VariableDeclarator variable : field.getVariables()) {
                        if (!publicGetters.contains(variable.getNameAsString().toLowerCase())) {
                            // System.out.println("- var: " + variable.getNameAsString().toLowerCase());
                            variable.accept(this, arg);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void visit(VariableDeclarator varDeclarator, Void arg) {
        System.out.println("  " + "private " + varDeclarator.getTypeAsString() + " " + varDeclarator.getNameAsString());
    }

    /* -------------------------------------------------------------------------- */
    /*                            old optimized version                           */
    /* -------------------------------------------------------------------------- */

    /**
     * Optimized view of `visitTypeDeclaration`
     * 
     * @param declaration
     */
    @SuppressWarnings("unused")
    private void noVisitTypeDeclaration(TypeDeclaration<?> declaration) {
        if (!declaration.isPublic())
            return;
        Set<String> privateFields = new HashSet<>();
        Set<String> publicGetters = new HashSet<>();

        for (BodyDeclaration<?> member : declaration.getMembers()) {
            if (member instanceof FieldDeclaration) {
                FieldDeclaration field = (FieldDeclaration) member;
                if (field.isPrivate()) {
                    for (VariableDeclarator variable : field.getVariables()) {
                        privateFields.add(variable.getNameAsString());
                    }
                }
            } else if (member instanceof MethodDeclaration) {
                MethodDeclaration method = (MethodDeclaration) member;
                if (method.isPublic() && method.getNameAsString().startsWith("get")
                        && method.getParameters().isEmpty()) {
                    publicGetters.add(method.getNameAsString().substring(3).toLowerCase());
                }
            }
        }

        for (String field : privateFields) {
            if (!publicGetters.contains(field.toLowerCase())) {
                System.out.println(
                        "Class: " + declaration.getFullyQualifiedName().orElse("[Anonymous]") + ", Field: " + field);
            }
        }
    }

}
