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

package org.bemuisoft.quantum.api;

/**
 * A quantum state, usually referred to as |psi>,
 * is a commonly represented as a complex vector
 * in Hilbert space.
 */
public interface IQuantumState {

	/**
	 * Returns the number of qubits that are tracked
	 * by this quantum state.
	 * <p>
	 * This is also the maximum number of non-zero bits
	 * in the binary representation of the state index.
	 * For example, a quantum state for 3 qubits has a
	 * highest state index of binary 111 = 7.
	 * 
	 * @return	the number of qubits
	 */
	public int qubits();

	/**
	 * Returns the size of the state vector.
	 * <p>
	 * This size must always be equal to 2^qubits.
	 * Valid state indexes are in the range 0 - size-1,
	 * inclusive.
	 * 
	 * @return	the size of the state vector.
	 */
	public default int size() {
		return 1 << qubits();
	}

	/**
	 * Returns the magnitude of the complex amplitude
	 * at the specified state index.
	 * 
	 * @param i		state index
	 * @return		the magnitude
	 */
	public double getMagnitude(int i);

	/**
	 * Returns the phase of the complex amplitude
	 * at the specified state index.
	 * 
	 * @param i		state index
	 * @return		the phase
	 */
	public double getPhase(int i);

	/**
	 * Returns the probability of a specific combination of measurement
	 * outcomes to come true, if all qubits are to be measured now.
	 * <p>
	 * The combination is identified by the state index.
	 * For example, {@code getProbability(3)} returns the
	 * probability of measurement outcomes 011 to come true, that is,
	 * the probability that qubits represented by the two rightmost
	 * bits of the binary state index will be measured as 1
	 * and all other qubits will be measured as 0.
	 * 
	 * @param i		state index
	 * @return		the probability of the specified outcome
	 */
	public double getProbability(int i);

	/**
	 * Returns a boolean value which indicates if qubit labels
	 * are assigned from left to right or from right to left.
	 * <p>
	 * Qubits are uniquely associated with a single bit in the
	 * binary state index. But some implementations associate
	 * the first qubit with the leftmost bit, while others
	 * associate it with the rightmost bit. This association
	 * is usually intrinsic to the implementation and not easy
	 * to change. Comparing two implementations with different
	 * associations can be very hard and confusing.
	 * <p>
	 * To overcome this problem, it should be possible to assign
	 * labels 'A', 'B', 'C', etc. either from left to right or
	 * from right to left
	 * 
	 * @return		{@code true} for right to left,
	 * 				{@code false} for left to right
	 */
	public boolean isRightToLeft();

}
