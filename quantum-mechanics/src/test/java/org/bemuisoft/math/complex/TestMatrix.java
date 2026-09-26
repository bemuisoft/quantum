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
import org.bemuisoft.qm.sim.QuantumSystemAnalyzer;

/**
 * Some test matrices used in other tests.
 * 
 * @author Benno Muilwijk
 */
public class TestMatrix extends ComplexBase {

	public static Matrix rho1() {
		return new Matrix(
			row(0.7,			c(0.2, 0.1)),
			row(c(0.2, -0.1),	0.3)
		);
	}

	public static Matrix rhoA() {
		return qsABC().reducedDensityMatrix(0);
	}

	public static Matrix rhoB() {
		return qsABC().reducedDensityMatrix(1);
	}

	public static Matrix rhoC() {
		return qsABC().reducedDensityMatrix(2);
	}

	public static Matrix rhoAB() {
		return qsABC().reducedDensityMatrix(0, 1);
	}

	public static Matrix rhoAC() {
		return qsABC().reducedDensityMatrix(0, 2);
	}

	public static Matrix rhoBC() {
		return qsABC().reducedDensityMatrix(1, 2);
	}

	public static Matrix rhoABC() {
		return qsABC().densityMatrix();
	}

	public static Matrix psiABC() {
		return qsABC().state();
	}

	public static QuantumSystemAnalyzer qsABC() {
		if (qsABC == null || qsABC.stateIdentifier() != qsABCid) {
			QuantumSystemAnalyzer qs = new QuantumSystemAnalyzer(3);
			int qa = qs.qubitIndex('A');
			int qb = qs.qubitIndex('B');
			int qc = qs.qubitIndex('C');
			qs.apply(Gate.ry(PI/4), qa);
			qs.apply(Gate.ry(PI/3), qb);
			qs.apply(Gate.ry(PI/2), qc);
			qs.apply(Gate.p(PI/2), qa, qb);
			qs.apply(Gate.p(PI/3), qa, qc);
			qs.apply(Gate.p(PI/4), qb, qc);
			qs.apply(Gate.rx(PI/3), qa);
			qs.apply(Gate.rx(PI/2), qb);
			qs.apply(Gate.rx(PI/4), qc);
			qsABCid = qs.stateIdentifier();
			qsABC = qs;
		}
		return qsABC;
	}

	public static QuantumSystemAnalyzer qsGHZPlus() {
		return qsGHZ(0.0);
	}

	public static QuantumSystemAnalyzer qsGHZMinus() {
		return qsGHZ(PI);
	}

	public static QuantumSystemAnalyzer qsGHZ(double phase) {
		QuantumSystemAnalyzer qs = new QuantumSystemAnalyzer(3);
		int qa = qs.qubitIndex('A');
		int qb = qs.qubitIndex('B');
		int qc = qs.qubitIndex('C');
		qs.apply(Gate.h(), qa);
		qs.apply(Gate.rz(phase), qa);
		qs.apply(Gate.x(), qa, qb);
		qs.apply(Gate.x(), qa, qc);
		return qs;
	}

	private static QuantumSystemAnalyzer qsABC;
	private static int qsABCid;

}
