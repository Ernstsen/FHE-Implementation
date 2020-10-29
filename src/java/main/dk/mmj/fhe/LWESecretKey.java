package dk.mmj.fhe;

import dk.mmj.fhe.interfaces.SecretKey;
import dk.mmj.matrix.Matrix;

import java.util.Objects;

public class LWESecretKey implements SecretKey {

    private final Matrix t;

    public LWESecretKey(Matrix t) {
        this.t = t;
    }

    public Matrix getT() {
        return t;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LWESecretKey that = (LWESecretKey) o;
        return Objects.equals(t, that.t);
    }

    @Override
    public int hashCode() {
        return Objects.hash(t);
    }
}
