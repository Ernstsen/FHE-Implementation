package dk.mmj.benchmarking.noise;

import dk.mmj.LWEBenchmarkUtils;
import dk.mmj.circuit.Circuit;
import dk.mmj.circuit.CircuitBuilder;
import dk.mmj.circuit.GateType;
import dk.mmj.fhe.LWE;
import dk.mmj.fhe.LWECiphertext;
import dk.mmj.fhe.LWESecretKey;
import dk.mmj.fhe.interfaces.Ciphertext;
import dk.mmj.fhe.interfaces.FHE;
import dk.mmj.fhe.interfaces.SecretKey;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class NoiseBenchmarkRunner {

    @Test
    public void benchmarkNoiseAnd() {
        LWE lwe = new LWE();
        FHE.KeyPair keyPair = lwe.generateKey(128);

        NoiseObserver observer = new NoiseObserver(keyPair.getSecretKey(), lwe);

        CircuitBuilder cb = new CircuitBuilder(lwe).addObserver(observer);

        CircuitBuilder.MultipleInputGateBuilder and = cb.and();
        and.leftGate().input(0);
        and.rightGate().input(1);

        Circuit circuit = cb.build();

        for (boolean[] booleans : permutations2()) {
            Ciphertext[] ciphertexts = {
                    lwe.encrypt(booleans[0], keyPair.getPublicKey()),
                    lwe.encrypt(booleans[1], keyPair.getPublicKey())
            };

            Ciphertext valC = circuit.evaluate(keyPair.getPublicKey(), ciphertexts);
            boolean decrypt = lwe.decrypt(valC, keyPair.getSecretKey());
            assertEquals("Decryption failed - noise was too high!: ", (booleans[0] & booleans[1]), decrypt);
        }

        observer.log();
    }

    @Test
    public void benchmarkNoiseNand() {
        LWE lwe = new LWE();
        FHE.KeyPair keyPair = lwe.generateKey(128);

        NoiseObserver observer = new NoiseObserver(keyPair.getSecretKey(), lwe);

        CircuitBuilder cb = new CircuitBuilder(lwe).addObserver(observer);

        CircuitBuilder.MultipleInputGateBuilder and = cb.nand();
        and.leftGate().input(0);
        and.rightGate().input(1);

        Circuit circuit = cb.build();

        for (boolean[] booleans : permutations2()) {
            Ciphertext[] ciphertexts = {
                    lwe.encrypt(booleans[0], keyPair.getPublicKey()),
                    lwe.encrypt(booleans[1], keyPair.getPublicKey())
            };

            Ciphertext valC = circuit.evaluate(keyPair.getPublicKey(), ciphertexts);
            boolean decrypt = lwe.decrypt(valC, keyPair.getSecretKey());
            assertEquals("Decryption failed - noise was too high!: ", !(booleans[0] & booleans[1]), decrypt);
        }

        observer.log();
    }

    @Test
    public void benchmarkNoiseXor() {
        LWE lwe = new LWE();
        FHE.KeyPair keyPair = lwe.generateKey(128);

        NoiseObserver observer = new NoiseObserver(keyPair.getSecretKey(), lwe);

        CircuitBuilder cb = new CircuitBuilder(lwe).addObserver(observer);

        CircuitBuilder.MultipleInputGateBuilder and = cb.xor();
        and.leftGate().input(0);
        and.rightGate().input(1);

        Circuit circuit = cb.build();

        for (boolean[] booleans : permutations2()) {
            Ciphertext[] ciphertexts = {
                    lwe.encrypt(booleans[0], keyPair.getPublicKey()),
                    lwe.encrypt(booleans[1], keyPair.getPublicKey())
            };

            Ciphertext valC = circuit.evaluate(keyPair.getPublicKey(), ciphertexts);
            boolean decrypt = lwe.decrypt(valC, keyPair.getSecretKey());
            assertEquals("Decryption failed - noise was too high!: ", (booleans[0] ^ booleans[1]), decrypt);
        }

        observer.log();
    }

    @Test
    public void benchmarkNoiseOr() {
        LWE lwe = new LWE();
        FHE.KeyPair keyPair = lwe.generateKey(128);

        NoiseObserver observer = new NoiseObserver(keyPair.getSecretKey(), lwe);

        CircuitBuilder cb = new CircuitBuilder(lwe).addObserver(observer);

        CircuitBuilder.MultipleInputGateBuilder and = cb.or();
        and.leftGate().input(0);
        and.rightGate().input(1);

        Circuit circuit = cb.build();

        for (boolean[] booleans : permutations2()) {
            Ciphertext[] ciphertexts = {
                    lwe.encrypt(booleans[0], keyPair.getPublicKey()),
                    lwe.encrypt(booleans[1], keyPair.getPublicKey())
            };

            Ciphertext valC = circuit.evaluate(keyPair.getPublicKey(), ciphertexts);
            boolean decrypt = lwe.decrypt(valC, keyPair.getSecretKey());
            assertEquals("Decryption failed - noise was too high!: ", (booleans[0] | booleans[1]), decrypt);
        }

        observer.log();
    }

    @Test
    public void benchmarkNoiseAndWithThreeInpus() {
        LWE lwe = new LWE();
        FHE.KeyPair keyPair = lwe.generateKey(128);

        NoiseObserver observer = new NoiseObserver(keyPair.getSecretKey(), lwe);

        CircuitBuilder cb = new CircuitBuilder(lwe).addObserver(observer);

        CircuitBuilder.MultipleInputGateBuilder and = cb.and();
        and.leftGate().input(0);
        CircuitBuilder.MultipleInputGateBuilder secondAnd = and.rightGate().and();
        secondAnd.leftGate().input(1);
        secondAnd.rightGate().input(2);

        Circuit circuit = cb.build();

        for (boolean[] booleans : permutations3()) {
            Ciphertext[] ciphertexts = {
                    lwe.encrypt(booleans[0], keyPair.getPublicKey()),
                    lwe.encrypt(booleans[1], keyPair.getPublicKey()),
                    lwe.encrypt(booleans[2], keyPair.getPublicKey())
            };

            Ciphertext valC = circuit.evaluate(keyPair.getPublicKey(), ciphertexts);
            boolean decrypt = lwe.decrypt(valC, keyPair.getSecretKey());
            assertEquals("Decryption failed - noise was too high!: ", (booleans[0] & booleans[1] & booleans[2]), decrypt);
        }

        observer.log();
    }

    @Test
    public void benchmarkNoiseNot() {
        LWE lwe = new LWE();
        FHE.KeyPair keyPair = lwe.generateKey(128);

        NoiseObserver observer = new NoiseObserver(keyPair.getSecretKey(), lwe);

        CircuitBuilder cb = new CircuitBuilder(lwe).addObserver(observer);

        cb.not().input(0);

        Circuit circuit = cb.build();

        for (boolean bool : new Boolean[]{true, false}) {
            Ciphertext[] ciphertexts = {
                    lwe.encrypt(bool, keyPair.getPublicKey())
            };

            Ciphertext valC = circuit.evaluate(keyPair.getPublicKey(), ciphertexts);
            boolean decrypt = lwe.decrypt(valC, keyPair.getSecretKey());
            assertEquals("Decryption failed - noise was too high!: ", (!bool), decrypt);
        }

        observer.log();
    }

    boolean[][] permutations2() {
        return new boolean[][]{
                {true, true},
                {true, false},
                {false, true},
                {false, false},
        };
    }
    boolean[][] permutations3() {
        return new boolean[][]{
                {false, true, true},
                {false, true, false},
                {false, false, true},
                {false, false, false},
                {true, true, true},
                {true, true, false},
                {true, false, true},
                {true, false, false},
        };
    }

    @Test
    public void testCircuitEvaluatesCorrectly() {
        LWE lwe = new LWE();
        FHE.KeyPair keyPair = lwe.generateKey(128);
        NoiseObserver observer = new NoiseObserver(keyPair.getSecretKey(), lwe);

        CircuitBuilder cb = new CircuitBuilder(lwe).addObserver(observer);

        CircuitBuilder.MultipleInputGateBuilder nandCB = cb.nand();
        CircuitBuilder.MultipleInputGateBuilder andCB = nandCB.leftGate().and();

        CircuitBuilder.MultipleInputGateBuilder orCB =  andCB.leftGate().or();
        orCB.leftGate().input(0);
        orCB.rightGate().input(1);

        CircuitBuilder.MultipleInputGateBuilder xorCB = andCB.rightGate().xor();
        xorCB.leftGate().input(2);
        xorCB.rightGate().input(3);

        nandCB.rightGate().not().input(4);

        Circuit build = cb.build();

        for (boolean[] booleans : allPermutations5()) {
            boolean expected = !(((booleans[0] | booleans[1]) & (booleans[2] ^ booleans[3])) & (!booleans[4]));

            Ciphertext[] input = new Ciphertext[booleans.length];
            for (int i = 0; i < booleans.length; i++) {
                input[i] = lwe.encrypt(booleans[i], keyPair.getPublicKey());
            }

            Ciphertext actualC = build.evaluate(keyPair.getPublicKey(), input);
            boolean actual = lwe.decrypt(actualC, keyPair.getSecretKey());

            assertEquals("Circuit did not match expected output", expected, actual);
        }

        observer.log();
    }

    private boolean[][] allPermutations5(){
        boolean[] bools = {true, false};
        boolean[][] res = new boolean[32][5];

        int idx = 0;
        for (boolean first : bools) {
            for (boolean second : bools) {
                for (boolean third : bools) {
                    for (boolean fourth : bools) {
                        for (boolean fifth : bools) {
                            res[idx++] = new boolean[]{first, second, third, fourth, fifth};
                        }
                    }
                }
            }
        }

        return res;
    }



    public static class NoiseObserver implements CircuitBuilder.Observer {
        private final LWE lwe;
        private final List<String> logLines = new ArrayList<>();
        private final LWESecretKey sk;

        public NoiseObserver(SecretKey sk, LWE lwe) {
            logLines.add("Type\tEvalNoise\tLeftInputNoise\tRightInputNoise\tComment\n");
            this.sk = (LWESecretKey) sk;
            this.lwe = lwe;
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

        void log(){
            logLines.forEach(System.out::print);
        }

    }
}
