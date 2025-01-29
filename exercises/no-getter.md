# No getter

With the help of JavaParser ([[../code/javaparser-starter]]) implement a program that obtains the private fields of public classes that have no public getter in a Java project.

A field has a public getter if, in the same class, there is a public method that simply returns the value of the field and whose name is `get<name-of-the-field>`.

For example, in the following class:

```Java
class Person {
    private int age;
    private String name;
    
    public String getName() { return name; }

    public boolean isAdult() {
        return age > 17;
    }
}
```

`name` has a public getter, while `age` doesn't.

The program should take as input the path to the source code of the project. It should produce a report in the format of your choice (TXT, CSV, Markdown, HTML, etc.) that lists for each detected field: its name, the name of the declaring class and the package of the declaring class.

Include in this repository the code of your application. Remove all unnecessary files like compiled binaries. See the [instructions](../sujet.md) for suggestions on the projects to use.

> [!WARNING]
> *Disclaimer* In a real project not all fields need to be accessed with a public getter.

## Answer

Run the main of javaparser-starter `Main` class with argument: `code/javaparser-starter/assets/tests/`

for these java classes `Person.java`:

```java
public class Person {
    private int age;
    private String name;

    public String getName() {
        return name;
    }

    public boolean isAdult() {
        return age > 17;
    }
}
```

and `PersonMultiVar.java`

```java
public class PersonMultiVar {
    private int age, knownAge;
    private String name;

    public String getName() {
        return name;
    }

    public int getKnowAge() {
        return knowAge;
    }

    public boolean isAdult() {
        return age > 17;
    }
}
```

it will print

```text
----- Find public methods -----
Person
  public String getName()
  public boolean isAdult()
PersonMultiVar
  public String getName()
  public int getKnowAge()
  public boolean isAdult()
-- Find unaccessible fields ---
Person
  private int age
PersonMultiVar
  private int age
  private int knownAge
```
