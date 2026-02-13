package dna;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortBasedFinder implements KmerFinder {

    @Override
    public List<KmerResult> findRepeatedKmers(String dna, int k) {
        int n = dna.length();
        if (n < k) {
            return new ArrayList<>();
        }

        // Create an array of all valid starting indices
        Integer[] indices = new Integer[n - k + 1];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = i;
        }

        // Sort indices based on the dictionary order of the substrings starting at them
        Arrays.sort(indices, new Comparator<Integer>() {
            @Override
            public int compare(Integer i, Integer j) {
                for (int offset = 0; offset < k; offset++) {
                    char c1 = dna.charAt(i + offset);
                    char c2 = dna.charAt(j + offset);
                    if (c1 != c2) {
                        return Character.compare(c1, c2);
                    }
                }
                return 0;
            }
        });

        List<KmerResult> results = new ArrayList<>();

        // Scan the sorted array to find duplicates
        // They will be adjacent in the sorted array

        int currentStart = 0;
        while (currentStart < indices.length) {
            int currentEnd = currentStart + 1;

            // "String" at indices[currentStart]
            // We don't construct it yet to save memory

            // Look for run of identical K-mers
            while (currentEnd < indices.length) {
                if (areKmersEqual(dna, k, indices[currentStart], indices[currentEnd])) {
                    currentEnd++;
                } else {
                    break;
                }
            }

            // The run is from currentStart to currentEnd - 1
            int count = currentEnd - currentStart;

            if (count > 1) {
                String kmer = dna.substring(indices[currentStart], indices[currentStart] + k);
                List<Integer> positions = new ArrayList<>();
                for (int m = currentStart; m < currentEnd; m++) {
                    positions.add(indices[m]);
                }
                Collections.sort(positions);
                results.add(new KmerResult(kmer, count, positions));
            }

            currentStart = currentEnd;
        }

        return results;
    }

    private boolean areKmersEqual(String dna, int k, int i, int j) {
        for (int m = 0; m < k; m++) {
            if (dna.charAt(i + m) != dna.charAt(j + m)) {
                return false;
            }
        }
        return true;
    }
}
