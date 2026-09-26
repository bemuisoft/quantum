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

import org.bemuisoft.qm.core.TestBase;

/**
 * Base class for testing eigen decomposition.
 * <p>
 * Not intended for automated testing.
 * Log output should be inspected visually.
 * 
 * @author Benno Muilwijk
 */
public class EigenTestBase extends ComplexBase implements TestBase {

	public void verify(Matrix rho, EigenDecomposition ed) {
		// verify that (ρ - λI)v = 0 for each eigenvalue/vector pair
		final int n = ed.size();
		for (int i = 0; i < n; i++) {
			ComplexNumber lambda = ed.getEigenvalue(i);
			ComplexNumber negLambda = c(-lambda.realPart(), -lambda.imaginaryPart());
			Matrix v = Matrix.columnVector(ed.getEigenvector(i));
			Matrix negLambdaI = Matrix.diagonal(n, negLambda);
			Matrix verify = Matrix.product(Matrix.sum(rho, negLambdaI), v);
			checkState(verify0(verify), "Eigen decomposition is incorrect");
		}
		// verify correct normalization
		Matrix u = ed.getEigenvectors();	// should be unit matrix
		checkState(Matrix.product(u, u.transpose()).isCloseTo(Matrix.identity(n)), "Incorrectly normalized eigenvectors");
		// verify correct reconstruction
		checkState(reconstruct(ed).isCloseTo(rho), "Incorrect density matrix reconstruction from eigen decomposition");
		checkState(reconstruct2(ed).isCloseTo(rho), "Incorrect density matrix reconstruction from eigen decomposition");
	}

	protected boolean verify0(Matrix m) {
		// verify that all elements are 0
		final int rows = m.rows();
		final int cols = m.columns();
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				ComplexNumber z = m.get(i, j);
				if (z.absValue() > 1e-8) {
					return false;
				}
			}
		}
		return true;
	}

	public Matrix reconstruct(EigenDecomposition ed) {
		DiagonalMatrix d = ed.getEigenvalues();
		Matrix u = ed.getEigenvectors();
		return Matrix.product(u, d, u.transpose(true));
	}

	public Matrix reconstruct2(EigenDecomposition ed) {
		final int n = ed.size();
		Matrix rho = Matrix.zero(n);
		for (int i = 0; i < n; i++) {
			Matrix v = Matrix.columnVector(ed.getEigenvector(i));
			Matrix comp = Matrix.product(v, v.transpose(true));
			rho.add(comp.multiply(ed.getEigenvalue(i)));
		}
		return rho;
	}

}
