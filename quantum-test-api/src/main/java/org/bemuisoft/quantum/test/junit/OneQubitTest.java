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

import org.bemuisoft.quantum.api.Axis;
import org.bemuisoft.quantum.api.IQubit;
import org.bemuisoft.quantum.api.IQubitFactory;
import org.junit.jupiter.api.Test;

/**
 * Tests the {@link IQubit} interface in a single qubit system.
 * 
 * @param <Q>	the type of qubit to be tested
 * 
 * @author Benno Muilwijk
 */
public class OneQubitTest<Q extends IQubit> extends AbstractQubitTest<Q> {

	/** Qubit A. */
	protected Q qa;

	/**
	 * Default constructor.
	 */
	public OneQubitTest() {
		super(1);
	}

	@Override
	public final OneQubitTest<Q> init(IQubitFactory<Q> factory) {
		super.init(factory);
		qa = q(0);
		return this;
	}

	@Override
	public void runAll() {
		assertAll(getHeading(),
			run(() -> testInitialState()),
			run(() -> testReset()),
			run(() -> testHZH()),
			run(() -> testHSH()),
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
			run(() -> testNOT())
		);
	}

	/**
	 * Asserts that the initial state is |0⟩.
	 */
	@Test
	public void testInitialState() {
		// initial state should be |0⟩
		assertIsEqual("Initial state", 0, qa.measure());
	}

	/**
	 * Asserts that reset gives |0⟩.
	 */
	@Test
	public void testReset() {
		// test 0 - reset from |0⟩ (initial state)
		qa.reset();
		assertIsEqual("Reset from |0⟩", 0, qa.measure());
		
		// test 1 - reset from |1⟩
		qa.x();
		qa.reset();
		assertIsEqual("Reset from |1⟩", 0, qa.measure());
		
		// test 2 - reset from |+⟩ (defined as (|0⟩ + |1⟩)/sqrt(2))
		qa.h();
		qa.reset();
		assertIsEqual("Reset from |+⟩", 0, qa.measure());
		
		// test 3 - reset from |-⟩ (defined as (|0⟩ - |1⟩)/sqrt(2))
		qa.x().h();
		qa.reset();
		assertIsEqual("Reset from |-⟩", 0, qa.measure());
		
		// test 4 - reset from |»⟩ (defined here as (|0⟩ + i|1⟩)/sqrt(2))
		qa.h().s();
		qa.reset();
		assertIsEqual("Reset from |»⟩", 0, qa.measure());
		
		// test 5 - reset from |«⟩ (defined here as (|0⟩ - i|1⟩)/sqrt(2))
		qa.h().sdg();
		qa.reset();
		assertIsEqual("Reset from |«⟩", 0, qa.measure());
	}

	/**
	 * Asserts that the Hadamard gate works as expected
	 * in combination with the Pauli-Z gate.
	 */
	@Test
	public void testHZH() {
		// test 0
		qa.h();		// h on |0⟩ should give |+⟩
		qa.z();		// z on |+⟩ should give |-⟩
		qa.h();		// h on |-⟩ should give |1⟩
		assertIsEqual("HZH from |0⟩", 1, qa.measure());
		
		// test 1
		qa.h();		// h on |1⟩ should give |-⟩
		qa.z();		// z on |-⟩ should give |+⟩
		qa.h();		// h on |+⟩ should give |0⟩
		assertIsEqual("HZH from |1⟩", 0, qa.measure());
	}

	/**
	 * Asserts that the Hadamard gate works as expected
	 * in combination with the S gate.
	 */
	@Test
	public void testHSH() {
		// test 0
		qa.h();		// h on |0⟩ should give |+⟩
		qa.s();		// s on |+⟩ should give |»⟩
		qa.h();		// h on |»⟩ should give |«⟩
		qa.s();		// s on |«⟩ should give |+⟩
		qa.h();		// h on |+⟩ should give |0⟩
		assertIsEqual("HSHSH from |0⟩", 0, qa.measure());
		
		// test 1
		qa.x();		// x on |0⟩ should give |1⟩
		qa.h();		// h on |1⟩ should give |-⟩
		qa.s();		// s on |-⟩ should give |«⟩
		qa.h();		// h on |«⟩ should give |»⟩
		qa.s();		// s on |»⟩ should give |-⟩
		qa.h();		// h on |-⟩ should give |1⟩
		assertIsEqual("HSHSH from |1⟩", 1, qa.measure());
	}

	/**
	 * Asserts that the Pauli-X gate works as expected
	 * in combination with the Hadamard and S gates.
	 */
	@Test
	public void testX() {
		// test 0 - x on |0⟩ should give |1⟩
		qa.x();
		assertIsEqual("X from |0⟩", 1, qa.measure());
		
		// test 1 - x on |-⟩ should give |-⟩
		qa.h();
		qa.x();
		qa.h();
		assertIsEqual("HXH from |1⟩", 1, qa.measure());
		
		// test 2 - x on |«⟩ should give |»⟩
		qa.h().s();
		qa.x();
		qa.s().h();
		assertIsEqual("HSXSH from |1⟩", 1, qa.measure());
		
		// test 3 - x on |1⟩ should give |0⟩
		qa.x();
		assertIsEqual("X from |1⟩", 0, qa.measure());
		
		// test 4 - x on |+⟩ should give |+⟩
		qa.h();
		qa.x();
		qa.h();
		assertIsEqual("HXH from |0⟩", 0, qa.measure());
		
		// test 5 - x on |»⟩ should give |«⟩
		qa.h().s();
		qa.x();
		qa.s().h();
		assertIsEqual("HSXSH from |0⟩", 0, qa.measure());
	}

	/**
	 * Asserts that the Pauli-Y gate works as expected
	 * in combination with the Hadamard and S gates.
	 */
	@Test
	public void testY() {
		// test 0 - y on |0⟩ should give |1⟩
		qa.y();
		assertIsEqual("Y from |0⟩", 1, qa.measure());
		
		// test 1 - y on |1⟩ should give |0⟩
		qa.y();
		assertIsEqual("Y from |1⟩", 0, qa.measure());
		
		// test 2 - y on |+⟩ should give |-⟩
		qa.h();
		qa.y();
		qa.h();
		assertIsEqual("HYH from |0⟩", 1, qa.measure());
		
		// test 3 - y on |-⟩ should give |+⟩
		qa.h();
		qa.y();
		qa.h();
		assertIsEqual("HYH from |1⟩", 0, qa.measure());
		
		// test 4 - y on |»⟩ should give |»⟩
		qa.h().s();
		qa.y();
		qa.s().h();
		assertIsEqual("HSYSH from |0⟩", 1, qa.measure());
		
		// test 5 - y on |«⟩ should give |«⟩
		qa.h().s();
		qa.y();
		qa.s().h();
		assertIsEqual("HSYSH from |1⟩", 0, qa.measure());
	}

	/**
	 * Asserts that the Pauli-Z gate works as expected
	 * in combination with the Hadamard and S gates.
	 */
	@Test
	public void testZ() {
		// test 0 - z on |0⟩ should give |0⟩
		qa.z();
		assertIsEqual("Z from |0⟩", 0, qa.measure());
		
		// test 1 - z on |+⟩ should give |-⟩
		qa.h();
		qa.z();
		qa.h();
		assertIsEqual("HZH from |0⟩", 1, qa.measure());
		
		// test 2 - z on |«⟩ should give |»⟩
		qa.h().s();
		qa.z();
		qa.s().h();
		assertIsEqual("HSZSH from |1⟩", 1, qa.measure());
		
		// test 3 - z on |1⟩ should give |1⟩
		qa.z();
		assertIsEqual("Z from |1⟩", 1, qa.measure());
		
		// test 4 - z on |-⟩ should give |+⟩
		qa.h();
		qa.z();
		qa.h();
		assertIsEqual("HZH from |1⟩", 0, qa.measure());
		
		// test 5 - z on |»⟩ should give |«⟩
		qa.h().s();
		qa.z();
		qa.s().h();
		assertIsEqual("HSZSH from |0⟩", 0, qa.measure());
	}

	/**
	 * Asserts that the S gate works as expected
	 * in combination with the Hadamard gate.
	 */
	@Test
	public void testS() {
		// test 0 - s on |0⟩ should give |0⟩
		qa.s();
		assertIsEqual("S from |0⟩", 0, qa.measure());
		
		// test 1 - s on |+⟩ should give |»⟩
		// test 2 - s on |»⟩ should give |-⟩
		qa.h();
		qa.s();
		qa.s();
		qa.h();
		assertIsEqual("HSSH from |0⟩", 1, qa.measure());
		
		// test 3 - s on |1⟩ should give |1⟩
		qa.s();
		assertIsEqual("S from |1⟩", 1, qa.measure());
		
		// test 4 - s on |-⟩ should give |«⟩
		// test 5 - s on |«⟩ should give |+⟩
		qa.h();
		qa.s();
		qa.s();
		qa.h();
		assertIsEqual("HSSH from |1⟩", 0, qa.measure());
	}

	/**
	 * Asserts that the S-dagger gate works as expected
	 * in combination with the Hadamard and S gates.
	 */
	@Test
	public void testSdg() {
		// test 0 - sdg on |0⟩ should give |0⟩
		qa.sdg();
		assertIsEqual("S† from |0⟩", 0, qa.measure());
		
		// test 1 - sdg on |»⟩ should give |+⟩	(inverse of S)
		qa.h();
		qa.s();
		qa.sdg();
		qa.h();
		
		// test 2 - sdg on |+⟩ should give |«⟩
		// test 3 - sdg on |«⟩ should give |-⟩
		qa.h();
		qa.sdg();
		qa.sdg();
		qa.h();
		assertIsEqual("HS†S†H from |0⟩", 1, qa.measure());
		
		// test 4 - sdg on |1⟩ should give |1⟩
		qa.sdg();
		assertIsEqual("S† from |1⟩", 1, qa.measure());
		
		// test 5 - sdg on |-⟩ should give |»⟩	(inverse of S)
		qa.h();
		qa.sdg();
		qa.s();
		qa.h();
		assertIsEqual("HS†SH from |1⟩", 1, qa.measure());
		
		// test 6 - sdg on |-⟩ should give |»⟩
		// test 7 - sdg on |»⟩ should give |+⟩
		qa.h();
		qa.sdg();
		qa.sdg();
		qa.h();
		assertIsEqual("HS†S†H from |1⟩", 0, qa.measure());
	}

	/**
	 * Asserts that the T gate works as expected
	 * in combination with the Hadamard gate.
	 */
	@Test
	public void testT() {
		// test 0 - t on |0⟩ should give |0⟩
		qa.t();
		assertIsEqual("T from |0⟩", 0, qa.measure());
		
		// test 1 - tt on |+⟩ should give |»⟩
		// test 2 - tt on |«⟩ should give |+⟩
		qa.h();
		qa.t().t();		// tt on |+⟩ should give |»⟩
		qa.h();			// h  on |»⟩ should give |«⟩
		qa.t().t();		// tt on |«⟩ should give |+⟩
		qa.h();
		assertIsEqual("HTTHTTH from |0⟩", 0, qa.measure());
		
		// test 3 - tttt == ss == z
		qa.h();
		qa.t().t();		// tt on |+⟩ should give |»⟩
		qa.t().t();		// tt on |»⟩ should give |-⟩
		qa.h();
		assertIsEqual("HTTTTH from |0⟩", 1, qa.measure());
		
		// test 4 - t on |1⟩ should give |1⟩
		qa.t();
		assertIsEqual("T from |1⟩", 1, qa.measure());
		
		// test 5 - tt on |-⟩ should give |«⟩
		qa.h();
		qa.t().t();		// tt on |-⟩ should give |«⟩
		qa.h();			// h  on |«⟩ should give |»⟩
		qa.t().t();		// tt on |»⟩ should give |-⟩
		qa.h();
		assertIsEqual("HTTHTTH from |1⟩", 1, qa.measure());
	}

	/**
	 * Asserts that the T-dagger gate works as expected
	 * in combination with the Hadamard and other gates.
	 */
	@Test
	public void testTdg() {
		// test 0 - tdg on |0⟩ should give |0⟩
		qa.tdg();
		assertIsEqual("T† from |0⟩", 0, qa.measure());
		
		// test 1 - tdg is inverse of t
		qa.h();
		qa.t();
		qa.tdg();
		qa.h();
		assertIsEqual("HTT†H from |0⟩", 0, qa.measure());
		
		// test 2 - tdg² on |+⟩ should give |«⟩
		qa.h();
		qa.tdg().tdg();
		qa.sdg();
		qa.h();
		assertIsEqual("HT†T†S†H from |0⟩", 1, qa.measure());
		
		// test 3 - tdg on |1⟩ should give |1⟩
		qa.tdg();
		assertIsEqual("T† from |1⟩", 1, qa.measure());
		
		// test 4 - tdg is inverse of t (also in reverse order and different state)
		qa.h();
		qa.tdg();
		qa.t();
		qa.h();
		assertIsEqual("HT†TH from |1⟩", 1, qa.measure());
		
		// test 5 - tdg² on |«⟩ should give |-⟩
		qa.h();
		qa.sdg();			// sdg  on |-⟩ should give |»⟩
		qa.h();				// h    on |»⟩ should give |«⟩
		qa.tdg().tdg();		// tdg² on |«⟩ should give |-⟩
		qa.h();
		assertIsEqual("HS†HT†T†H from |1⟩", 1, qa.measure());
	}

	/**
	 * Asserts that the P gate works as expected
	 * in combination with the Hadamard and X gate.
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
		// test 0 - p(pi) on |0⟩ should give |0⟩
		qa.p(PI);
		assertIsEqual("Rz(pi) from |0⟩", 0, qa.measure());
		
		// test 1 - p(pi) on |+⟩ should give |-⟩
		qa.h();
		qa.p(PI);
		qa.h();
		assertIsEqual("HP(pi)H from |0⟩", 1, qa.measure());
		
		// test 2 - p(pi/2) on |1⟩ should give |1⟩
		qa.p(HALF_PI);
		assertIsEqual("P(pi) from |1⟩", 1, qa.measure());
		
		// test 3 - p(pi/2) on |-⟩ should give |«⟩
		// test 4 - p(pi/2) on |«⟩ should give |+⟩
		qa.h();
		qa.p(HALF_PI);
		qa.p(HALF_PI);
		qa.h();
		assertIsEqual("HP²(pi/2)H from |1⟩", 0, qa.measure());
	}

	/**
	 * Asserts that the Rz gate works as expected
	 * in combination with the Hadamard and X gate.
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
		// test 0 - rz(pi) on |0⟩ should give |0⟩
		qa.rz(PI);
		assertIsEqual("Rz(pi) from |0⟩", 0, qa.measure());
		
		// test 1 - rz(pi) on |+⟩ should give |-⟩
		qa.h();
		qa.rz(PI);
		qa.h();
		assertIsEqual("HRz(pi)H from |0⟩", 1, qa.measure());
		
		// test 2 - rz(pi/2) on |1⟩ should give |1⟩
		qa.rz(HALF_PI);
		assertIsEqual("Rz(pi) from |1⟩", 1, qa.measure());
		
		// test 3 - rz(pi/2) on |-⟩ should give |«⟩
		// test 4 - rz(pi/2) on |«⟩ should give |+⟩
		qa.h();
		qa.rz(HALF_PI);
		qa.rz(HALF_PI);
		qa.h();
		assertIsEqual("HRz²(pi/2)H from |1⟩", 0, qa.measure());
	}

	/**
	 * Asserts that the Ry gate works as expected
	 * in combination with the Hadamard and other gates.
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
		// test 0 - ry(pi) on |0⟩ should give |1⟩
		qa.ry(PI);
		assertIsEqual("Ry(pi) from |0⟩", 1, qa.measure());
		
		// test 1 - ry(pi/2) on |1⟩ should give |-⟩
		// test 2 - ry(pi/2) on |-⟩ should give |0⟩
		qa.ry(HALF_PI);
		qa.ry(HALF_PI);
		assertIsEqual("Ry²(pi/2) from |1⟩", 0, qa.measure());
		
		// test 3 - ry(pi/2) on |»⟩ should give |»⟩
		qa.h().s();
		qa.ry(PI);
		// test 4 - ry(pi/2) on |«⟩ should give |«⟩
		qa.z();
		qa.ry(HALF_PI);
		qa.s().h();
		assertIsEqual("HSRy(pi)ZRy(pi/2)SH from |0⟩", 0, qa.measure());
	}

	/**
	 * Asserts that the Rx gate works as expected
	 * in combination with the Hadamard and other gates.
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
		// test 0 - rx(pi) on |0⟩ should give |1⟩
		qa.rx(PI);
		assertIsEqual("Rx(pi) from |0⟩", 1, qa.measure());
		
		// test 1 - rx(pi/2) on |1⟩ should give |»⟩
		// test 2 - rx(pi/2) on |»⟩ should give |0⟩
		qa.rx(HALF_PI);
		qa.rx(HALF_PI);
		assertIsEqual("Rx²(pi/2) from |1⟩", 0, qa.measure());
		
		// test 3 - rx(pi) on |+⟩ should give |+⟩
		qa.h();
		qa.rx(PI);
		// test 4 - rx(pi/2) on |-⟩ should give |-⟩
		qa.z();
		qa.rx(HALF_PI);
		qa.h();
		assertIsEqual("HRx(pi)ZRx(pi/2)H from |0⟩", 1, qa.measure());
	}

	/**
	 * Asserts that the Sx gate works as expected
	 * in combination with the Hadamard and other gates.
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
		// test 1 - sx on |«⟩ should give |1⟩
		qa.sx();
		qa.sx();
		assertIsEqual("Sx² from |0⟩", 1, qa.measure());
		
		// test 2 - sx on |-⟩ should give |-⟩
		qa.h();
		qa.rx(PI);
		qa.h();
		assertIsEqual("HSxH from |1⟩", 1, qa.measure());
		
		// test 3 - sx on |1⟩ should give |»⟩
		// test 4 - sx on |»⟩ should give |0⟩
		qa.sx();
		qa.sx();
		assertIsEqual("Sx² from |1⟩", 0, qa.measure());
		
		// test 5 - sx on |+⟩ should give |+⟩
		qa.h();
		qa.rx(PI);
		qa.h();
		assertIsEqual("HSxH from |0⟩", 0, qa.measure());
	}

	/**
	 * Asserts that the Sx-dagger gate works as expected
	 * in combination with the Hadamard and other gates.
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
		// test 1 - sxdg on |»⟩ should give |1⟩
		qa.sx();
		qa.sx();
		assertIsEqual("Sx†² from |0⟩", 1, qa.measure());
		
		// test 2 - sxdg is inverse of sx
		qa.sx();
		qa.sxdg();
		assertIsEqual("SxSx† from |1⟩", 1, qa.measure());
		
		// test 3 - sxdg on |-⟩ should give |-⟩
		qa.h();
		qa.rx(PI);
		qa.h();
		assertIsEqual("HSx†H from |1⟩", 1, qa.measure());
		
		// test 4 - sxdg on |1⟩ should give |«⟩
		// test 5 - sxdg on |«⟩ should give |0⟩
		qa.sx();
		qa.sx();
		assertIsEqual("Sx†² from |1⟩", 0, qa.measure());
		
		// test 6 - sxdg on |+⟩ should give |+⟩
		qa.h();
		qa.rx(PI);
		qa.h();
		assertIsEqual("HSx†H from |0⟩", 0, qa.measure());
		
		// test 7 - sxdg is inverse of sx (also in reverse order and different state)
		qa.sxdg();
		qa.sx();
		assertIsEqual("Sx†Sx from |0⟩", 0, qa.measure());
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
		assertIsEqual("R(X, pi) from |0⟩", 1, qa.measure());
		
		// test 1 - r(Y, pi) on |1⟩ should give |0⟩
		qa.r(Axis.Y, PI);
		assertIsEqual("R(Y, pi) from |1⟩", 0, qa.measure());
		
		// test 2 - r(H, pi) on |1⟩ should give |+⟩
		// test 3 - r(Z, pi) on |+⟩ should give |-⟩
		// test 4 - r(Y, pi/2) on |-⟩ should give |0⟩
		qa.r(Axis.H, PI);
		qa.r(Axis.Z, PI);
		qa.r(Axis.Y, HALF_PI);
		assertIsEqual("R(H, pi).R(Z, pi).R(Y, pi/2) from |0⟩", 0, qa.measure());
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
		// test 1 - u(pi/2, pi/2, pi/2) on |»⟩ should give |0⟩
		qa.u(HALF_PI, HALF_PI, HALF_PI);
		qa.u(HALF_PI, HALF_PI, HALF_PI);
		assertIsEqual("U²(pi/2, pi/2, pi/2) from |0⟩", 0, qa.measure());
		
		// test 2 - u(pi, 0, 0) on |0⟩ should give |1⟩
		qa.u(PI, ZERO, ZERO);
		assertIsEqual("U(pi, 0, 0) from |0⟩", 1, qa.measure());
		
		// test 3 - u(pi/2, pi/2, pi/2) on |1⟩ should give |«⟩
		// test 4 - u(pi/2, pi/2, pi/2) on |«⟩ should give |1⟩
		qa.u(HALF_PI, HALF_PI, HALF_PI);
		qa.u(HALF_PI, HALF_PI, HALF_PI);
		assertIsEqual("U²(pi/2, pi/2, pi/2) from |1⟩", 1, qa.measure());
		
		// test 5 - u(pi, 0, 0) on |1⟩ should give |0⟩
		qa.u(PI, ZERO, ZERO);
		assertIsEqual("U(pi, 0, 0) from |1⟩", 0, qa.measure());
		
		// test 6 - u(pi/2, pi, pi) on |0⟩ should give |-⟩
		// test 7 - u(pi/2, pi, pi) on |-⟩ should give |1⟩
		qa.u(HALF_PI, PI, PI);
		qa.u(HALF_PI, PI, PI);
		assertIsEqual("U²(pi/2, pi, pi) from |0⟩", 1, qa.measure());
		
		// test 8 - u(pi/2, -pi/2, 0) on |1⟩ should give |»⟩
		// test 9 - u(pi/2, pi, pi/2) on |»⟩ should give |0⟩
		qa.u(HALF_PI, -HALF_PI, ZERO);
		qa.u(HALF_PI, PI, HALF_PI);
		assertIsEqual("U(pi/2, -pi/2, 0).U(pi/2, pi, pi/2) from |1⟩", 0, qa.measure());
	}

	/**
	 * Asserts that the NOT gate works as expected
	 * in combination with the Hadamard and S gates.
	 * <p>
	 * The NOT gate should work exactly the same as
	 * the Pauli-X gate.
	 */
	@Test
	public void testNOT() {
		// test 0 - not on |0⟩ should give |1⟩
		qa.not();
		assertIsEqual("NOT from |0⟩", 1, qa.measure());
		
		// test 1 - not on |-⟩ should give |-⟩
		qa.h();
		qa.not();
		qa.h();
		assertIsEqual("H.NOT.H from |1⟩", 1, qa.measure());
		
		// test 2 - not on |«⟩ should give |»⟩
		qa.h().s();
		qa.not();
		qa.s().h();
		assertIsEqual("HS.NOT.SH from |1⟩", 1, qa.measure());
		
		// test 3 - not on |1⟩ should give |0⟩
		qa.not();
		assertIsEqual("NOT from |1⟩", 0, qa.measure());
		
		// test 4 - not on |+⟩ should give |+⟩
		qa.h();
		qa.not();
		qa.h();
		assertIsEqual("H.NOT.H from |0⟩", 0, qa.measure());
		
		// test 5 - not on |»⟩ should give |«⟩
		qa.h().s();
		qa.not();
		qa.s().h();
		assertIsEqual("HS.NOT.SH from |0⟩", 0, qa.measure());
	}

}
