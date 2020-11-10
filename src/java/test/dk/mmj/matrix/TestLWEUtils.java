package dk.mmj.matrix;

import org.junit.Test;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.valueOf;
import static org.junit.Assert.*;

public class TestLWEUtils {

    private static BigInteger[] toBigIntArray(int[] ints) {
        return Arrays.stream(ints).mapToObj(BigInteger::valueOf).toArray(BigInteger[]::new);
    }

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
//        final BigInteger[] vals = {valueOf(1), valueOf(3), valueOf(7), valueOf(8)};
//        final BigInteger[][] inner = Arrays.stream(vals).map(v -> Matrix.decompose(v, q.bitLength())).map(Matrix::asVector).toArray(BigInteger[][]::new);
//        final Matrix matrix = new Matrix(inner);



        final Matrix val = new Matrix(new BigInteger[][]{
                {valueOf(1), valueOf(0), valueOf(0), valueOf(0)},//1
                {valueOf(1), valueOf(1), valueOf(0), valueOf(0)},//3
                {valueOf(1), valueOf(1), valueOf(1), valueOf(0)}//7
        }).transpose();//numbers in columns
        final Matrix g1 = LWEUtils.createG(4, q);

        final Matrix multiply1 = g1.transpose().multiply(val, q);

        assertEquals("Should be one", ONE, Arrays.stream(multiply1.transpose().getRow(0)).reduce(BigInteger::add).orElse(BigInteger.ZERO));
        assertEquals("Should be one", valueOf(3), Arrays.stream(multiply1.transpose().getRow(1)).reduce(BigInteger::add).orElse(BigInteger.ZERO));
        assertEquals("Should be one", valueOf(7), Arrays.stream(multiply1.transpose().getRow(2)).reduce(BigInteger::add).orElse(BigInteger.ZERO));

        final Matrix decompose = Matrix.decompose(valueOf(42), 6);
        final BigInteger[] g = LWEUtils.calculateSmallG(6);

        final Matrix gMatrix = new Matrix(new BigInteger[][]{g});

        final Matrix multiply = gMatrix.multiply(decompose, valueOf(1_000_000));

        final BigInteger[] bigIntegers = multiply.asVector();
        assertEquals(valueOf(42), bigIntegers[0]);
        fail("Still not testing the correct thing..");
    }
}
