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
 * Tests the {@link IQubit} interface in a two-qubit system.
 * 
 * @param <Q>	the type of qubit to be tested
 * 
 * @author Benno Muilwijk
 */
public class TwoQubitTest<Q extends IQubit> extends AbstractQubitTest<Q> {

	/** Qubit A. */
	protected Q qa;
	/** Qubit B. */
	protected Q qb;

	/**
	 * Default constructor.
	 */
	public TwoQubitTest() {
		super(2);
	}

	@Override
	public final TwoQubitTest<Q> init(IQubitFactory<Q> factory) {
		super.init(factory);
		qa = q(0);
		qb = q(1);
		return this;
	}

	@Override
	public void runAll() {
		assertAll(getHeading(),
			run(() -> testInitialState()),
			run(() -> testBellPhiPlus()),
			run(() -> testBellPhiMinus()),
			run(() -> testBellPsiPlus()),
			run(() -> testBellPsiMinus()),
			run(() -> testSwap())
		);
	}

	/**
	 * Asserts that the initial state is |00⟩.
	 */
	@Test
	public void testInitialState() {
		// initial state should be |00⟩
		assertIsEqual("Initial state - qa", 0, qa.measure());
		assertIsEqual("Initial state - qb", 0, qb.measure());
	}

	/**
	 * Asserts that Bell state |Phi+⟩ gives the expected correlations,
	 * also when the Hadamard gate is applied to both qubits.
	 * 
	 * @see <a href="https://en.wikipedia.org/wiki/Bell_state">Bell state</a>
	 */
	@RepeatedTest(10)
	public final void testBellPhiPlus() {
		int corr;	// correlation, +1 = even, -1 = odd
		// test 0 - cnot on |+0⟩ should give |Φ+⟩ (even parity, in phase)
		qa.h();
		qb.cnot(qa);
		corr = qa.measureSign() * qb.measureSign();
		assertIsEqual("CNOT |+0⟩ -> |Φ+⟩ correlation", +1, corr);
		
		// test 1 - h⊗h on |Φ+⟩ should give |Φ+⟩ (even parity, in phase)
		qa.reset().h();
		qb.reset().cnot(qa);
		qa.h();
		qb.h();
		corr = qa.measureSign() * qb.measureSign();
		assertIsEqual("h⊗h |Φ+⟩ -> |Φ+⟩ correlation", +1, corr);
		
		// test 2 - cnot on |Φ+⟩ should give |+0⟩
		qa.reset().h();
		qb.reset().cnot(qa);
		qa.h();
		qb.h();
		qb.cnot(qa);
		assertIsEqual("CNOT |Φ+⟩ -> |+0⟩ qb", 0, qb.measure());
		qa.h();
		assertIsEqual("CNOT |Φ+⟩ -> |+0⟩ qa", +1, qa.measureSign());
	}

	/**
	 * Asserts that Bell state |Phi-⟩ gives the expected correlations,
	 * also when the Hadamard gate is applied to both qubits.
	 * 
	 * @see <a href="https://en.wikipedia.org/wiki/Bell_state">Bell state</a>
	 */
	@RepeatedTest(10)
	public final void testBellPhiMinus() {
		int corr;	// correlation, +1 = even, -1 = odd
		// test 0 - cnot on |-0⟩ should give |Φ-⟩ (even parity, contra phase)
		qa.not().h();
		qb.cnot(qa);
		corr = qa.measureSign() * qb.measureSign();
		assertIsEqual("CNOT |-0⟩ -> |Φ-⟩ correlation", +1, corr);
		
		// test 1 - h⊗h on |Φ-⟩ should give |Ψ+⟩ (odd parity, in phase)
		qa.reset().not().h();
		qb.reset().cnot(qa);
		qa.h();
		qb.h();
		corr = qa.measureSign() * qb.measureSign();
		assertIsEqual("h⊗h |Φ-⟩ -> |Ψ+⟩ correlation", -1, corr);
		
		// test 2 - cnot on |Ψ+⟩ should give |+1⟩
		qa.reset().not().h();
		qb.reset().cnot(qa);
		qa.h();
		qb.h();
		qb.cnot(qa);
		assertIsEqual("CNOT |Ψ+⟩ -> |+1⟩ qb", 1, qb.measure());
		qa.h();
		assertIsEqual("CNOT |Ψ+⟩ -> |+1⟩ qa", +1, qa.measureSign());
	}

	/**
	 * Asserts that Bell state |Psi+⟩ gives the expected correlations,
	 * also when the Hadamard gate is applied to both qubits.
	 * 
	 * @see <a href="https://en.wikipedia.org/wiki/Bell_state">Bell state</a>
	 */
	@RepeatedTest(10)
	public final void testBellPsiPlus() {
		int corr;	// correlation, +1 = even, -1 = odd
		// test 0 - cnot on |+1⟩ should give |Ψ+⟩ (odd parity, in phase)
		qa.h();
		qb.not();
		qb.cnot(qa);
		corr = qa.measureSign() * qb.measureSign();
		assertIsEqual("CNOT |+1⟩ -> |Ψ+⟩ correlation", -1, corr);
		
		// test 1 - h⊗h on |Ψ+⟩ should give |Φ-⟩ (even parity, contra phase)
		qa.reset().h();
		qb.reset().not();
		qb.cnot(qa);
		qa.h();
		qb.h();
		corr = qa.measureSign() * qb.measureSign();
		assertIsEqual("h⊗h |Ψ+⟩ -> |Φ-⟩ correlation", +1, corr);
		
		// test 2 - cnot on |Φ-⟩ should give |-0⟩
		qa.reset().h();
		qb.reset().not();
		qb.cnot(qa);
		qa.h();
		qb.h();
		qb.cnot(qa);
		assertIsEqual("CNOT |Φ-⟩ -> |-0⟩ qb", 0, qb.measure());
		qa.h();
		assertIsEqual("CNOT |Φ-⟩ -> |-0⟩ qa", -1, qa.measureSign());
	}

	/**
	 * Asserts that Bell state |Psi-⟩ gives the expected correlations,
	 * also when the Hadamard gate is applied to both qubits.
	 * 
	 * @see <a href="https://en.wikipedia.org/wiki/Bell_state">Bell state</a>
	 */
	@RepeatedTest(10)
	public final void testBellPsiMinus() {
		int corr;	// correlation, +1 = even, -1 = odd
		// test 0 - cnot on |-1⟩ should give |Ψ-⟩ (odd parity, contra phase)
		qa.not().h();
		qb.not();
		qb.cnot(qa);
		corr = qa.measureSign() * qb.measureSign();
		assertIsEqual("CNOT |-1⟩ -> |Ψ-⟩ correlation", -1, corr);
		
		// test 1 - h⊗h on |Ψ-⟩ should give |Ψ-⟩ (odd parity, contra phase)
		qa.reset().not().h();
		qb.reset().not().cnot(qa);
		qa.h();
		qb.h();
		corr = qa.measureSign() * qb.measureSign();
		assertIsEqual("h⊗h |Ψ-⟩ -> |Ψ-⟩ correlation", -1, corr);
		
		// test 2 - cnot on |Ψ-⟩ should give |-1⟩
		qa.reset().not().h();
		qb.reset().not().cnot(qa);
		qa.h();
		qb.h();
		qb.cnot(qa);
		assertIsEqual("CNOT |Ψ-⟩ -> |-1⟩ qb", 1, qb.measure());
		qa.h();
		assertIsEqual("CNOT |Ψ-⟩ -> |-1⟩ qa", -1, qa.measureSign());
	}

	/**
	 * Asserts that the SWAP gate works as expected.
	 */
	@Test
	public final void testSwap() {
		// test 0 - swap on |00⟩ should give |00⟩
		qb.swap(qa);
		assertIsEqual("SWAP |00⟩ -> |00⟩ qa", 0, qa.measure());
		assertIsEqual("SWAP |00⟩ -> |00⟩ qb", 0, qb.measure());
		
		// test 1 - swap on |01⟩ should give |10⟩
		qb.not();
		qb.swap(qa);
		assertIsEqual("SWAP |01⟩ -> |10⟩ qa", 1, qa.measure());
		assertIsEqual("SWAP |01⟩ -> |10⟩ qb", 0, qb.measure());
		
		// test 2 - swap on |10⟩ should give |01⟩
		qb.swap(qa);
		assertIsEqual("SWAP |10⟩ -> |01⟩ qa", 0, qa.measure());
		assertIsEqual("SWAP |10⟩ -> |01⟩ qb", 1, qb.measure());
		
		// test 3 - swap on |11⟩ should give |11⟩
		qa.not();
		qb.swap(qa);
		assertIsEqual("SWAP |11⟩ -> |11⟩ qa", 1, qa.measure());
		assertIsEqual("SWAP |11⟩ -> |11⟩ qb", 1, qb.measure());
	}

}
