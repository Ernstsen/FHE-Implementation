package dk.mmj.fhe;

import dk.mmj.fhe.interfaces.PublicKey;
import dk.mmj.matrix.Matrix;

import java.math.BigInteger;

public class LWEPublicKey implements PublicKey {
    private final Matrix key;
    private final BigInteger q;
    private final int n;

    public LWEPublicKey(Matrix key, BigInteger q, int n) {
        this.key = key;
        this.q = q;
        this.n = n;
    }

    public Matrix getKey() {
        return key;
    }

    public BigInteger getQ() {
        return q;
    }
}
