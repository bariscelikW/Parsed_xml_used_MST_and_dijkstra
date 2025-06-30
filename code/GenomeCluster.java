import java.util.*;

public class GenomeCluster {
    public Map<String, Genome> genomeMap = new HashMap<>();

    public void addGenome(Genome genome) {
        genomeMap.put(genome.id, genome);
    }

    public boolean contains(String genomeId) {
        return genomeMap.containsKey(genomeId);
    }

    public Genome getMinEvolutionGenome() {
        if (genomeMap.isEmpty()) {
            return null;
        }

        Genome minGenome = null;
        int minEvolutionFactor = Integer.MAX_VALUE;

        //O(N) solution :(
        for (Genome genome : genomeMap.values()) {
            if (genome.evolutionFactor < minEvolutionFactor) {
                minEvolutionFactor = genome.evolutionFactor;
                minGenome = genome;
            }
        }
        return minGenome;

    }

    public int dijkstra(String startId, String endId) {
        // not in the genome map
        if (!genomeMap.containsKey(startId) || !genomeMap.containsKey(endId)) {
            return -1;
        }

        // helper map for keeping distances
        Map<String, Integer> distances = new HashMap<>();
        for (String id : genomeMap.keySet()) {
            distances.put(id, Integer.MAX_VALUE);
        }
        distances.put(startId, 0);

        // create pq, and the pq compares Genome Objects by distance
        PriorityQueue<GenomeDistance> pq = new PriorityQueue<>(Comparator.comparingInt(gd -> gd.dist));
        pq.add(new GenomeDistance(startId, 0));

        while (!pq.isEmpty()) {
            GenomeDistance curr = pq.poll();
            String currId = curr.genomeId;
            int currDistance = curr.dist;

            // if current id equals end id return distance
            if (currId.equals(endId)) {
                return currDistance;
            }

            Genome currGenome = genomeMap.get(currId);
            // update pq by neighbours of current genome
            for (Genome.Link link : currGenome.links) {
                String neighborId = link.target;
                int newDistance = currDistance + link.adaptationFactor;

                if (genomeMap.containsKey(neighborId) && newDistance < distances.get(neighborId)) {
                    distances.put(neighborId, newDistance);
                    pq.add(new GenomeDistance(neighborId, newDistance));
                }
            }
        }
        // if they are not in the same cluster, return -1
        return -1;
    }
    // Small helper class
    private static class GenomeDistance {
        String genomeId;
        int dist;

        public GenomeDistance(String genomeId, int distance) {
            this.genomeId = genomeId;
            this.dist = distance;
        }
    }
}
