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

package org.bemuisoft.quantum.test.api;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.bemuisoft.quantum.api.Axis;
import org.bemuisoft.quantum.api.Base;
import org.bemuisoft.quantum.api.IQubit;
import org.bemuisoft.quantum.api.IQubitAnalyzer;
import org.bemuisoft.quantum.api.IQubitFactory;

/**
 * A generic qubit tester. It wraps the qubit to be tested.
 * <p>
 * All qubit actions are delegated to the tested qubit
 * and the state is verified after each action.
 * This requires the tested qubit class to implement
 * {@link IQubitAnalyzer}.
 * <p>
 * Optionally, a verification qubit can be specified.
 * In that case, all qubit actions are also delegated
 * to the verification qubit, so it runs in shadow mode.
 * The internal state of the tested qubit is then compared
 * to that of the verification qubit after each action.
 * 
 * @author Benno Muilwijk
 */
public class QubitTester implements IQubitAnalyzer, Base {

	private static boolean verify = true;
	private static boolean verifyComponents = true;

	/** The qubit to be tested. */
	protected IQubitAnalyzer qt;
	/** The qubit to be compared to (for verification) */
	protected IQubitAnalyzer qv;

	/**
	 * Sets verification of internal consistency on or off.
	 * 
	 * @param enabled	- true = on, false = off
	 */
	public static void setVerification(boolean enabled) {
		verify = enabled;
	}

	/**
	 * Answers whether internal consistency is (to be) verified.
	 * 
	 * @return the verification flag
	 */
	public static boolean isVerified() {
		return verify;
	}

	/**
	 * Returns a {@link QubitTesterFactory} without verification qubits.
	 * 
	 * @param qtFactory		factory for qubits to be tested
	 * @return	a qubit tester factory
	 */
	public static QubitTesterFactory factory(IQubitFactory<? extends IQubitAnalyzer> qtFactory) {
		return new QubitTesterFactory(qtFactory);
	}

	/**
	 * Returns a {@link QubitTesterFactory} with verification qubits.
	 * <p>
	 * Qubits created by {@code qvFactory} must implement {@code setHiddenZ()}.
	 * 
	 * @param qtFactory		factory for qubits to be tested
	 * @param qvFactory		factory for verification qubits
	 * @return	a 	qubit tester factory
	 */
	public static QubitTesterFactory factory(
			IQubitFactory<? extends IQubitAnalyzer> qtFactory,
			IQubitFactory<? extends IQubitAnalyzer> qvFactory) {
		return new QubitTesterFactory(qtFactory, qvFactory);
	}

	/**
	 * Constructor.
	 *
	 * @param qt	the qubit to be tested
	 * @param qv	the verification qubit, or null
	 */
	public QubitTester(IQubitAnalyzer qt, IQubitAnalyzer qv) {
		this.qt = qt;
		this.qv = qv;
	}

	/**
	 * Verifies the state of the tested qubit by
	 * comparing it to the verification qubit.
	 */
	public void verify() {
		if (verify) {
			try {
				// general checks
				final double z = qt.getZ();
				final double y = qt.getY();
				final double x = qt.getX();
				final double r = qt.getMagnitude();
				verifyRange("z", z, -1.0, 1.0);
				verifyRange("y", y, -1.0, 1.0);
				verifyRange("x", x, -1.0, 1.0);
				if (qt.isMixed()) {
					verifyRange("magnitude", r, 0.0, 1.0);
				} else {
					verify("magnitude", r, 1.0);
				}
				// compare to verification qubit
				if (qv != null) {
					verify("z", z, qv.getZ());
					verify("y", y, qv.getY());
					verify("x", x, qv.getX());
					if (verifyComponents) {
						verifyComponents();
					}
				}
			} catch (UnsupportedOperationException e) {
				log("Cannot verify due to " + e);
				verify = false;
			}
		}
	}

	/**
	 * Verifies the Bloch vector components of the tested qubit by
	 * comparing them to those of the verification qubit.
	 */
	protected void verifyComponents() {
		int n = qt.components();
		if (n == 0 || n != qv.components()) {
			if (n > 0) {
				log("Cannot verify components due to incompatible number of components");
			}
			verifyComponents = false;
			return;
		}
		for (int i = 0; i < n; i++) {
			int logTheta = 0;
			int logPhase = 0;
			logTheta |= verify("z." + i, qt.getZ(i), qv.getZ(i));
			logPhase |= verify("y." + i, qt.getY(i), qv.getY(i));
			logPhase |= verify("x." + i, qt.getX(i), qv.getX(i));
			if (logTheta != 0) {
				double qtNorm = Math.sqrt(qt.getX(i)*qt.getX(i) + qt.getY(i)*qt.getY(i) + qt.getZ(i)*qt.getZ(i));
				double qvNorm = Math.sqrt(qv.getX(i)*qv.getX(i) + qv.getY(i)*qv.getY(i) + qv.getZ(i)*qv.getZ(i));
				double qtTheta = (qtNorm == 0.0) ? 0.0 : Math.acos(qt.getZ(i)/qtNorm);
				double qvTheta = (qvNorm == 0.0) ? 0.0 : Math.acos(qv.getZ(i)/qvNorm);
				verify("θ." + i, toPi(qtTheta), toPi(qvTheta));
			}
			if (logPhase != 0) {
				double qtPhase = Math.atan2(qt.getY(i), qt.getX(i));
				double qvPhase = Math.atan2(qv.getY(i), qv.getX(i));
				verify("ϕ." + i, toPi(qtPhase), toPi(qvPhase));
			}
		}
	}

	/**
	 * Verifies the value of a single property and logs any difference.
	 * <p>
	 * The return value can be used by a subclass to gather statistics.
	 * 
	 * @param property	name or label of the property to verify
	 * @param actual	the actual value
	 * @param expected	the expected value
	 * @return			0 if the actual value equals the expected value, 1 otherwise
	 */
	protected int verify(String property, String actual, String expected) {
		if (!actual.equals(expected)) {
			log(getLabel() + " - " + property + " = " + actual + " but expected " + expected);
			return 1;
		}
		return 0;
	}

	/**
	 * Verifies the value of a single property and logs any difference.
	 * <p>
	 * The return value can be used by a subclass to gather statistics.
	 * 
	 * @param property	name or label of the property to verify
	 * @param actual	the actual value
	 * @param expected	the expected value
	 * @return			0 if the actual value is close to the expected value, 1 otherwise
	 */
	protected int verify(String property, double actual, double expected) {
		if (Math.abs(actual - expected) > 3e-8) {
			log(getLabel() + " - " + property + " = " + actual + " but expected " + expected);
			return 1;
		}
		return 0;
	}

	/**
	 * Verifies that the value of a single property is in a given range
	 * and logs any deviation.
	 * <p>
	 * The return value can be used by a subclass to gather statistics.
	 * 
	 * @param property	name or label of the property to verify
	 * @param actual	the actual value
	 * @param min		the minimum value
	 * @param max		the maximum value
	 * @return			0 if the actual value is in the expected range, 1 otherwise
	 */
	protected int verifyRange(String property, double actual, double min, double max) {
		if (actual < min) {
			log(getLabel() + " - " + property + " = " + actual + " is less than " + min);
			return 1;
		}
		if (actual > max) {
			log(getLabel() + " - " + property + " = " + actual + " is greater than " + max);
			return 1;
		}
		return 0;
	}

	/**
	 * Performs the given operation on this tester's qubits.
	 * 
	 * @param consumer	- the qubit operation to be performed
	 */
	protected void perform(Consumer<IQubitAnalyzer> consumer) {
		consumer.accept(qt);
		if (qv != null) {
			consumer.accept(qv);
		}
	}

	/**
	 * Performs the given controlled operation on this tester's qubits.
	 * 
	 * @param consumer	- the qubit operation to be performed
	 * @param ctrl		- the control qubit tester
	 */
	protected void perform(BiConsumer<IQubitAnalyzer, IQubitAnalyzer> consumer, QubitTester ctrl) {
		consumer.accept(qt, ctrl.qt);
		if (qv != null && ctrl.qv != null) {
			consumer.accept(qv, ctrl.qv);
		}
	}

	/**
	 * Performs the given multi-controlled operation on this tester's qubits.
	 * 
	 * @param consumer	- the qubit operation to be performed
	 * @param iCtrl		- the control qubit testers
	 */
	protected void perform(BiConsumer<IQubitAnalyzer, IQubitAnalyzer[]> consumer, IQubit... iCtrl) {
		IQubitAnalyzer[] cqt = new IQubitAnalyzer[iCtrl.length];
		IQubitAnalyzer[] cqv = new IQubitAnalyzer[iCtrl.length];
		for (int i = 0; i < iCtrl.length; i++) {
			QubitTester c = (QubitTester) iCtrl[i];
			cqt[i] = c.qt;
			cqv[i] = c.qv;
		}
		consumer.accept(qt, cqt);
		if (qv != null) {
			consumer.accept(qv, cqv);
		}
	}

	/**
	 * Performs the given operation on this tester's qubit
	 * and verifies the result.
	 * 
	 * @param consumer	- the qubit operation to be performed
	 */
	protected void process(Consumer<IQubitAnalyzer> consumer) {
		perform(consumer);
		verify();
	}

	/**
	 * Performs the given controlled operation on this tester's qubit
	 * and verifies the result.
	 * 
	 * @param consumer	- the qubit operation to be performed
	 * @param iCtrl		- the control qubit tester
	 */
	protected void process(BiConsumer<IQubitAnalyzer, IQubitAnalyzer> consumer, IQubit iCtrl) {
		QubitTester ctrl = (QubitTester) iCtrl;
		perform(consumer, ctrl);
		this.verify();
		ctrl.verify();
	}

	/**
	 * Performs the given multi-controlled operation on this tester's qubit
	 * and verifies the result.
	 * 
	 * @param consumer	- the qubit operation to be performed
	 * @param iCtrl		- the control qubit testers
	 */
	protected void process(BiConsumer<IQubitAnalyzer, IQubitAnalyzer[]> consumer, IQubit... iCtrl) {
		perform(consumer, iCtrl);
		this.verify();
		for (int i = 0; i < iCtrl.length; i++) {
			QubitTester c = (QubitTester) iCtrl[i];
			c.verify();
		}
	}

	@Override
	public String getLabel() {
		return qt.getClass().getSimpleName() + ' ' + qt.getLabel();
	}

	@Override
	public int measure() {
		int outcome = qt.measure();
		if (qv != null) {
			qv.setHiddenZ(1.0 - outcome*2.0);
			int x = qv.measure();
			if (x != outcome) {
				qv.x();
			}
		}
		verify();
		return outcome;
	}

	@Override
	public QubitTester reset() {
		process(q -> q.reset());
		return this;
	}

	@Override
	public QubitTester h() {
		process(q -> q.h());
		return this;
	}

	@Override
	public QubitTester x() {
		process(q -> q.x());
		return this;
	}

	@Override
	public QubitTester y() {
		process(q -> q.y());
		return this;
	}

	@Override
	public QubitTester z() {
		process(q -> q.z());
		return this;
	}

	@Override
	public QubitTester s() {
		process(q -> q.s());
		return this;
	}

	@Override
	public QubitTester sdg() {
		process(q -> q.sdg());
		return this;
	}

	@Override
	public QubitTester t() {
		process(q -> q.t());
		return this;
	}

	@Override
	public QubitTester tdg() {
		process(q -> q.tdg());
		return this;
	}

	@Override
	public QubitTester p(double radians) {
		process(q -> q.p(radians));
		return this;
	}

	@Override
	public QubitTester sx() {
		process(q -> q.sx());
		return this;
	}

	@Override
	public QubitTester sxdg() {
		process(q -> q.sxdg());
		return this;
	}

	@Override
	public QubitTester rx(double radians) {
		process(q -> q.rx(radians));
		return this;
	}

	@Override
	public QubitTester ry(double radians) {
		process(q -> q.ry(radians));
		return this;
	}

	@Override
	public QubitTester rz(double radians) {
		process(q -> q.rz(radians));
		return this;
	}

	@Override
	public QubitTester r(Axis axis, double radians) {
		process(q -> q.r(axis, radians));
		return this;
	}

	@Override
	public QubitTester u(double theta, double phi, double lambda) {
		process(q -> q.u(theta, phi, lambda));
		return this;
	}

	@Override
	public QubitTester cx(IQubit ctrl) {
		process((qt, qc) -> qt.cx(qc),  ctrl);
		return this;
	}

	@Override
	public QubitTester cz(IQubit ctrl) {
		process((qt, qc) -> qt.cz(qc),  ctrl);
		return this;
	}

	@Override
	public QubitTester c(Axis axis, IQubit ctrl) {
		process((qt, qc) -> qt.c(axis, qc),  ctrl);
		return this;
	}

	@Override
	public QubitTester c(Axis axis, IQubit... ctrl) {
		process((qt, qc) -> qt.c(axis, qc),  ctrl);
		return this;
	}

	@Override
	public QubitTester cp(double radians, IQubit ctrl) {
		process((qt, qc) -> qt.cp(radians, qc),  ctrl);
		return this;
	}

	@Override
	public QubitTester cr(Axis axis, double radians, IQubit ctrl) {
		process((qt, qc) -> qt.cr(axis, radians, qc),  ctrl);
		return this;
	}

	@Override
	public QubitTester cr(Axis axis, double radians, IQubit... ctrl) {
		process((qt, qc) -> qt.cr(axis, radians, qc),  ctrl);
		return this;
	}

	@Override
	public QubitTester cu(double theta, double phi, double lambda, IQubit ctrl) {
		process((qt, qc) -> qt.cu(theta, phi, lambda, qc),  ctrl);
		return this;
	}

	@Override
	public QubitTester cu(double theta, double phi, double lambda, IQubit... ctrl) {
		process((qt, qc) -> qt.cu(theta, phi, lambda, qc),  ctrl);
		return this;
	}

	@Override
	public IQubit ccx(IQubit ctrl1, IQubit ctrl2) {
		QubitTester ctl1 = (QubitTester) ctrl1;
		QubitTester ctl2 = (QubitTester) ctrl2;
		qt.ccx(ctl1.qt, ctl2.qt);
		if (qv != null && ctl1.qv != null && ctl2.qv != null) {
			qv.ccx(ctl1.qv, ctl2.qv);
		}
		this.verify();
		ctl1.verify();
		ctl2.verify();
		return this;
	}

	@Override
	public IQubit ccz(IQubit ctrl1, IQubit ctrl2) {
		QubitTester ctl1 = (QubitTester) ctrl1;
		QubitTester ctl2 = (QubitTester) ctrl2;
		qt.ccz(ctl1.qt, ctl2.qt);
		if (qv != null && ctl1.qv != null && ctl2.qv != null) {
			qv.ccz(ctl1.qv, ctl2.qv);
		}
		this.verify();
		ctl1.verify();
		ctl2.verify();
		return this;
	}

	@Override
	public IQubit ccp(double radians, IQubit ctrl1, IQubit ctrl2) {
		QubitTester ctl1 = (QubitTester) ctrl1;
		QubitTester ctl2 = (QubitTester) ctrl2;
		qt.ccp(radians, ctl1.qt, ctl2.qt);
		if (qv != null && ctl1.qv != null && ctl2.qv != null) {
			qv.ccp(radians, ctl1.qv, ctl2.qv);
		}
		this.verify();
		ctl1.verify();
		ctl2.verify();
		return this;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ':' + qt.toString();
	}

	@Override
	public double getX() {
		return qt.getX();
	}

	@Override
	public double getY() {
		return qt.getY();
	}

	@Override
	public double getZ() {
		return qt.getZ();
	}

	@Override
	public double getX(int i) {
		return qt.getX(i);
	}

	@Override
	public double getY(int i) {
		return qt.getY(i);
	}

	@Override
	public double getZ(int i) {
		return qt.getZ(i);
	}

}
