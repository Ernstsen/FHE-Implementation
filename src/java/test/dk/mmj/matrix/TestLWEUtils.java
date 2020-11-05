package dk.mmj.matrix;

import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;

import static java.math.BigInteger.valueOf;
import static org.junit.Assert.*;

public class TestLWEUtils {

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

    private static BigInteger[] toBigIntArray(int[] ints){
        return Arrays.stream(ints).mapToObj(BigInteger::valueOf).toArray(BigInteger[]::new);
    }
}
