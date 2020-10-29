package dk.mmj.fhe;

import org.bouncycastle.pqc.math.linearalgebra.Matrix;

public class LWE implements FHE {
    public KeyPair generateKey(int securityParameter) {
        return null;
    }

    public Ciphertext encrypt(boolean m, Matrix publicKey) {
        return null;
    }

    public boolean decrypt(Ciphertext c, Matrix secretKey) {
        return false;
    }
}
