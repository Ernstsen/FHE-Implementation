package dk.mmj.fhe;

import dk.mmj.fhe.interfaces.Ciphertext;
import dk.mmj.matrix.Matrix;

/**
 * Implementation of a ciphertext for LWE
 */
public class LWECiphertext implements Ciphertext {
    /**
     * matrix C from <i>Zvika Brakerski.Fundamentals of fully homomorphic encryption</i>
     */
    private final Matrix c;

    public LWECiphertext(Matrix c) {
        this.c = c;
    }

    public Matrix getC() {
        return c;
    }
}
