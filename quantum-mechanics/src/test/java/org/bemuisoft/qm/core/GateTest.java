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
package org.bemuisoft.qm.core;

import org.bemuisoft.math.complex.ComplexBase;
import org.bemuisoft.math.complex.ComplexNumber;
import org.bemuisoft.quantum.api.Axis;

/**
 * Testing the Gate class.
 * <p>
 * Not intended for automated testing.
 * Log output should be inspected visually.
 * 
 * @author Benno Muilwijk
 */
public class GateTest extends ComplexBase {

	public static void main(String[] args) {
		try {
			GateTest test = new GateTest();
			test.run();
			test.runPauli();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void run() {
		Gate rx0 = Gate.rx(HALF_PI);
		Gate ry0 = Gate.ry(HALF_PI);
		Gate rz0 = Gate.rz(HALF_PI);
		Gate rx1 = r(Axis.X, HALF_PI);
		Gate ry1 = r(Axis.Y, HALF_PI);
		Gate rz1 = r(Axis.Z, HALF_PI);
		int diffs = 0;
		diffs += compare("rx", rx0, rx1);
		diffs += compare("ry", ry0, ry1);
		diffs += compare("rz", rz0, rz1);
		if (diffs == 0) {
			log("No differences");
		}
	}

	void runPauli() {
		Gate rx0 = Gate.x();
		Gate ry0 = Gate.y();
		Gate rz0 = Gate.z();
		Gate rx1 = Gate.z().tilt(Axis.X.getTheta(), Axis.X.getPhi());
		Gate ry1 = Gate.z().tilt(Axis.Y.getTheta(), Axis.Y.getPhi());
		Gate rz1 = Gate.z().tilt(Axis.Z.getTheta(), Axis.Z.getPhi());
		int diffs = 0;
		diffs += compare("x", rx0, rx1);
		diffs += compare("y", ry0, ry1);
		diffs += compare("z", rz0, rz1);
		if (diffs == 0) {
			log("No differences");
		}
	}

	Gate r(Axis axis, double radians) {
		return Gate.rz(radians).tilt(axis.getTheta(), axis.getPhi());
	}

	int compare(String s, Gate gate0, Gate gate1) {
		int diffs = 0;
		diffs += compare(s + "(0, 0): ", gate0.get(0, 0), gate1.get(0, 0));
		diffs += compare(s + "(0, 1): ", gate0.get(0, 1), gate1.get(0, 1));
		diffs += compare(s + "(1, 0): ", gate0.get(1, 0), gate1.get(1, 0));
		diffs += compare(s + "(1, 1): ", gate0.get(1, 1), gate1.get(1, 1));
		return diffs;
	}

	int compare(String s, ComplexNumber z0, ComplexNumber z1) {
		if (Math.abs(z1.realPart() - z0.realPart()) > 1e-15 ||
			Math.abs(z1.imaginaryPart() - z0.imaginaryPart()) > 1e-15) {
			log(s + z1 + " is not equal to " + z0);
			return 1;
		}
		return 0;
	}

}
