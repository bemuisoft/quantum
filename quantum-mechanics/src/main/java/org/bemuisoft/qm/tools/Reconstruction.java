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
