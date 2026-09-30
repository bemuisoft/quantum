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
package org.bemuisoft.quantum.sim.copenhagen;

import org.bemuisoft.quantum.api.IQuantumState;
import org.bemuisoft.quantum.api.QubitFactory;
import org.bemuisoft.quantum.core.QuantumState;
import org.bemuisoft.quantum.core.QubitState;

/**
 * This is a standard bare-bone, no-nonsense simulation of a qubit.
 * It works according to the Copenhagen interpretation.
 * There is no hidden variable.
 * <p>
 * All functionality is inherited from {@link QubitState}.
 * 
 *  @author Benno Muilwijk
 */
public class StandardQubit extends QubitState {

	/**
	 * Returns a qubit factory for {@code StandardQubit}.
	 * 
	 * @param n				- maximum number of qubits
	 * @param rightToLeft	- direction of qubit label assignment
	 * @return				a qubit factory for {@code StandardQubit}
	 * @see					IQuantumState#isRightToLeft()
	 */
	public static QubitFactory<StandardQubit> factory(int n, boolean rightToLeft) {
		QuantumState qs = new QuantumState(n, rightToLeft);
		return new QubitFactory<>(qs, StandardQubit.class);
	}

	/**
	 * Constructs a standard qubit with a shared {@link QuantumState}.
	 * <p>
	 * The identifying short label will be derived
	 * from the last character of the long label.
	 * Therefore, the last character must in a consecutive
	 * range starting at either 0 or A or a.
	 * 
	 * @param qs	- the quantum state to share
	 * @param label	- a long label with identifying last character
	 * @see	IQuantumState#isRightToLeft()
	 */
	public StandardQubit(QuantumState qs, String label) {
		super(qs, label);
	}

	@Override
	public double getHiddenZ() {
		// there is no hidden variable in this implementation
		return Double.NaN;
	}

}
