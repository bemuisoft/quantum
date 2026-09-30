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

import org.bemuisoft.quantum.api.QubitFactory;
import org.bemuisoft.quantum.sim.hidden.QubitV1;

/**
 * This is a classical universal bit simulation
 * with hidden variable variation 1.
 * <p>
 * The hidden variable is a single value,
 * which is only updated during reset,
 * much like {@link QubitV1}.
 * 
 *  @author Benno Muilwijk
 */
public class CubitV1 extends CubitState {

	/** Hidden variable */
	private double lambda;

	/**
	 * Returns a cubit factory for {@code CubitV1}.
	 * 
	 * @return	a cubit factory for {@code CubitV1}
	 */
	public static QubitFactory<CubitV1> factory() {
		return new QubitFactory<>(CubitV1.class);
	}

	/**
	 * Constructs a simple cubit
	 * with hidden variable variation 1.
	 * 
	 * @param label	- a label
	 */
	public CubitV1(String label) {
		super(label);
	}

	@Override
	public CubitState reset() {
		super.reset();
		lambda = randomCos();
		return this;
	}

	@Override
	public double getHiddenZ() {
		return lambda;
	}

	@Override
	public void setHiddenZ(double z) {
		lambda = z;
	}

}
