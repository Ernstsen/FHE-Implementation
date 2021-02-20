package dk.mmj.benchmarking;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class GaussianBenchmark {
    private SecureRandom rand = new SecureRandom();

    /**
     * @param q BigInteger q deciding size
     * @return next gaussian random int with width = alpha*q
     */
    private BigInteger nextGaussian(BigInteger q) {
        int qInt = q.intValue();
        double gaussian = rand.nextGaussian();

        double v = gaussian * (3.2);

        long vRounded = v> 0 ? ((long) (v + .5d)) : ((long) (v - .5d));
        return BigInteger.valueOf(vRounded);
    }

    public static void main(String[] args) {
        BigInteger bigInteger = BigInteger.valueOf(50);

        HashMap<BigInteger, Integer> observed = new HashMap<>();

        GaussianBenchmark b = new GaussianBenchmark();

        BigInteger number;
        for (int i = 0; i < 10_000_000; i++) {
            number = b.nextGaussian(bigInteger);
            observed.compute(number, (key, val) -> val != null ? val+1 : 1);
        }

        ArrayList<BigInteger> values = new ArrayList<>(observed.keySet());
        values.sort(BigInteger::compareTo);


        StringBuilder sb = new StringBuilder();
        sb.append("numbers = [")
                .append(values.stream().map(BigInteger::toString).collect(Collectors.joining(", ")))
                .append("]\n");
        sb.append("occurrences = [")
                .append(values.stream().map(observed::get).map(val -> Integer.toString(val)).collect(Collectors.joining(", ")))
                .append("]\n");

        System.out.println(sb.toString());


    }
}
