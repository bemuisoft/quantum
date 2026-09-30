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

package org.bemuisoft.quantum.api;

import org.bemuisoft.math.base.SpatialVector;

/**
 * An abstract implementation of the {@link IQubitAnalyzer} interface.
 * <p>
 * This class can be useful as superclass for an {@link IQubitAnalyzer}
 * implementation, if the subclass instances do have a shared
 * {@link IQuantumState} object.
 * 
 * @author Benno Muilwijk
 */
public abstract class AbstractQubitAnalyzer extends SpatialVector implements IQubitAnalyzer, Base {

	private IQuantumState qs;
	private String label;
	private int qMask;
	private int stateId = -1;
	private double[][] components;

	/**
	 * Constructs a qubit with a shared {@link IQuantumState}.
	 * <p>
	 * The identifying short label will be derived
	 * from the last character of the long label.
	 * Therefore, the last character must in a consecutive
	 * range starting at either 0 or A or a.
	 * 
	 * @param qs	- the quantum system to share
	 * @param label	- a long label with identifying last character
	 * @see	IQuantumState#isRightToLeft()
	 */
	protected AbstractQubitAnalyzer(IQuantumState qs, String label) {
		this.qs = qs;
		this.label = label;
		this.qMask = getMask(label);
	}

	private int getMask(String label) {
		int i = qs.shortLabel(label) - 'A';
		return (qs.isRightToLeft()) ? 1 << i : qs.size() >> (i+1);
	}

	private double[][] getComponents() {
		if (components == null) {
			components = new double[components()][3];
			stateId--;		// force update on synchState
		}
		synchState();
		return components;
	}

	private void addComponent(int i0, int i1, int k) {
		// extract the Bloch vector component
		double p0 = qs.getProbability(i0);
		double p1 = qs.getProbability(i1);
		double p = p0 + p1;							// probability == weighted length
		double pcos = p0 - p1;						// p*cos(theta) == z
		double psin = Math.sqrt(p*p - pcos*pcos);	// p*sin(theta) == sqrt(x² + y²) == sqrt(p² - z²)
		double phase = qs.getPhase(i1) - qs.getPhase(i0);
		
		double x = psin * Math.cos(phase);
		double y = psin * Math.sin(phase);
		double z = pcos;
		
		super.x += x;
		super.y += y;
		super.z += z;
		
		if (components != null) {
			components[k][0] = x;
			components[k][1] = y;
			components[k][2] = z;
		}
	}

	private void setXYZ(double x, double y, double z) {
		super.x = x;
		super.y = y;
		super.z = z;
	}

	private void synchState() {
		// refresh this qubit's Bloch vector and its components
		// if the system state has changed
		if (stateId != stateIdentifier()) synchronized(qs) {
			setXYZ(0.0, 0.0, 0.0);
			int mask = qMask;
			int k = 0;
			for (int i = 0; i < qs.size(); i++) {
				if ((i & mask) == 0) {
					int j = i | mask;
					addComponent(i, j, k++);
				}
			}
			setXYZ(roundCos(x), roundCos(y), roundCos(z));
			stateId = stateIdentifier();
		}
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
	 */
	protected abstract int stateIdentifier();

	//------------------------
	// IQubitAnalyzer methods
	//------------------------

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public IQuantumState getSystemState() {
		return qs;
	}

	@Override
	public double getX() {
		synchState();
		return super.x;
	}

	@Override
	public double getY() {
		synchState();
		return super.y;
	}

	@Override
	public double getZ() {
		synchState();
		return super.z;
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
		return qs.size() >> 1;		// qs.size() / 2
	}

	@Override
	public double getX(int i) {
		synchState();
		return getComponents()[i][0];
	}

	@Override
	public double getY(int i) {
		synchState();
		return getComponents()[i][1];
	}

	@Override
	public double getZ(int i) {
		synchState();
		return getComponents()[i][2];
	}

}
