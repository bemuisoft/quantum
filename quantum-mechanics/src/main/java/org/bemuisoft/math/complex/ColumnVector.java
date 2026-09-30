/*
Copyright 2026 Benno Muilwijk

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.bemuisoft.math.complex;

/**
 * A column vector is a matrix with just one column.
 * 
 * @author Benno Muilwijk
 */
public class ColumnVector extends Matrix {

	private Complex[] values;

	/**
	 * Constructs a column vector with the specified number of rows.
	 * All values are initialized to {@code null}.
	 * 
	 * @param rows	number of rows
	 */
	public ColumnVector(int rows) {
		super(rows, 1, null);
		values = new Complex[rows];
	}

	/**
	 * Constructs a column vector with the specified values.
	 * 
	 * @param values	initial values
	 */
	public ColumnVector(Number... values) {
		this(values.length);
		init(values);
	}

	@Override
	public ColumnVector setFinal() {
		super.setFinal();
		return this;
	}

	/**
	 * Returns the value in the specified row.
	 * 
	 * @param row	the row index
	 * @return		the value
	 */
	public ComplexNumber get(int row) {
		return super.get(row, 0);
	}

	/**
	 * Returns the internal value in the specified row.
	 * 
	 * @param row	the row index
	 * @return		the value
	 */
	private Complex getValue(int row) {
		return values[row];
	}

	/**
	 * Sets the internal value in the specified row.
	 * 
	 * @param row	the row index
	 * @param value	the value to set
	 */
	void setValue(int row, Complex value) {
		values[row] = value;
	}

	@Override
	public ComplexNumber get(int row, int column) {
		check(column == 0, "Invalid column index");
		return super.get(row, column);
	}

	@Override
	protected Complex getValue(int row, int column) {
		// no need to check in protected method
		return getValue(row);
	}

	/**
	 * Sets the value in the specified row.
	 * 
	 * @param row	the row index
	 * @param value	the value to set
	 * @throws		IllegalStateException if this vector is set final
	 * @see			#setFinal()
	 * @see			#init(int, Number)
	 */
	public void set(int row, Number value) {
		if (isFinal()) {
			throw new IllegalStateException("Vector is final");
		}
		setValue(row, c(value));
	}

	@Override
	public void set(int row, int column, Number value) {
		check(column == 0, "Invalid column index");
		setValue(row, c(value));
	}

	@Override
	protected void setValue(int row, int column, Complex value) {
		// no need to check in protected method
		setValue(row, value);
	}

	/**
	 * Initializes the value in the specified row.
	 * 
	 * @param row	the row index
	 * @param value	the initial value
	 * @throws		IllegalStateException if the row already has a value
	 * @see			#set(int, Number)
	 */
	public void init(int row, Number value) {
		if (getValue(row) != null) {
			throw new IllegalStateException("Cell is initialized already");
		}
		setValue(row, c(value));
	}

	@Override
	public void init(int row, int column, Number value) {
		check(column == 0, "Invalid column index");
		init(row, value);
	}

	/**
	 * Initializes this vector with the specified values.
	 * 
	 * @param values	initial values
	 * @throws IllegalArgumentException if the number of values differs from number of rows
	 * @throws IllegalStateException if any row already has a value
	 */
	public void init(Number... values) {
		check(values.length == rows(), "Inconsistent number of rows");
		for (int i = 0; i < rows(); i++) {
			init(i, values[i]);
		}
	}

	/**
	 * Sets this vector to the same values as the given vector.
	 * 
	 * @param v	the source vector
	 * @return	this vector
	 * @throws	IllegalArgumentException if the source vector has a different number of rows
	 * @throws	IllegalStateException if this vector is set final
	 * @see		#setFinal()
	 */
	public ColumnVector set(ColumnVector v) {
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
	 * @throws	IllegalArgumentException if the vector to add has a different number of rows
	 */
	public ColumnVector add(ColumnVector v) {
		check(v.rows() == this.rows(), "Vector to add has a different dimension");
		return (ColumnVector) super.add(v);
	}

	/**
	 * Subtracts the given vector from this vector.
	 * <p>
	 * If this vector is final, a new vector is returned.
	 * Otherwise, this vector is updated and returned.
	 * 
	 * @param v	the vector to subtract
	 * @return	vector with the result of the subtraction
	 * @throws	IllegalArgumentException if the vector to subtract has a different number of rows
	 */
	public ColumnVector subtract(ColumnVector v) {
		check(v.rows() == this.rows(), "Vector to subtract has a different dimension");
		return (ColumnVector) super.subtract(v);
	}

	@Override
	public ColumnVector multiply(Number x) {
		return (ColumnVector) super.multiply(x);
	}

	@Override
	public ColumnVector copy() {
		ColumnVector v = ColumnVector.create(rows());
		return v.set(this);
	}

	@Override
	public ColumnVector conjugate() {
		ColumnVector v = ColumnVector.create(rows());
		for (int i = 0; i < rows(); i++) {
			v.setValue(i, getValue(i).conjugate());
		}
		return v;
	}

	@Override
	public RowVector transpose() {
		return transpose(true);
	}

	@Override
	public RowVector transpose(boolean conjugate) {
		RowVector v = RowVector.create(rows());
		for (int i = 0; i < rows(); i++) {
			Complex z = getValue(i);
			v.setValue(i, conjugate ? z.conjugate() : cc(z));
		}
		return v;
	}

	//////////////////
	// Static methods
	//////////////////

	/**
	 * Returns a new column vector with the specified number of rows.
	 * All values are initialized to {@code null}.
	 * 
	 * @param rows	number of rows
	 * @return		a new column vector
	 */
	public static ColumnVector create(int rows) {
		return new ColumnVector(rows);
	}

}
