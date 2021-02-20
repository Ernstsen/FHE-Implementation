package dk.mmj.matrix;


import java.math.BigInteger;
import java.util.Arrays;

import static java.math.BigInteger.*;

@SuppressWarnings("UnnecessaryLocalVariable")//Readability is important
public class LWEUtils {
    private static Matrix lastCalculatedG = null;

    /**
     * Calculates logQ of a BigInteger q, which is a power of 2.
     *
     * @param q the q
     * @return Number of bits needed to represent numbers in the ring Z_q
     */
    public static int logQ(BigInteger q) {
        return q.bitLength() - 1;
    }


    /**
     * Creates the matrix G for use in LWE encryption
     */
    public static Matrix createG(int n, BigInteger q) {
        int rows = n;
        int ceilLogQ = logQ(q);
        int columns = n * ceilLogQ;

        if(lastCalculatedG != null && lastCalculatedG.getRows() == rows && lastCalculatedG.getColumns() == columns){
            return lastCalculatedG;
        }

        BigInteger[][] inner = new BigInteger[rows][columns];

        BigInteger[] g = calculateSmallG(ceilLogQ);

        for (int row = 0; row < rows; row++) {
            Arrays.fill(inner[row], ZERO);
            System.arraycopy(g, 0, inner[row], row * g.length, g.length);
        }

        return lastCalculatedG = new Matrix(inner);
    }


    /**
     * Calculates the vector <i>g</i>
     *
     * @return vector g
     */
    public static BigInteger[] calculateSmallG(int bitLength) {
        BigInteger[] res = new BigInteger[bitLength];

        for (int i = 0; i < bitLength; i++) {
            res[i] = BigInteger.valueOf(2).pow(i);
        }

        return res;
    }

    /**
     * Inner product between two vectors
     *
     * @param v1 left vector
     * @param v2 right vector
     * @return result
     */
    public static BigInteger innerProduct(BigInteger[] v1, BigInteger[] v2) {
        if (v1.length != v2.length) {
            throw new MalformedMatrixException("Vectors must be of equal length. Was: v1=" + v1.length + " v2=" + v2.length);
        }

        BigInteger res = BigInteger.ZERO;
        for (int i = 0; i < v1.length; i++) {
            res = res.add(v1[i].multiply(v2[i]));
        }

        return res;
    }

    /**
     * Evaluates G^{-1}(m) function on matrix m.
     *
     * @param m matrix (n x n*logQ)
     * @param q q used in LWE system
     * @return new Matrix
     */
    public static Matrix calculateGInverse(Matrix m, BigInteger q) {
        Matrix mTrans = m.transpose();
        int ceilLogQ = logQ(q);

        BigInteger[][] inner = new BigInteger[mTrans.getRows()][mTrans.getColumns() * ceilLogQ];
        for (int row = 0; row < mTrans.getRows(); row++) {
            for (int col = 0; col < mTrans.getColumns(); col++) {
                BigInteger[] decompose = Matrix.decompose(mTrans.get(row, col), ceilLogQ).asVector();
                System.arraycopy(decompose, 0, inner[row], col * ceilLogQ, ceilLogQ);
            }
        }

        Matrix transpose = new Matrix(inner).transpose();
        return transpose;
    }

    /**
     * Calculates sum of all entries in array
     *
     * @param arr array to calculate sum of
     * @return sum of all entries
     */
    public static BigInteger arraySum(BigInteger[] arr) {
        return Arrays.stream(arr).reduce(BigInteger::add).orElse(BigInteger.ZERO);
    }


    /**
     * Reads a bit from the ciphertext
     *
     * @param sc        secret key multiplied with ciphertext
     * @param sg        secret key multiplied with G
     * @param modulus   modulus used in cryptosystem
     * @param bitNumber 0-indexed bit-number to be extracted
     * @return whether the decrypted bit is true or false
     * @deprecated Old decryption method - kept for possibility of reverting
     */
    @Deprecated
    public static boolean readBit(Matrix sc, Matrix sg,
                                  BigInteger modulus, int bitNumber) {
        final BigInteger leftBitValue = sc.get(0, bitNumber);

        final Matrix zeroHypothesis = sg.multiply(ZERO, modulus);
        final Matrix oneHypothesis = sg.multiply(ONE, modulus);

        final BigInteger zeroHBitValue = zeroHypothesis.get(0, bitNumber);
        final BigInteger oneHBitValue = oneHypothesis.get(0, bitNumber);

        final BigInteger zeroDiff = leftBitValue.subtract(zeroHBitValue).mod(modulus).min(
                zeroHBitValue.subtract(leftBitValue).mod(modulus)
        );
        final BigInteger oneDiff = leftBitValue.subtract(oneHBitValue).mod(modulus).min(
                oneHBitValue.subtract(leftBitValue).mod(modulus)
        );

        //Returns true if zeroDiff is larger than or equals to oneDiff
        return zeroDiff.compareTo(oneDiff) >= 0;
    }
}
