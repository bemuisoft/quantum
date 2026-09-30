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
package org.bemuisoft.quantum.sim.distributed;

import org.bemuisoft.quantum.core.BlochVector;

/**
 * This class implements a conditional Bloch vector
 * with various single part operations, mostly rotations.
 * <p>
 * It is optimized for fast processing and analysis,
 * at the cost of extra memory.
 * <p>
 * The magnitude (aka norm or length) is equal to the probability
 * that the qubit state will collapse into this vector
 * when all other qubits are measured.
 * 
 * @see BlochVector
 * 
 * @author Benno Muilwijk
 */
public class ConditionalBlochVector extends BlochVector {

	// instance variables
	private String label;
	private double theta;
	private double phase;
	private double probability;

	/**
	 * Constructor with label.
	 * 
	 * @param label
	 */
	ConditionalBlochVector(String label) {
		super(0.0, 0.0, 1.0);
		this.label = label;
	}

	/**
	 * Returns the label of this vector.
	 * 
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Returns the probability that the qubit state
	 * will collapse into this vector when
	 * all other qubits are measured.
	 * 
	 * @return the probability
	 */
	public double getProbability() {
		return probability;
	}

	/**
	 * Sets the probability that the qubit state
	 * will collapse into this vector when
	 * all other qubits are measured.
	 * 
	 * @param probability	- the probability to set
	 * @return this vector after update
	 */
	public ConditionalBlochVector setProbability(double probability) {
		check(probability >= 0.0 && probability <= 1.0, "Invalid probability " + probability);
		this.probability = probability;
		return this;
	}

	/**
	 * Sets the phase of this vector.
	 * 
	 * @param phase	- the phase to set
	 * @return this vector after update
	 */
	public ConditionalBlochVector setPhase(double phase) {
		this.phase = phase;
		double z = getNormalizedZ();				// cos(theta)
		double q = Math.sqrt(1.0 - z*z);			// sin(theta)
		super.setXYZ(Math.cos(phase)*q, Math.sin(phase)*q, z);
		return this;
	}

	/**
	 * Sets the probability and direction of this vector.
	 * 
	 * @param p		- the probability to set
	 * @param z		- the weighted z value to set
	 * @param phase	- the phase to set
	 * @return this vector after update
	 */
	public ConditionalBlochVector setPZ(double p, double z, double phase) {
		check(p >= 0.0 && p <= 1.0, "Invalid probability " + p);
		this.probability = p;
		if (p > 0.0) {
			if (Math.abs(p - z) < 1e-15) z = p;
			if (Math.abs(p + z) < 1e-15) z = -p;
			// set super with normalized values
			z = roundCos(z/p);						// cos(theta)
			double q = Math.sqrt(1.0 - z*z);		// sin(theta)
			super.setXYZ(Math.cos(phase)*q, Math.sin(phase)*q, z);
		}
		updatePolar();
		this.phase = phase;
		return this;
	}

	/**
	 * Sets this vector to the same direction
	 * and probability as the given vector.
	 * 
	 * @param v		- the source vector
	 * @return this vector after update
	 */
	public ConditionalBlochVector set(ConditionalBlochVector v) {
		this.probability = v.probability;
		this.theta = v.theta;
		this.phase = v.phase;
		super.x = v.x;
		super.y = v.y;
		super.z = v.z;
		return this;
	}

	@Override
	public ConditionalBlochVector setXYZ(double x, double y, double z) {
		// set weighted x, y and z
		double p = Math.sqrt(x*x + y*y + z*z);
		if (p > 1.0 && p - 1.0 < 1e-15) {
			p = 1.0;
		} else {
			check(p >= 0.0 && p <= 1.0, "Invalid probability " + p);
		}
		if (p < 1e-15) {
			probability = 0.0;
//			super.setXYZ(0.0, 0.0, 1.0);
		} else {
			probability = p;
			// set super with normalized values
			super.setXYZ(x/p, y/p, z/p);
		}
		updatePolar();
		return this;
	}

	/**
	 * Returns the normalized x value,
	 * which is the weighted x value
	 * divided by the probability.
	 * 
	 * @return the normalized x value
	 */
	public double getNormalizedX() {
		return super.getX();
	}

	/**
	 * Returns the normalized y value,
	 * which is the weighted y value
	 * divided by the probability.
	 * 
	 * @return the normalized y value
	 */
	public double getNormalizedY() {
		return super.getY();
	}

	/**
	 * Returns the normalized z value,
	 * which is the weighted z value
	 * divided by the probability.
	 * 
	 * @return the normalized z value
	 */
	public double getNormalizedZ() {
		return super.getZ();
	}

	/**
	 * Returns the weighted x value,
	 * which is the normalized x value
	 * times the probability.
	 * 
	 * @return the weighted x value
	 */
	@Override
	public double getX() {
		return probability * super.getX();
	}

	/**
	 * Returns the weighted y value,
	 * which is the normalized y value
	 * times the probability.
	 * 
	 * @return the weighted y value
	 */
	@Override
	public double getY() {
		return probability * super.getY();
	}

	/**
	 * Returns the weighted z value,
	 * which is the normalized z value
	 * times the probability.
	 * 
	 * @return the weighted z value
	 */
	@Override
	public double getZ() {
		return probability * super.getZ();
	}

	@Override
	public double getMagnitude() {
		return getProbability();
	}

	@Override
	public double getTheta() {
		if (Double.isNaN(theta)) {
			// calculate theta
			theta = Math.acos(super.getZ());
		}
		return theta;
	}

	@Override
	public double getPhase() {
		return phase;
	}

	@Override
	public ConditionalBlochVector h() {
		super.h();
		updatePhase(PI);	// just in case x == y == 0, when updatePolar does not change the phase
		updatePolar();
		return this;
	}

	@Override
	public ConditionalBlochVector x() {
		super.x();
		theta = PI - theta;
		phase = -phase;
		return this;
	}

	@Override
	public ConditionalBlochVector y() {
		super.y();
		theta = PI - theta;
		phase = (phase < 0.0 ? -PI : PI) - phase;
		return this;
	}

	@Override
	public ConditionalBlochVector z() {
		super.z();
		// theta does not change
		updatePhase(PI);
		return this;
	}

	@Override
	public ConditionalBlochVector s() {
		super.s();
		// theta does not change
		updatePhase(HALF_PI);
		return this;
	}

	@Override
	public ConditionalBlochVector sdg() {
		super.sdg();
		// theta does not change
		updatePhase(-HALF_PI);
		return this;
	}

	@Override
	public ConditionalBlochVector t() {
		super.t();
		// theta does not change
		updatePhase(QUARTER_PI);
		return this;
	}

	@Override
	public ConditionalBlochVector tdg() {
		super.tdg();
		// theta does not change
		updatePhase(-QUARTER_PI);
		return this;
	}

	@Override
	public ConditionalBlochVector rx(double radians) {
		super.rx(radians);
		updatePolar();
		return this;
	}

	@Override
	public ConditionalBlochVector ry(double radians) {
		super.ry(radians);
		updatePolar();
		return this;
	}

	@Override
	public ConditionalBlochVector rz(double radians) {
		super.rz(radians);
		// theta does not change
		updatePhase(radians);
		return this;
	}

	/**
	 * Answers whether this vector and another are
	 * pointing in opposite directions.
	 * 
	 * @param other	- the vector to compare to
	 * @return		{@code true} if this and other are antiparallel,
	 * 				{@code false} otherwise
	 */
	public boolean isAntiParallel(ConditionalBlochVector other) {
		double dotProduct = this.getNormalizedX() * other.getNormalizedX() +
							this.getNormalizedY() * other.getNormalizedY() +
							this.getNormalizedZ() * other.getNormalizedZ();
		return (Math.abs(dotProduct + 1.0) < 1e-12);
	}

	/**
	 * Updates the phase of this vector
	 * by adding the specified angle.
	 * <p>
	 * It is strictly to be used as a fast alternative
	 * to updatePolar, either before or after update
	 * of the <i>x</i> and <i>y</i> coordinates.
	 * 
	 * @param radians - the rotation angle
	 */
	private void updatePhase(double radians) {
		phase = mod2Pi(phase + radians);
	}

	/**
	 * Updates the polar coordinates <i>theta</i> and <i>phase</i>,
	 * based on updated values of <i>x</i>, <i>y</i> and <i>z</i>.
	 * 
	 * <pre>
	 *  z = p cos(theta)
	 *  y = p sin(theta)sin(phase)
	 *  x = p sin(theta)cos(phase)
	 * </pre>
	 */
	private void updatePolar() {
		// just invalidate the current value of theta; recalculate only when needed
		theta = Double.NaN;
		// keep current value of phase when landing on either pole
		if (super.getX() != 0.0 || super.getY() != 0.0) {
			// not on pole; recalculate phase
			phase = Math.atan2(super.getY(), super.getX());
		}
	}

	@Override
	protected StringBuilder addDescription(StringBuilder sb) {
		sb.append(' ').append(label);
		sb.append(" {theta=").append(toPi(getTheta()));
		sb.append(", phase=").append(toPi(getPhase()));
		sb.append(", p=").append(getProbability());
		sb.append(", x=").append(getX());
		sb.append(", y=").append(getY());
		sb.append(", z=").append(getZ());
		sb.append('}');
		return sb;
	}

}
