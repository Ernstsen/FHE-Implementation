package dk.mmj.fhe;

import org.bouncycastle.pqc.math.linearalgebra.Matrix;

/**
 * Fully Homomorphic Encryption
 */
public interface FHE {

    /**
     * Generates a keypair for the encryption scheme
     *
     * @param securityParameter decides how secure the scheme is - often as the size of the keys
     * @return a {@link KeyPair}
     */
    KeyPair generateKey(int securityParameter);

    /**
     * Encrypts a message under the secret key
     *
     * @param m the bit to be encrypted
     * @param publicKey the secret key to encrypt m under
     * @return encryption of m under publicKey
     */
    Ciphertext encrypt(boolean m, Matrix publicKey);

    /**
     * Decrypts a message encrypted under the publicKey, related to the given secretKey
     *
     * @param c the ciphertext
     * @param secretKey the secret key, relating to the public key used for encryption
     * @return the plaintext value x where c = ENC(x)
     */
    boolean decrypt(Ciphertext c, Matrix secretKey);

    /**
     * Keypair for the fully homomorphic encryption scheme
     */
    class KeyPair {
        private Matrix secretKey;
        private Matrix publicKey;
    }
}
