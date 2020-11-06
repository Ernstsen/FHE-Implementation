package dk.mmj.matrix;


import java.math.BigInteger;

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
                inner[row][col] = BigInteger.ZERO;
            }
            System.arraycopy(g, 0, inner[row], row, g.length);
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
}
