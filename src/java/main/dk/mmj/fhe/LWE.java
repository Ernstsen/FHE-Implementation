package dk.mmj.fhe;


import org.ejml.simple.SimpleMatrix;

/**
 * Learning with Errors implementation of Fully Homomorphic Encryption
 */
public class LWE implements FHE {
    private static final int nFactorToM = 69;//TODO: Is this unsafe to have as constant?

    /**
     * For general documentation see {@link FHE}
     *
     * @param securityParameter in this case n
     * @return keypair
     */
    public KeyPair generateKey(int securityParameter) {
        new SimpleMatrix()
        return null;
    }

    public Ciphertext encrypt(boolean m, SimpleMatrix publicKey) {
        return null;
    }

    public boolean decrypt(Ciphertext c, SimpleMatrix secretKey) {
        return false;
    }
}
