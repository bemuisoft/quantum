package org.bemuisoft.math.complex;

/**
 * A matrix is a rectangular array of numbers arranged in rows and columns.
 * The numbers can be real or complex.
 * 
 * @author Benno Muilwijk
 */
public class Matrix extends ComplexBase {

	private int rows;
	private int cols;
	private Complex[][] values;
	private boolean isFinal;
	
	/**
	 * Constructs a matrix with the specified number of rows and columns.
	 * 
	 * @param rows		number of rows
	 * @param columns	number of columns
	 * @param values	initial values, or null
	 */
	protected Matrix(int rows, int columns, Complex[][] values) {
		this.rows = rows;
		this.cols = columns;
		this.values = values;
	}
	
	/**
	 * Constructs a matrix with the specified number of rows and columns.
	 * All values are initialized to {@code null}.
	 * 
	 * @param rows		number of rows
	 * @param columns	number of columns
	 */
	public Matrix(int rows, int columns) {
		this(rows, columns, new Complex[rows][columns]);
	}

	/**
	 * Constructs a matrix with the specified values.
	 * 
	 * @param rows	array of initial row values
	 */
	public Matrix(Number[]... rows) {
		this(rows.length, rows[0].length);
		init(rows);
	}

	/**
	 * Returns the number of rows in this matrix.
	 * 
	 * @return	the number of rows
	 */
	public final int rows() {
		return rows;
	}

	/**
	 * Returns the number of columns in this matrix.
	 * 
	 * @return	the number of columns
	 */
	public final int columns() {
		return cols;
	}

	/**
	 * Answers whether this matrix is set to final state.
	 * 
	 * @return	{@code true} if this matrix is set final,
	 * 			{@code false} otherwise
	 * @see #setFinal()
	 */
	public final boolean isFinal() {
		return isFinal;
	}

	/**
	 * Sets this matrix to final state.
	 * That means its values can no longer be changed.
	 * 
	 * @return	this matrix
	 */
	public Matrix setFinal() {
		isFinal = true;
		return this;
	}

	/**
	 * Returns the value in the cell at the specified row and column.
	 * 
	 * @param row		the row index
	 * @param column	the column index
	 * @return			the value
	 */
	public ComplexNumber get(int row, int column) {
		final Complex v = getValue(row, column);
		if (v instanceof ComplexNumber) {
			return (ComplexNumber) v;
		}
		ComplexNumber value = new ComplexNumber(v);
		if (isFinal()) {
			setValue(row, column, value);
		}
		return value;
	}

	/**
	 * Returns the value in the cell at the specified row and column
	 * as a modifiable {@link Complex} number.
	 * <p>
	 * A change to the value of the returned object will be reflected in this matrix!
	 * This is only allowed until this matrix is set to final state.
	 * 
	 * @param row		the row index
	 * @param column	the column index
	 * @return			the {@link Complex} value
	 * @throws			IllegalStateException if this matrix is set final
	 * @see				#setFinal()
	 */
	protected final Complex getComplex(int row, int column) {
		if (isFinal()) {
			throw new IllegalStateException();
		}
		final Complex v = getValue(row, column);
		if (v.getClass() == Complex.class) {
			return v;
		}
		Complex value = new Complex(v);
		setValue(row, column, value);
		return value;
	}

	/**
	 * Returns the internal value in the cell at the specified row and column.
	 * 
	 * @param row		the row index
	 * @param column	the column index
	 * @return			the value
	 */
	protected Complex getValue(int row, int column) {
		return values[row][column];
	}

	/**
	 * Sets the internal value in the cell at the specified row and column.
	 * 
	 * @param row		the row index
	 * @param column	the column index
	 * @param value		the value to set
	 */
	protected void setValue(int row, int column, Complex value) {
		values[row][column] = value;
	}

	/**
	 * Sets the value in the cell at the specified row and column.
	 * 
	 * @param row		the row index
	 * @param column	the column index
	 * @param value		the value to set
	 * @throws			IllegalStateException if this matrix is set final
	 * @see				#setFinal()
	 * @see				#init(int, int, Number)
	 */
	public void set(int row, int column, Number value) {
		if (isFinal()) {
			throw new IllegalStateException("Matrix is final");
		}
		setValue(row, column, c(value));
	}

	/**
	 * Initializes the value in the cell at the specified row column.
	 * 
	 * @param row		the row index
	 * @param column	the column index
	 * @param value		the initial value
	 * @throws			IllegalStateException if the cell already has a value
	 * @see				#set(int, int, Number)
	 */
	public void init(int row, int column, Number value) {
		if (getValue(row, column) != null) {
			throw new IllegalStateException("Cell is initialized already");
		}
		setValue(row, column, c(value));
	}

	/**
	 * Initializes this matrix with the specified values.
	 * 
	 * @param rows	array of initial row values
	 * @throws		IllegalArgumentException if the number of rows does not match
	 * @throws		IllegalArgumentException if the number of values per row differs from number of columns
	 * @throws		IllegalStateException if any cell already has a value
	 */
	public void init(Number[]... rows) {
		check(rows.length == rows(), "Inconsistent number of rows");
		for (int i = 0; i < rows(); i++) {
			Number[] row = rows[i];
			check(row.length == columns(), "Inconsistent number of columns");
			for (int j = 0; j < columns(); j++) {
				init(i, j, row[j]);
			}
		}
	}

	/**
	 * Sets this matrix to the same values as the given matrix.
	 * 
	 * @param m	the source matrix
	 * @return	this matrix
	 * @throws	IllegalArgumentException if the source matrix has different dimensions
	 * @throws	IllegalStateException if this matrix is set final
	 * @see		#setFinal()
	 */
	public Matrix set(Matrix m) {
		if (isFinal()) {
			throw new IllegalStateException("Matrix is final");
		}
		check(m.rows == this.rows && m.cols == this.cols, "Matrix to set has different dimensions");
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				// set to complex copy in case value is not ComplexNumber
				setValue(i, j, cc(m.getValue(i, j)));
			}
		}
		return this;
	}

	/**
	 * Adds the given matrix to this matrix.
	 * <p>
	 * If this matrix is final, a new matrix is returned.
	 * Otherwise, this matrix is updated and returned.
	 * 
	 * @param m	the matrix to add
	 * @return	matrix with the result of the addition
	 * @throws	IllegalArgumentException if the matrix to add has different dimensions
	 */
	public Matrix add(Matrix m) {
		check(m.rows == this.rows && m.cols == this.cols, "Matrix to add has different dimensions");
		if (isFinal()) {
			return copy().add(m);
		}
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				getComplex(i, j).add(m.getValue(i, j));
			}
		}
		return this;
	}

	/**
	 * Subtracts the given matrix from this matrix.
	 * <p>
	 * If this matrix is final, a new matrix is returned.
	 * Otherwise, this matrix is updated and returned.
	 * 
	 * @param m	the matrix to subtract
	 * @return	matrix with the result of the subtraction
	 * @throws	IllegalArgumentException if the matrix to subtract has different dimensions
	 */
	public Matrix subtract(Matrix m) {
		check(m.rows == this.rows && m.cols == this.cols, "Matrix to subtract has different dimensions");
		if (isFinal()) {
			return copy().subtract(m);
		}
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				getComplex(i, j).subtract(m.getValue(i, j));
			}
		}
		return this;
	}

	/**
	 * Multiplies this matrix by the given scalar number.
	 * Each cell value is multiplied by the same number.
	 * <p>
	 * If this matrix is final, a new matrix is returned.
	 * Otherwise, this matrix is updated and returned.
	 * 
	 * @param x	the scalar number to multiply by
	 * @return	matrix with the result of the multiplication
	 */
	public Matrix multiply(Number x) {
		if (isFinal()) {
			return copy().multiply(x);
		}
		Complex z = cv(x);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				getComplex(i, j).multiply(z);
			}
		}
		return this;
	}

	/**
	 * Returns a 2x2 matrix with values from the intersection
	 * of the specified row/column indices.
	 * <p>
	 * Row 0 gets values from cells at (i, i) and (i, j).<br/>
	 * Row 1 gets values from cells at (j, i) and (j, j).<br/>
	 * 
	 * @param i	row and column index 0
	 * @param j	row and column index 1
	 * @return	matrix with two rows and two columns
	 */
	public Matrix subMatrix(int i, int j) {
		return new Matrix(row(getValue(i, i), getValue(i, j)), row(getValue(j, i), getValue(j, j)));
	}

	/**
	 * Returns a copy of this matrix.
	 * The copy is not final, so it can be modified.
	 * 
	 * @return	new copy of this matrix
	 */
	public Matrix copy() {
		Matrix m = Matrix.create(rows, cols);
		return m.set(this);
	}

	/**
	 * Returns the conjugate matrix of this matrix.
	 * Each cell is initialized with the complex conjugate value
	 * of the corresponding cell value in this matrix.
	 * 
	 * @return	the conjugate matrix
	 */
	public Matrix conjugate() {
		Matrix m = Matrix.create(rows, cols);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				m.setValue(i, j, getValue(i, j).conjugate());
			}
		}
		return m;
	}

	/**
	 * Returns the conjugate transpose matrix of this matrix.
	 * Each cell (i, j) is initialized with the complex conjugate value
	 * of cell (j, i) in this matrix.
	 * 
	 * @return	the conjugate transpose matrix
	 */
	public Matrix transpose() {
		return transpose(true);
	}

	/**
	 * Returns the conjugate transpose matrix of this matrix.
	 * Each cell (i, j) is initialized from the value
	 * of cell (j, i) in this matrix.
	 * <p>
	 * Only if the conjugate argument equals ({@code true},
	 * all cell values are conjugated.
	 * 
	 * @param conjugate	{@code true} for conjugate transpose
	 * @return	the transpose matrix
	 */
	public Matrix transpose(boolean conjugate) {
		Matrix m = Matrix.create(cols, rows);
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				Complex z = getValue(j, i);
				m.setValue(i, j, conjugate ? z.conjugate() : cc(z));
			}
		}
		return m;
	}

	/**
	 * Returns the trace of this matrix.
	 * <p>
	 * The trace of a square matrix is the sum of its diagonal entries.
	 * It is only defined for square matrices.
	 * 
	 * @return	the trace
	 * @throws	IllegalStateException if this matrix is not square
	 */
	public Complex trace() {
		checkState(isSquare(), "Trace is only defined for square matrices.");
		Complex tr = cv(0.0);
		for (int i = 0; i < rows; i++) {
			tr.add(getValue(i, i));
		}
		return tr;
	}

	/**
	 * Answers whether this matrix is a square matrix.
	 * <p>
	 * A matrix is square if the number of rows equals
	 * the number of columns.
	 * 
	 * @return	{@code true} if this matrix is square, {@code false} otherwise
	 */
	public boolean isSquare() {
		return (rows == cols);
	}

	/**
	 * Answers whether this matrix is a Hermitian matrix.
	 * <p>
	 * A matrix is Hermitian if it is a square matrix and
	 * the value of each cell (i, j) is the complex conjugate value
	 * of its transpose cell (j, i).
	 * 
	 * @return	{@code true} if this matrix is Hermitian, {@code false} otherwise
	 */
	public boolean isHermitian() {
		if (rows != cols) {
			// not square
			return false;
		}
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j <= i; j++) {
				Complex ij = getValue(i, j);
				Complex ji = getValue(j, i);
				double realDiff = Math.abs(ij.realPart() - ji.realPart());
				double imagDiff = Math.abs(ij.imaginaryPart() + ji.imaginaryPart());
				if (realDiff > 1e-8 || imagDiff > 1e-8) {
					// conjugate transpose is not the same
					return false;
				}
			}
		}
		// all conditions satisfied
		return true;
	}

	/**
	 * Answers whether this matrix is (almost) equal to the given matrix.
	 * <p>
	 * This is considered to be the case if all cell values of this matrix
	 * are equal or close to the corresponding cell values of the given matrix.
	 * This definition allows for rounding errors to be ignored.
	 * 
	 * @param m	the matrix to compare to
	 * @return	{@code true} if this matrix is close to m, {@code false} otherwise
	 * @see Complex#isCloseTo(Complex)
	 */
	public boolean isCloseTo(Matrix m) {
		if (m.rows != this.rows || m.cols != this.cols) {
			return false;
		}
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (!getValue(i, j).isCloseTo(m.getValue(i, j))) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Returns the square root of this matrix, if supported.
	 * <p>
	 * This method has a dependency on the
	 * <a href="https://central.sonatype.com/artifact/org.ojalgo/ojalgo/versions">
	 * ojAlgo</a> package
	 * and works only for the square root of a Hermitian matrix.
	 * 
	 * @return	the square root of this matrix
	 * @see #isHermitian()
	 */
	public Matrix sqrt() {
		// this might only work for Hermitian matrices
		EigenDecomposition ed = EigenDecomposition.get(this);
		DiagonalMatrix d = ed.getEigenvalues();
		Matrix u = ed.getEigenvectors();
		return Matrix.product(u, d.sqrt(), u.transpose(true));
	}

	//////////////////
	// Static methods
	//////////////////

	/**
	 * Creates a matrix with the specified number of rows and columns.
	 * All values are initialized to {@code null}.
	 * <p>
	 * If the number of columns is one and
	 * the numbers of rows is greater than one,
	 * a {@link ColumnVector} is returned.
	 * <p>
	 * If the number of rows is one and
	 * the numbers of columns is greater than one,
	 * a {@link RowVector} is returned.
	 * 
	 * @param rows		number of rows
	 * @param columns	number of columns
	 * @return			a new matrix
	 * @see ColumnVector#create(int)
	 * @see RowVector#create(int)
	 * @see DiagonalMatrix#create(int)
	 */
	public static Matrix create(int rows, int columns) {
		if (columns == 1 && rows > 1) {
			return new ColumnVector(rows);
		}
		if (rows == 1 && columns > 1) {
			return new RowVector(columns);
		}
		return new Matrix(rows, columns);
	}

	/**
	 * Creates a square matrix with the specified number of rows and columns.
	 * All values are initialized to {@code null}.
	 * 
	 * @param n	number of rows and columns
	 * @return	a new square matrix
	 */
	public static Matrix square(int n) {
		return new Matrix(n, n);
	}

	/**
	 * Creates a matrix with the specified number of rows and columns.
	 * All values are initialized to zero.
	 * <p>
	 * If the number of columns is one and
	 * the numbers of rows is greater than one,
	 * a {@link ColumnVector} is returned.
	 * <p>
	 * If the number of rows is one and
	 * the numbers of columns is greater than one,
	 * a {@link RowVector} is returned.
	 * 
	 * @param rows		number of rows
	 * @param columns	number of columns
	 * @return			a new zero matrix
	 */
	public static Matrix zero(int rows, int columns) {
		Matrix m = create(rows, columns);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				m.setValue(i, j, ZERO);
			}
		}
		return m;
	}

	/**
	 * Creates a square matrix with the specified number of rows and columns.
	 * All values are initialized to zero.
	 * 
	 * @param n	number of rows and columns
	 * @return	a new square zero matrix
	 */
	public static Matrix zero(int n) {
		return zero(n, n);
	}

	/**
	 * Creates an identity matrix with the specified number of rows and columns.
	 * All diagonal values are initialized to one.
	 * 
	 * @param n	number of rows and columns
	 * @return	a new identity matrix
	 * @see DiagonalMatrix
	 */
	public static DiagonalMatrix identity(int n) {
		return diagonal(n, ONE);
	}

	/**
	 * Creates a diagonal matrix with the specified number of rows and columns.
	 * All diagonal values are initialized to the specified number.
	 * 
	 * @param n		number of rows and columns
	 * @param value	initial value for each diagonal element
	 * @return		a new diagonal matrix
	 * @see DiagonalMatrix
	 */
	public static DiagonalMatrix diagonal(int n, Number value) {
		ComplexNumber cValue = c(value);
		DiagonalMatrix d = new DiagonalMatrix(n);
		for (int i = 0; i < n; i++) {
			d.setValue(i, i, cValue);
		}
		return d;
	}

	/**
	 * Creates a diagonal matrix with the specified diagonal values.
	 * 
	 * @param values	initial diagonal values
	 * @return			a new diagonal matrix
	 * @see DiagonalMatrix
	 */
	public static DiagonalMatrix diagonal(Number... values) {
		return new DiagonalMatrix(values);
	}

	/**
	 * Creates a column vector with the specified values.
	 * 
	 * @param values	initial values
	 * @return			a new column vector
	 * @see ColumnVector
	 */
	public static ColumnVector columnVector(Number... values) {
		return new ColumnVector(values);
	}

	/**
	 * Creates a row vector with the specified values.
	 * 
	 * @param values	initial values
	 * @return			a new row vector
	 * @see RowVector
	 */
	public static RowVector rowVector(Number... values) {
		return new RowVector(values);
	}

	/**
	 * Returns the sum of two matrices.
	 * 
	 * @param <M>	the type of matrix
	 * @param a		matrix A
	 * @param b		matrix B
	 * @return		A + B
	 */
	@SuppressWarnings("unchecked")
	public static <M extends Matrix> M sum(M a, M b) {
		Matrix m = a.copy();
		return (M) m.add(b);
	}

	/**
	 * Returns the sum of the given matrices.
	 * 
	 * @param <M>	the type of matrix
	 * @param x		the matrices to add
	 * @return		the sum of the matrices (Σx)
	 */
	@SuppressWarnings("unchecked")
	public static <M extends Matrix> M sum(M... x) {
		Matrix m = x[0].copy();
		for (int i = 1; i < x.length; i++) {
			m.add(x[i]);
		}
		return (M) m;
	}
	/**
	 * Returns the difference between two matrices.
	 * 
	 * @param <M>	the type of matrix
	 * @param a		matrix A
	 * @param b		matrix B
	 * @return		A - B
	 */
	@SuppressWarnings("unchecked")
	public static <M extends Matrix> M difference(M a, M b) {
		Matrix m = a.copy();
		return (M) m.subtract(b);
	}

	/**
	 * Returns the tensor product of two matrices.
	 * 
	 * @param a		matrix A
	 * @param b		matrix B
	 * @return		A ⊗ B
	 */
	public static Matrix tensor(Matrix a, Matrix b) {
		Matrix m = Matrix.create(a.rows * b.rows, a.cols * b.cols);
		int row = 0;
		for (int i1 = 0; i1 < a.rows; i1++) {
			for (int i2 = 0; i2 < b.rows; i2++) {
				int col = 0;
				for (int j1 = 0; j1 < a.cols; j1++) {
					for (int j2 = 0; j2 < b.cols; j2++) {
						m.setValue(row, col, Complex.product(a.getValue(i1, j1), b.getValue(i2, j2)));
						col += 1;
					}
				}
				row += 1;
			}
		}
		return m;
	}

	/**
	 * Returns the scalar product of a matrix.
	 * <p>
	 * As this kind of product is commutative,
	 * the arguments can be specified in any order.
	 * 
	 * @param <M>	the type of matrix
	 * @param x		the scalar number
	 * @param a		the matrix (A)
	 * @return		xA = Ax
	 * @see			#product(Matrix, Number)
	 */
	@SuppressWarnings("unchecked")
	public static <M extends Matrix> M product(Number x, M a) {
		return (M) a.copy().multiply(x);
	}

	/**
	 * Returns the scalar product of a matrix.
	 * <p>
	 * As this kind of product is commutative,
	 * the arguments can be specified in any order.
	 * 
	 * @param <M>	the type of matrix
	 * @param a		the matrix (A)
	 * @param x		the scalar number
	 * @return		Ax = xA
	 * @see			#product(Number, Matrix)
	 */
	@SuppressWarnings("unchecked")
	public static <M extends Matrix> M product(M a, Number x) {
		return (M) a.copy().multiply(x);
	}

	/**
	 * Returns the matrix product of two matrices.
	 * 
	 * @param a		matrix A
	 * @param b		matrix B
	 * @return		AB
	 */
	public static Matrix product(Matrix a, Matrix b) {
		a.check(a.cols == b.rows, "Number of columns in matrix A does not match number of rows in matrix B");
		Matrix m = Matrix.create(a.rows, b.cols);
		for (int i = 0; i < a.rows; i++) {
			for (int j = 0; j < b.cols; j++) {
				m.setValue(i, j, dotProduct(a, i, b, j));
			}
		}
		return m;
	}

	/**
	 * Returns the matrix product of the given matrices.
	 * 
	 * @param x		the matrices to multiply
	 * @return		the product of the matrices (Πx)
	 */
	public static Matrix product(Matrix... x) {
		// resolve from right to left for easier debugging
		int i = x.length - 1;
		Matrix m = x[i];
		while (0 < i--) {
			m = product(x[i], m);
		}
		return m;
	}

	/**
	 * Returns the dot product of a row in one matrix
	 * and a column in another matrix
	 * 
	 * @param a		matrix A
	 * @param row	row index of A
	 * @param b		matrix B
	 * @param col	column index of B
	 * @return		the dot product row(A) . column(B)
	 */
	private static Complex dotProduct(Matrix a, int row, Matrix b, int col) {
		Complex z = new Complex();
		for (int k = 0; k < b.rows; k++) {
			z.add(Complex.product(a.getValue(row, k), b.getValue(k, col)));
		}
		return z;
	}

}
