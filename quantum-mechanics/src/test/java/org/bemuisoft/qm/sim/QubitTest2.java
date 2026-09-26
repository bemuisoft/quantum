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

import org.bemuisoft.qm.core.SimpleBlochVector;
import org.bemuisoft.qm.core.BlochVectorAnalyzer;

/**
 * Testing two-qubit systems.
 * <p>
 * Not intended for automated testing.
 * Log output should be inspected visually.
 * 
 * @author Benno Muilwijk
 */
public class QubitTest2 extends QubitTestBase {

	public static void main(String[] args) {
		try {
			QubitTest2 test = new QubitTest2();
			test.run();
//			test.runBell();
//			test.runRandom();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	QubitQM qa, qb;

	public QubitTest2() {
		super(2, true);
		this.qa = qubit('A');
		this.qb = qubit('B');
	}

	public void run() {
		log("Start run");
//		inspect("Initial state");
		qa.ry(PI/3);
		qb.ry(PI/2);
		qb.cp(PI, qa);
		inspect("After CP on A and B");
		analyzeRotation(qa, "RX", rad -> qa.rx(rad));
		analyzeRotation(qa, "RY", rad -> qa.ry(rad));
		analyzeRotation(qb, "RX", rad -> qb.rx(rad));
		analyzeRotation(qb, "RY", rad -> qb.ry(rad));
//		qb.ry(-PI/4);
//		inspect("After RY on B");
		qb.rz( HALF_PI);
//		inspect("After RZ on B");
//		qb.ry( PI/4);
//		inspect("After RY on B");
//		qb.rz(PI/3);
		inspect("After RZ on B");
		analyzeRotation(qa, "RX", rad -> qa.rx(rad));
		analyzeRotation(qa, "RY", rad -> qa.ry(rad));
		analyzeRotation(qb, "RX", rad -> qb.rx(rad));
		analyzeRotation(qb, "RY", rad -> qb.ry(rad));
//		qb.rx(PI/2);
//		inspect("After RX on B");
//		analyzeRotation(qb, "RX", rad -> qb.rx(rad));
//		analyzeRotation(qb, "RY", rad -> qb.ry(rad));
	}

	public void run0() {
//		inspect("Initial state");
		qa.ry(4*PI/6);
		inspect("After RY on A");
		qa.rz(-2*PI/6);
		inspect("After RZ on A");
		qb.ry(3*PI/6);
		inspect("After RY on B");
		qb.cp(4*PI/6, qa);
		inspect("After CP on A and B");
		qa.rx(3*PI/6);
		inspect("After RX on A");
		qa.rx(3*PI/6);
		inspect("After RX on A");
	}

	public void run1() {
//		inspect("Initial state");
		qa.ry(PI/3);
		qb.ry(PI/2);
		qb.cp(PI, qa);
		inspect("After CP on A and B");
		qb.rz(PI/6);
		inspect("After RZ on B");
		qb.rx(PI/2);
		inspect("After RX on B");
//		qb.ry(-PI/2);
//		inspect("After RY on B");
	}

	public void run2() {
//		inspect("Initial state");
		qa.ry(3*PI/4);
		qb.ry(1*PI/3);
		qb.cp(PI/2, qa);
		inspect("After CP on A and B");
		qb.rz(PI/3);
		inspect("After RZ on B");
		for (int i = 1; i < 25; i++) {
			qb.ry(-PI/12);
			inspect("After RY on B");
		}
	}

	public void runBell() {
//		qa.h();
//		qa.ry(PI/3);
		qa.ry(randomTheta());
		qb.ry(randomTheta());
		inspectDensityMatrix("Not entangled");
		qa.rz(random2Pi());
		qb.rz(random2Pi());
		inspectDensityMatrix("Not entangled");
		qb.cx(qa);
		inspectDensityMatrix("Bell Phi+");
		qa.z();
		inspectDensityMatrix("Bell Phi-");
		qa.y();
		inspectDensityMatrix("Bell Psi+");
		qa.z();
		inspectDensityMatrix("Bell Psi-");
	}

	public void runRandom() {
		log("Start runRandom");
		qa.ry(randomTheta());
		qb.ry(randomTheta());
		entangleRandom(qa, qb);
		analyzeRotation(qa);
		analyzeRotation(qb);
	}

	public void inspectDensityMatrix(String description) {
		logDensityMatrix(description);
		log(qa);
		log(new BlochVectorAnalyzer("center", qa).scale(2));
		log(qb);
		log(new BlochVectorAnalyzer("center", qb).scale(2));
		SimpleBlochVector eq = new SimpleBlochVector("A==B", qs.densityMatrix(), 0, 3);
		SimpleBlochVector ne = new SimpleBlochVector("A!=B", qs.densityMatrix(), 1, 2);
		log(eq);
		log(new BlochVectorAnalyzer("normalized", eq).scale(eq.length()));
		log(ne);
		log(new BlochVectorAnalyzer("normalized", ne).scale(ne.length()));
	}

	public void inspect(String description) {
		log(description);
		logState();
//		log(qa.toString());
//		log(qb.toString());
		inspect(qa, qb);
		log("");
	}

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

	public void inspectRotation(QubitQM q) {
		BlochVectorAnalyzer center  = new BlochVectorAnalyzer(q).scale(2.0);	// shortcut, works only for 2 entangled qubits
		log(new BlochVectorAnalyzer(q.getLabel() + "0 - center", q.getComps().get(0)).subtract(center));
		log(new BlochVectorAnalyzer(q.getLabel() + "1 - center", q.getComps().get(1)).subtract(center));
	}

	public void analyzeRotationOld(Consumer<Double> rotation) {
		// get center of rotation for each qubit component vector
		// by getting the average of the current vector and after rotating by pi
		Map<String, BlochVectorAnalyzer> map0 = getCentersOfRotation(rotation);
		Map<String, BlochVectorAnalyzer> dia0 = getDiagonals(rotation);
		
		// do the same in perpendicular direction
		rotation.accept(HALF_PI);
		Map<String, BlochVectorAnalyzer> map1 = getCentersOfRotation(rotation);
		Map<String, BlochVectorAnalyzer> dia1 = getDiagonals(rotation);
		
		// restore to original state
		rotation.accept(-HALF_PI);
		log(qa);
		log(qb);
		
		// compare centers in both directions (should be the same for each qubit component)
		for (BlochVectorAnalyzer v0 : map0.values()) {
			String label = v0.getLabel();
			BlochVectorAnalyzer v1 = map1.get(label);
			double diffX = v1.getX() - v0.getX();
			double diffY = v1.getY() - v0.getY();
			double diffZ = v1.getZ() - v0.getZ();
			double margin = 1e-14;
			if (Math.abs(diffX) > margin || Math.abs(diffY) > margin || Math.abs(diffZ) > margin) {
				if (Math.abs(diffX) > margin) {
					log("Center of " + label + " is ambiguous in X direction by " + diffX);
				}
				if (Math.abs(diffY) > margin) {
					log("Center of " + label + " is ambiguous in Y direction by " + diffY);
				}
				if (Math.abs(diffZ) > margin) {
					log("Center of " + label + " is ambiguous in Z direction by " + diffZ);
				}
			}
			else {
				log("Center of " + label + " is " + v0);
				BlochVectorAnalyzer axis = BlochVectorAnalyzer.crossProduct(label, dia0.get(label), dia1.get(label));
				log("  Axis of " + label + " is " + axis.scale(axis.length()));
			}
		}
	}

	private Map<String, BlochVectorAnalyzer> getCentersOfRotation(Consumer<Double> rotation) {
		// labels are based on IBM convention (qubit A is represented by the least significant bit)
		LinkedHashMap<String, BlochVectorAnalyzer> map = new LinkedHashMap<>();
		List<SimpleBlochVector> comps;
		comps = qa.getComps();
		BlochVectorAnalyzer a00 = new BlochVectorAnalyzer("Ab0", comps.get(0));	map.put(a00.getLabel(), a00);
		BlochVectorAnalyzer a10 = new BlochVectorAnalyzer("Ab1", comps.get(1));	map.put(a10.getLabel(), a10);
		comps = qb.getComps();
		BlochVectorAnalyzer b00 = new BlochVectorAnalyzer("Ba0", comps.get(0));	map.put(b00.getLabel(), b00);
		BlochVectorAnalyzer b10 = new BlochVectorAnalyzer("Ba1", comps.get(1));	map.put(b10.getLabel(), b10);
		
		rotation.accept(PI);
		
		comps = qa.getComps();
		a00 = new BlochVectorAnalyzer("Ab0", comps.get(0));	map.get(a00.getLabel()).add(a00);
		a10 = new BlochVectorAnalyzer("Ab1", comps.get(1));	map.get(a10.getLabel()).add(a10);
		comps = qb.getComps();
		b00 = new BlochVectorAnalyzer("Ba0", comps.get(0));	map.get(b00.getLabel()).add(b00);
		b10 = new BlochVectorAnalyzer("Ba1", comps.get(1));	map.get(b10.getLabel()).add(b10);
		
		// divide all map entries and return the map
		for (BlochVectorAnalyzer v : map.values()) {
			v.scale(2);
		}
		return map;
	}

	private Map<String, BlochVectorAnalyzer> getDiagonals(Consumer<Double> rotation) {
		// labels are based on IBM convention (qubit A is represented by the least significant bit)
		LinkedHashMap<String, BlochVectorAnalyzer> map = new LinkedHashMap<>();
		List<SimpleBlochVector> comps;
		comps = qa.getComps();
		BlochVectorAnalyzer a00 = new BlochVectorAnalyzer("Ab0", comps.get(0));	map.put(a00.getLabel(), a00);
		BlochVectorAnalyzer a10 = new BlochVectorAnalyzer("Ab1", comps.get(1));	map.put(a10.getLabel(), a10);
		comps = qb.getComps();
		BlochVectorAnalyzer b00 = new BlochVectorAnalyzer("Ba0", comps.get(0));	map.put(b00.getLabel(), b00);
		BlochVectorAnalyzer b10 = new BlochVectorAnalyzer("Ba1", comps.get(1));	map.put(b10.getLabel(), b10);
		
		rotation.accept(PI);
		
		comps = qa.getComps();
		a00 = new BlochVectorAnalyzer("Ab0", comps.get(0));	map.get(a00.getLabel()).subtract(a00);
		a10 = new BlochVectorAnalyzer("Ab1", comps.get(1));	map.get(a10.getLabel()).subtract(a10);
		comps = qb.getComps();
		b00 = new BlochVectorAnalyzer("Ba0", comps.get(0));	map.get(b00.getLabel()).subtract(b00);
		b10 = new BlochVectorAnalyzer("Ba1", comps.get(1));	map.get(b10.getLabel()).subtract(b10);
		
//		// divide all map entries and return the map
//		for (BlochVectorAnalyzer v : map.values()) {
//			v.scale(2);
//		}
		return map;
	}

}
