package dk.mmj.fhe;


import dk.mmj.fhe.interfaces.Ciphertext;
import dk.mmj.fhe.interfaces.FHE;
import dk.mmj.fhe.interfaces.PublicKey;
import dk.mmj.fhe.interfaces.SecretKey;
import dk.mmj.matrix.LWEUtils;
import dk.mmj.matrix.Matrix;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

import static java.math.BigInteger.*;

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
        int n = securityParameter / 8;//Todo: Fix!
        int m = n * nFactorToM;

        Matrix bigB = new Matrix(n, m, rand, q);

        Matrix t = new Matrix(1, n, rand, q);

        Matrix e = new Matrix(1, m, rand, BigInteger.valueOf(n));

        Matrix b = t.multiply(bigB, q).add(e, q);

        Matrix minusT = t.negate().addColumn(new BigInteger[]{ONE});
        Matrix bigA = bigB.addRow(b.asVector());
        return new KeyPair(
                new LWESecretKey(minusT, bigA, q),
                new LWEPublicKey(bigA, q, n)
        );
    }

    public Ciphertext encrypt(boolean x, PublicKey publicKey) {
        if (!(publicKey instanceof LWEPublicKey)) {
            throw new RuntimeException("Key must be for LWE system");
        }

        LWEPublicKey key = (LWEPublicKey) publicKey;
        Matrix a = key.getKey();
        BigInteger q = key.getQ();
        int n = a.getRows();

        Matrix bigG = LWEUtils.createG(n, q);
        Matrix r = new Matrix(a.getColumns(), bigG.getColumns(), rand, BigInteger.valueOf(2));

        Matrix multiply = a.multiply(r, q);

        Matrix c = multiply.add(bigG.multiply(x ? ONE : ZERO, q), q);

        return new LWECiphertext(c);
    }

    public boolean decrypt(Ciphertext c, SecretKey secretKey) {
        if (!(c instanceof LWECiphertext)) {
            throw new RuntimeException("Ciphertext must be for LWE system!");
        }
        if (!(secretKey instanceof LWESecretKey)) {
            throw new RuntimeException("Key must be for LWE system");
        }

        Matrix cMatrix = ((LWECiphertext) c).getC();
        

        BigInteger[] smallG = LWEUtils.calculateSmallG(cMatrix.getColumns());

        for (int i = 0; i < cMatrix.getRows(); i++) {
            BigInteger[] row = cMatrix.getRow(i);

            BigInteger x = LWEUtils.innerProduct(row, smallG);
            BigInteger v = smallG[i];

            BigInteger[] partialResult = x.divideAndRemainder(v);

            System.out.println(Arrays.toString(partialResult));

        }


        return false;
    }
}
