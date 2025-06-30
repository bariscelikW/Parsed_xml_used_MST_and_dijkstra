import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.util.*;

public class AlienFlora {
    private File xmlFile;
    List<GenomeCluster> clusters = new ArrayList<>();
    Map<String, Genome> genomeMap = new HashMap<>();
    List<String[]> evolutionPairs = new ArrayList<>();
    List<String[]> adaptationPairs = new ArrayList<>();

    public AlienFlora(File xmlFile) {
        this.xmlFile = xmlFile;
    }

    public void readGenomes() {

        try {
            //parse and read
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            // find out how many genomes we have
            NodeList genomeList = document.getElementsByTagName("genome");

            for (int i = 0; i < genomeList.getLength(); i++) {

                Node genomeNode = genomeList.item(i);

                if (genomeNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element genomeElement = (Element) genomeNode;

                    // get id and evaluation factor
                    String genomeId = genomeElement.getElementsByTagName("id").item(0).getTextContent();
                    int evolutionFactor = Integer.parseInt(genomeElement.getElementsByTagName("evolutionFactor").item(0).getTextContent());

                    // create genome by above values
                    Genome genome = new Genome(genomeId, evolutionFactor);

                    // Add links
                    NodeList links = genomeElement.getElementsByTagName("link");
                    for (int j = 0; j < links.getLength(); j++) {
                        Node linkNode = links.item(j);
                        if (linkNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element linkEle = (Element) linkNode;

                            String targetId = linkEle.getElementsByTagName("target").item(0).getTextContent();
                            int adaptationFactor = Integer.parseInt(linkEle.getElementsByTagName("adaptationFactor").item(0).getTextContent());
                            //add links to genome
                            genome.addLink(targetId, adaptationFactor);
                        }
                    }
                    // add genome to genome map
                    genomeMap.put(genomeId, genome);
                }
            }

            // find cluster using iterative dfs
            //visited set
            Set<String> visited = new HashSet<>();

            for (String startId : genomeMap.keySet()) {
                if (!visited.contains(startId)) {
                    GenomeCluster cluster = new GenomeCluster();
                    Stack<String> stack = new Stack<>();
                    stack.push(startId);

                    while (!stack.isEmpty()) {
                        String genomeId = stack.pop();

                        if (visited.contains(genomeId)) {
                            continue;
                        }
                        visited.add(genomeId);

                        Genome genome = genomeMap.get(genomeId);
                        cluster.addGenome(genome);

                        // Visit neighbors by outgoing links
                        for (Genome.Link link : genome.links) {
                            String neighborId = link.target;
                            if (genomeMap.containsKey(neighborId) && !visited.contains(neighborId)) {
                                stack.push(neighborId);
                            }
                        }

                        // Visit neighbors by incoming links
                        for (Genome otherGenome : genomeMap.values()) {
                            for (Genome.Link link : otherGenome.links) {
                                if (link.target.equals(genomeId)) {
                                    if (!visited.contains(otherGenome.id)) {
                                        stack.push(otherGenome.id);
                                    }
                                }
                            }
                        }
                    }
                    // add cluster
                    clusters.add(cluster);
                }
            }

            // print output
            System.out.println("##Start Reading Flora Genomes##");
            System.out.println("Number of Genome Clusters: " + clusters.size());
            System.out.print("For the Genomes: [");
            for (int i = 0; i < clusters.size(); i++) {
                GenomeCluster cluster = clusters.get(i);
                List<String> ids = new ArrayList<>(cluster.genomeMap.keySet());

                System.out.print("[");
                for (int j = 0; j < ids.size(); j++) {
                    System.out.print(ids.get(j));
                    if (j != ids.size() - 1) {
                        System.out.print(", ");
                    }
                }
                System.out.print("]");
                if (i != clusters.size() - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println("]");
            System.out.println("##Reading Flora Genomes Completed##");

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void evaluateEvolutions() {


        try {
            // parse and read possibleEvolutionPairs
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            NodeList evolutionPairList = document.getElementsByTagName("possibleEvolutionPairs");
            if (evolutionPairList.getLength() > 0) {
                Element evolutionPairsElement = (Element) evolutionPairList.item(0);
                NodeList pairs = evolutionPairsElement.getElementsByTagName("pair");
                for (int i = 0; i < pairs.getLength(); i++) {
                    Element pairElement = (Element) pairs.item(i);
                    String firstId = pairElement.getElementsByTagName("firstId").item(0).getTextContent();
                    String secondId = pairElement.getElementsByTagName("secondId").item(0).getTextContent();
                    evolutionPairs.add(new String[]{firstId, secondId});
                }
            }
            // start printing
            System.out.println("##Start Evaluating Possible Evolutions##");
            System.out.println("Number of Possible Evolutions: " + evolutionPairs.size());

            List<Double> results = new ArrayList<>();
            int numCertified = 0;

            for (String[] pair : evolutionPairs) {
                String id1 = pair[0];
                String id2 = pair[1];

                GenomeCluster cluster1 = null;
                GenomeCluster cluster2 = null;

                for (GenomeCluster cluster : clusters) {
                    if (cluster.contains(id1)) {
                        cluster1 = cluster;
                    }
                    if (cluster.contains(id2)) {
                        cluster2 = cluster;
                    }
                }

                if (cluster1 != null && cluster2 != null && cluster1 != cluster2) {
                    Genome minGenome1 = cluster1.getMinEvolutionGenome();
                    Genome minGenome2 = cluster2.getMinEvolutionGenome();

                    if (minGenome1 != null && minGenome2 != null) {
                        double answer = (minGenome1.evolutionFactor + minGenome2.evolutionFactor) / 2.0;
                        results.add(answer);
                        numCertified++;
                    } else {
                        results.add(-1.0);
                    }
                } else {
                    results.add(-1.0);
                }
            }

            System.out.println("Number of Certified Evolution: " + numCertified);
            System.out.print("Evolution Factor for Each Evolution Pair: [");
            for (int i = 0; i < results.size(); i++) {
                if (i > 0) {
                    System.out.print(", ");
                }
                double result = results.get(i);
                System.out.print(result);
            }
            System.out.println("]");
            System.out.println("##Evaluated Possible Evolutions##");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void evaluateAdaptations() {
        try {
            //read and parse
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            // Read possibleAdaptationPairs
            NodeList adaptationPairList = document.getElementsByTagName("possibleAdaptationPairs");
            if (adaptationPairList.getLength() > 0) {
                Element adaptationPairsElement = (Element) adaptationPairList.item(0);
                NodeList pairs = adaptationPairsElement.getElementsByTagName("pair");

                for (int i = 0; i < pairs.getLength(); i++) {
                    Element pairElement = (Element) pairs.item(i);
                    String firstId = pairElement.getElementsByTagName("firstId").item(0).getTextContent();
                    String secondId = pairElement.getElementsByTagName("secondId").item(0).getTextContent();
                    adaptationPairs.add(new String[]{firstId, secondId});
                }
            }

            System.out.println("##Start Evaluating Possible Adaptations##");
            System.out.println("Number of Possible Adaptations: " + adaptationPairs.size());

            List<Integer> results = new ArrayList<>();
            int numCertified = 0;

            for (String[] pair : adaptationPairs) {
                String id1 = pair[0];
                String id2 = pair[1];

                GenomeCluster cluster1 = null;
                GenomeCluster cluster2 = null;

                for (GenomeCluster cluster : clusters) {
                    if (cluster.contains(id1)) {
                        cluster1 = cluster;
                    }
                    if (cluster.contains(id2)) {
                        cluster2 = cluster;
                    }
                }

                if (cluster1 != null && cluster2 != null && cluster1 == cluster2) {
                    int minAdaptationFactor = cluster1.dijkstra(id1, id2);
                    results.add(minAdaptationFactor);
                    if (minAdaptationFactor != -1) {
                        numCertified++;
                    }
                } else {
                    results.add(-1);
                }
            }
            System.out.println("Number of Certified Adaptations: " + numCertified);
            System.out.print("Adaptation Factor for Each Adaptation Pair: [");
            for (int i = 0; i < results.size(); i++) {
                if (i > 0) System.out.print(", ");
                System.out.print(results.get(i));
            }
            System.out.println("]");
            System.out.println("##Evaluated Possible Adaptations##");

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}


