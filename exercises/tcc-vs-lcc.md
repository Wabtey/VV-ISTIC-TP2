# TCC *vs* LCC

Explain under which circumstances *Tight Class Cohesion* (TCC) and *Loose Class Cohesion* (LCC) metrics produce the same value for a given Java class. Build an example of such as class and include the code below or find one example in an open-source project from Github and include the link to the class below. Could LCC be lower than TCC for any given class? Explain.

A refresher on TCC and LCC is available in the [course notes](https://oscarlvp.github.io/vandv-classes/#cohesion-graph).

## Answer

LCC could never be lower than TCC. `LCC = TCC + indirect connections`.

> TCC is defined as the ratio of directly connected pairs of node in the graph to the number or all pairs of nodes. On its side, LCC is the number of pairs of connected (directly or indirectly) nodes to all pairs of node.

To represent a case `TCC = LCC`, we can modify the [first example](https://oscarlvp.github.io/vandv-classes/#cohesion-graph) to only include `sub` and `dot` methods

```java
class Point {

    private double x, y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // no getters :)

    public double dot(Point p) {
        return x*p.x + y*p.y;
    }

    public Point sub(Point p) {
        return new Point(x - p.x, y - p.y);
    }

}
```

I almost represent the TCC in a submodule of a [rust video game](https://github.com/Fabinistere/fabien-et-la-trahison-de-olf/tree/main/src/characters) (simplifying Queries and Resources as whole variables)...
