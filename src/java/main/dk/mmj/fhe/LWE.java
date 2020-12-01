package dk.mmj.fhe;


import dk.mmj.fhe.interfaces.Ciphertext;
import dk.mmj.fhe.interfaces.FHE;
import dk.mmj.fhe.interfaces.PublicKey;
import dk.mmj.fhe.interfaces.SecretKey;
import dk.mmj.matrix.LWEUtils;
import dk.mmj.matrix.Matrix;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.stream.IntStream;

import static dk.mmj.matrix.LWEUtils.logQ;
import static java.math.BigInteger.*;

/**
 * Learning with Errors implementation of Fully Homomorphic Encryption
 * <br/>
 * Naming is greatly inspired by the paper <i>Fundamentals of Fully Homomorphic Encryption – A Survey,
 * Zvika Brakerski (Electronic Colloquium on Computational Complexity, Report No. 125 (2018)) </i>
 */
public class LWE implements FHE<LWEParameters> {
    private final SecureRandom rand = new SecureRandom();
    private double alpha;

    /**
     * @param q BigInteger q deciding size
     * @return next gaussian random int with width = alpha*q
     */
    private BigInteger nextGaussian(BigInteger q) {
        int qInt = q.intValue();
        double gaussian = rand.nextGaussian();

        double v = gaussian * (alpha * qInt);

        long vRounded = v > 0 ? ((long) (v + .5d)) : ((long) (v - .5d));
        return BigInteger.valueOf(vRounded).mod(q);
    }

    /**
     * @param q BigInteger q deciding size
     * @return next uniform random int mod q
     */
    private BigInteger nextUniform(BigInteger q) {
        return new BigInteger(q.bitLength(), rand).mod(q);
    }

    /**
     * For general documentation see {@link FHE}
     *
     * @param parameters in this case n, q, m and alpha
     * @return keypair
     */
    public KeyPair generateKey(LWEParameters parameters) {
        this.alpha = parameters.getAlpha();
        BigInteger q = BigInteger.valueOf(parameters.getQ());
        int n = parameters.getN();
        int m = parameters.getM();

        Matrix bigB = new Matrix(n, m, this::nextUniform, q);

        Matrix t = new Matrix(1, n, this::nextUniform, q);

        Matrix e = new Matrix(1, m, this::nextGaussian, q);

        Matrix b = t.multiply(bigB, q).add(e, q);

        Matrix minusT = t.negate(q).addColumn(new BigInteger[]{ONE});
        Matrix bigA = bigB.addRow(b.asVector());
        return new KeyPair(
                new LWESecretKey(minusT, q),
                new LWEPublicKey(bigA, q)
        );
    }

    public Ciphertext encrypt(boolean x, PublicKey publicKey) {
        LWEPublicKey key = assertOwnKey(publicKey);

        Matrix a = key.getKey();
        BigInteger q = key.getQ();
        int n = a.getRows();

        Matrix bigG = LWEUtils.createG(n, q);
        Matrix r = new Matrix(a.getColumns(), bigG.getColumns(), this::nextUniform, BigInteger.valueOf(2));

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
        final LWESecretKey sk = (LWESecretKey) secretKey;

        Matrix cMatrix = ((LWECiphertext) c).getC();
        final Matrix s = sk.getS();

        final BigInteger q = sk.getQ();
        final Matrix sC = s.multiply(cMatrix, q);

        final Matrix bigG = LWEUtils.createG(s.getColumns(), q);
        final Matrix sG = s.multiply(bigG, q);

        BigInteger trueNoise = calculateNoiseFromAssumption(sC, sG, true, q);
        BigInteger falseNoise = calculateNoiseFromAssumption(sC, sG, false, q);

        return falseNoise.compareTo(trueNoise) >= 0;
    }

    /**
     * Calculates the noise in the ciphertext, based on the assumption that C = ENC(assumption)
     *
     * @param sC         secret key * ciphertext
     * @param sG         secret key * G
     * @param assumption assumed encrypted value
     * @param q          modulus
     * @return the average noise
     */
    private BigInteger calculateNoiseFromAssumption(Matrix sC, Matrix sG, boolean assumption, BigInteger q) {
        int logQ = logQ(q);

        Matrix xsG = sG.multiply(assumption ? ONE : ZERO, q);

        Optional<BigInteger> reduce = IntStream.range(0, sC.getColumns()).parallel()
                .filter(i -> i % (logQ) == 0)//Only read one for each g in G
                .map(i -> i + logQ - 1)//read the index corresponding to the most significant bit
                .mapToObj(idx -> {//Calculates noise on index 'idx'
                    BigInteger leftBitValue = sC.get(0, idx);
                    BigInteger zeroHBitValue = xsG.get(0, idx);

                    return leftBitValue.subtract(zeroHBitValue).mod(q).min(
                            zeroHBitValue.subtract(leftBitValue).mod(q)
                    );
                })
                .reduce(BigInteger::add);

        return reduce.map(bigInteger -> bigInteger.divide(valueOf(logQ))).orElse(null);
    }

    @Override
    public Ciphertext not(Ciphertext c, PublicKey pk) {
        LWEPublicKey key = assertOwnKey(pk);
        Matrix c1M = assertOwnCiphertext(c).getC();

        BigInteger q = key.getQ();

        Matrix bigG = LWEUtils.createG(c1M.getRows(), q);
        Matrix cRes = bigG.subtract(c1M, q);
        return new LWECiphertext(cRes);
    }

    @Override
    public Ciphertext nand(Ciphertext c1, Ciphertext c2, PublicKey pk) {
        return not(and(c1, c2, pk), pk);
    }

    @Override
    public Ciphertext and(Ciphertext c1, Ciphertext c2, PublicKey pk) {
        LWEPublicKey key = assertOwnKey(pk);
        Matrix c1M = assertOwnCiphertext(c1).getC();
        Matrix c2M = assertOwnCiphertext(c2).getC();

        BigInteger q = key.getQ();

        Matrix gInverse = LWEUtils.calculateGInverse(c2M, q);
        Matrix res = c1M.multiply(gInverse, q);
        return new LWECiphertext(res);
    }

    @Override
    public Ciphertext or(Ciphertext c1, Ciphertext c2, PublicKey pk) {
        return nand(not(c1, pk), not(c2, pk), pk);
    }

    @Override
    public Ciphertext xor(Ciphertext c1, Ciphertext c2, PublicKey pk) {
        Matrix c1Matrix = assertOwnCiphertext(c1).getC();
        Matrix c2Matrix = assertOwnCiphertext(c2).getC();
        BigInteger q = assertOwnKey(pk).getQ();
        return new LWECiphertext(c1Matrix.add(c2Matrix, q));
    }

    private LWEPublicKey assertOwnKey(PublicKey pk) {
        if (!(pk instanceof LWEPublicKey)) {
            throw new RuntimeException("Key must be for LWE system");
        }
        return (LWEPublicKey) pk;
    }

    private LWECiphertext assertOwnCiphertext(Ciphertext c) {
        if (!(c instanceof LWECiphertext)) {
            throw new RuntimeException("Ciphertext must be for LWE system");
        }
        return (LWECiphertext) c;
    }
}
