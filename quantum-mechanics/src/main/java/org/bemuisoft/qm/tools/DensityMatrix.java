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
package org.bemuisoft.qm.tools;

import org.bemuisoft.math.complex.Complex;
import org.bemuisoft.math.complex.DiagonalMatrix;
import org.bemuisoft.math.complex.EigenDecomposition;
import org.bemuisoft.math.complex.Matrix;

/**
 * Provides some methods specific for density matrices.
 * 
 * @author Benno Muilwijk
 */
public class DensityMatrix extends Matrix {

	private DensityMatrix(int n) {
		super(n, n);
	}

	/**
	 * Constructor.
	 * Creates a {@code DensityMatrix} from any {@link Matrix}
	 * if it represents a valid density matrix.
	 * 
	 * @param m	the input matrix to convert to {@code DensityMatrix}
	 * @throws IllegalArgumentException if the input matrix does not represent a valid density matrix
	 * @see #isDensityMatrix(Matrix)
	 */
	public DensityMatrix(Matrix m) {
		this(m.rows());
		this.set(m).setFinal();
		check(isDensityMatrix(this), "Invalid density matrix");
	}

	@Override
	protected void setValue(int row, int column, Complex value) {
		super.setValue(row, column, round(value, 10));
	}

	/**
	 * Answers whether this density matrix represents a pure quantum state.
	 * <p>
	 * A quantum state is pure when the square of the density matrix has a trace of 1.
	 * 
	 * @return	{@code true} if the quantum state is pure,
	 * 			{@code false} otherwise
	 * @see <a href="https://en.wikipedia.org/wiki/Purity_(quantum_mechanics)">
	 * Purity on Wikipedia</a>
	 */
	public boolean isPure() {
		Complex tr = Matrix.product(this, this).trace();
		return (round(tr.realPart()) == 1.0 && round(tr.imaginaryPart()) == 0.0);
	}

	/**
	 * Returns a matrix that is the square root of this matrix.
	 * 
	 * @return	the square root of this matrix
	 */
	public Matrix sqrt() {
		EigenDecomposition ed = EigenDecomposition.get(this);
		DiagonalMatrix d = ed.getEigenvalues();
		Matrix u = ed.getEigenvectors();
		return Matrix.product(u, d.sqrt(), u.transpose(true));
	}

	//////////////////
	// Static methods
	//////////////////

	/**
	 * Answers whether a given matrix is a valid density matrix.
	 * <p>
	 * A valid density matrix is Hermition and has a trace of 1.
	 * 
	 * @param m	the matrix to examine
	 * @return	{@code true} if the matrix is a valid density matrix,
	 * 			{@code false} otherwise
	 * @see <a href="https://en.wikipedia.org/wiki/Density_matrix">
	 * Density matrix on Wikipedia</a>
	 */
	public static boolean isDensityMatrix(Matrix m) {
		if (m.isHermitian()) {
			// trace must be 1
			Complex tr = m.trace();
			return (m.round(tr.realPart(), 14) == 1.0);		// isHermitian() guarantees that the trace is a real number
		}
		// not Hermitian
		return false;
	}

}
