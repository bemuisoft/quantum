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
package org.bemuisoft.qm.sim;

import java.util.List;

import org.bemuisoft.qm.core.SimpleBlochVector;

/**
 * Base class for testing three-qubit systems.
 * <p>
 * Not intended for automated testing.
 * Log output should be inspected visually.
 * 
 * @author Benno Muilwijk
 */
public class QubitTest3Base extends QubitTestBase {

	public static void main(String[] args) {
		try {
			QubitTest3Base test = new QubitTest3Base();
//			test.run();
//			test.runGHZ();
			test.runNearGHZ();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	QubitQM qa, qb, qc;

	public QubitTest3Base() {
		super(3, true);
		this.qa = qubit('A');
		this.qb = qubit('B');
		this.qc = qubit('C');
	}

	public void run() {
		qa.ry( PI/3);
		qb.ry( HALF_PI);
		qc.ry( 2*PI/3);
//		inspect("Initial state");
		qb.cp( PI/3, qa);
		inspect("After CP on AB");
		qb.rx( HALF_PI);
		inspect("After RX on B");
		qc.cp( HALF_PI, qb);
		inspect("After CP on BC");
		printComponents(qb);
	}

	public void runGHZ() {
		qa.ry(PI/3);
		qb.ry(HALF_PI);
		qc.ry(HALF_PI);
//		inspect("Initial state");
		qb.cp(PI, qa);
		inspect("After CP on AB");
		qb.ry(-HALF_PI);
		inspect("After RY on B");
		qc.cp(PI, qb);
		inspect("After CP on BC");
		qc.ry(-HALF_PI);
		inspect("After RY on C");
		qa.p(PI/3);
		inspect("After P on A");
		qb.p(PI/6);
		inspect("After P on B");
		qc.p(PI/2);
		inspect("After P on C");
		printComponents(qa);
		printComponents(qb);
		printComponents(qc);
	}

	public void runNearGHZ() {
		final double THETA = 0.47*PI;
		qa.ry(PI/3);
		qb.ry(THETA);
		qc.ry(THETA);
//		inspect("Initial state");
		qb.cp(PI, qa);
		inspect("After CP on AB");
		qb.ry(-THETA);
		inspect("After RY on B");
		qc.cp(PI, qb);
		inspect("After CP on BC");
		qc.ry(-THETA);
		inspect("After RY on C");
		qa.p(PI/3);
		inspect("After P on A");
		qb.p(PI/6);
		inspect("After P on B");
		qc.p(PI/2);
		inspect("After P on C");
		printComponents(qa);
		printComponents(qb);
		printComponents(qc);
	}

	public void inspect(String description) {
		log(description);
		logState();
//		log(qa.toString());
//		log(qb.toString());
//		log(qc.toString());
		inspect(qa, qb);
		inspect(qb, qc);
		log("");
	}

//	public void inspectDiagonals(String description) {
//		// ONLY USE at very end, because this method changes the system state!
//		log(description);
//		log(qa);
//		printDensityMatrix(qa.rz(-qa.getPhase()).ry(-qa.getTheta()), "");
//		log(qb);
//		printDensityMatrix(qb.rz(-qb.getPhase()).ry(-qb.getTheta()), "");
//		log(qc);
//		printDensityMatrix(qc.rz(-qc.getPhase()).ry(-qc.getTheta()), "");
//		printDiagonal(reducedDensityMatrix(qa.getIndex(), qb.getIndex()), "Density AB");
//		printDiagonal(reducedDensityMatrix(qa.getIndex(), qc.getIndex()), "Density AC");
//		printDiagonal(reducedDensityMatrix(qb.getIndex(), qc.getIndex()), "Density BC");
//		log("");
//	}
//
//	public void inspectLengths(String description) {
//		log(description);
//		double test = qb.length() * qc.length();
//		log("A.r = " + qa.length() + ", B.r * C.r = " + test + ", delta=" + (qa.length() - test) + ", ratio=" + (qa.length() / test));
//		log(qa);
//		log(qb);
//		log(qc);
//		inspect(qa, qb);
//		inspect(qa, qc);
//		inspect(qb, qc);
//		log("");
//	}

	public void inspect(QubitQM q1, QubitQM q2) {
		// get list of 4 subcomponents: Ab0, Ab1, Ba0 and Ba1
		// q1 maps to A and q2 maps to B
		List<SimpleBlochVector> comps = blochVectorComponents(q1, q2);
		log(q1.toString());
		log(comps.get(0));
		log(comps.get(1));
		//
		log(q2.toString());
		log(comps.get(2));
		log(comps.get(3));
	}

	public void printComponents(QubitQM q) {
		log(q.toString());
//		List<SimpleBlochVector> comps = qs.blochVectorComponents(q.getIndex());
		List<SimpleBlochVector> comps = q.getComps();
		for (SimpleBlochVector comp : comps) {
			log(comp);
		}
	}

//	public void printDensityMatrix(Qubit q, String description) {
//		printMatrix(reducedDensityMatrix(q.getIndex()), description);
//	}
//
//	public void printMatrix(Matrix m, String description) {
//		log(description);
//		for (int i = 0; i < m.rows(); i++) {
//			StringBuilder sb = new StringBuilder();
//			for (int j = 0; j < m.columns(); j++) {
//				sb.append(round(m.get(i, j)).asString()).append('\t');
//			}
//			log(sb.toString());
//		}
//		log("");
//	}
//
//	public void printDiagonal(Matrix m, String description) {
//		log(description);
//		check(m.rows() == m.columns(), "Matrix must be square, but it isn't");
//		for (int i = 0; i < m.rows(); i++) {
//			log(round(m.get(i, i)).asString());
//		}
//		log("");
//	}

}
