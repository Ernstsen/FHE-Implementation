package dk.mmj.matrix;

import org.junit.Test;

import java.math.BigInteger;
import java.security.SecureRandom;

import static java.math.BigInteger.*;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotEquals;

public class TestMatrix {

    public static final BigInteger MODULO = new BigInteger("1000000");

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

        Matrix matrix = new Matrix(8, 5, (q) -> new BigInteger(q.bitCount(), rand).mod(q), roof);

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
    public void testAddRow() {
        BigInteger[][] inner = {
                {valueOf(1), valueOf(4), valueOf(5)},
                {valueOf(-5), valueOf(8), valueOf(9)},
        };
        Matrix matrix = new Matrix(inner);

        BigInteger[][] expectedInner = {
                {valueOf(1), valueOf(4), valueOf(5)},
                {valueOf(-5), valueOf(8), valueOf(9)},
                {valueOf(4), valueOf(8), valueOf(9)}
        };
        Matrix expected = new Matrix(expectedInner);

        BigInteger[] newRow = {valueOf(4), valueOf(8), valueOf(9)};
        Matrix res = matrix.addRow(newRow);

        assertEquals("Adding row had unexpected result",
                expected,
                res);
    }

    @Test
    public void testAddColumn() {
        BigInteger[][] inner = {
                {valueOf(1), valueOf(4), valueOf(5)},
                {valueOf(-5), valueOf(8), valueOf(9)},
        };
        Matrix matrix = new Matrix(inner);

        BigInteger[][] expectedInner = {
                {valueOf(1), valueOf(4), valueOf(5), valueOf(4)},
                {valueOf(-5), valueOf(8), valueOf(9), valueOf(89)},
        };
        Matrix expected = new Matrix(expectedInner);

        BigInteger[] newColumn = {valueOf(4), valueOf(89)};
        Matrix res = matrix.addColumn(newColumn);

        assertEquals("Adding row had unexpected result", expected, res);
    }

    @Test
    public void testNegate() {
        BigInteger[][] inner = {
                {valueOf(1), valueOf(4), valueOf(5)},
                {valueOf(-5), valueOf(8), valueOf(9)},
        };
        Matrix matrix = new Matrix(inner);
        Matrix res = matrix.negate(MODULO);


        for (int row = 0; row < matrix.getRows(); row++) {
            for (int col = 0; col < matrix.getColumns(); col++) {
                assertEquals("Value was not negated. row=" + row + ", column=" + col,
                        matrix.get(row, col).negate().mod(MODULO), res.get(row, col));
            }
        }
    }

    @Test
    public void testGetRow(){
        BigInteger[] row1 = {valueOf(1), valueOf(4), valueOf(5)};
        BigInteger[] row2 = {valueOf(-5), valueOf(8), valueOf(9)};
        BigInteger[][] inner = {
                row1,
                row2,
        };
        Matrix matrix = new Matrix(inner);

        assertArrayEquals("First row did not match ", row1, matrix.getRow(0));
        assertArrayEquals("Second row did not match ", row2, matrix.getRow(1));
    }

    @Test
    public void testMatrixMultiplication() {
        BigInteger[][] innerA = {
                {valueOf(8), valueOf(6)},
                {valueOf(86), valueOf(2)},
                {valueOf(9), valueOf(65)},
                {valueOf(2), valueOf(3)}
        };
        BigInteger[][] innerB = {
                {valueOf(1), valueOf(4), valueOf(5)},
                {valueOf(-5), valueOf(8), valueOf(9)},
        };
        BigInteger[][] innerC = {
                {valueOf(-22).mod(MODULO), valueOf(80), valueOf(94)},
                {valueOf(76), valueOf(360), valueOf(448)},
                {valueOf(-316).mod(MODULO), valueOf(556), valueOf(630)},
                {valueOf(-13).mod(MODULO), valueOf(32), valueOf(37)}
        };

        Matrix b = new Matrix(innerB);
        Matrix a = new Matrix(innerA);
        Matrix c = new Matrix(innerC);

        Matrix product = a.multiply(b, MODULO);

        assertEquals("Matrix multiplication went wrong", c, product);
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    @Test
    public void testMatrixMultiplicationWithConstant() {
        BigInteger[][] inner = {
                {valueOf(1), valueOf(4), valueOf(5)},
                {valueOf(5), valueOf(8), valueOf(9)},
        };

        BigInteger[][] innerExpected = {
                {valueOf(1 * 3), valueOf(4 * 3), valueOf(5 * 3)},
                {valueOf(5 * 3), valueOf(8 * 3), valueOf(9 * 3)},
        };

        Matrix matrix = new Matrix(inner);
        Matrix res = matrix.multiply(valueOf(3), MODULO);

        assertEquals("Unexpected result", new Matrix(innerExpected), res);
    }


    @Test
    public void testMatrixAdd() {
        BigInteger[][] innerA = {
                {valueOf(1), valueOf(4), valueOf(5)},
                {valueOf(-5), valueOf(8), valueOf(9)},
        };
        BigInteger[][] innerB = {
                {valueOf(8), valueOf(5), valueOf(9)},
                {valueOf(4), valueOf(21), valueOf(5)},
        };
        BigInteger[][] innerC = {
                {valueOf(9), valueOf(9), valueOf(14)},
                {MODULO.add(valueOf(1).negate()), valueOf(29), valueOf(14)}
        };

        Matrix a = new Matrix(innerA);
        Matrix b = new Matrix(innerB);
        Matrix c = new Matrix(innerC);

        Matrix sum = a.add(b, MODULO);

        assertEquals("Matrix addition went wrong", c, sum);
    }

    @Test
    public void testMatrixAddModulo() {
        BigInteger[][] innerA = {
                {valueOf(1), valueOf(4)},
                {valueOf(5), valueOf(8)},
        };

        BigInteger[][] innerC = {
                {valueOf(2), valueOf(8)},
                {valueOf(0), valueOf(6)}
        };

        Matrix a = new Matrix(innerA);
        Matrix c = new Matrix(innerC);

        Matrix sum = a.add(a, valueOf(10));

        assertEquals("Matrix addition went wrong, using modulo", c, sum);
    }


    @Test
    public void testMatrixMultiplicationModulo() {
        BigInteger[][] innerA = {
                {valueOf(2), valueOf(4)},
                {valueOf(8), valueOf(9)},
        };

        BigInteger[][] innerC = {
                {valueOf(6), valueOf(4)},
                {valueOf(8), valueOf(3)}
        };

        Matrix a = new Matrix(innerA);
        Matrix c = new Matrix(innerC);

        Matrix sum = a.multiply(a, valueOf(10));

        assertEquals("Matrix multiplication went wrong, using modulo", c, sum);
    }

    @Test
    public void testRowAsVector() {
        BigInteger[] row = {valueOf(2), valueOf(4)};
        BigInteger[][] inner = {
                row,
        };
        Matrix matrix = new Matrix(inner);

        assertArrayEquals("Did not return the row as expected", row, matrix.asVector());
    }

    @Test
    public void testColumnAsVector() {
        BigInteger[] column = {valueOf(2), valueOf(4)};
        BigInteger[][] inner = {
                {column[0]},
                {column[1]},
        };
        Matrix matrix = new Matrix(inner);

        assertArrayEquals("Did not return the column as expected", column, matrix.asVector());

    }

    @Test
    public void testDecomposeInteger() {
        BigInteger number = new BigInteger("42");

        Matrix decomposition = Matrix.decompose(number, 6);

        BigInteger[] vectorDecomposition = decomposition.asVector();

        assertEquals("Wrong size of decomposition", 6, vectorDecomposition.length);

        BigInteger[] expected = {ZERO, ONE, ZERO, ONE, ZERO, ONE};
        assertArrayEquals("Decomposition not as expected", expected, vectorDecomposition);
    }

    @Test
    public void testTranspose() {
        final Matrix org = new Matrix(
                new BigInteger[][]{
                        {valueOf(4), valueOf(8), valueOf(12)},
                        {valueOf(45), valueOf(15), valueOf(56)}}
        );

        final Matrix expected = new Matrix(
                new BigInteger[][]{
                        {valueOf(4), valueOf(45)},
                        {valueOf(8), valueOf(15)},
                        {valueOf(12), valueOf(56)}
                }
        );

        assertEquals("Transpose not working", expected, org.transpose());
    }

}
