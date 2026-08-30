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

package org.bemuisoft.quantum.test.api;

import org.bemuisoft.quantum.api.Axis;
import org.bemuisoft.quantum.api.Base;
import org.bemuisoft.quantum.api.IQubit;
import org.bemuisoft.quantum.api.IQubitFactory;

/**
 * Abstract class to gather basic statistics of a quantum experiment.
 * <p>
 * The experiment (circuit) to run must be defined in a subclass.
 * It is run a specified number of times and experiment results
 * are counted.
 * One experiment result is the combination of different qubit
 * measurement outcomes in a single run.
 * 
 * @param <Q>	the type of qubit used in the experiment
 */
public abstract class AbstractExperiment<Q extends IQubit> implements Base {

	// instance variables
	private BinaryStatistics stats;
	private int[] masks;
	private IQubit[] q;

	/**
	 * Constructor.
	 * <p>
	 * Sets up the experiment for the specified number of qubits.
	 * The specified qubit factory is used to instantiate the qubits.
	 * 
	 * @param n			number of qubits used in this experiment
	 * @param factory	the qubit factory to use
	 */
	public AbstractExperiment(int n, IQubitFactory<Q> factory) {
		stats = new BinaryStatistics(n);
		masks = new int[n];
		q = new IQubit[n];
		int mask = 1;
		for (int i = 0; i < n; i++) {
			q[i] = factory.newQubit("q" + i);
			masks[i] = mask;
			mask *= 2;
		}
	}

	/**
	 * Resets all qubits to |0>.
	 */
	private void init() {
		for (int i = 0; i < q.length; i++) {
			q[i].reset();
		}
	}

	/**
	 * Returns the number of qubits in this experiment.
	 * 
	 * @return	number of qubits used in this experiment
	 */
	public int getSize() {
		return q.length;
	}

	/**
	 * Returns the binary mask associated with
	 * the specified index.
	 * The value of this mask is always equal to 2^i.
	 * 
	 * @param i	the index
	 * @return	the mask
	 */
	public int getMask(int i) {
		return masks[i];
	}

	/**
	 * Returns the qubit with the specified index as {@link IQubit}.
	 * 
	 * @param i	index of the qubit to return
	 * @return	the qubit
	 */
	public IQubit getQubit(int i) {
		return q[i];
	}

	/**
	 * Returns the qubit with the specified index
	 * cast to the declared type Q.
	 * 
	 * @param i	index of the qubit to return
	 * @return	the qubit
	 */
	@SuppressWarnings("unchecked")
	public Q q(int i) {
		return (Q) q[i];
	}

	/**
	 * Runs the experiment the specified number of times
	 * and prints the results to {@code System.out}.
	 * <p>
	 * All qubits are reset to |0> before each run.
	 * 
	 * @param times	number of times to run the experiment
	 */
	public void run(int times) {
		for (int i = 0; i < times; i++) {
			init();
			run();
		}
		stats.printResults();
	}

	/**
	 * Runs the experiment one time.
	 * Normally, {@code measureAll()} should be called once at the end,
	 * but it is allowed to run a set of experiments and
	 * call {@code measureAll()} for each experiment in the set.
	 * <p>
	 * Alternatively, if not all qubits in the experiment must be measured,
	 * call {@code addResult()} with the required subset of measurement outcomes.
	 */
	public abstract void run();

	/**
	 * Measures all qubits. The combination of the outcomes
	 * is added as one experiment result.
	 */
	public void measureAll() {
		int result = 0;
		int outcome;
		for (int i = 0; i < getSize(); i++) {
			outcome = q(i).measure();
			result |= outcome * getMask(i);
		}
		addResult(result);
	}

	/**
	 * Increments the count of an experiment result.
	 * 
	 * @param result	the experiment result
	 */
	public void addResult(int result) {
		stats.addMeasurement(result);
	}

	/**
	 * Returns the total count for the specified experiment result.
	 * This is the number of times this result was measured.
	 * 
	 * @param result	the experiment result
	 * @return			the total count for the experiment result
	 */
	public int getTotal(int result) {
		return stats.getTotal(result);
	}

	/**
	 * Returns the total number of experiment results.
	 * 
	 * @return	total number of experiment results
	 */
	public int getTotal() {
		return stats.getTotal();
	}

	/**
	 * Resets all measurement counts to zero.
	 */
	public void resetStatistics() {
		stats.reset();
	}

	//--------------------------
	// some useful test methods
	//--------------------------

	/**
	 * Returns a random angle in the range [-PI, +PI].
	 * <p>
	 * The returned angles have a uniform distribution
	 * over the whole range.
	 * This is most useful as argument to RZ or P
	 * to get a random phase shift.
	 * <p>
	 * Be careful with RX and RY if that will give
	 * a rotation in a (near) polar orbit, because
	 * that would give an unequallly distributed
	 * projection on the measurement axis.
	 * This is not a problem if the state of the qubit
	 * is already randomized, because then the orbits
	 * will be distributed uniformly over the rotation axis.
	 * 
	 * @return a random angle
	 * @see #randomTheta()
	 */
	protected double random2Pi() {
		return PI * randomCos();
	}

	/**
	 * Returns a random angle in the range [0, PI].
	 * <p>
	 * The returned angles have a uniformly distributed cosine!
	 * This is most useful as argument to RX or RY when
	 * the qubit is known to be in state |0>  or |1>.
	 * <p>
	 * This example gives a qubit a random pure state
	 * with even distribution over the Bloch sphere:<pre>
	 * q(0).reset().ry(randomTheta()).p(random2Pi());
	 * </pre>
	 * 
	 * @return a random angle
	 * @see #random2Pi()
	 */
	protected double randomTheta() {
		return Math.acos(randomCos());
	}

	/**
	 * Entangles two qubits in a random way.
	 * 
	 * @param q1	qubit 1
	 * @param q2	qubit 2
	 */
	protected void entangleRandom(IQubit q1, IQubit q2) {
		q1.rx(random2Pi());
		q2.ry(random2Pi());
		q1.cp(random2Pi(), q2);
		q1.ry(random2Pi());
		q2.rx(random2Pi());
	}

	//--------------------------
	// some useful constants
	//--------------------------

	/** The X-axis. */
	protected static final Axis X = Axis.X;
	/** The Y-axis. */
	protected static final Axis Y = Axis.Y;
	/** The Z-axis. */
	protected static final Axis Z = Axis.Z;
	/** The Hadamard axis. */
	protected static final Axis H = Axis.H;

}
