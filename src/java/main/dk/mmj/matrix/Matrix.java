package dk.mmj.matrix;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Objects;

/**
 * Matrix of BigIntegers
 */
public class Matrix {
    private final int nrOfCols;
    private final int nrOfRows;
    private final BigInteger[][] inner;


    /**
     * Constructor for fixed data
     * @param matrix the data
     */
    Matrix(BigInteger[][] matrix){
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
     * @param rand provider of randomness for the instantiation
     * @param q is the biggest allowed number (All calculations are mod q)
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
     * @param row the row
     * @param column the column
     * @return the value
     */
    public BigInteger get(int row, int column){
        return inner[row][column];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Matrix matrix = (Matrix) o;
        return nrOfCols == matrix.nrOfCols &&
                nrOfRows == matrix.nrOfRows &&
                Arrays.equals(inner, matrix.inner);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(nrOfCols, nrOfRows);
        result = 31 * result + Arrays.hashCode(inner);
        return result;
    }
}
