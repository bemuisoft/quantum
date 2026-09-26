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
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.bemuisoft.quantum.api.IQubit;
import org.bemuisoft.quantum.api.IQubitFactory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * A template for qubit test classes.
 * 
 * @param <Q>	the type of qubit to be tested
 * 
 * @author Benno Muilwijk
 */
public class ATestTemplate<Q extends IQubit> extends AbstractQubitTest<Q> {

	/** Qubit A. */
	protected Q qa;

	/**
	 * Default constructor.
	 */
	public ATestTemplate() {
		super(1);
	}

	@Override
	public final ATestTemplate<Q> init(IQubitFactory<Q> factory) {
		super.init(factory);
		qa = q(0);
		return this;
	}

	@Override
	public void runAll() {
		assertAll(getHeading(),
			run(() -> succeedingTest()),
//			run(() -> failingTest()),
			run(() -> abortedTest())
		);
	}

	/**
	 * Asserts that ...
	 */
	@Test
	public void succeedingTest() {
		qa.reset();
		assertIsEqual("label", PLUS, PLUS);
	}

	/**
	 * Asserts that ...
	 */
	@Test
	@Disabled("for demonstration purposes")
	public void failingTest() {
		// not executed
		fail("a failing test");
	}

	/**
	 * Asserts that ...
	 */
	@Test
	public void abortedTest() {
		assumeTrue("abc".contains("Z"));
		fail("test should have been aborted");
	}

}
