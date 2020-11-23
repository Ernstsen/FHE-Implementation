package dk.mmj;

import dk.mmj.fhe.LWECiphertext;
import dk.mmj.fhe.LWESecretKey;
import dk.mmj.matrix.LWEUtils;
import dk.mmj.matrix.Matrix;

import java.math.BigInteger;
import java.util.Optional;
import java.util.stream.IntStream;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

/**
 * Utility class for different type of benchmarks on the cryptosystem
 */
public class LWEBenchmarkUtils {
    private final Matrix xsG;
    private final BigInteger q;
    private final Matrix sC;

    private LWEBenchmarkUtils(Matrix xsG, BigInteger q, Matrix sC) {
        this.xsG = xsG;
        this.q = q;
        this.sC = sC;
    }

    /**
     * @param ciphertext ciphertext to measure noise one
     * @param sk         the secret key that decrypts the ciphertext
     * @param value      the value for which <code> value = DEC(ciphertext) </code>
     * @return the noise contained in the ciphertext
     */
    public static BigInteger calculateNoise(LWECiphertext ciphertext, LWESecretKey sk, boolean value) {
        Matrix c = ciphertext.getC();
        Matrix s = sk.getS();
        BigInteger q = sk.getQ();
        final Matrix bigG = LWEUtils.createG(s.getColumns(), q);
        final Matrix sG = s.multiply(bigG, q);
        final Matrix sC = s.multiply(c, q);
        final Matrix xsG = sG.multiply(value ? ONE : ZERO, q);

        LWEBenchmarkUtils util = new LWEBenchmarkUtils(xsG, q, sC);

        Optional<BigInteger> reduce = IntStream.range(0, c.getColumns()).parallel()
                .mapToObj(util::calculateNoiseSingle)
                .reduce(BigInteger::max);

        return reduce.orElse(null);

    }

    /**
     * @param index index to be read, and have its noise calculated
     * @return the noise contained in the ciphertext
     */
    private BigInteger calculateNoiseSingle(int index) {
        BigInteger leftBitValue = sC.get(0, index);
        BigInteger zeroHBitValue = xsG.get(0, index);

        return leftBitValue.subtract(zeroHBitValue).mod(q).min(
                zeroHBitValue.subtract(leftBitValue).mod(q)
        );
    }

}
