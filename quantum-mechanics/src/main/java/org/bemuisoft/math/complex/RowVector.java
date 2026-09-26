package org.bemuisoft.math.complex;

/**
 * A row vector is a matrix with just one row.
 * 
 * @author Benno Muilwijk
 */
public class RowVector extends Matrix {

	private Complex[] values;

	/**
	 * Constructs a row vector with the specified number of columns.
	 * All values are initialized to {@code null}.
	 * 
	 * @param columns	number of columns
	 */
	public RowVector(int columns) {
		super(1, columns, null);
		values = new Complex[columns];
	}

	/**
	 * Constructs a row vector with the specified values.
	 * 
	 * @param values	initial values
	 */
	public RowVector(Number... values) {
		this(values.length);
		init(values);
	}

	@Override
	public RowVector setFinal() {
		super.setFinal();
		return this;
	}

	/**
	 * Returns the value in the specified column.
	 * 
	 * @param column	the column index
	 * @return			the value
	 */
	public ComplexNumber get(int column) {
		return super.get(0, column);
	}

	/**
	 * Returns the internal value in the specified column.
	 * 
	 * @param column	the column index
	 * @return			the value
	 */
	private Complex getValue(int column) {
		return values[column];
	}

	/**
	 * Sets the internal value in the specified column.
	 * 
	 * @param column	the column index
	 * @param value		the value to set
	 */
	void setValue(int column, Complex value) {
		values[column] = value;
	}

	@Override
	public ComplexNumber get(int row, int column) {
		check(row == 0, "Invalid row index");
		return super.get(row, column);
	}

	@Override
	protected Complex getValue(int row, int column) {
		// no need to check in protected method
		return getValue(column);
	}

	/**
	 * Sets the value in the specified column.
	 * 
	 * @param column	the column index
	 * @param value		the value to set
	 * @throws			IllegalStateException if this vector is set final
	 * @see				#setFinal()
	 * @see				#init(int, Number)
	 */
	public void set(int column, Number value) {
		if (isFinal()) {
			throw new IllegalStateException("Vector is final");
		}
		setValue(column, c(value));
	}

	@Override
	public void set(int row, int column, Number value) {
		check(row == 0, "Invalid row index");
		setValue(column, c(value));
	}

	@Override
	protected void setValue(int row, int column, Complex value) {
		// no need to check in protected method
		setValue(column, value);
	}

	/**
	 * Initializes the value in the specified column.
	 * 
	 * @param column	the column index
	 * @param value		the initial value
	 * @throws			IllegalStateException if the column already has a value
	 * @see				#set(int, Number)
	 */
	public void init(int column, Number value) {
		if (getValue(column) != null) {
			throw new IllegalStateException("Cell is initialized already");
		}
		setValue(column, c(value));
	}

	@Override
	public void init(int row, int column, Number value) {
		check(row == 0, "Invalid row index");
		init(column, value);
	}

	/**
	 * Initializes this vector with the specified values.
	 * 
	 * @param values	initial values
	 * @throws IllegalArgumentException if the number of values differs from number of columns
	 * @throws IllegalStateException if any column already has a value
	 */
	public void init(Number... values) {
		check(values.length == columns(), "Inconsistent number of columns");
		for (int i = 0; i < columns(); i++) {
			init(i, values[i]);
		}
	}

	/**
	 * Sets this vector to the same values as the given vector.
	 * 
	 * @param v	the source vector
	 * @return	this vector
	 * @throws	IllegalArgumentException if the source vector has a different number of columns
	 * @throws	IllegalStateException if this vector is set final
	 * @see		#setFinal()
	 */
	public RowVector set(RowVector v) {
		super.set(v);
		return this;
	}

	/**
	 * Adds the given vector to this vector.
	 * <p>
	 * If this vector is final, a new vector is returned.
	 * Otherwise, this vector is updated and returned.
	 * 
	 * @param v	the vector to add
	 * @return	vector with the result of the addition
	 * @throws	IllegalArgumentException if the vector to add has a different number of columns
	 */
	public RowVector add(RowVector v) {
		check(v.columns() == this.columns(), "Vector to add has a different dimension");
		return (RowVector) super.add(v);
	}

	/**
	 * Subtracts the given vector from this vector.
	 * <p>
	 * If this vector is final, a new vector is returned.
	 * Otherwise, this vector is updated and returned.
	 * 
	 * @param v	the vector to subtract
	 * @return	vector with the result of the subtraction
	 * @throws	IllegalArgumentException if the vector to subtract has a different number of columns
	 */
	public RowVector subtract(RowVector v) {
		check(v.columns() == this.columns(), "Vector to subtract has a different dimension");
		return (RowVector) super.subtract(v);
	}

	@Override
	public RowVector multiply(Number x) {
		return (RowVector) super.multiply(x);
	}

	@Override
	public RowVector copy() {
		RowVector v = RowVector.create(columns());
		return v.set(this);
	}

	@Override
	public RowVector conjugate() {
		RowVector v = RowVector.create(columns());
		for (int i = 0; i < columns(); i++) {
			v.setValue(i, getValue(i).conjugate());
		}
		return v;
	}

	@Override
	public ColumnVector transpose() {
		return transpose(true);
	}

	@Override
	public ColumnVector transpose(boolean conjugate) {
		ColumnVector v = ColumnVector.create(columns());
		for (int i = 0; i < columns(); i++) {
			Complex z = getValue(i);
			v.setValue(i, conjugate ? z.conjugate() : cc(z));
		}
		return v;
	}

	//////////////////
	// Static methods
	//////////////////

	/**
	 * Returns a new row vector with the specified number of columns.
	 * All values are initialized to {@code null}.
	 * 
	 * @param columns	number of columns
	 * @return			a new row vector
	 */
	public static RowVector create(int columns) {
		return new RowVector(columns);
	}

}
