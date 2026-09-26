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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.bemuisoft.quantum.api.IQuantumState;
import org.bemuisoft.quantum.api.IQubitAnalyzer;
import org.bemuisoft.quantum.api.IQubitFactory;
import org.junit.jupiter.api.Test;

/**
 * Tests the optional methods of the {@link IQubitAnalyzer}
 * interface in a two-qubit system,
 * as far as they are supported.
 * 
 * @param <Q>	the type of qubit to be tested
 * 
 * @author Benno Muilwijk
 */
public class TwoQubitAnalyzerExtraTest<Q extends IQubitAnalyzer> extends AbstractQubitTest<Q> {

	/** Qubit A. */
	protected Q qa;
	/** Qubit B. */
	protected Q qb;

	/**
	 * Default constructor.
	 */
	public TwoQubitAnalyzerExtraTest() {
		super(2);
	}

	@Override
	public final TwoQubitAnalyzerExtraTest<Q> init(IQubitFactory<Q> factory) {
		super.init(factory);
		qa = q(0);
		qb = q(1);
		return this;
	}

	@Override
	public void runAll() {
		assertAll(getHeading(),
			run(() -> testLabel()),
			run(() -> testSystemState()),
			run(() -> testComponentValues())
		);
	}

	/**
	 * Asserts that if a qubit label is returned, it
	 * is equal to, or ends with, the assigned label.
	 */
	@Test
	public void testLabel() {
		assumeFalse(qa.getLabel().isEmpty(), "No label assigned.");
		assertTrue(qa.getLabel().endsWith("A"), () -> "Unexpected label: " + qa.getLabel());
		assertTrue(qb.getLabel().endsWith("B"), () -> "Unexpected label: " + qb.getLabel());
	}

	/**
	 * Asserts that if a system state is returned, it has
	 * four dimensions for this two-qubit system test.
	 * <p>
	 * Also asserts that the amplitudes in those dimension
	 * evolve as expected.
	 * 
	 * @see #testComponentValues()
	 */
	@Test
	public void testSystemState() {
		assumeFalse(qa.getSystemState() == null, "No system state.");
		assertIsEqual("System state size", 4, qa.getSystemState().size());
		Q ctrl = qa;
		Q trgt = qb;
		if (qa.getSystemState().isRightToLeft()) {
			// switch control and target roles
			ctrl = qb;
			trgt = qa;
		}
		
		// test 0 - |00⟩ (initial state)
		assertAmplitude("Initial state |00⟩ - 00", 1.0, 0.0, 0);
		assertAmplitude("Initial state |00⟩ - 01", 0.0, 0.0, 1);
		assertAmplitude("Initial state |00⟩ - 10", 0.0, 0.0, 2);
		assertAmplitude("Initial state |00⟩ - 11", 0.0, 0.0, 3);
		
		// test 1 - |+»⟩ (before controlled Z)
		ctrl.h();
		trgt.h().s();
		assertAmplitude("State |+»⟩ - 00", 0.25, 0.0, 0);
		assertAmplitude("State |+»⟩ - 01", 0.25, 0.5, 1);
		assertAmplitude("State |+»⟩ - 10", 0.25, 0.0, 2);
		assertAmplitude("State |+»⟩ - 11", 0.25, 0.5, 3);
		
		// test 2 - after controlled Z
		trgt.cz(ctrl);
		assertAmplitude("After controlled Z - 00", 0.25,  0.0, 0);
		assertAmplitude("After controlled Z - 01", 0.25,  0.5, 1);
		assertAmplitude("After controlled Z - 10", 0.25,  0.0, 2);
		assertAmplitude("After controlled Z - 11", 0.25, -0.5, 3);
		
		// test 3 - after RX on target qubit
		trgt.rx(HALF_PI);
		assertAmplitude("After RX on target - 00", 0.5,  0.0, 0);
		assertAmplitude("After RX on target - 01", 0.0,  0.0, 1);
		assertAmplitude("After RX on target - 10", 0.0,  0.0, 2);
		assertAmplitude("After RX on target - 11", 0.5, -0.5, 3);
		
		// test 4 - after controlled X
		trgt.cx(ctrl);
		assertAmplitude("After controlled X - 00", 0.5,  0.0, 0);
		assertAmplitude("After controlled X - 01", 0.0,  0.0, 1);
		assertAmplitude("After controlled X - 10", 0.5, -0.5, 2);
		assertAmplitude("After controlled X - 11", 0.0,  0.0, 3);
		assertXYZ("State |«⟩ - ctrl", ZERO, -1.0, ZERO, ctrl, 0);
		assertXYZ("State |«⟩ - ctrl", ZERO,  0.0, ZERO, ctrl, 1);
		assertXYZ("State |0⟩ - trgt", ZERO, ZERO,  0.5, trgt, 0);
		assertXYZ("State |0⟩ - trgt", ZERO, ZERO,  0.5, trgt, 1);
	}

	/**
	 * Asserts that if the test qubit has components,
	 * the x-, y-, and z-value of the components
	 * have expected values.
	 * <p>
	 * This test assumes that the components represent
	 * conditional Bloch vectors and that the x-, y- and
	 * z-values are weighted values, that is,
	 * their normalized values multiplied by probability.
	 */
	@Test
	public void testComponentValues() {
		assumeTrue(qa.components() > 0, "No components.");
		assumeTrue(qa.components() > 1, "Not enough components.");		// allow for extra "hidden" component(s)
		
		// test 0 - |00⟩ (initial state)
		assertXYZ("Initial state |0⟩ - qa", ZERO, ZERO, PLUS, qa, 0);	// Bloch vector if qb is measured as |0⟩ * probability 1
		assertXYZ("Initial state |0⟩ - qa", ZERO, ZERO, ZERO, qa, 1);	// Bloch vector if qb is measured as |1⟩ * probability 0
		assertXYZ("Initial state |0⟩ - qb", ZERO, ZERO, PLUS, qb, 0);	// Bloch vector if qa is measured as |0⟩ * probability 1
		assertXYZ("Initial state |0⟩ - qb", ZERO, ZERO, ZERO, qb, 1);	// Bloch vector if qa is measured as |1⟩ * probability 0
		
		// test 1 - |+»⟩ (before controlled Z)
		qa.h();
		qb.h().s();
		assertXYZ("State |+⟩ - qa", 0.5, ZERO, ZERO, qa, 0);
		assertXYZ("State |+⟩ - qa", 0.5, ZERO, ZERO, qa, 1);
		assertXYZ("State |»⟩ - qb", ZERO, 0.5, ZERO, qb, 0);
		assertXYZ("State |»⟩ - qb", ZERO, 0.5, ZERO, qb, 1);
		
		// test 2 - after controlled Z
		qb.cz(qa);
		assertXYZ("State |+⟩ - qa",  0.5, ZERO, ZERO, qa, 0);
		assertXYZ("State |-⟩ - qa", -0.5, ZERO, ZERO, qa, 1);
		assertXYZ("State |»⟩ - qb", ZERO,  0.5, ZERO, qb, 0);
		assertXYZ("State |«⟩ - qb", ZERO, -0.5, ZERO, qb, 1);
		
		// test 3 - after SX on qubit B
		// qubit B's components rotate as expected for Sx
		// XXX but note that qubit A's components are dragged along!
		// this is the essence of entanglement in a nutshell
		qb.sx();
		assertXYZ("State |1⟩ - qa", ZERO, ZERO,  0.5, qa, 0);
		assertXYZ("State |0⟩ - qa", ZERO, ZERO, -0.5, qa, 1);
		assertXYZ("State |1⟩ - qb", ZERO, ZERO,  0.5, qb, 0);
		assertXYZ("State |0⟩ - qb", ZERO, ZERO, -0.5, qb, 1);
		
		// test 4 - after controlled X
		// target qubit B returns to state |0⟩
		// XXX but note the phase of control qubit A!
		// phase information is not lost, even in Bell type states
		qb.cx(qa);
		assertXYZ("State |«⟩ - qa", ZERO, -1.0, ZERO, qa, 0);
		assertXYZ("State |«⟩ - qa", ZERO,  0.0, ZERO, qa, 1);
		assertXYZ("State |0⟩ - qb", ZERO, ZERO,  0.5, qb, 0);
		assertXYZ("State |0⟩ - qb", ZERO, ZERO,  0.5, qb, 1);
	}

	/**
	 * Asserts that the x-, y-, and z-value of the specified component
	 * are equal or close to their expected values.
	 * 
	 * @param heading	common header that identifies the test
	 * @param expectedX	expected value for x
	 * @param expectedY	expected value for y
	 * @param expectedZ	expected value for z
	 * @param q			the test qubit
	 * @param i			the component index
	 * @see				#assertIsClose(Supplier, double, double)
	 */
	public final void assertXYZ(String heading, double expectedX, double expectedY, double expectedZ, Q q, int i) {
		assertAll(heading,
			() -> assertIsClose(msg("z", i), expectedZ, q.getZ(i)),
			() -> assertIsClose(msg("x", i), expectedX, q.getX(i)),
			() -> assertIsClose(msg("y", i), expectedY, q.getY(i))
		);
	}

	/**
	 * Asserts that the probability, magnitude and phase of the amplitude
	 * in the specified quantum system state dimension
	 * are equal or close to their expected values.
	 * 
	 * @param heading				common header that identifies the test
	 * @param expectedProbability	expected probability
	 * @param expectedPhase			expected phase as a fraction of pi
	 * @param i						the amplitude index
	 * @see	#assertIsClose(Supplier, double, double, double)
	 */
	public final void assertAmplitude(String heading, double expectedProbability, double expectedPhase, int i) {
		IQuantumState state = qa.getSystemState();
		double expectedMagnitude = Math.sqrt(expectedProbability);
		if (state.hasStrictPhases()) {
			assertAll(heading,
				() -> assertEquals(expectedProbability, state.getProbability(i), MAX_TOLERANCE, msg("probability", i)),
				() -> assertEquals(expectedMagnitude, state.getMagnitude(i), MAX_TOLERANCE, msg("magnitude", i)),
				() -> assertEquals(expectedPhase, state.getPhase(i)/PI, MAX_TOLERANCE, msg("phase/π", i))
			);
			return;
		}
		
		// test only relative phases
		if (i == 0) {
			// reset global phase (test must always assert dimension 0 first)
			globalPhase = Double.NaN;
		}
		if (expectedProbability == 0.0) {
			// do not assert the phase in dimensions with zero probability
			assertAll(heading,
				() -> assertEquals(expectedProbability, state.getProbability(i), MAX_TOLERANCE, msg("probability", i)),
				() -> assertEquals(expectedMagnitude, state.getMagnitude(i), MAX_TOLERANCE, msg("magnitude", i))
			);
			return;
		}
		if (Double.isNaN(globalPhase)) {
			// determine difference between actual and expected phase on first dimension with non-zero probability
			globalPhase = mod2Pi(state.getPhase(i) - expectedPhase*PI);
		}
		// adjusted phase should be equal to the expected phase for all dimensions with non-zero probability
		double adjustedPhase = mod2Pi(state.getPhase(i) - globalPhase);
		assertAll(heading,
			() -> assertEquals(expectedProbability, state.getProbability(i), MAX_TOLERANCE, msg("probability", i)),
			() -> assertEquals(expectedMagnitude, state.getMagnitude(i), MAX_TOLERANCE, msg("magnitude", i)),
			() -> assertEquals(expectedPhase, adjustedPhase/PI, MAX_TOLERANCE, msg("phase/π", i))
		);
	}

	private double globalPhase;

}
