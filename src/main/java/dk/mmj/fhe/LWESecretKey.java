package dk.mmj.fhe;

import dk.mmj.fhe.interfaces.SecretKey;
import dk.mmj.matrix.Matrix;

import java.math.BigInteger;
import java.util.Objects;

public class LWESecretKey implements SecretKey {

    private final Matrix s;
    private final BigInteger q;

    public LWESecretKey(Matrix s, BigInteger q) {
        this.s = s;
        this.q = q;
    }

    public Matrix getS() {
        return s;
    }

    public BigInteger getQ() {
        return q;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LWESecretKey that = (LWESecretKey) o;
        return Objects.equals(s, that.s);
    }

    @Override
    public int hashCode() {
        return Objects.hash(s);
    }
}
