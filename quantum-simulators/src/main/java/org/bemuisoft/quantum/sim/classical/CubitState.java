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

import java.util.ArrayList;
import java.util.function.Consumer;

import org.bemuisoft.math.base.SpatialVector;
import org.bemuisoft.quantum.api.Axis;
import org.bemuisoft.quantum.api.Base;
import org.bemuisoft.quantum.api.IQubit;
import org.bemuisoft.quantum.api.IQubitAnalyzer;
import org.bemuisoft.quantum.core.BlochVector;

/**
 * Base implementation of a classical universal bit (cubit),
 * like a toy model.
 * It resembles {@code QubitState} in some respects,
 * but it does not behave the same, not in the quantum way.
 * <p>
 * This implementation supports all operations defined by {@code IQubit},
 * except {@code measure()} and controlled rotations.
 * <p>
 * Default code provides a symmetric implementation of controlled
 * rotations, but these rely on a consistent random value,
 * which must be provided by an override of {@code getHiddenZ()}.
 * <p>
 * Consistent means the same value must be returned for
 * consecutive executions of the same gate,
 * for the symmetry to work.
 * <p>
 * To support these, the {@code getHiddenZ()} method must be overridden.
 * If the subclass does not support a hidden variable (Copenhagen interpretation),
 * this method should return {@code Double.NaN}
 * and supported controlled rotations must be overridden
 * with an implementation that does not depend on a
 * consistent random value.
 */
public class CubitState extends SpatialVector implements IQubitAnalyzer, Base {

	/** Debug flag. */
	public static boolean debug = false;

	private String label;
	private ArrayList<BlochVector> parts;

	/**
	 * Constructs a cubit with one Bloch vector.
	 * 
	 * @param label	- a label
	 */
	public CubitState(String label) {
		this(label, 1);
	}

	/**
	 * Constructs a cubit with the specified
	 * number of parts (Bloch vectors).
	 * 
	 * @param label	- a label
	 * @param n		- number of parts
	 */
	protected CubitState(String label, int n) {
		super(0.0, 0.0, 1.0);
		this.label = label;
		if (n > 0) {
			initialzeParts(n);
		}
	}

	/**
	 * Allows a subclass to initialize the parts list for this cubit.
	 * <p>
	 * This method is called once, during construction.
	 * Default action is to initialize the parts list with
	 * n zero vectors, where n is the number specified on construction.
	 * 
	 * @param	n - number specified on construction
	 */
	protected void initialzeParts(int n) {
		ArrayList<BlochVector> parts = new ArrayList<BlochVector>(n);
		for (int i = 0; i < n; i++) {
			parts.add(new BlochVector());
		}
		setParts(parts);
	}

	/**
	 * Sets the internal parts of this cubit.
	 * 
	 * @param	parts	- the parts ArrayList
	 */
	protected void setParts(ArrayList<BlochVector> parts) {
		this.parts = parts;
	}

	/**
	 * Returns the internal parts of this cubit.
	 * 
	 * @return	the parts ArrayList
	 */
	protected ArrayList<BlochVector> getParts() {
		return parts;
	}

	/**
	 * Returns this cubit's internal part at the specified index.
	 * 
	 * @param	index		- index of the part
	 * @return	the part
	 */
	protected BlochVector getPart(int index) {
		return parts.get(index);
	}

	/**
	 * Performs the given action on each part in the parts list.
	 * 
	 * @param	action	- the action to perform
	 */
	protected void forEach(Consumer<BlochVector> action) {
		for (BlochVector part : parts) {
			if (part != null) {
				action.accept(part);
			}
		}
	}

	/**
	 * Returns this cubit's label.
	 * 
	 * @return	the label
	 */
	public String getLabel() {
		return label;
	}

	@Override
	public double getX() {
		// just a  default implementation; override when needed
		return getPart(0).getX();
	}

	@Override
	public double getY() {
		// just a  default implementation; override when needed
		return getPart(0).getY();
	}

	@Override
	public double getZ() {
		// just a  default implementation; override when needed
		return getPart(0).getZ();
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
		debug("measured |" + result + '⟩');
		getPart(0).setXYZ(0.0, 0.0, 1.0 - 2.0*result);
		return result;
	}

	@Override
	public CubitState reset() {
		debug("reset");
		getPart(0).setXYZ(0.0, 0.0, 1.0);
		return this;
	}

	@Override
	public CubitState h() {
		debug("H");
		forEach(part -> part.h());
		return this;
	}

	@Override
	public CubitState x() {
		debug("X");
		forEach(part -> part.x());
		return this;
	}

	@Override
	public CubitState y() {
		debug("Y");
		forEach(part -> part.y());
		return this;
	}

	@Override
	public CubitState z() {
		debug("Z");
		forEach(part -> part.z());
		return this;
	}

	@Override
	public CubitState s() {
		debug("S");
		forEach(part -> part.s());
		return this;
	}

	@Override
	public CubitState sdg() {
		debug("Sdg");
		forEach(part -> part.sdg());
		return this;
	}

	@Override
	public CubitState t() {
		debug("T");
		forEach(part -> part.t());
		return this;
	}

	@Override
	public CubitState tdg() {
		debug("Tdg");
		forEach(part -> part.tdg());
		return this;
	}

	@Override
	public CubitState p(double radians) {
		debug("P", radians);
		forEach(part -> part.p(radians));
		return this;
	}

	@Override
	public CubitState sx() {
		debug("SX");
		forEach(part -> part.sx());
		return this;
	}

	@Override
	public CubitState sxdg() {
		debug("SXdg");
		forEach(part -> part.sxdg());
		return this;
	}

	@Override
	public CubitState rx(double radians) {
		debug("RX", radians);
		forEach(part -> part.rx(radians));
		return this;
	}

	@Override
	public CubitState ry(double radians) {
		debug("RY", radians);
		forEach(part -> part.ry(radians));
		return this;
	}

	@Override
	public CubitState rz(double radians) {
		debug("RZ", radians);
		forEach(part -> part.rz(radians));
		return this;
	}

	@Override
	public CubitState r(Axis axis, double radians) {
		debug("R", axis.getTheta(), axis.getPhi(), radians);
		forEach(part -> part.r(axis, radians));
		return this;
	}

	@Override
	public CubitState u(double theta, double phi, double lambda) {
		debug("U", theta, phi, lambda);
		forEach(part -> part.u(theta, phi, lambda));
		return this;
	}

	@Override
	public CubitState cx(IQubit ctrl) {
		debug("CX", (CubitState) ctrl);
		cr(Axis.X, PI, (CubitState) ctrl);
		return this;
	}

	@Override
	public CubitState cz(IQubit ctrl) {
		debug("CZ", (CubitState) ctrl);
		cp(PI, (CubitState) ctrl);
		return this;
	}

	@Override
	public CubitState cp(double radians, IQubit ctrl) {
		debug("CP", radians, (CubitState) ctrl);
		cp(radians, (CubitState) ctrl);
		return this;
	}

	/**
	 * Applies a controlled phase shift.
	 * <p>
	 * This operation is symmetric, i.e.
	 * {@code this.cp(lambda, ctrl)} is equivalent to {@code ctrl.cp(lambda, this)}.
	 * 
	 * @param radians - the controlled angle to rotate (lambda)
	 * @param ctrl - the control qubit
	 */
	protected void cp(double radians, CubitState ctrl) {
		final CubitState trgt = this;
		
		// perform controlled phase shift on target cubit (simple solution, not perfect)
		if (ctrl.isMinus()) {
			trgt.rz(radians);
		}
		// apply phase kickback on control qubit
		if (trgt.isMinus()) {
			ctrl.rz(radians);
		}
	}

	@Override
	public CubitState c(Axis axis, IQubit ctrl) {
		debug("C", axis.getTheta(), axis.getPhi(), PI, (CubitState) ctrl);
		cr(axis, PI, (CubitState) ctrl);
		return this;
	}

	@Override
	public CubitState cr(Axis axis, double radians, IQubit ctrl) {
		debug("CR", axis.getTheta(), axis.getPhi(), radians, (CubitState) ctrl);
		cr(axis, radians, (CubitState) ctrl);
		ctrl.rz(-radians/2);					// compensate phase kickback on control qubit
		return this;
	}

	/**
	 * Applies a "clean" controlled rotation to this qubit.
	 * In all cases, the control qubit is affected by a phase kickback over the same angle,
	 * which is controlled by how this qubit (the target) would be measured along the specified axis.
	 * This phase kickback is NOT compensated by an extra phase shift of -lambda/2 on the control qubit.
	 * 
	 * @param axis - the rotation axis
	 * @param radians - the controlled angle to rotate (lambda)
	 * @param ctrl - the control qubit
	 */
	protected void cr(Axis axis, double radians, CubitState ctrl) {
		final double theta = axis.getTheta();
		final double phase = axis.getPhi();
		
		boolean dbg = debug;
		if (dbg) debug = false;
		
		// align rotation axis with z-axis
		if (phase != 0.0) rz(-phase);
		if (theta != 0.0) ry(-theta);
		
		// perform controlled phase shift
		cp(radians, ctrl);
		
		// re-align with rotation axis
		if (theta != 0.0) ry(theta);
		if (phase != 0.0) rz(phase);
		
		if (dbg) debug = true;
	}

	@Override
	public CubitState cu(double theta, double phi, double lambda, IQubit ctrl) {
		debug("CU", theta, phi, lambda, (CubitState) ctrl);
		if (lambda != 0.0) cp(lambda, (CubitState) ctrl);
		if (theta  != 0.0) cr(Axis.Y, theta, (CubitState) ctrl);
		if (phi    != 0.0) cp(phi,    (CubitState) ctrl);
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
	 * Writes this cubit state to the system log.
	 */
	public void log() {
		log(toString());
	}

	/**
	 * Writes the specified operation on this cubit to the system log
	 * if and only if the debug flag is switched on.
	 * 
	 * @param oper	- operation name
	 * @see #debug
	 */
	protected void debug(String oper) {
		if (debug) {
			StringBuilder sb = new StringBuilder();
			sb.append(getLabel()).append('.').append(oper);
			log(sb.toString());
		}
	}

	/**
	 * Writes the specified operation on this cubit to the system log
	 * if and only if the debug flag is switched on.
	 * 
	 * @param oper	- operation name
	 * @param ctrl	- control cubit
	 * @see #debug
	 */
	protected void debug(String oper, CubitState ctrl) {
		if (debug) {
			StringBuilder sb = new StringBuilder();
			sb.append(getLabel()).append('.').append(oper);
			sb.append('(').append(ctrl.getLabel()).append(')');
			log(sb.toString());
		}
	}

	/**
	 * Writes the specified operation on this cubit to the system log
	 * if and only if the debug flag is switched on.
	 * 
	 * @param oper		- operation name
	 * @param radians	- rotation angle
	 * @see #debug
	 */
	protected void debug(String oper, double radians) {
		if (debug) {
			StringBuilder sb = new StringBuilder();
			sb.append(getLabel()).append('.').append(oper);
			sb.append('(').append(round(radians/PI, 5)).append("π)");
			log(sb.toString());
		}
	}

	/**
	 * Writes the specified operation on this cubit to the system log
	 * if and only if the debug flag is switched on.
	 * 
	 * @param oper		- operation name
	 * @param radians	- rotation angle
	 * @param ctrl		- control cubit
	 * @see #debug
	 */
	protected void debug(String oper, double radians, CubitState ctrl) {
		if (debug) {
			StringBuilder sb = new StringBuilder();
			sb.append(getLabel()).append('.').append(oper);
			sb.append('(').append(round(radians/PI, 5)).append("π, ").append(ctrl.getLabel()).append(')');
			log(sb.toString());
		}
	}

	/**
	 * Writes the specified operation on this cubit to the system log
	 * if and only if the debug flag is switched on.
	 * 
	 * @param oper		- operation name
	 * @param theta		- theta
	 * @param phi		- phi
	 * @param lambda	- lambda
	 * @see #debug
	 */
	protected void debug(String oper, double theta, double phi, double lambda) {
		if (debug) {
			StringBuilder sb = new StringBuilder();
			sb.append(getLabel()).append('.').append(oper);
			sb.append("(θ=").append(round(theta/PI, 5)).append("π,");
			sb.append(" ϕ=").append(round(phi/PI, 5)).append("π,");
			sb.append(" λ=").append(round(lambda/PI, 5)).append("π)");
			log(sb.toString());
		}
	}

	/**
	 * Writes the specified operation on this cubit to the system log
	 * if and only if the debug flag is switched on.
	 * 
	 * @param oper		- operation name
	 * @param theta		- theta
	 * @param phi		- phi
	 * @param lambda	- lambda
	 * @param ctrl		- control cubit
	 * @see #debug
	 */
	protected void debug(String oper, double theta, double phi, double lambda, CubitState ctrl) {
		if (debug) {
			StringBuilder sb = new StringBuilder();
			sb.append(getLabel()).append('.').append(oper);
			sb.append("(θ=").append(round(theta/PI, 5)).append("π,");
			sb.append(" ϕ=").append(round(phi/PI, 5)).append("π,");
			sb.append(" λ=").append(round(lambda/PI, 5)).append("π, ");
			sb.append(ctrl.getLabel()).append(')');
			log(sb.toString());
		}
	}

}
