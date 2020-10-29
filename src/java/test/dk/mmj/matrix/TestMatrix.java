package dk.mmj.matrix;

import org.junit.Test;

import java.math.BigInteger;
import java.security.SecureRandom;

import static java.math.BigInteger.valueOf;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertNotEquals;

public class TestMatrix {

    @Test
    public void testMatrixSize() {

        Matrix matrix = new Matrix(89, 3);

        assertEquals("Matrix did not have the right amount of rows", 89, matrix.getRows());
        assertEquals("Matrix did not have the right amount of columns", 3, matrix.getColumns());
    }

    @Test
    public void testNoNullFieldWhenRandom() {
        SecureRandom rand = new SecureRandom();

        BigInteger roof = valueOf(500);

        Matrix matrix = new Matrix(8, 5, rand, roof);

        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 5; column++) {
                assertNotNull(matrix.get(row, column));
            }
        }
    }

    @Test
    public void shouldBeEqual() {
        BigInteger[][] inner = {
                new BigInteger[]{valueOf(1), valueOf(7), valueOf(89)},
                new BigInteger[]{valueOf(345), valueOf(324), valueOf(123)},
                new BigInteger[]{valueOf(355), valueOf(55), valueOf(239)}
        };
        BigInteger[][] inner2 = {
                new BigInteger[]{valueOf(1), valueOf(7), valueOf(89)},
                new BigInteger[]{valueOf(345), valueOf(324), valueOf(123)},
                new BigInteger[]{valueOf(355), valueOf(55), valueOf(239)}
        };

        Matrix matrix = new Matrix(inner);
        Matrix alternative = new Matrix(inner2);

        assertEquals("Matrices should be equals", matrix, alternative);
        assertEquals("Matrices toString should be equals", matrix.toString(), alternative.toString());
    }

    @Test
    public void shouldNotBeEqual() {
        BigInteger[][] inner = {
                new BigInteger[]{valueOf(1), valueOf(7), valueOf(89)},
                new BigInteger[]{valueOf(345), valueOf(324), valueOf(123)},
                new BigInteger[]{valueOf(355), valueOf(55), valueOf(239)}
        };
        BigInteger[][] innerAlt = {
                new BigInteger[]{valueOf(1), valueOf(7), valueOf(89)},
                new BigInteger[]{valueOf(344), valueOf(324), valueOf(123)},
                new BigInteger[]{valueOf(355), valueOf(55), valueOf(239)}
        };

        Matrix matrix = new Matrix(inner);
        Matrix alternative = new Matrix(innerAlt);

        assertNotEquals("Matrices should not be equals", matrix, alternative);
    }

    @Test
    public void testMatrixMultiplication() {
        BigInteger[][] innerA = {
                new BigInteger[]{valueOf(8), valueOf(6)},
                new BigInteger[]{valueOf(86), valueOf(2)},
                new BigInteger[]{valueOf(9), valueOf(65)},
                new BigInteger[]{valueOf(2), valueOf(3)}
        };
        BigInteger[][] innerB = {
                new BigInteger[]{valueOf(1), valueOf(4), valueOf(5)},
                new BigInteger[]{valueOf(-5), valueOf(8), valueOf(9)},
        };
        BigInteger[][] innerC = {
                new BigInteger[]{valueOf(-22), valueOf(80), valueOf(94)},
                new BigInteger[]{valueOf(76), valueOf(360), valueOf(448)},
                new BigInteger[]{valueOf(-316), valueOf(556), valueOf(630)},
                new BigInteger[]{valueOf(-13), valueOf(32), valueOf(37)}
        };

        Matrix b = new Matrix(innerB);
        Matrix a = new Matrix(innerA);
        Matrix c = new Matrix(innerC);

        Matrix product = a.multiply(b);

        assertEquals("Matrix multiplication went wrong", c, product);
    }

    @Test
    public void testMatrixAdd() {
        BigInteger[][] innerA = {
                new BigInteger[]{valueOf(1), valueOf(4), valueOf(5)},
                new BigInteger[]{valueOf(-5), valueOf(8), valueOf(9)},
        };
        BigInteger[][] innerB = {
                new BigInteger[]{valueOf(8), valueOf(5), valueOf(9)},
                new BigInteger[]{valueOf(4), valueOf(21), valueOf(5)},
        };
        BigInteger[][] innerC = {
                new BigInteger[]{valueOf(9), valueOf(9), valueOf(14)},
                new BigInteger[]{valueOf(-1), valueOf(29), valueOf(14)}
        };

        Matrix a = new Matrix(innerA);
        Matrix b = new Matrix(innerB);
        Matrix c = new Matrix(innerC);

        Matrix sum = a.add(b);

        assertEquals("Matrix addition went wrong", c, sum);
    }

}
