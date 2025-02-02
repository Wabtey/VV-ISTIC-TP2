# Code of your exercise

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

```xpath
//IfStatement[
    count(
        descendant::IfStatement[
            not(ancestor::IfStatement/parent::IfStatement)
        ]
    ) > 1
]
```

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
