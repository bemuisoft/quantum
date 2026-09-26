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

import java.util.function.Consumer;

import org.bemuisoft.quantum.api.Axis;
import org.bemuisoft.quantum.api.IQubit;
import org.bemuisoft.quantum.api.IQubitFactory;
import org.junit.jupiter.api.Test;

/**
 * Tests controlled gates of the {@link IQubit} interface
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
public class ControlledGateTest<Q extends IQubit> extends AbstractQubitTest<Q> {

	/** Qubit A. */
	protected Q qa;
	/** Qubit B. */
	protected Q qb;

	/**
	 * Default constructor.
	 */
	public ControlledGateTest() {
		super(2);
	}

	@Override
	public final ControlledGateTest<Q> init(IQubitFactory<Q> factory) {
		super.init(factory);
		qa = q(0);
		qb = q(1);
		return this;
	}

	@Override
	public void runAll() {
		assertAll(getHeading(),
			run(() -> testCXGate()),
			run(() -> testCZGate()),
			run(() -> testCGate()),
			run(() -> testCPGate()),
			run(() -> testCRGate()),
			run(() -> testCUGate())
		);
	}

	/**
	 * Asserts that the CX gate is reversible.
	 * <p>
	 * Qubit A is control qubit and B is target.
	 */
	@Test
	public void testCXGate() {
		// N.B. in these comments, theta refers to
		// the argument of the initial RY gate
		// that the repeat method applies before
		// repeating the controlled gate.
		// For example, RY(-pi/n) on |0⟩ gives a
		// state with theta == pi/n and phase == pi.
		
		// test 0 - qa.theta = pi/3, qb.theta = pi/3
		repeat(PI/3, PI/3, 2, (pi) -> qb.cx(qa));
		assertIsEqual("Test 0 - qa", 0, qa.measure());
		assertIsEqual("Test 0 - qb", 0, qb.measure());
		
		// test 1 - qa.theta = pi/4, qb.theta = -pi/4
		repeat(PI/4, -PI/4, 2, (pi) -> qb.cx(qa));
		assertIsEqual("Test 1 - qa", 0, qa.measure());
		assertIsEqual("Test 1 - qb", 0, qb.measure());
		
		// test 2 - qa.theta = -pi/6, qb.theta = pi/6
		repeat(-PI/6, PI/6, 2, (pi) -> qb.cx(qa));
		assertIsEqual("Test 2 - qa", 0, qa.measure());
		assertIsEqual("Test 2 - qb", 0, qb.measure());
		
		// test 3 - qa.theta = -pi/2, qb.theta = -pi/2
		repeat(-PI/2, -PI/2, 2, (pi) -> qb.cx(qa));
		assertIsEqual("Test 3 - qa", 0, qa.measure());
		assertIsEqual("Test 3 - qb", 0, qb.measure());
		
		// test 4 - qa.theta = pi/2, qb.theta = pi/4
		repeat(PI/2, PI/4, 2, (pi) -> qb.cx(qa));
		assertIsEqual("Test 4 - qa", 0, qa.measure());
		assertIsEqual("Test 4 - qb", 0, qb.measure());
		
		// test 5 - qa.state = |+⟩, qb.state = |0⟩		(Bell state |Phi+⟩ and back)
		repeat(PI/2, ZERO, 2, (pi) -> qb.cx(qa));
		assertIsEqual("Test 5 - qa", 0, qa.measure());
		assertIsEqual("Test 5 - qb", 0, qb.measure());
		
		// test 6 - qa.state = |-⟩, qb.state = |0⟩		(Bell state |Phi-⟩ and back)
		repeat(-PI/2, ZERO, 2, (pi) -> qb.cx(qa));
		assertIsEqual("Test 6 - qa", 0, qa.measure());
		assertIsEqual("Test 6 - qb", 0, qb.measure());
		
		// test 7 - qa.state = |+⟩, qb.state = |1⟩		(Bell state |Psi+⟩ and back)
		repeat(PI/2, PI, 2, (pi) -> qb.cx(qa));
		assertIsEqual("Test 7 - qa", 0, qa.measure());
		assertIsEqual("Test 7 - qb", 0, qb.measure());
		
		// test 8 - qa.state = |-⟩, qb.state = |1⟩		(Bell state |Psi-⟩ and back)
		repeat(-PI/2, PI, 2, (pi) -> qb.cx(qa));
		assertIsEqual("Test 8 - qa", 0, qa.measure());
		assertIsEqual("Test 8 - qb", 0, qb.measure());
	}

	/**
	 * Asserts that the CZ gate is reversible.
	 */
	@Test
	public void testCZGate() {
		// test 0 - qa.theta = pi/3, qb.theta = pi/3
		repeat(PI/3, PI/3, 2, (pi) -> qb.cz(qa));
		assertIsEqual("Test 0 - qa", 0, qa.measure());
		assertIsEqual("Test 0 - qb", 0, qb.measure());
		
		// test 1 - qa.theta = pi/4, qb.theta = -pi/4
		repeat(PI/4, -PI/4, 2, (pi) -> qb.cz(qa));
		assertIsEqual("Test 1 - qa", 0, qa.measure());
		assertIsEqual("Test 1 - qb", 0, qb.measure());
		
		// test 2 - qa.theta = -pi/6, qb.theta = pi/6
		repeat(-PI/6, PI/6, 2, (pi) -> qb.cz(qa));
		assertIsEqual("Test 2 - qa", 0, qa.measure());
		assertIsEqual("Test 2 - qb", 0, qb.measure());
		
		// test 3 - qa.theta = -pi/2, qb.theta = -pi/4
		repeat(-PI/2, -PI/4, 2, (pi) -> qb.cz(qa));
		assertIsEqual("Test 3 - qa", 0, qa.measure());
		assertIsEqual("Test 3 - qb", 0, qb.measure());
		
		// test 4 - qa.state = |+⟩, qb.state = |+⟩
		repeat(PI/2, PI/2, 2, (pi) -> qb.cz(qa));
		assertIsEqual("Test 4 - qa", 0, qa.measure());
		assertIsEqual("Test 4 - qb", 0, qb.measure());
		
		// test 5 - qa.state = |+⟩, qb.state = |-⟩
		repeat(PI/2, -PI/2, 2, (pi) -> qb.cz(qa));
		assertIsEqual("Test 5 - qa", 0, qa.measure());
		assertIsEqual("Test 5 - qb", 0, qb.measure());
		
		// test 6 - qa.state = |-⟩, qb.state = |+⟩
		repeat(-PI/2, PI/2, 2, (pi) -> qb.cz(qa));
		assertIsEqual("Test 6 - qa", 0, qa.measure());
		assertIsEqual("Test 6 - qb", 0, qb.measure());
		
		// test 7 - qa.state = |-⟩, qb.state = |-⟩
		repeat(-PI/2, -PI/2, 2, (pi) -> qb.cz(qa));
		assertIsEqual("Test 7 - qa", 0, qa.measure());
		assertIsEqual("Test 7 - qb", 0, qb.measure());
	}

	/**
	 * Asserts that the C gate (for any axis) is reversible.
	 * <p>
	 * Qubit A is control qubit and B is target.
	 */
	@Test
	public void testCGate() {
		// test 0 - qa.state = |+⟩, qb.state = |0⟩			Controlled X	(Bell state |Phi+⟩ and back)
		repeat(PI/2, ZERO, 2, (pi) -> qb.c(Axis.X, qa));
		assertIsEqual("Test 0 - qa", 0, qa.measure());
		assertIsEqual("Test 0 - qb", 0, qb.measure());
		
		// test 1 - qa.state = |-⟩, qb.state = |0⟩			Controlled Y
		repeat(-PI/2, ZERO, 2, (pi) -> qb.c(Axis.Y, qa));
		assertIsEqual("Test 1 - qa", 0, qa.measure());
		assertIsEqual("Test 1 - qb", 0, qb.measure());
		
		// test 2 - qa.state = |+⟩, qb.state = |-⟩			Controlled Z
		repeat(PI/2, -PI/2, 2, (pi) -> qb.c(Axis.Z, qa));
		assertIsEqual("Test 2 - qa", 0, qa.measure());
		assertIsEqual("Test 2 - qb", 0, qb.measure());
		
		// test 3 - qa.state = |-⟩, qb.theta = -pi/4			Controlled H
		repeat(-PI/2, -PI/4, 2, (pi) -> qb.c(Axis.H, qa));
		assertIsEqual("Test 3 - qa", 0, qa.measure());
		assertIsEqual("Test 3 - qb", 0, qb.measure());
	}

	/**
	 * Asserts that the CP gate gives identical state
	 * if the sum of controlled rotations is 2pi.
	 */
	@Test
	public void testCPGate() {
		// test 0 - qa.theta = pi/3, qb.theta = pi/3		Controlled P(2pi)
		repeat(PI/3, PI/3, 1, (rad) -> qb.cp(rad, qa));
		assertIsEqual("Test 0 - qa", 0, qa.measure());
		assertIsEqual("Test 0 - qb", 0, qb.measure());
		
		// test 1 - qa.theta = pi/4, qb.theta = -pi/4		Controlled P(pi)
		repeat(PI/4, -PI/4, 2, (rad) -> qb.cp(rad, qa));
		assertIsEqual("Test 1 - qa", 0, qa.measure());
		assertIsEqual("Test 1 - qb", 0, qb.measure());
		
		// test 2 - qa.theta = -pi/6, qb.theta = pi/6		Controlled P(2pi/3)
		repeat(-PI/6, PI/6, 3, (rad) -> qb.cp(rad, qa));
		assertIsEqual("Test 2 - qa", 0, qa.measure());
		assertIsEqual("Test 2 - qb", 0, qb.measure());
		
		// test 3 - qa.theta = -pi/2, qb.theta = -pi/4		Controlled P(pi/2)
		repeat(-PI/2, -PI/4, 4, (rad) -> qb.cp(rad, qa));
		assertIsEqual("Test 3 - qa", 0, qa.measure());
		assertIsEqual("Test 3 - qb", 0, qb.measure());
		
		// test 4 - qa.state = |+⟩, qb.state = |+⟩			Controlled P(2pi)
		repeat(PI/2, PI/2, 1, (rad) -> qb.cp(rad, qa));
		assertIsEqual("Test 4 - qa", 0, qa.measure());
		assertIsEqual("Test 4 - qb", 0, qb.measure());
		
		// test 5 - qa.state = |+⟩, qb.state = |-⟩			Controlled P(pi)
		repeat(PI/2, -PI/2, 2, (rad) -> qb.cp(rad, qa));
		assertIsEqual("Test 5 - qa", 0, qa.measure());
		assertIsEqual("Test 5 - qb", 0, qb.measure());
		
		// test 6 - qa.state = |-⟩, qb.state = |+⟩			Controlled P(2pi/3)
		repeat(-PI/2, PI/2, 3, (rad) -> qb.cp(rad, qa));
		assertIsEqual("Test 6 - qa", 0, qa.measure());
		assertIsEqual("Test 6 - qb", 0, qb.measure());
		
		// test 7 - qa.state = |-⟩, qb.state = |-⟩			Controlled P(pi/2)
		repeat(-PI/2, -PI/2, 4, (rad) -> qb.cp(rad, qa));
		assertIsEqual("Test 7 - qa", 0, qa.measure());
		assertIsEqual("Test 7 - qb", 0, qb.measure());
	}

	/**
	 * Asserts that the CR gate gives identical state
	 * for the target qubit, but not for the control qubit,
	 * if the sum of controlled rotations is 2pi.
	 * <p>
	 * The controlled RX, RY and RZ gates, and more
	 * in general the controlled R(axis, angle) gate,
	 * have in common that the control qubit gets
	 * an extra phase shift of -angle/2.
	 * So, when the sum of the controlled rotation
	 * angles equals 2pi, the control qubit's phase
	 * has shifted by -pi.
	 */
	@Test
	public void testCRGate() {
		// test 0 - qa.theta = pi/3, qb.theta = pi/3				Controlled RX(2pi)
		repeat(PI/3, PI/3, 1, (rad) -> qb.cr(Axis.X, rad, qa));
		// expect qa to have phase pi and theta 2pi/3 due to "reverse preparation" in repeat()
		// another RY(-pi/3) should set qa to |1⟩, which can be measured with certainty
		assertIsEqual("Test 0 - qa", 1, qa.ry(-PI/3).measure());
		assertIsEqual("Test 0 - qb", 0, qb.measure());
		
		// test 1 - qa.theta = pi/4, qb.theta = -pi/4				Controlled RY(pi)
		// note: qa starts from |1⟩ and ry(pi/4) on |1⟩ gives phase pi and theta 3pi/4
		repeat(PI/4, -PI/4, 2, (rad) -> qb.cr(Axis.Y, rad, qa));
		// expect qa to have phase 0 and state |+⟩ due to "reverse preparation" in repeat()
		assertIsEqual("Test 1 - qa", 0, qa.h().measure());
		assertIsEqual("Test 1 - qb", 0, qb.measure());
		
		// test 2 - qa.theta = -pi/6, qb.theta = pi/6				Controlled RZ(2pi/3)
		repeat(-PI/6, PI/6, 3, (rad) -> qb.cr(Axis.Z, rad, qa));
		assertIsEqual("Test 2 - qa", 0, qa.ry(-PI/3).measure());
		assertIsEqual("Test 2 - qb", 0, qb.measure());
		
		// test 3 - qa.theta = -pi/2, qb.theta = -pi/4				Controlled RH(pi/2)
		repeat(-PI/2, -PI/4, 4, (rad) -> qb.cr(Axis.H, rad, qa));
		assertIsEqual("Test 3 - qa", 1, qa.measure());				// expect |1⟩
		assertIsEqual("Test 3 - qb", 0, qb.measure());
		
		// test 4 - qa.state = |-⟩, qb.state = |+⟩					Controlled RX(2pi)
		// note: qa starts from |1⟩ and ry(pi/2) on |1⟩ gives |-⟩
		repeat(PI/2, PI/2, 1, (rad) -> qb.cr(Axis.X, rad, qa));
		assertIsEqual("Test 4 - qa", 0, qa.measure());
		assertIsEqual("Test 4 - qb", 0, qb.measure());
		
		// test 5 - qa.state = |+⟩, qb.state = |-⟩					Controlled RY(pi)
		repeat(PI/2, -PI/2, 2, (rad) -> qb.cr(Axis.Y, rad, qa));
		assertIsEqual("Test 5 - qa", 1, qa.measure());				// expect |1⟩
		assertIsEqual("Test 5 - qb", 0, qb.measure());
		
		// test 6 - qa.state = |+⟩, qb.state = |+⟩					Controlled RZ(2pi/3)
		repeat(-PI/2, PI/2, 3, (rad) -> qb.cr(Axis.Z, rad, qa));
		// note: qa starts from |1⟩ and ry(-pi/2) on |1⟩ gives |+⟩
		assertIsEqual("Test 6 - qa", 0, qa.measure());
		assertIsEqual("Test 6 - qb", 0, qb.measure());
		
		// test 7 - qa.state = |-⟩, qb.state = |-⟩					Controlled RH(pi/2)
		repeat(-PI/2, -PI/2, 4, (rad) -> qb.cr(Axis.H, rad, qa));
		assertIsEqual("Test 7 - qa", 1, qa.measure());				// expect |1⟩
		assertIsEqual("Test 7 - qb", 0, qb.measure());
	}

	/**
	 * Asserts that the CU gate gives identical state for the
	 * target qubit, but usually not for the control qubit,
	 * if the sum of controlled rotations is 2pi.
	 * <p>
	 * Like U(theta, phi, lambda) is identical to
	 * matrix multiplication P(phi)RY(theta)P(lambda),
	 * The controlled version CU(theta, phi, lambda)
	 * is identical to CP(phi)CRY(theta)CP(lambda).
	 * <p>
	 * As demonstrated in {@link #testCPGate()} and
	 * {@link #testCRGate()}, only CRY(theta) will cause
	 * a phase shift of -theta/2 on the control qubit.
	 * So, when the sum of the controlled rotation
	 * angles theta equals 2pi, the control qubit's phase
	 * has shifted by -pi.
	 */
	@Test
	public void testCUGate() {
		// test 0 - qa.theta = pi/3, qb.theta = pi/3							Controlled P(2pi)
		repeat(PI/3, PI/3, 1, (phi) -> qb.cu(ZERO, phi, ZERO, qa));
		assertIsEqual("Test 0 - qa", 0, qa.measure());
		assertIsEqual("Test 0 - qb", 0, qb.measure());
		
		// test 1 - qa.theta = pi/4, qb.theta = -pi/4							Controlled P(pi)
		repeat(PI/4, -PI/4, 2, (phi) -> qb.cu(ZERO, phi, ZERO, qa));
		assertIsEqual("Test 1 - qa", 0, qa.measure());
		assertIsEqual("Test 1 - qb", 0, qb.measure());
		
		// test 2 - qa.theta = -pi/6, qb.theta = pi/6							Controlled P(2pi/3)
		repeat(-PI/6, PI/6, 3, (lambda) -> qb.cu(ZERO, ZERO, lambda, qa));
		assertIsEqual("Test 2 - qa", 0, qa.measure());
		assertIsEqual("Test 2 - qb", 0, qb.measure());
		
		// test 3 - qa.theta = -pi/2, qb.theta = -pi/4							Controlled P(pi/2)
		repeat(-PI/2, -PI/4, 4, (lambda) -> qb.cu(ZERO, ZERO, lambda, qa));
		assertIsEqual("Test 3 - qa", 0, qa.measure());
		assertIsEqual("Test 3 - qb", 0, qb.measure());
		
		// test 4 - qa.state = |+⟩, qb.state = |+⟩								Controlled RY(2pi)
		repeat(PI/2, PI/2, 1, (theta) -> qb.cu(theta, ZERO, ZERO, qa));
		assertIsEqual("Test 4 - qa", 1, qa.measure());							// expect |1⟩
		assertIsEqual("Test 4 - qb", 0, qb.measure());
		
		// test 5 - qa.state = |-⟩, qb.state = |-⟩								Controlled H
		// note: qa starts from |1⟩ and ry(pi/2) on |1⟩ gives |-⟩
		repeat(PI/2, -PI/2, 2, (pi) -> qb.cu(pi/2, ZERO, pi, qa));
		// lambda+phi equals pi, this reverses the effect of theta, so no net phase change after two times
		assertIsEqual("Test 5 - qa", 1, qa.measure());							// expect |1⟩ (no change)
		assertIsEqual("Test 5 - qb", 0, qb.measure());
		
		// test 6 - qa.state = |+⟩, qb.state = |0⟩								Controlled RX(2pi/3)
		// note: qa starts from |1⟩ and ry(-pi/2) on |1⟩ gives |+⟩
		repeat(-PI/2, ZERO, 3, (theta) -> qb.cu(theta, -PI/2, PI/2, qa));
		// lambda+phi equals 0, so they cancel each other on consecutive calls, so net phase change for CRX as for CRY
		assertIsEqual("Test 6 - qa", 0, qa.measure());							// expect |0⟩ (change)
		assertIsEqual("Test 6 - qb", 0, qb.measure());
		
		// test 7 - qa.state = |-⟩, qb.state = |-⟩								Controlled RY(pi/2)
		repeat(-PI/2, -PI/2, 4, (theta) -> qb.cu(theta, ZERO, ZERO, qa));
		assertIsEqual("Test 7 - qa", 1, qa.measure());							// expect |1⟩
		assertIsEqual("Test 7 - qb", 0, qb.measure());
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
	 * @param steps		number of times to perform the given operation
	 * @param operation	the operation to perform repeatedly
	 */
	protected void repeat(double initA, double initB, int steps, Consumer<Double> operation) {
		// prepare both qubits
		qa.ry(initA);
		qb.ry(initB);
		
		// repeat the operation
		double stepSize = TWO_PI / steps;
		for (int step = 0; step < steps; step++) {
			operation.accept(stepSize);
		}
		
		// reverse preparation
		qb.ry(-initB);
		qa.ry(-initA);
	}

}
