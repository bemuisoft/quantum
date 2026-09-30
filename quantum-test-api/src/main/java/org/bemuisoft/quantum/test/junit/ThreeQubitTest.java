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
package org.bemuisoft.quantum.test.junit;

import static org.junit.jupiter.api.Assertions.assertAll;

import org.bemuisoft.quantum.api.IQubit;
import org.bemuisoft.quantum.api.IQubitFactory;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

/**
 * Tests the {@link IQubit} interface in a three-qubit system.
 * 
 * @param <Q>	the type of qubit to be tested
 * 
 * @author Benno Muilwijk
 */
public class ThreeQubitTest<Q extends IQubit> extends AbstractQubitTest<Q> {

	/** Qubit A. */
	protected Q qa;
	/** Qubit B. */
	protected Q qb;
	/** Qubit C. */
	protected Q qc;

	/**
	 * Default constructor.
	 */
	public ThreeQubitTest() {
		super(3);
	}

	@Override
	public final ThreeQubitTest<Q> init(IQubitFactory<Q> factory) {
		super.init(factory);
		qa = q(0);
		qb = q(1);
		qc = q(2);
		return this;
	}

	@Override
	public void runAll() {
		assertAll(getHeading(),
			run(() -> testInitialState()),
			run(() -> testGHZ()),
			run(() -> testXXX()),
			run(() -> testXYY()),
			run(() -> testYXY()),
			run(() -> testYYX()),
			run(() -> testToffoli())
		);
	}

	/**
	 * Asserts that the initial state is |000⟩.
	 */
	@Test
	public void testInitialState() {
		// initial state should be |000⟩
		assertIsEqual("Initial state - qa", 0, qa.measure());
		assertIsEqual("Initial state - qb", 0, qb.measure());
		assertIsEqual("Initial state - qc", 0, qb.measure());
	}

	/**
	 * Asserts that GHZ state |GHZ⟩ gives the expected correlations
	 * for a ZZZ measurement.
	 * 
	 * @see <a href="https://en.wikipedia.org/wiki/Greenberger%E2%80%93Horne%E2%80%93Zeilinger_state">GHZ state</a>
	 */
	@RepeatedTest(10)
	public final void testGHZ() {
		// test - |GHZ⟩ should give either |000⟩ or |111⟩ when measured (with normal ZZZ measurement)
		qa.h();
		qb.cnot(qa);
		qc.cnot(qb);
		int outcome = qa.measure();
		assertIsEqual("|GHZ⟩ - qb", outcome, qb.measure());
		assertIsEqual("|GHZ⟩ - qc", outcome, qc.measure());
	}

	/**
	 * Asserts that GHZ state |GHZ⟩ gives the expected correlations
	 * for an XXX measurement.
	 * 
	 * @see <a href="https://en.wikipedia.org/wiki/Greenberger%E2%80%93Horne%E2%80%93Zeilinger_state">GHZ state</a>
	 */
	@RepeatedTest(10)
	public final void testXXX() {
		int corr;	// correlation, +1 = even, -1 = odd
		// test - an XXX measurement on |GHZ⟩ should give even parity
		qa.h();
		qb.cnot(qa);
		qc.cnot(qb);
		corr = qa.h().measureSign() * qb.h().measureSign() * qc.h().measureSign();
		assertIsEqual("|GHZ⟩ - XXX correlation", +1, corr);
	}

	/**
	 * Asserts that GHZ state |GHZ⟩ gives the expected correlations
	 * for an XYY measurement.
	 * 
	 * @see <a href="https://en.wikipedia.org/wiki/Greenberger%E2%80%93Horne%E2%80%93Zeilinger_state">GHZ state</a>
	 */
	@RepeatedTest(10)
	public final void testXYY() {
		int corr;	// correlation, +1 = even, -1 = odd
		// test - an XYY measurement on |GHZ⟩ should give odd parity
		qa.h();
		qb.cnot(qa);
		qc.cnot(qb);
		corr = qa.h().measureSign() * qb.sx().measureSign() * qc.sx().measureSign();
		assertIsEqual("|GHZ⟩ - XYY correlation", -1, corr);
	}

	/**
	 * Asserts that GHZ state |GHZ⟩ gives the expected correlations
	 * for a YXY measurement.
	 * 
	 * @see <a href="https://en.wikipedia.org/wiki/Greenberger%E2%80%93Horne%E2%80%93Zeilinger_state">GHZ state</a>
	 */
	@RepeatedTest(10)
	public final void testYXY() {
		int corr;	// correlation, +1 = even, -1 = odd
		// test - a YXY measurement on |GHZ⟩ should give odd parity
		qa.h();
		qb.cnot(qa);
		qc.cnot(qb);
		corr = qa.sx().measureSign() * qb.h().measureSign() * qc.sx().measureSign();
		assertIsEqual("|GHZ⟩ - YXY correlation", -1, corr);
	}

	/**
	 * Asserts that GHZ state |GHZ⟩ gives the expected correlations
	 * for a YYX measurement.
	 * 
	 * @see <a href="https://en.wikipedia.org/wiki/Greenberger%E2%80%93Horne%E2%80%93Zeilinger_state">GHZ state</a>
	 */
	@RepeatedTest(10)
	public final void testYYX() {
		int corr;	// correlation, +1 = even, -1 = odd
		// test - a YYX measurement on |GHZ⟩ should give odd parity
		qa.h();
		qb.cnot(qa);
		qc.cnot(qb);
		corr = qa.sx().measureSign() * qb.sx().measureSign() * qc.h().measureSign();
		assertIsEqual("|GHZ⟩ - YYX correlation", -1, corr);
	}

	/**
	 * Asserts that the Toffoli gate works as expected.
	 */
	@Test
	public final void testToffoli() {
		// test 0 - toffoli on |000⟩ should give |000⟩
		qc.toffoli(qa, qb);
		assertIsEqual("Toffoli |000⟩ - qa", 0, qa.measure());
		assertIsEqual("Toffoli |000⟩ - qb", 0, qb.measure());
		assertIsEqual("Toffoli |000⟩ - qc", 0, qc.measure());
		
		// test 1 - toffoli on |001⟩ should give |001⟩
		qc.not();
		qc.toffoli(qa, qb);
		assertIsEqual("Toffoli |001⟩ - qa", 0, qa.measure());
		assertIsEqual("Toffoli |001⟩ - qb", 0, qb.measure());
		assertIsEqual("Toffoli |001⟩ - qc", 1, qc.measure());
		
		// test 2 - toffoli on |010⟩ should give |010⟩
		qb.not();
		qc.not();
		qc.toffoli(qa, qb);
		assertIsEqual("Toffoli |010⟩ - qa", 0, qa.measure());
		assertIsEqual("Toffoli |010⟩ - qb", 1, qb.measure());
		assertIsEqual("Toffoli |010⟩ - qc", 0, qc.measure());
		
		// test 3 - toffoli on |011⟩ should give |011⟩
		qc.not();
		qc.toffoli(qa, qb);
		assertIsEqual("Toffoli |011⟩ - qa", 0, qa.measure());
		assertIsEqual("Toffoli |011⟩ - qb", 1, qb.measure());
		assertIsEqual("Toffoli |011⟩ - qc", 1, qc.measure());
		
		// test 4 - toffoli on |100⟩ should give |100⟩
		qa.not();
		qb.not();
		qc.not();
		qc.toffoli(qa, qb);
		assertIsEqual("Toffoli |100⟩ - qa", 1, qa.measure());
		assertIsEqual("Toffoli |100⟩ - qb", 0, qb.measure());
		assertIsEqual("Toffoli |100⟩ - qc", 0, qc.measure());
		
		// test 5 - toffoli on |101⟩ should give |101⟩
		qc.not();
		qc.toffoli(qa, qb);
		assertIsEqual("Toffoli |101⟩ - qa", 1, qa.measure());
		assertIsEqual("Toffoli |101⟩ - qb", 0, qb.measure());
		assertIsEqual("Toffoli |101⟩ - qc", 1, qc.measure());
		
		// test 6 - toffoli on |110⟩ should give |111⟩
		qb.not();
		qc.not();
		qc.toffoli(qa, qb);
		assertIsEqual("Toffoli |110⟩ - qa", 1, qa.measure());
		assertIsEqual("Toffoli |110⟩ - qb", 1, qb.measure());
		assertIsEqual("Toffoli |110⟩ - qc", 1, qc.measure());
		
		// test 7 - toffoli on |111⟩ should give |110⟩
		qc.toffoli(qa, qb);
		assertIsEqual("Toffoli |111⟩ - qa", 1, qa.measure());
		assertIsEqual("Toffoli |111⟩ - qb", 1, qb.measure());
		assertIsEqual("Toffoli |111⟩ - qc", 0, qc.measure());
	}

}
