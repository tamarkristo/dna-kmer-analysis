package dna;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BruteForceFinder implements KmerFinder {

    @Override
    public List<KmerResult> findRepeatedKmers(String dna, int k) {
        List<KmerResult> results = new ArrayList<>();
        int n = dna.length();
        if (n < k) {
            return results;
        }

        boolean[] visited = new boolean[n];

        for (int i = 0; i <= n - k; i++) {
            if (visited[i]) {
                continue;
            }

            String target = dna.substring(i, i + k);
            List<Integer> positions = new ArrayList<>();
            positions.add(i);

            // Scan the rest of the string
            for (int j = i + 1; j <= n - k; j++) {
                if (!visited[j]) {
                    // Compare characters manually to avoid substring creation overhead
                    boolean match = true;
                    for (int m = 0; m < k; m++) {
                        if (dna.charAt(i + m) != dna.charAt(j + m)) {
                            match = false;
                            break;
                        }
                    }

                    if (match) {
                        positions.add(j);
                        visited[j] = true;
                    }
                }
            }

            if (positions.size() > 1) {
                visited[i] = true;
                results.add(new KmerResult(target, positions.size(), positions));
            }
        }

        Collections.sort(results);

        return results;
    }
}
