package dk.mmj.fhe;


import org.ejml.simple.SimpleMatrix;

/**
 * Learning with Errors implementation of Fully Homomorphic Encryption
 */
public class LWE implements FHE {
    public KeyPair generateKey(int securityParameter) {

        return null;
    }

    public Ciphertext encrypt(boolean m, SimpleMatrix publicKey) {
        return null;
    }

    public boolean decrypt(Ciphertext c, SimpleMatrix secretKey) {
        return false;
    }
}
