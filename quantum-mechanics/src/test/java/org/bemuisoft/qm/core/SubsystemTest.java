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
package org.bemuisoft.qm.core;

import org.bemuisoft.math.complex.Matrix;
import org.bemuisoft.math.complex.TestMatrix;

/**
 * Testing quantum subsystems.
 * <p>
 * Not intended for automated testing.
 * Log output should be inspected visually.
 * 
 * @author Benno Muilwijk
 */
public class SubsystemTest implements TestBase {

	public static void main(String[] args) {
		try {
			SubsystemTest test = new SubsystemTest();
			test.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
//		test1Qubit();
//		test2Qubits();
		test3Qubits();
	}

	void test1Qubit() {
		log("");
		log("test1Qubit");
		PureQuantumSystem qs = new PureQuantumSystem(1);
		int qa = qs.qubitIndex('A');
		qs.apply(Gate.h(), qa);
		log(qs.blochVector(qa));
		
		QuantumSystem sub = new QuantumSystem(qs, qa);
		log(sub.blochVector(qa));
		sub.apply(Gate.s(), qa);
		log(sub.blochVector(qa));
		sub.apply(Gate.rx(HALF_PI), qa);
		log(sub.blochVector(qa));
	}

	void test2Qubits() {
		log("");
		log("test2Qubits");
		PureQuantumSystem qs = new PureQuantumSystem(2);
		int qa = qs.qubitIndex('A');
		int qb = qs.qubitIndex('B');
		qs.apply(Gate.ry(PI/3), qa);
		qs.apply(Gate.h(), qb);
		log(qs.blochVector(qa));
		log(qs.blochVector(qb));
		
		QuantumSystem sub = new QuantumSystem(qs, qa, qb);
		log(sub.blochVector(qa));
		log(sub.blochVector(qb));
		sub.apply(Gate.s(), qa);
		log(sub.blochVector(qa));
		sub.apply(Gate.rx(HALF_PI), qa);
		log(sub.blochVector(qa));
		sub.apply(Gate.s(), qa);
		log(sub.blochVector(qa));
		sub.apply(Gate.h(), qa);
		log(sub.blochVector(qa));
		log(sub.blochVector(qb));
		log("");
		sub.apply(Gate.z(), qa, qb);	// Controlled Z
		inspect(sub, qa, qb);
		log(sub.blochVector(qb));
		sub.apply(Gate.h(), qb);
		log(sub.blochVector(qb));
		inspect(sub, qa, qb);
	}

	@SuppressWarnings("unused")
	void test3Qubits() {
		// visually inspect results with debugger (set breakpoint(s))
		PureQuantumSystem qs = TestMatrix.qsABC();
		Matrix rhoA = qs.reducedDensityMatrix(0);
		Matrix rhoB = qs.reducedDensityMatrix(1);
		Matrix rhoC = qs.reducedDensityMatrix(2);
		QuantumSystem subAB = qs.subsystem(0, 1);
		QuantumSystem subAC = qs.subsystem(0, 2);
		QuantumSystem subBC = qs.subsystem(1, 2);
		Matrix check;
		check = qs.reducedDensityMatrix(0);
		check = qs.reducedDensityMatrix(1);
		check = qs.reducedDensityMatrix(2);
		check = subAB.reducedDensityMatrix(0);
		check = subAB.reducedDensityMatrix(1);
		check = subAC.reducedDensityMatrix(0);
		check = subAC.reducedDensityMatrix(1);
		check = subBC.reducedDensityMatrix(0);
		check = subBC.reducedDensityMatrix(1);
	}

	public void inspect(QuantumSystem sub, int q1, int q2) {
		final int subIndex1 = sub.indexOf(q1);
		final int subIndex2 = sub.indexOf(q2);
		log(sub.blochVector(subIndex1));
		String label = "" + (char)('A' + q1) + (char)('a' + q2);
		Matrix rdm = sub.reducedDensityMatrix(subIndex1, subIndex2);	// rdm is a 4x4 matrix
		if (subIndex1 < subIndex2) {
			log(new SimpleBlochVector(label+'0', rdm, 0, 2));
			log(new SimpleBlochVector(label+'1', rdm, 1, 3));
		} else {
			log(new SimpleBlochVector(label+'0', rdm, 0, 1));
			log(new SimpleBlochVector(label+'1', rdm, 2, 3));
		}
	}

}
