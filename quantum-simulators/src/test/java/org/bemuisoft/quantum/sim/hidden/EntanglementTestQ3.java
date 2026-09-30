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
package org.bemuisoft.quantum.sim.hidden;

import org.bemuisoft.quantum.test.api.AbstractExperiment;

/**
 * Entanglement test for {@link QubitV3}.
 * <p>
 * Not intended for automated testing.
 * Log output should be inspected visually.
 * 
 * @author Benno Muilwijk
 */
public class EntanglementTestQ3 extends AbstractExperiment<QubitV3> {

	public static void main(String[] args) {
		try {
			EntanglementTestQ3 test = new EntanglementTestQ3(3);	// test with n qubits
			test.run(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public EntanglementTestQ3(int n) {
		super(n, QubitV3.factory(n, true));
	}

	@Override
	public void run() {
//		adHoc();
		bellsInequalityY();
//		oddH();
//		cz();
//		ghz();
		measureAll();
	}

	void adHoc() {
		// QM predicts the following amplitudes and probabilities:
		// 000	0.86603 	0.00000π	0.75000
		// 001	0.50000 	0.00000π	0.25000
		q(0).ry(PI/3);
	}

	void bellsInequalityX() {
		// QM predicts the following amplitudes and probabilities:
		// 000	0.00000 	0.00000π	0.00000
		// 001	0.70711 	1.00000π	0.50000
		// 010	0.70711 	0.00000π	0.50000
		// 011	0.00000 	0.00000π	0.00000
		q(0).ry(-HALF_PI);
		q(1).x();
		q(1).cx(q(0));
		q(0).rx(PI/4);
		q(1).rx(PI/4);
	}

	void bellsInequalityY() {
		// QM predicts the following amplitudes and probabilities:
		// 000	0.00000 	0.00000π	0.00000
		// 001	0.70711 	1.00000π	0.50000
		// 010	0.70711 	0.00000π	0.50000
		// 011	0.00000 	0.00000π	0.00000
		q(0).ry(-HALF_PI);
		q(1).x();
		q(1).cx(q(0));
		q(0).ry(PI/4);
		q(1).ry(PI/4);
	}

	void cz() {
		q(0).h();
		q(1).h();
		q(1).cz(q(0));
		q(1).h();
	}

	void oddH() {
		q(0).h();
		q(1).cx(q(0));
		q(0).h();
		q(1).h();
		q(1).cx(q(0));
		q(0).h();
		q(0).ry(PI/3);
	}

	void ghz() {
		q(0).h();
		q(1).cx(q(0));
		q(2).cx(q(1));
	}

}
