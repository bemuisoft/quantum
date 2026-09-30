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
package org.bemuisoft.quantum.sim.hidden;

import org.bemuisoft.quantum.api.IQuantumState;
import org.bemuisoft.quantum.api.QubitFactory;
import org.bemuisoft.quantum.core.QuantumState;
import org.bemuisoft.quantum.core.QubitState;

/**
 * This is a qubit simulation with hidden variable variation 1.
 * <p>
 * The qubit itself behaves exactly the same as the standard qubit,
 * as all functionality is inherited from {@link QubitState}.
 * The only difference is that the randomness of a measurement
 * is not generated during measurement (God does not play dice).
 * This makes measurements deterministic in principle:
 * if both the quantum state and the value of the hidden variable
 * are known, the measurement outcome can be predicted exactly.
 * <p>
 * What sets this variation apart from other hidden variable
 * variations, is that the hidden value is only updated
 * during reset. It does not change when the qubit is rotated.
 * At first glance, this just the same as in the standard qubit,
 * only the random value is generated at a different time.
 * But there is a deeper physical consequence.
 * <p>
 * Consider the relativity of the chosen axes of the coordinate
 * system. Measurements are always done in the direction of the
 * z-axis, by definition. Traditionally, we think of the positive
 * z-axis as pointing up. But that does not have to be. In fact,
 * when we want to do a measurement in a horizontal direction,
 * we can rotate the qubit before measurement in such a way, that
 * the desired measurement direction is aligned with the z-axis.
 * But instead of rotating the qubit, we can also rotate the
 * measurement device, so that its measurement direction matches
 * the desired horizontal direction. In that case, the coordinate
 * system itself is rotated along with the measurement device, and
 * all coordinates need to be transformed to the new orientation
 * of the measurement device.
 * <p>
 * In other words, the coordinate system is tied to the orientation
 * of the measurement device. The orientation relative to the earth
 * is not relevant. Besides what is up or down if the experiment
 * is executed in outer space?
 * The only thing that matters is the orientation of the qubit
 * relative to the measurement device. A physical rotation of a
 * qubit cannot be distinguished from the opposite rotation of the
 * measurement device.
 * <p>
 * So, a hidden z-value that is not transformed due to a relative
 * rotation, is tied to the orientation of the measurement device.
 * Therefore, it should be considered as a property of the
 * measurement device, not of the qubit.
 * <p>
 * Having said that, would it be possible to influence this property
 * of the measurement device? Probably yes.
 * And that would also influence the measurement results. 
 * For example, a magnetic field would have an impact when measuring
 * spin direction.
 * One might say that the measurement is biased in that case.
 * So an "objective" measurement requires that the influence of the
 * measuring device is random with an even distribution?
 * 
 *  @author Benno Muilwijk
 */
public class QubitV1 extends QubitState {

	/** Hidden variable */
	private double lambda;

	/**
	 * Returns a qubit factory for {@code QubitV1}.
	 * 
	 * @param n				- maximum number of qubits
	 * @param rightToLeft	- direction of qubit label assignment
	 * @return				a qubit factory for {@code QubitV1}
	 * @see					IQuantumState#isRightToLeft()
	 */
	public static QubitFactory<QubitV1> factory(int n, boolean rightToLeft) {
		QuantumState qs = new QuantumState(n, rightToLeft);
		return new QubitFactory<>(qs, QubitV1.class);
	}

	/**
	 * Constructs a qubit with a shared {@link QuantumState}.
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
	public QubitV1(QuantumState qs, String label) {
		super(qs, label);
	}

	@Override
	public QubitState reset() {
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
