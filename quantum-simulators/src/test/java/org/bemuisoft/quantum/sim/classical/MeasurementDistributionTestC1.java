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

import org.bemuisoft.quantum.test.api.AbstractExperiment;

/**
 * Measurement distribution test for {@link CubitV1}.
 * <p>
 * Not intended for automated testing.
 * Log output should be inspected visually.
 * 
 * @author Benno Muilwijk
 */
public class MeasurementDistributionTestC1 extends AbstractExperiment<CubitV1> {

	public static void main(String[] args) {
		try {
			MeasurementDistributionTestC1 test = new MeasurementDistributionTestC1(2);	// test with n qubits
			test.run(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public MeasurementDistributionTestC1(int n) {
		super(n, CubitV1.factory());
	}

	@Override
	public void run() {
//		adHoc();
		run0();
		measureAll();
	}

	void adHoc() {
		q(0).h();
//		q(1).h();
		q(0).rz(0*Math.PI/4);
		q(0).ry( Math.PI/4);
//		q(1).ry( Math.PI/4);
	}

	void run0() {
		// expect almost equal distribution over all combinations
		for (int i = 0; i < getSize(); i++) {
			q(i).h();
		}
	}

}
