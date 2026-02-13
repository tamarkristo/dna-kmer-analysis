package dna;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HashMapFinder implements KmerFinder {

    @Override
    public List<KmerResult> findRepeatedKmers(String dna, int k) {
        Map<String, List<Integer>> map = new HashMap<>();
        int n = dna.length();

        for (int i = 0; i <= n - k; i++) {
            String sub = dna.substring(i, i + k);
            map.putIfAbsent(sub, new ArrayList<>());
            map.get(sub).add(i);
        }

        List<KmerResult> results = new ArrayList<>();
        for (Map.Entry<String, List<Integer>> entry : map.entrySet()) {
            List<Integer> positions = entry.getValue();
            if (positions.size() > 1) {
                results.add(new KmerResult(entry.getKey(), positions.size(), positions));
            }
        }

        Collections.sort(results);
        return results;
    }
}
