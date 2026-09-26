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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.bemuisoft.quantum.api.Axis;
import org.bemuisoft.quantum.api.IQubitAnalyzer;
import org.bemuisoft.quantum.api.IQubitFactory;
import org.junit.jupiter.api.Test;

/**
 * Tests the required methods of the {@link IQubitAnalyzer}
 * interface in a single qubit system.
 * 
 * @param <Q>	the type of qubit to be tested
 * 
 * @author Benno Muilwijk
 */
public class OneQubitAnalyzerTest<Q extends IQubitAnalyzer> extends AbstractQubitTest<Q> {

	/** Qubit A. */
	protected Q qa;

	/**
	 * Default constructor.
	 */
	public OneQubitAnalyzerTest() {
		super(1);
	}

	@Override
	public final OneQubitAnalyzerTest<Q> init(IQubitFactory<Q> factory) {
		super.init(factory);
		qa = q(0);
		return this;
	}

	@Override
	public void runAll() {
		assertAll(getHeading(),
			run(() -> testInitialState()),
			run(() -> testReset()),
			run(() -> testH()),
			run(() -> testX()),
			run(() -> testY()),
			run(() -> testZ()),
			run(() -> testS()),
			run(() -> testSdg()),
			run(() -> testT()),
			run(() -> testTdg()),
			run(() -> testP()),
			run(() -> testRz()),
			run(() -> testRy()),
			run(() -> testRx()),
			run(() -> testSx()),
			run(() -> testSxdg()),
			run(() -> testR()),
			run(() -> testU()),
			run(() -> testNOT()),
			run(() -> testProbabilities())
		);
	}

	/**
	 * Asserts that the initial state is |0⟩.
	 */
	@Test
	public void testInitialState() {
		// initial state should be |0⟩
		assertXYZ("Initial state", ZERO, ZERO, PLUS);
	}

	/**
	 * Asserts that reset gives |0⟩.
	 */
	@Test
	public void testReset() {
		// test 0 - reset from |0⟩ (initial state)
		qa.reset();
		assertXYZ("Reset from |0⟩", ZERO, ZERO, PLUS);
		
		// test 1 - reset from |1⟩
		qa.x();
		qa.reset();
		assertXYZ("Reset from |1⟩", ZERO, ZERO, PLUS);
		
		// test 2 - reset from |+⟩ (defined as (|0⟩ + |1⟩)/sqrt(2))
		qa.h();
		qa.reset();
		assertXYZ("Reset from |+⟩", ZERO, ZERO, PLUS);
		
		// test 3 - reset from |-⟩ (defined as (|0⟩ - |1⟩)/sqrt(2))
		qa.x().h();
		qa.reset();
		assertXYZ("Reset from |-⟩", ZERO, ZERO, PLUS);
		
		// test 4 - reset from |»⟩ (defined here as (|0⟩ + i|1⟩)/sqrt(2))
		qa.h().s();
		qa.reset();
		assertXYZ("Reset from |»⟩", ZERO, ZERO, PLUS);
		
		// test 5 - reset from |«⟩ (defined here as (|0⟩ - i|1⟩)/sqrt(2))
		qa.h().sdg();
		qa.reset();
		assertXYZ("Reset from |«⟩", ZERO, ZERO, PLUS);
	}

	/**
	 * Asserts that the Hadamard gate works as expected.
	 */
	@Test
	public void testH() {
		// test 0 - h on |0⟩ should give |+⟩
		qa.h();
		assertXYZ("H |0⟩", PLUS, ZERO, ZERO);
		
		// test 1 - h on |+⟩ should give |0⟩
		qa.h();
		assertXYZ("H |+⟩", ZERO, ZERO, PLUS);
		
		// test 2 - h on |1⟩ should give |-⟩
		qa.x();
		qa.h();
		assertXYZ("H |+⟩", MINUS, ZERO, ZERO);
		
		// test 3 - h on |-⟩ should give |1⟩
		qa.h();
		assertXYZ("H |-⟩", ZERO, ZERO, MINUS);
		
		// test 4 - h on |»⟩ should give |«⟩
		qa.reset().h().s();
		qa.h();
		assertXYZ("H |»⟩", ZERO, MINUS, ZERO);
		
		// test 5 - h on |«⟩ should give |»⟩
		qa.h();
		assertXYZ("H |«⟩", ZERO, PLUS, ZERO);
	}

	/**
	 * Asserts that the Pauli-X gate works as expected.
	 */
	@Test
	public void testX() {
		// test 0 - x on |0⟩ should give |1⟩
		qa.x();
		assertXYZ("X |0⟩", ZERO, ZERO, MINUS);
		
		// test 1 - x on |1⟩ should give |0⟩
		qa.x();
		assertXYZ("X |1⟩", ZERO, ZERO, PLUS);
		
		// test 2 - x on |+⟩ should give |+⟩
		qa.h();
		qa.x();
		assertXYZ("X |+⟩", PLUS, ZERO, ZERO);
		
		// test 3 - x on |-⟩ should give |-⟩
		qa.z();
		qa.x();
		assertXYZ("X |-⟩", MINUS, ZERO, ZERO);
		
		// test 4 - x on |»⟩ should give |«⟩
		qa.sdg();
		qa.x();
		assertXYZ("X |»⟩", ZERO, MINUS, ZERO);
		
		// test 5 - x on |«⟩ should give |»⟩
		qa.x();
		assertXYZ("X |«⟩", ZERO, PLUS, ZERO);
	}

	/**
	 * Asserts that the Pauli-Y gate works as expected.
	 */
	@Test
	public void testY() {
		// test 0 - y on |0⟩ should give |1⟩
		qa.y();
		assertXYZ("Y |0⟩", ZERO, ZERO, MINUS);
		
		// test 1 - y on |1⟩ should give |0⟩
		qa.y();
		assertXYZ("Y |1⟩", ZERO, ZERO, PLUS);
		
		// test 2 - y on |+⟩ should give |-⟩
		qa.h();
		qa.y();
		assertXYZ("Y |+⟩", MINUS, ZERO, ZERO);
		
		// test 3 - y on |-⟩ should give |+⟩
		qa.y();
		assertXYZ("Y |-⟩", PLUS, ZERO, ZERO);
		
		// test 4 - y on |»⟩ should give |»⟩
		qa.s();
		qa.y();
		assertXYZ("Y |»⟩", ZERO, PLUS, ZERO);
		
		// test 5 - y on |«⟩ should give |«⟩
		qa.z();
		qa.y();
		assertXYZ("Y |«⟩", ZERO, MINUS, ZERO);
	}

	/**
	 * Asserts that the Pauli-Z gate works as expected.
	 */
	@Test
	public void testZ() {
		// test 0 on |0⟩ should give |0⟩
		qa.z();
		assertXYZ("Z |0⟩", ZERO, ZERO, PLUS);
		
		// test 1 on |1⟩ should give |1⟩
		qa.x();
		qa.z();
		assertXYZ("Z |1⟩", ZERO, ZERO, MINUS);
		
		// test 2 on |+⟩ should give |-⟩
		qa.x().h();
		qa.z();
		assertXYZ("Z |+⟩", MINUS, ZERO, ZERO);
		
		// test 3 on |-⟩ should give |+⟩
		qa.z();
		assertXYZ("Z |-⟩", PLUS, ZERO, ZERO);
		
		// test 4 on |»⟩ should give |«⟩
		qa.s();
		qa.z();
		assertXYZ("Z |»⟩", ZERO, MINUS, ZERO);
		
		// test 5 on |«⟩ should give |»⟩
		qa.z();
		assertXYZ("Z |«⟩", ZERO, PLUS, ZERO);
	}

	/**
	 * Asserts that the S gate works as expected.
	 */
	@Test
	public void testS() {
		// test 0 - s on |0⟩ should give |0⟩
		qa.s();
		assertXYZ("S |0⟩", ZERO, ZERO, PLUS);
		
		// test 1 - s on |1⟩ should give |1⟩
		qa.x();
		qa.s();
		assertXYZ("S |1⟩", ZERO, ZERO, MINUS);
		
		// test 2 - s on |+⟩ should give |»⟩
		qa.x().h();
		qa.s();
		assertXYZ("S |+⟩", ZERO, PLUS, ZERO);
		
		// test 3 - s on |»⟩ should give |-⟩
		qa.s();
		assertXYZ("S |»⟩", MINUS, ZERO, ZERO);		
		// test 4 - s on |-⟩ should give |«⟩
		qa.s();
		assertXYZ("S |-⟩", ZERO, MINUS, ZERO);
		
		// test 5 - s on |«⟩ should give |+⟩
		qa.s();
		assertXYZ("S |«⟩", PLUS, ZERO, ZERO);
	}

	/**
	 * Asserts that the S-dagger gate works as expected.
	 */
	@Test
	public void testSdg() {
		// test 0 - sdg on |0⟩ should give |0⟩
		qa.sdg();
		assertXYZ("Sdg |0⟩", ZERO, ZERO, PLUS);
		
		// test 1 - sdg on |1⟩ should give |1⟩
		qa.x();
		qa.sdg();
		assertXYZ("Sdg |1⟩", ZERO, ZERO, MINUS);
		
		// test 2 - sdg on |+⟩ should give |«⟩
		qa.x().h();
		qa.sdg();
		assertXYZ("Sdg |-⟩", ZERO, MINUS, ZERO);
		
		// test 3 - sdg on |«⟩ should give |-⟩
		qa.sdg();
		assertXYZ("Sdg |»⟩", MINUS, ZERO, ZERO);
		
		// test 4 - sdg on |-⟩ should give |»⟩
		qa.sdg();
		assertXYZ("Sdg |+⟩", ZERO, PLUS, ZERO);
		
		// test 5 - sdg on |»⟩ should give |+⟩
		qa.sdg();
		assertXYZ("Sdg |«⟩", PLUS, ZERO, ZERO);
	}

	/**
	 * Asserts that the T gate works as expected.
	 */
	@Test
	public void testT() {
		// test 0 - t on |0⟩ should give |0⟩
		qa.t();
		assertXYZ("T |0⟩", ZERO, ZERO, PLUS);
		
		// test 1 - t on |1⟩ should give |1⟩
		qa.x();
		qa.t();
		assertXYZ("T |1⟩", ZERO, ZERO, MINUS);
		
		// test 2 - tt on |+⟩ should give |»⟩
		qa.x().h();
		qa.t().t();
		assertXYZ("TT |+⟩", ZERO, PLUS, ZERO);
		
		// test 3 - tt on |»⟩ should give |-⟩
		qa.t().t();
		assertXYZ("TT |»⟩", MINUS, ZERO, ZERO);
		
		// test 4 - tt on |-⟩ should give |«⟩
		qa.t().t();
		assertXYZ("TT |-⟩", ZERO, MINUS, ZERO);
		
		// test 5 - tt on |«⟩ should give |+⟩
		qa.t().t();
		assertXYZ("TT |«⟩", PLUS, ZERO, ZERO);
	}

	/**
	 * Asserts that the T-dagger gate works as expected.
	 */
	@Test
	public void testTdg() {
		// test 0 - tdg on |0⟩ should give |0⟩
		qa.tdg();
		assertXYZ("Tdg |0⟩", ZERO, ZERO, PLUS);
		
		// test 1 - tdg on |1⟩ should give |1⟩
		qa.x();
		qa.tdg();
		assertXYZ("Tdg |1⟩", ZERO, ZERO, MINUS);
		
		// test 2 - tdg² on |+⟩ should give |«⟩
		qa.x().h();
		qa.tdg().tdg();
		assertXYZ("Tdg² |-⟩", ZERO, MINUS, ZERO);
		
		// test 3 - tdg² on |«⟩ should give |-⟩
		qa.tdg().tdg();
		assertXYZ("Tdg² |»⟩", MINUS, ZERO, ZERO);
		
		// test 3 - tdg² on |-⟩ should give |»⟩
		qa.tdg().tdg();
		assertXYZ("Tdg² |+⟩", ZERO, PLUS, ZERO);
		
		// test 5 - tdg² on |»⟩ should give |+⟩
		qa.tdg().tdg();
		assertXYZ("Tdg² |«⟩", PLUS, ZERO, ZERO);
	}

	/**
	 * Asserts that the P gate works as expected.
	 * <p>
	 * The P gate is a universal phase shift gate.
	 * In some quantum computers it is also known
	 * as the U1 gate.
	 * <ul>
	 * <li>P(pi) ≡ Z</li>
	 * <li>P(pi/2) ≡ S</li>
	 * <li>P(pi/4) ≡ T</li>
	 * <li>P(-pi/2) ≡ Sdg</li>
	 * <li>P(-pi/4) ≡ Tdg</li>
	 * </ul>
	 */
	@Test
	public void testP() {
		// test 0 - p(pi/2) on |0⟩ should give |0⟩
		qa.p(HALF_PI);
		assertXYZ("P(pi/2) |0⟩", ZERO, ZERO, PLUS);
		
		// test 1 - p(pi/2) on |1⟩ should give |1⟩
		qa.x();
		qa.p(HALF_PI);
		assertXYZ("P(pi/2) |1⟩", ZERO, ZERO, MINUS);
		
		// test 2 - p(pi/2) on |+⟩ should give |»⟩
		qa.x().h();
		qa.p(HALF_PI);
		assertXYZ("P(pi/2) |+⟩", ZERO, PLUS, ZERO);
		
		// test 3 - p(pi/2) on |»⟩ should give |-⟩
		qa.p(HALF_PI);
		assertXYZ("P(pi/2) |»⟩", MINUS, ZERO, ZERO);
		
		// test 4 - p(pi/2) on |-⟩ should give |«⟩
		qa.p(HALF_PI);
		assertXYZ("P(pi/2) |-⟩", ZERO, MINUS, ZERO);
		
		// test 5 - p(pi/2) on |«⟩ should give |+⟩
		qa.p(HALF_PI);
		assertXYZ("P(pi/2) |«⟩", PLUS, ZERO, ZERO);
	}

	/**
	 * Asserts that the Rz gate works as expected.
	 * <p>
	 * Note that the Rz gate works practically the
	 * same as the P gate.
	 * Mathematically they differ in the resulting
	 * global phase, which cannot be measured.
	 * <p>
	 * A real difference appears only in the
	 * controlled versions of these gates,
	 * but that is out of scope for this test.
	 * 
	 * @see ControlledGateTest#testCRGate()
	 */
	@Test
	public void testRz() {
		// test 0 - rz(pi/2) on |0⟩ should give |0⟩
		qa.rz(HALF_PI);
		assertXYZ("Rz(pi/2) |0⟩", ZERO, ZERO, PLUS);
		
		// test 1 - rz(pi/2) on |1⟩ should give |1⟩
		qa.x();
		qa.rz(HALF_PI);
		assertXYZ("Rz(pi/2) |1⟩", ZERO, ZERO, MINUS);
		
		// test 2 - rz(pi/2) on |+⟩ should give |»⟩
		qa.x().h();
		qa.rz(HALF_PI);
		assertXYZ("Rz(pi/2) |+⟩", ZERO, PLUS, ZERO);
		
		// test 3 - rz(pi/2) on |»⟩ should give |-⟩
		qa.rz(HALF_PI);
		assertXYZ("Rz(pi/2) |»⟩", MINUS, ZERO, ZERO);
		
		// test 4 - rz(pi/2) on |-⟩ should give |«⟩
		qa.rz(HALF_PI);
		assertXYZ("Rz(pi/2) |-⟩", ZERO, MINUS, ZERO);
		
		// test 5 - rz(pi/2) on |«⟩ should give |+⟩
		qa.rz(HALF_PI);
		assertXYZ("Rz(pi/2) |«⟩", PLUS, ZERO, ZERO);
	}

	/**
	 * Asserts that the Ry gate works as expected.
	 * <p>
	 * Note that Ry(pi) works practically the
	 * same as the Pauli-Y gate.
	 * Mathematically they differ in the resulting
	 * global phase, which cannot be measured.
	 * <p>
	 * A real difference appears only in the
	 * controlled versions of these gates,
	 * but that is out of scope for this test.
	 * 
	 * @see ControlledGateTest#testCRGate()
	 */
	@Test
	public void testRy() {
		// test 0 - ry(pi/2) on |0⟩ should give |+⟩
		qa.ry(HALF_PI);
		assertXYZ("Ry(pi/2) |0⟩", PLUS, ZERO, ZERO);
		
		// test 1 - ry(pi/2) on |+⟩ should give |1⟩
		qa.ry(HALF_PI);
		assertXYZ("Ry(pi/2) |+⟩", ZERO, ZERO, MINUS);
		
		// test 2 - ry(pi/2) on |1⟩ should give |-⟩
		qa.ry(HALF_PI);
		assertXYZ("Ry(pi/2) |1⟩", MINUS, ZERO, ZERO);
		
		// test 3 - ry(pi/2) on |-⟩ should give |0⟩
		qa.ry(HALF_PI);
		assertXYZ("Ry(pi/2) |-⟩", ZERO, ZERO, PLUS);
		
		// test 4 - ry(pi/2) on |»⟩ should give |»⟩
		qa.h().s();
		qa.ry(HALF_PI);
		assertXYZ("Ry(pi/2) |»⟩", ZERO, PLUS, ZERO);
		
		// test 5 - ry(pi/2) on |«⟩ should give |«⟩
		qa.z();
		qa.ry(HALF_PI);
		assertXYZ("Ry(pi/2) |«⟩", ZERO, MINUS, ZERO);
	}

	/**
	 * Asserts that the Rx gate works as expected.
	 * <p>
	 * Note that Rx(pi) works practically the
	 * same as the Pauli-X gate.
	 * Mathematically they differ in the resulting
	 * global phase, which cannot be measured.
	 * <p>
	 * A real difference appears only in the
	 * controlled versions of these gates,
	 * but that is out of scope for this test.
	 * 
	 * @see ControlledGateTest#testCRGate()
	 */
	@Test
	public void testRx() {
		// test 0 - rx(pi/2) on |0⟩ should give |«⟩
		qa.rx(HALF_PI);
		assertXYZ("Rx(pi/2) |0⟩", ZERO, MINUS, ZERO);
		
		// test 1 - rx(pi/2) on |«⟩ should give |1⟩
		qa.rx(HALF_PI);
		assertXYZ("Rx(pi/2) |«⟩", ZERO, ZERO, MINUS);
		
		// test 2 - rx(pi/2) on |1⟩ should give |»⟩
		qa.rx(HALF_PI);
		assertXYZ("Rx(pi/2) |1⟩", ZERO, PLUS, ZERO);
		
		// test 3 - rx(pi/2) on |»⟩ should give |0⟩
		qa.rx(HALF_PI);
		assertXYZ("Rx(pi/2) |»⟩", ZERO, ZERO, PLUS);
		
		// test 4 - rx(pi/2) on |+⟩ should give |+⟩
		qa.h();
		qa.rx(HALF_PI);
		assertXYZ("Rx(pi/2) |+⟩", PLUS, ZERO, ZERO);
		
		// test 5 - rx(pi/2) on |-⟩ should give |-⟩
		qa.z();
		qa.rx(HALF_PI);
		assertXYZ("Rx(pi/2) |-⟩", MINUS, ZERO, ZERO);
	}

	/**
	 * Asserts that the Sx gate works as expected.
	 * <p>
	 * Note that the Sx gate works practically the
	 * same as an Rx(pi/2) gate.
	 * Mathematically they differ in the resulting
	 * global phase, which cannot be measured.
	 * <p>
	 * For example, Sx²≡ X (like S² ≡ Z),
	 * but Rx(pi) differs from Sx² and X in the
	 * global phase.
	 * <p>
	 * A real difference appears only in the
	 * controlled versions of these gates,
	 * but that is out of scope for this test.
	 * 
	 * @see ControlledGateTest#testCRGate()
	 */
	@Test
	public void testSx() {
		// test 0 - sx on |0⟩ should give |«⟩
		qa.sx();
		assertXYZ("Sx |0⟩", ZERO, MINUS, ZERO);
		
		// test 1 - sx on |«⟩ should give |1⟩
		qa.sx();
		assertXYZ("Sx |«⟩", ZERO, ZERO, MINUS);
		
		// test 2 - sx on |1⟩ should give |»⟩
		qa.sx();
		assertXYZ("Sx |1⟩", ZERO, PLUS, ZERO);
		
		// test 3 - sx on |»⟩ should give |0⟩
		qa.sx();
		assertXYZ("Sx |»⟩", ZERO, ZERO, PLUS);
		
		// test 4 - sx on |+⟩ should give |+⟩
		qa.h();
		qa.sx();
		assertXYZ("Sx |+⟩", PLUS, ZERO, ZERO);
		
		// test 5 - sx on |-⟩ should give |-⟩
		qa.z();
		qa.sx();
		assertXYZ("Sx |-⟩", MINUS, ZERO, ZERO);
	}

	/**
	 * Asserts that the Sx-dagger gate works as expected.
	 * <p>
	 * Note that the Sx-dagger gate works practically
	 * the same as an Rx(-pi/2) gate.
	 * Mathematically they differ in the resulting
	 * global phase, which cannot be measured.
	 * <p>
	 * A real difference appears only in the
	 * controlled versions of these gates,
	 * but that is out of scope for this test.
	 * 
	 * @see ControlledGateTest#testCRGate()
	 */
	@Test
	public void testSxdg() {
		// test 0 - sxdg on |0⟩ should give |»⟩
		qa.sxdg();
		assertXYZ("Sxdg |0⟩", ZERO, PLUS, ZERO);
		
		// test 1 - sxdg on |»⟩ should give |1⟩
		qa.sxdg();
		assertXYZ("Sxdg |«⟩", ZERO, ZERO, MINUS);
		
		// test 2 - sxdg on |1⟩ should give |«⟩
		qa.sxdg();
		assertXYZ("Sxdg |1⟩", ZERO, MINUS, ZERO);
		
		// test 3 - sxdg on |«⟩ should give |0⟩
		qa.sxdg();
		assertXYZ("Sxdg |»⟩", ZERO, ZERO, PLUS);
		
		// test 4 - sxdg on |+⟩ should give |+⟩
		qa.h();
		qa.sxdg();
		assertXYZ("Sxdg |+⟩", PLUS, ZERO, ZERO);
		
		// test 5 - sxdg on |-⟩ should give |-⟩
		qa.z();
		qa.sxdg();
		assertXYZ("Sxdg |-⟩", MINUS, ZERO, ZERO);
	}

	/**
	 * Asserts that the R gate works as expected.
	 * <p>
	 * The R gate is a universal rotation gate.
	 * <ul>
	 * <li>R(Axis.X, angle) ≡ Rx(angle)
	 * <li>R(Axis.Y, angle) ≡ Ry(angle)
	 * <li>R(Axis.Z, angle) ≡ Rz(angle)
	 * </ul>
	 * Note that R(Axis.X, pi), R(Axis.Y, pi),
	 * R(Axis.Z, pi) and R(Axis.H, pi) work
	 * practically the same as X, Y, Z and H,
	 * respectively.
	 * Mathematically they differ in the resulting
	 * global phase, which cannot be measured.
	 * <p>
	 * A real difference appears only in the
	 * controlled versions of these gates,
	 * but that is out of scope for this test.
	 * 
	 * @see ControlledGateTest#testCRGate()
	 */
	@Test
	public void testR() {
		// test 0 - r(X, pi) on |0⟩ should give |1⟩
		qa.r(Axis.X, PI);
		assertXYZ("R(X, pi) |0⟩", ZERO, ZERO, MINUS);
		
		// test 1 - r(Y, pi) on |1⟩ should give |0⟩
		qa.r(Axis.Y, PI);
		assertXYZ("R(Y, pi) |1⟩", ZERO, ZERO, PLUS);
		
		// test 2 - r(H, pi) on |0⟩ should give |+⟩
		qa.r(Axis.H, PI);
		assertXYZ("R(H, pi) |0⟩", PLUS, ZERO, ZERO);
		
		// test 3 - r(Z, pi) on |+⟩ should give |-⟩
		qa.r(Axis.Z, PI);
		assertXYZ("R(Z, pi) |+⟩", MINUS, ZERO, ZERO);
		
		// test 4 - r(H, pi) on |-⟩ should give |1⟩
		qa.r(Axis.H, PI);
		assertXYZ("R(H, pi) |-⟩", ZERO, ZERO, MINUS);
		
		// test 5 - r(X, pi/2) on |1⟩ should give |»⟩
		qa.r(Axis.X, HALF_PI);
		assertXYZ("R(Y, pi/2) |1⟩", ZERO, PLUS, ZERO);
		
		// test 6 - r(Z, pi/2) on |»⟩ should give |-⟩
		qa.r(Axis.Z, HALF_PI);
		assertXYZ("R(Y, pi/2) |1⟩", MINUS, ZERO, ZERO);
		
		// test 7 - r(Y, pi/2) on |-⟩ should give |0⟩
		qa.r(Axis.Y, HALF_PI);
		assertXYZ("R(Y, pi/2) |1⟩", ZERO, ZERO, PLUS);
	}

	/**
	 * Asserts that the U gate works as expected.
	 * <p>
	 * The U gate is the universal gate.
	 * In some quantum computers it is also known
	 * as the U3 gate.
	 * <ul>
	 * <li>U(theta, phi, lambda) ≡ P(phi)Ry(theta)P(lambda)
	 * (as matrix product, executed from right to left)</li>
	 * <li>U(theta, phi, lambda) ≡ p(lambda).ry(theta).p(phi)
	 * (as code, executed from left to right)</li>
	 * <li>U(theta, 0, 0) ≡ Ry</li>
	 * <li>U(0, phi, lambda) ≡ P(lambda+phi)</li>
	 * <li>U(theta, -pi/2, pi/2) ≡ Rx(theta)</li>
	 * <li>U(pi/2, 0, pi) ≡ H</li>
	 * </ul>
	 */
	@Test
	public void testU() {
		// test 0 - u(pi/2, pi/2, pi/2) on |0⟩ should give |»⟩
		qa.u(HALF_PI, HALF_PI, HALF_PI);
		assertXYZ("U(pi/2, pi/2, pi/2) |0⟩", ZERO, PLUS, ZERO);
		
		// test 1 - u(pi/2, pi/2, pi/2) on |»⟩ should give |0⟩
		qa.u(HALF_PI, HALF_PI, HALF_PI);
		assertXYZ("U(pi/2, pi/2, pi/2) |»⟩", ZERO, ZERO, PLUS);
		
		// test 2 - u(pi, 0, 0) on |0⟩ should give |1⟩
		qa.u(PI, ZERO, ZERO);
		assertXYZ("U(pi, 0, 0) |»⟩", ZERO, ZERO, MINUS);
		
		// test 3 - u(pi/2, 0, pi) on |1⟩ should give |-⟩
		qa.u(HALF_PI, ZERO, PI);
		assertXYZ("U(pi/2, 0, pi) |1⟩", MINUS, ZERO, ZERO);
		
		// test 4 - u(0, pi/2, pi/2) on |-⟩ should give |+⟩
		qa.u(ZERO, HALF_PI, HALF_PI);
		assertXYZ("U(0, pi/2, pi/2) |-⟩", PLUS, ZERO, ZERO);
		
		// test 5 - u(pi/2, 0, pi) on |+⟩ should give |0⟩
		qa.u(HALF_PI, ZERO, PI);
		assertXYZ("U(pi/2, 0, pi) |1⟩", ZERO, ZERO, PLUS);
		
		// test 6 - u(pi/2, -pi/2, pi/2) on |0⟩ should give |«⟩
		qa.u(HALF_PI, -HALF_PI, HALF_PI);
		assertXYZ("U(pi/2, -pi/2, pi/2) |0⟩", ZERO, MINUS, ZERO);
	}

	/**
	 * Asserts that the NOT gate works as expected.
	 * <p>
	 * The NOT gate should work exactly the same as
	 * the Pauli-X gate.
	 */
	@Test
	public void testNOT() {
		// test 0 - not on |0⟩ should give |1⟩
		qa.not();
		assertXYZ("NOT |0⟩", ZERO, ZERO, MINUS);
		
		// test 1 - not on |1⟩ should give |0⟩
		qa.not();
		assertXYZ("NOT |1⟩", ZERO, ZERO, PLUS);
		
		// test 2 - not on |+⟩ should give |+⟩
		qa.h();
		qa.not();
		assertXYZ("NOT |+⟩", PLUS, ZERO, ZERO);
		
		// test 3 - not on |-⟩ should give |-⟩
		qa.z();
		qa.not();
		assertXYZ("NOT |-⟩", MINUS, ZERO, ZERO);
		
		// test 4 - not on |»⟩ should give |«⟩
		qa.sdg();
		qa.not();
		assertXYZ("NOT |»⟩", ZERO, MINUS, ZERO);
		
		// test 5 - not on |«⟩ should give |»⟩
		qa.not();
		assertXYZ("NOT |«⟩", ZERO, PLUS, ZERO);
	}

	/**
	 * Asserts that predicted probabilities are as expected.
	 * <p>
	 * Also asserts that the test qubit is always pure, not mixed,
	 * because this test class works with a single qubit.
	 */
	@Test
	public void testProbabilities() {
		// test 0 - probabilities at |0⟩ (initial state) 
		assertProbabilities("Probabilities |0⟩", 0.0);
		
		// test 1 - probabilities at |+⟩ 
		qa.h();
		assertProbabilities("Probabilities |+⟩", 0.5);
		
		// test 2 - probabilities at |-⟩ 
		qa.z();
		assertProbabilities("Probabilities |-⟩", 0.5);
		
		// test 3 - probabilities at |1⟩ 
		qa.h();
		assertProbabilities("Probabilities |1⟩", 1.0);
		
		// test 4 - probabilities at rx(pi/3) on |1⟩ 
		qa.rx(PI/3.0);
		assertProbabilities("Probabilities Rx(pi/3) |1⟩", 0.75);
		
		// test 5 - probabilities at rx(2*pi/3) on |1⟩ 
		qa.rx(PI/3.0);
		assertProbabilities("Probabilities Rx(2pi/3) |1⟩", 0.25);
	}

	/**
	 * Asserts that the x-, y-, and z-value of the test qubit are
	 * equal or close to their expected values.
	 * <p>
	 * Also asserts that the polar angle theta and the phase are
	 * equal or close to their expected values
	 * (which are calculated from the expected x-, y-, and z-value).
	 * 
	 * @param heading	common header that identifies the test
	 * @param expectedX	expected value for x
	 * @param expectedY	expected value for y
	 * @param expectedZ	expected value for z
	 * @see				#assertIsClose(String, double, double)
	 */
	public final void assertXYZ(String heading, double expectedX, double expectedY, double expectedZ) {
		// assert x, y and z
		assertXYZ(heading, expectedX, expectedY, expectedZ, qa);
		
		// assert theta and phase (only if previous assertion passed)
		assertAll(heading,
			() -> assertIsClose("theta", Math.acos(expectedZ), qa.getTheta(), MAX_TOLERANCE),
			() -> {
				if (Math.hypot(expectedX, expectedY) > 1e-6) {
					double phase = qa.getPhase();
					if (phase < (MAX_TOLERANCE - PI)) phase += TWO_PI;
					assertIsClose("phase", Math.atan2(expectedY, expectedX), phase, MAX_TOLERANCE);
				}
			}
		);
	}

	/**
	 * Asserts that the measurement probabilities of the test qubit
	 * are equal or close to their expected values.
	 * <p>
	 * Also asserts that the test qubit is always pure, not mixed,
	 * because this test class works with a single qubit.
	 * <p>
	 * Only the probability of measuring |1⟩ needs to be passed
	 * as an argument, because the probability of measuring |0⟩
	 * is always equal to (1 minus the probability of measuring |1⟩,
	 * by definition.
	 * 
	 * @param heading				common header that identifies the test
	 * @param expectedProbability1	the probability of measuring |1⟩
	 */
	public final void assertProbabilities(String heading, double expectedProbability1) {
		double expectedProbability0 = 1.0 - expectedProbability1;
		assertAll(heading,
			() -> assertIsClose("probability1", expectedProbability1, qa.getProbability1(), MIN_TOLERANCE),
			() -> assertIsClose("probability0", expectedProbability0, qa.getProbability0(), MIN_TOLERANCE),
			() -> assertIsClose("magnitude", 1.0, qa.getMagnitude(), MIN_TOLERANCE),
			() -> assertIsClose("purity", 1.0, qa.getPurity(), MIN_TOLERANCE),
			() -> assertTrue(qa.isPure(), msg("isPure")),
			() -> assertFalse(qa.isMixed(), msg("isMixed"))
		);
	}

}
