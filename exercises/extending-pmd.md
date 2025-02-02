# Extending PMD

Use XPath to define a new rule for PMD to prevent complex code. The rule should detect the use of three or more nested `if` statements in Java programs so it can detect patterns like the following:

```Java
if (...) {
    ...
    if (...) {
        ...
        if (...) {
            ....
        }
    }

}
```

Notice that the nested `if`s may not be direct children of the outer `if`s. They may be written, for example, inside a `for` loop or any other statement.
Write below the XML definition of your rule.

You can find more information on extending PMD in the following link: <https://pmd.github.io/latest/pmd_userdocs_extending_writing_rules_intro.html>, as well as help for using `pmd-designer` [here](./designer-help.md).

Use your rule with different projects and describe you findings below. See the [instructions](../sujet.md) for suggestions on the projects to use.

## Answer

test1:

```java
public class Test {
    public void main() {
        boolean condition = false;
        if (!condition) {
            // alo
            condition = !condition;
            if(condition) {
                System.out.println("weird");
                if(true) {
                    System.out.println("good job!");
                }
            }
        }
        System.out.println("weird adventure...");   
    }
}
```

test2:

```java
public class Test {
    public void main() {
        boolean condition = false;
        if (!condition) {
            // alo
            condition = !condition;
            if(condition) {
                System.out.println("weird");
                for (int i=0; i<100; i++) {
                    if(true) {
                        System.out.println("good job!");
                    }
                }
            }
        }
        System.out.println("weird adventure...");   
    }
}
```

to detect 3 nested if:

```xpath
//IfStatement[
    count(
        descendant::IfStatement[
            not(ancestor::IfStatement/parent::IfStatement)
        ]
    ) > 1
]
```

or simpler:

```xpath
//IfStatement//IfStatement//IfStatement
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<rule name="AvoidExcessivelyNestedIfStatements"
      language="java"
      message="Avoid using more than two nested if statements"
      class="net.sourceforge.pmd.lang.rule.xpath.XPathRule">
    <description>
        This rule detects Java code with more than two levels of nested if statements,
        helping to prevent overly complex conditional logic.
    </description>
    <priority>2</priority>
    <properties>
        <property name="xpath">
            <value>
//IfStatement//IfStatement//IfStatement
            </value>
        </property>
    </properties>
    <example>
        <![CDATA[
    // Violating example
    public void complexMethod() {
        if (condition1) {
            if (condition2) {
                if (condition3) {
                    // Too many nested if statements
                }
            }
        }
    }

// Compliant example
public void simpleMethod() {
    if (condition1) {
        if (condition2) {
            // Maximum of two nested if statements
        }
    }
}
        ]]>
    </example>
</rule>
```
