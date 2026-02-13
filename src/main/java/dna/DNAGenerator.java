package dna;

import java.util.Random;

public class DNAGenerator {
    private static final char[] NUCLEOTIDES = { 'A', 'C', 'G', 'T' };
    private static final Random random = new Random();

    public static String generateUniform(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(NUCLEOTIDES[random.nextInt(4)]);
        }
        return sb.toString();
    }

    public static String generateRepetitive(int length) {
        StringBuilder sb = new StringBuilder(length);
        String motif = "ACGT";
        // Highly repetitive: just repeat the motif over and over
        for (int i = 0; i < length; i++) {
            sb.append(motif.charAt(i % motif.length()));
        }
        return sb.toString();
    }

    public static String generateBiased(int length, double gcContent) {
        StringBuilder sb = new StringBuilder(length);
        // gcContent is probability of G or C
        // so prob(G) = prob(C) = gcContent / 2
        // prob(A) = prob(T) = (1 - gcContent) / 2

        for (int i = 0; i < length; i++) {
            double r = random.nextDouble();
            if (r < gcContent) {
                // Generate G or C
                sb.append(random.nextBoolean() ? 'G' : 'C');
            } else {
                // Generate A or T
                sb.append(random.nextBoolean() ? 'A' : 'T');
            }
        }
        return sb.toString();
    }
}
