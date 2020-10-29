package dk.mmj.matrix;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Matrix of BigIntegers
 */
public class Matrix {
    private final int nrOfRows;
    private final int nrOfCols;
    private final BigInteger[][] inner;


    /**
     * Constructor for fixed data
     *
     * @param matrix the data
     */
    Matrix(BigInteger[][] matrix) {
        this.nrOfRows = matrix.length;
        this.nrOfCols = matrix[0].length;
        this.inner = matrix;
    }

    /**
     * Creates matrix with given dimensions - all entries are null
     *
     * @param nrOfRows number of rows
     * @param nrOfCols number of columns
     */
    public Matrix(int nrOfRows, int nrOfCols) {
        this.nrOfRows = nrOfRows;
        this.nrOfCols = nrOfCols;
        inner = new BigInteger[nrOfRows][nrOfCols];
    }

    /**
     * Creates matrix with given dimensions - all entries are random values
     *
     * @param nrOfRows number of rows
     * @param nrOfCols number of columns
     * @param rand     provider of randomness for the instantiation
     * @param q        is the biggest allowed number (All calculations are mod q)
     */
    public Matrix(int nrOfRows, int nrOfCols, SecureRandom rand, BigInteger q) {
        this(nrOfRows, nrOfCols);
        for (int col = 0; col < nrOfCols; col++) {
            for (int row = 0; row < nrOfRows; row++) {
                inner[row][col] = new BigInteger(q.bitCount(), rand).mod(q);
            }
        }
    }

    public int getRows() {
        return nrOfRows;
    }

    public int getColumns() {
        return nrOfCols;
    }

    /**
     * Reads a value of the matrix
     *
     * @param row    the row
     * @param column the column
     * @return the value
     */
    public BigInteger get(int row, int column) {
        return inner[row][column];
    }

    @SuppressWarnings("UnnecessaryLocalVariable")//Readability
    public Matrix multiply(Matrix b) {
        if (nrOfCols != b.nrOfRows) {
            throw new MalformedMatrixException("Matrix with dimensions " + nrOfRows + "x" + nrOfCols +
                    " cannot be multiplied with matrix with dimensions " + b.nrOfRows + "x" + b.nrOfCols);
        }
        Matrix a = this;

        int m = a.nrOfRows;
        int n = b.nrOfRows;
        int p = b.nrOfCols;

        BigInteger[][] result = new BigInteger[m][p];

        for (int row = 0; row < m; row++) {
            for (int col = 0; col < p; col++) {
                BigInteger partial = BigInteger.ZERO;

                for (int i = 0; i < n; i++) {
                    BigInteger aVal = a.get(row, i);
                    BigInteger bVal = b.get(i, col);
                    partial = partial.add(aVal.multiply(bVal));
                }

                result[row][col] = partial;
            }
        }

        return new Matrix(result);
    }

    public Matrix add(Matrix b) {
        Matrix a = this;
        if (a.getColumns() != b.getColumns() ||
                a.getRows() != b.getRows()) {
            throw new MalformedMatrixException("Matrix with dimensions " + nrOfRows + "x" + nrOfCols +
                    " cannot be added to matrix with dimensions " + b.nrOfRows + "x" + b.nrOfCols);
        }

        BigInteger[][] res = new BigInteger[a.nrOfRows][a.nrOfCols];

        for (int row = 0; row < a.getRows(); row++) {
            for (int column = 0; column < a.getColumns(); column++) {
                res[row][column] = a.get(row, column).add(b.get(row, column));
            }
        }

        return new Matrix(res);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Matrix matrix = (Matrix) o;

        if (nrOfCols != matrix.nrOfCols ||
                nrOfRows != matrix.nrOfRows) {
            return false;
        }

        for (int i = 0; i < inner.length; i++) {
            if (!Arrays.equals(inner[i], matrix.inner[i])) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (BigInteger[] bigIntegers : inner) {
            sb.append(Arrays.toString(bigIntegers)).append(",");
        }

        sb.append("]");


        return "Matrix{" +
                "inner=" + sb.toString() +
                '}';
    }
}
