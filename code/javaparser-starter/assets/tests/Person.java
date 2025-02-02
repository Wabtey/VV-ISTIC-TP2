// TCC = 0/2 as each method hasn't no instance variable in common
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