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

package org.bemuisoft.quantum.test.api;

import org.bemuisoft.quantum.api.IQubit;
import org.bemuisoft.quantum.api.IQubitFactory;

/**
 * A generic CHSH experiment as <a href="https://en.wikipedia.org/wiki/Bell_test">Bell test</a>.
 * 
 * @param <Q>	the type of qubit tested in this experiment
 * 
 * @author Benno Muilwijk
 */
public class CHSHExperiment<Q extends IQubit> extends AbstractExperiment<Q> {

	private int runIndex;
	private double[] ev = new double[4];

	/**
	 * Constructor.
	 * <p>
	 * Sets up the experiment for two qubits.
	 * The specified qubit factory is used to instantiate the qubits.
	 * 
	 * @param factory	the qubit factory to use
	 */
	public CHSHExperiment(IQubitFactory<Q> factory) {
		super(2, factory);
	}

	/**
	 * Runs each of the four experiments the specified number of times
	 * and prints the results to {@code System.out}.
	 * 
	 * @param times	number of times to run each experiment
	 */
	@Override
	public void run(int times) {
		for (int i = 0; i < ev.length; i++) {
			runIndex = i + 1;
			log("Run" + runIndex);
			super.run(times);
			ev[i] = getExpectationValue();
			log("E" + runIndex + " = " + ev[i]);
			resetStatistics();
		}
		log("S  = " + (ev[0] - ev[1] + ev[2] + ev[3]));
	}

	/**
	 * Runs one of the four experiments one time
	 * and measures both qubits.
	 */
	@Override
	public void run() {
		switch (runIndex) {
			case 1: run1(); break;
			case 2: run2(); break;
			case 3: run3(); break;
			case 4: run4(); break;
		}
		measureAll();
	}

	/**
	 * Runs CHSH experiment 1 one time.
	 */
	public void run1() {
		q(0).h();
		q(1).cx(q(0));
		q(1).ry(QUARTER_PI);
	}

	/**
	 * Runs CHSH experiment 2 one time.
	 */
	public void run2() {
		q(0).h();
		q(1).cx(q(0));
		q(1).ry(QUARTER_PI * 3.0);
	}

	/**
	 * Runs CHSH experiment 3 one time.
	 */
	public void run3() {
		q(0).h();
		q(1).cx(q(0));
		q(0).ry(HALF_PI);
		q(1).ry(QUARTER_PI);
	}

	/**
	 * Runs CHSH experiment 4 one time.
	 */
	public void run4() {
		q(0).h();
		q(1).cx(q(0));
		q(0).ry(HALF_PI);
		q(1).ry(QUARTER_PI * 3.0);
	}

	/**
	 * Returns the expectation value for one of the four CHSH experiments.
	 * 
	 * @return	the CHSH expectation value
	 */
	private double getExpectationValue() {
		double n = getTotal(0b00) - getTotal(0b01) - getTotal(0b10) + getTotal(0b11);
		return n / getTotal();
	}

}
