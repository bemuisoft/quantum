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
 * Provides a wrapper for {@link IQubit} implementations
 * that do not implement {@link IQubitAnalyzer} themselves,
 * but do have a shared {@link IQuantumState} object.
 * <p>
 * This wrapper adds {@link IQubitAnalyzer} functionality
 * to the wrapped qubit.
 * 
 * @author Benno Muilwijk
 */
public class QubitAnalyzer extends AbstractQubitAnalyzer {

	private IQubit q;
	private StateIdentifier state;

	/**
	 * A factory for qubit analyzers.
	 * 
	 * @param <Q>	the type of qubit to be wrapped by the analyzer
	 */
	public static class Factory<Q extends IQubit> implements IQubitFactory<QubitAnalyzer> {
		private QubitFactory<Q> qubitFactory;
		private IQuantumState qs;
		private StateIdentifier stateId = new StateIdentifier();

		/**
		 * Factory constructor for qubit analyzers.
		 * 
		 * @param qState	- a quantum state object
		 * @param implClass	- the class Q that implements the qubit to be wrapped
		 * @see IQuantumState
		 */
		public Factory(IQuantumState qState, Class<Q> implClass) {
			qubitFactory = new QubitFactory<>(qState, implClass);
			qs = qState;
		}

		/**
		 * Returns a qubit analyzer that wraps a qubit of type Q.
		 * 
		 * @param label	- a label that identifies the qubit
		 * @return		a new qubit instance of type Q
		 */
		@Override
		public QubitAnalyzer newQubit(String label) {
			IQubit q = qubitFactory.newQubit(label);
			return new QubitAnalyzer(qs, q, label, stateId);
		}
	}

	private static class StateIdentifier {
		private int id;
	}

	// private constructor
	private QubitAnalyzer(IQuantumState qs, IQubit q, String label, StateIdentifier state) {
		super(qs, q.getClass().getSimpleName() + ' ' + label);
		this.q = q;
		this.state = state;
	}

	@Override
	protected int stateIdentifier() {
		return state.id;
	}

	private QubitAnalyzer thisUpdated() {
		state.id++;
		return this;
	}

	//------------------------
	// IQubit wrapper methods
	//------------------------

	@Override
	public int measure() {
		thisUpdated();
		return q.measure();
	}

	@Override
	public int measureSign() {
		thisUpdated();
		return q.measureSign();
	}

	@Override
	public QubitAnalyzer reset() {
		q.reset();
		return thisUpdated();
	}

	//--------------------
	// Single-qubit gates
	//--------------------

	@Override
	public QubitAnalyzer h() {
		q.h();
		return thisUpdated();
	}

	@Override
	public QubitAnalyzer x() {
		q.x();
		return thisUpdated();
	}

	@Override
	public QubitAnalyzer y() {
		q.y();
		return thisUpdated();
	}

	@Override
	public QubitAnalyzer z() {
		q.z();
		return thisUpdated();
	}

	@Override
	public QubitAnalyzer s() {
		q.s();
		return thisUpdated();
	}

	@Override
	public QubitAnalyzer sdg() {
		q.sdg();
		return thisUpdated();
	}

	@Override
	public QubitAnalyzer t() {
		q.t();
		return thisUpdated();
	}

	@Override
	public QubitAnalyzer tdg() {
		q.tdg();
		return thisUpdated();
	}

	@Override
	public QubitAnalyzer p(double radians) {
		q.p(radians);
		return thisUpdated();
	}

	@Override
	public QubitAnalyzer sx() {
		q.sx();
		return thisUpdated();
	}

	@Override
	public QubitAnalyzer sxdg() {
		q.sxdg();
		return thisUpdated();
	}

	@Override
	public QubitAnalyzer rx(double radians) {
		q.rx(radians);
		return thisUpdated();
	}

	@Override
	public QubitAnalyzer ry(double radians) {
		q.ry(radians);
		return thisUpdated();
	}

	@Override
	public QubitAnalyzer rz(double radians) {
		q.rz(radians);
		return thisUpdated();
	}

	@Override
	public QubitAnalyzer r(Axis axis, double radians) {
		q.r(axis, radians);
		return thisUpdated();
	}

	@Override
	public QubitAnalyzer u(double theta, double phi, double lambda) {
		q.u(theta, phi, lambda);
		return thisUpdated();
	}

	@Override
	public QubitAnalyzer not() {
		q.not();
		return thisUpdated();
	}

	//-----------------
	// Two-qubit gates
	//-----------------

	@Override
	public QubitAnalyzer cx(IQubit ctrl) {
		q.cx(q(ctrl));
		return thisUpdated();
	}

	@Override
	public QubitAnalyzer cz(IQubit ctrl) {
		q.cz(q(ctrl));
		return thisUpdated();
	}

	@Override
	public QubitAnalyzer cp(double radians, IQubit ctrl) {
		q.cp(radians, q(ctrl));
		return thisUpdated();
	}

	@Override
	public QubitAnalyzer c(Axis axis, IQubit ctrl) {
		q.c(axis, q(ctrl));
		return thisUpdated();
	}

	@Override
	public QubitAnalyzer cr(Axis axis, double radians, IQubit ctrl) {
		q.cr(axis, radians, q(ctrl));
		return thisUpdated();
	}

	@Override
	public QubitAnalyzer cu(double theta, double phi, double lambda, IQubit ctrl) {
		q.cu(theta, phi, lambda, q(ctrl));
		return thisUpdated();
	}

	@Override
	public QubitAnalyzer cnot(IQubit ctrl) {
		q.cnot(q(ctrl));
		return thisUpdated();
	}

	@Override
	public QubitAnalyzer swap(IQubit ctrl) {
		q.swap(q(ctrl));
		return thisUpdated();
	}

	//-------------------
	// Three-qubit gates
	//-------------------

	@Override
	public QubitAnalyzer ccx(IQubit ctrl1, IQubit ctrl2) {
		q.ccx(q(ctrl1), q(ctrl2));
		return thisUpdated();
	}

	@Override
	public QubitAnalyzer ccz(IQubit ctrl1, IQubit ctrl2) {
		q.ccz(q(ctrl1), q(ctrl2));
		return thisUpdated();
	}

	@Override
	public QubitAnalyzer ccp(double radians, IQubit ctrl1, IQubit ctrl2) {
		q.ccp(radians, q(ctrl1), q(ctrl2));
		return thisUpdated();
	}

	@Override
	public QubitAnalyzer toffoli(IQubit ctrl1, IQubit ctrl2) {
		q.toffoli(q(ctrl1), q(ctrl2));
		return thisUpdated();
	}

	@Override
	public QubitAnalyzer c(Axis axis, IQubit... ctrl) {
		q.c(axis, q(ctrl));
		return thisUpdated();
	}

	//-------------------
	// Multi-qubit gates
	//-------------------

	@Override
	public QubitAnalyzer cr(Axis axis, double radians, IQubit... ctrl) {
		q.cr(axis, radians, q(ctrl));
		return thisUpdated();
	}

	@Override
	public QubitAnalyzer cu(double theta, double phi, double lambda, IQubit... ctrl) {
		q.cu(theta, phi, lambda, q(ctrl));
		return thisUpdated();
	}

	//------------------
	// Private adapters
	//------------------

	private final IQubit q(IQubit ctrl) {
		return ((QubitAnalyzer) ctrl).q;
	}

	private final IQubit[] q(IQubit... ctrl) {
		IQubit[] qc = new IQubit[ctrl.length];
		for (int i = 0; i < ctrl.length; i++) {
			qc[i] = q(ctrl[i]);
		}
		return qc;
	}

}
