package dk.mmj.matrix;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

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
    public Matrix(int nrOfRows, int nrOfCols, Random rand, BigInteger q) {
        this(nrOfRows, nrOfCols);
        for (int col = 0; col < nrOfCols; col++) {
            for (int row = 0; row < nrOfRows; row++) {
                inner[row][col] = rand.nextRandom(q);
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

    /**
     * @param row which row to get
     * @return the row as a BigInteger array
     */
    public BigInteger[] getRow(int row){
        return inner[row];
    }

    /**
     * If the matrix has either one row, or one column, this method will return said row/column as an array
     *
     * @return the matrix as a vector
     */
    public BigInteger[] asVector() {
        if (nrOfRows != 1 && nrOfCols != 1) {
            throw new MalformedMatrixException("Matrix is not a vector");
        }

        if (nrOfRows == 1) {
            return inner[0];
        }

        BigInteger[] res = new BigInteger[nrOfRows];

        for (int row = 0; row < nrOfRows; row++) {
            res[row] = inner[row][0];
        }

        return res;
    }

    /**
     * Does matrix multiplication, mod the modulo parameter
     *
     * @param b      right-hand matrix
     * @param modulo the modulo for the group
     * @return this X B mod <i>modulo</i>
     */
    @SuppressWarnings("UnnecessaryLocalVariable")//Readability
    public Matrix multiply(Matrix b, BigInteger modulo) {
        if (nrOfCols != b.nrOfRows) {
            throw new MalformedMatrixException("Matrix with dimensions " + nrOfRows + "x" + nrOfCols +
                    " cannot be multiplied with matrix with dimensions " + b.nrOfRows + "x" + b.nrOfCols);
        }
        Matrix a = this;

        int m = a.nrOfRows;
        int p = b.nrOfCols;

        final BigInteger[][] result = new BigInteger[m][p];

        //Compute the resulting row, using a parallel stream to properly utilize multi-core CPU
        IntStream.range(0, m).parallel().forEach(computeRowMultiplication(result, a, b, modulo));

        return new Matrix(result);
    }

    /**
     * Multiplies matrix with a constant c
     *
     * @param c      the constant
     * @param modulo the modulo
     * @return new matrix which is this * c
     */
    public Matrix multiply(BigInteger c, BigInteger modulo) {
        int rows = getRows();
        int columns = getColumns();
        BigInteger[][] res = new BigInteger[rows][columns];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                res[row][col] = get(row, col).multiply(c).mod(modulo);
            }
        }

        return new Matrix(res);
    }

    /**
         * Builds an {@link IntConsumer} which computes a row of the resulting matrix, used in matrix multiplication
         *
         * @param res    the matrix to write the resulting row to
         * @param a      matrix a from the multiplication
         * @param b      matrix b from he multiplication
         * @param modulo the modulo for the multiplication
         * @return an intConsumer for computing multiplication for a given row
         */
    @SuppressWarnings("UnnecessaryLocalVariable")
    private IntConsumer computeRowMultiplication(final BigInteger[][] res, final Matrix a, final Matrix b, final BigInteger modulo) {
        final int n = b.nrOfRows;
        final int p = b.nrOfCols;
        return row -> {
            for (int col = 0; col < p; col++) {
                BigInteger partial = BigInteger.ZERO;

                for (int i = 0; i < n; i++) {
                    BigInteger aVal = a.get(row, i);
                    BigInteger bVal = b.get(i, col);
                    partial = partial.add(aVal.multiply(bVal));
                }
                res[row][col] = partial.mod(modulo);
            }
        };
    }

    /**
     * Does matrix addition, mod the modulo parameter
     *
     * @param b      other matrix
     * @param modulo the modulo for the group
     * @return this + B mod <i>modulo</i>
     */
    public Matrix add(Matrix b, BigInteger modulo) {
        Matrix a = this;
        if (a.getColumns() != b.getColumns() ||
                a.getRows() != b.getRows()) {
            throw new MalformedMatrixException("Matrix with dimensions " + nrOfRows + "x" + nrOfCols +
                    " cannot be added to matrix with dimensions " + b.nrOfRows + "x" + b.nrOfCols);
        }

        BigInteger[][] res = new BigInteger[a.nrOfRows][a.nrOfCols];

        for (int row = 0; row < a.getRows(); row++) {
            for (int column = 0; column < a.getColumns(); column++) {
                BigInteger aVal = a.get(row, column);
                BigInteger bVal = b.get(row, column);
                res[row][column] = aVal.add(bVal).mod(modulo);
            }
        }

        return new Matrix(res);
    }

    /**
     * Creates a matrix, with the new row appended to the matrix
     *
     * @param newRow the row to be appended underneath. Must have length=numberOfCols
     * @return new matrix with the extra row
     */
    public Matrix addRow(BigInteger[] newRow) {
        if (newRow.length != nrOfCols) {
            throw new MalformedMatrixException("New row must have the same length as the matrix has columns");
        }

        BigInteger[][] res = new BigInteger[nrOfRows + 1][nrOfCols];
        for (int i = 0; i < nrOfRows; i++) {
            res[i] = Arrays.copyOf(inner[i], nrOfCols);
        }
        res[nrOfRows] = Arrays.copyOf(newRow, nrOfCols);

        return new Matrix(res);
    }

    /**
     * Creates a matrix, with the new column appended to the matrix
     *
     * @param newColumn the column to be appended to the right. Must have length=numberOfRows
     * @return new matrix with the extra column
     */
    public Matrix addColumn(BigInteger[] newColumn) {
        if (newColumn.length != nrOfRows) {
            throw new MalformedMatrixException("New column must have the same length as the matrix has rows");
        }

        BigInteger[][] res = new BigInteger[nrOfRows][nrOfCols + 1];
        for (int i = 0; i < nrOfRows; i++) {
            res[i] = Arrays.copyOf(inner[i], nrOfCols + 1);
            res[i][nrOfCols] = newColumn[i];
        }

        return new Matrix(res);
    }

    public Matrix negate() {
        BigInteger[][] res = new BigInteger[nrOfRows][nrOfCols];

        for (int row = 0; row < nrOfRows; row++) {
            for (int col = 0; col < nrOfCols; col++) {
                res[row][col] = inner[row][col].negate();
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
            sb.append(Arrays.toString(bigIntegers));
        }

        sb.append("]");


        return "Matrix{" +
                "inner=" + sb.toString() +
                '}';
    }

    public static Matrix decompose(BigInteger x){
        int len = x.bitLength();

        BigInteger[][] inner = new BigInteger[len][1];

        for (int i = 0; i < len; i++) {
            BigInteger pow = BigInteger.valueOf(2).pow(i);
            inner[i][0] = x.and(pow).equals(pow) ? BigInteger.ONE : BigInteger.ZERO;
        }

        return new Matrix(inner);
    }

    /**
     * Provides random values, without promise of distribution
     */
    public interface Random {
        /**
         * @return positive random value below q
         */
        BigInteger nextRandom(BigInteger q);
    }
}
