package org.bemuisoft.math.adapters;

import java.util.List;

import org.bemuisoft.math.complex.ComplexBase;
import org.bemuisoft.math.complex.Matrix;
import org.ojalgo.matrix.decomposition.Eigenvalue;
import org.ojalgo.matrix.decomposition.Eigenvalue.Eigenpair;
import org.ojalgo.matrix.store.GenericStore;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.structure.Access1D;

/**
 * Adapter class for <a href="https://ojalgo.org/">ojAlgo</a>.
 * <p>
 * It is only used for functions that require eigen decomposition,
 * such as calculating the square root of a matrix
 * or the concurrence of two qubits,
 * or quantum state reconstruction.
 * 
 * @author Benno Muilwijk
 */
public class OjAlgo extends ComplexBase {

	// -------------------------------------------------------------
	// Methods to convert org.bemuisoft.math.complex types to ojAlgo
    // -------------------------------------------------------------

	/**
	 * Converts a BeMuiSoft complex number to an ojAlgo complex number.
	 * 
	 * @param z	- the BeMuiSoft complex number to convert
	 * @return	the converted ojAlgo complex number
	 */
	public static ComplexNumber complex(org.bemuisoft.math.complex.ComplexNumber z) {
		return ComplexNumber.of(z.realPart(), z.imaginaryPart());
	}

	/**
	 * Converts a BeMuiSoft complex matrix to an ojAlgo complex matrix.
	 * 
	 * @param m	- the BeMuiSoft complex matrix to convert
	 * @return	the converted ojAlgo complex matrix
	 */
	public static GenericStore<ComplexNumber> genericStore(Matrix m) {
		final int rows = m.rows();
		final int cols = m.columns();
		GenericStore<ComplexNumber> ma = GenericStore.C128.make(rows, cols);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				ma.set(i, j, complex(m.get(i, j)));
			}
		}
		return ma;
	}

	// -------------------------------------------------------------
	// Methods to convert ojAlgo types to org.bemuisoft.math.complex
    // -------------------------------------------------------------

	/**
	 * Converts an ojAlgo complex number to a BeMuiSoft complex number.
	 * 
	 * @param z	- the ojAlgo complex number to convert
	 * @return	the converted BeMuiSoft complex number
	 */
	public static org.bemuisoft.math.complex.ComplexNumber c(ComplexNumber z) {
		return c(z.getReal(), z.getImaginary());
	}

	/**
	 * Converts an ojAlgo complex vector to a BeMuiSoft complex number array.
	 * 
	 * @param zArray	- the ojAlgo complex vector to convert
	 * @return	the converted BeMuiSoft complex number array
	 */
	public static org.bemuisoft.math.complex.ComplexNumber[] c(Access1D<ComplexNumber> zArray) {
		org.bemuisoft.math.complex.ComplexNumber[] cArray = new org.bemuisoft.math.complex.ComplexNumber[zArray.size()];
		for (int i = 0; i < zArray.size(); i++) {
			cArray[i] = c(zArray.get(i));
		}
		return cArray;
	}

	/**
	 * Converts an ojAlgo complex matrix to a BeMuiSoft complex matrix.
	 * 
	 * @param ma	- the ojAlgo complex matrix to convert
	 * @return	the converted BeMuiSoft complex matrix
	 */
	public static Matrix matrix(GenericStore<ComplexNumber> ma) {
		final int rows = ma.getRowDim();
		final int cols = ma.getColDim();
		Matrix m = Matrix.create(rows, cols);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				m.set(i, j, c(ma.get(i, j)));
			}
		}
		return m;
	}

	// ----------------------
	// Eigenvalue calculation
    // ----------------------

	/**
	 * Returns an eigen decomposition of a matrix.
	 * The actual decomposition is delegated to ojAlgo.
	 * 
	 * @param m	- the matrix to decompose
	 * @return	the eigen decomposition
	 */
	public static EigenDecomposition eigenDecomposition(Matrix m) {
		return new OjAlgo.EigenDecomposition(m);
	}

	/**
	 * Inner class OjAlgo.EigenDecomposition.
	 * <p>
	 * It overrides the {@code decompose} method of
	 * {@link org.bemuisoft.math.complex.EigenDecomposition}
	 * to delegate the decomposition to ojAlgo.
	 */
	public static class EigenDecomposition extends org.bemuisoft.math.complex.EigenDecomposition {

		/**
		 * Constructor.
		 * 
		 * @param m	- the matrix to decompose
		 */
		public EigenDecomposition(Matrix m) {
			super(m);
		}

		@Override
		protected void decompose(Matrix m) {
			// convert the BeMuiSoft matrix to ojAlgo
			GenericStore<ComplexNumber> rho = OjAlgo.genericStore(m);
			
			// have ojAlgo decompose the matrix
			boolean hermitian = m.isHermitian();
			Eigenvalue<ComplexNumber> evd = Eigenvalue.C128.make(hermitian);
			evd.decompose(rho);
			
			// convert the eigen values and vectors to BeMuiSoft
			List<Eigenpair> eigenpairs = evd.getEigenpairs();
			for (int i = 0; i < eigenpairs.size(); i++) {
				Eigenpair eigen = eigenpairs.get(i);
				setEigenvalue(i, OjAlgo.c(eigen.value));
				setEigenvector(i, OjAlgo.c(eigen.vector));
			}
		}
	}

}
