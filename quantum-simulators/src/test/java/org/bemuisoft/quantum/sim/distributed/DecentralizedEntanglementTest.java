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
package org.bemuisoft.quantum.sim.distributed;

import org.bemuisoft.quantum.sim.hidden.QubitV1;
import org.bemuisoft.quantum.test.api.AbstractExperiment;
import org.bemuisoft.quantum.test.api.QubitTester;

/**
 * Entanglement test for {@link DecentralizedQubit}.
 * <p>
 * Not intended for automated testing.
 * Log output should be inspected visually.
 * 
 * @author Benno Muilwijk
 */
public class DecentralizedEntanglementTest extends AbstractExperiment<QubitTester> {

	public static void main(String[] args) {
		try {
			DecentralizedQubit.debug = true;
			DecentralizedEntanglementTest test = new DecentralizedEntanglementTest(3);	// test with n qubits
			test.run(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DecentralizedEntanglementTest(int n) {
		super(n, QubitTester.factory(DecentralizedQubit.factory(n), QubitV1.factory(n, true)));
	}

	@Override
	public void run() {
//		randomPure();
//		adHoc();
//		toFix();	// Fixed!
//		base();
//		randomPure();
//		randomMixed();
//		bellsInequalityX();
//		bellsInequalityY();
//		oddH();
//		cz();
//		ghz();
		ghzXXX();
//		ghzXYY();
//		ghzYXY();
//		ghzYYX();
		measureAll();
	}

	void adHoc() {
		q(0).ry(PI/2);
//		verifyAll();
		q(1).ry(PI/2);
//		verifyAll();
//		q(1).rz(PI/3);
//		verifyAll();
		q(1).cp(PI, q(0));
//		verifyAll();
//		q(0).p(random2Pi());
//		q(1).p(random2Pi());
//		q(1).rz( PI/4);
//		q(1).ry(-PI/2);
		q(0).rz(0.0);
		q(1).rz( PI/2);
		q(0).rz(-PI/2);
		q(1).rx( PI/2);
		q(0).rz(0.0);
		q(1).rx(-PI/2);
//		q(1).ry(random2Pi());
//		q(1).rx(random2Pi());
//		verifyAll();
		q(0).rz(0.0);
	}

	void toFix() {
		q(0).ry(PI/3);
		q(1).ry(PI/2);
		q(1).cp(PI, q(0));
		q(1).rz( PI/5);
		q(1).ry( PI/4);
		q(0).rz(0.0);
	}

	void verifyAll() {
		// verify internal states
		q(0).rz(0.0);
		q(1).rz(0.0);
		q(2).rz(0.0);
	}

	void base() {
		// QM predicts the following amplitudes and probabilities:
		// 000	0.86603 	0.00000π	0.75000
		// 001	0.50000 	0.00000π	0.25000
		q(0).ry(PI/3);
		q(0).rz(PI/3);
		q(1).rx(0.0);
		q(2).ry(0.0);
	}

	void randomPure() {
		q(0).ry(randomTheta()).rz(random2Pi());
		verifyAll();
		q(1).ry(randomTheta()).rz(random2Pi());
		verifyAll();
		q(2).ry(randomTheta()).rz(random2Pi());
		verifyAll();
	}

	void randomMixed() {
		entangleRandom(q(0), q(1));
		verifyAll();
		entangleRandom(q(1), q(2));
		verifyAll();
		entangleRandom(q(2), q(0));
		verifyAll();
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
		verifyAll();
		q(0).rx(PI/4);
		verifyAll();
		q(1).rx(PI/4);
		verifyAll();
	}

	void bellsInequalityY() {
		// QM predicts the following amplitudes and probabilities:
		// 000	0.00000 	0.00000π	0.00000
		// 001	0.70711 	1.00000π	0.50000
		// 010	0.70711 	0.00000π	0.50000
		// 011	0.00000 	0.00000π	0.00000
		q(0).ry(-HALF_PI);
		q(1).x();
		q(0).rz(0.0);
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
		// QM predicts the following amplitudes and probabilities:
		// 000	0.86603 	0.00000π	0.75000
		// 001	0.50000 	0.00000π	0.25000
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

	void ghzXXX() {
		q(0).h();
		q(1).cx(q(0));
		q(2).cx(q(1));
		verifyAll();
		q(0).h();
		verifyAll();
		q(1).h();
		verifyAll();
		q(2).h();
		verifyAll();
	}

	void ghzXYY() {
		q(0).h();
		q(1).cx(q(0));
		q(2).cx(q(1));
		q(0).h();
		q(1).sx();
		q(2).sx();
	}

	void ghzYXY() {
		q(0).h();
		q(1).cx(q(0));
		q(2).cx(q(1));
		q(0).sx();
		q(1).h();
		q(2).sx();
	}

	void ghzYYX() {
		q(0).h();
		q(1).cx(q(0));
		q(2).cx(q(1));
		q(0).sx();
		q(1).sx();
		q(2).h();
	}

}
