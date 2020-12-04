package dk.mmj.benchmarking.securityparameters;

import dk.mmj.LWEBenchmarkUtils;
import dk.mmj.circuit.Circuit;
import dk.mmj.circuit.CircuitBuilder;
import dk.mmj.circuit.GateType;
import dk.mmj.fhe.LWE;
import dk.mmj.fhe.LWECiphertext;
import dk.mmj.fhe.LWEParameters;
import dk.mmj.fhe.LWESecretKey;
import dk.mmj.fhe.interfaces.Ciphertext;
import dk.mmj.fhe.interfaces.FHE;
import dk.mmj.fhe.interfaces.PublicKey;
import dk.mmj.fhe.interfaces.SecretKey;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.math.BigInteger.*;

/**
 * Runner executing noise benchmarks on different combinations of security parameters
 */
public class SecurityParameterBenchmark {


    public static void main(String[] args) {
        int n, qPower;
        double alpha;
        List<IterationResults> results = new ArrayList<>();

        LWE lwe = new LWE();

        boolean[][] combinations = getBloodtypes();
        Circuit circuit = createBloodTypeCircuit(lwe);
        StringBuilder nArrayBuilder = new StringBuilder().append("X = [");

        for (n = 1; n <= 512; n = n << 1) {
            nArrayBuilder.append(n).append(", ");
            for (qPower = 19; qPower < 32; qPower += 4) {
                System.out.println("n:" + n + ", q: 2^" + qPower + "\r");
                for (alpha = 0.00000001; alpha < 0.1; alpha *= 10) {

                    LWEParameters params = new LWEParameters()
                            .setN(n)
                            .setQ(ONE.shiftLeft(qPower))
                            .setAlpha(alpha);

                    FHE.KeyPair keyPair = lwe.generateKey(params);
                    PublicKey pk = keyPair.getPublicKey();

                    BigInteger noiseAcc = ZERO;
                    for (boolean[] recipient : combinations) {
                        for (boolean[] donor : combinations) {
                            Ciphertext xa = lwe.encrypt(donor[0], pk);
                            Ciphertext xb = lwe.encrypt(donor[1], pk);
                            Ciphertext xr = lwe.encrypt(donor[2], pk);

                            Ciphertext ya = lwe.encrypt(recipient[0], pk);
                            Ciphertext yb = lwe.encrypt(recipient[1], pk);
                            Ciphertext yr = lwe.encrypt(recipient[2], pk);


                            Ciphertext c = circuit.evaluate(pk, xa, xb, xr, ya, yb, yr);
                            boolean expected = (!(donor[0] & (!recipient[0]))) & (!(donor[1] & (!recipient[1]))) & (!(donor[2] & (!recipient[2])));

                            noiseAcc = noiseAcc.add(LWEBenchmarkUtils.calculateNoise((LWECiphertext) c, (LWESecretKey) keyPair.getSecretKey(), expected));

                        }
                    }
                    results.add(new IterationResults(n, qPower, alpha, noiseAcc.divide(valueOf(4))));
                }
            }
        }

        nArrayBuilder.append("]");

        StringBuilder alphaStringBuilder = new StringBuilder().append("Y = [");
        for (alpha = 0.00000001; alpha < 0.1; alpha *= 10) {
            alphaStringBuilder.append(alpha).append(", ");
        }
        alphaStringBuilder.append("]");

        //Graf 1-4: n X alpha

        results.forEach(System.out::println);

        StringBuilder outputStringBuilder = new StringBuilder();

        System.out.println(nArrayBuilder);
        System.out.println(alphaStringBuilder);

        outputStringBuilder.append("#q=2^31\n").append("Z31 = np.asarray([");
        writePartialResultsToStringBuilder(results, outputStringBuilder, 31);
        outputStringBuilder.append("#q=2^27\n").append("Z27 = np.asarray([");
        writePartialResultsToStringBuilder(results, outputStringBuilder, 27);
        outputStringBuilder.append("#q=2^23\n").append("Z23 = np.asarray([");
        writePartialResultsToStringBuilder(results, outputStringBuilder, 23);
        outputStringBuilder.append("#q=2^19\n").append("Z19 = np.asarray([");
        writePartialResultsToStringBuilder(results, outputStringBuilder, 19);

        System.out.println(outputStringBuilder.toString());

    }

    private static void writePartialResultsToStringBuilder(List<IterationResults> results, StringBuilder outputStringBuilder, int currQPower) {
        double alpha;
        List<IterationResults> collect = results.stream().filter(r -> r.qPower == currQPower).collect(Collectors.toList());
        boolean first = true;
        for (alpha = 0.00000001; alpha < 0.1; alpha *= 10) {
            if (!first) {
                outputStringBuilder.append(",");
            } else {
                first = false;
            }

            outputStringBuilder.append("[");
            double finalAlpha = alpha;
            outputStringBuilder.append(collect.stream().filter(r -> r.alpha == finalAlpha).map(r -> r.noise).map(BigInteger::toString).collect(Collectors.joining(", ")));
            outputStringBuilder.append("]");
        }
        outputStringBuilder.append("])\n\n");
    }

    private static boolean[][] getBloodtypes() {
        boolean[] booleans = {true, false};
        boolean[][] combinations = new boolean[8][3];

        int idx = 0;
        for (boolean a : booleans) {
            for (boolean b : booleans) {
                for (boolean rhesus : booleans) {
                    combinations[idx++] = new boolean[]{a, b, rhesus};
                }
            }
        }
        return combinations;
    }

    private static Circuit createBloodTypeCircuit(LWE lwe) {
        CircuitBuilder cb = new CircuitBuilder(lwe);

        CircuitBuilder.MultipleInputGateBuilder outermostAnd = cb.and();
        CircuitBuilder.MultipleInputGateBuilder abAnd = outermostAnd.rightGate().and();
        CircuitBuilder.MultipleInputGateBuilder aAnd = abAnd.leftGate().not().and();
        aAnd.leftGate().input(0);
        aAnd.rightGate().not().input(1);

        CircuitBuilder.MultipleInputGateBuilder bAnd = abAnd.rightGate().not().and();
        bAnd.leftGate().input(2);
        bAnd.rightGate().not().input(3);

        CircuitBuilder.MultipleInputGateBuilder rhesusAnd = outermostAnd.leftGate().not().and();
        rhesusAnd.leftGate().input(4);
        rhesusAnd.rightGate().not().input(5);

        return cb.build();
    }


    private static class IterationResults {
        private final int n, qPower;
        private final double alpha;
        BigInteger noise;


        public IterationResults(int n, int qPower, double alpha, BigInteger noise) {
            this.n = n;
            this.qPower = qPower;
            this.alpha = alpha;
            this.noise = noise;
        }

        @Override
        public String toString() {
            return "IterationResults{" +
                    "n=" + n +
                    ", qPower=" + qPower +
                    ", alpha=" + alpha +
                    ", noise='" + noise + '\'' +
                    '}';
        }
    }

    private static class ObserverImpl implements CircuitBuilder.Observer {

        private final LWE lwe;
        private final List<String> logLines = new ArrayList<>();
        private final LWESecretKey sk;

        public ObserverImpl(SecretKey sk, LWE lwe) {
            logLines.add("Type\tEvalNoise\tLeftInputNoise\tRightInputNoise\tComment\n");
            this.sk = (LWESecretKey) sk;
            this.lwe = lwe;
        }

        private void reset() {
            logLines.clear();
            logLines.add("Type\tEvalNoise\tLeftInputNoise\tRightInputNoise\tComment\n");
        }

        @Override
        public void register(GateType type, Ciphertext inputValue, Ciphertext eval) {
            StringBuilder sb = new StringBuilder()
                    .append(type).append("\t");

            boolean expectedEval = lwe.decrypt(eval, sk);
            BigInteger evalNoise = LWEBenchmarkUtils.calculateNoise((LWECiphertext) eval, sk, expectedEval);
            sb.append(evalNoise).append("\t");
            boolean expectedInput = lwe.decrypt(inputValue, sk);
            BigInteger inputNoise = LWEBenchmarkUtils.calculateNoise((LWECiphertext) inputValue, sk, expectedInput);
            sb.append(inputNoise).append("\t");


            logLines.add(sb.append("\n").toString());
        }

        @Override
        public void register(GateType type, Ciphertext leftValue, Ciphertext rightValue, Ciphertext eval, String comment) {
            StringBuilder sb = new StringBuilder()
                    .append(type).append("\t");

            boolean expectedEval = lwe.decrypt(eval, sk);
            BigInteger evalNoise = LWEBenchmarkUtils.calculateNoise((LWECiphertext) eval, sk, expectedEval);
            sb.append(evalNoise).append("\t");

            boolean expectedLeft = lwe.decrypt(leftValue, sk);
            BigInteger leftNoise = LWEBenchmarkUtils.calculateNoise((LWECiphertext) leftValue, sk, expectedLeft);
            sb.append(leftNoise).append("\t");

            boolean expectedRight = lwe.decrypt(rightValue, sk);
            BigInteger rightNoise = LWEBenchmarkUtils.calculateNoise((LWECiphertext) rightValue, sk, expectedRight);
            sb.append(rightNoise).append("\t");

            sb.append(comment);
            logLines.add(sb.append("\n").toString());

        }

        public List<String> getLogLines() {
            return logLines;
        }
    }
}
