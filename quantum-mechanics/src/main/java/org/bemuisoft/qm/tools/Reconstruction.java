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

import org.bemuisoft.math.complex.ComplexBase;
import org.bemuisoft.math.complex.DiagonalMatrix;
import org.bemuisoft.math.complex.EigenDecomposition;
import org.bemuisoft.math.complex.Matrix;

/**
 * Generic reconstruction class for density matrices.
 * 
 * @author Benno Muilwijk
 */
public class Reconstruction extends ComplexBase {

	/**
	 * Reconstructs a density matrix from its eigen decomposition.
	 * 
	 * @param ed	the eigen decomposition
	 * @return		the reconstructed density matrix
	 */
	public static Matrix densityMatrix(EigenDecomposition ed) {
		DiagonalMatrix d = ed.getEigenvalues();
		Matrix u = ed.getEigenvectors();
		return Matrix.product(u, d, u.transpose(true));
	}

}
