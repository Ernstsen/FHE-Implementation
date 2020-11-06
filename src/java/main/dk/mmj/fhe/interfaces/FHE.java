package dk.mmj.fhe.interfaces;

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
     * @param m         the integer to be encrypted
     * @param publicKey the secret key to encrypt m under
     * @return encryption of m under publicKey
     */
    Ciphertext encrypt(boolean m, PublicKey publicKey);

    /**
     * Decrypts a message encrypted under the publicKey, related to the given secretKey
     *
     * @param c         the ciphertext
     * @param secretKey the secret key, relating to the public key used for encryption
     * @return the plaintext value x where c = ENC(x)
     */
    boolean decrypt(Ciphertext c, SecretKey secretKey);

    /**
     * Evaluates nand on two encrypted messages
     *
     * @param c1 ENC_pk(m1)
     * @param c2 ENC_pk(m2)
     * @param pk public key for system
     * @return ENC_pk(nand ( m1, m2))
     */
    Ciphertext nand(Ciphertext c1, Ciphertext c2, PublicKey pk);

    /**
     * Keypair for the fully homomorphic encryption scheme
     */
    class KeyPair {
        private final SecretKey secretKey;
        private final PublicKey publicKey;

        public KeyPair(SecretKey secretKey, PublicKey publicKey) {
            this.secretKey = secretKey;
            this.publicKey = publicKey;
        }

        public SecretKey getSecretKey() {
            return secretKey;
        }

        public PublicKey getPublicKey() {
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
