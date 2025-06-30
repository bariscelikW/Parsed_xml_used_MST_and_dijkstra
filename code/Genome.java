import java.util.*;

public class Genome {
    public String id;
    public int evolutionFactor;
    public List<Link> links;

    public Genome(String id, int evolutionFactor) {
        this.id = id;
        this.evolutionFactor = evolutionFactor;
        this.links = new ArrayList<>();
    }

    public void addLink(String target, int adaptationFactor) {
        Link newGenome = new Link(target, adaptationFactor);
        links.add(newGenome);
    }

    public static class Link {
        public String target;
        public int adaptationFactor;

        public Link(String target, int adaptationFactor) {
            this.target = target;
            this.adaptationFactor = adaptationFactor;
        }
    }
}
