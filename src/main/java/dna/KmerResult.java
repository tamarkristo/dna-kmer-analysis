package dna;

import java.util.Collections;
import java.util.List;

public class KmerResult implements Comparable<KmerResult> {
    private final String kmer;
    private final int count;
    private final List<Integer> positions;

    public KmerResult(String kmer, int count, List<Integer> positions) {
        this.kmer = kmer;
        this.count = count;
        this.positions = positions;
        // Ensure positions are sorted
        Collections.sort(this.positions);
    }

    public String getKmer() {
        return kmer;
    }

    public int getCount() {
        return count;
    }

    public List<Integer> getPositions() {
        return positions;
    }

    @Override
    public int compareTo(KmerResult other) {
        // Sort alphabetically by k-mer
        return this.kmer.compareTo(other.kmer);
    }

    @Override
    public String toString() {
        return String.format("%s: count=%d, positions=%s", kmer, count, positions);
    }
}
