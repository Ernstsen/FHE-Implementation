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

    @Test
    public void createG() {
        int n = 5;
        int q = 2<<5;

        Matrix g = LWEUtils.createG(n, valueOf(q));

        Matrix expected = new Matrix(new BigInteger[][]{
                toBigIntArray(new int[]{1, 2, 4, 8, 16, 32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
                toBigIntArray(new int[]{0, 0, 0, 0, 0, 0, 1, 2, 4, 8, 16, 32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
                toBigIntArray(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 4, 8, 16, 32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
                toBigIntArray(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 4, 8, 16, 32, 0, 0, 0, 0, 0, 0}),
                toBigIntArray(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 4, 8, 16, 32})
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
        final BigInteger q = valueOf(128);
        int n = 5;

        final Matrix valMatrix = new Matrix(new BigInteger[][]{
                {valueOf(1), valueOf(3), valueOf(7), valueOf(8)},
                {valueOf(11), valueOf(4), valueOf(89), valueOf(122)},
                {valueOf(21), valueOf(6), valueOf(76), valueOf(99)},
                {valueOf(41), valueOf(33), valueOf(34), valueOf(46)},
                {valueOf(51), valueOf(123), valueOf(26), valueOf(38)},
        });

        Matrix gInverse = LWEUtils.calculateGInverse(valMatrix, q);
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
