package dk.mmj.fhe;


import dk.mmj.fhe.interfaces.Ciphertext;
import dk.mmj.fhe.interfaces.FHE;
import dk.mmj.fhe.interfaces.PublicKey;
import dk.mmj.fhe.interfaces.SecretKey;
import dk.mmj.matrix.Matrix;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Learning with Errors implementation of Fully Homomorphic Encryption
 * <br/>
 * Naming is greatly inspired by the paper <i>Fundamentals of Fully Homomorphic Encryption – A Survey,
 * Zvika Brakerski (Electronic Colloquium on Computational Complexity, Report No. 125 (2018)) </i>
 */
public class LWE implements FHE {
    private static final int nFactorToM = 69;//TODO: Is this unsafe to have as constant?
    private final SecureRandom rand = new SecureRandom();

    /**
     * For general documentation see {@link FHE}
     *
     * @param securityParameter in this case n
     * @return keypair
     */
    public KeyPair generateKey(int securityParameter) {
        BigInteger q = new BigInteger(securityParameter, rand);
        int n = securityParameter / 2;
        int m = n * nFactorToM;
        Matrix bigB = new Matrix(n, m, rand, q);

        Matrix t = new Matrix(1, n, rand, q);

        Matrix e = new Matrix(m, 1, rand, BigInteger.valueOf(n));

//        Matrix b = t * bigB + e;

        return new KeyPair(
                new LWESecretKey(null),
                new LWEPublicKey(null)
        );
    }

    public Ciphertext encrypt(boolean m, PublicKey publicKey) {
        return null;
    }

    public boolean decrypt(Ciphertext c, SecretKey secretKey) {
        return false;
    }
}
