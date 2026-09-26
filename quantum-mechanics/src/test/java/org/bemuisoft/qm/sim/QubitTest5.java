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
 * Testing five-qubit systems.
 * <p>
 * Not intended for automated testing.
 * Log output should be inspected visually.
 * 
 * @author Benno Muilwijk
 */
public class QubitTest5 extends QubitTestBase {

	public static void main(String[] args) {
		try {
			QubitTest5 test = new QubitTest5();
			test.runRandom();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	QubitQM qa, qb, qc, qd, qe;

	public QubitTest5() {
		super(5, false);
		this.qa = qubit('A');
		this.qb = qubit('B');
		this.qc = qubit('C');
		this.qd = qubit('D');
		this.qe = qubit('E');
	}

	public void run() {
		log("Start run");
		qa.ry(randomTheta());
		qb.ry(randomTheta());
		qc.ry(randomTheta());
		qd.ry(randomTheta());
		qe.ry(randomTheta());
//		inspect("After RY on all");
		qa.cp(random2Pi(), qb);
//		inspect("After CP on AB");
		qc.cp(random2Pi(), qd);
//		inspect("After CP on CD");
		qe.cp(random2Pi(), qa);
//		inspect("After CP on EA");
		qd.cp(random2Pi(), qc);
		qe.cp(random2Pi(), qc);
		qe.h();
		inspect("After CP on EC");
		analyzeRotation(qe, "RY", rad -> qe.ry(rad));
	}

	public void runRandom() {
		log("Start runRandom");
		qa.ry(randomTheta());
		qb.ry(randomTheta());
		qc.ry(randomTheta());
		qd.ry(randomTheta());
		qe.ry(randomTheta());
		entangleRandom(qa, qb);
		entangleRandom(qc, qd);
		entangleRandom(qe, qa);
		entangleRandom(qb, qc);
		entangleRandom(qd, qe);
		entangleRandom(qa, qc);
		entangleRandom(qb, qd);
		entangleRandom(qe, qb);
		entangleRandom(qd, qa);
		entangleRandom(qc, qe);
//		analyzeComponentLengths(qa);
//		analyzeComponentLengths(qb);
//		analyzeComponentLengths(qc);
//		analyzeComponentLengths(qd);
//		analyzeComponentLengths(qe);
//		analyzeRotation(qe, "RY", rad -> qe.ry(rad));
		analyzeComponents(qe);
//		analyzeRotation(qe);
//		analyzeComponentRotations(qe);
	}

	public void inspect(String description) {
		log("");
		log(description);
		log(qa.toString());
		log(qb.toString());
		log(qc.toString());
		log(qd.toString());
		log(qe.toString());
		printLengths(qa, qe, "Ae0, Ae1, Ae, Ea0, Ea1, Ea: ");
		printLengths(qb, qe, "Be0, Be1, Be, Eb0, Eb1, Eb: ");
		printLengths(qc, qe, "Ce0, Ce1, Ce, Ec0, Ec1, Ec: ");
		printLengths(qd, qe, "De0, De1, De, Ed0, Ed1, Ed: ");
		printLengths(qc, qd, "Cd0, Cd1, Cd, Dc0, Dc1, Dc: ");
		
		printLengths(qa, qb, "Ab0, Ab1, Ab, Ba0, Ba1, Ba: ");
		printLengths(qa, qc, "Ac0, Ac1, Ac, Ca0, Ca1, Ca: ");
		printLengths(qa, qd, "Ad0, Ad1, Ad, Da0, Da1, Da: ");
		printLengths(qa, qe, "Ae0, Ae1, Ae, Ea0, Ea1, Ea: ");
	}

	public void printLengths(QubitQM q1, QubitQM q2, String description) {
		List<SimpleBlochVector> comps = blochVectorComponents(q1, q2);
		log(description + '\t' + comps.get(0).length()
						+ '\t' + comps.get(1).length()
						+ '\t' + (comps.get(0).length() + comps.get(1).length())
						+ '\t' + comps.get(2).length()
						+ '\t' + comps.get(3).length()
						+ '\t' + (comps.get(2).length() + comps.get(3).length()));
	}

}
