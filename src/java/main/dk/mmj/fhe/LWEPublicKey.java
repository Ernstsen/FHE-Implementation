package dk.mmj.fhe;

import dk.mmj.fhe.interfaces.PublicKey;
import dk.mmj.matrix.Matrix;

public class LWEPublicKey implements PublicKey {
    private final Matrix key;

    public LWEPublicKey(Matrix key) {
        this.key = key;
    }

    public Matrix getKey() {
        return key;
    }
}
