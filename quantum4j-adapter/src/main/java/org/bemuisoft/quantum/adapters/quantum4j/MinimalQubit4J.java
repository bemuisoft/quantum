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
package org.bemuisoft.quantum.adapters.quantum4j;

import org.bemuisoft.quantum.api.AbstractQubit;
import org.bemuisoft.quantum.api.IQuantumState;
import org.bemuisoft.quantum.api.IQubit;
import org.bemuisoft.quantum.api.QubitAnalyzer;
import org.bemuisoft.quantum.api.QubitFactory;

/**
 * A minimal implementation of {@link IQubit}
 * using {@link QuantumState4J} as a backbone.
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
 * @see QuantumState4J
 */
public class MinimalQubit4J extends AbstractQubit {

	private QuantumState4J qs;
	private int qIndex;

	/**
	 * Returns a qubit factory for {@code MinimalQubit4J}.
	 * 
	 * @param n				maximum number of qubits
	 * @return				a qubit factory for {@code MinimalQubit4J}
	 * @see					IQuantumState#isRightToLeft()
	 */
	public static QubitFactory<MinimalQubit4J> factory(int n) {
		QuantumState4J qs = new QuantumState4J(n);
		return new QubitFactory<>(qs, MinimalQubit4J.class);
	}

	/**
	 * Returns a qubit analyzer factory for {@code MinimalQubit4J}.
	 * 
	 * @param n				maximum number of qubits
	 * @return				a qubit factory for {@code MinimalQubit4J}
	 * @see					IQuantumState#isRightToLeft()
	 * @see					QubitAnalyzer
	 */
	public static QubitAnalyzer.Factory<MinimalQubit4J> analyzerFactory(int n) {
		QuantumState4J qs = new QuantumState4J(n);
		return new QubitAnalyzer.Factory<>(qs, MinimalQubit4J.class);
	}

	/**
	 * Constructs a qubit with a shared {@link QuantumState4J}.
	 * <p>
	 * The identifying short label will be derived
	 * from the last character of the long label.
	 * Therefore, the last character must in a consecutive
	 * range starting at either 0 or A or a.
	 * 
	 * @param qs	- the quantum4j state vector to share
	 * @param label	- a label with identifying last character
	 * @see			IQuantumState#isRightToLeft()
	 */
	public MinimalQubit4J(QuantumState4J qs, String label) {
		this.qs = qs;
		this.qIndex = qs.shortLabel(label) - 'A';
	}

	@Override
	public int measure() {
		return qs.measure(qIndex);
	}

	@Override
	public IQubit p(double radians) {
		qs.apply(Gates4J.u1(radians), qIndex);
		return this;
	}

	@Override
	public IQubit rz(double radians) {
		qs.apply(Gates4J.rz(radians), qIndex);
		return this;
	}

	@Override
	protected IQubit u2(double phi, double lambda) {
		qs.apply(Gates4J.u2(phi, lambda), qIndex);
		return this;
	}

	@Override
	public IQubit cp(double radians, IQubit ctrl) {
		qs.apply(Gates4J.cp(radians), index(ctrl), qIndex);
		return this;
	}

	private int index(IQubit q) {
		return ((MinimalQubit4J) q).qIndex;
	}

}
