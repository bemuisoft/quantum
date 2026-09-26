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
import org.bemuisoft.math.complex.ComplexBase;
import org.bemuisoft.math.complex.ComplexNumber;
import org.bemuisoft.math.complex.Matrix;
import org.bemuisoft.math.complex.TestMatrix;
import org.bemuisoft.qm.core.Gate;
import org.bemuisoft.qm.core.TestBase;
import org.bemuisoft.qm.sim.QuantumSystemAnalyzer;

/**
 * Testing the {@link ThreeQubitReconstruction} class.
 * <p>
 * Not intended for automated testing.
 * Log output should be inspected visually.
 * 
 * @author Benno Muilwijk
 */
public class ThreeQubitReconstructionTest extends ComplexBase implements TestBase {

	public static void main(String[] args) {
		try {
			ThreeQubitReconstructionTest test = new ThreeQubitReconstructionTest();
			test.runAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void runAll() {
		run0();
		log("");
		run1();
		log("");
		runRandomNotEntangled();
		log("");
		runRandomEntangled();
		log("");
		runGHZ();
	}

	void run0() {
		log("run0");
		QuantumSystemAnalyzer qs = new QuantumSystemAnalyzer(3);
		testThreeQubitReconstruction(qs);
	}

	void run1() {
		log("run1");
		QuantumSystemAnalyzer qs = TestMatrix.qsABC();
		testThreeQubitReconstruction(qs);
	}

	void runGHZ() {
		log("runGHZ");
		QuantumSystemAnalyzer qs = TestMatrix.qsGHZMinus();
		testThreeQubitReconstruction(qs);
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
		testThreeQubitReconstruction(qs);
	}

	void runRandomEntangled() {
		log("runRandomEntangled");
		ColumnVector psi = randomStateVector();
		QuantumSystemAnalyzer qs = new QuantumSystemAnalyzer(3, psi);
		testThreeQubitReconstruction(qs);
	}

	ColumnVector randomStateVector() {
		final int dim = 8;						// 2^3
		ComplexNumber[] random = new ComplexNumber[dim];
		double sum = 0.0;
		for (int i = 0; i < dim; i++) {
			double prob  = Math.random();
			double phase = (i == 0) ? 0.0 : random2Pi();
			random[i] = e(prob, phase);
			sum += prob;
		}
		ColumnVector psi = ColumnVector.create(dim);
		for (int i = 0; i < dim; i++) {
			psi.set(i, 0, e(Math.sqrt(random[i].absValue() / sum), random[i].phase()));
		}
		return psi;
	}

	void testThreeQubitReconstruction(QuantumSystemAnalyzer qs) {
		qs.logState();
		Matrix rhoAB = qs.reducedDensityMatrix(0, 1);
		Matrix rhoAC = qs.reducedDensityMatrix(0, 2);
		Matrix rhoBC = qs.reducedDensityMatrix(1, 2);
		log("");
		qs.logState(0);
		
		ThreeQubitReconstruction test = new ThreeQubitReconstruction();
		test.setRhoAB(null);
		test.setRhoAC(rhoAC);
		test.setRhoBC(rhoBC);
		ColumnVector psiAB = test.reconstruct();
		test.setRhoAB(rhoAB);
		test.setRhoAC(null);
		test.setRhoBC(rhoBC);
		ColumnVector psiAC = test.reconstruct();
		test.setRhoAB(rhoAB);
		test.setRhoAC(rhoAC);
		test.setRhoBC(null);
		ColumnVector psiBC = test.reconstruct();
		
		log("");
		QuantumSystemAnalyzer qs2 = new QuantumSystemAnalyzer(3, psiAC);
		qs2.logState(0);
		log(String.valueOf(psiAB.isCloseTo(psiAC)));
		log(String.valueOf(psiBC.isCloseTo(psiAC)));
	}

}
