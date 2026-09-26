package org.bemuisoft.math.complex;

/**
 * A diagonal matrix is a square matrix with all non-zero elements on the main diagonal.
 * <p>
 * This class provides a sparse implementation of a diagonal matrix.
 * Because all off-diagonal values are zero by definition,
 * only the diagonal values are stored.
 * 
 * @author Benno Muilwijk
 */
public class DiagonalMatrix extends Matrix {

	private Complex[] values;

	/**
	 * Constructs a diagonal matrix with the specified number of rows and columns.
	 * All diagonal values are initialized to {@code null}.
	 * 
	 * @param n	number of rows and columns
	 */
	public DiagonalMatrix(int n) {
		super(n, n, null);
		values = new Complex[n];
	}

	/**
	 * Constructs a diagonal matrix with the specified diagonal values.
	 * 
	 * @param values	initial diagonal values
	 */
	public DiagonalMatrix(Number... values) {
		this(values.length);
		init(values);
	}

	@Override
	public DiagonalMatrix setFinal() {
		super.setFinal();
		return this;
	}

	@Override
	protected Complex getValue(int row, int column) {
		return (row == column) ? values[row] : ZERO;
	}

	@Override
	protected void setValue(int row, int column, Complex value) {
		if (row == column) {
			values[row] = value;
		} else if (value.realPart() != 0.0 || value.imaginaryPart() != 0.0) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Initializes this matrix with the specified diagonal values.
	 * 
	 * @param values	initial diagonal values
	 * @throws IllegalArgumentException if the number of values differs from number of rows
	 * @throws IllegalStateException if any diagonal element already has a value
	 */
	public void init(Number... values) {
		check(values.length == rows(), "Inconsistent number of elements");
		for (int i = 0; i < rows(); i++) {
			super.init(i, i, values[i]);
		}
	}

	/**
	 * Sets this matrix to the same values as the given diagonal matrix.
	 * 
	 * @param m	the source matrix
	 * @return	this matrix
	 * @throws	IllegalArgumentException if the source matrix has different dimensions
	 * @throws	IllegalStateException if this matrix is set final
	 * @see		#setFinal()
	 */
	public DiagonalMatrix set(DiagonalMatrix m) {
		super.set(m);
		return this;
	}

	/**
	 * Adds the given diagonal matrix to this matrix.
	 * <p>
	 * If this matrix is final, a new diagonal matrix is returned.
	 * Otherwise, this matrix is updated and returned.
	 * 
	 * @param m	the diagonal matrix to add
	 * @return	diagonal matrix with the result of the addition
	 * @throws	IllegalArgumentException if the matrix to add has different dimensions
	 */
	public DiagonalMatrix add(DiagonalMatrix m) {
		check(m.rows() == this.rows(), "Matrix to add has different dimensions");
		return (DiagonalMatrix) super.add(m);
	}

	@Override
	public Matrix add(Matrix m) {
		if (m instanceof DiagonalMatrix) {
			return add((DiagonalMatrix) m);
		}
		return copy(false).add(m);
	}

	/**
	 * Subtracts the given diagonal matrix from this matrix.
	 * <p>
	 * If this matrix is final, a new diagonal matrix is returned.
	 * Otherwise, this matrix is updated and returned.
	 * 
	 * @param m	the diagonal matrix to subtract
	 * @return	diagonal matrix with the result of the subtraction
	 * @throws	IllegalArgumentException if the matrix to subtract has different dimensions
	 */
	public DiagonalMatrix subtract(DiagonalMatrix m) {
		check(m.rows() == this.rows(), "Matrix to add has different dimensions");
		return (DiagonalMatrix) super.subtract(m);
	}

	@Override
	public Matrix subtract(Matrix m) {
		if (m instanceof DiagonalMatrix) {
			return subtract((DiagonalMatrix) m);
		}
		return copy(false).subtract(m);
	}

	@Override
	public DiagonalMatrix multiply(Number x) {
		return (DiagonalMatrix) super.multiply(x);
	}

	@Override
	public DiagonalMatrix copy() {
		DiagonalMatrix m = DiagonalMatrix.create(rows());
		return m.set(this);
	}

	/**
	 * Returns a copy of this matrix.
	 * The copy is not final, so it can be modified.
	 * 
	 * @param trueCopy	{@code true} to return a sparse diagonal matrix,
	 * 					{@code false} to return a full matrix
	 * @return	new copy of this matrix
	 */
	public Matrix copy(boolean trueCopy) {
		if (trueCopy) {
			return this.copy();
		}
		return super.copy();
	}

	@Override
	public DiagonalMatrix conjugate() {
		DiagonalMatrix m = DiagonalMatrix.create(rows());
		for (int i = 0; i < rows(); i++) {
			m.setValue(i, i, getValue(i, i).conjugate());
		}
		return m;
	}

	@Override
	public DiagonalMatrix transpose() {
		return transpose(true);
	}

	@Override
	public DiagonalMatrix transpose(boolean conjugate) {
		DiagonalMatrix m = DiagonalMatrix.create(rows());
		for (int i = 0; i < rows(); i++) {
			Complex z = getValue(i, i);
			m.setValue(i, i, conjugate ? z.conjugate() : cc(z));
		}
		return m;
	}

	/**
	 * Returns the square root of this matrix.
	 * <p>
	 * This square root of a diagonal matrix is simply
	 * another diagonal matrix with each value as the square
	 * root of the corresponding cell value in this matrix.
	 * 
	 * @return	the square root of this matrix
	 * @see #isHermitian()
	 */
	public DiagonalMatrix sqrt() {
		DiagonalMatrix m = DiagonalMatrix.create(rows());
		for (int i = 0; i < rows(); i++) {
			Complex z = getValue(i, i);
			m.setValue(i, i, e(Math.sqrt(z.absValue()), z.phase() * 0.5));
		}
		return m;
	}

	//////////////////
	// Static methods
	//////////////////

	/**
	 * Creates a diagonal matrix with the specified number of rows and columns.
	 * All diagonal values are initialized to {@code null}.
	 * 
	 * @param n	number of rows and columns
	 * @return	a new diagonal matrix
	 */
	public static DiagonalMatrix create(int n) {
		return new DiagonalMatrix(n);
	}

}
