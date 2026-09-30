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
import org.bemuisoft.quantum.core.RandomBlochVector;
import org.bemuisoft.quantum.sim.hidden.QubitV2;

/**
 * This is a classical universal bit simulation
 * with hidden variable variation 2.
 * <p>
 * The hidden variable is a vector that rotates
 * along with the qubit, much like {@link QubitV2}.
 * 
 *  @author Benno Muilwijk
 */
public class CubitV2 extends CubitState {

	/** Hidden variable (a co-rotating vector) */
	private RandomBlochVector lambda = new RandomBlochVector();

	/**
	 * Returns a cubit factory for {@code CubitV2}.
	 * 
	 * @return	a cubit factory for {@code CubitV2}
	 */
	public static QubitFactory<CubitV2> factory() {
		return new QubitFactory<>(CubitV2.class);
	}

	/**
	 * Constructs a simple cubit
	 * with hidden variable variation 2.
	 * 
	 * @param label	- a label
	 */
	public CubitV2(String label) {
		super(label, 2);
		getParts().set(1, lambda);
	}

	@Override
	public CubitState reset() {
		super.reset();
		lambda.randomize();
		return this;
	}

	@Override
	public double getHiddenX() {
		return lambda.getX();
	}

	@Override
	public double getHiddenY() {
		return lambda.getY();
	}

	@Override
	public double getHiddenZ() {
		return lambda.getZ();
	}

}
