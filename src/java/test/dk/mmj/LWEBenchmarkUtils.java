package dk.mmj;

import dk.mmj.fhe.LWECiphertext;
import dk.mmj.fhe.LWESecretKey;
import dk.mmj.matrix.LWEUtils;
import dk.mmj.matrix.Matrix;

import java.math.BigInteger;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

/**
 * Utility class for different type of benchmarks on the cryptosystem
 */
public class LWEBenchmarkUtils {

    /**
     * @param ciphertext the ciphertext to be measured
     * @param sk         secretKey able to decrypt ciphertext
     * @param value      the value for which <code> value = DEC(ciphertext) </code>
     * @return the noise contained in the ciphertext
     */
    BigInteger calculateNoise(LWECiphertext ciphertext, LWESecretKey sk, boolean value) {
        Matrix s = sk.getS();
        BigInteger q = sk.getQ();
        Matrix c = ciphertext.getC();

        final Matrix bigG = LWEUtils.createG(s.getColumns(), q);
        final Matrix sG = s.multiply(bigG, q);
        final Matrix sC = s.multiply(c, q);

        BigInteger leftBitValue = sC.get(0,0);

        final Matrix hypothesis = sG.multiply(value ? ONE : ZERO, q);

        return leftBitValue.subtract(hypothesis.get(0,0)).abs();
    }

}
