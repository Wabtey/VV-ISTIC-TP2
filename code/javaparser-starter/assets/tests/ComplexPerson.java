/**
 * @formatter:off
 * Total possible method pairs: (7 parmi 2) = 21.
 * Connected pairs = 8.
 * TCC = 8/21 ~= 38.09%
 */
public class ComplexPerson {
    private int age;
    private String name;
    // FIXME: should not be counted as instance variable as it is `static`
    public static final List<String> belongings;

    /**
     * CC = 1.
     * `name` shared with `printDetails`, `updateDetails` and `showAllBelongings`
     * @return name
     */
    public String getName() {
        return name;
    }

    // CC = 1
    public boolean isAdult() {
        return age > 17;
    }

    // CC = 3
    public void printDetails() {
        if (name != null) {
            System.out.println("Name: " + name);
        } else {
            System.out.println("Name not provided");
        }

        if (age > 0) {
            System.out.println("Age: " + age);
        } else {
            System.out.println("Age not provided");
        }
    }

    // CC = 3
    public void updateDetails(String newName, int newAge) {
        if (newName != null && !newName.isEmpty()) {
            name = newName;
        }

        if (newAge > 0) {
            age = newAge;
        }
    }

    // CC = 1
    // For TCC, no direct instance variable access
    public boolean canVote() {
        return isAdult();
    }

    // CC = 2
    public void showAllBelongings() {
        System.out.println(name + " got:");
        for (String belonging : belongings) {
            System.out.println("* " + belonging);
        }
    }

    // CC = 4
    public int countPlasticCups() {
        int cupCount = 0;

        bool coolLogic = false;
        if (coolLogic) {
            System.out.println("elo");
        } else {
            for (String belonging : belongings) {
                if (belonging.equals("Plastic cup")) {
                    cupCount++;
                    System.out.println("* " + belonging);
                }
            }
        }
        return cupCount;
    }
}