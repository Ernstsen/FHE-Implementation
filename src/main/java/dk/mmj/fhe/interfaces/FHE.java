package dk.mmj.fhe.interfaces;

import java.util.Objects;

/**
 * Fully Homomorphic Encryption
 */
public interface FHE <ParameterClass extends FHE.Parameters> {

    /**
     * Generates a keypair for the encryption scheme
     *
     * @param parameters Is scheme-specific. Often decides key-size modulus etc.
     * @return a {@link KeyPair}
     */
    KeyPair generateKey(ParameterClass parameters);

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
     * Evaluates not on an encrypted message
     *
     * @param c ENC_pk(m1)
     * @param pk public key for system
     * @return ENC_pk(not(m1))
     */
    Ciphertext not(Ciphertext c, PublicKey pk);

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
     * Evaluates and on two encrypted messages
     *
     * @param c1 ENC_pk(m1)
     * @param c2 ENC_pk(m2)
     * @param pk public key for system
     * @return ENC_pk(and ( m1, m2))
     */
    Ciphertext and(Ciphertext c1, Ciphertext c2, PublicKey pk);

    /**
     * Evaluates or on two encrypted messages
     *
     * @param c1 ENC_pk(m1)
     * @param c2 ENC_pk(m2)
     * @param pk public key for system
     * @return ENC_pk(or ( m1, m2))
     */
    Ciphertext or(Ciphertext c1, Ciphertext c2, PublicKey pk);

    /**
     * Evaluates exclusive or on two encrypted messages
     *
     * @param c1 ENC_pk(m1)
     * @param c2 ENC_pk(m2)
     * @param pk public key for system
     * @return ENC_pk(xor ( m1, m2))
     */
    Ciphertext xor(Ciphertext c1, Ciphertext c2, PublicKey pk);

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

    interface Parameters {

    }
}
