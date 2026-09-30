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

import org.bemuisoft.quantum.adapters.quantum4j.Qubit4J;
import org.bemuisoft.quantum.sim.copenhagen.StandardQubit;
import org.bemuisoft.quantum.sim.hidden.QubitV1;
import org.bemuisoft.quantum.test.api.AbstractExperiment;
import org.bemuisoft.quantum.test.api.QubitTester;

/**
 * Runs quantum experiments on
 * {@link Qubit4J} in quantum4j-adapter
 * and {@link StandardQubit} in quantum-simulators,
 * and compares the state after each change.
 * <p>
 * The actual comparison is done by {@link QubitTester},
 * which logs differences to the console.
 * 
 * @author Benno Muilwijk
 */
public class Quantum4JStandard extends AbstractExperiment<QubitTester> {

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
			Quantum4JStandard test = new Quantum4JStandard(3);	// test with n qubits
			test.run(100);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constructor.
	 * 
	 * @param n	- number of qubits used in this experiment
	 */
	public Quantum4JStandard(int n) {
		super(n, QubitTester.factory(Qubit4J.factory(n), QubitV1.factory(n, true)));
	}

	@Override
	public void run() {
		randomPure();
		randomMixed();
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

}
