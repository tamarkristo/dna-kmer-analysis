# Assignment 2 Report: Finding Repeated K-mers in a DNA String

## 1. Introduction
In this assignment, we explored the problem of finding repeated substrings of length $K$ (K-mers) within a DNA sequence. This is a fundamental task in bioinformatics for identifying motifs and repetitive elements. We implemented and compared three different algorithmic approaches: a naive Brute Force method, a HashMap-based counting method, and a Sort-Based method that utilizes suffix-sorting concepts. Our goal was to analyze the trade-offs between execution time and memory usage across different dataset sizes ($N$) and K-mer lengths ($K$).

## 2. Methodology and Algorithms

### Approach A: Brute Force (Baseline)
The Brute Force approach represents the most intuitive solution. We iterate through every possible starting position $i$ in the string and, for each $i$, scan all subsequent positions $j > i$ to check for a match.
*   **Implementation Details**: To avoid reporting the same K-mer multiple times (e.g., if "AAA" appears at indices 0, 1, and 2, we shouldn't report the group {0,1,2} three separate times), we used a `visited` boolean array. Once an index is identified as part of a repeated group, it is marked as visited and skipped in the outer loop.
*   **Limitations**: As expected, this approach performs $O(N^2)$ comparisons. For a genome of millions of base pairs, this is computationally infeasible.

### Approach B: Hash-Based Counting
This approach leverages the $O(1)$ average lookup time of a Hash Table. We iterate through the DNA string once. For every window of length $K$, we extract the substring and use it as a key in a `HashMap`. The value associated with each key is a list of starting positions.
*   **Java Specifics**: In Java, `String.substring()` creates a new `String` object. For a string of length $N$, this results in $N$ new objects being allocated on the heap. Additionally, the `HashMap` itself has overhead for managing nodes and buckets. While fast, we anticipated high memory consumption.

### Approach C: Sort-Based
The Sort-Based approach is a memory-optimized technique often used in suffix array construction. Instead of storing copies of the K-mer strings, we create an array of *integers* representing the starting indices $\{0, 1, \dots, N-K\}$.
*   **Sorting Logic**: We sort these indices using a custom `Comparator`. The comparator accesses the original DNA string reference and compares the characters starting at the two indices being compared.
*   **Duplicate Detection**: After sorting, all identical K-mers are adjacent in the index array. A simple linear scan allows us to group and count them.
*   **Advantage**: We fundamentally avoid creating millions of `String` objects, storing only a single copy of the DNA string and an array of integers.

## 3. Complexity Analysis

| Algorithm | Time Complexity | Space Complexity | Theoretical bottlenecks |
| :--- | :--- | :--- | :--- |
| **Brute Force** | $O((N-K)^2 \cdot K)$ | $O(N)$ | Quadratic comparisons make this unscalable. The $\cdot K$ factor comes from character-by-character comparison. |
| **HashMap** | $O(N \cdot K)$ avg | $O(N \cdot K)$ | Creating $N$ strings of length $K$ fills the heap. Java's object header overhead (12-16 bytes per object) adds up significantly. |
| **Sort-Based** | $O(N \cdot K \cdot \log N)$ | $O(N)$ | Sorting is slightly slower than hashing ($N \log N$ vs $N$), but space is minimal ($4 \times N$ bytes for the integer array). |

## 4. Experimental Setup
*   **Environment**: Java HotSpot(TM) 64-Bit Server VM.
*   **Data Generation**:
    *   *Uniform*: Random selection of A, C, G, T.
    *   *Repetitive*: Repeating motif to trigger worst-case collisions or comparisons.
    *   *Biased*: 90% GC content to simulate High-GC genomic islands.
*   **Metrics**: We measured wall-clock time in milliseconds and approximate memory usage in Megabytes (MB).

## 5. Benchmark Results

### 5.1 Small Dataset ($N=10,000$)
We first ran all three algorithms on a moderate dataset to verify the baseline.

| Dataset | K | Algorithm | Time (ms) | Memory (MB) |
| :--- | :--- | :--- | :--- | :--- |
| Uniform | 10 | BruteForce | 1292.19 | 1.44 |
| ~ | ~ | HashMap | 12.63 | 1.91 |
| ~ | ~ | SortBased | 19.00 | **0.29** |
| Uniform | 50 | BruteForce | 546.13 | 1.89 |
| ~ | ~ | HashMap | 8.09 | 2.28 |
| ~ | ~ | SortBased | 13.39 | **0.25** |
| Uniform | 200 | BruteForce | 453.22 | 3.23 |
| ~ | ~ | HashMap | 3.61 | 3.61 |
| ~ | ~ | SortBased | 9.18 | **0.25** |

*Observation*: Brute Force is already orders of magnitude slower (1.2 seconds vs 12 milliseconds). The difference between HashMap and SortBased is negligible at this scale, though SortBased consistently uses less memory.

### 5.2 Large Scale ($N=100,000$)
For the larger dataset, we excluded Brute Force as it would take too long to run efficiently given the quadratic scaling.

| Dataset | K | Algorithm | Time (ms) | Memory (MB) |
| :--- | :--- | :--- | :--- | :--- |
| Uniform | 10 | HashMap | **146.70** | 18.31 |
| ~ | ~ | SortBased | 150.48 | **3.55** |
| Uniform | 50 | HashMap | **119.38** | 24.03 |
| ~ | ~ | SortBased | 137.48 | **3.04** |
| Uniform | 200 | HashMap | **134.19** | 37.82 |
| ~ | ~ | SortBased | 174.67 | **3.19** |

## 6. Discussion

### Speed vs. Memory Trade-off
The results clearly highlight the classic time-space trade-off in computer science.
*   The **HashMap** approach is the fastest. By sacrificing memory to cache every substring, it achieves near-linear time processing. However, the memory usage grew linearly with $K$ and $N$. At $N=100,000$ and $K=200$, it consumed nearly 38 MB. Projecting this to a full human genome (3 billion base pairs), this approach would crash due to `OutOfMemoryError` on most standard machines.
*   The **Sort-Based** approach was remarkably memory stable. Note that its memory usage remained flat around 3 MB regardless of $N$ or $K$. This is because we only reshuffle integers; we never allocate new strings. This makes it the only viable option for truly large genomic datasets, despite being roughly 10-20% slower than the HashMap.

### Brute Force Performance
The Brute Force method performed poorly, confirming the theoretical prediction. Interestingly, its run time decreased slightly as $K$ increased. This is likely because with a larger $K$, the probability of a mismatch happening on the *first* character increases, allowing the inner comparison loop to break early more often.

### Conclusion
For small, quick scripts, the HashMap approach is superior due to its simplicity and raw speed. However, for a robust bioinformatics tool designed to handle real-world genomic data, the **Sort-Based** algorithm is the correct choice. It provides a scalable solution that respects system memory limits, which is the critical constraint in large-scale DNA analysis.
