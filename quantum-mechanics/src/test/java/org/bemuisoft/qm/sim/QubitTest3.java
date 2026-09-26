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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.bemuisoft.math.complex.Matrix;
import org.bemuisoft.qm.core.SimpleBlochVector;
import org.bemuisoft.quantum.api.Axis;
import org.bemuisoft.qm.core.BlochVectorAnalyzer;

/**
 * Testing three-qubit systems.
 * <p>
 * Not intended for automated testing.
 * Log output should be inspected visually.
 * 
 * @author Benno Muilwijk
 */
public class QubitTest3 extends QubitTestBase {

	public static void main(String[] args) {
		try {
			QubitTest3 test = new QubitTest3();
//			test.run();
//			test.run0();
//			test.run1();
			test.runCCP();
//			test.runCCRz();
//			test.runCCRx();
//			test.runBell();
//			test.runRandom();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	QubitQM qa, qb, qc;

	public QubitTest3() {
		super(3, true);
		this.qa = qubit('A');
		this.qb = qubit('B');
		this.qc = qubit('C');
	}

	public void runCCP() {
		qa.ry(HALF_PI);
		qb.ry(HALF_PI);
		qc.ry(HALF_PI);
		qa.ccp(HALF_PI, qb, qc);
		inspect("After ccp()");
		qb.cp(-QUARTER_PI, qc);
		inspect("After cp()");
	}

	public void runCCRz() {
		qa.ry(HALF_PI);
		qb.ry(HALF_PI);
		qc.ry(HALF_PI);
		qa.cr(Axis.Z, HALF_PI, qb, qc);
		inspect("After cr(Z)");
	}

	public void runCCRx() {
		qa.ry(HALF_PI);
		qb.ry(HALF_PI);
		qc.ry(HALF_PI);
		qa.cr(Axis.X, TWO_PI, qb, qc);
		inspect("After cr(X)");
	}

	public void run() {
//		inspect("Initial state");
		qa.ry(PI/3);
		qb.ry(PI/3);
		qa.cp(PI, qb);
		inspectLengths("After CP on AB");
//		qb.h();
//		qb.ry(-PI/2);
//		qb.rx(random2pi());
		qc.ry(PI/3);
		qc.cp(PI, qb);
		inspectLengths("After CP on BC");
//		qc.cp(PI, qa);
//		inspectLengths("After CP on AC");
		qc.rz(PI/3);
		analyzeRotation(qc, "RY", rad -> qc.ry(rad));
	}

	public void run0() {
//		inspect("Initial state");
		qa.ry(PI/3);
		qb.ry(PI/3);
		qc.ry(PI/2);
		qc.cp(PI, qa);
		qc.cp(HALF_PI, qb);
		inspect("After CP on C");
//		qa.ry(PI/3);
//		inspect("After RY on A");
//		qb.rz(PI/4);
//		qb.rx(0.28205*PI);
//		inspect("After RX on B");
//		qb.ry(-0.20978*PI);
//		inspect("After RY on B");
		qs.logState();
//		qc.ry(PI/3);
		analyzeComponents(qc);
//		qa.ry(HALF_PI);
		qb.rz(-PI/4);
//		qb.ry(random2pi());
		analyzeComponents(qc);
		inspect("After RY on A");
//		analyzeComponentLengths(qc);
//		analyzeComponentRotations(qc);
//		qc.rx(random2pi());
//		qc.ry(random2pi());
//		analyzeComponentLengths(qc);
//		analyzeComponentRotations(qc);	// should log the same spheroids as before
	}

	public void run1() {
//		inspect("Initial state");
		qa.ry(3*PI/6);
//		qa.ry(random());
//		inspect("After RY on A");
		qb.ry(3*PI/6);
//		qb.ry(random());
//		inspect("After RY on B");
		qc.ry(3*PI/6);
//		qc.ry(random());
//		inspect("After RY on C");
		qb.cp( 4*PI/6, qa);
//		qb.cp(random(), qa);
		inspectLengths("After CP on AB");
		qc.cp( 4*PI/6, qa);
//		qc.cp(random(), qa);
		inspectLengths("After CP on AC");
		qc.cp( 4*PI/6, qb);
//		qc.cp(random(), qb);
		inspectLengths("After CP on BC");
		inspect("After CP on AB, AC and BC");
//		qb.cp(-3*PI/6, qa);
//		inspectLengths("After -CP on AB");
//		qc.cp(-4*PI/6, qa);
//		inspectLengths("After -CP on AC");
	}

	public void run2() {
//		inspect("Initial state");
		qa.ry(2*PI/6);
		qb.ry(3*PI/6);
		qb.cp( 6*PI/6, qa);
		inspectLengths("After CP on AB");
		qb.h();
		inspectLengths("After H on B");
		qc.ry(3*PI/6);
		qc.cp( 6*PI/6, qb);
		inspectLengths("After CP on BC");
//		qa.rz(9*PI/6);
//		inspectLengths("After RZ on A");
//		qb.rz(9*PI/6);
//		inspectLengths("After RZ on B");
//		qb.rx(9*PI/6);
//		inspectLengths("After RX on B");
//		qb.cp( 6*PI/6, qa);
//		inspectLengths("After CP on AB");
	}

	public void run3() {
//		inspect("Initial state");
		qa.ry(PI/2);
		qb.ry(PI/4);
		qa.cp( 6*PI/6, qb);
		inspectLengths("After CP on AB");
		qb.ry(PI/4);
		inspectLengths("After RY on B");
		qc.ry(PI/2);
		qc.cp( 6*PI/6, qb);
		inspectLengths("After CP on BC");
		qb.ry(PI/2);
		inspectLengths("After RY on B");
//		inspect("After CP on AB and BC");
	}

	public void runGHZ() {
//		inspect("Initial state");
		qa.ry(HALF_PI);
		qb.cx(qa);
		inspect(qa, qb);
		qc.cx(qa);
		inspect(qa, qb);
		inspect(qa, qc);
		inspect(qb, qc);
	}

	public void runRandom() {
		log("Start runRandom");
		qa.ry(randomTheta());
		qb.ry(randomTheta());
		qc.ry(randomTheta());
		entangleRandom(qa, qb);
		entangleRandom(qb, qc);
		entangleRandom(qa, qc);
		analyzeComponentLengths(qa);
		analyzeComponentLengths(qb);
		analyzeComponentLengths(qc);
//		analyzeRotation(qc, "RX", rad -> qc.rx(rad)).logExtremeLengths("Ac");
//		analyzeRotation(qc, "RY", rad -> qc.ry(rad)).logExtremeLengths("Bc");
//		analyzeRotation(qc, "RZ", rad -> qc.rz(rad)).logExtremeLengths("Bc");
		analyzeRotation(qa);
		analyzeRotation(qb);
		analyzeRotation(qc);
//		qc.rz(random2pi());
//		qc.r(orthogonal(qc), random2pi());
		// bring p0 of qc to max (and thus p1 to min)
		qc.rz(-qc.getPhase()).ry(-qc.getTheta());
		analyzeRotation(qc);
		// bring p1 of qc to max (and thus p0 to min)
		qc.x();
		analyzeRotation(qc);
		// intermediate conclusions:
		// - while qa and qb are not changed, Ab0, Ab1, Ba0 and Ba1 don't change, so they do NOT depend on the orientation of qc
		// - no matter how qc is rotated, Ca0, Ca1, Cb0 and Cb1 rotate the same as qc itself, as one fixed ball
		// - no matter how qc is rotated, Ac0, Ac1, Bc0 and Bc1 revolve about a fixed center, which is (Ac0 + Ac1)/2 resp. (Bc0 + Bc1)/2
		//	 Ac0 and Ac1 are always on opposite sides of this center and at equal distance; same for Bc0 and Bc1.
		//	 proof: A == Ac0 + Ac1, so A/2 == (Ac0 + Ac1)/2 == Ac0 + (Ac1 - Ac0)/2 == Ac1 + (Ac0 - Ac1)/2
		// - if qc is rotated about the z-axis, Ac0, Ac1, Bc0 and Bc1 do not change (or their rotation has radius 0)
		// - if qc is rotated about any axis in the XOY plane, Ac0, Ac1, Bc0 and Bc1 follow an elliptical path,
		//	 but the plane of such a path depends on the rotation axis of qc
		//	 to be investigated: are all these paths on the intersection of the plane and a single spheroid?
		// - if qc is rotated about any other axis, Ac0, Ac1, Bc0 and Bc1 (do what? the same? yet to be investigated)
		// - Ac0, Ac1, Bc0 and Bc1 can NOT always be found on an spheroid with the same center and long axis through the center of the qubit,
		//	 because |Ac0| + |Ac1| is not always the same, but depends on the orientation of qc (at least); the same for |Bc0| + |Bc1|
		//	 interestingly, for any orientation of qc, |Ac0| + |Ac1| == |Bc0| + |Bc1| (see QM2Bloch.txt for proof)
		// - QM2Bloch.txt contains more interesting facts and conclusions
		// TODO analyze Ac0, Ac1, Bc0 and Bc1 relative to center of rotation
		//		what are the directions of Ac0, Ac1, Bc0 and Bc1 relative to this center, when p0 and p1 of qc have extreme values?
		//		what is the distance of Ac0 and Ac1 from this center in that situation, same for Bc0 and Bc1; are they extreme too? max?
		//		note that distance to center is half of length Ac1 - Ac0, resp. Bc1 - Bc0
		//		same questions as above when p0 == p1 == 0.5; is distance extreme too? min?
		inspect("Final state");
	}

	public void inspect(String description) {
		List<SimpleBlochVector> comps;
		log(description);
		// labels are based on IBM convention (qubit A is represented by the least significant bit)
		comps = qa.getComps();
		log(qa);
		BlochVectorAnalyzer a00 = new BlochVectorAnalyzer("Ab0c0", comps.get(0));
		BlochVectorAnalyzer a10 = new BlochVectorAnalyzer("Ab1c0", comps.get(1));
		BlochVectorAnalyzer a01 = new BlochVectorAnalyzer("Ab0c1", comps.get(2));
		BlochVectorAnalyzer a11 = new BlochVectorAnalyzer("Ab1c1", comps.get(3));
		log(a00, qa, qb.getProbability0() * qc.getProbability0());
		log(a10, qa, qb.getProbability1() * qc.getProbability0());
		log(a01, qa, qb.getProbability0() * qc.getProbability1());
		log(a11, qa, qb.getProbability1() * qc.getProbability1());
//		log(BlochVectorAnalyzer.sum("Ab0", a00, a01), qa);
//		log(BlochVectorAnalyzer.sum("Ab1", a10, a11), qa);
//		log(BlochVectorAnalyzer.sum("Ac0", a00, a10), qa);
//		log(BlochVectorAnalyzer.sum("Ac1", a01, a11), qa);
		comps = qb.getComps();
		log(qb);
		BlochVectorAnalyzer b00 = new BlochVectorAnalyzer("Ba0c0", comps.get(0));
		BlochVectorAnalyzer b10 = new BlochVectorAnalyzer("Ba1c0", comps.get(1));
		BlochVectorAnalyzer b01 = new BlochVectorAnalyzer("Ba0c1", comps.get(2));
		BlochVectorAnalyzer b11 = new BlochVectorAnalyzer("Ba1c1", comps.get(3));
		log(b00, qb, qa.getProbability0() * qc.getProbability0());
		log(b10, qb, qa.getProbability1() * qc.getProbability0());
		log(b01, qb, qa.getProbability0() * qc.getProbability1());
		log(b11, qb, qa.getProbability1() * qc.getProbability1());
//		log(BlochVectorAnalyzer.sum("Ba0", b00, b01), qb);
//		log(BlochVectorAnalyzer.sum("Ba1", b10, b11), qb);
//		log(BlochVectorAnalyzer.sum("Bc0", b00, b10), qb);
//		log(BlochVectorAnalyzer.sum("Bc1", b01, b11), qb);
		comps = qc.getComps();
		log(qc);
		BlochVectorAnalyzer c00 = new BlochVectorAnalyzer("Ca0b0", comps.get(0));
		BlochVectorAnalyzer c10 = new BlochVectorAnalyzer("Ca1b0", comps.get(1));
		BlochVectorAnalyzer c01 = new BlochVectorAnalyzer("Ca0b1", comps.get(2));
		BlochVectorAnalyzer c11 = new BlochVectorAnalyzer("Ca1b1", comps.get(3));
		log(c00, qc, qa.getProbability0() * qb.getProbability0());
		log(c10, qc, qa.getProbability1() * qb.getProbability0());
		log(c01, qc, qa.getProbability0() * qb.getProbability1());
		log(c11, qc, qa.getProbability1() * qb.getProbability1());
//		log(BlochVectorAnalyzer.sum("Ca0", c00, c01), qc);
//		log(BlochVectorAnalyzer.sum("Ca1", c10, c11), qc);
//		log(BlochVectorAnalyzer.sum("Cb0", c00, c10), qc);
//		log(BlochVectorAnalyzer.sum("Cb1", c01, c11), qc);
		log("");
	}

	public void inspectLengths(String description) {
		log(description);
//		// for run1 only
//		double test = qb.length() * qc.length();
//		log("A.r = " + qa.length() + ", B.r * C.r = " + test + ", delta=" + (qa.length() - test) + ", ratio=" + (qa.length() / test));
		//
		log(qa);
		log(qb);
		log(qc);
		inspect(qa, qb);
		inspect(qa, qc);
		inspect(qb, qc);
		log("");
	}

	public void inspect(QubitQM q1, QubitQM q2) {
		// get list of 4 subcomponents: Ab0, Ab1, Ba0 and Ba1
		// q1 maps to A and q2 maps to B
		List<SimpleBlochVector> comps = blochVectorComponents(q1, q2);
		log(q1);
		log(comps.get(0));
		log(comps.get(1));
		//
		log(q2);
		log(comps.get(2));
		log(comps.get(3));
		//
		log("Concurrence = " + qs.getConcurrence(q1, q2));
	}

	public void log(SimpleBlochVector v, QubitQM q, double ref) {
		// ref is the expected probability without entanglement
		log(v.toString() + " Δr=" + (v.length() - ref));
//		log("λ = " + round(q.getLambda(v)/PI, 5) + 'π');
	}

	public void log(SimpleBlochVector v, QubitQM q) {
		log(v.toString());
//		log("λ = " + round(q.getLambda(v)/PI, 5) + 'π');
	}

	public void inspectDiagonals(String description) {
		// ONLY USE at very end, because this method changes the system state!
		log(description);
		log(qa);
		printDensityMatrix(qa.rz(-qa.getPhase()).ry(-qa.getTheta()), "");
		log(qb);
		printDensityMatrix(qb.rz(-qb.getPhase()).ry(-qb.getTheta()), "");
		log(qc);
		printDensityMatrix(qc.rz(-qc.getPhase()).ry(-qc.getTheta()), "");
		printDiagonal(reducedDensityMatrix(qa.getIndex(), qb.getIndex()), "Density AB");
		printDiagonal(reducedDensityMatrix(qa.getIndex(), qc.getIndex()), "Density AC");
		printDiagonal(reducedDensityMatrix(qb.getIndex(), qc.getIndex()), "Density BC");
		log("");
	}

	public void printDensityMatrix(QubitQM q, String description) {
		printMatrix(reducedDensityMatrix(q.getIndex()), description);
	}

	public void printMatrix(Matrix m, String description) {
		log(description);
		for (int i = 0; i < m.rows(); i++) {
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < m.columns(); j++) {
				sb.append(round(m.get(i, j), 14).asString()).append('\t');
			}
			log(sb.toString());
		}
		log("");
	}

	public void printDiagonal(Matrix m, String description) {
		log(description);
		check(m.rows() == m.columns(), "Matrix must be square, but it isn't");
		for (int i = 0; i < m.rows(); i++) {
			log(round(m.get(i, i), 14).asString());
		}
		log("");
	}

	public void analyzeRotationOld(Consumer<Double> rotation) {
		// get center of rotation for each qubit component vector
		// by getting the average of the current vector and after rotating by pi
		Map<String, BlochVectorAnalyzer> map0 = getCentersOfRotation(rotation);
		
		// do the same in perpendicular direction
		rotation.accept(HALF_PI);
		Map<String, BlochVectorAnalyzer> map1 = getCentersOfRotation(rotation);
		
		// restore to original state
		rotation.accept(-HALF_PI);
		log(qa);
		log(qb);
		log(qc);
		
		// compare centers in both directions (should be the same for each qubit component)
		final double margin = 1e-14;
		for (BlochVectorAnalyzer v0 : map0.values()) {
			BlochVectorAnalyzer v1 = map1.get(v0.getLabel());
			double diffX = v1.getX() - v0.getX();
			double diffY = v1.getY() - v0.getY();
			double diffZ = v1.getZ() - v0.getZ();
			if (Math.abs(diffX) > margin || Math.abs(diffY) > margin || Math.abs(diffZ) > margin) {
				if (Math.abs(diffX) > margin) {
					log("Center of " + v0.getLabel() + " is ambiguous in X direction by " + diffX);
				}
				if (Math.abs(diffY) > margin) {
					log("Center of " + v0.getLabel() + " is ambiguous in Y direction by " + diffY);
				}
				if (Math.abs(diffZ) > margin) {
					log("Center of " + v0.getLabel() + " is ambiguous in Z direction by " + diffZ);
				}
			}
			else {
				log("Center of " + v0.getLabel() + " is " + v0);
			}
		}
	}

	private Map<String, BlochVectorAnalyzer> getCentersOfRotation(Consumer<Double> rotation) {
		// labels are based on IBM convention (qubit A is represented by the least significant bit)
		LinkedHashMap<String, BlochVectorAnalyzer> map = new LinkedHashMap<>();
		List<SimpleBlochVector> comps;
		comps = qa.getComps();
		BlochVectorAnalyzer a00 = new BlochVectorAnalyzer("Ab0c0", comps.get(0));	map.put(a00.getLabel(), a00);
		BlochVectorAnalyzer a10 = new BlochVectorAnalyzer("Ab1c0", comps.get(1));	map.put(a10.getLabel(), a10);
		BlochVectorAnalyzer a01 = new BlochVectorAnalyzer("Ab0c1", comps.get(2));	map.put(a01.getLabel(), a01);
		BlochVectorAnalyzer a11 = new BlochVectorAnalyzer("Ab1c1", comps.get(3));	map.put(a11.getLabel(), a11);
		BlochVectorAnalyzer ab0 = BlochVectorAnalyzer.sum("Ab0", a00, a01);			map.put(ab0.getLabel(), ab0);
		BlochVectorAnalyzer ab1 = BlochVectorAnalyzer.sum("Ab1", a10, a11);			map.put(ab1.getLabel(), ab1);
		BlochVectorAnalyzer ac0 = BlochVectorAnalyzer.sum("Ac0", a00, a10);			map.put(ac0.getLabel(), ac0);
		BlochVectorAnalyzer ac1 = BlochVectorAnalyzer.sum("Ac1", a01, a11);			map.put(ac1.getLabel(), ac1);
		log("|Ac0| + |Ac1| = " + (ac0.length() + ac1.length()));
		comps = qb.getComps();
		BlochVectorAnalyzer b00 = new BlochVectorAnalyzer("Ba0c0", comps.get(0));	map.put(b00.getLabel(), b00);
		BlochVectorAnalyzer b10 = new BlochVectorAnalyzer("Ba1c0", comps.get(1));	map.put(b10.getLabel(), b10);
		BlochVectorAnalyzer b01 = new BlochVectorAnalyzer("Ba0c1", comps.get(2));	map.put(b01.getLabel(), b01);
		BlochVectorAnalyzer b11 = new BlochVectorAnalyzer("Ba1c1", comps.get(3));	map.put(b11.getLabel(), b11);
		BlochVectorAnalyzer ba0 = BlochVectorAnalyzer.sum("Ba0", b00, b01);			map.put(ba0.getLabel(), ba0);
		BlochVectorAnalyzer ba1 = BlochVectorAnalyzer.sum("Ba1", b10, b11);			map.put(ba1.getLabel(), ba1);
		BlochVectorAnalyzer bc0 = BlochVectorAnalyzer.sum("Bc0", b00, b10);			map.put(bc0.getLabel(), bc0);
		BlochVectorAnalyzer bc1 = BlochVectorAnalyzer.sum("Bc1", b01, b11);			map.put(bc1.getLabel(), bc1);
		log("|Bc0| + |Bc1| = " + (bc0.length() + bc1.length()));
		comps = qc.getComps();
		BlochVectorAnalyzer c00 = new BlochVectorAnalyzer("Ca0b0", comps.get(0));	map.put(c00.getLabel(), c00);
		BlochVectorAnalyzer c10 = new BlochVectorAnalyzer("Ca1b0", comps.get(1));	map.put(c10.getLabel(), c10);
		BlochVectorAnalyzer c01 = new BlochVectorAnalyzer("Ca0b1", comps.get(2));	map.put(c01.getLabel(), c01);
		BlochVectorAnalyzer c11 = new BlochVectorAnalyzer("Ca1b1", comps.get(3));	map.put(c11.getLabel(), c11);
		BlochVectorAnalyzer ca0 = BlochVectorAnalyzer.sum("Ca0", c00, c01);			map.put(ca0.getLabel(), ca0);
		BlochVectorAnalyzer ca1 = BlochVectorAnalyzer.sum("Ca1", c10, c11);			map.put(ca1.getLabel(), ca1);
		BlochVectorAnalyzer cb0 = BlochVectorAnalyzer.sum("Cb0", c00, c10);			map.put(cb0.getLabel(), cb0);
		BlochVectorAnalyzer cb1 = BlochVectorAnalyzer.sum("Cb1", c01, c11);			map.put(cb1.getLabel(), cb1);
		
		rotation.accept(PI);
		
		comps = qa.getComps();
		a00 = new BlochVectorAnalyzer("Ab0c0", comps.get(0));	map.get(a00.getLabel()).add(a00);
		a10 = new BlochVectorAnalyzer("Ab1c0", comps.get(1));	map.get(a10.getLabel()).add(a10);
		a01 = new BlochVectorAnalyzer("Ab0c1", comps.get(2));	map.get(a01.getLabel()).add(a01);
		a11 = new BlochVectorAnalyzer("Ab1c1", comps.get(3));	map.get(a11.getLabel()).add(a11);
		ab0 = BlochVectorAnalyzer.sum("Ab0", a00, a01);			map.get(ab0.getLabel()).add(ab0);
		ab1 = BlochVectorAnalyzer.sum("Ab1", a10, a11);			map.get(ab1.getLabel()).add(ab1);
		ac0 = BlochVectorAnalyzer.sum("Ac0", a00, a10);			map.get(ac0.getLabel()).add(ac0);
		ac1 = BlochVectorAnalyzer.sum("Ac1", a01, a11);			map.get(ac1.getLabel()).add(ac1);
		log("|Ac0| + |Ac1| = " + (ac0.length() + ac1.length()));
		comps = qb.getComps();
		b00 = new BlochVectorAnalyzer("Ba0c0", comps.get(0));	map.get(b00.getLabel()).add(b00);
		b10 = new BlochVectorAnalyzer("Ba1c0", comps.get(1));	map.get(b10.getLabel()).add(b10);
		b01 = new BlochVectorAnalyzer("Ba0c1", comps.get(2));	map.get(b01.getLabel()).add(b01);
		b11 = new BlochVectorAnalyzer("Ba1c1", comps.get(3));	map.get(b11.getLabel()).add(b11);
		ba0 = BlochVectorAnalyzer.sum("Ba0", b00, b01);			map.get(ba0.getLabel()).add(ba0);
		ba1 = BlochVectorAnalyzer.sum("Ba1", b10, b11);			map.get(ba1.getLabel()).add(ba1);
		bc0 = BlochVectorAnalyzer.sum("Bc0", b00, b10);			map.get(bc0.getLabel()).add(bc0);
		bc1 = BlochVectorAnalyzer.sum("Bc1", b01, b11);			map.get(bc1.getLabel()).add(bc1);
		log("|Bc0| + |Bc1| = " + (bc0.length() + bc1.length()));
		comps = qc.getComps();
		c00 = new BlochVectorAnalyzer("Ca0b0", comps.get(0));	map.get(c00.getLabel()).add(c00);
		c10 = new BlochVectorAnalyzer("Ca1b0", comps.get(1));	map.get(c10.getLabel()).add(c10);
		c01 = new BlochVectorAnalyzer("Ca0b1", comps.get(2));	map.get(c01.getLabel()).add(c01);
		c11 = new BlochVectorAnalyzer("Ca1b1", comps.get(3));	map.get(c11.getLabel()).add(c11);
		ca0 = BlochVectorAnalyzer.sum("Ca0", c00, c01);			map.get(ca0.getLabel()).add(ca0);
		ca1 = BlochVectorAnalyzer.sum("Ca1", c10, c11);			map.get(ca1.getLabel()).add(ca1);
		cb0 = BlochVectorAnalyzer.sum("Cb0", c00, c10);			map.get(cb0.getLabel()).add(cb0);
		cb1 = BlochVectorAnalyzer.sum("Cb1", c01, c11);			map.get(cb1.getLabel()).add(cb1);
		
		// divide all map entries and return the map
		for (BlochVectorAnalyzer v : map.values()) {
			v.scale(2);
		}
		return map;
	}

}
