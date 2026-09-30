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

import org.bemuisoft.quantum.api.AbstractQubit;
import org.bemuisoft.quantum.api.IQuantumState;
import org.bemuisoft.quantum.api.IQubit;
import org.bemuisoft.quantum.api.QubitAnalyzer;
import org.bemuisoft.quantum.api.QubitFactory;
import org.bemuisoft.quantum.core.QuantumState;
import org.bemuisoft.quantum.core.QubitState;

/**
 * A minimal implementation of {@link IQubit}
 * using {@link QuantumState} as a backbone.
 * <p>
 * This implementation is minimal in the sense
 * that only required methods are implemented,
 * so least amount of coding.
 * Default methods from {@link IQubit} and
 * {@link AbstractQubit} are inherited, but
 * it should be noted that those are not always
 * the most efficient implementations.
 * 
 * @author Benno Muilwijk
 * 
 * @see IQubit
 * @see AbstractQubit
 * @see QuantumState
 */
public class MinimalQubit extends AbstractQubit {

	private QubitState q;

	/**
	 * Returns a qubit factory for {@code MinimalQubit}.
	 * 
	 * @param n				- maximum number of qubits
	 * @param rightToLeft	- direction of qubit label assignment
	 * @return				a qubit factory for {@code MinimalQubit}
	 * @see					IQuantumState#isRightToLeft()
	 */
	public static QubitFactory<MinimalQubit> factory(int n, boolean rightToLeft) {
		QuantumState qs = new QuantumState(n, rightToLeft);
		return new QubitFactory<>(qs, MinimalQubit.class);
	}

	/**
	 * Returns a qubit analyzer factory for {@code MinimalQubit}.
	 * 
	 * @param n				- maximum number of qubits
	 * @param rightToLeft	- direction of qubit label assignment
	 * @return				a qubit factory for {@code MinimalQubit}
	 * @see					IQuantumState#isRightToLeft()
	 * @see					QubitAnalyzer
	 */
	public static QubitAnalyzer.Factory<MinimalQubit> analyzerFactory(int n, boolean rightToLeft) {
		QuantumState qs = new QuantumState(n, rightToLeft);
		return new QubitAnalyzer.Factory<>(qs, MinimalQubit.class);
	}

	/**
	 * Constructs a minimal qubit with a shared {@link QuantumState}.
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
	public MinimalQubit(QuantumState qs, String label) {
		this.q = new QubitState(qs, label);
	}

	@Override
	public int measure() {
		double p1 = q.getProbability1();
		int outcome = (Math.random() < p1) ? 1 : 0;
		q.getSystemState().setMeasured(outcome, q);
		return outcome;
	}

	@Override
	public IQubit p(double radians) {
		q.p(radians);
		return this;
	}

	@Override
	public IQubit rz(double radians) {
		q.rz(radians);
		return this;
	}

	@Override
	protected IQubit u2(double phi, double lambda) {
		q.u(HALF_PI, phi, lambda);
		return this;
	}

	@Override
	public IQubit cp(double radians, IQubit ctrl) {
		MinimalQubit c = (MinimalQubit) ctrl;
		q.cp(radians, c.q);
		return this;
	}

}
