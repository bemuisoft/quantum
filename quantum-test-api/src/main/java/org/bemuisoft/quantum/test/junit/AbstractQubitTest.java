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

import java.util.function.Supplier;

import org.bemuisoft.quantum.api.Base;
import org.bemuisoft.quantum.api.IQubit;
import org.bemuisoft.quantum.api.IQubitAnalyzer;
import org.bemuisoft.quantum.api.IQubitFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.function.Executable;

/**
 * Base class for all qubit test classes in this package.
 * <p>
 * The idea is to allow all standard test classes to be
 * used in two ways, as best suited for the type of qubit:
 * <ol>
 * <li>As superclass to adopt all test methods
 * (as-is or with override). In this case the
 * subclass should override {@link #getFactory(int)}
 * to return a qubit factory for a specific type of qubit.
 * Alternatively, the subclass should have the possibility
 * to override {@link #init(IQubitFactory)}
 * to initialize the test qubits in another way.</li>
 * <li>As wrapped class, so that {@link #runAll()} or
 * selected test methods can be executed.
 * In this case, the wrapping class must invoke
 * {@link #init(IQubitFactory)} to initialize
 * the test qubits.</li>
 * </ol>
 * 
 * @param <Q>	the type of qubit used in the test
 * 
 * @author Benno Muilwijk
 */
abstract class AbstractQubitTest<Q extends IQubit> implements Base {

	/** Real number  0.0 with double precision. */
	public static final double ZERO = 0.0;
	/** Real number  1.0 with double precision. */
	public static final double PLUS = 1.0;
	/** Real number -1.0 with double precision. */
	public static final double MINUS = -1.0;

	/** Maximum tolerance. */
	protected static final double MAX_TOLERANCE = 1e-6;
	/** Minimum tolerance. */
	protected static final double MIN_TOLERANCE = 1e-12;	// MAX_TOLERANCE squared

	/** Qubits used in this test. */
	private IQubit[] q;

	/**
	 * Constructor for subclasses in this package.
	 * 
	 * @param qubits
	 */
	AbstractQubitTest(int qubits) {
		q = new IQubit[qubits];
	}

	/**
	 * Returns a test qubit as specified by its index.
	 * 
	 * @param i	the test qubit index
	 * @return	the test qubit
	 */
	@SuppressWarnings("unchecked")
	public final Q q(int i) {
		return (Q) q[i];
	}

	/**
	 * Returns a qubit factory for the type of qubit
	 * to be used in the test.
	 * <p>
	 * This method is normally invoked before each test and the
	 * returned factory is passed to {@link #init(IQubitFactory)}.
	 * <p>
	 * All qubits produced by the factory are expected to be
	 * in initial state |0⟩, also on repeated invocations!
	 * For example, if produced qubits are backed by a shared
	 * quantum state that is the same after each invocation
	 * of {@code getFactory}, this quantum state must be
	 * reset to initial state |0⟩ on each invocation!
	 * <p>
	 * The default implementation returns {@code null},
	 * so this method should be overridden to return a
	 * valid qubit factory for the given number of qubits.
	 * Otherwise, the using class must wrap the test class
	 * and invoke {@link #init(IQubitFactory)} directly
	 * with a proper qubit factory.
	 * 
	 * @param n		number of qubits used in this test
	 * @return		the qubit factory
	 */
	protected IQubitFactory<Q> getFactory(int n) {
		return null;
	}

	/**
	 * Standard JUnit initialization method before each test.
	 * 
	 * @see #init(IQubitFactory)
	 */
	@BeforeEach
	final void init() {
		init(getFactory(q.length));
	}

	/**
	 * Uses the given factory to initialize the qubits used in this test.
	 * 
	 * @param factory	the qubit factory
	 * @return			this test class instance
	 */
	// Standard subclasses (for generic qubit types)
	// should override this method to explicitly
	// return their own type, but this super method must
	// always be invoked by the subclass.
	public AbstractQubitTest<Q> init(IQubitFactory<Q> factory) {
		char label = 'A';
		for (int i = 0; i < q.length; i++, label++) {
			q[i] = factory.newQubit(String.valueOf(label));
		}
		return this;
	}

	/**
	 * Returns a heading for errors caught by {@link #runAll()}.
	 * 
	 * @return	the heading
	 */
	protected String getHeading() {
		return getClass().getSimpleName() + '<' + q[0].getClass().getSimpleName() + '>';
	}

	/**
	 * Runs all tests in this class.
	 * <p>
	 * All tests are run inside
	 * {@link Assertions#assertAll(String, Executable...)}
	 * and all test qubits are reset before each test.
	 * For example:
	 * <pre>
	 *	assertAll(getHeading(),
	 *		run(() -> test1()),
	 *		run(() -> test2()),
	 *		run(() -> test3())
	 *	);
	 * </pre>
	 * 
	 * @see #run(Executable)
	 */
	public abstract void runAll();

	/**
	 * Runs the given test code after resetting
	 * all test qubits to initial state |0⟩.
	 * 
	 * @param test	lambda expression for the test to run
	 * @return		code that runs test code after reset
	 */
	public final Executable run(Executable test) {
		return () -> {
			reset();
			test.execute();
		};
	}

	/**
	 * Resets all test qubits.
	 */
	private void reset() {
		for (IQubit qubit : q) {
			qubit.reset();
		}
	}

	/**
	 * Asserts that the actual value is equal to the expected value.
	 * 
	 * @param label		a short description of the expected value
	 * @param expected	the expected value
	 * @param actual	the actual value
	 * @see	Assertions#assertEquals(Object, Object, String)
	 */
	public final void assertIsEqual(String label, Object expected, Object actual) {
		assertEquals(expected, actual, msg(label));
	}

	/**
	 * Asserts that the actual value is equal to the expected value.
	 * 
	 * @param label		a short description of the expected value
	 * @param expected	the expected value
	 * @param actual	the actual value
	 * @see	Assertions#assertEquals(int, int, String)
	 */
	public final void assertIsEqual(String label, int expected, int actual) {
		assertEquals(expected, actual, msg(label));
	}

	/**
	 * Asserts that the actual value is exactly equal to the expected value.
	 * 
	 * @param label		a short description of the expected value
	 * @param expected	the expected value
	 * @param actual	the actual value
	 * @see	Assertions#assertEquals(double, double, String)
	 */
	public final void assertIsEqual(String label, double expected, double actual) {
		assertEquals(expected, actual, msg(label));
	}

	/**
	 * Asserts that the actual value is equal or close to the expected value,
	 * within the specified tolerance.
	 * 
	 * @param label		a short description of the expected value
	 * @param expected	the expected value
	 * @param actual	the actual value
	 * @param tolerance	the maximum difference
	 * @see	Assertions#assertEquals(double, double, double, String)
	 */
	public final void assertIsClose(String label, double expected, double actual, double tolerance) {
		assertEquals(expected, actual, tolerance, msg(label));
	}

	/**
	 * Asserts that the actual value is equal or close to the expected value.
	 * <p>
	 * A default tolerance is applied, which depends on the expected value
	 * mapped to a sine or cosine curve, which makes sense in the quantum world.
	 * That is, small tolerance (1e-12) near extreme values -1 and +1,
	 * and a bigger tolerance (1e-6) near 0 (the middle).
	 * 
	 * @param label		a short description of the expected value
	 * @param expected	the expected value
	 * @param actual	the actual value
	 * @see	Assertions#assertEquals(double, double, double, String)
	 */
	public final void assertIsClose(String label, double expected, double actual) {
		assertIsClose(msg(label), expected, actual);
	}

	/**
	 * Asserts that the actual value is equal or close to the expected value.
	 * <p>
	 * A default tolerance is applied, which depends on the expected value
	 * mapped to a sine or cosine curve, which makes sense in the quantum world.
	 * That is, small tolerance (1e-12) near extreme values -1 and +1,
	 * and a bigger tolerance (1e-6) near 0 (the middle).
	 * 
	 * @param supplier	a message supplier
	 * @param expected	the expected value
	 * @param actual	the actual value
	 * @see	Assertions#assertEquals(double, double, double, String)
	 */
	public final void assertIsClose(Supplier<String> supplier, double expected, double actual) {
		double tolerance = MAX_TOLERANCE;		// maximum tolerance at 0.0
		if (expected != ZERO) {
			if (expected == PLUS || expected == MINUS) {
				tolerance = MIN_TOLERANCE;		// minimum tolerance at +1.0 and -1.0
			} else {
				double slope = Math.sqrt(1.0 - expected*expected);
				if (Double.isNaN(slope)) {
					// expected² > 1	let tolerance grow proportional with expected
					tolerance = Math.abs(expected) * MIN_TOLERANCE;
				} else {
					// expected² < 1	let tolerance grow proportional with slope
					tolerance = slope * (MAX_TOLERANCE - MIN_TOLERANCE) + MIN_TOLERANCE;
				}
			}
		}
		assertEquals(expected, actual, tolerance, supplier);
	}

	/**
	 * Asserts that the x-, y-, and z-value of the specified test qubit are
	 * equal or close to their expected values.
	 * 
	 * @param heading	common header that identifies the test
	 * @param expectedX	expected value for x
	 * @param expectedY	expected value for y
	 * @param expectedZ	expected value for z
	 * @param q			the test qubit
	 * @see				#assertIsClose(String, double, double)
	 */
	public final void assertXYZ(String heading, double expectedX, double expectedY, double expectedZ, IQubitAnalyzer q) {
		assertAll(heading,
			() -> assertIsClose("z", expectedZ, q.getZ()),
			() -> assertIsClose("x", expectedX, q.getX()),
			() -> assertIsClose("y", expectedY, q.getY())
		);
	}

	/**
	 * Returns a {@link Supplier} for a label on a new line.
	 * 
	 * @param label	the label
	 * @return		the enhanced label
	 */
	public final Supplier<String> msg(String label) {
		return () -> {
			if (label.contains(" ")) {
				return "\n" + label + "\n";
			}
			// label is a single word
			return "\n" + label;
		};
	}

	/**
	 * Returns a {@link Supplier} for an indexed label on a new line.
	 * 
	 * @param label	the label
	 * @param index	the index
	 * @return		the enhanced label
	 */
	public final Supplier<String> msg(String label, int index) {
		return () -> "\n" + label + '(' + index + ')';
	}

	/**
	 * Answers whether the given operation is supported.
	 * 
	 * @param operation	the operation to test
	 * @return			{@code true} if the operation returns without exception,
	 * 					{@code false} if the operation throws an {@link UnsupportedOperationException}
	 */
	public final boolean isSupported(Runnable operation) {
		try {
			operation.run();
			return true;
		} catch (UnsupportedOperationException e) {
			return false;
		}
	}

}
