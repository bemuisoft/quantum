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

import org.bemuisoft.math.complex.ColumnVector;
import org.bemuisoft.math.complex.Complex;
import org.bemuisoft.math.complex.ComplexNumber;
import org.bemuisoft.math.complex.EigenDecomposition;
import org.bemuisoft.math.complex.Matrix;
import org.bemuisoft.qm.core.QuantumSystem;
import org.bemuisoft.qm.core.PureQuantumSystem;

/**
 * Reconstruction class for 3-qubit state vector
 * from two of its 2-qubit reduced density matrices.
 * <p>
 * The underlying mathematics is described by L. Diósi in the paper
 * <a href="https://arxiv.org/pdf/quant-ph/0403200">
 * Three-party pure quantum states are determined by two two-party reduced states
 * </a>.
 * 
 * @author Benno Muilwijk
 */
public class ThreeQubitReconstruction extends Reconstruction {

	private static final Integer FIRST = 0;
	private static final Integer SECOND = 1;
	private static final Integer THIRD = 2;

	private PureQuantumSystem qsABC = new PureQuantumSystem(3);
	private QuantumSystem qsAB;
	private QuantumSystem qsAC;
	private QuantumSystem qsBC;
	private Matrix rhoA;
	private Matrix rhoB;
	private Matrix rhoC;

	/**
	 * Sets the reduced density matrix of qubits A and B.
	 * 
	 * @param rhoAB the rhoAB to set
	 */
	public void setRhoAB(Matrix rhoAB) {
		if (rhoAB == null) {
			qsAB = null;
			return;
		}
		if (qsAB == null) {
			qsAB = new QuantumSystem(qsABC, FIRST, SECOND);
		}
		qsAB.setDensityMatrix(rhoAB);
	}

	/**
	 * Sets the reduced density matrix of qubits A and C.
	 * 
	 * @param rhoAC the rhoAC to set
	 */
	public void setRhoAC(Matrix rhoAC) {
		if (rhoAC == null) {
			qsAC = null;
			return;
		}
		if (qsAC == null) {
			qsAC = new QuantumSystem(qsABC, FIRST, THIRD);
		}
		qsAC.setDensityMatrix(rhoAC);
	}

	/**
	 * Sets the reduced density matrix of qubits B and C.
	 * 
	 * @param rhoBC the rhoBC to set
	 */
	public void setRhoBC(Matrix rhoBC) {
		if (rhoBC == null) {
			qsBC = null;
			return;
		}
		if (qsBC == null) {
			qsBC = new QuantumSystem(qsABC, SECOND, THIRD);
		}
		qsBC.setDensityMatrix(rhoBC);
	}

	/**
	 * Reconstructs the 3-qubit state from two 2-qubit reduced density matrices.
	 * 
	 * @return the quantum state vector
	 */
	public ColumnVector reconstruct() {
		extractSingleDensities();
		EigenDecomposition edA = EigenDecomposition.get(rhoA);
		EigenDecomposition edB = EigenDecomposition.get(rhoB);
		EigenDecomposition edC = EigenDecomposition.get(rhoC);
		
		ColumnVector psi;
		if (getRhoBC() == null) {
			// reconstruct from rhoAB and rhoAC
			EigenDecomposition edAB = EigenDecomposition.get(getRhoAB());
			EigenDecomposition edAC = EigenDecomposition.get(getRhoAC());
			psi = reconstructBC(edA, edB, edC, edAB, edAC);
		} else if (getRhoAB() == null) {
			// reconstruct from rhoAC and rhoBC
			EigenDecomposition edAC = EigenDecomposition.get(getRhoAC());
			EigenDecomposition edBC = EigenDecomposition.get(getRhoBC());
			psi = reconstructAB(edA, edB, edC, edAC, edBC);
		} else {
			// reconstruct from rhoAB and rhoBC
			EigenDecomposition edAB = EigenDecomposition.get(getRhoAB());
			EigenDecomposition edBC = EigenDecomposition.get(getRhoBC());
			psi = reconstructAC(edA, edB, edC, edAB, edBC);
		}
		qsABC.setState(psi);
		return psi;
	}

	/**
	 * Reconstructs the 3-qubit state from eigen decompositions of rhoAC and rhoBC.
	 * 
	 * @return the quantum state vector
	 */
	private ColumnVector reconstructAB(
			EigenDecomposition edA,
			EigenDecomposition edB,
			EigenDecomposition edC,
			EigenDecomposition edAC,
			EigenDecomposition edBC)
	{
		final int j = 0;	// either 0 or 1
		Matrix bj = getAmplitudeMatrix(edA, edC, edAC.getEigenvector(j));
		
		ColumnVector psi = Matrix.columnVector(0, 0, 0, 0, 0, 0, 0, 0);
		int n = Math.min(edA.size(), edBC.size());
		for (int i = 0; i < n; i++) {
			double lambda = edA.getEigenvalue(i).realPart();
			int i2 = matchEigenvalue(edBC, lambda);
			Matrix vA = Matrix.columnVector(edA.getEigenvector(i));
			Matrix vBC = Matrix.columnVector(edBC.getEigenvector(i2));
			Matrix t = Matrix.tensor(vA, vBC);
			Matrix ai = getAmplitudeMatrix(edB, edC, edBC.getEigenvector(i2));
			Complex phase = new Complex();
			for (int k = 0; k < 2; k++) {
				phase.add(Complex.product(ai.get(j, k).conjugate(), bj.get(i, k)));
			}
			phase.normalize();
			psi.add(t.multiply(cv(Math.sqrt(lambda)).multiply(phase)));
		}
		return psi.multiply(ei(-psi.get(0, 0).phase()));
	}

	/**
	 * Reconstructs the 3-qubit state from eigen decompositions of rhoAB and rhoBC.
	 * 
	 * @return the quantum state vector
	 */
	private ColumnVector reconstructAC(
			EigenDecomposition edA,
			EigenDecomposition edB,
			EigenDecomposition edC,
			EigenDecomposition edAB,
			EigenDecomposition edBC)
	{
		final int k = 0;	// either 0 or 1
		Matrix ck = getAmplitudeMatrix(edA, edB, edAB.getEigenvector(k));
		
		ColumnVector psi = Matrix.columnVector(0, 0, 0, 0, 0, 0, 0, 0);
		int n = Math.min(edA.size(), edBC.size());
		for (int i = 0; i < n; i++) {
			double lambda = edA.getEigenvalue(i).realPart();
			int i2 = matchEigenvalue(edBC, lambda);
			Matrix vA = Matrix.columnVector(edA.getEigenvector(i));
			Matrix vBC = Matrix.columnVector(edBC.getEigenvector(i2));
			Matrix t = Matrix.tensor(vA, vBC);
			Matrix ai = getAmplitudeMatrix(edB, edC, edBC.getEigenvector(i2));
			Complex phase = new Complex();
			for (int j = 0; j < 2; j++) {
				phase.add(Complex.product(ai.get(j, k).conjugate(), ck.get(i, j)));
			}
			phase.normalize();
			psi.add(t.multiply(cv(Math.sqrt(lambda)).multiply(phase)));
		}
		return psi.multiply(ei(-psi.get(0, 0).phase()));
	}

	/**
	 * Reconstructs the 3-qubit state from eigen decompositions of rhoAB and rhoAC.
	 * 
	 * @return the quantum state vector
	 */
	private ColumnVector reconstructBC(
			EigenDecomposition edA,
			EigenDecomposition edB,
			EigenDecomposition edC,
			EigenDecomposition edAB,
			EigenDecomposition edAC)
	{
		final int j = 0;	// either 0 or 1
		Matrix bj = getAmplitudeMatrix(edA, edC, edAC.getEigenvector(j));
		
		ColumnVector psi = Matrix.columnVector(0, 0, 0, 0, 0, 0, 0, 0);
		int n = Math.min(edAB.size(), edC.size());
		for (int k = 0; k < n; k++) {
			double lambda = edC.getEigenvalue(k).realPart();
			int k2 = matchEigenvalue(edAB, lambda);
			Matrix vAB = Matrix.columnVector(edAB.getEigenvector(k));
			Matrix vC = Matrix.columnVector(edC.getEigenvector(k2));
			Matrix t = Matrix.tensor(vAB, vC);
			Matrix ck = getAmplitudeMatrix(edA, edB, edAB.getEigenvector(k2));
			Complex phase = new Complex();
			for (int i = 0; i < 2; i++) {
				phase.add(Complex.product(bj.get(i, k), ck.get(i, j).conjugate()));
			}
			phase.normalize();
			psi.add(t.multiply(cv(Math.sqrt(lambda)).multiply(phase)));
		}
		return psi.multiply(ei(-psi.get(0, 0).phase()));
	}

	/**
	 * Extracts 1-qubit reduced density matrices for all three qubits
	 * from the available 2-qubit reduced density matrices.
	 */
	private void extractSingleDensities() {
		rhoA = null;
		rhoB = null;
		rhoC = null;
		if (qsAB != null) {
			rhoA = qsAB.reducedDensityMatrix(FIRST);
			rhoB = qsAB.reducedDensityMatrix(SECOND);
		}
		if (qsAC != null) {
			if (qsAB != null) {
				check(qsAC.reducedDensityMatrix(FIRST).isCloseTo(rhoA), "rhoAB and rhoAC are not compatible");
			} else {
				rhoA = qsAC.reducedDensityMatrix(FIRST);
			}
			rhoC = qsAC.reducedDensityMatrix(SECOND);
		}
		if (qsBC != null) {
			if (qsAB != null) {
				check(qsBC.reducedDensityMatrix(FIRST).isCloseTo(rhoB), "rhoAB and rhoBC are not compatible");
			} else {
				rhoB = qsBC.reducedDensityMatrix(FIRST);
			}
			if (qsAC != null) {
				check(qsBC.reducedDensityMatrix(SECOND).isCloseTo(rhoC), "rhoAC and rhoBC are not compatible");
			} else {
				rhoC = qsBC.reducedDensityMatrix(SECOND);
			}
		}
		check(rhoA != null && rhoB != null && rhoC != null, "At least two 2-qubit density matrices are needed");
	}

	private int matchEigenvalue(EigenDecomposition ed, double target) {
		for (int i = 0; i < ed.size(); i++) {
			if (Math.abs(ed.getEigenvalue(i).realPart() - target) < 1e-8) {
				return i;
			}
		}
		throw new IllegalStateException("Eigenvalue match failed");
	}

	private Matrix getAmplitudeMatrix(EigenDecomposition ed1, EigenDecomposition ed2, ComplexNumber[] v12) {
//		check(ed1.size() == 2, "ed1 must be for single qubit density matrix (2x2)");
//		check(ed2.size() == 2, "ed2 must be for single qubit density matrix (2x2)");
//		check(v12.length == 4, "v12 must be eigenvector for 2-qubit matrix (4x1)");
		Matrix v = Matrix.columnVector(v12);
		Matrix m = Matrix.create(2, 2);
		// create amplitude matrix
		for (int i = 0; i < 2; i++) {
			Matrix vi = Matrix.columnVector(ed1.getEigenvector(i));
			for (int j = 0; j < 2; j++) {
				Matrix vj = Matrix.columnVector(ed2.getEigenvector(j));
				Matrix vij = Matrix.tensor(vi, vj);
				Matrix mij = Matrix.product(vij.transpose(true), v);
				m.init(i, j, mij.get(0, 0));
			}
		}
//		// check amplitude matrix
//		Matrix c = Matrix.columnVector(0, 0, 0, 0);
//		for (int i = 0; i < 2; i++) {
//			Matrix vi = Matrix.columnVector(ed1.getEigenvector(i));
//			for (int j = 0; j < 2; j++) {
//				Matrix vj = Matrix.columnVector(ed2.getEigenvector(j));
//				Matrix vij = Matrix.tensor(vi, vj);
//				c.add(vij.multiply(m.get(i, j)));
//			}
//		}
//		check(c.isCloseTo(v), "error in calculating amplitude matrix");
		return m;
	}

	/**
	 * Returns the reduced density matrix for qubit A after reconstruction.
	 * 
	 * @return the rhoA
	 */
	public Matrix getRhoA() {
		return rhoA;
	}

	/**
	 * Returns the reduced density matrix for qubit B after reconstruction.
	 * 
	 * @return the rhoB
	 */
	public Matrix getRhoB() {
		return rhoB;
	}

	/**
	 * Returns the reduced density matrix for qubit C after reconstruction.
	 * 
	 * @return the rhoC
	 */
	public Matrix getRhoC() {
		return rhoC;
	}

	/**
	 * Returns the reduced density matrix AB as previously set.
	 * 
	 * @return the rhoAB
	 */
	public Matrix getRhoAB() {
		return (qsAB == null) ? null : qsAB.densityMatrix();
	}

	/**
	 * Returns the reduced density matrix AC as previously set.
	 * 
	 * @return the rhoAC
	 */
	public Matrix getRhoAC() {
		return (qsAC == null) ? null : qsAC.densityMatrix();
	}

	/**
	 * Returns the reduced density matrix BC as previously set.
	 * 
	 * @return the rhoBC
	 */
	public Matrix getRhoBC() {
		return (qsBC == null) ? null : qsBC.densityMatrix();
	}

	/**
	 * Returns the reconstructed 3-qubit density matrix.
	 * 
	 * @return the density matrix for ABC
	 */
	public Matrix getRhoABC() {
		return qsABC.densityMatrix();
	}

	/**
	 * Returns the reconstructed 3-qubit state vector.
	 * 
	 * @return the quantum state vector
	 */
	public Matrix getState() {
		return qsABC.state();
	}

}
