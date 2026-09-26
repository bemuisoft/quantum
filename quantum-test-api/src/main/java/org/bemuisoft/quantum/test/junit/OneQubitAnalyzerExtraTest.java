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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.bemuisoft.quantum.api.IQubitAnalyzer;
import org.bemuisoft.quantum.api.IQubitFactory;
import org.junit.jupiter.api.Test;

/**
 * Tests the optional methods of the {@link IQubitAnalyzer}
 * interface in a single qubit system,
 * as far as they are supported.
 * 
 * @param <Q>	the type of qubit to be tested
 * 
 * @author Benno Muilwijk
 */
public class OneQubitAnalyzerExtraTest<Q extends IQubitAnalyzer> extends AbstractQubitTest<Q> {

	/** Qubit A. */
	protected Q qa;

	/**
	 * Default constructor.
	 */
	public OneQubitAnalyzerExtraTest() {
		super(1);
	}

	@Override
	public final OneQubitAnalyzerExtraTest<Q> init(IQubitFactory<Q> factory) {
		super.init(factory);
		qa = q(0);
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
	}

	/**
	 * Asserts that if a system state is returned, it has
	 * two dimensions for this single qubit system test.
	 */
	@Test
	public void testSystemState() {
		assumeFalse(qa.getSystemState() == null, "No system state.");
		assertIsEqual("System state size", 2, qa.getSystemState().size());
	}

	/**
	 * Asserts that if the test qubit has components,
	 * the x-, y-, and z-value of the test qubit
	 * match those of the first component
	 * in this single qubit system test.
	 */
	@Test
	public void testComponentValues() {
		assumeTrue(qa.components() > 0, "No components.");
		
		// test 0 - |0⟩ (initial state)
		assertXYZ("Initial state |0⟩", ZERO, ZERO, PLUS);
		
		// test 1 - |+⟩
		qa.h();
		assertXYZ("State |+⟩", PLUS, ZERO, ZERO);
		
		// test 2 - |»⟩
		qa.s();
		assertXYZ("State |»⟩", ZERO, PLUS, ZERO);
		
		// test 3 - |-⟩
		qa.s();
		assertXYZ("State |-⟩", MINUS, ZERO, ZERO);
		
		// test 4 - |«⟩
		qa.s();
		assertXYZ("State |«⟩", ZERO, MINUS, ZERO);
		
		// test 5 - |1⟩
		qa.sx();
		assertXYZ("State |1⟩", ZERO, ZERO, MINUS);
	}

	/**
	 * Asserts that the x-, y-, and z-value of the test qubit are
	 * equal or close to their expected values.
	 * <p>
	 * Also asserts the same for its component at index 0.
	 * 
	 * @param heading	common header that identifies the test
	 * @param expectedX	expected value for x
	 * @param expectedY	expected value for y
	 * @param expectedZ	expected value for z
	 * @see				#assertIsClose(String, double, double)
	 */
	public final void assertXYZ(String heading, double expectedX, double expectedY, double expectedZ) {
		// assert x, y and z at qubit level
		assertXYZ(heading, expectedX, expectedY, expectedZ, qa);
		
		// assert x, y and z at component level
		assertAll(heading,
			() -> assertIsClose("Component z(0)", expectedZ, qa.getZ(0)),
			() -> assertIsClose("Component x(0)", expectedX, qa.getX(0)),
			() -> assertIsClose("Component y(0)", expectedY, qa.getY(0))
		);
	}

}
