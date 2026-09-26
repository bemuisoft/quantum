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

import java.util.List;
import java.util.function.Consumer;

import org.bemuisoft.math.complex.ComplexBase;
import org.bemuisoft.math.complex.Matrix;
import org.bemuisoft.qm.core.SimpleBlochVector;
import org.bemuisoft.qm.core.TestBase;
import org.bemuisoft.quantum.api.Axis;

/**
 * Base class for testing qubits.
 * 
 * @author Benno Muilwijk
 */
public abstract class QubitTestBase extends ComplexBase implements TestBase {

	protected QuantumSystemAnalyzer qs;

	protected QubitTestBase() {}

	protected QubitTestBase(int qubits) {
		qs = new QuantumSystemAnalyzer(qubits);
	}

	protected QubitTestBase(int qubits, boolean ibm) {
		qs = new QuantumSystemAnalyzer(qubits, ibm);
	}

	protected Axis axis(double theta, double phi) {
		return new Axis(theta, phi);
	}

	protected Axis orthogonal(QubitQM q) {
		// return axis perpendicular to the qubit's phase and the z-axis (so in the XOY plane)
		// rotation of this qubit about this axis will be towards one of the poles
		return new Axis(HALF_PI, q.getPhase() + HALF_PI);
	}

	protected void entangleRandom(QubitQM q1, QubitQM q2) {
		// not thread safe, but it's assumed QubitTests are always run single threaded
		final boolean mcc = qs.monitorComponentChanges;
		final boolean mrc = qs.monitorRelativeChanges;
		if (mcc) qs.monitorComponentChanges = false;
		if (mrc) qs.monitorRelativeChanges = false;
		q1.rx(random2Pi());
		q2.rx(random2Pi());
		q1.cp(random2Pi(), q2);
		q1.ry(random2Pi());
		q2.ry(random2Pi());
		if (mcc) qs.monitorComponentChanges = true;
		if (mrc) qs.monitorRelativeChanges = true;
	}

	public QubitQM qubit(char label) {
		QubitQM q = qs.getQubit(label);
		if (q == null) {
			q = new QubitQM(qs, label);
			qs.setQubit(q);
		}
		return q;
	}

	public void analyzeComponents() {
		qs.analyzeComponents(null);
	}

	public void analyzeComponents(QubitQM q) {
		qs.analyzeComponents(q);
	}

	public void analyzeComponentLengths(QubitQM qr) {
		qs.analyzeComponentLengths(qr);
	}

	public void analyzeComponentRotations(QubitQM qr) {
		qs.analyzeComponentRotations(qr);
	}

	public void analyzeRotation(QubitQM qr) {
		qs.analyzeRotation(qr);
	}

	public RotationAnalyzerMap analyzeRotation(QubitQM qr, String operation, Consumer<Double> rotation) {
		log("");
		log("Analyzing effect of " + operation + " on " + qr.getLabel());			// on relative Bloch vector components, eg Ab0 and Ab1
		RotationAnalyzerMap raMap = qs.getRotationAnalyzerMap(qr, rotation);
		raMap.logExtremesFromCenter();
		return raMap;
	}

	public RotationAnalyzerMap analyzeRotation(Consumer<Double> rotation, String description) {
		log("");
		log("Analyzing effect of " + description);						// on individual Bloch vector components, eg A00, A01, A10 and A11
		RotationAnalyzerMap raMap = qs.getRotationAnalyzerMap(null, rotation);
		raMap.logExtremesFromCenter();
		return raMap;
	}

	public List<SimpleBlochVector> blochVectorComponents(QubitQM q1, QubitQM q2) {
		return qs.blochVectorComponents(q1, q2);
	}

	public Matrix reducedDensityMatrix(Integer... qIndex) {
		return qs.reducedDensityMatrix(qIndex);
	}

	public void logState() {
		qs.logState();
	}

	public void logDensityMatrix(String description) {
		qs.logDensityMatrix(description);
	}

	public void logDensityMatrix(String description, Matrix rho) {
		qs.logDensityMatrix(description, rho);
	}

}
