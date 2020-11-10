package dk.mmj.matrix;


import java.math.BigInteger;
import java.util.Arrays;

import static java.math.BigInteger.*;

@SuppressWarnings("UnnecessaryLocalVariable")//Readability is important
public class LWEUtils {


    /**
     * Creates the matrix G for use in LWE encryption
     */
    public static Matrix createG(int n, BigInteger q) {
        int rows = n;
        int ceilLogQ = q.bitLength();
        int columns = n * ceilLogQ;
        BigInteger[][] inner = new BigInteger[rows][columns];

        BigInteger[] g = calculateSmallG(ceilLogQ);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                if(row == col){
                    inner[row][col] = valueOf(2).pow(row);
                }else {
                    inner[row][col] = BigInteger.ZERO;
                }
            }
//            System.arraycopy(g, 0, inner[row], row, g.length);
        }

        return new Matrix(inner);
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

    public static Matrix calculateGInverse(Matrix g) {



        return null;
    }

    /**
     * Calculates sum of all entries in array
     *
     * @param arr array to calculate sum of
     * @return sum of all entries
     */
    public static BigInteger arraySum(BigInteger[] arr){
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
     */
    public static boolean readBit(Matrix sc, Matrix sg,
                                  BigInteger modulus, int bitNumber) {
        final BigInteger leftBitValue = sc.get(0, 0);

        final Matrix zeroHypothesis = sg.multiply(ZERO, modulus);
        final Matrix oneHypothesis = sg.multiply(ONE, modulus);

        final BigInteger zeroHBitValue = zeroHypothesis.get(0, bitNumber);
        final BigInteger oneHBitValue = oneHypothesis.get(0, bitNumber);

        final BigInteger zeroDiff = leftBitValue.subtract(zeroHBitValue).abs();
        final BigInteger oneDiff = leftBitValue.subtract(oneHBitValue).abs();

        //Returns true if oneDiff is larger than or equals to zeroDiff
        return zeroDiff.compareTo(oneDiff) >= 0;
    }
}
