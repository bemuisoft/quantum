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
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.function.Consumer;

import org.bemuisoft.quantum.api.Axis;
import org.bemuisoft.quantum.api.IQubit;
import org.bemuisoft.quantum.api.IQubitFactory;
import org.junit.jupiter.api.Test;

/**
 * Tests multi-controlled gates of the {@link IQubit} interface
 * and their reversibility in a two-qubit system.
 * <p>
 * To get predictable measurement results,
 * the controlled gates are applied repeatedly
 * to entangle and untangle the qubits before
 * asserting expected results.
 * 
 * @param <Q>	the type of qubit to be tested
 * 
 * @author Benno Muilwijk
 */
public class MultiControlledGateTest<Q extends IQubit> extends AbstractQubitTest<Q> {

	/** Qubit A. */
	protected Q qa;
	/** Qubit B. */
	protected Q qb;
	/** Qubit C. */
	protected Q qc;

	/**
	 * Default constructor.
	 */
	public MultiControlledGateTest() {
		super(3);
	}

	@Override
	public final MultiControlledGateTest<Q> init(IQubitFactory<Q> factory) {
		super.init(factory);
		qa = q(0);
		qb = q(1);
		qc = q(2);
		return this;
	}

	@Override
	public void runAll() {
		assertAll(getHeading(),
			run(() -> testCCXGate()),
			run(() -> testCCZGate()),
			run(() -> testCCGate()),
			run(() -> testCCPGate()),
			run(() -> testCCRGate()),
			run(() -> testCCUGate())
		);
	}

	/**
	 * Asserts that the CCX gate is reversible.
	 * <p>
	 * Qubits A and B are control qubits and C is target.
	 */
	@Test
	public void testCCXGate() {
		// N.B. in these comments, theta refers to
		// the argument of the initial RY gate
		// that the repeat method applies before
		// repeating the controlled gate.
		// For example, RY(-pi/n) on |0⟩ gives a
		// state with theta == pi/n and phase == pi.
		
		// initialize control qubits to |1⟩ for better probability distribution
		qa.x();
		qb.x();
		
		// test 0 - qa.theta = pi/3, qb.theta = pi/3, qc.theta = pi/3
		repeat(PI/3, PI/3, PI/3, 2, (pi) -> qc.ccx(qa, qb));
		assertIsEqual("Test 0 - qa", 1, qa.measure());
		assertIsEqual("Test 0 - qb", 1, qb.measure());
		assertIsEqual("Test 0 - qc", 0, qc.measure());
		
		// test 1 - qa.theta = pi/4, qb.theta = -pi/4, qc.theta = pi/3
		repeat(PI/4, -PI/4, PI/3, 2, (pi) -> qc.ccx(qa, qb));
		assertIsEqual("Test 1 - qa", 1, qa.measure());
		assertIsEqual("Test 1 - qb", 1, qb.measure());
		assertIsEqual("Test 1 - qc", 0, qc.measure());
		
		// test 2 - qa.theta = -pi/6, qb.theta = pi/6, qc.theta = -pi/4
		repeat(-PI/6, PI/6, -PI/4, 2, (pi) -> qc.ccx(qa, qb));
		assertIsEqual("Test 2 - qa", 1, qa.measure());
		assertIsEqual("Test 2 - qb", 1, qb.measure());
		assertIsEqual("Test 2 - qc", 0, qc.measure());
		
		// test 3 - qa.theta = -pi/2, qb.theta = -pi/2, qc.theta = pi/6
		repeat(-PI/2, -PI/2, PI/6, 2, (pi) -> qc.ccx(qa, qb));
		assertIsEqual("Test 3 - qa", 1, qa.measure());
		assertIsEqual("Test 3 - qb", 1, qb.measure());
		assertIsEqual("Test 3 - qc", 0, qc.measure());
		
		// test 4 - qa.theta = pi/2, qb.theta = pi/4, qc.theta = -pi/2
		repeat(PI/2, PI/4, -PI/2, 2, (pi) -> qc.ccx(qa, qb));
		assertIsEqual("Test 4 - qa", 1, qa.measure());
		assertIsEqual("Test 4 - qb", 1, qb.measure());
		assertIsEqual("Test 4 - qc", 0, qc.measure());
		
		// reset control qubits to |0⟩ for the following tests
		qa.x();
		qb.x();
		
		// test 5 - qa.state = |+⟩, qb.state = |1⟩, qc.state = |0⟩
		repeat(PI/2, PI, ZERO, 2, (pi) -> qc.ccx(qa, qb));
		assertIsEqual("Test 5 - qa", 0, qa.measure());
		assertIsEqual("Test 5 - qb", 0, qb.measure());
		assertIsEqual("Test 5 - qc", 0, qc.measure());
		
		// test 6 - qa.state = |1⟩, qb.state = |-⟩, qc.state = |0⟩
		repeat(PI, -PI/2, ZERO, 2, (pi) -> qc.ccx(qa, qb));
		assertIsEqual("Test 6 - qa", 0, qa.measure());
		assertIsEqual("Test 6 - qb", 0, qb.measure());
		assertIsEqual("Test 6 - qc", 0, qc.measure());
		
		// test 7 - qa.state = |1⟩, qb.state = |+⟩, qc.state = |1⟩
		repeat(PI, PI/2, PI, 2, (pi) -> qc.ccx(qa, qb));
		assertIsEqual("Test 7 - qa", 0, qa.measure());
		assertIsEqual("Test 7 - qb", 0, qb.measure());
		assertIsEqual("Test 7 - qc", 0, qc.measure());
		
		// test 8 - qa.state = |-⟩, qb.state = |1⟩, qc.state = |1⟩
		repeat(-PI/2, PI, PI, 2, (pi) -> qc.ccx(qa, qb));
		assertIsEqual("Test 8 - qa", 0, qa.measure());
		assertIsEqual("Test 8 - qb", 0, qb.measure());
		assertIsEqual("Test 8 - qc", 0, qc.measure());
	}

	/**
	 * Asserts that the CCZ gate is reversible.
	 */
	@Test
	public void testCCZGate() {
		// initialize control qubits to |1⟩ for better probability distribution
		qa.x();
		qb.x();
		
		// test 0 - qa.theta = pi/3, qb.theta = pi/3, qc.theta = pi/3
		repeat(PI/3, PI/3, PI/3, 2, (pi) -> qc.ccz(qa, qb));
		assertIsEqual("Test 0 - qa", 1, qa.measure());
		assertIsEqual("Test 0 - qb", 1, qb.measure());
		assertIsEqual("Test 0 - qc", 0, qc.measure());
		
		// test 1 - qa.theta = pi/4, qb.theta = -pi/4, qc.theta = pi/3
		repeat(PI/4, -PI/4, PI/3, 2, (pi) -> qc.ccz(qa, qb));
		assertIsEqual("Test 1 - qa", 1, qa.measure());
		assertIsEqual("Test 1 - qb", 1, qb.measure());
		assertIsEqual("Test 1 - qc", 0, qc.measure());
		
		// test 2 - qa.theta = -pi/6, qb.theta = pi/6, qc.theta = -pi/4
		repeat(-PI/6, PI/6, -PI/4, 2, (pi) -> qc.ccz(qa, qb));
		assertIsEqual("Test 2 - qa", 1, qa.measure());
		assertIsEqual("Test 2 - qb", 1, qb.measure());
		assertIsEqual("Test 2 - qc", 0, qc.measure());
		
		// test 3 - qa.theta = -pi/2, qb.theta = -pi/4, qc.theta = pi/6
		repeat(-PI/2, -PI/4, PI/6, 2, (pi) -> qc.ccz(qa, qb));
		assertIsEqual("Test 3 - qa", 1, qa.measure());
		assertIsEqual("Test 3 - qb", 1, qb.measure());
		assertIsEqual("Test 3 - qc", 0, qc.measure());
		
		// reset control qubits to |0⟩ for the following tests
		qa.x();
		qb.x();
		
		// test 4 - qa.state = |+⟩, qb.state = |1⟩, qc.state = |+⟩
		repeat(PI/2, PI, PI/2, 2, (pi) -> qc.ccz(qa, qb));
		assertIsEqual("Test 4 - qa", 0, qa.measure());
		assertIsEqual("Test 4 - qb", 0, qb.measure());
		assertIsEqual("Test 4 - qc", 0, qc.measure());
		
		// test 5 - qa.state = |1⟩, qb.state = |-⟩, qc.state = |-⟩
		repeat(PI, -PI/2, -PI/2, 2, (pi) -> qc.ccz(qa, qb));
		assertIsEqual("Test 5 - qa", 0, qa.measure());
		assertIsEqual("Test 5 - qb", 0, qb.measure());
		assertIsEqual("Test 5 - qc", 0, qc.measure());
		
		// test 6 - qa.state = |1⟩, qb.state = |+⟩, qc.state = |+⟩
		repeat(PI, PI/2, PI/2, 2, (pi) -> qc.ccz(qa, qb));
		assertIsEqual("Test 6 - qa", 0, qa.measure());
		assertIsEqual("Test 6 - qb", 0, qb.measure());
		assertIsEqual("Test 6 - qc", 0, qc.measure());
		
		// test 7 - qa.state = |-⟩, qb.state = |1⟩, qc.state = |-⟩
		repeat(-PI/2, PI, -PI/2, 2, (pi) -> qc.ccz(qa, qb));
		assertIsEqual("Test 7 - qa", 0, qa.measure());
		assertIsEqual("Test 7 - qb", 0, qb.measure());
		assertIsEqual("Test 7 - qc", 0, qc.measure());
	}

	/**
	 * Asserts that the multi-C gate (for any axis) is reversible.
	 * <p>
	 * Qubits A and B are control qubits and C is target.
	 */
	@Test
	public void testCCGate() {
		assumeTrue(isSupported(() -> qc.c(Axis.Z, qa, qb)), "Multi-C gate is not supported.");
		
		// test 0 - qa.state = |+⟩, qb.state = |1⟩, qc.state = |0⟩			Controlled controlled X
		repeat(PI/2, PI, ZERO, 2, (pi) -> qc.c(Axis.X, qa, qb));
		assertIsEqual("Test 0 - qa", 0, qa.measure());
		assertIsEqual("Test 0 - qb", 0, qb.measure());
		assertIsEqual("Test 0 - qc", 0, qc.measure());
		
		// test 1 - qa.state = |1⟩, qb.state = |-⟩, qc.state = |0⟩			Controlled controlled Y
		repeat(PI, -PI/2, ZERO, 2, (pi) -> qc.c(Axis.Y, qa, qb));
		assertIsEqual("Test 1 - qa", 0, qa.measure());
		assertIsEqual("Test 1 - qb", 0, qb.measure());
		assertIsEqual("Test 1 - qc", 0, qc.measure());
		
		// test 2 - qa.state = |1⟩, qb.state = |+⟩, qc.state = |-⟩			Controlled controlled Z
		repeat(PI, PI/2, -PI/2, 2, (pi) -> qc.c(Axis.Z, qa, qb));
		assertIsEqual("Test 2 - qa", 0, qa.measure());
		assertIsEqual("Test 2 - qb", 0, qb.measure());
		assertIsEqual("Test 2 - qc", 0, qc.measure());
		
		// test 3 - qa.state = |-⟩, qb.state = |1⟩, qc.theta = -pi/4			Controlled controlled H
		repeat(-PI/2, PI, -PI/4, 2, (pi) -> qc.c(Axis.H, qa, qb));
		assertIsEqual("Test 3 - qa", 0, qa.measure());
		assertIsEqual("Test 3 - qb", 0, qb.measure());
		assertIsEqual("Test 3 - qc", 0, qc.measure());
	}

	/**
	 * Asserts that the CCP gate gives identical state
	 * if the sum of controlled rotations is 2pi.
	 */
	@Test
	public void testCCPGate() {
		// initialize control qubits to |1⟩ for better probability distribution
		qa.x();
		qb.x();
		
		// test 0 - qa.theta = pi/3, qb.theta = pi/3, qc.theta = pi/3		Controlled controlled P(2pi)
		repeat(PI/3, PI/3, PI/3, 1, (rad) -> qc.ccp(rad, qa, qb));
		assertIsEqual("Test 0 - qa", 1, qa.measure());
		assertIsEqual("Test 0 - qb", 1, qb.measure());
		assertIsEqual("Test 0 - qc", 0, qc.measure());
		
		// test 1 - qa.theta = pi/4, qb.theta = -pi/4, qc.theta = pi/3		Controlled controlled P(pi)
		repeat(PI/4, -PI/4, PI/3, 2, (rad) -> qc.ccp(rad, qa, qb));
		assertIsEqual("Test 1 - qa", 1, qa.measure());
		assertIsEqual("Test 1 - qb", 1, qb.measure());
		assertIsEqual("Test 1 - qc", 0, qc.measure());
		
		// test 2 - qa.theta = -pi/6, qb.theta = pi/6, qc.theta = -pi/4		Controlled controlled P(2pi/3)
		repeat(-PI/6, PI/6, -PI/4, 3, (rad) -> qc.ccp(rad, qa, qb));
		assertIsEqual("Test 2 - qa", 1, qa.measure());
		assertIsEqual("Test 2 - qb", 1, qb.measure());
		assertIsEqual("Test 2 - qc", 0, qc.measure());
		
		// test 3 - qa.theta = -pi/2, qb.theta = -pi/4, qc.theta = pi/6		Controlled controlled P(pi/2)
		repeat(-PI/2, -PI/4, PI/6, 4, (rad) -> qc.ccp(rad, qa, qb));
		assertIsEqual("Test 3 - qa", 1, qa.measure());
		assertIsEqual("Test 3 - qb", 1, qb.measure());
		assertIsEqual("Test 3 - qc", 0, qc.measure());
		
		// reset control qubits to |0⟩ for the following tests
		qa.x();
		qb.x();
		
		// test 4 - qa.state = |+⟩, qb.state = |1⟩, qc.state = |+⟩			Controlled controlled P(2pi)
		repeat(PI/2, PI, PI/2, 1, (rad) -> qc.ccp(rad, qa, qb));
		assertIsEqual("Test 4 - qa", 0, qa.measure());
		assertIsEqual("Test 4 - qb", 0, qb.measure());
		assertIsEqual("Test 4 - qc", 0, qc.measure());
		
		// test 5 - qa.state = |1⟩, qb.state = |-⟩, qc.state = |-⟩			Controlled controlled P(pi)
		repeat(PI, -PI/2, -PI/2, 2, (rad) -> qc.ccp(rad, qa, qb));
		assertIsEqual("Test 5 - qa", 0, qa.measure());
		assertIsEqual("Test 5 - qb", 0, qb.measure());
		assertIsEqual("Test 5 - qc", 0, qc.measure());
		
		// test 6 - qa.state = |1⟩, qb.state = |+⟩, qc.state = |+⟩			Controlled controlled P(2pi/3)
		repeat(PI, PI/2, PI/2, 3, (rad) -> qc.ccp(rad, qa, qb));
		assertIsEqual("Test 6 - qa", 0, qa.measure());
		assertIsEqual("Test 6 - qb", 0, qb.measure());
		assertIsEqual("Test 6 - qc", 0, qc.measure());
		
		// test 7 - qa.state = |-⟩, qb.state = |1⟩, qc.state = |-⟩			Controlled controlled P(pi/2)
		repeat(-PI/2, PI, -PI/2, 4, (rad) -> qc.ccp(rad, qa, qb));
		assertIsEqual("Test 7 - qa", 0, qa.measure());
		assertIsEqual("Test 7 - qb", 0, qb.measure());
		assertIsEqual("Test 7 - qc", 0, qc.measure());
	}

	/**
	 * Asserts that the multi-CR gate gives identical state
	 * for the target qubit, but not for the control qubits,
	 * if the sum of controlled rotations is 2pi.
	 * <p>
	 * The multi-controlled RX, RY and RZ gates, and more
	 * in general the multi-controlled R(axis, angle) gate,
	 * have in common that the control qubits get
	 * an extra controlled phase shift of -angle/2.
	 * So, when the sum of the controlled rotation
	 * angles equals 2pi, the control qubits are
	 * entangled as if a (multi-)CZ was applied to them.
	 */
	@Test
	public void testCCRGate() {
		assumeTrue(isSupported(() -> qc.cr(Axis.Z, TWO_PI, qa, qb)), "Multi-CR gate is not supported.");
		
		// test 0 - qa.state = |+⟩, qb.state = |1⟩, qc.theta = pi/3				Controlled controlled RX(2pi)
		repeat(PI/2, PI, PI/3, 1, (rad) -> qc.cr(Axis.X, rad, qa, qb));
		assertIsEqual("Test 0 - qa", 1, qa.measure());							// expect |1⟩
		assertIsEqual("Test 0 - qb", 0, qb.measure());
		assertIsEqual("Test 0 - qc", 0, qc.measure());
		
		// test 1 - qa.state = |-⟩, qb.state = |-⟩, qc.theta = pi/3				Controlled controlled RY(pi)
		// note: qa starts from |1⟩ and ry-pi/2) on |1⟩ gives |-⟩
		repeat(PI/2, PI, PI/3, 2, (rad) -> qc.cr(Axis.Y, rad, qa, qb));
		assertIsEqual("Test 1 - qa", 0, qa.measure());							// expect |0⟩
		assertIsEqual("Test 1 - qb", 0, qb.measure());
		assertIsEqual("Test 1 - qc", 0, qc.measure());
		
		// test 2 - qa.state = |1⟩, qb.state = |+⟩, qc.theta = pi/3				Controlled controlled RZ(2pi/3)
		repeat(PI, PI/2, PI/3, 3, (rad) -> qc.cr(Axis.Z, rad, qa, qb));
		// note: qa starts from |1⟩ and ry(-pi/2) on |1⟩ gives |+⟩
		assertIsEqual("Test 2 - qa", 0, qa.measure());
		assertIsEqual("Test 2 - qb", 1, qb.measure());							// expect |1⟩
		assertIsEqual("Test 2 - qc", 0, qc.measure());
		
		// test 3 - qa.state = |-⟩, qb.state = |-⟩, qc.theta = pi/3				Controlled controlled RH(pi/2)
		repeat(PI, -PI/2, PI/3, 4, (rad) -> qc.cr(Axis.H, rad, qa, qb));
		assertIsEqual("Test 3 - qa", 0, qa.measure());
		assertIsEqual("Test 3 - qb", 0, qb.measure());							// expect |0⟩
		assertIsEqual("Test 3 - qc", 0, qc.measure());
	}

	/**
	 * Asserts that the multi-CU gate gives identical state for
	 * the target qubit, but usually not for the control qubits,
	 * if the sum of controlled rotations is 2pi.
	 * <p>
	 * Like U(theta, phi, lambda) is identical to
	 * matrix multiplication P(phi)RY(theta)P(lambda),
	 * The controlled version CU(theta, phi, lambda)
	 * is identical to CP(phi)CRY(theta)CP(lambda).
	 * <p>
	 * As demonstrated in {@link #testCCPGate()} and
	 * {@link #testCCRGate()}, only CRY(theta) will cause
	 * an extra controlled phase shift of -theta/2 on the
	 * control qubits.
	 * So, when the sum of the controlled rotation
	 * angles equals 2pi, the control qubits are
	 * entangled as if a (multi-)CZ was applied to them.
	 */
	@Test
	public void testCCUGate() {
		assumeTrue(isSupported(() -> qc.cu(ZERO, ZERO, ZERO, qa, qb)), "Multi-CU gate is not supported.");
		
		// initialize control qubits to |1⟩ for better probability distribution
		qa.x();
		qb.x();
		
		// test 0 - qa.theta = pi/3, qb.theta = pi/3, qc.theta = pi/3				Controlled controlled P(2pi)
		repeat(PI/3, PI/3, PI/3, 1, (phi) -> qc.cu(ZERO, phi, ZERO, qa, qb));
		assertIsEqual("Test 0 - qa", 1, qa.measure());
		assertIsEqual("Test 0 - qb", 1, qb.measure());
		assertIsEqual("Test 0 - qc", 0, qc.measure());
		
		// test 1 - qa.theta = pi/4, qb.theta = -pi/4, qc.theta = pi/3				Controlled controlled P(pi)
		repeat(PI/4, -PI/4, PI/3, 2, (phi) -> qc.cu(ZERO, phi, ZERO, qa, qb));
		assertIsEqual("Test 1 - qa", 1, qa.measure());
		assertIsEqual("Test 1 - qb", 1, qb.measure());
		assertIsEqual("Test 1 - qc", 0, qc.measure());
		
		// test 2 - qa.theta = -pi/6, qb.theta = pi/6, qc.theta = -pi/4				Controlled controlled P(2pi/3)
		repeat(-PI/6, PI/6, -PI/4, 3, (lambda) -> qc.cu(ZERO, ZERO, lambda, qa, qb));
		assertIsEqual("Test 2 - qa", 1, qa.measure());
		assertIsEqual("Test 2 - qb", 1, qb.measure());
		assertIsEqual("Test 2 - qc", 0, qc.measure());
		
		// test 3 - qa.theta = -pi/2, qb.theta = -pi/4, qc.theta = pi/6				Controlled controlled P(pi/2)
		repeat(-PI/2, -PI/4, PI/6, 4, (lambda) -> qc.cu(ZERO, ZERO, lambda, qa, qb));
		assertIsEqual("Test 3 - qa", 1, qa.measure());
		assertIsEqual("Test 3 - qb", 1, qb.measure());
		assertIsEqual("Test 3 - qc", 0, qc.measure());
		
		// reset control qubits to |0⟩ for the following tests
		qa.x();
		qb.x();
		
		// test 4 - qa.state = |+⟩, qb.state = |1⟩, qc.theta = pi/3					Controlled controlled RY(2pi)
		repeat(PI/2, PI, PI/3, 1, (theta) -> qc.cu(theta, ZERO, ZERO, qa, qb));
		assertIsEqual("Test 4 - qa", 1, qa.measure());								// expect |1⟩ (change)
		assertIsEqual("Test 4 - qb", 0, qb.measure());								// expect |0⟩ (no change because of state |1⟩ into CU)
		assertIsEqual("Test 4 - qc", 0, qc.measure());
		
		// test 5 - qa.state = |1⟩, qb.state = |-⟩, qc.theta = pi/3					Controlled controlled H
		// note: qa starts from |1⟩ (from previous assert)
		repeat(ZERO, -PI/2, PI/3, 2, (pi) -> qc.cu(pi/2, ZERO, pi, qa, qb));
		// lambda+phi equals pi, this reverses the effect of theta, so no net phase change after two times
		assertIsEqual("Test 5 - qa", 1, qa.measure());								// expect |1⟩ (no change)
		assertIsEqual("Test 5 - qb", 0, qb.measure());								// expect |0⟩ (no change)
		assertIsEqual("Test 5 - qc", 0, qc.measure());
		
		// test 6 - qa.state = |1⟩, qb.state = |+⟩, qc.theta = pi/3					Controlled controlled RX(2pi/3)
		// note: qa starts from |1⟩ (from previous assert)
		repeat(ZERO, PI/2, PI/3, 3, (theta) -> qc.cu(theta, -PI/2, PI/2, qa, qb));
		// lambda+phi equals 0, so they cancel each other on consecutive calls, so net phase change for CRX as for CRY
		assertIsEqual("Test 6 - qa", 1, qa.measure());								// expect |1⟩ (no change because of state |1⟩ into CU)
		assertIsEqual("Test 6 - qb", 1, qb.measure());								// expect |1⟩ (change)
		assertIsEqual("Test 6 - qc", 0, qc.measure());
		
		// test 7 - qa.state = |-⟩, qb.state = |1⟩, qc.theta = pi/3					Controlled controlled RY(pi/2)
		// note: both qa and qb start from |1⟩ (from previous assert)
		repeat(PI/2, ZERO, PI/3, 4, (theta) -> qc.cu(theta, ZERO, ZERO, qa, qb));
		assertIsEqual("Test 7 - qa", 0, qa.measure());								// expect |0⟩ (change)
		assertIsEqual("Test 7 - qb", 1, qb.measure());								// expect |1⟩ (no change because of state |1⟩ into CU)
		assertIsEqual("Test 7 - qc", 0, qc.measure());
	}

	/**
	 * Performs the given operation for the specified number of times.
	 * <p>
	 * Before starting the loop, qubits A and B are prepared by an
	 * RY operation with the arguments {@code initA} and {@code initB}.
	 * These operations are reversed after the loop.
	 * <p>
	 * The argument passed to the repeated operation is always
	 * 2pi divided by {@code steps}.
	 * 
	 * @param initA		argument for initial RY operation on qubit A
	 * @param initB		argument for initial RY operation on qubit B
	 * @param initC		argument for initial RY operation on qubit C
	 * @param steps		number of times to perform the given operation
	 * @param operation	the operation to perform repeatedly
	 */
	protected void repeat(double initA, double initB, double initC, int steps, Consumer<Double> operation) {
		// prepare both qubits
		qa.ry(initA);
		qb.ry(initB);
		qc.ry(initC);
		
		// repeat the operation
		double stepSize = TWO_PI / steps;
		for (int step = 0; step < steps; step++) {
			operation.accept(stepSize);
		}
		
		// reverse preparation
		qc.ry(-initC);
		qb.ry(-initB);
		qa.ry(-initA);
	}

}
