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

import org.bemuisoft.math.base.SpatialVector;
import org.bemuisoft.quantum.api.Axis;
import org.bemuisoft.quantum.api.Base;
import org.bemuisoft.quantum.api.IQuantumState;
import org.bemuisoft.quantum.api.IQubit;
import org.bemuisoft.quantum.api.IQubitAnalyzer;

/**
 * Base implementation of an {@code IQubit} based on a {@code QuantumState}.
 * <p>
 * This implementation supports all operations defined by {@code IQubit},
 * except {@code measure()}.
 * To support measurement, the {@code getHiddenZ()} method must be overridden.
 * If the subclass does not support a hidden variable (Copenhagen interpretation),
 * this method should return {@code Double.NaN}.
 */
public class QubitState extends SpatialVector implements IQubitAnalyzer, Base {

	/** Debug flag. */
	public static boolean debug = false;

	private QuantumState qs;
	private String label;
	private int qIndex;
	private int qMask;
	private int stateId = -1;

	private BlochVector[] comps;
	private int nextComp;

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
	public QubitState(QuantumState qs, String label) {
		this.qs = qs;
		this.label = label;
		this.qIndex = qs.qubitIndex(qs.shortLabel(label));
		this.qMask = 1 << qIndex;
	}

	@Override
	public QuantumState getSystemState() {
		return qs;
	}

	@Override
	public String getLabel() {
		return label;
	}

	/**
	 * Returns the index that identifies this qubit
	 * in the related quantum system state.
	 * 
	 * @return	- the qubit index
	 */
	public int getIndex() {
		return qIndex;
	}

	/**
	 * Returns the mask that identifies this qubit's bit
	 * in the related quantum state's dimension index.
	 * 
	 * @return	- the qubit index
	 */
	int getMask() {
		return qMask;
	}

	private int mask(IQubit q) {
		return ((QubitState) q).qMask;
	}

	private int mask(IQubit... ctrl) {
		int mask = 0;
		for (IQubit q : ctrl) {
			mask |= mask(q);
		}
		return mask;
	}

	private void addComponent(BlochVector v) {
		BlochVector comp = comps[nextComp];
		if (comp == null) {
			comps[nextComp] = new BlochVector(v);
		} else {
			comp.set(v);
		}
		nextComp++;
	}

	private void add(BlochVector comp) {
//		log(label + ' ' + comp);
		super.x += comp.getX();
		super.y += comp.getY();
		super.z += comp.getZ();
		if (comps != null) {
			addComponent(comp);
		}
	}

	private void setXYZ(double x, double y, double z) {
		super.x = x;
		super.y = y;
		super.z = z;
	}

	private void synchState() {
		// check if not updated in other thread while waiting for the synchronization lock
		if (stateId == qs.stateIdentifier()) return;
		synchronized (qs) {
			nextComp = 0;
			setXYZ(0.0, 0.0, 0.0);
			qs.forEachComponent(qMask, comp -> add(comp));
			setXYZ(roundCos(x), roundCos(y), roundCos(z));
			stateId = qs.stateIdentifier();
		}
	}

	@Override
	public double getX() {
		synchState();
		return super.getX();
	}

	@Override
	public double getY() {
		synchState();
		return super.getY();
	}

	@Override
	public double getZ() {
		synchState();
		return super.getZ();
	}

	@Override
	public double getMagnitude() {
		return roundCos(IQubitAnalyzer.super.getMagnitude());
	}

	@Override
	public boolean isMixed() {
		return !isPure();
	}

	@Override
	public boolean isPure() {
		return (getMagnitude() > 1.0 - 1e-12);
	}

	@Override
	public int components() {
		return getComponents().length;
	}

	private BlochVector[] getComponents() {
		if (comps == null) {
			comps = new BlochVector[qs.size() / 2];
			stateId--;		// force update on synchState
		}
		return comps;
	}

	private BlochVector getComponent(int i) {
		BlochVector[] comps = getComponents();
		synchState();
		return comps[i];
	}

	@Override
	public double getX(int i) {
		return getComponent(i).getX();
	}

	@Override
	public double getY(int i) {
		return getComponent(i).getY();
	}

	@Override
	public double getZ(int i) {
		return getComponent(i).getZ();
	}

	/**
	 * Allows a subclass to return a hidden value in the range [-1, 1].
	 * <p>
	 * If a subclass does that, this value will be used in combination
	 * with the z-value of the Bloch vector of this qubit to determine
	 * the result of a measurement.
	 * If the sum (or average) of the Bloch vector's z-value and this
	 * hidden z-value is positive (closer to +1), the result is |0⟩,
	 * otherwise the result is |1⟩.
	 * Note that sum or average makes no difference when comparing to zero.
	 * <p>
	 * If a subclass returns {@code Double.NaN} or a value outside
	 * the range [-1, 1], this value will be ignored.
	 * In that case, a random number will be generated to determine the
	 * result of the measurement.
	 * This is consistent with the Copenhagen interpretation.
	 * 
	 * @return	a value in the range [-1, 1] or NaN
	 * @throws	UnsupportedOperationException if this method is not overridden
	 */
	@Override
	public double getHiddenZ() {
		throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support measurements.");
	}

	/**
	 * Returns the result of a "weak" measurement.
	 * This measurement does not change the quantum state in any way.
	 * 
	 * @return	{@code true} if this qubit would be measured as |1⟩, {@code false} otherwise
	 */
	protected boolean isMinus() {
		double lambda = getHiddenZ();
		if (Double.isNaN(lambda) || lambda < -1.0 || lambda > 1.0) {
			// no hidden value or invalid value - let "God play dice"
			lambda = randomCos();
		}
		return (getZ() + lambda) < 0.0;
	}

	@Override
	public int measure() {
		int result = isMinus() ? 1 : 0;
		debug(label + " result = " + result);
		qs.setMeasured(result, qMask);
		return result;
	}

	@Override
	public QubitState reset() {
//		qs.setMeasured(0, qMask);		// a forced reset when p0 = 0 (because z = -1) gives loss of information in QuantumState
		if (getProbability0() < 1e-16) {
			x();
		} else {
			qs.setMeasured(0, qMask);
		}
		return this;
	}

	@Override
	public QubitState h() {
		u(HALF_PI, 0.0, PI);
		return this;
	}

	@Override
	public QubitState x() {
		qs.rotate(Axis.X, qMask);
		return this;
	}

	@Override
	public QubitState y() {
		qs.rotate(Axis.Y, qMask);
		return this;
	}

	@Override
	public QubitState z() {
		p(PI);
		return this;
	}

	@Override
	public QubitState s() {
		p(HALF_PI);
		return this;
	}

	@Override
	public QubitState sdg() {
		p(-HALF_PI);
		return this;
	}

	@Override
	public QubitState t() {
		p(QUARTER_PI);
		return this;
	}

	@Override
	public QubitState tdg() {
		p(-QUARTER_PI);
		return this;
	}

	@Override
	public QubitState p(double radians) {
		qs.shiftPhase(radians, qMask);
		return this;
	}

	@Override
	public QubitState sx() {
		u(HALF_PI, 0.0, HALF_PI);
		r(Axis.Z, -HALF_PI);
		return this;
	}

	@Override
	public QubitState sxdg() {
		u(HALF_PI, 0.0, -HALF_PI);
		r(Axis.Z, HALF_PI);
		return this;
	}

	@Override
	public QubitState rx(double radians) {
		u(radians, -HALF_PI, HALF_PI);
		return this;
	}

	@Override
	public QubitState ry(double radians) {
		u(radians, 0.0, 0.0);
		return this;
	}

	@Override
	public QubitState rz(double radians) {
		r(Axis.Z, radians);
		return this;
	}

	@Override
	public QubitState r(Axis axis, double radians) {
		qs.rotate( axis, radians, qMask);
		return this;
	}

	@Override
	public QubitState u(double theta, double phi, double lambda) {
		qs.rotate(theta, phi, lambda, qMask);
		return this;
	}

	@Override
	public QubitState cx(IQubit ctrl) {
		qs.rotate(Axis.X, qMask, mask(ctrl));
		return this;
	}

	@Override
	public QubitState ccx(IQubit ctrl1, IQubit ctrl2) {
		qs.rotate(Axis.X, qMask, mask(ctrl1) | mask(ctrl2));
		return this;
	}

	@Override
	public QubitState cz(IQubit ctrl) {
		cp(PI, ctrl);
		return this;
	}

	@Override
	public QubitState ccz(IQubit ctrl1, IQubit ctrl2) {
		ccp(PI, ctrl1, ctrl2);
		return this;
	}

	@Override
	public QubitState cp(double radians, IQubit ctrl) {
		qs.shiftPhase(radians, qMask | mask(ctrl));
		return this;
	}

	@Override
	public QubitState ccp(double radians, IQubit ctrl1, IQubit ctrl2) {
		qs.shiftPhase(radians, qMask | mask(ctrl1) | mask(ctrl2));
		return this;
	}

	@Override
	public QubitState c(Axis axis, IQubit ctrl) {
		qs.rotate(axis, qMask, mask(ctrl));
		return this;
	}

	@Override
	public QubitState c(Axis axis, IQubit... ctrl) {
		qs.rotate(axis, qMask, mask(ctrl));
		return this;
	}

	@Override
	public QubitState cr(Axis axis, double radians, IQubit ctrl) {
		qs.rotate(axis, radians, qMask, mask(ctrl));
		return this;
	}

	@Override
	public QubitState cr(Axis axis, double radians, IQubit... ctrl) {
		qs.rotate(axis, radians, qMask, mask(ctrl));
		return this;
	}

	@Override
	public QubitState cu(double theta, double phi, double lambda, IQubit ctrl) {
		qs.rotate(theta, phi, lambda, qMask, mask(ctrl));
		return this;
	}

	@Override
	public QubitState cu(double theta, double phi, double lambda, IQubit... ctrl) {
		qs.rotate(theta, phi, lambda, qMask, mask(ctrl));
		return this;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ' ' + label +
				" {x=" + roundSmart(getX()) +
				", y=" + roundSmart(getY()) +
				", z=" + roundSmart(getZ()) +
				", ϕ=" + toPi(Math.atan2(getY(), getX())) +
				"}";
	}

	/**
	 * Writes this qubit state to the system log.
	 */
	public void log() {
		log(toString());
	}

	/**
	 * Writes the specified message to the system log
	 * if and only if the debug flag is switched on.
	 * 
	 * @param msg	- a debug message
	 * @see #debug
	 */
	public void debug(String msg) {
		if (debug) {
			log(msg);
		}
	}

}
