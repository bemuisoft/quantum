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

import org.bemuisoft.qm.core.Gate;
import org.bemuisoft.qm.core.QuantumSystem;
import org.bemuisoft.qm.sim.QuantumSystemAnalyzer;	

/**
 * Testing reconstruction of 3-qubit state vector
 * from two of its 2-qubit reduced density matrices.
 * <p>
 * Not intended for automated testing.
 * Log output should be inspected visually.
 * 
 * @author Benno Muilwijk
 */
public class ThreeQubitReconstructionTest extends EigenTestBase {

	public static void main(String[] args) {
		try {
			ThreeQubitReconstructionTest test = new ThreeQubitReconstructionTest();
			test.runAll();
//			test.runRandomNotEntangled();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void runAll() {
//		run0();
//		log("");
//		run1();
//		log("");
		runRandomNotEntangled();
		log("");
		runRandomEntangled();
//		log("");
//		runGHZ();
	}

	void run0() {
		log("run0");
		QuantumSystemAnalyzer qs = new QuantumSystemAnalyzer(3);
		qs.logState();
		int qa = qs.qubitIndex('A');
		int qb = qs.qubitIndex('B');
		int qc = qs.qubitIndex('C');
		QuantumSystem rhoAB = qs.subsystem(qa, qb);
		QuantumSystem rhoAC = qs.subsystem(qa, qc);
		QuantumSystem rhoBC = qs.subsystem(qb, qc);
		ColumnVector result = reconstruct(rhoAB, rhoAC, rhoBC);
		QuantumSystemAnalyzer qs2 = new QuantumSystemAnalyzer(3, result);
		log("");
		qs2.logState();
	}

	void run1() {
		log("run1");
		QuantumSystemAnalyzer qs = TestMatrix.qsABC();
		qs.logState();
		int qa = qs.qubitIndex('A');
		int qb = qs.qubitIndex('B');
		int qc = qs.qubitIndex('C');
		QuantumSystem rhoAB = qs.subsystem(qa, qb);
		QuantumSystem rhoAC = qs.subsystem(qa, qc);
		QuantumSystem rhoBC = qs.subsystem(qb, qc);
		ColumnVector result = reconstruct(rhoAB, rhoAC, rhoBC);
		QuantumSystemAnalyzer qs2 = new QuantumSystemAnalyzer(3, result);
		log("");
		qs.logState(0);
		log("");
		qs2.logState(0);
	}

	void runGHZ() {
		log("runGHZ");
		QuantumSystemAnalyzer qs = TestMatrix.qsGHZMinus();
		qs.logState();
		int qa = qs.qubitIndex('A');
		int qb = qs.qubitIndex('B');
		int qc = qs.qubitIndex('C');
		QuantumSystem rhoAB = qs.subsystem(qa, qb);
		QuantumSystem rhoAC = qs.subsystem(qa, qc);
		QuantumSystem rhoBC = qs.subsystem(qb, qc);
		ColumnVector result = reconstruct(rhoAB, rhoAC, rhoBC);
		QuantumSystemAnalyzer qs2 = new QuantumSystemAnalyzer(3, result);
		log("");
		qs2.logState(0);
	}

	void runRandomNotEntangled() {
		log("runRandomNotEntangled");
		QuantumSystemAnalyzer qs = new QuantumSystemAnalyzer(3);
		int qa = qs.qubitIndex('A');
		int qb = qs.qubitIndex('B');
		int qc = qs.qubitIndex('C');
		qs.apply(Gate.ry(randomTheta()), qa);
		qs.apply(Gate.ry(randomTheta()), qb);
		qs.apply(Gate.ry(randomTheta()), qc);
		qs.apply(Gate.rz(random2Pi()), qa);
		qs.apply(Gate.rz(random2Pi()), qb);
		qs.apply(Gate.rz(random2Pi()), qc);
		qs.logState(0);
		QuantumSystem rhoAB = qs.subsystem(qa, qb);
		QuantumSystem rhoAC = qs.subsystem(qa, qc);
		QuantumSystem rhoBC = qs.subsystem(qb, qc);
		ColumnVector result = reconstruct(rhoAB, rhoAC, rhoBC);
		QuantumSystemAnalyzer qs2 = new QuantumSystemAnalyzer(3, result);
		log("");
		qs2.logState(0);
	}

	void runRandomEntangled() {
		log("runRandomEntangled");
		ColumnVector psi = randomStateVector();
		QuantumSystemAnalyzer qs = new QuantumSystemAnalyzer(3, psi);
		qs.logState();
		int qa = qs.qubitIndex('A');
		int qb = qs.qubitIndex('B');
		int qc = qs.qubitIndex('C');
		QuantumSystem rhoAB = qs.subsystem(qa, qb);
		QuantumSystem rhoAC = qs.subsystem(qa, qc);
		QuantumSystem rhoBC = qs.subsystem(qb, qc);
		ColumnVector result = reconstruct(rhoAB, rhoAC, rhoBC);
		QuantumSystemAnalyzer qs2 = new QuantumSystemAnalyzer(3, result);
		log("");
		qs2.logState(0);
	}

	ColumnVector randomStateVector() {
		ComplexNumber[] random = new ComplexNumber[DIM3];
		double sum = 0.0;
		for (int i = 0; i < DIM3; i++) {
			double prob  = Math.random();
			double phase = (i == 0) ? 0.0 : random2Pi();
			random[i] = e(prob, phase);
			sum += prob;
		}
		ColumnVector psi = ColumnVector.create(DIM3);
		for (int i = 0; i < DIM3; i++) {
			psi.set(i, 0, e(Math.sqrt(random[i].absValue() / sum), random[i].phase()));
		}
		return psi;
	}

	ColumnVector reconstruct(QuantumSystem qsAB, QuantumSystem qsAC, QuantumSystem qsBC) {
		Matrix rhoA = qsAB.reducedDensityMatrix(0);
		Matrix rhoB = qsAB.reducedDensityMatrix(1);
		Matrix rhoC = qsAC.reducedDensityMatrix(1);
		if (!qsAC.reducedDensityMatrix(0).isCloseTo(rhoA)) {
			throw new IllegalStateException("Subsystems AB and AC are not compatible.");
		}
		if (!qsBC.reducedDensityMatrix(0).isCloseTo(rhoB)) {
			throw new IllegalStateException("Subsystems AB and BC are not compatible.");
		}
		if (!qsBC.reducedDensityMatrix(1).isCloseTo(rhoC)) {
			throw new IllegalStateException("Subsystems AC and BC are not compatible.");
		}
		Matrix rhoAB = qsAB.densityMatrix();
		Matrix rhoAC = qsAC.densityMatrix();
		Matrix rhoBC = qsBC.densityMatrix();
		EigenDecomposition eigA = EigenDecomposition.get(rhoA);
		EigenDecomposition eigB = EigenDecomposition.get(rhoB);
		EigenDecomposition eigC = EigenDecomposition.get(rhoC);
		EigenDecomposition eigAB = EigenDecomposition.get(rhoAB);
		EigenDecomposition eigAC = EigenDecomposition.get(rhoAC);
		EigenDecomposition eigBC = EigenDecomposition.get(rhoBC);
		verify(rhoA, eigA);
		verify(rhoB, eigB);
		verify(rhoC, eigC);
		verify(rhoAB, eigAB);
		verify(rhoAC, eigAC);
		verify(rhoBC, eigBC);
		// show amplitude matrices
//		printMatrix(getAmplitudeMatrix(eigA, eigB, eigAB.getEigenvector(0)), "Amplitude matrix C0 (from AB)");
//		printMatrix(getAmplitudeMatrix(eigA, eigB, eigAB.getEigenvector(1)), "Amplitude matrix C1 (from AB)");
//		printMatrix(getAmplitudeMatrix(eigA, eigC, eigAC.getEigenvector(0)), "Amplitude matrix B0 (from AC)");
//		printMatrix(getAmplitudeMatrix(eigA, eigC, eigAC.getEigenvector(1)), "Amplitude matrix B1 (from AC)");
//		printMatrix(getAmplitudeMatrix(eigB, eigC, eigBC.getEigenvector(0)), "Amplitude matrix A0 (from BC)");
//		printMatrix(getAmplitudeMatrix(eigB, eigC, eigBC.getEigenvector(1)), "Amplitude matrix A1 (from BC)");
		
		ColumnVector psiAB = reconstructAB(eigA, eigB, eigC, eigAC, eigBC);
		ColumnVector psiAC = reconstructAC(eigA, eigB, eigC, eigAB, eigBC);
		ColumnVector psiBC = reconstructBC(eigA, eigB, eigC, eigAB, eigAC);
		log(String.valueOf(psiAB.isCloseTo(psiAC)));
		log(String.valueOf(psiBC.isCloseTo(psiAC)));
//		return psiAB;
//		return psiAC;
		return psiBC;
	}

	ColumnVector reconstructAB(
			EigenDecomposition eigA,
			EigenDecomposition eigB,
			EigenDecomposition eigC,
			EigenDecomposition eigAC,
			EigenDecomposition eigBC)
	{
		final int j = 1;	// either 0 or 1
		Matrix bj = getAmplitudeMatrix(eigA, eigC, eigAC.getEigenvector(j));
		
		ColumnVector psi = Matrix.columnVector(0, 0, 0, 0, 0, 0, 0, 0);
		int n = Math.min(eigA.size(), eigBC.size());
		for (int i1 = 0; i1 < n; i1++) {
			double lambda = eigA.getEigenvalue(i1).realPart();
			int i2 = matchEigenvalue(eigBC, lambda);
			Matrix vA = Matrix.columnVector(eigA.getEigenvector(i1));
			Matrix vBC = Matrix.columnVector(eigBC.getEigenvector(i2));
			Matrix t = Matrix.tensor(vA, vBC);
			Matrix ai = getAmplitudeMatrix(eigB, eigC, eigBC.getEigenvector(i2));
			Complex phase = getPhaseAB(ai.conjugate(), i1, bj, j);
			psi.add(t.multiply(cv(Math.sqrt(lambda)).multiply(phase)));
		}
//		return psi;
		return psi.multiply(ei(-psi.get(0, 0).phase()));
	}

	ColumnVector reconstructAC(
			EigenDecomposition eigA,
			EigenDecomposition eigB,
			EigenDecomposition eigC,
			EigenDecomposition eigAB,
			EigenDecomposition eigBC)
	{
		final int k = 0;	// either 0 or 1
		Matrix ck = getAmplitudeMatrix(eigA, eigB, eigAB.getEigenvector(k));
		
		ColumnVector psi = Matrix.columnVector(0, 0, 0, 0, 0, 0, 0, 0);
		int n = Math.min(eigA.size(), eigBC.size());
		for (int i1 = 0; i1 < n; i1++) {
			double lambda = eigA.getEigenvalue(i1).realPart();
			int i2 = matchEigenvalue(eigBC, lambda);
			Matrix vA = Matrix.columnVector(eigA.getEigenvector(i1));
			Matrix vBC = Matrix.columnVector(eigBC.getEigenvector(i2));
			Matrix t = Matrix.tensor(vA, vBC);
			Matrix ai = getAmplitudeMatrix(eigB, eigC, eigBC.getEigenvector(i2));
			Complex phase = getPhaseAC(ai.conjugate(), i1, ck, k);
			psi.add(t.multiply(cv(Math.sqrt(lambda)).multiply(phase)));
		}
//		return psi;
		return psi.multiply(ei(-psi.get(0, 0).phase()));
	}

	ColumnVector reconstructBC(
			EigenDecomposition eigA,
			EigenDecomposition eigB,
			EigenDecomposition eigC,
			EigenDecomposition eigAB,
			EigenDecomposition eigAC)
	{
		final int j = 1;	// either 0 or 1
		Matrix bj = getAmplitudeMatrix(eigA, eigC, eigAC.getEigenvector(j));
		
		ColumnVector psi = Matrix.columnVector(0, 0, 0, 0, 0, 0, 0, 0);
		int n = Math.min(eigAB.size(), eigC.size());
		for (int k1 = 0; k1 < n; k1++) {
			double lambda = eigC.getEigenvalue(k1).realPart();
			int k2 = matchEigenvalue(eigAB, lambda);
			Matrix vAB = Matrix.columnVector(eigAB.getEigenvector(k1));
			Matrix vC = Matrix.columnVector(eigC.getEigenvector(k2));
			Matrix t = Matrix.tensor(vAB, vC);
			Matrix ck = getAmplitudeMatrix(eigA, eigB, eigAB.getEigenvector(k2));
			Complex phase = getPhaseBC(bj, j, ck.conjugate(), k1);
			psi.add(t.multiply(cv(Math.sqrt(lambda)).multiply(phase)));
		}
//		return psi;
		return psi.multiply(ei(-psi.get(0, 0).phase()));
	}

	int matchEigenvalue(EigenDecomposition eig, double target) {
		for (int i = 0; i < eig.size(); i++) {
			if (Math.abs(eig.getEigenvalue(i).realPart() - target) < 1e-8) {
				return i;
			}
		}
		throw new IllegalStateException("Eigenvalue match failed");
	}

	Matrix getAmplitudeMatrix(EigenDecomposition eig1, EigenDecomposition eig2, ComplexNumber[] v12) {
		check(eig1.size() == 2, "eig1 must be for single qubit density matrix (2x2)");
		check(eig2.size() == 2, "eig2 must be for single qubit density matrix (2x2)");
		check(v12.length == 4, "v12 must be eigenvector for 2-qubit matrix (4x1)");
		Matrix v = Matrix.columnVector(v12);
		Matrix m = Matrix.create(2, 2);
		// create amplitude matrix
		for (int i = 0; i < 2; i++) {
			Matrix vi = Matrix.columnVector(eig1.getEigenvector(i));
			for (int j = 0; j < 2; j++) {
				Matrix vj = Matrix.columnVector(eig2.getEigenvector(j));
				Matrix vij = Matrix.tensor(vi, vj);
				Matrix mij = Matrix.product(vij.transpose(true), v);
				m.init(i, j, mij.get(0, 0));
			}
		}
		// check amplitude matrix
		Matrix c = Matrix.columnVector(0, 0, 0, 0);
		for (int i = 0; i < 2; i++) {
			Matrix vi = Matrix.columnVector(eig1.getEigenvector(i));
			for (int j = 0; j < 2; j++) {
				Matrix vj = Matrix.columnVector(eig2.getEigenvector(j));
				Matrix vij = Matrix.tensor(vi, vj);
				c.add(vij.multiply(m.get(i, j)));
			}
		}
		checkState(c.isCloseTo(v), "error in calculating amplitude matrix");
		return m;
	}

	Complex getPhaseAB(Matrix ai, int i, Matrix bj, int j) {
		check(ai.rows() == 2 && ai.columns() == 2, "Amplitude matrix Ai must be 2x2");
		check(bj.rows() == 2 && bj.columns() == 2, "Amplitude matrix Bj must be 2x2");
		Complex phase = new Complex();
		for (int k = 0; k < 2; k++) {
			phase.add(ComplexNumber.product(ai.get(j, k), bj.get(i, k)));
		}
		phase.normalize();
		checkState(Math.abs(phase.absValue() - 1.0) < 1e-8, "Phase norm is not 1");
		return phase;
	}

	Complex getPhaseAC(Matrix ai, int i, Matrix ck, int k) {
		check(ai.rows() == 2 && ai.columns() == 2, "Amplitude matrix Ai must be 2x2");
		check(ck.rows() == 2 && ck.columns() == 2, "Amplitude matrix Ck must be 2x2");
		Complex phase = new Complex();
		for (int j = 0; j < 2; j++) {
			phase.add(ComplexNumber.product(ai.get(j, k), ck.get(i, j)));
		}
		phase.normalize();
		checkState(Math.abs(phase.absValue() - 1.0) < 1e-8, "Phase norm is not 1");
		return phase;
	}

	Complex getPhaseBC(Matrix bj, int j, Matrix ck, int k) {
		check(bj.rows() == 2 && bj.columns() == 2, "Amplitude matrix Bj must be 2x2");
		check(ck.rows() == 2 && ck.columns() == 2, "Amplitude matrix Ck must be 2x2");
		Complex phase = new Complex();
		for (int i = 0; i < 2; i++) {
			phase.add(ComplexNumber.product(bj.get(i, k), ck.get(i, j)));
		}
		phase.normalize();
		checkState(Math.abs(phase.absValue() - 1.0) < 1e-8, "Phase norm is not 1");
		return phase;
	}

	void printMatrix(Matrix m, String description) {
		log(description);
		for (int i = 0; i < m.rows(); i++) {
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < m.columns(); j++) {
				sb.append(round(m.get(i, j), 14).asString()).append('\t');
			}
			log(sb.toString());
		}
	}

	final static int DIM3 = 8;		// 2^3

}
