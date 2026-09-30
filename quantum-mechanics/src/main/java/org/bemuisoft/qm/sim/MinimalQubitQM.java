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
package org.bemuisoft.qm.sim;

import org.bemuisoft.qm.core.Gate;
import org.bemuisoft.qm.core.PureQuantumSystem;
import org.bemuisoft.quantum.api.AbstractQubit;
import org.bemuisoft.quantum.api.IQuantumState;
import org.bemuisoft.quantum.api.IQubit;
import org.bemuisoft.quantum.api.QubitAnalyzer;
import org.bemuisoft.quantum.api.QubitFactory;

/**
 * A minimal implementation of {@link IQubit}
 * using {@link PureQuantumSystem} as a backbone.
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
 * @see PureQuantumSystem
 */
public class MinimalQubitQM extends AbstractQubit {

	private PureQuantumSystem qs;
	private int qIndex;

	/**
	 * Returns a qubit factory for {@code MinimalQubitQM}.
	 * 
	 * @param n				maximum number of qubits
	 * @param rightToLeft	direction of qubit label assignment
	 * @return				a qubit factory for {@code MinimalQubitQM}
	 * @see					IQuantumState#isRightToLeft()
	 */
	public static QubitFactory<MinimalQubitQM> factory(int n, boolean rightToLeft) {
		PureQuantumSystem qs = new PureQuantumSystem(n, rightToLeft);
		return new QubitFactory<>(qs, MinimalQubitQM.class);
	}

	/**
	 * Returns a qubit analyzer factory for {@code MinimalQubitQM}.
	 * 
	 * @param n				maximum number of qubits
	 * @param rightToLeft	direction of qubit label assignment
	 * @return				a qubit factory for {@code MinimalQubitQM}
	 * @see					IQuantumState#isRightToLeft()
	 * @see					QubitAnalyzer
	 */
	public static QubitAnalyzer.Factory<MinimalQubitQM> analyzerFactory(int n, boolean rightToLeft) {
		PureQuantumSystem qs = new PureQuantumSystem(n, rightToLeft);
		return new QubitAnalyzer.Factory<>(qs, MinimalQubitQM.class);
	}

	/**
	 * Constructs a qubit with a shared {@link PureQuantumSystem}.
	 * <p>
	 * The identifying short label will be derived
	 * from the last character of the long label.
	 * Therefore, the last character must in a consecutive
	 * range starting at either 0 or A or a.
	 * 
	 * @param qs		the quantum system to share
	 * @param label		a long label with identifying last character
	 * @see				IQuantumState#isRightToLeft()
	 */
	public MinimalQubitQM(PureQuantumSystem qs, String label) {
		this.qs = qs;
		this.qIndex = qs.qubitIndex(qs.shortLabel(label));
	}

	@Override
	public int measure() {
		double p1 = qs.getProbability1(qIndex);
		int outcome = (Math.random() < p1) ? 1 : 0;
		qs.setMeasured(outcome, qIndex);
		return outcome;
	}

	@Override
	public IQubit p(double radians) {
		qs.apply(Gate.p(radians), qIndex);
		return this;
	}

	@Override
	public IQubit rz(double radians) {
		qs.apply(Gate.rz(radians), qIndex);
		return this;
	}

	@Override
	protected IQubit u2(double phi, double lambda) {
		qs.apply(Gate.u(HALF_PI, phi, lambda), qIndex);
		return this;
	}

	@Override
	public IQubit cp(double radians, IQubit ctrl) {
		qs.apply(Gate.p(radians), index(ctrl), this.qIndex);
		return this;
	}

	private int index(IQubit q) {
		return ((MinimalQubitQM) q).qIndex;
	}

}
