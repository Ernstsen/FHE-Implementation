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
    public void shouldBeEqual(){
        BigInteger[][] inner = {
                new BigInteger[]{valueOf(1), valueOf(7), valueOf(89)},
                new BigInteger[]{valueOf(345), valueOf(324), valueOf(123)},
                new BigInteger[]{valueOf(355), valueOf(55), valueOf(239)}
        };

        Matrix matrix = new Matrix(inner);
        Matrix alternative = new Matrix(inner);

        assertEquals("Matrices should be equals", matrix, alternative);
    }

    @Test
    public void shouldNotBeEqual(){
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

}
