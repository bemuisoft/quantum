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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import org.bemuisoft.math.complex.ColumnVector;
import org.bemuisoft.math.complex.ComplexNumber;
import org.bemuisoft.math.complex.Matrix;
import org.bemuisoft.qm.core.SimpleBlochVector;
import org.bemuisoft.qm.core.BlochVectorAnalyzer;
import org.bemuisoft.qm.core.Gate;
import org.bemuisoft.qm.core.PureQuantumSystem;
import org.bemuisoft.qm.core.TestBase;
import org.bemuisoft.qm.tools.Concurrence;

/**
 * Helper class for extended analysis of pure quantum systems,
 * used by several test classes.
 * 
 * @author Benno Muilwijk
 */
public class QuantumSystemAnalyzer extends PureQuantumSystem implements TestBase {

	public boolean monitorComponentChanges = false;
	public boolean monitorRelativeChanges = false;
	
	private QubitQM[] qubits;
	private String[] binary;
	private HashMap<String, SimpleBlochVector> qubitComponents = new HashMap<>();

	public QuantumSystemAnalyzer(int qubits) {
		super(qubits);
		this.qubits = new QubitQM[qubits];
	}

	public QuantumSystemAnalyzer(int qubits, boolean ibm) {
		super(qubits, ibm);
		this.qubits = new QubitQM[qubits];
	}

	public QuantumSystemAnalyzer(int qubits, ColumnVector state) {
		this(qubits);
		super.setState(state);
	}

	@Override
	public QuantumSystemAnalyzer apply(Gate gate, int qIndex) {
		if (qubitComponents.isEmpty()) {
			// initialize qubitComponents if needed
			monitorChanges();
		}
		super.apply(gate, qIndex);
		monitorChanges();
		return this;
	}

	@Override
	public QuantumSystemAnalyzer apply(Gate gate, int cIndex, int qIndex) {
		super.apply(gate, cIndex, qIndex);
		monitorChanges();
		return this;
	}

	public SimpleBlochVector simpleBlochVector(QubitQM q) {
		return blochVector(q.getIndex());
	}

	public List<SimpleBlochVector> blochVectorComponents(QubitQM q) {
		// the number of components returned depends on the number of qubits in the quantum system
		// for n qubits, 2^(n-1) components are returned
		// TODO rearrange order of components in case of IBM labeling (right to left)
		//		and check for using methods already doing that
//		return blochVectorComponents(q.getIndex());
		return q.getComponents();
	}

	public List<SimpleBlochVector> blochVectorComponents(QubitQM qa, QubitQM qb) {
		// always return 4 components, for Ab0, Ab1, Ba0 and Ba1
		// where Ab0 is the sum of components of A when B is measured as |0⟩, etc.
		// caller has to ensure that qa and qb are different Qubits
		final int qIndexA = qa.getIndex();
		final int qIndexB = qb.getIndex();
		String labelAb = qa.getLabel() + qb.getLabel().toLowerCase();
		String labelBa = qb.getLabel() + qa.getLabel().toLowerCase();
		ArrayList<SimpleBlochVector> blochComponents = new ArrayList<>(4);
		Matrix rdm = reducedDensityMatrix(qIndexA, qIndexB);	// rdm is a 4x4 matrix
		if (qIndexA < qIndexB) {
			blochComponents.add(new SimpleBlochVector(labelAb+'0', rdm, 0, 2));
			blochComponents.add(new SimpleBlochVector(labelAb+'1', rdm, 1, 3));
			blochComponents.add(new SimpleBlochVector(labelBa+'0', rdm, 0, 1));
			blochComponents.add(new SimpleBlochVector(labelBa+'1', rdm, 2, 3));
		} else {
			blochComponents.add(new SimpleBlochVector(labelAb+'0', rdm, 0, 1));
			blochComponents.add(new SimpleBlochVector(labelAb+'1', rdm, 2, 3));
			blochComponents.add(new SimpleBlochVector(labelBa+'0', rdm, 0, 2));
			blochComponents.add(new SimpleBlochVector(labelBa+'1', rdm, 1, 3));
		}
		return blochComponents;
	}

	public double getConcurrence(QubitQM qa, QubitQM qb) {
		try {
			Concurrence concurrence = new Concurrence(this, qa.getIndex(), qb.getIndex());
			return concurrence.value();
		} catch (RuntimeException e) {
			Throwable t = e.getCause();
			if (t == null) t = e;
			log(e.getMessage());
			return Double.NaN;
		}
	}

	public void logState() {
		logState(0.0);
	}

	public void logState(int relativePhaseIndex) {
		logState(state().get(relativePhaseIndex, 0).phase());
	}

	private void logState(double globalPhase) {
		ColumnVector state = state();
		for (int i = 0; i < state.rows(); i++) {
			ComplexNumber v = state.get(i);							// probability amplitude
			double r = round(v.magnitude());
			double phase = round((v.phase() - globalPhase) / PI);
			if (globalPhase != 0.0) {
				if (phase > 1.0) phase -= 2.0;
				else if (phase <= -1.0) phase += 2.0;
			}
			// log magnitude, phase and probability
			log(String.valueOf(i) + '\t' + format(r) + " \t" + format(phase) + "π\t" + format(r*r));
		}
	}

	private String format(double value) {
		return String.format(Locale.US, "%.5f", value);
	}

	public void logDensityMatrix(String description) {
		logDensityMatrix(description, densityMatrix());
	}

	public void logDensityMatrix(String description, Matrix rho) {
		log(description);
		for (int i = 0; i < rho.rows(); i++) {
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < rho.columns(); j++) {
				if (j != 0) sb.append('\t');
				ComplexNumber z = rho.get(i, j);
				sb.append(round(z.absValue())).append("*e^").append(toPi(z.phase())).append('i');
			}
			log(sb.toString());
		}
	}

	QubitQM getQubit(char label) {
		return getQubit(qubitIndex(label));
	}

	QubitQM getQubit(int index) {
		return qubits[index];
	}

	void setQubit(QubitQM q) {
		qubits[q.getIndex()] = q;
	}

	String toBinaryString(int index) {
		if (binary == null) {
			binary = new String[state().rows() / 2];
			int binaryDigits = qubits() - 1;
			String format = "%" + binaryDigits + 's';
			for (int i = 0; i < binary.length; i++) {
				binary[i] = String.format(format, Integer.toBinaryString(i)).replace(' ', '0');
			}
		}
		return binary[index];
	}

	private void monitorChanges() {
		if (QubitQM.debug) {
			if (monitorComponentChanges) {
				logComponentChanges();
			}
			if (monitorRelativeChanges) {
				logRelativeChanges();
			}
		}
	}

	private void logComponentChanges() {
		for (int i = 0; i < qubits.length; i++) {
			QubitQM q = qubits[i];
			if (q != null) {
				logComponentChanges(q);
			}
			
		}
	}

	private void logComponentChanges(QubitQM q) {
		List<SimpleBlochVector> comps = q.getComps();
		for (int i = 0; i < comps.size(); i++) {
			SimpleBlochVector newComp = comps.get(i);
			String label = q.getLabel() + toBinaryString(i);
			SimpleBlochVector oldComp = qubitComponents.get(label);
			if (!newComp.isCloseTo(oldComp)) {
				if (oldComp != null) {
					logComponentChange(label, oldComp, newComp);
				}
				qubitComponents.put(label, newComp);
			}
		}
	}

	private void logComponentChange(String label, SimpleBlochVector oldComp, SimpleBlochVector newComp) {
		double deltaX = newComp.getX() - oldComp.getX();
		double deltaY = newComp.getY() - oldComp.getY();
		double deltaZ = newComp.getZ() - oldComp.getZ();
		log(label +
			"  x: " + oldComp.getX() + (deltaX < 0.0 ? " - " + (-deltaX) : " + " + deltaX) + " -> " + newComp.getX() +
			", y: " + oldComp.getY() + (deltaY < 0.0 ? " - " + (-deltaY) : " + " + deltaY) + " -> " + newComp.getY() +
			", z: " + oldComp.getZ() + (deltaZ < 0.0 ? " - " + (-deltaZ) : " + " + deltaZ) + " -> " + newComp.getZ()
		);
	}

	private void logRelativeChanges() {
		if (qubitIndex('A') == 0) {
			// start with qubit 'A', which has lowest index
			for (int i = 0; i < qubits(); i++) {
				for (int j = 0; j < qubits(); j++) {
					logRelativeChanges(qubits[i], qubits[j]);
				}
			}
		} else {
			// start with QubitQM 'A', which has highest index
			for (int i = qubits() - 1; i >= 0 ; i--) {
				for (int j = qubits() - 1; j >= 0 ; j--) {
					logRelativeChanges(qubits[i], qubits[j]);
				}
			}
		}
	}

	private void logRelativeChanges(QubitQM q1, QubitQM q2) {
		if (q1 == q2 || q1 == null || q2 == null) return;
		List<SimpleBlochVector> comps = blochVectorComponents(q1, q2);
		for (int i = 0; i < 2; i++) {
			SimpleBlochVector newComp = comps.get(i);
			String label = newComp.getLabel();
			newComp = new BlochVectorAnalyzer(label, newComp).rz(-q1.getPhase()).ry(-q1.getTheta());	// .rz(q1.getPhase());
			SimpleBlochVector oldComp = qubitComponents.get(label);
			if (!newComp.isCloseTo(oldComp)) {
				if (oldComp != null) {
					logRelativeChange(label, oldComp, newComp);
				}
				qubitComponents.put(label, newComp);
			}
		}
	}

	private void logRelativeChange(String label, SimpleBlochVector oldComp, SimpleBlochVector newComp) {
		double deltaP = newComp.getPhase() - oldComp.getPhase();
		double deltaZ = newComp.getZ() - oldComp.getZ();
		double deltaR = newComp.length() - oldComp.length();
		String oldPhase = toPi(oldComp.getPhase());
		String newPhase = toPi(newComp.getPhase());
		log(label +
			"  ϕ: " + oldPhase + (deltaP < 0.0 ? " - " + toPi(-deltaP) : " + " + toPi(deltaP)) + " -> " + newPhase +
			", z: " + oldComp.getZ()	 + (deltaZ < 0.0 ? " - " + (-deltaZ) : " + " + deltaZ) + " -> " + newComp.getZ() +
			", r: " + oldComp.length()	 + (deltaR < 0.0 ? " - " + (-deltaR) : " + " + deltaR) + " -> " + newComp.length()
		);
	}

	public void analyzeComponentLengths(QubitQM qr) {
		// test if lengths of components 0 + 1 for qr are constant no matter how qr is rotated
		// for example, if qr has index 0 then the sum of lengths of Q0xxx and Q1xxx should not change
		final int qrIndex = qr.getIndex();
		final String qrLabel = qr.getLabel();
		final boolean debug = QubitQM.debug;
		if (debug) QubitQM.debug = false;
		RotationAnalyzerMap rxMap = getRotationAnalyzerMap(null, rad -> qr.rx(rad), 8);
		RotationAnalyzerMap ryMap = getRotationAnalyzerMap(null, rad -> qr.ry(rad), 8);
		for (RotationAnalyzer rx : rxMap.values()) {
			String label = rx.getLabel();
			if (!label.startsWith(qrLabel)) {
				int qIndex = qubitIndex(label.charAt(0));
				int pos = (qIndex < qrIndex) ? qrIndex : qrIndex + 1;
				if (label.charAt(pos) == '0') {
					char[] chars = label.toCharArray();
					chars[pos] = '1';
					String label1 = new String(chars);
					RotationAnalyzer rx1 = rxMap.get(label1);
					double sum = rx.getSample(0).length() + rx1.getSample(0).length();
					assert checkSum(rx, rx1, sum) == 0;
					assert checkSum(ryMap.get(label), ryMap.get(label1), sum) == 0;
				}
			}
		}
		if (debug) QubitQM.debug = true;
	}

	private int checkSum(RotationAnalyzer ra0, RotationAnalyzer ra1, double sum) {
		int failures = 0;
		for (int i = 0; i < ra0.getSamples().size(); i++) {
			double check = ra0.getSample(i).length() + ra1.getSample(i).length();
			if (Math.abs(check - sum) > 1e-12) {
				failures++;
			}
		}
		return failures;
	}

	public void analyzeComponents(QubitQM q) {
		ArrayList<RotationAnalyzerMap> rxMaps = new ArrayList<>(qubits.length);
		ArrayList<RotationAnalyzerMap> ryMaps = new ArrayList<>(qubits.length);
		final boolean debug = QubitQM.debug;
		if (debug) QubitQM.debug = false;
		for (QubitQM qr : qubits) {
			rxMaps.add(getRotationAnalyzerMap(null, rad -> qr.rx(rad), 4));
			ryMaps.add(getRotationAnalyzerMap(null, rad -> qr.ry(rad), 4));
		}
		for (RotationAnalyzer ra : rxMaps.get(0).values()) {
			String label = ra.getLabel();
			if (q == null || label.startsWith(q.getLabel())) {
				log("");
				log(label + " = " + ra.getSample(0));
				for (QubitQM qr : qubits) {
					if (!label.startsWith(qr.getLabel())) {
						log("Component " + label + " wrt rotations of " + qr.getLabel());
						RotationAnalyzer rx = rxMaps.get(qr.getIndex()).get(label);
						RotationAnalyzer ry = ryMaps.get(qr.getIndex()).get(label);
						assert ry.getCenter().isCloseTo(rx.getCenter(), 1e-12);
						log(rx.getCenter());
						double sum2a = rx.getSample(0).length() + rx.getSample(2).length();
						assert Math.abs(ry.getSample(0).length() + ry.getSample(2).length() - sum2a) < 1e-12;
						double a = sum2a / 2;
						double f = rx.getCenter().length();
						log("2a = " + sum2a + ", b = " + Math.sqrt(a*a - f*f));
						log(qr.getLabel() + ".RX -> " + rx.getRotationAxis());
						log(qr.getLabel() + ".RY -> " + ry.getRotationAxis());
					}
				}
			}
		}
		if (debug) QubitQM.debug = true;
	}

	public void analyzeComponentRotations(QubitQM qr) {
		final String qrLabel = qr.getLabel();
		final boolean debug = QubitQM.debug;
		if (debug) QubitQM.debug = false;
		RotationAnalyzerMap rxMap = getRotationAnalyzerMap(null, rad -> qr.rx(rad), 8);
		RotationAnalyzerMap ryMap = getRotationAnalyzerMap(null, rad -> qr.ry(rad), 8);
		log("");
		log(qr);
		for (RotationAnalyzer ra : rxMap.values()) {
			String label = ra.getLabel();
			if (!label.startsWith(qrLabel)) {
				log("Analyzing effect of rotating " + qr.getLabel() + " on component " + label);
				log(ra.getSample(0));
				log(ra.getCenter());
				// get semi major and minor axes from rx and ry
				RotationAnalyzer rx = sampleMajorMinorAxes(ra,				 rad -> qr.rx(rad));
				RotationAnalyzer ry = sampleMajorMinorAxes(ryMap.get(label), rad -> qr.ry(rad));
				logSpheroidCharacteristics(rx, ry);
			}
		}
		if (debug) QubitQM.debug = true;
	}

	public void analyzeRotation(QubitQM qr) {
		final String qrLabel = qr.getLabel().toLowerCase();
		final boolean debug = QubitQM.debug;
		if (debug) QubitQM.debug = false;
		RotationAnalyzerMap rxMap = getRotationAnalyzerMap(qr, rad -> qr.rx(rad), 8);
		RotationAnalyzerMap ryMap = getRotationAnalyzerMap(qr, rad -> qr.ry(rad), 8);
		log("");
		log(qr);
		for (QubitQM q : qubits) {
			if (q != null && q != qr) {
				String label = q.getLabel() + qrLabel + '0';
				log("Analyzing effect of rotating " + qr.getLabel() + " on " + label);
				log(q);
				log(rxMap.get(label).getCenter());
				// get semi major and minor axes from rx and ry
				RotationAnalyzer rx = sampleMajorMinorAxes(q, qr, rxMap.get(label), rad -> qr.rx(rad));
				RotationAnalyzer ry = sampleMajorMinorAxes(q, qr, ryMap.get(label), rad -> qr.ry(rad));
				logSpheroidCharacteristics(rx, ry);
			}
		}
		if (debug) QubitQM.debug = true;
	}

	public RotationAnalyzerMap getRotationAnalyzerMap(QubitQM qr, Consumer<Double> rotation) {
		return getRotationAnalyzerMap(qr, rotation, 48);
	}

	public RotationAnalyzerMap getRotationAnalyzerMap(QubitQM qr, Consumer<Double> rotation, final int steps) {
		final double step = PI * 2 / steps;
		final boolean debug = QubitQM.debug;
		if (debug) QubitQM.debug = false;
		RotationAnalyzerMap raMap = new RotationAnalyzerMap(steps);
		for (int i = 0; i < steps; i++) {
			sampleRotationData(qr, raMap);
			rotation.accept(step);
		}
		sampleRotationData(qr, raMap);		// last sample must be the same as first sample (after full rotation of 2π)
		if (debug) QubitQM.debug = true;
		return raMap;
	}

	private void sampleRotationData(QubitQM qr, RotationAnalyzerMap raMap) {
		for (QubitQM q : qubits) {
			if (q != null) {
				if (qr == null) {
					sampleComponentRotationData(q, raMap);
				} else if (q != qr) {
					sampleRelativeRotationData(q, qr, raMap);
				} else {	// q == qr
					sampleQubitRotationData(qr, raMap);
				}
			}
		}
	}

	private void sampleQubitRotationData(QubitQM qr, RotationAnalyzerMap raMap) {
		String label = qr.getLabel();
		RotationAnalyzer ra = raMap.get(label);
		ra.addSample(new BlochVectorAnalyzer(label, qr));
	}

	private void sampleRelativeRotationData(QubitQM q, QubitQM qr, RotationAnalyzerMap raMap) {
		List<SimpleBlochVector> comps = blochVectorComponents(q, qr);
		for (int i = 0; i < 2; i++) {
			SimpleBlochVector comp = comps.get(i);
			String label = comp.getLabel();
			RotationAnalyzer ra = raMap.get(label);
			ra.addSample(comp);
		}
	}

	private void sampleComponentRotationData(QubitQM q, RotationAnalyzerMap raMap) {
		List<SimpleBlochVector> comps = q.getComps();
		for (int i = 0; i < comps.size(); i++) {
			SimpleBlochVector comp = comps.get(i);
			String label = q.getLabel() + toBinaryString(i);
			RotationAnalyzer ra = raMap.get(label);
			ra.addSample(comp);
		}
	}

	private RotationAnalyzer sampleMajorMinorAxes(RotationAnalyzer ra, Consumer<Double> rotation) {
		// of this component rotation analyzer
		String label = ra.getLabel();
		QubitQM q = getQubit(label.charAt(0));
		RotationAnalyzerMap raMap = new RotationAnalyzerMap(4);
		double phi = ra.getPhiMajor();
		rotation.accept(phi);
		for (int i = 0; i < 4; i++) {
			sampleComponentRotationData(q, raMap);
			rotation.accept(HALF_PI);
		}
		sampleComponentRotationData(q, raMap);	// last sample must be the same as first sample (after full rotation of 2π)
		// restore state
		rotation.accept(-phi);		// TODO implement proper save/restore state?
		return raMap.get(label);
	}

	private RotationAnalyzer sampleMajorMinorAxes(QubitQM q, QubitQM qr, RotationAnalyzer ra, Consumer<Double> rotation) {
		// of q relative to qr
		RotationAnalyzer axes = new RotationAnalyzer(ra.getLabel(), 5);
		double phi = ra.getPhiMajor();
		rotation.accept(phi);
		for (int i = 0; i < 4; i++) {
			SimpleBlochVector v = blochVectorComponents(q, qr).get(0);
			assert v.getLabel().equals(ra.getLabel());
			axes.addSample(v);
			rotation.accept(HALF_PI);
		}
		axes.addSample(blochVectorComponents(q, qr).get(0));	// last sample must be the same as first sample (after full rotation of 2π)
		// restore state
		rotation.accept(-phi);		// TODO implement proper save/restore state?
		return axes;
	}

	private void logSpheroidCharacteristics(RotationAnalyzer rx, RotationAnalyzer ry) {
		SimpleBlochVector rxMajor = rx.fromCenter(0);
		SimpleBlochVector rxMinor = rx.fromCenter(1);
		SimpleBlochVector ryMajor = ry.fromCenter(0);
		SimpleBlochVector ryMinor = ry.fromCenter(1);
		// verify that each minor axis is not longer than its major axis
		assert rxMinor.length() <= rxMajor.length();
		assert ryMinor.length() <= ryMajor.length();
		if (rxMajor.length() < 1e-8 && ryMajor.length() < 1e-8) {
			log(rx.getLabel() + " is not affected");
			log("");
			return;
		}
		// are minor axes from rx and ry perpendicular? Usually not (precise)
		// log("rxMinor.ryMinor = " + rxMinor.dotProduct(ryMinor));
		// verify that minor axes from rx and ry have the same length
		if (!(Math.abs(rxMinor.length() - ryMinor.length()) < 1e-12)) {
			log("rxMinor and ryMinor have different lengths");
			log("extreme effects of RX:");
			rx.logExtremes();
			log("extreme effects of RY:");
			ry.logExtremes();
			log("");
			return;
		}
		assert Math.abs(rxMinor.length() - ryMinor.length()) < 1e-12 : "rxMinor and ryMinor have different lengths";
		// calculate direction and length of spheroid
		BlochVectorAnalyzer major;
		String axisLabel = rx.getLabel() + " spheroid major axis";
		if (rxMinor.length() < 1e-8) {
			// spheroid with minor axis length 0 is a straight line along the major axis
//			assert ryMajor.isCloseTo(rxMajor, 1e-12);							// length is not accurate in this case
			assert Math.abs(rxMajor.getTheta() - ryMajor.getTheta()) < 1e-8;
			assert Math.abs(rxMajor.getPhase() - ryMajor.getPhase()) < 1e-8;
			major = BlochVectorAnalyzer.sum(axisLabel, rxMajor, ryMajor);
			log(rx.getLabel() + " is rotated over straight line");
		} else {
			// the (semi) major and minor axes describe an ellipse that is the intersection
			// of the plane of the ellipse and a spheroid
			// determine direction of major axis of spheroid, which is cross product of minor axes from rx and ry
			major = BlochVectorAnalyzer.crossProduct(axisLabel, rxMinor, ryMinor);
		}
		// calculate length of (semi) major axis
		// - using major axis from rx
		SimpleBlochVector rxAxis = rx.getSpheroidSemiMajor(major);
		// - using major axis from ry
		SimpleBlochVector ryAxis = ry.getSpheroidSemiMajor(major);
		// - and verifying that both give the same length and direction
		assert Math.abs(rxAxis.length() - ryAxis.length()) < 1e-8;
		assert Math.abs(rxAxis.getTheta() - ryAxis.getTheta()) < 1e-8;
		assert Math.abs(rxAxis.getPhase() - ryAxis.getPhase()) < 1e-8;
		// calculate and log characteristics of spheroid
		double a = rxAxis.length();			// largest distance from center
		double b = rxMinor.length();		// smallest distance from center
		if ((a - b) < 1e-8) {
			log(rx.getLabel() + " is rotated over sphere with r = " + a);
			log("");
			return;
		}
		double f = Math.sqrt(a*a - b*b);	// focus distance from center
		double e = f / a;					// eccentricity
		log(rxAxis);
		log(rx.getLabel() + " is rotated over spheroid with focus distance f = " + f + ", eccentricity e = " + e + ", min/max = " + (b/a));
		BlochVectorAnalyzer f1 = new BlochVectorAnalyzer("focus 1", rxAxis).scale(1/e).add(rx.getCenter());
		log(f1);
		BlochVectorAnalyzer f2 = new BlochVectorAnalyzer("focus 2", rxAxis).scale(-1/e).add(rx.getCenter());
		log(f2);
		log("");
	}

}
