package dk.mmj.matrix;

import org.junit.Ignore;
import org.junit.Test;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

import static dk.mmj.matrix.LWEUtils.arraySum;
import static java.math.BigInteger.*;
import static org.junit.Assert.*;

public class TestLWEUtils {

    private static BigInteger[] toBigIntArray(int[] ints) {
        return Arrays.stream(ints).mapToObj(BigInteger::valueOf).toArray(BigInteger[]::new);
    }

    @Ignore("We may change G impl")
    @Test
    public void createG() {
        int n = 5;
        int q = 50;

        Matrix g = LWEUtils.createG(n, valueOf(q));

        Matrix expected = new Matrix(new BigInteger[][]{
                toBigIntArray(new int[]{1, 2, 4, 8, 16, 32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
                toBigIntArray(new int[]{0, 1, 2, 4, 8, 16, 32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
                toBigIntArray(new int[]{0, 0, 1, 2, 4, 8, 16, 32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
                toBigIntArray(new int[]{0, 0, 0, 1, 2, 4, 8, 16, 32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
                toBigIntArray(new int[]{0, 0, 0, 0, 1, 2, 4, 8, 16, 32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0})
        });

        assertEquals("Unexpected output", expected, g);

    }

    @Test
    public void calculateSmallG() {
        BigInteger[] bigIntegers = LWEUtils.calculateSmallG(8);

        BigInteger[] expected = new BigInteger[]{
                valueOf(1), valueOf(2), valueOf(4), valueOf(8),
                valueOf(16), valueOf(32), valueOf(64), valueOf(128)
        };

        assertArrayEquals("Unexpected array returned", expected, bigIntegers);
    }

    @Test
    public void testDotProduct() {
        BigInteger[] v1 = {valueOf(1), valueOf(3), valueOf(-5)};
        BigInteger[] v2 = {valueOf(4), valueOf(-2), valueOf(-1)};

        BigInteger expected = valueOf(3);
        BigInteger res = LWEUtils.innerProduct(v1, v2);

        assertEquals(expected, res);

    }

    @Test
    public void testGInverse() {
        final BigInteger q = new BigInteger(16, new SecureRandom());
        final BigInteger[] vals = {valueOf(1), valueOf(3), valueOf(7), valueOf(8)};
        int n = 5;

        //BEGIN: DECOMPOSE
        //Write each value as a decomposed bitstring (row)
        final BigInteger[][] inner = Arrays.stream(vals)
                .map(v -> Matrix.decompose(v, q.bitLength()))
                .map(Matrix::asVector)
                .toArray(BigInteger[][]::new);

        BigInteger[][] actualInner = new BigInteger[inner.length][n * q.bitLength()];
        for (int i = 0; i < inner.length; i++) {
            actualInner[i] = new BigInteger[n * q.bitLength()];
            Arrays.fill(actualInner[i], ZERO);
            System.arraycopy(inner[i], 0, actualInner[i], 0, inner[i].length);
        }

        final Matrix matrix = new Matrix(actualInner).transpose();//We create the matrix, and then write the numbers on columns, instead of rows

        //BEGIN: RECOMPOSE
        Matrix g1 = LWEUtils.createG(n, q);
        Matrix g = new Matrix(new BigInteger[][]{LWEUtils.calculateSmallG(n*q.bitLength())});

        final Matrix multiply1 = g1.multiply(matrix, q);
        Matrix readable = multiply1.transpose();//We transpose, so that numbers are on rows instead of columns - prettier to read

        BigInteger[] row = multiply1.getRow(0);
        assertEquals("Should be one", ONE, row[0]);
        assertEquals("Should be three", valueOf(3), row[1]);
        assertEquals("Should be seven", valueOf(7), row[2]);
        assertEquals("Should be eight", valueOf(8), row[3]);

        fail("Still not testing the correct thing..");
        final Matrix valMatrix = new Matrix(new BigInteger[][]{
                {valueOf(1), valueOf(3), valueOf(7), valueOf(8)},
                {valueOf(11), valueOf(23), valueOf(37), valueOf(48)}
        });

        Matrix gInverse = LWEUtils.calculateGInverse(valMatrix, n, q);
        Matrix multiply = LWEUtils.createG(n, q).multiply(gInverse, q);
        assertEquals("Should be back to original", valMatrix, multiply);


    }

    @Test
    public void testArraySum() {
        BigInteger[] bigIntegers = {valueOf(4), valueOf(87), valueOf(89)};
        BigInteger sum = arraySum(bigIntegers);

        assertEquals("Wrong sum calculated", valueOf(180), sum);
    }

    @Test
    public void testDecomposeRecompose() {
        //Number and vector:
        final Matrix decompose = Matrix.decompose(valueOf(42), 6);
        final BigInteger[] g = LWEUtils.calculateSmallG(6);

        final Matrix gMatrix = new Matrix(new BigInteger[][]{g});

        final Matrix multiply = gMatrix.multiply(decompose, valueOf(1_000_000));

        final BigInteger[] bigIntegers = multiply.asVector();
        assertEquals("Recompose failed", valueOf(42), bigIntegers[0]);
    }
}
