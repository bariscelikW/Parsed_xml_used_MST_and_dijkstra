# 🌱 FloraX

## 🧬 Overview

In this assignment, you take on the role of a lead scientist tasked with analyzing alien flora genomes from another planet. The task involves XML parsing, graph algorithms, and implementing key computational techniques to identify genome clusters, evaluate evolutionary potential, and determine adaptation paths.

The project is divided into three main parts:

---

## 📌 Parts

### 🔹 Part 1: Identify Genomes

- Parse an XML file to construct genome graphs.
- Detect and group genomes into clusters based on their links.
- Output the number of genome clusters and their genome IDs.

**Goal:** Identify and structure all genome clusters using graph traversal.

---

### 🔹 Part 2: Find the Minimum Energy to Evolve

- For each `possibleEvolutionPair`, determine if the two genomes belong to different clusters.
- If they do, compute the average of the minimum evolution factors from each cluster.

**Formula:**

(EvolutionFactor of Genome1's cluster minimum + Genome2's cluster minimum) / 2

- If both genomes are in the same cluster, return `-1`.

---

### 🔹 Part 3: Find Possible Adaptations

- For each `possibleAdaptationPair`, check if genomes are in the same cluster.
- If yes, compute the shortest adaptation path using **Dijkstra’s Algorithm**.
- If not, return `-1`.

**Goal:** Determine the minimum adaptation factor between related genomes.

---

## 🧪 Sample Output

##Start Reading Flora Genomes##

Number of Genome Clusters: 2

For the Genomes: [[G103, G102, G104], [G202, G201]]

##Reading Flora Genomes Completed##

##Start Evaluating Possible Evolutions##

Number of Possible Evolutions: 2

Number of Certified Evolution: 1

Evolution Factor for Each Evolution Pair: [9.5, -1.0]

##Evaluated Possible Evolutions##

##Start Evaluating Possible Adaptations##

Number of Possible Adaptations: 2

Number of Certified Adaptations: 1

Adaptation Factor for Each Adaptation Pair: [-1, 7]

##Evaluated Possible Evolutions##

## 💻 How to Compile & Run

```bash
javac *.java -d .
java Main <AlienFlora>
```

bariscelik
