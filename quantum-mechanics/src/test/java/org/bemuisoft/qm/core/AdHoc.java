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

import java.util.List;

import org.bemuisoft.math.complex.ColumnVector;
import org.bemuisoft.math.complex.ComplexBase;
import org.bemuisoft.math.complex.ComplexNumber;
import org.bemuisoft.math.complex.EigenDecomposition;
import org.bemuisoft.math.complex.Matrix;
import org.bemuisoft.qm.sim.QuantumSystemAnalyzer;

/**
 * Ad hoc tests for QM core.
 * <p>
 * Not intended for automated testing.
 * Log output should be inspected visually.
 * 
 * @author Benno Muilwijk
 */
public class AdHoc extends ComplexBase implements TestBase {

	public static void main(String[] args) {
		try {
			AdHoc test = new AdHoc();
			test.run();
//			test.runEigenBloch();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void run() {
		BlochVectorAnalyzer t0 = new BlochVectorAnalyzer("t0", 0.105830052, -0.105830052, -0.1);
		BlochVectorAnalyzer t1 = new BlochVectorAnalyzer("t1", -0.138564065, 0.138564065, -0.1);
		BlochVectorAnalyzer sum = BlochVectorAnalyzer.sum("sum", t0, t1);
		log("Initial values");
		log(t0);
		log(t1);
		log(sum);
		t0.rz(-sum.getPhase());
		t1.rz(-sum.getPhase());
		log("After phase shift to 0");
		log(t0);
		log(t1);
		t0.ry(-sum.getTheta());
		t1.ry(-sum.getTheta());
		log("After theta shift to |0⟩");
		log(t0);
		log(t1);
		log("Δϕ = " + toPi(t1.getPhase() - t0.getPhase()));
	}

	void runEigenBloch() {
		PureQuantumSystem qs = new PureQuantumSystem(3, false);
		int qa = qs.qubitIndex('A');
		int qb = qs.qubitIndex('B');
//		qs.apply(Gate.x(), qa);
//		qs.apply(Gate.h(), qa);
//		qs.apply(Gate.s(), qa);
//		qs.apply(Gate.x(), qa, qb);
//		qs.apply(Gate.h(), qa);
		qs.apply(Gate.ry(PI/3), qa);
		qs.apply(Gate.p(PI/4), qa);
		qs.apply(Gate.ry(PI/2), qb);
		qs.apply(Gate.p(PI/2), qa, qb);
		BlochVectorAnalyzer ba = new BlochVectorAnalyzer(qs.blochVector(qa));
		Matrix rho = qs.reducedDensityMatrix(qa);
		EigenDecomposition eig = EigenDecomposition.get(rho);
		log(ba);
		log(blochVector(eig));
		printMatrix(ba.densityMatrix(), "Rho A from Bloch vector");
		printMatrix(densityMatrix(eig), "Rho A from eigen decomposition");
		printMatrix(rho, "Rho A");
		for (int i = 0; i < eig.size(); i++) {
			log("");
			log("Eigenvalue A" + i + " from Bloch vector = " + ba.eigenvalue(i));
			log("Eigenvalue A" + i + " = " + asString(eig.getEigenvalue(i)));
			ComplexNumber[] v = ba.eigenvector(i);
			log("");
			log("Eigenvector A" + i + " from Bloch vector");
			log(v[0].toString());
			log(v[1].toString());
			log("Eigenvector A" + i);
			log(eig.getEigenvector(i)[0].toString());
			log(eig.getEigenvector(i)[1].toString());
		}
	}

	void run5() {
		QuantumSystemAnalyzer qs = new QuantumSystemAnalyzer(2);
//		int qa = qs.qubitIndex('A');
		int qb = qs.qubitIndex('B');
		printStateZ(qs);
		qs.apply(Gate.rx(PI/0.75), qb);
		qs.logState();
		qs.logState(0);
		printStateZ(qs);
	}

	void run4() {
		PureQuantumSystem qs = new PureQuantumSystem(3, true);
		int qa = qs.qubitIndex('A');			// qa = 2 when rightToLeft == true
		int qb = qs.qubitIndex('B');			// qb = 1 when rightToLeft == true
		int qc = qs.qubitIndex('C');			// qc = 0 when rightToLeft == true
		qs.apply(Gate.ry(Math.random()*PI), qa);
		qs.apply(Gate.ry(Math.random()*PI), qb);
		qs.apply(Gate.ry(Math.random()*PI), qc);
		
		qs.apply(Gate.ry(-qs.blochVector(qa).getTheta()), qa);
		printMatrix(qs.reducedDensityMatrix(qa), "A:");
		qs.apply(Gate.ry(-qs.blochVector(qb).getTheta()), qb);
		printMatrix(qs.reducedDensityMatrix(qb), "B:");
		qs.apply(Gate.ry(-qs.blochVector(qc).getTheta()), qc);
		printMatrix(qs.reducedDensityMatrix(qc), "C:");
	}

	void run3() {
		PureQuantumSystem qs = new PureQuantumSystem(3, true);
		int qa = qs.qubitIndex('A');			// qa = 2 when rightToLeft == true
		int qb = qs.qubitIndex('B');			// qb = 1 when rightToLeft == true
		int qc = qs.qubitIndex('C');			// qc = 0 when rightToLeft == true
		qs.apply(Gate.ry(2*PI/6), qa);
		qs.apply(Gate.ry(2*PI/6), qb);
		qs.apply(Gate.ry(3*PI/6), qc);
//		qs.apply(Gate.ry(Math.random()*PI), qa);
//		qs.apply(Gate.ry(Math.random()*PI), qb);
//		qs.apply(Gate.ry(Math.random()*PI), qc);
		printBlochVectors(qs, qa, qb, qc, "After RY on all qubits");
		qs.apply(Gate.rz(3*PI/6), qb, qa);
		printBlochVectors(qs, qa, qb, qc, "After CRZ on qubits B and A");
		qs.apply(Gate.rz(4*PI/6), qb, qc);
//		qs.apply(Gate.p(Math.random()*PI), qb, qc);
		printBlochVectors(qs, qa, qb, qc, "After CRZ on qubits B and C");
		qs.apply(Gate.h(), qb);
		printBlochVectors(qs, qa, qb, qc, "After H on qubit B");
	}

	void run2() {
		PureQuantumSystem qs = new PureQuantumSystem(2, true);
		int c = qs.qubitIndex('A');
		int q = qs.qubitIndex('B');
//		qs.apply(Gate.I, q);
//		qs.apply(Gate.H, q);
//		qs.apply(Gate.X, q);
//		qs.apply(Gate.Y, q);
//		qs.apply(Gate.Z, q);
		qs.apply(Gate.H, c);
//		qs.apply(Gate.ry(HALF_PI), c);
		qs.apply(Gate.X, c, q);
//		printState(qs);
//		printMatrix(qs.operation(Gate.X, c, q), "");
//		printBlochVector(qs.blochVector(c), "A");
//		printBlochVector(qs.blochVector(q), "B");
		printBlochVectorComponents(qs.blochVectorComponents(c), "A");
		printBlochVectorComponents(qs.blochVectorComponents(q), "B");
	}

	void printStateZ(PureQuantumSystem qs) {
		ColumnVector state = qs.state();
		double p0 = state.get(0).abs2();
		for (int i = 0; i < state.rows(); i++) {
			double z = p0 - state.get(i).abs2();
			log(i + "\t" + z);
		}
	}

	void printState(PureQuantumSystem qs) {
		Matrix state = qs.state();
		check(state.columns() == 1, "state is not a column vector");
		for (int i = 0; i < state.rows(); i++) {
			log(state.get(i, 0).asString());
		}
	}

	void printMatrix(Matrix m, String description) {
		log(description);
		for (int i = 0; i < m.rows(); i++) {
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < m.columns(); j++) {
				sb.append(m.get(i, j).asString()).append('\t');
			}
			log(sb.toString());
		}
	}

	void printBlochVector(SimpleBlochVector v, String description) {
		log(description + ": " + v);
	}

	void printBlochVectorComponents(List<SimpleBlochVector> comps, String description) {
		log(description + ":");
		for (SimpleBlochVector v : comps) {
			log(v.toString());
		}
	}

	void printBlochVectors(PureQuantumSystem qs, int qa, int qb, int qc, String description) {
		log("");
		log(description + ":");
//		printState(qs);
		//
		List<SimpleBlochVector> compsA = qs.blochVectorComponents(qa);
		List<SimpleBlochVector> compsB = qs.blochVectorComponents(qb);
		List<SimpleBlochVector> compsC = qs.blochVectorComponents(qc);
		SimpleBlochVector blochVectorA = SimpleBlochVector.sum(compsA, 0, 1, 2, 3);
		SimpleBlochVector blochVectorB = SimpleBlochVector.sum(compsB, 0, 1, 2, 3);
		SimpleBlochVector blochVectorC = SimpleBlochVector.sum(compsC, 0, 1, 2, 3);
		SimpleBlochVector sub0, sub1;
//		printBlochVectorComponents(compsA, "A");
		printBlochVector(blochVectorA, "A  ");
		printBlochVector(sub0 = SimpleBlochVector.sum(compsA, 0, 1), "Ac0");		// c is leftmost qubit in state vector index (qc == 0)
		printBlochVector(sub1 = SimpleBlochVector.sum(compsA, 2, 3), "Ac1");
		inspect(blochVectorA, sub0, sub1, blochVectorC, blochVectorB, "Ac");
		printBlochVector(sub0 = SimpleBlochVector.sum(compsA, 0, 2), "Ab0");
		printBlochVector(sub1 = SimpleBlochVector.sum(compsA, 1, 3), "Ab1");
		inspect(blochVectorA, sub0, sub1, blochVectorB, blochVectorC, "Ab");
//		printBlochVectorComponents(compsB, "B");
		printBlochVector(blochVectorB, "B  ");
		printBlochVector(sub0 = SimpleBlochVector.sum(compsB, 0, 1), "Bc0");
		printBlochVector(sub1 = SimpleBlochVector.sum(compsB, 2, 3), "Bc1");
		inspect(blochVectorB, sub0, sub1, blochVectorC, blochVectorA, "Bc");
		printBlochVector(sub0 = SimpleBlochVector.sum(compsB, 0, 2), "Ba0");
		printBlochVector(sub1 = SimpleBlochVector.sum(compsB, 1, 3), "Ba1");
		inspect(blochVectorB, sub0, sub1, blochVectorA, blochVectorC, "Ba");
//		printBlochVectorComponents(compsC, "C");
		printBlochVector(blochVectorC, "C  ");
		printBlochVector(sub0 = SimpleBlochVector.sum(compsC, 0, 1), "Cb0");
		printBlochVector(sub1 = SimpleBlochVector.sum(compsC, 2, 3), "Cb1");
		inspect(blochVectorC, sub0, sub1, blochVectorB, blochVectorA, "Cb");
		printBlochVector(sub0 = SimpleBlochVector.sum(compsC, 0, 2), "Ca0");
		printBlochVector(sub1 = SimpleBlochVector.sum(compsC, 1, 3), "Ca1");
		inspect(blochVectorC, sub0, sub1, blochVectorA, blochVectorB, "Ca");
	}

	void inspect(SimpleBlochVector trgt, SimpleBlochVector sub0, SimpleBlochVector sub1, SimpleBlochVector ctrl, SimpleBlochVector other, String label) {
//		double r = trgt.length() * other.length() / ctrl.length();
//		double r = other.length();
//		r = 1.0;
		double p0 = (1 + ctrl.getZ()) / 2;
		double p1 = (1 - ctrl.getZ()) / 2;
		double s = sub0.length() + sub1.length();
		double t;
//		if ((t = sub0.length()) != p0) log("r0=" + t + ", p0=" + p0 + ", r0/p0=" + t/p0);
//		if ((t = sub1.length()) != p1) log("r1=" + t + ", p1=" + p1 + ", r1/p1=" + t/p1);
		if ((t = sub0.length()) != p0*s) log("r0/s=" + t + '/' + s + '=' + (t/s) + ", p0=" + p0);
		if ((t = sub1.length()) != p1*s) log("r1/s=" + t + '/' + s + '=' + (t/s) + ", p1=" + p1);
	}

	Matrix densityMatrix(EigenDecomposition eigen) {
		// reconstruct density matrix from eigen decomposition
		final int n = eigen.size();
		Matrix rho = Matrix.zero(n);
		for (int i = 0; i < n; i++) {
			Matrix v = Matrix.columnVector(eigen.getEigenvector(i));
			Matrix comp = Matrix.product(v, v.transpose(true));
			rho.add(comp.multiply(eigen.getEigenvalue(i)));
		}
		return rho;
	}

	BlochVectorAnalyzer blochVector(EigenDecomposition eigen) {
		// reconstruct Bloch vector from eigen decomposition
		final int n = eigen.size();
		check(n == 2, "Bloch vector from eigen decomosition only works for single qubit decompositions");
		BlochVectorAnalyzer[] bloch = new BlochVectorAnalyzer[2];
		for (int i = 0; i < 2; i++) {
			bloch[i] = new BlochVectorAnalyzer("From eigen decomposition");
			bloch[i].set(eigen.getEigenvalue(i).realPart(), eigen.getEigenvector(i));
		}
		check(bloch[1].isCloseTo(bloch[0]), "Inconsistent result");
		return bloch[0];
	}

	void testRound() {
		double x = 0.12345;
		log("x=" + x);
		log("6=" + round(x, 6));
		log("5=" + round(x, 5));
		log("4=" + round(x, 4));
		log("3=" + round(x, 3));
		log("2=" + round(x, 2));
		log("1=" + round(x, 1));
	}

}
