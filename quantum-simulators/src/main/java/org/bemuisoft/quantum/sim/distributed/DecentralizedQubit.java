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

import java.util.ArrayList;
import java.util.function.Consumer;

import org.bemuisoft.quantum.api.Axis;
import org.bemuisoft.quantum.api.Base;
import org.bemuisoft.quantum.api.IQubitFactory;
import org.bemuisoft.quantum.core.BlochVector;
import org.bemuisoft.quantum.sim.classical.CubitState;

/**
 * TODO
 * 
 * @author Benno Muilwijk
 */
public class DecentralizedQubit extends CubitState {

	private static final double PRECISION = 1.0 - Math.nextDown(1.0);
	private static final double MARGIN = 1e-8;
	private static final ThreadLocal<WorkArea> workArea = new ThreadLocal<>();

	private final int id;
	private final int index;
	private final EventManager eventManager;

	/**
	 * Returns a qubit factory for {@code DecentralizedQubit}.
	 * 
	 * @param n	- maximum number of qubits
	 * @return	a qubit factory for {@code DecentralizedQubit}
	 */
	public static Factory factory(int n) {
		EventManager em = new EventManager(n);
		return new Factory(em);
	}

	// private constructor
	private DecentralizedQubit(EventManager eventManager, String label) {
		super(label, 0);
		this.eventManager = eventManager;
		this.index = eventManager.register(this);
		this.id = 1 << index;
		initialzeParts(eventManager.getPartLabels().length);
		getPart(0).setXYZ(0.0, 0.0, 1.0);	// initialize sum of probabilities to 1
	}

	@Override
	protected void initialzeParts(int n) {
		String[] partLabels = eventManager.getPartLabels();
		ArrayList<BlochVector> parts = new ArrayList<BlochVector>(n);
		for (int i = 0; i < partLabels.length; i++) {
			parts.add(new ConditionalBlochVector(getLabel() + '.' + partLabels[i]));
		}
		setParts(parts);
	}

	/**
	 * Sets the Bloch vector of this qubit.
	 * 
	 * @param x - x
	 * @param y - y
	 * @param z - z
	 */
	protected void setXYZ(double x, double y, double z) {
		super.x = roundCos(x);
		super.y = roundCos(y);
		super.z = roundCos(z);
	}

	@Override
	protected void forEach(Consumer<BlochVector> action) {
		WorkArea w = getWorkArea();
		int i = 0;
		double x = 0.0;
		double y = 0.0;
		double z = 0.0;
		double p = 0.0;
		for (BlochVector part : getParts()) {
			w.ph[i++] = part.getPhase();
			action.accept(part);
			x += part.getX();
			y += part.getY();
			z += part.getZ();
			p += part.getMagnitude();
		}
		logIf(p != 1.0, getLabel() + " sum of probabilities = " + p);
		setXYZ(x, y, z);
	}

	@Override
	public int components() {
		return eventManager.getPartLabels().length;
	}

	@Override
	public double getX() {
		return super.x;
	}

	@Override
	public double getY() {
		return super.y;
	}

	@Override
	public double getZ() {
		return super.z;
	}

	@Override
	public double getX(int i) {
		return getPart(i).getX();
	}

	@Override
	public double getY(int i) {
		return getPart(i).getY();
	}

	@Override
	public double getZ(int i) {
		return getPart(i).getZ();
	}

	@Override
	public double getHiddenZ() {
		return Double.NaN;
	}

	private int measure(int up) {
		boolean minus = isMinus();
		int z = minus ? -1 : +1;
		int result = (1 - z) / 2;
		debug("measured |" + result + '⟩');
		forEach(part -> setZ((ConditionalBlochVector) part, z));
		
		// notify other qubits of this change event (will also update this qubit's part probabilities)
		eventManager.qubitMeasured(this, minus);
		return (up == 1) ? z : result;
	}

	@Override
	public int measure() {
		return measure(0);
	}

	@Override
	public int measureSign() {
		return measure(1);
	}

	@Override
	public DecentralizedQubit reset() {
		debug("reset");
		double z = getZ();
		if (z < 1.0) {
			if (z > -1.0) {
				forEach(part -> setZ((ConditionalBlochVector) part, 1.0));
				eventManager.qubitMeasured(this, false);
			} else {
				// flip pure state |1⟩ to |0⟩ (qubitMeasured would run into zero divide)
				x();
			}
		}
		return this;
	}

	@Override
	public DecentralizedQubit h() {
		super.h();
		qubitChanged(HALF_PI, 0.0, PI);
		return this;
	}

	@Override
	public DecentralizedQubit x() {
		super.x();
		qubitChanged(Axis.X, PI);
		return this;
	}

	@Override
	public DecentralizedQubit y() {
		super.y();
		qubitChanged(Axis.Y, PI);
		return this;
	}

	@Override
	public CubitState sx() {
		super.sx();
		qubitChanged(Axis.X, HALF_PI);
		return this;
	}

	@Override
	public CubitState sxdg() {
		super.sxdg();
		qubitChanged(Axis.X, -HALF_PI);
		return this;
	}

	@Override
	public DecentralizedQubit rx(double radians) {
		super.rx(radians);
		qubitChanged(Axis.X, radians);
		return this;
	}

	@Override
	public DecentralizedQubit ry(double radians) {
		super.ry(radians);
		qubitChanged(Axis.Y, radians);
		return this;
	}

	@Override
	public DecentralizedQubit r(Axis axis, double radians) {
		// TODO either convert (axis, radians) to single u() or invoke qubitChanged after each u()
		super.r(axis, radians);
		qubitChanged(axis, radians);	// TODO this only works for axes in the xy-plane
		return this;
	}

	@Override
	public DecentralizedQubit u(double theta, double phi, double lambda) {
		super.u(theta, phi, lambda);
		qubitChanged(theta, phi, lambda);
		return this;
	}

	@Override
	protected void cp(double radians, CubitState ctrl) {
		cp(radians, (DecentralizedQubit) ctrl);
	}

	/**
	 * Applies a controlled phase shift.
	 * <p>
	 * This operation is symmetric, i.e.
	 * {@code this.cp(lambda, ctrl)} is equivalent to {@code ctrl.cp(lambda, this)}.
	 * 
	 * @param ctrl - the control qubit
	 * @return this qubit (the target)
	 */
	private void cp(double radians, DecentralizedQubit ctrl) {
		final DecentralizedQubit trgt = this;
		final int trgtPartIndexOfCtrl = trgt.getPartIndex(ctrl);
		final int ctrlPartIndexOfTrgt = ctrl.getPartIndex(trgt);
		
		double trgtX = 0.0;
		double trgtY = 0.0;
		double ctrlX = 0.0;
		double ctrlY = 0.0;
		for (int i = 0; i < components(); i++) {
			BlochVector trgtPart = trgt.getPart(i);
			BlochVector ctrlPart = ctrl.getPart(i);
			if ((i & trgtPartIndexOfCtrl) != 0) {
				trgtPart.p(radians);
			}
			if ((i & ctrlPartIndexOfTrgt) != 0) {
				ctrlPart.p(radians);
			}
			trgtX += trgtPart.getX();
			trgtY += trgtPart.getY();
			ctrlX += ctrlPart.getX();
			ctrlY += ctrlPart.getY();
		}
		
		// update weighted average
		trgt.setXYZ(trgtX, trgtY, trgt.getZ());
		ctrl.setXYZ(ctrlX, ctrlY, ctrl.getZ());
	}

	/**
	 * Notifies other qubits of a change event on this qubit.
	 */
	private void qubitChanged(Axis axis, double radians) {
		// only works for axes in the xy-plane
		eventManager.qubitChanged(this, radians, axis.getPhi() - HALF_PI, HALF_PI - axis.getPhi());
	}

	/**
	 * Notifies other qubits of a change event on this qubit.
	 */
	private void qubitChanged(double theta, double phi, double lambda) {
		eventManager.qubitChanged(this, theta, phi, lambda);
	}

	/**
	 * Processes a change event on another qubit.
	 * <p>
	 * This is updating our knowledge about the correlation
	 * between this qubit and the other.
	 * 
	 * @param other			- the qubit that changed
	 * @param rotationAxis	- the rotation axis
	 * @param radians		- the rotation angle in radians
	 */
	void qubitChanged(DecentralizedQubit other, double theta, double phi, double lambda) {
		final int thisPartIndexOfOther = this.getPartIndex(other);
		final int otherPartIndexOfThis = other.getPartIndex(this);
		final WorkArea w = getWorkArea();
		boolean sumChanged = false;
		double x = 0.0;
		double y = 0.0;
		double z = 0.0;
		double pSum = 0.0;
		
		// update parts and their probabilities in pairs, depending on qubit index
		int pairs = components() / 2;
		for (int i = 0; i < pairs; i++) {
			int partIndex = insertPartIndex(i, otherPartIndexOfThis);
			w.blochA0 = (ConditionalBlochVector) other.getPart(partIndex);
			w.blochA1 = (ConditionalBlochVector) other.getPart(partIndex | otherPartIndexOfThis);
			w.oldPhaseA0 = w.ph[partIndex];
			w.oldPhaseA1 = w.ph[partIndex | otherPartIndexOfThis];
			
			partIndex = insertPartIndex(i, thisPartIndexOfOther);
			w.blochB0 = (ConditionalBlochVector) this.getPart(partIndex);
			w.blochB1 = (ConditionalBlochVector) this.getPart(partIndex | thisPartIndexOfOther);
			
			w.updateParts(theta, phi, lambda);
			
			if (debug) {
				// verify z
				double zb = w.blochB0.getZ() + w.blochB1.getZ();	// should equal pa0 - pa1
				logIf(Math.abs(w.blochA0.getProbability() - w.blochA1.getProbability() - zb) > MARGIN, getLabel() + " unexpected z");
				// TODO verify relative phases
			}
			
			// TODO set sumChanged only when needed
			sumChanged = true;
			
			x += w.blochB0.getX() + w.blochB1.getX();
			y += w.blochB0.getY() + w.blochB1.getY();
			z += w.blochB0.getZ() + w.blochB1.getZ();
			pSum += w.blochB0.getProbability();
			pSum += w.blochB1.getProbability();
		}
		
		if (sumChanged) {
			logIf(pSum != 1.0, getLabel() + " sum of probabilities == " + pSum);
			// update weighted average
			setXYZ(x, y, z);
		} else {
			// verify that the weighted average has indeed not changed (if enabled by Java VM option -ea)
//			assert Math.abs(x - getX()) < MARGIN;
//			assert Math.abs(y - getY()) < MARGIN;
//			assert Math.abs(z - getZ()) < MARGIN;
			check(Math.abs(z - getZ()) < MARGIN, getLabel() + ".z changed unexpectedly");
			check(Math.abs(y - getY()) < MARGIN, getLabel() + ".y changed unexpectedly");
			check(Math.abs(x - getX()) < MARGIN, getLabel() + ".x changed unexpectedly");
		}
	}

	/**
	 * Processes a measurement event on another qubit.
	 * <p>
	 * This is updating our knowledge about the correlation
	 * between this qubit and the other.
	 * 
	 * @param other	- the qubit that was measured
	 * @param down	- the measurement outcome, false if |0⟩, true if |1⟩
	 */
	void qubitMeasured(DecentralizedQubit other, boolean down) {
		final int partIndexOther = this.getPartIndex(other);
		int pairs = components() / 2;
		
		// calculate the probability of the outcome
		double p = 0.0;
		for (int i = 0; i < pairs; i++) {
			int index0 = insertPartIndex(i, partIndexOther);
			int index1 = index0 | partIndexOther;
			if (down) {
				p += getPart(index1).getMagnitude();
			} else {
				p += getPart(index0).getMagnitude();
			}
		}
		if (p == 1.0) return;
		logIf(p == 0.0, other.getLabel() + " had impossible measurement outcome for " + this.getLabel());
		
		// update the known probabilities and weighted average
		double x = 0.0;
		double y = 0.0;
		double z = 0.0;
		double pSum = 0.0;
		final double q = p;
		p = 0.0;
		for (int i = 0; i < pairs; i++) {
			int index0 = insertPartIndex(i, partIndexOther);
			int index1 = index0 | partIndexOther;
			ConditionalBlochVector part0 = (ConditionalBlochVector) getPart(index0);
			ConditionalBlochVector part1 = (ConditionalBlochVector) getPart(index1);
			if (down) {
				p += part1.getProbability();
				part0.setProbability(0.0);
				part1.setProbability(round(p/q - pSum));
				x += part1.getX();
				y += part1.getY();
				z += part1.getZ();
				pSum += part1.getProbability();
			} else {
				p += part0.getProbability();
				part0.setProbability(round(p/q - pSum));
				part1.setProbability(0.0);
				x += part0.getX();
				y += part0.getY();
				z += part0.getZ();
				pSum += part0.getProbability();
			}
		}
		logIf(pSum != 1.0, getLabel() + " sum of probabilities != 1.0");
		// update weighted average
		setXYZ(x, y, z);
		
		// update the measured qubit's probabilities accordingly
		other.qubitChanged(this, 0.0, 0.0, 0.0);
	}

	/**
	 * Returns the base part index for another qubit.
	 * 
	 * @param other	- the other qubit
	 * @return the base part index for the other qubit
	 */
	private int getPartIndex(DecentralizedQubit other) {
		int partIndex = other.id;
		if (partIndex > this.id) {
			partIndex >>= 1;			// partIndex /= 2;
		}
		return partIndex;
	}

	/**
	 * Inserts one bit at the specified location.
	 * <p>
	 * Some examples, using binary numbers
	 * (a and b can be 0 or 1 in any combination):
	 * <ol>
	 * <li>{@code insertPartIndex(ab, 001)} returns {@code ab0}</li>
	 * <li>{@code insertPartIndex(ab, 010)} returns {@code a0b}</li>
	 * <li>{@code insertPartIndex(ab, 100)} returns {@code 0ab}</li>
	 * </ol>
	 * 
	 * @param index		- target for insertion 
	 * @param location	- a mask that specifies the location
	 * @return the updated target
	 */
	private int insertPartIndex(int index, int location) {
		int left  = index / location;
		int right = index % location;
		return 2 * left * location + right;
	}

	/**
	 * Sets the given part to (0, 0, z*p),
	 * where p is the part's current probability.
	 * 
	 * @param part	- 
	 */
	private void setZ(ConditionalBlochVector part, double z) {
		// only called after measurement or reset, so z == ±1
		double p = part.getProbability();
		part.setPZ(p, z*p, 0.0);
	}

	/**
	 * @param p		- probability to be rounded
	 * @return		rounded probability
	 */
	@Override
	public double round(double p) {
		return Math.round(p / PRECISION) * PRECISION;
	}

	/**
	 * @param cond
	 * @param msg
	 */
	private void logIf(boolean cond, String msg) {
		if (debug && cond) {
			log(msg);
		}
	}

	/**
	 * Returns a (thread local) work area for calculations
	 * to reflect a state change in one qubit (A) into
	 * another (B).
	 * 
	 * @return	the work area
	 */
	private WorkArea getWorkArea() {
		WorkArea w = workArea.get();
		if (w == null) {
			w = new WorkArea();
			workArea.set(w);
		}
		if (w.ph == null || w.ph.length < components()) {
			w.ph = new double[components()];
		}
		w.base = this;
		return w;
	}

	//-----------------------------
	// Inner class WorkArea
	//-----------------------------

	/**
	 * 
	 */
	private static class WorkArea {
		private Base base;							// a Base object for rounding methods
		private ConditionalBlochVector blochA0;		// Bloch vector of changed qubit in case this qubit is measured |0⟩
		private ConditionalBlochVector blochA1;		// Bloch vector of changed qubit in case this qubit is measured |1⟩
		private ConditionalBlochVector blochA = new ConditionalBlochVector("sum");	// weighted sum of blochA0 and blochA1
		private ConditionalBlochVector blochB0;		// Bloch vector of this qubit in case changed qubit is measured |0⟩
		private ConditionalBlochVector blochB1;		// Bloch vector of this qubit in case changed qubit is measured |1⟩
		private ConditionalBlochVector blochB = new ConditionalBlochVector("sum");	// weighted sum of blochB0 and blochB1
		private double p0;							// new probability of blochB0
		private double p1;							// new probability of blochB1
		private double[] pr = new double[4];		// probabilities of related state vector dimensions in Hilbert space
		private double[] pc = new double[4];		// phase changes of related state vector dimensions caused by the change
		private double[] ph;						// phase history of changed qubit (component phases before change)
		private double oldPhaseA0;					// phase of BlochA0 before the change
		private double oldPhaseA1;					// phase of BlochA1 before the change

		private void updateParts(double theta, double phi, double lambda) {
			final double p = blochA0.getProbability() + blochA1.getProbability();
			if (p == 0.0 && blochB0.getProbability() + blochB1.getProbability() == 0.0) {
				// nothing can change for this set of conditional Bloch vectors
				return;
			}
			
			// calculate probabilities p0 for blochB0 and p1 for blochB1
			calculatePartProbabilities(p);
			
			// TODO replace theta != 0.0 by isRotation() and check/fix cases of U with theta == 0 and R with Axis.Z
			if (theta != 0.0 && blochA0.isAntiParallel(blochA1)) {
				// update parts after special state rotation (Bell state or similar)
				updatePartsFromState(theta, phi, lambda);
			} else {
				// update parts after measurement or other state rotation (not like Bell state)
				updatePartsFromOther(p);
			}
		}

		private void updatePartsFromState(double theta, double phi, double lambda) {
			// calculate z values directly and delta phases using half the rotation angle
			calculateStateProbabilitiesBeforeChange();
			calculateStatePhaseChanges(theta, phi, lambda);
			calculateStateProbabilitiesAfterChange();
			double z0 = pr[0b00] - pr[0b10];
			double z1 = pr[0b01] - pr[0b11];
			double d0 = pc[0b10] - pc[0b00];
			double d1 = pc[0b11] - pc[0b01];
			
			// update blochB0 and blochB1 of this qubit
			blochB0.setPZ(p0, z0, blochB0.getPhase() + d0);
			blochB1.setPZ(p1, z1, blochB1.getPhase() + d1);
		}

		private void updatePartsFromOther(double p) {
			// calculate z values and new phases in relative coordinates, then transform to lab coordinates
			calculateWeightedSum(blochA0, blochA1, blochA);
			calculateWeightedSum(blochB0, blochB1, blochB);
			
			// reconstruct blochB0 and blochB1 of this qubit
			// first get the relative phases from the other qubit
			blochB0.set(blochA0);
			blochB1.set(blochA1);
			toRel(blochB0, blochB1, blochA);
			
			// now construct a triangle of blochB0 (length p0), blochB1 (length p1) and blochB (with same length r as blochA)
			// z0 and z1 are the projections of blochB0 and blochB1 on blochB (which is virtually aligned with the z-axis for convenience)
			// these can be calculated with the cosine rule, simplified to avoid divide by zero,
			// using the fact that p0² - p1² == p * (blochA0.z + blochA1.z) and blochA.normalizedZ == (blochA0.z + blochA1.z) / r
			double r	= blochA.getMagnitude();
			double diff	= p * blochA.getNormalizedZ();			// p cos(blochA.theta) == (r0² - r1²) / r
			double z0	= (r + diff) * 0.5;						// z0 = (r² + r0² - r1²) / 2r
			double z1	= (r - diff) * 0.5;						// z1 = (r² + r1² - r0²) / 2r
			blochB0.setPZ(p0, z0, blochB0.getPhase());
			blochB1.setPZ(p1, z1, blochB1.getPhase());
			
			// restore direction of axes to lab coordinates
			toLab(blochB0, blochB1, blochB);
		}

		private void calculatePartProbabilities(double p) {
			// calculate updated probabilities for blochB0 and blochB1
			double z = base.round(blochA0.getZ() + blochA1.getZ());
			if (z > 0.0) {
				p1 = base.round((p - z) * 0.5);
				p0 = p - p1;
			} else {
				p0 = base.round((p + z) * 0.5);
				p1 = p - p0;
			}
		}

		private void calculateStateProbabilitiesAfterChange() {
			// calculate probabilities of partial state vector
			pr[0b00] = (blochA0.getProbability() + blochA0.getZ()) * 0.5;
			pr[0b01] = (blochA0.getProbability() - blochA0.getZ()) * 0.5;
			pr[0b10] = (blochA1.getProbability() + blochA1.getZ()) * 0.5;
			pr[0b11] = (blochA1.getProbability() - blochA1.getZ()) * 0.5;
		}

		private void calculateStateProbabilitiesBeforeChange() {
			// calculate probabilities of partial state vector before the change
			pr[0b00] = (blochB0.getProbability() + blochB0.getZ()) * 0.5;
			pr[0b01] = (blochB1.getProbability() + blochB1.getZ()) * 0.5;
			pr[0b10] = (blochB0.getProbability() - blochB0.getZ()) * 0.5;
			pr[0b11] = (blochB1.getProbability() - blochB1.getZ()) * 0.5;
		}

		private void calculateStatePhaseChanges(double theta, double phi, double lambda) {
			double halfTheta = theta * 0.5;
			calculateStatePhaseChanges(oldPhaseA0, 0, halfTheta, phi, lambda);
			calculateStatePhaseChanges(oldPhaseA1, 2, halfTheta, phi, lambda);
		}

		private void calculateStatePhaseChanges(double oldPhase, int i, double halfTheta, double phi, double lambda) {
			// calculate phase changes pc[i] and pc[i+1] from state before the change
			double m0 = Math.sqrt(pr[i]);
			double m1 = Math.sqrt(pr[i+1]);
			double cos = base.roundCos(Math.cos(halfTheta));
			double sin = base.roundCos(Math.sin(halfTheta));
			double phase = (m1 == 0) ? oldPhase + lambda : oldPhase + lambda;
			double nx = sin * base.roundCos(Math.cos(phase));
			double ny = sin * base.roundCos(Math.sin(phase));
			pc[i]	= Math.atan2(-m1*ny, m0*cos - m1*nx);
			pc[i+1]	= Math.atan2(-m0*ny, m1*cos + m0*nx);
		}

		/**
		 * Sets the sum object to the weighted sum of
		 * the two given conditional quantum parts.
		 * <p>
		 * Note: {@code part0 + part1} identifies one of two
		 * focus points of a spheroid like a rugby ball.
		 * The other focus point is the center of the Bloch ball.
		 * The center of this spheroid is in the middle between
		 * these two focus points, i.e. at {@code (part0 + part1)/2}.
		 * All possible combinations of part0 and part1 lie
		 * on this spheroid at opposite sides of the spheroid,
		 * as long as only one specific other qubit is rotated
		 * (by one or more single qubit rotations) in any way.
		 * <p>
		 * Multiple spheroids exist if the are more than two
		 * qubits in the system, one for each possible state
		 * of the other (3rd, 4th, etc) qubits.
		 * There are n-1 sets of spheroids in each qubit
		 * in an n-qubit system (one set for each other qubit,
		 * with 2^(n-2) spheroids per set.
		 * 
		 * @param part0	- conditional quantum part 0
		 * @param part1	- conditional quantum part 1
		 * @param sum	- object to be set to the sum of part0 and part1
		 */
		private void calculateWeightedSum(BlochVector part0, BlochVector part1, ConditionalBlochVector sum) {
			double x = part0.getX() + part1.getX();
			double y = part0.getY() + part1.getY();
			double z = part0.getZ() + part1.getZ();
			sum.setXYZ(x, y, z);
		}

		/**
		 * Transforms lab (external) coordinates of part0 and part1
		 * to relative (internal) coordinates.
		 * 
		 * @param part0	- conditional quantum part 0
		 * @param part1	- conditional quantum part 1
		 * @param axis	- direction of the relative z-axis (in lab coordinates)
		 */
		private void toRel(BlochVector part0, BlochVector part1, BlochVector axis) {
			double theta = axis.getTheta();
			if (theta == 0.0) return;
			double phase = axis.getPhase();
			part0.rz(-phase).ry(-theta);
			part1.rz(-phase).ry(-theta);
		}

		/**
		 * Transforms relative (internal) coordinates of part0 and part1
		 * back to lab (external) coordinates.
		 * 
		 * @param part0	- conditional quantum part 0
		 * @param part1	- conditional quantum part 1
		 * @param axis	- direction of the relative z-axis (in lab coordinates)
		 */
		private void toLab(BlochVector part0, BlochVector part1, BlochVector axis) {
			double theta = axis.getTheta();
			if (theta == 0.0) return;
			double phase = axis.getPhase();
			part0.ry(theta).rz(phase);
			part1.ry(theta).rz(phase);
		}
	}

	//-----------------------------
	// Inner class EventManager
	//-----------------------------

	/**
	 * 
	 */
	private static class EventManager {
		private int n = 0;							// number of registered qubits
		private DecentralizedQubit[] qubits;
		private String[] partLabels;

		EventManager(int maxQubits) {
			qubits = new DecentralizedQubit[maxQubits];
			initPartLabels();
		}

		private void initPartLabels() {
			int digits = qubits.length - 1;
			int size = 1 << digits;
			partLabels = new String[size];
			String format = "%" + Math.max(digits, 1) + "s";
			for (int i = 0; i < size; i++) {
				partLabels[i] = String.format(format, Integer.toBinaryString(i)).replace(' ', '0');
			}
		}

		String[] getPartLabels() {
			return partLabels;
		}

		int register(DecentralizedQubit q) {
			qubits[n] = q;
			return n++;
		}

		/**
		 * @param changed	- the qubit that changed
		 * @param theta		- first arg of universal operation (angle of polar rotation)
		 * @param phi		- second arg of universal operation (rotation axis relative to y-axis)
		 */
		void qubitChanged(DecentralizedQubit changed, double theta, double phi, double lambda) {
			for (int i = 0; i < qubits.length; i++) {
				DecentralizedQubit q = qubits[i];
				if (q != null && q != changed) {
					q.qubitChanged(changed, theta, phi, lambda);
				}
			}
		}

		/**
		 * @param measured	- the qubit that was measured
		 * @param down		- the measurement outcome, false if |0⟩, true if |1⟩
		 */
		void qubitMeasured(DecentralizedQubit measured, boolean down) {
			for (int i = 0; i < qubits.length; i++) {
				DecentralizedQubit q = qubits[i];
				if (q != null && q != measured) {
					q.qubitMeasured(measured, down);
				}
			}
		}
	}

	//-----------------------------
	// Inner class Factory
	//-----------------------------

	/**
	 * 
	 */
	private static class Factory implements IQubitFactory<DecentralizedQubit> {
		private EventManager eventManager;

		public Factory(EventManager eventManager) {
			this.eventManager = eventManager;
		}

		@Override
		public DecentralizedQubit newQubit(String label) {
			return new DecentralizedQubit(eventManager, label);
		}
	}

}
