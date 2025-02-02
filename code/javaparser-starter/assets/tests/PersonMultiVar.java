// TCC = 0/3 because there is no instance variable in common in the methods
public class PersonMultiVar {
    private int age, knownAge;
    private String name;

    public String getName() {
        return name;
    }

    public int getKnownAge() {
        return knownAge;
    }

    public boolean isAdult() {
        return age > 17;
    }
}