package dk.mmj.fhe;

import dk.mmj.fhe.interfaces.Ciphertext;
import dk.mmj.fhe.interfaces.FHE;
import dk.mmj.fhe.interfaces.PublicKey;
import dk.mmj.fhe.interfaces.SecretKey;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * IntegrationsTests of LWE
 * <br/>
 * Note that this is not pretty code
 * <br/>
 * The important parts are the circuit-generation in setup, and the evaluation in test methods
 * <br/>
 * The rest is simply an attempt to make these parts kind of readable
 */
public class TestLWECircuits {
    PublicKey pk;
    SecretKey sk;
    CircuitGenerator gen;
    FHE fhe;

    @Before
    public void setup() {
        fhe = new LWE();
        FHE.KeyPair keyPair = fhe.generateKey(64);
        pk = keyPair.getPublicKey();
        sk = keyPair.getSecretKey();

        gen = ((xa, xb, xr, ya, yb, yr) -> {
            //NOT(x_a AND (NOT(y_a)))
            Gate a = new InDegOne(new InDegTwo(xa, new InDegOne(ya, fhe::not), fhe::and), fhe::not);
            //NOT(x_b AND (NOT(y_b)))
            Gate b = new InDegOne(new InDegTwo(xb, new InDegOne(yb, fhe::not), fhe::and), fhe::not);
            //NOT(x_r AND (NOT(y_r)))
            Gate rhesus = new InDegOne(new InDegTwo(xr, new InDegOne(yr, fhe::not), fhe::and), fhe::not);

            return new InDegTwo(new InDegTwo(a, b, fhe::and), rhesus, fhe::and);
        });
    }

    @Test
    public void testBloodTypes() {
        boolean[][] combinations = getBloodtypes();

        for (boolean[] recipient : combinations) {
            for (boolean[] donor : combinations) {
                Ciphertext xa = fhe.encrypt(donor[0], pk);
                Ciphertext xb = fhe.encrypt(donor[1], pk);
                Ciphertext xr = fhe.encrypt(donor[2], pk);

                Ciphertext ya = fhe.encrypt(recipient[0], pk);
                Ciphertext yb = fhe.encrypt(recipient[1], pk);
                Ciphertext yr = fhe.encrypt(recipient[2], pk);


                Ciphertext c = gen.generateCircuit(xa, xb, xr, ya, yb, yr).evaluate();
                boolean result = fhe.decrypt(c, sk);
                boolean expected = (!(donor[0] & (!recipient[0]))) & (!(donor[1] & (!recipient[1]))) & (!(donor[2] & (!recipient[2])));
                assertEquals("Failed", expected, result);

            }
        }
    }

    private boolean[][] getBloodtypes() {
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


    private interface Gate {
        Ciphertext evaluate();
    }

    private interface Evaluator1 {
        Ciphertext evaluate(Ciphertext c, PublicKey publicKey);
    }

    private interface Evaluator2 {
        Ciphertext evaluate(Ciphertext c1, Ciphertext c2, PublicKey publicKey);
    }

    /**
     * Generates circuit determining bloodtype from (x_a, x_b, x_r)(y_a, y_b, y_r)
     */
    private interface CircuitGenerator {
        Gate generateCircuit(
                Ciphertext xa, Ciphertext xb, Ciphertext xr,
                Ciphertext ya, Ciphertext yb, Ciphertext yr);
    }

    /**
     * Gate with indegree two
     */
    private class InDegTwo implements Gate {
        private final Evaluator2 eval;
        Gate input1;
        Gate input2;
        Ciphertext c1;
        Ciphertext c2;

        public InDegTwo(Gate input1, Gate input2, Evaluator2 eval) {
            this.input1 = input1;
            this.input2 = input2;
            c1 = null;
            c2 = null;
            this.eval = eval;
        }

        public InDegTwo(Ciphertext ciphertext1, Ciphertext ciphertext2, Evaluator2 eval) {
            input1 = null;
            input2 = null;
            this.c1 = ciphertext1;
            this.c2 = ciphertext2;
            this.eval = eval;
        }

        public InDegTwo(Ciphertext ciphertext1, Gate gate2, Evaluator2 eval) {
            input1 = null;
            input2 = null;
            this.c1 = ciphertext1;
            this.input2 = gate2;
            this.eval = eval;
        }

        @Override
        public Ciphertext evaluate() {
            if (c1 == null) {
                c1 = input1.evaluate();
            }
            if (c2 == null) {
                c2 = input2.evaluate();
            }
            return eval.evaluate(c1, c2, pk);
        }

    }

    /**
     * Gate with indegree one
     */
    private class InDegOne implements Gate {
        private final Evaluator1 eval;
        Gate input1;
        Ciphertext c1;

        public InDegOne(Gate input1, Evaluator1 eval) {
            this.input1 = input1;
            c1 = null;
            this.eval = eval;
        }

        public InDegOne(Ciphertext ciphertext1, Evaluator1 eval) {
            input1 = null;
            this.c1 = ciphertext1;
            this.eval = eval;
        }

        @Override
        public Ciphertext evaluate() {
            if (c1 == null) {
                c1 = input1.evaluate();
            }
            return eval.evaluate(c1, pk);
        }

    }
}
