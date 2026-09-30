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
package org.bemuisoft.quantum.core;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.bemuisoft.quantum.api.AbstractQubitAnalyzer;
import org.bemuisoft.quantum.api.Axis;
import org.bemuisoft.quantum.api.Base;
import org.bemuisoft.quantum.api.IQuantumState;
import org.bemuisoft.quantum.api.IQubit;

/**
 * A {@code QuantumState} is a lightweight implementation of a
 * Quantum Mechanics wave function, usually referred to as |Ψ⟩ or |psi⟩.
 * <p>
 * This implementation is not exactly the same as how the QM wave function
 * is described in standard QM textbooks, but it is mathematically equivalent.
 * The QM wave function can be derived from this implementation.
 * <p>
 * The QM wave function is usually expressed as a vector with 2<sup>n</sup>
 * complex elements, but this class keeps track of probabilities and phases
 * for each possible combination of measurement results.
 * <p>
 * The effect of gates is implemented as a series of rotations of
 * Bloch vector components, instead of matrix multiplications.
 * The effect is the same, but the mechanism is arguably more intuitive.
 * <p>
 * Each Bloch vector component is derived directly from a pair of
 * probabilities and their associated phases.
 * A density matrix is not needed, so it is not used.
 * <p>
 * The effect of measurements is implemented by directly updating
 * the same pairs of probabilities and their associated phases.
 * Textbook QM does this exactly the same, because it cannot be done
 * with a matrix multiplication on a multi-qubit system.
 * <p>
 * In summary, standard textbook QM teaches that gate operations,
 * Bloch vector  derivation and wave function update after measurement
 * require three different mechanisms, involving complex numbers.
 * This implementation shows all three can be achieved with
 * one and the same mechanism, and without complex numbers.
 * With just high school math, in fact.
 */
public class QuantumState implements IQuantumState, Base {

	private static ThreadLocal<BlochVector> tlBloch = new ThreadLocal<>();
	private static ThreadLocal<Sum> tlSum1 = new ThreadLocal<>();
	private static ThreadLocal<Sum> tlSum2 = new ThreadLocal<>();

	private int qubits;
	private boolean rightToLeft;
	private int modCount;
	private double pr[];	// probabilities
	private double ph[];	// phases

	/**
	 * Constructor with default labels for qubits.
	 * Default is to associate label A with qubit index 0.
	 * <p>
	 * The qubit index must not be confused with the state dimension index.
	 * For example, a quantum state for 3 qubits has 2^3 = 8 dimensions.
	 * Then valid qubit indices are 0, 1 and 2,
	 * whilst valid state dimension indices are in the range 0 - 7
	 * or binary 000 - 111.
	 * Qubit index 0 is associated with the rightmost bit
	 * of the binary state dimension index.
	 * 
	 * @param qubits		number of qubits to allocate for
	 */
	public QuantumState(int qubits) {
		this(qubits, true);
	}

	/**
	 * Constructor with direction for qubit labels.
	 * <p>
	 * It allocates a quantum state for each possible combination
	 * of measurement outcomes.
	 * For example, 3 qubits can each be measured as 0 or as 1.
	 * That gives a total of 2^3 = 8 combinations.
	 * The probability for each specific combination is stored in
	 * an element identified by its state dimension index in the range
	 * 000 (0) - 111 (7).
	 * <p>
	 * Qubit labels can be associated with the binary representation
	 * of the state dimension indices from left to right (ABC)
	 * or from right to left (CBA).
	 * But either way, the qubit index always counts from right to left,
	 * so qubit index 0 is always associated with the rightmost bit of
	 * the state dimension index.
	 * 
	 * @param qubits		- number of qubits to allocate for
	 * @param rightToLeft	- {@code true} for right to left,
	 * 						{@code false} for left to right
	 */
	public QuantumState(int qubits, boolean rightToLeft) {
		this.qubits = qubits;
		this.rightToLeft = rightToLeft;
		int n = 1 << qubits;			// n = (int) Math.pow(2, qubits);
		pr = new double[n];
		ph = new double[n];
		pr[0] = 1.0;
	}

	//===================
	// Public methods
	//===================

	@Override
	public final int qubits() {
		return qubits;
	}

	/**
	 * Returns the index for a qubit identified by a capital.
	 * <p>
	 * 'A' -> 0, 'B' -> 1, etc.
	 * <p>
	 * This is reversed for left-to-right labeling: 'A' -> highest index.
	 * 
	 * @param q	- the qubit's character 
	 * @return	the qubit index
	 */
	public int qubitIndex(char q) {
		// qubit index is right to left, i.e. qubit with index 0 is rightmost (least significant) bit
		if (rightToLeft) {
			// qubit A is rightmost (least significant) bit
			return q - 'A';
		}
		// qubit A is leftmost (most significant) bit
		return qubits - (q - '@');
	}

	@Override
	public final boolean isRightToLeft() {
		return rightToLeft;
	}

	/**
	 * Returns the current system state identifier.
	 * <p>
	 * If the returned value is the same as
	 * from a previous invocation, that means
	 * the quantum state of the system has not
	 * changed in between those two invocations.
	 * Otherwise, it has (possibly) changed.
	 * 
	 * @return	the current state identifier
	 * @see AbstractQubitAnalyzer#stateIdentifier
	 */
	public int stateIdentifier() {
		return modCount;
	}

	@Override
	public double getMagnitude(int i) {
		return Math.sqrt(pr[i]);
	}

	/**
	 * Returns the probability of a specific combination of measurement
	 * outcomes to come true, if all qubits are to be measured now.
	 * <p>
	 * The combination is identified by the state dimension index.
	 * For example, {@code getProbability(3)} returns the
	 * probability of measurement outcomes 011 to come true, that is,
	 * the probability that qubits with qubit index 0 and 1 will be
	 * measured as |1⟩ and all other qubits will be measured as |0⟩.
	 * 
	 * @param i		state dimension index
	 * @return		the probability of the specified outcome
	 */
	@Override
	public double getProbability(int i) {
		return pr[i];
	}

	@Override
	public double getPhase(int i) {
		return ph[i];
	}

	@Override
	public boolean hasStrictPhases() {
		return true;
	}

	/**
	 * Applies a (controlled) phase shift for the specified qubit(s).
	 * <p>
	 * If no qubit is passed, the effect is a global phase shift.
	 * If one qubit is passed, the effect is a phase shift for that qubit.
	 * If two distinct qubits are passed, the effect is a controlled
	 * phase shift for those qubits.
	 * If three or more distinct qubits are passed, the effect is a
	 * multi-controlled phase shift for those qubits.
	 * 
	 * @param radians	- the phase shift angle
	 * @param qubits	- the qubit(s)
	 * @see IQubit#p(double)
	 * @see IQubit#cp(double, IQubit)
	 * @see IQubit#ccp(double, IQubit, IQubit)
	 * @see IQubit#cu(double, double, double, IQubit[])
	 */
	public void shiftPhase(double radians, QubitState... qubits) {
		shiftPhase(radians, getMask(qubits));
	}

	/**
	 * Applies a (controlled) universal gate to the specified target qubit.
	 * <p>
	 * If no control qubit is passed, the effect is a single-qubit U gate.
	 * If one control qubit is passed, the effect is a controlled U gate.
	 * If two or more distinct control qubits are passed, the effect is a
	 * multi-controlled U gate.
	 * 
	 * @param theta		- theta
	 * @param phi		- phi
	 * @param lambda	- lambda
	 * @param target	- the target qubit
	 * @param control	- the control qubit(s)
	 * @see IQubit#u(double, double, double)
	 * @see IQubit#cu(double, double, double, IQubit)
	 * @see IQubit#cu(double, double, double, IQubit[])
	 */
	public void rotate(double theta, double phi, double lambda, QubitState target, QubitState... control) {
		rotate(theta, phi, lambda, target.getMask(), getMask(control));
	}

	/**
	 * Applies a (controlled) universal rotation to the specified target qubit.
	 * <p>
	 * If no control qubit is passed, the effect is a single-qubit R gate.
	 * If one control qubit is passed, the effect is a controlled R gate.
	 * If two or more distinct control qubits are passed, the effect is a
	 * multi-controlled R gate.
	 * 
	 * @param axis		- the rotation axis
	 * @param radians	- the rotation angle
	 * @param target	- the target qubit
	 * @param control	- the control qubit(s)
	 */
	public void rotate(Axis axis, double radians, QubitState target, QubitState... control) {
		rotate(axis, radians, target.getMask(), getMask(control));
	}

	/**
	 * Applies a (controlled) rotation of pi about the specified axis
	 * to the specified target qubit.
	 * <p>
	 * If no control qubit is passed, the effect is a single-qubit gate,
	 * like X, Y, Z or H, according to the specified axis.
	 * If one control qubit is passed, the effect is a controlled gate.
	 * If two or more distinct control qubits are passed, the effect is a
	 * multi-controlled gate.
	 * 
	 * @param axis		- the rotation axis
	 * @param target	- the target qubit
	 * @param control	- the control qubit(s)
	 * @see IQubit#h()
	 * @see IQubit#x()
	 * @see IQubit#y()
	 * @see IQubit#z()
	 * @see IQubit#c(Axis, IQubit)
	 * @see IQubit#c(Axis, IQubit[])
	 */
	public void rotate(Axis axis, QubitState target, QubitState... control) {
		rotate(axis, target.getMask(), getMask(control));
	}

	/**
	 * Processes a measurement outcome by collapsing the
	 * wave function represented by this quantaum state
	 * accordingly.
	 * 
	 * @param result	- the measurement outcome 0 or 1
	 * @param qubit		- the measured qubit
	 */
	public void setMeasured(int result, QubitState qubit) {
		setMeasured(result, qubit.getMask());
	}

//	public BlochVector getComponent(int i0, int i1) {
//		return getComponent(i0, i1, new BlochVector());
//	}

	//===================
	// Package methods		(for direct use by QubitState)
	//===================

	int getMask(QubitState... qs) {
		int mask = 0;
		for (QubitState q : qs) {
			mask |= q.getMask();
		}
		return mask;
	}

	void setMeasured(int result, int qMask) {
		Sum sum1 = getSum(tlSum1).reset();
		Sum sum2 = getSum(tlSum2).reset();
		if (result == 0) {
			forEach(qMask, (i, j) -> sum1.add(pr[i]));
		} else {
			forEach(qMask, (i, j) -> sum1.add(pr[j]));
		}
		double p = sum1.get();
		check(p > 0.0, "Impossible measurement result.");
		sum1.reset();
		forEach(qMask, (i, j) -> setMeasured(i, j, result, p, sum1, sum2));
		if (sum2.get() != 1.0) {
			// Born rule violation!!!
			log("Candidate for assert, but set breakpoint here during development");
			throw new IllegalStateException();
		}
		modCount++;
	}

	void shiftPhase(double radians, int mask) {
		// mask 0 performs a global phase shift, which does not affect any qubit
		// mask with 1 bit set performs a phase shift for that mask's qubit
		// mask with multiple bits set performs a controlled phase shift
		// XXX Note that there is no distinction between control and target for controlled phase shifts!
		forEach(mask, (i, j) -> shift(j, radians));
		modCount++;
	}

	void rotate(double theta, double phi, double lambda, int qMask) {
		rotate(theta, phi, lambda, qMask, 0);
	}

	void rotate(double theta, double phi, double lambda, int qMask, int cMask) {
		forEach(qMask, (i, j) -> rotate(i, j, cMask, theta, phi, lambda));
		modCount++;
	}

	void rotate(Axis axis, double radians, int qMask) {
		rotate(axis, radians, qMask, 0);
	}

	void rotate(Axis axis, double radians, int qMask, int cMask) {
		forEach(qMask, (i, j) -> rotate(i, j, cMask, axis, radians, false));
		modCount++;
	}

	void rotate(Axis axis, int qMask) {
		rotate(axis, qMask, 0);
	}

	void rotate(Axis axis, int qMask, int cMask) {
		// TODO find better (faster) solution for Pauli X and Y gates?
		forEach(qMask, (i, j) -> rotate(i, j, cMask, axis, PI, true));
		modCount++;
	}

	void forEachComponent(int qMask, Consumer<BlochVector> action) {
		forEach(qMask, (i, j) -> forComponent(i, j, action));
	}

	//===================
	// Private methods
	//===================

	private synchronized void forEach(int mask, BiConsumer<Integer, Integer> action) {
		for (int i = 0; i < pr.length; i++) {
			if ((i & mask) == 0) {
				int j = i | mask;
				action.accept(i, j);
			}
		}
	}

	private void setMeasured(int i0, int i1, int result, double odds, Sum before, Sum after) {
		if (pr[i0] == 0 && pr[i1] == 0.0) return;
		
		// update probabilities
		if (result == 0) {
//			pr[i0] = pr[i0] / odds;		// this can break the Born rule due to numerical limit of accuracy
			before.add(pr[i0]);
			pr[i0] = before.get()/odds - after.get();
			pr[i1] = 0.0;
			ph[i1] = 0.0;				// not really needed, but cleaner
			after.add(pr[i0]);
		} else {
//			pr[i1] = pr[i1] / odds;		// this can break the Born rule due to numerical limit of accuracy
			before.add(pr[i1]);
			pr[i1] = before.get()/odds - after.get();
			pr[i0] = 0.0;
			ph[i0] = 0.0;				// not really needed, but cleaner
			after.add(pr[i1]);
		}
	}

	private void rotate(int i0, int i1, int cMask, double theta, double phi, double lambda) {
		// XXX Note how easy controlled rotations are handled in the next line. Simply said: if controlled do less!
		if ((i1 | cMask) != i1) return;
		if (pr[i0] == 0.0 && pr[i1] == 0.0) return;
		
		if (lambda != 0.0) shift(i0, i1, lambda);
		if (theta != 0.0) lift(i0, i1, theta);
		if (phi != 0.0) shift(i0, i1, phi);
	}

	private void rotate(int i0, int i1, int cMask, Axis axis, double radians, boolean clean) {
		// XXX Note how easy controlled rotations are handled in the next line. Simply said: if controlled do less!
		if ((i1 | cMask) != i1) return;
		if (pr[i0] == 0.0 && pr[i1] == 0.0) return;
		
		final double axisTheta = axis.getTheta();
		final double axisPhase = axis.getPhi();
		if (axisPhase != 0.0) shift(i0, i1, -axisPhase);
		if (axisTheta != 0.0) lift(i0, i1, -axisTheta);
		if (clean) {
			// perform a clean phase shift
			// only used for X, Y, Z and H gates, so radians == PI always
			shift(i0, i1, PI);
		} else {
			// perform an RZ operation
			double half = radians * 0.5;
			shift(i0, -half);
			shift(i1, +half);
		}
		if (axisTheta != 0.0) lift(i0, i1, axisTheta);
		if (axisPhase != 0.0) shift(i0, i1, axisPhase);
	}

	private void forComponent(int i0, int i1, Consumer<BlochVector> action) {
		BlochVector comp = getComponent(i0, i1, tlBloch);
		action.accept(comp);
	}

	private BlochVector getComponent(int i0, int i1, ThreadLocal<BlochVector> tl) {
		BlochVector comp = tl.get();
		if (comp == null) {
			// initialize ThreadLocal variable for reuse (to avoid many instantiations and garbage after use)
			comp = new BlochVector();
			tl.set(comp);
		}
		return getComponent(i0, i1, comp);
	}

	private BlochVector getComponent(int i0, int i1, BlochVector target) {
		// extract the Bloch vector component
		double p = pr[i0] + pr[i1];					// probability == weighted length
		double pcos = pr[i0] - pr[i1];				// p*cos(theta) == z
		double psin = Math.sqrt(p*p - pcos*pcos);	// p*sin(theta) == sqrt(x² + y²) == sqrt(p² - z²)
		double phase = ph[i1] - ph[i0];
		
		return target.setXYZ(psin * Math.cos(phase), psin * Math.sin(phase), pcos);
	}

	private void shift(int i, double radians) {
		// shift phase -> rotate about z-axis
		if (pr[i] == 0.0) {
			if (ph[i] != 0.0) ph[i] = 0.0;
		} else {
			ph[i] = mod2Pi(ph[i] + radians);
		}
	}

	private void shift(int i0, int i1, double radians) {
		// i0 is not needed, but kept for consistency with lift(i0, i1, radians)
		shift(i1, radians);
	}

	private void lift(int i0, int i1, double radians) {
		// lift theta -> rotate about y-axis
		BlochVector comp = getComponent(i0, i1, tlBloch);
		comp.ry(radians);
		
		// update probabilities
		double p0 = pr[i0];
		double p1 = pr[i1];
		double p = p0 + p1;
		double newZ = comp.getZ();
//		pr[i0] = (p + newZ) / 2.0;
//		pr[i1] = (p - newZ) / 2.0;
		// calculate more precise to ensure that the Born rule is adhered to
		if (newZ < 0.0) {
			double pr1 = p;
			if (newZ > -p) {
				pr1 = (p - newZ) * 0.5;
			}
			pr[i0] = p - pr1;
			pr[i1] = pr1;
		} else {
			double pr0 = p;
			if (newZ < p) {
				pr0 = (p + newZ) * 0.5;
			}
			pr[i0] = pr0;
			pr[i1] = p - pr0;
		}
		
		// calculate phase changes using half the rotation angle
		double halfAngle = radians * 0.5;
		double cos = roundCos(Math.cos(halfAngle));
		double sin = roundCos(Math.sin(halfAngle));
		double oldPhase = ph[i1] - ph[i0];
		double nx = sin * roundCos(Math.cos(oldPhase));
		double ny = sin * roundCos(Math.sin(oldPhase));
		double m0 = Math.sqrt(p0);							// magnitude = square root of probability
		double m1 = Math.sqrt(p1);							// same for other element of state vector
		double d0 = Math.atan2(-m1*ny, m0*cos - m1*nx);
		double d1 = Math.atan2(-m0*ny, m1*cos + m0*nx);
		
		// update phases
		shift(i0, d0);
		shift(i1, d1);
		
		// assert general consistency
		if (pr[i0] != 0.0 && pr[i1] != 0.0) {
			assert Math.abs(mod2Pi(ph[i1] - ph[i0] - comp.getPhase())) < 1e-12;
		}
		
		if (getClass().desiredAssertionStatus()) {
			// assert consistency with textbook QM
			double cos2 = cos*cos;							// cos²(halfAngle)
			double sin2 = sin*sin;							// sin²(halfAngle)
			double oldX = 2*m0*m1*Math.cos(oldPhase);		// old x value of comp
			double xsin = sin*cos*oldX;						// old x * sin(fullAngle) / 2
			assert Math.abs(p0*cos2 + p1*sin2 - xsin - pr[i0]) < 1e-12;
			assert Math.abs(p0*sin2 + p1*cos2 + xsin - pr[i1]) < 1e-12;
		}
	}

//	private void liftOld(int i0, int i1, double radians) {
//		// lift theta -> rotate about y-axis
//		// works well for rotation angles between -π and +π, inclusive
//		// a global phase shift of π occurs when π < radians mod 4π < 3π
//		BlochVector comp = getComponent(i0, i1, tlBloch);
//		comp.ry(radians);
//		
//		// update probabilities
//		double p0 = pr[i0];
//		double p1 = pr[i1];
//		double p = p0 + p1;
//		double oldZ = p0 - p1;
//		double newZ = comp.getZ();
//		pr[i0] = (p + newZ) / 2.0;
//		pr[i1] = (p - newZ) / 2.0;
//		
//		// calculate phase changes
//		double d0, d1;
//		double oldPhase = ph[i1] - ph[i0];
//		double newPhase = comp.getPhase();
//		double cos = roundCos(Math.cos(radians));
//		if (cos == -1) {
//			// special case - RY(π)
//			d1 = ph[i0] - ph[i1];
//			d0 = PI - d1;
//			log("theta = PI");
//		} else {
//			double ysin = comp.getY() * roundCos(Math.sin(radians));
//			double pcos = p * (1 + cos);
//			double zsum = oldZ + newZ;
//			if (zsum < 0.0) {
//				d1 = Math.atan2(-ysin, pcos - zsum);
//				d0 = d1 + oldPhase - newPhase;
//				log("d0 from d1");
//			} else {
//				d0 = Math.atan2(-ysin, pcos + zsum);
//				d1 = d0 + newPhase - oldPhase;
//				log("d1 from d0");
//			}
//		}
//		
//		if (getClass().desiredAssertionStatus()) {
//			// assert consistency with textbook QM
//			double m0 = Math.sqrt(p0);				// magnitude = sqrt of probability
//			double m1 = Math.sqrt(p1);				// same for lower element of state vector
//			double cp = Math.cos(oldPhase);			// cos(phi1-phi0)
//			double sp = Math.sin(oldPhase);			// sin(phi1-phi0)
//			double halfTheta = radians * 0.5;		// theta is rotation angle in Bloch ball
//			double cr = Math.cos(halfTheta);		// cos(theta/2)
//			double sr = Math.sin(halfTheta);		// sin(theta/2)
//			assert Math.abs(p0*cr*cr + p1*sr*sr - 2*m0*m1*cr*sr*cp - pr[i0]) < 1e-12;
//			assert Math.abs(p0*sr*sr + p1*cr*cr + 2*m0*m1*cr*sr*cp - pr[i1]) < 1e-12;
//			if (pr[i0] != 0)
//				assert Math.abs(Math.atan2(-sr*m1*sp, cr*m0 - sr*m1*cp) - mod2Pi(d0)) < 1e-12;
//			if (pr[i1] != 0)
//				assert Math.abs(Math.atan2(-sr*m0*sp, cr*m1 + sr*m0*cp) - mod2Pi(d1)) < 1e-12;
//		}
//		
//		// update phases
//		shift(i0, i0, d0);
//		shift(i1, i1, d1);
//		
//		// assert general consistency
//		if (pr[i0] != 0.0 && pr[i1] != 0.0) {
//			assert Math.abs(mod2Pi(ph[i1] - ph[i0] - newPhase)) < 1e-12;
//		}
//	}

	private Sum getSum(ThreadLocal<Sum> tlSum) {
		Sum c = tlSum.get();
		if (c == null) {
			// initialize ThreadLocal variable for reuse (to avoid many instantiations and garbage after use)
			c = new Sum();
			tlSum.set(c);
		}
		return c;
	}

	private static class Sum {
		private double sum;
		private Sum reset() {
			sum = 0;
			return this;
		}
		private void add(double p) {
			sum += p;
		}
		private double get() {
			return sum;
		}
		@Override
		public String toString() {
			return Double.toString(sum);
		}
	}

}
