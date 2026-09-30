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

import org.bemuisoft.math.adapters.OjAlgo;

/**
 * Decomposes a matrix into its eigen values and vectors.
 * <p>
 * The trivial case of a 2x2 Hermitian matrix can be handled
 * by this class.
 * Other cases must be handled by a subclass.
 * Static method {@link EigenDecomposition#get(Matrix)}
 * returns an instance of {@link OjAlgo.EigenDecomposition},
 * which can decompose any density matrix.
 * 
 * @author Benno Muilwijk
 */
public class EigenDecomposition extends ComplexBase {

	/**
	 * Returns an eigen decomposition of the specified
	 * density matrix.
	 * <p>
	 * The actual decomposition is delegated to ojAlgo
	 * by subclass {@link OjAlgo.EigenDecomposition}.
	 * 
	 * @param m	the (density) matrix to decompose.
	 * @return	the eigen decomposition
	 */
	public static EigenDecomposition get(Matrix m) {
		// use complex decomposition from ojAlgo
		return OjAlgo.eigenDecomposition(m);
	}

	private ComplexNumber[] eigenvalues;
	private ComplexNumber[][] eigenvectors;

	/**
	 * Constructor.
	 * Should be used by subclass.
	 * <p>
	 * If used directly by any other class,
	 * only 2x2 Hermitian matrices are supported,
	 * i.e. only 1-qubit (reduced) density matrices.
	 * 
	 * @param m	the (density) matrix to decompose.
	 */
	public EigenDecomposition(Matrix m) {
		eigenvalues = new ComplexNumber[m.rows()];
		eigenvectors = new ComplexNumber[m.rows()][];
		decompose(m);
	}

	/**
	 * Returns the size of this eigen decomposition,
	 * that is, the number of eigen value-vector pairs.
	 * 
	 * @return	the size of this eigen decomposition
	 */
	public int size() {
		return eigenvalues.length;
	}

	/**
	 * Returns the eigenvalue at the specified index.
	 * 
	 * @param index	the index
	 * @return		the eigenvalue
	 */
	public ComplexNumber getEigenvalue(int index) {
		return eigenvalues[index];
	}

	/**
	 * Returns all eigenvalues as a {@link DiagonalMatrix}.
	 * 
	 * @return	a {@code DiagonalMatrix} with all eigenvalues
	 */
	public DiagonalMatrix getEigenvalues() {
		return new DiagonalMatrix(eigenvalues);
	}

	/**
	 * Returns the eigenvector at the specified index
	 * as a {@link ComplexNumber} array.
	 * 
	 * @param index	the index
	 * @return		the eigenvector
	 */
	public ComplexNumber[] getEigenvector(int index) {
		return eigenvectors[index];
	}

	/**
	 * Returns a {@link Matrix}
	 * with all eigenvectors as columns.
	 * 
	 * @return	a {@link Matrix} with all eigenvectors
	 */
	public Matrix getEigenvectors() {
		int n = size();
		Matrix vectors = Matrix.create(n, n);
		for (int j = 0; j < n; j++) {
			ComplexNumber[] vector = getEigenvector(j);
			for (int i = 0; i < n; i++) {
				vectors.init(i, j, vector[i]);
			}
		}
		return vectors;
	}

	/**
	 * Sets the eigenvalue at the specified index.
	 * 
	 * @param i		the index
	 * @param value	the eigenvalue to set
	 */
	protected void setEigenvalue(int i, ComplexNumber value) {
		eigenvalues[i] = c(round(value.realPart()), round(value.imaginaryPart()));
	}

	/**
	 * Sets the eigenvector at the specified index.
	 * 
	 * @param i			the index
	 * @param vector	the eigenvector to set
	 */
	protected void setEigenvector(int i, ComplexNumber[] vector) {
		eigenvectors[i] = normalize(vector);
	}

	/**
	 * Decomposes a matrix into eigenvectors and eigenvalues.
	 * 
	 * @param m	the matrix to decompose
	 */
	protected void decompose(Matrix m) {
		// default implementation for Hermitian 2x2 matrix only
		// use subclass for other type of matrix
		int rows = m.rows();
		int cols = m.columns();
		if (rows == 2 && cols == 2 && m.isHermitian()) {
			decomposeHermitian2x2(m);
			return;
		}
		// could try complex decomposition from Open Source Physics or Jampack
		// or Hermitian matrix decomposition from ojAlgo
		throw new UnsupportedOperationException("Decomposition of this matrix is not supported. Use a subclass.");
	}

	private void decomposeHermitian2x2(Matrix m) {
		// A Hermitian 2x2 matrix has the following elements:
		//	a		c-di
		//	c+di	b
		double a = m.get(0, 0).realPart();
		double b = m.get(1, 1).realPart();
		double c = m.get(1, 0).realPart();
		double d = m.get(1, 0).imaginaryPart();
		
		// eigenvalues λ0 and λ1 are: (a+b)/2 ± sqrt( (a-b)²/4 + c² + d² )
		// note in terms of Bloch coordinates q = (a-b)/2 = z/2 and c = x/2 and d = y/2
		double p = (a + b) / 2.0;
		double q = (a - b) / 2.0;
		double det = Math.sqrt(q*q + c*c + d*d);
		ComplexNumber[] lambda = {c(p + det), c(p - det)};
		
		// For each eigenvalue λ, an eigenvector is (c-di, λ-a) or (λ-b, c+di), both normalize to the same vector
		ComplexNumber[][] phi = new ComplexNumber[lambda.length][];
		for (int i = 0; i < lambda.length; i++) {
			phi[i] = normalize(c(lambda[i].realPart() - b), c(c, d));
		}
		
		this.eigenvalues = lambda;
		this.eigenvectors = phi;
	}

	private ComplexNumber[] normalize(ComplexNumber... vector) {
		double norm2 = 0.0;		// norm²
		for (int i = 0; i < vector.length; i++) {
			norm2 += vector[i].abs2();
		}
		ComplexNumber norm = e((norm2 < 1e-16) ? 1.0 : Math.sqrt(norm2), vector[0].phase());
		for (int i = 0; i < vector.length; i++) {
			vector[i] = round(ComplexNumber.quotient(vector[i], norm));
		}
		return vector;
	}

}
