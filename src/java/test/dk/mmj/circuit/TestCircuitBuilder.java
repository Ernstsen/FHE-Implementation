package dk.mmj.circuit;

import dk.mmj.fhe.interfaces.Ciphertext;
import dk.mmj.fhe.interfaces.FHE;
import dk.mmj.fhe.interfaces.PublicKey;
import dk.mmj.fhe.interfaces.SecretKey;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestCircuitBuilder {

    @Test
    public void testCircuitEvaluatesCorrectly() {
        PlaintextFHE fhe = new PlaintextFHE();
        CircuitBuilder cb = new CircuitBuilder(fhe);

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
                input[i] = fhe.encrypt(booleans[i], null);
            }

            Ciphertext actualC = build.evaluate(null, input);
            boolean actual = fhe.decrypt(actualC, null);

            assertEquals("Circuit did not match expected output", expected, actual);
        }

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

    static class PlaintextFHE implements FHE<Params> {

        @Override
        public KeyPair generateKey(Params securityParameter) {
            return null;
        }

        @Override
        public Ciphertext encrypt(boolean m, PublicKey publicKey) {
            return new PlaintextCiphertext(m);
        }

        @Override
        public boolean decrypt(Ciphertext c, SecretKey secretKey) {
            return cast(c).value;
        }

        @Override
        public Ciphertext not(Ciphertext c, PublicKey pk) {
            return new PlaintextCiphertext(!cast(c).value);
        }

        @Override
        public Ciphertext nand(Ciphertext c1, Ciphertext c2, PublicKey pk) {
            return new PlaintextCiphertext(!(cast(c1).value & cast(c2).value));
        }

        @Override
        public Ciphertext and(Ciphertext c1, Ciphertext c2, PublicKey pk) {
            return new PlaintextCiphertext(cast(c1).value & cast(c2).value);
        }

        @Override
        public Ciphertext or(Ciphertext c1, Ciphertext c2, PublicKey pk) {
            return new PlaintextCiphertext(cast(c1).value | cast(c2).value);
        }

        @Override
        public Ciphertext xor(Ciphertext c1, Ciphertext c2, PublicKey pk) {
            return new PlaintextCiphertext(cast(c1).value ^ cast(c2).value);
        }

        private PlaintextCiphertext cast(Ciphertext c){
            return (PlaintextCiphertext) c;
        }
    }

    static class PlaintextCiphertext implements Ciphertext {
        boolean value;

        public PlaintextCiphertext(boolean value) {
            this.value = value;
        }
    }

    static class Params implements FHE.Parameters{

    }

}
