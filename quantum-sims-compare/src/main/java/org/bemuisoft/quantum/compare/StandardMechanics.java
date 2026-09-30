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
package org.bemuisoft.quantum.compare;

import org.bemuisoft.qm.sim.QubitQM;
import org.bemuisoft.quantum.sim.copenhagen.StandardQubit;
import org.bemuisoft.quantum.test.api.AbstractExperiment;
import org.bemuisoft.quantum.test.api.QubitTester;

/**
 * Runs quantum experiments on
 * {@link StandardQubit} in quantum-simulators
 * and {@link QubitQM} in quantum-mechanics,
 * and compares the state after each change.
 * <p>
 * The actual comparison is done by {@link QubitTester},
 * which logs differences to the console.
 * 
 * @author Benno Muilwijk
 */
public class StandardMechanics extends AbstractExperiment<QubitTester> {

	/**
	 * Main method.
	 * <p>
	 * Starts the experiment(s) in the {@code run} method.
	 * 
	 * @param args	command line arguments (ignored)
	 */
	public static void main(String[] args) {
		try {
//			StandardQubit.debug = true;
			QubitQM.debug = false;
			StandardMechanics test = new StandardMechanics(3);	// test with n qubits
			test.run(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constructor.
	 * 
	 * @param n	- number of qubits used in this experiment
	 */
	public StandardMechanics(int n) {
		super(n, QubitTester.factory(StandardQubit.factory(n, true), QubitQM.factory(n, true)));
	}

	@Override
	public void run() {
		randomPure();
		randomMixed();
		multiControl();
//		bell();
		measureAll();
	}

	void randomPure() {
		q(0).ry(randomTheta()).rz(random2Pi());
		q(1).ry(randomTheta()).rz(random2Pi());
		q(2).ry(randomTheta()).rz(random2Pi());
	}

	void randomMixed() {
		entangleRandom(q(0), q(1));
		entangleRandom(q(1), q(2));
		entangleRandom(q(2), q(0));
	}

	void multiControl() {
		q(0).h();
		q(1).h();
		q(2).h();
		q(2).ccp(HALF_PI, q(0), q(1));
		q(2).ccz(q(0), q(1));
		q(2).toffoli(q(0), q(1));
		q(2).c(Z, q(0), q(1));
		q(2).c(X, q(0), q(1));
		q(2).c(Y, q(0), q(1));
		q(2).c(H, q(0), q(1));
		q(2).cu(1, 2, 3, q(0), q(1));
		q(2).cr(Z, 1, q(0), q(1));
		q(2).cr(X, 1, q(0), q(1));
		q(2).cr(Y, 1, q(0), q(1));
		q(2).cr(H, 1, q(0), q(1));
	}

	void bell() {
		QubitQM.debug = true;
		q(0).h();
		q(1).h();
		q(1).cz(q(0));
//		q(1).h();
		q(1).ry(-HALF_PI);
		for (int i = 0; i < 4; i++) {
			q(1).rx(HALF_PI);
			log(q(0), 0);
			log(q(0), 1);
			log(q(1), 0);
			log(q(1), 1);
		}
	}

	void log(QubitTester q, int i) {
		StringBuilder sb = new StringBuilder(q.getLabel());
		if (q.getX(i) != 0.0) {
			sb.append(" x.").append(i).append('=').append(q.getX(i));
		}
		if (q.getY(i) != 0.0) {
			sb.append(" y.").append(i).append('=').append(q.getY(i));
		}
		if (q.getZ(i) != 0.0) {
			sb.append(" z.").append(i).append('=').append(q.getZ(i));
		}
		log(sb.toString());
	}

}
