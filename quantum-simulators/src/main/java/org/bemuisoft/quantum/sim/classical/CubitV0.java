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
package org.bemuisoft.quantum.sim.classical;

import org.bemuisoft.quantum.api.Axis;
import org.bemuisoft.quantum.api.IQubit;
import org.bemuisoft.quantum.api.QubitFactory;

/**
 * This is a classical universal bit simulation
 * without hidden variable.
 * <p>
 * On controlled rotations the control cubit is
 * actually measured, as if the wave function
 * collapses immediately.
 * The target cubit is only rotated if the
 * control cubit has state |1⟩ after measurement.
 * 
 *  @author Benno Muilwijk
 */
public class CubitV0 extends CubitState {

	/**
	 * Returns a cubit factory for {@code CubitV0}.
	 * 
	 * @return	a cubit factory for {@code CubitV0}
	 */
	public static QubitFactory<CubitV0> factory() {
		return new QubitFactory<>(CubitV0.class);
	}

	/**
	 * Constructs a simple cubit
	 * without hidden variable.
	 * 
	 * @param label	- a label
	 */
	public CubitV0(String label) {
		super(label);
	}

	@Override
	public double getHiddenZ() {
		// there is no hidden variable in this implementation
		return Double.NaN;
	}

	@Override
	public CubitState cx(IQubit ctrl) {
		if (ctrl.measure() == 1) x();
		return this;
	}

	@Override
	public CubitState cz(IQubit ctrl) {
		if (ctrl.measure() == 1) z();
		return this;
	}

	@Override
	public CubitState c(Axis axis, IQubit ctrl) {
		if (ctrl.measure() == 1) r(axis, PI);
		return this;
	}

	@Override
	public CubitState cp(double radians, IQubit ctrl) {
		if (ctrl.measure() == 1) p(radians);
		return this;
	}

	@Override
	public CubitState cr(Axis axis, double radians, IQubit ctrl) {
		if (ctrl.measure() == 1) r(axis, radians);
		return this;
	}

	@Override
	public CubitState cu(double theta, double phi, double lambda, IQubit ctrl) {
		if (ctrl.measure() == 1) u(theta, phi, lambda);
		return this;
	}

}
