# Using PMD

Pick a Java project from Github (see the [instructions](../sujet.md) for suggestions). Run PMD on its source code using any ruleset (see the [pmd install instruction](./pmd-help.md)). Describe below an issue found by PMD that you think should be solved (true positive) and include below the changes you would add to the source code. Describe below an issue found by PMD that is not worth solving (false positive). Explain why you would not solve this issue.

## Answer

- `pmd check -f text -R rulesets/java/quickstart.xml -d ../common-math -r report` generates [[../report]]

For a true positive:
Let's take the `line 2182`: `../commons-math/commons-math-neuralnet/src/main/java/org/apache/commons/math4/neuralnet/Network.java:47: LooseCoupling: Avoid using implementation types like 'ConcurrentHashMap'; use the interface instead`

```java
public class Network implements Iterable<Neuron> {
    /** Neurons. */
    private final ConcurrentHashMap<Long, Neuron> neuronMap = new ConcurrentHashMap<>();
    // ...
}
```

Just change `ConcurrentHashMap` to `ConcurrentMap`.

But the warning `../commons-math/commons-math-legacy/src/test/java/org/apache/commons/math4/legacy/fitting/leastsquares/MinpackTest.java:618: UnusedFormalParameter: Avoid unused constructor parameters such as 'theoreticalStartCost'.` is a "false" positive.
As this parameters could be use in the future.
