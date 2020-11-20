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
    private final Matrix s;
    private final BigInteger q;
    private final Matrix c;

    private LWEBenchmarkUtils(Matrix s, BigInteger q, Matrix c) {
        this.s = s;
        this.q = q;
        this.c = c;
    }

    /**
     * @param ciphertext ciphertext to measure noise one
     * @param sk         the secret key that decrypts the ciphertext
     * @param value      the value for which <code> value = DEC(ciphertext) </code>
     * @return the noise contained in the ciphertext
     */
    public static BigInteger calculateNoise(LWECiphertext ciphertext, LWESecretKey sk, boolean value) {
        Matrix c = ciphertext.getC();
        LWEBenchmarkUtils util = new LWEBenchmarkUtils(sk.getS(), sk.getQ(), c);

        Optional<BigInteger> reduce = IntStream.range(0, c.getColumns()).parallel()
                .mapToObj(i -> util.calculateNoiseSingle(i, value))
                .reduce(BigInteger::max);

        return reduce.orElse(null);

    }

    /**
     * @param index index to be read, and have its noise calculated
     * @param value the value for which <code> value = DEC(ciphertext) </code>
     * @return the noise contained in the ciphertext
     */
    private BigInteger calculateNoiseSingle(int index, boolean value) {
        final Matrix bigG = LWEUtils.createG(s.getColumns(), q);
        final Matrix sG = s.multiply(bigG, q);
        final Matrix sC = s.multiply(c, q);

        BigInteger leftBitValue = sC.get(0, index);

        final Matrix hypothesis = sG.multiply(value ? ONE : ZERO, q);
        BigInteger zeroHBitValue = hypothesis.get(0, index);

        return leftBitValue.subtract(zeroHBitValue).mod(q).min(
                zeroHBitValue.subtract(leftBitValue).mod(q)
        );
    }

}
