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

import org.bemuisoft.quantum.api.IQubitAnalyzer;
import org.bemuisoft.quantum.api.IQubitFactory;
import org.junit.jupiter.api.Test;

/**
 * Tests the required methods of the {@link IQubitAnalyzer}
 * interface in a two-qubit system.
 * 
 * @param <Q>	the type of qubit to be tested
 * 
 * @author Benno Muilwijk
 */
public class TwoQubitAnalyzerTest<Q extends IQubitAnalyzer> extends AbstractQubitTest<Q> {

	/** Qubit A. */
	protected Q qa;
	/** Qubit B. */
	protected Q qb;

	/**
	 * Default constructor.
	 */
	public TwoQubitAnalyzerTest() {
		super(2);
	}

	@Override
	public final TwoQubitAnalyzerTest<Q> init(IQubitFactory<Q> factory) {
		super.init(factory);
		qa = q(0);
		qb = q(1);
		return this;
	}

	@Override
	public void runAll() {
		assertAll(getHeading(),
			run(() -> testInitialState()),
			run(() -> testZbasis()),
			run(() -> testXbasis()),
			run(() -> testControlZero()),
			run(() -> testControlOne()),
			run(() -> testTargetPlus()),
			run(() -> testTargetMinus())
		);
	}

	/**
	 * Asserts that the initial state is |00⟩.
	 */
	@Test
	public void testInitialState() {
		// initial state should be |00⟩
		assertXYZ("Initial state - qa", ZERO, ZERO, PLUS, qa);
		assertXYZ("Initial state - qb", ZERO, ZERO, PLUS, qb);
	}

	/**
	 * Asserts that the CNOT gate works as expected
	 * when the state of both qubits is either |0⟩ or |1⟩.
	 * <p>
	 * The CNOT gate should work exactly the same as
	 * the CX gate.
	 */
	@Test
	public final void testZbasis() {
		// test 0 - cnot on |00⟩ should give |00⟩
		qb.cnot(qa);
		assertXYZ("CNOT |00⟩ - qa", ZERO, ZERO, PLUS, qa);
		assertXYZ("CNOT |00⟩ - qb", ZERO, ZERO, PLUS, qb);
		
		// test 1 - cnot on |01⟩ should give |01⟩
		qb.not();
		qb.cnot(qa);
		assertXYZ("CNOT |01⟩ - qa", ZERO, ZERO, PLUS, qa);
		assertXYZ("CNOT |01⟩ - qb", ZERO, ZERO, MINUS, qb);
		
		// test 2 - cnot on |10⟩ should give |11⟩
		qa.not();
		qb.not();
		qb.cnot(qa);
		assertXYZ("CNOT |10⟩ - qa", ZERO, ZERO, MINUS, qa);
		assertXYZ("CNOT |10⟩ - qb", ZERO, ZERO, MINUS, qb);
		
		// test 3 - cnot on |11⟩ should give |10⟩
		qb.cnot(qa);
		assertXYZ("CNOT |11⟩ - qa", ZERO, ZERO, MINUS, qa);
		assertXYZ("CNOT |11⟩ - qb", ZERO, ZERO, PLUS, qb);
	}

	/**
	 * Asserts that the CNOT gate works as expected
	 * when the state of both qubits is either |+⟩ or |-⟩.
	 * <p>
	 * The CNOT gate should work exactly the same as
	 * the CX gate.
	 */
	@Test
	public final void testXbasis() {
		// test 0 - cnot on |++⟩ should give |++⟩
		qa.h();
		qb.h();
		qb.cnot(qa);
		assertXYZ("CNOT |++⟩ - qa", PLUS, ZERO, ZERO, qa);
		assertXYZ("CNOT |++⟩ - qb", PLUS, ZERO, ZERO, qb);
		
		// test 2 - cnot on |-+⟩ should give |-+⟩
		qa.z();
		qb.cnot(qa);
		assertXYZ("CNOT |-+⟩ - qa", MINUS, ZERO, ZERO, qa);
		assertXYZ("CNOT |-+⟩ - qb", PLUS, ZERO, ZERO, qb);
		
		// test 1 - cnot on |+-⟩ should give |--⟩
		qa.z();
		qb.z();
		qb.cnot(qa);
		assertXYZ("CNOT |+-⟩ - qa", MINUS, ZERO, ZERO, qa);
		assertXYZ("CNOT |+-⟩ - qb", MINUS, ZERO, ZERO, qb);
		
		// test 3 - cnot on |--⟩ should give |+-⟩
		qb.cnot(qa);
		assertXYZ("CNOT |--⟩ - qa", PLUS, ZERO, ZERO, qa);
		assertXYZ("CNOT |--⟩ - qb", MINUS, ZERO, ZERO, qb);
	}

	/**
	 * Asserts that the CNOT gate works as expected
	 * when the state of the control qubit is |0⟩.
	 * <p>
	 * The CNOT gate should work exactly the same as
	 * the CX gate.
	 */
	@Test
	public final void testControlZero() {
		// test 0 - cnot on |0+⟩ should give |0+⟩
		qb.h();
		qb.cnot(qa);
		assertXYZ("CNOT |0+⟩ - qa", ZERO, ZERO, PLUS, qa);
		assertXYZ("CNOT |0+⟩ - qb", PLUS, ZERO, ZERO, qb);
		
		// test 1 - cnot on |0-⟩ should give |0-⟩
		qb.z();
		qb.cnot(qa);
		assertXYZ("CNOT |0-⟩ - qa", ZERO, ZERO, PLUS, qa);
		assertXYZ("CNOT |0-⟩ - qb", MINUS, ZERO, ZERO, qb);
		
		// test 2 - cnot on |0»⟩ should give |0»⟩
		qb.sdg();
		qb.cnot(qa);
		assertXYZ("CNOT |0»⟩ - qa", ZERO, ZERO, PLUS, qa);
		assertXYZ("CNOT |0»⟩ - qb", ZERO, PLUS, ZERO, qb);
		
		// test 3 - cnot on |0«⟩ should give |0«⟩
		qb.z();
		qb.cnot(qa);
		assertXYZ("CNOT |0«⟩ - qa", ZERO, ZERO, PLUS, qa);
		assertXYZ("CNOT |0«⟩ - qb", ZERO, MINUS, ZERO, qb);
	}

	/**
	 * Asserts that the CNOT gate works as expected
	 * when the state of the control qubit is |1⟩.
	 * <p>
	 * The CNOT gate should work exactly the same as
	 * the CX gate.
	 */
	@Test
	public final void testControlOne() {
		// test 0 - cnot on |1+⟩ should give |1+⟩
		qa.not();
		qb.h();
		qb.cnot(qa);
		assertXYZ("CNOT |1+⟩ - qa", ZERO, ZERO, MINUS, qa);
		assertXYZ("CNOT |1+⟩ - qb", PLUS, ZERO, ZERO, qb);
		
		// test 1 - cnot on |1-⟩ should give |1-⟩
		qb.z();
		qb.cnot(qa);
		assertXYZ("CNOT |1-⟩ - qa", ZERO, ZERO, MINUS, qa);
		assertXYZ("CNOT |1-⟩ - qb", MINUS, ZERO, ZERO, qb);
		
		// test 2 - cnot on |1»⟩ should give |1«⟩
		qb.sdg();
		qb.cnot(qa);
		assertXYZ("CNOT |1»⟩ - qa", ZERO, ZERO, MINUS, qa);
		assertXYZ("CNOT |1»⟩ - qb", ZERO, MINUS, ZERO, qb);
		
		// test 3 - cnot on |1«⟩ should give |1»⟩
		qb.cnot(qa);
		assertXYZ("CNOT |1«⟩ - qa", ZERO, ZERO, MINUS, qa);
		assertXYZ("CNOT |1«⟩ - qb", ZERO, PLUS, ZERO, qb);
	}

	/**
	 * Asserts that the CNOT gate works as expected
	 * when the state of the target qubit is |+⟩.
	 * <p>
	 * The CNOT gate should work exactly the same as
	 * the CX gate.
	 */
	@Test
	public final void testTargetPlus() {
		// test 0 - cnot on |0+⟩ should give |0+⟩
		qb.h();
		qb.cnot(qa);
		assertXYZ("CNOT |0+⟩ - qa", ZERO, ZERO, PLUS, qa);
		assertXYZ("CNOT |0+⟩ - qb", PLUS, ZERO, ZERO, qb);
		
		// test 1 - cnot on |1+⟩ should give |1+⟩
		qa.not();
		qb.cnot(qa);
		assertXYZ("CNOT |1+⟩ - qa", ZERO, ZERO, MINUS, qa);
		assertXYZ("CNOT |1+⟩ - qb", PLUS, ZERO, ZERO, qb);
		
		// test 2 - cnot on |»+⟩ should give |»+⟩
		qa.rx(Math.PI/2);
		qb.cnot(qa);
		assertXYZ("CNOT |»+⟩ - qa", ZERO, PLUS, ZERO, qa);
		assertXYZ("CNOT |»+⟩ - qb", PLUS, ZERO, ZERO, qb);
		
		// test 3 - cnot on |«+⟩ should give |«+⟩
		qa.not();
		qb.cnot(qa);
		assertXYZ("CNOT |«+⟩ - qa", ZERO, MINUS, ZERO, qa);
		assertXYZ("CNOT |«+⟩ - qb", PLUS, ZERO, ZERO, qb);
	}

	/**
	 * Asserts that the CNOT gate works as expected
	 * when the state of the target qubit is |-⟩.
	 * <p>
	 * The CNOT gate should work exactly the same as
	 * the CX gate.
	 */
	@Test
	public final void testTargetMinus() {
		// test 0 - cnot on |0-⟩ should give |0-⟩
		qb.h().z();
		qb.cnot(qa);
		assertXYZ("CNOT |0-⟩ - qa", ZERO, ZERO, PLUS, qa);
		assertXYZ("CNOT |0-⟩ - qb", MINUS, ZERO, ZERO, qb);
		
		// test 1 - cnot on |1-⟩ should give |1-⟩
		qa.not();
		qb.cnot(qa);
		assertXYZ("CNOT |1-⟩ - qa", ZERO, ZERO, MINUS, qa);
		assertXYZ("CNOT |1-⟩ - qb", MINUS, ZERO, ZERO, qb);
		
		// test 2 - cnot on |»-⟩ should give |«-⟩
		qa.rx(Math.PI/2);
		qb.cnot(qa);
		assertXYZ("CNOT |»-⟩ - qa", ZERO, MINUS, ZERO, qa);
		assertXYZ("CNOT |»-⟩ - qb", MINUS, ZERO, ZERO, qb);
		
		// test 3 - cnot on |«-⟩ should give |»-⟩
		qb.cnot(qa);
		assertXYZ("CNOT |«-⟩ - qa", ZERO, PLUS, ZERO, qa);
		assertXYZ("CNOT |«-⟩ - qb", MINUS, ZERO, ZERO, qb);
	}

}
