package dna;

import java.util.List;

public interface KmerFinder {
    /**
     * Finds keys that appear at least twice in the DNA string.
     * 
     * @param dna The DNA sequence string.
     * @param k   The length of the k-mer.
     * @return A list of KmerResult objects for repeated k-mers.
     */
    List<KmerResult> findRepeatedKmers(String dna, int k);
}
