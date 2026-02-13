package dna;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("Starting DNA K-mer Analysis...");

        // 1. Verification
        System.out.println("\n--- Verification Phase ---");
        verifyCorrectness();

        // 2. Benchmarking
        System.out.println("\n--- Benchmarking Phase ---");
        runBenchmarks();
    }

    private static void verifyCorrectness() {
        String testDna = "ACGACGTACG";
        // ACG starts at 0, 3, 7.
        // CGA starts at 1, 4.
        // GAC starts at 2, 5.
        // CGT starts at 4. (Wait, ACG ACG T ACG - indices: 012 345 6 789)
        // 0: ACG
        // 1: CGA
        // 2: GAC
        // 3: ACG (repeat)
        // 4: CGT
        // 5: GT A
        // 6: TAC
        // 7: ACG (repeat)
        // Repeats: ACG (0,3,7).

        int k = 3;

        KmerFinder brute = new BruteForceFinder();
        KmerFinder map = new HashMapFinder();
        KmerFinder sort = new SortBasedFinder();

        List<KmerResult> r1 = brute.findRepeatedKmers(testDna, k);
        List<KmerResult> r2 = map.findRepeatedKmers(testDna, k);
        List<KmerResult> r3 = sort.findRepeatedKmers(testDna, k);

        System.out.println("Brute Force Results: " + r1);
        System.out.println("HashMap Results:     " + r2);
        System.out.println("Sort Results:        " + r3);

        if (areResultsEqual(r1, r2) && areResultsEqual(r2, r3)) {
            System.out.println("SUCCESS: All approaches produce identical results on test input.");
        } else {
            System.err.println("FAILURE: Approaches produced different results!");
            System.exit(1);
        }

        // Randomized verification
        System.out.println("Running randomized verification...");
        String randomDna = DNAGenerator.generateUniform(1000);
        r1 = brute.findRepeatedKmers(randomDna, 10);
        r2 = map.findRepeatedKmers(randomDna, 10);
        r3 = sort.findRepeatedKmers(randomDna, 10);

        if (areResultsEqual(r1, r2) && areResultsEqual(r2, r3)) {
            System.out.println("SUCCESS: All approaches produce identical results on random input (N=1000, K=10).");
        } else {
            System.err.println("FAILURE: Approaches produced different results on random input!");
            // Print first few diffs?
            System.exit(1);
        }
    }

    private static boolean areResultsEqual(List<KmerResult> list1, List<KmerResult> list2) {
        if (list1.size() != list2.size())
            return false;
        for (int i = 0; i < list1.size(); i++) {
            KmerResult k1 = list1.get(i);
            KmerResult k2 = list2.get(i);
            if (!k1.getKmer().equals(k2.getKmer()))
                return false;
            if (k1.getCount() != k2.getCount())
                return false;
            if (!k1.getPositions().equals(k2.getPositions()))
                return false;
        }
        return true;
    }

    private static void runBenchmarks() {
        int N_benchmark = 10000;

        // K values
        int[] K_values = { 10, 50, 200 };

        String[] types = { "Uniform", "Repetitive", "Biased" };

        KmerFinder brute = new BruteForceFinder();
        KmerFinder map = new HashMapFinder();
        KmerFinder sort = new SortBasedFinder();

        System.out.printf("%-12s %-5s %-12s %-15s %-10s %-10s\n", "Dataset", "K", "Algorithm", "Time(ms)", "Memory(MB)",
                "Results");

        for (String type : types) {
            String dna = "";
            if (type.equals("Uniform"))
                dna = DNAGenerator.generateUniform(N_benchmark);
            else if (type.equals("Repetitive"))
                dna = DNAGenerator.generateRepetitive(N_benchmark);
            else if (type.equals("Biased"))
                dna = DNAGenerator.generateBiased(N_benchmark, 0.9); // 90% GC

            for (int k : K_values) {
                // Run each alg
                runSingleBenchmark(type, k, "BruteForce", brute, dna);
                // System.gc();
                runSingleBenchmark(type, k, "HashMap", map, dna);
                // System.gc();
                runSingleBenchmark(type, k, "SortBased", sort, dna);
                System.out.println("----------------------------------------------------------------");
            }
        }

        // Run larger N for Map and Sort only
        System.out.println("\n--- Large N Benchmark (Map vs Sort) ---");
        int N_large = 100000;
        String dnaLarge = DNAGenerator.generateUniform(N_large);
        for (int k : K_values) {
            runSingleBenchmark("Uniform_L", k, "HashMap", map, dnaLarge);
            runSingleBenchmark("Uniform_L", k, "SortBased", sort, dnaLarge);
        }
    }

    private static void runSingleBenchmark(String dataset, int k, String algName, KmerFinder finder, String dna) {
        // Warmup
        if (dna.length() < 20000) { // Don't warmup too much on huge inputs
            finder.findRepeatedKmers(dna, k); // run once
        }
        System.gc();
        try {
            Thread.sleep(100);
        } catch (Exception e) {
        }

        long memStart = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long startTime = System.nanoTime();

        List<KmerResult> results = finder.findRepeatedKmers(dna, k);

        long endTime = System.nanoTime();
        long memEnd = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        long memUsedBytes = Math.max(0, memEnd - memStart);
        // Note: Memory calculation is approximate in Java without instrumentation.

        double timeMs = (endTime - startTime) / 1_000_000.0;
        double memMb = memUsedBytes / (1024.0 * 1024.0);

        System.out.printf("%-12s %-5d %-12s %-15.2f %-10.2f %-10d\n", dataset, k, algName, timeMs, memMb,
                results.size());
    }
}
