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
package org.bemuisoft.qm.sim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.bemuisoft.qm.core.SimpleBlochVector;
import org.bemuisoft.qm.core.Gate;
import org.bemuisoft.qm.core.PureQuantumSystem;
import org.bemuisoft.qm.core.QuantumSystem;
import org.bemuisoft.quantum.api.Axis;
import org.bemuisoft.quantum.api.IQuantumState;
import org.bemuisoft.quantum.api.IQubit;
import org.bemuisoft.quantum.api.IQubitAnalyzer;
import org.bemuisoft.quantum.api.QubitFactory;

/**
 * Implementation of a qubit that can be used as interface
 * to a quantum system.
 * <p>
 * This qubit implementation works a QM textbook (heavyweight)
 * quantum system {@link PureQuantumSystem}
 * or the more general {@link QuantumSystem}.
 * 
 * @author Benno Muilwijk
 * @see IQubit
 * @see IQubitAnalyzer
 * @see QuantumSystem
 * @see PureQuantumSystem
 */
public class QubitQM extends SimpleBlochVector implements IQubitAnalyzer {

	/** Debug flag. */
	public static boolean debug = false;

	private QuantumSystem qs;
	private int qIndex;
	private int stateId = -1;
	private double lambda = Double.NaN;
	private List<SimpleBlochVector> comps;

	/**
	 * Returns a qubit factory for {@code QubitQM}.
	 * 
	 * @param n				maximum number of qubits
	 * @param rightToLeft	direction of qubit label assignment
	 * @return				a qubit factory for {@code QubitQM}
	 * @see					IQuantumState#isRightToLeft()
	 */
	public static QubitFactory<QubitQM> factory(int n, boolean rightToLeft) {
		PureQuantumSystem qs = new PureQuantumSystem(n, rightToLeft);
		return new QubitFactory<>(qs, QubitQM.class);
	}

	/**
	 * Constructs a qubit with a shared {@link QuantumSystem}.
	 * <p>
	 * <i>Warning: if the specified quantum system
	 * is not a {@link PureQuantumSystem},
	 * this qubit cannot be measured or reset
	 * individually.</i>
	 * 
	 * @param qs	the quantum system to share
	 * @param label	a capital as short identifying label
	 * @see			IQuantumState#isRightToLeft()
	 */
	public QubitQM(QuantumSystem qs, char label) {
		super(String.valueOf(label));
		this.qs = qs;
		this.qIndex = qs.qubitIndex(label);
	}

	/**
	 * Constructs a qubit with a shared {@link PureQuantumSystem}.
	 * 
	 * @param qs	the quantum system to share
	 * @param label	a capital as short identifying label
	 * @see			IQuantumState#isRightToLeft()
	 */
	public QubitQM(PureQuantumSystem qs, char label) {
		this((QuantumSystem) qs, label);
	}

	/**
	 * Constructs a qubit with a shared {@link PureQuantumSystem}.
	 * <p>
	 * The qubit is assigned a long label, but the identifying short label
	 * will be derived from the last character of the long label.
	 * Therefore, the last character must in a consecutive range
	 * starting at either 0 or A or a.
	 * 
	 * @param qs		the quantum system to share
	 * @param label		a long label with identifying last character
	 * @see				IQuantumState#isRightToLeft()
	 */
	public QubitQM(PureQuantumSystem qs, String label) {
		super(label);
		this.qs = qs;
		this.qIndex = qs.qubitIndex(qs.shortLabel(label));
	}

	private void synchState() {
		// refresh this qubit's Bloch vector and its components
		// if the quantum system's state has changed
		if (stateId != qs.stateIdentifier()) synchronized(qs) {
			comps = qs.blochVectorComponents(qIndex);
			super.reset();
			for (SimpleBlochVector v : comps) {
				add(v);
			}
			stateId = qs.stateIdentifier();
		}
	}

	@Override
	public PureQuantumSystem getSystemState() {
		if (qs instanceof PureQuantumSystem) {
			return (PureQuantumSystem) qs;
		}
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the index that identifies this qubit
	 * in the related quantum system.
	 * 
	 * @return	the qubit index
	 */
	public int getIndex() {
		return qIndex;
	}

	/**
	 * Unsafe but fast version of {@link #getComponents()},
	 * for careful use within this Java package only.
	 * It gives direct access to the Bloch vector components.
	 * 
	 * @return	list of this qubit's Bloch vector components
	 */
	List<SimpleBlochVector> getComps() {
		// default protection (for use in this package only), because this method is not safe
		// use with care; ensure that comps is not changed in any way other than via synchState()
		// use public method getComponents() when the safe usage cannot be guaranteed
		synchState();
		return comps;
	}

	/**
	 * Returns a list of this qubit's Bloch vector components.
	 * 
	 * @return	list of this qubit's Bloch vector components
	 */
	public List<SimpleBlochVector> getComponents() {
		// a safe version for public use; must return a copy of comps on each invocation,
		// to prevent any other class from changing the contents of comps
		return new ArrayList<>(getComps());
	}

	@Override
	public int components() {
		return getComps().size();
	}

	@Override
	public double getX(int i) {
		return getComps().get(i).getX();
	}

	@Override
	public double getY(int i) {
		return getComps().get(i).getY();
	}

	@Override
	public double getZ(int i) {
		return getComps().get(i).getZ();
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
	public double length() {
		synchState();
		return super.length();
	}

	@Override
	public double getMagnitude() {
		return length();
	}

	@Override
	public double getTheta() {
		synchState();
		return super.getTheta();
	}

	@Override
	public double getPhase() {
		synchState();
		return super.getPhase();
	}

	@Override
	public double getProbability0() {
		return round((1.0 + getZ()) / 2.0);
	}

	@Override
	public double getProbability1() {
		return round((1.0 - getZ()) / 2.0);
	}

	@Override
	public double getHiddenZ() {
		return lambda;
	}

	@Override
	public void setHiddenZ(double z) {
		lambda = z;
	}

	@Override
	public int measure() {
		double lambda = getHiddenZ();
		if (Double.isNaN(lambda) || lambda < -1.0 || lambda > 1.0) {
			// roll the dice
			lambda = randomCos();
		}
		int outcome = (getZ() + lambda < 0.0) ? 1 : 0;
		debug("measured |" + outcome + '⟩');
		getSystemState().setMeasured(outcome, qIndex);
		return outcome;
	}

	@Override
	public QubitQM reset() {
		debug("reset");
		if (getProbability0() < 1e-14) {
			getSystemState().apply(Gate.x(), qIndex);
		} else {
			getSystemState().setMeasured(0, qIndex);
		}
		lambda = Double.NaN;
		return this;
	}

	@Override
	public QubitQM h() {
		debug("H");
		qs.apply(Gate.h(), qIndex);
		return this;
	}

	@Override
	public QubitQM x() {
		debug("X");
		qs.apply(Gate.x(), qIndex);
		return this;
	}

	@Override
	public QubitQM y() {
		debug("Y");
		qs.apply(Gate.y(), qIndex);
		return this;
	}

	@Override
	public QubitQM z() {
		debug("Z");
		qs.apply(Gate.z(), qIndex);
		return this;
	}

	@Override
	public QubitQM s() {
		debug("S");
		qs.apply(Gate.s(), qIndex);
		return this;
	}

	@Override
	public QubitQM sdg() {
		debug("Sdg");
		qs.apply(Gate.sdg(), qIndex);
		return this;
	}

	@Override
	public QubitQM t() {
		debug("T");
		qs.apply(Gate.t(), qIndex);
		return this;
	}

	@Override
	public QubitQM tdg() {
		debug("Tdg");
		qs.apply(Gate.tdg(), qIndex);
		return this;
	}

	@Override
	public QubitQM p(double radians) {
		debug("P", radians);
		qs.apply(Gate.p(radians), qIndex);
		return this;
	}

	@Override
	public QubitQM sx() {
		debug("SX");
		qs.apply(Gate.sx(), qIndex);
		return this;
	}

	@Override
	public QubitQM sxdg() {
		debug("SXdg");
		qs.apply(Gate.sxdg(), qIndex);
		return this;
	}

	@Override
	public QubitQM rx(double radians) {
		debug("RX", radians);
		qs.apply(Gate.rx(radians), qIndex);
		return this;
	}

	@Override
	public QubitQM ry(double radians) {
		debug("RY", radians);
		qs.apply(Gate.ry(radians), qIndex);
		return this;
	}

	@Override
	public QubitQM rz(double radians) {
		debug("RZ", radians);
		qs.apply(Gate.rz(radians), qIndex);
		return this;
	}

	@Override
	public QubitQM r(Axis axis, double radians) {
		debug("R", axis.getTheta(), axis.getPhi(), radians);
		qs.apply(Gate.rz(radians).tilt(axis.getTheta(), axis.getPhi()), qIndex);
		return this;
	}

	@Override
	public QubitQM u(double theta, double phi, double lambda) {
		debug("U", theta, phi, lambda);
		qs.apply(Gate.u(theta, phi, lambda), qIndex);
		return this;
	}

	@Override
	public QubitQM cx(IQubit ctrl) {
		return cx((QubitQM) ctrl);
	}

	/**
	 * See {@link IQubit#cx(IQubit)}.
	 * 
	 * @param ctrl	the control qubit
	 * @return		this qubit (the target)
	 */
	public QubitQM cx(QubitQM ctrl) {
		debug("CX", ctrl);
		qs.apply(Gate.x(), ctrl.qIndex, this.qIndex);
		return this;
	}

	@Override
	public QubitQM ccx(IQubit ctrl1, IQubit ctrl2) {
		debug("CCX", ctrl1, ctrl2);
		qs.apply(Gate.x(), indices(ctrl1, ctrl2), this.qIndex);
		return this;
	}

	@Override
	public QubitQM cz(IQubit ctrl) {
		return cz((QubitQM) ctrl);
	}

	/**
	 * See {@link IQubit#cz(IQubit)}.
	 * 
	 * @param ctrl	the control qubit
	 * @return		this qubit (the target)
	 */
	public QubitQM cz(QubitQM ctrl) {
		debug("CZ", ctrl);
		qs.apply(Gate.z(), ctrl.qIndex, this.qIndex);
		return this;
	}

	@Override
	public QubitQM ccz(IQubit ctrl1, IQubit ctrl2) {
		debug("CCZ", ctrl1, ctrl2);
		qs.apply(Gate.z(), indices(ctrl1, ctrl2), this.qIndex);
		return this;
	}

	@Override
	public QubitQM c(Axis axis, IQubit ctrl) {
		return c(axis, (QubitQM) ctrl);
	}

	/**
	 * See {@link IQubit#c(Axis, IQubit)}.
	 * 
	 * @param axis	the rotation axis
	 * @param ctrl	the control qubit
	 * @return		this qubit (the target)
	 */
	public QubitQM c(Axis axis, QubitQM ctrl) {
		debug("C", axis.getTheta(), axis.getPhi(), PI, ctrl);
		qs.apply(Gate.z().tilt(axis.getTheta(), axis.getPhi()), ctrl.qIndex, this.qIndex);
		return this;
	}

	@Override
	public QubitQM c(Axis axis, IQubit... ctrl) {
		debug("Cn", axis.getTheta(), axis.getPhi(), PI, ctrl);
		qs.apply(Gate.z().tilt(axis.getTheta(), axis.getPhi()), indices(ctrl), this.qIndex);
		return this;
	}

	@Override
	public QubitQM cp(double radians, IQubit ctrl) {
		return cp(radians, (QubitQM) ctrl);
	}

	/**
	 * See {@link IQubit#cp(double, IQubit)}.
	 * 
	 * @param radians	the controlled angle to rotate
	 * @param ctrl		the control qubit
	 * @return			this qubit (the target)
	 */
	public QubitQM cp(double radians, QubitQM ctrl) {
		debug("CP", radians, ctrl);
		qs.apply(Gate.p(radians), ctrl.qIndex, this.qIndex);
		return this;
	}

	@Override
	public QubitQM ccp(double radians, IQubit ctrl1, IQubit ctrl2) {
		debug("CCP", radians, ctrl1, ctrl2);
		qs.apply(Gate.p(radians), indices(ctrl1, ctrl2), this.qIndex);
		return this;
	}

	@Override
	public QubitQM cr(Axis axis, double radians, IQubit ctrl) {
		return cr(axis, radians, (QubitQM) ctrl);
	}

	/**
	 * See {@link IQubit#cr(Axis, double, IQubit)}.
	 * 
	 * @param axis		the rotation axis
	 * @param radians	the controlled angle to rotate
	 * @param ctrl		the control qubit
	 * @return			this qubit (the target)
	 */
	public QubitQM cr(Axis axis, double radians, QubitQM ctrl) {
		debug("CR", axis.getTheta(), axis.getPhi(), radians, ctrl);
		qs.apply(Gate.rz(radians).tilt(axis.getTheta(), axis.getPhi()), ctrl.qIndex, this.qIndex);
		return this;
	}

	@Override
	public QubitQM cr(Axis axis, double radians, IQubit... ctrl) {
		debug("CnR", axis.getTheta(), axis.getPhi(), radians, ctrl);
		qs.apply(Gate.rz(radians).tilt(axis.getTheta(), axis.getPhi()), indices(ctrl), this.qIndex);
		return this;
	}

	@Override
	public QubitQM cu(double theta, double phi, double lambda, IQubit ctrl) {
		return cu(theta, phi, lambda, (QubitQM) ctrl);
	}

	/**
	 * See {@link IQubit#cu(double, double, double, IQubit)}.
	 * 
	 * @param theta		theta
	 * @param phi		phi
	 * @param lambda	lambda
	 * @param ctrl		the control qubit
	 * @return			this qubit (the target)
	 */
	public QubitQM cu(double theta, double phi, double lambda, QubitQM ctrl) {
		debug("CU", theta, phi, lambda, ctrl);
		qs.apply(Gate.u(theta, phi, lambda), ctrl.qIndex, this.qIndex);
		return this;
	}

	@Override
	public QubitQM cu(double theta, double phi, double lambda, IQubit... ctrl) {
		debug("CnU", theta, phi, lambda, ctrl);
		qs.apply(Gate.u(theta, phi, lambda), indices(ctrl), this.qIndex);
		return this;
	}

	@Override
	public StringBuilder toStringBuilder() {
		StringBuilder sb = super.toStringBuilder();
		sb.insert(sb.length() - 1, ", p1=");
		sb.insert(sb.length() - 1, getProbability1());
		return sb;
	}

	private Collection<Integer> indices(IQubit... ctrl) {
		// return a collection of qubit indices
		Collection<Integer> list;
		if (ctrl.length > 2) {
			list = new HashSet<Integer>(ctrl.length * 2);
		} else {
			list = new ArrayList<Integer>(ctrl.length);
		}
		for (IQubit q : ctrl) {
			list.add(((QubitQM) q).qIndex);
		}
		return list;
	}

	private void appendLabels(StringBuilder sb, IQubit... ctrl) {
		boolean separate = false;	// not before the first label
		for (IQubit q : ctrl) {
			if (separate) {
				sb.append(", ");
			} else {
				separate = true;
			}
			sb.append(((QubitQM) q).getLabel());
		}
	}

	private void debug(String oper) {
		if (debug) {
			StringBuilder sb = new StringBuilder();
			sb.append(getLabel()).append('.').append(oper);
			log(sb.toString());
		}
	}

	private void debug(String oper, QubitQM ctrl) {
		if (debug) {
			StringBuilder sb = new StringBuilder();
			sb.append(getLabel()).append('.').append(oper);
			sb.append('(').append(ctrl.getLabel()).append(')');
			log(sb.toString());
		}
	}

	private void debug(String oper, IQubit ctrl1, IQubit ctrl2) {
		if (debug) {
			StringBuilder sb = new StringBuilder();
			sb.append(getLabel()).append('.').append(oper);
			sb.append('(');
			appendLabels(sb, ctrl1, ctrl2);
			sb.append(')');
			log(sb.toString());
		}
	}

	private void debug(String oper, double radians) {
		if (debug) {
			StringBuilder sb = new StringBuilder();
			sb.append(getLabel()).append('.').append(oper);
			sb.append('(').append(round(radians/PI, 5)).append("π)");
			log(sb.toString());
		}
	}

	private void debug(String oper, double radians, QubitQM ctrl) {
		if (debug) {
			StringBuilder sb = new StringBuilder();
			sb.append(getLabel()).append('.').append(oper);
			sb.append('(').append(round(radians/PI, 5)).append("π, ").append(ctrl.getLabel()).append(')');
			log(sb.toString());
		}
	}

	private void debug(String oper, double radians, IQubit ctrl1, IQubit ctrl2) {
		if (debug) {
			StringBuilder sb = new StringBuilder();
			sb.append(getLabel()).append('.').append(oper);
			sb.append('(').append(round(radians/PI, 5)).append("π, ");
			appendLabels(sb, ctrl1, ctrl2);
			sb.append(')');
			log(sb.toString());
		}
	}

	private void debug(String oper, double theta, double phi, double lambda) {
		if (debug) {
			StringBuilder sb = new StringBuilder();
			sb.append(getLabel()).append('.').append(oper);
			sb.append("(θ=").append(round(theta/PI, 5)).append("π,");
			sb.append(" ϕ=").append(round(phi/PI, 5)).append("π,");
			sb.append(" λ=").append(round(lambda/PI, 5)).append("π)");
			log(sb.toString());
		}
	}

	private void debug(String oper, double theta, double phi, double lambda, QubitQM ctrl) {
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

	private void debug(String oper, double theta, double phi, double lambda, IQubit... ctrl) {
		if (debug) {
			StringBuilder sb = new StringBuilder();
			sb.append(getLabel()).append('.').append(oper);
			sb.append("(θ=").append(round(theta/PI, 5)).append("π,");
			sb.append(" ϕ=").append(round(phi/PI, 5)).append("π,");
			sb.append(" λ=").append(round(lambda/PI, 5)).append("π, ");
			appendLabels(sb, ctrl);
			sb.append(')');
			log(sb.toString());
		}
	}

}
