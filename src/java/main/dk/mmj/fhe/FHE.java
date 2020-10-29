package dk.mmj.fhe;

import org.bouncycastle.pqc.math.linearalgebra.Matrix;

import java.util.Objects;

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

        public Matrix getSecretKey() {
            return secretKey;
        }

        public Matrix getPublicKey() {
            return publicKey;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            KeyPair keyPair = (KeyPair) o;
            return Objects.equals(secretKey, keyPair.secretKey) &&
                    Objects.equals(publicKey, keyPair.publicKey);
        }

        @Override
        public int hashCode() {
            return Objects.hash(secretKey, publicKey);
        }
    }
}
