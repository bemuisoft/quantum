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
package org.bemuisoft.quantum.sim.hidden.junit;

import org.bemuisoft.quantum.sim.hidden.QubitV1;
import org.bemuisoft.quantum.test.junit.ControlledGateTest;
import org.bemuisoft.quantum.test.junit.MultiControlledGateTest;
import org.bemuisoft.quantum.test.junit.OneQubitAnalyzerExtraTest;
import org.bemuisoft.quantum.test.junit.OneQubitAnalyzerTest;
import org.bemuisoft.quantum.test.junit.OneQubitTest;
import org.bemuisoft.quantum.test.junit.ThreeQubitTest;
import org.bemuisoft.quantum.test.junit.TwoQubitAnalyzerExtraTest;
import org.bemuisoft.quantum.test.junit.TwoQubitAnalyzerTest;
import org.bemuisoft.quantum.test.junit.TwoQubitTest;
import org.junit.jupiter.api.Test;

/**
 * Automated test for {@link QubitV1}.
 * 
 * @author Benno Muilwijk
 */
class QubitQ1Test {

	@Test
	void runOneQubitTest() {
		OneQubitTest<QubitV1> test = new OneQubitTest<QubitV1>().init(QubitV1.factory(1, true));
		test.runAll();
	}

	@Test
	void runOneQubitAnalyzerTest() {
		OneQubitAnalyzerTest<QubitV1> test = new OneQubitAnalyzerTest<QubitV1>();
		test.init(QubitV1.factory(1, true));
		test.runAll();
	}

	@Test
	void runOneQubitAnalyzerExtraTest() {
		OneQubitAnalyzerExtraTest<QubitV1> test = new OneQubitAnalyzerExtraTest<QubitV1>();
		test.init(QubitV1.factory(1, true));
		test.runAll();
	}

	@Test
	void runTwoQubitTest() {
		TwoQubitTest<QubitV1> test = new TwoQubitTest<QubitV1>().init(QubitV1.factory(2, true));
		test.runAll();
	}

	@Test
	void runTwoQubitAnalyzerTest() {
		TwoQubitAnalyzerTest<QubitV1> test = new TwoQubitAnalyzerTest<QubitV1>();
		test.init(QubitV1.factory(2, true));
		test.runAll();
	}

	@Test
	void runTwoQubitAnalyzerExtraTest() {
		TwoQubitAnalyzerExtraTest<QubitV1> test = new TwoQubitAnalyzerExtraTest<QubitV1>();
		test.init(QubitV1.factory(2, true));
		test.runAll();
	}

	@Test
	void runThreeQubitTest() {
		ThreeQubitTest<QubitV1> test = new ThreeQubitTest<QubitV1>().init(QubitV1.factory(3, true));
		test.runAll();
	}

	@Test
	void runControlledGateTest() {
		ControlledGateTest<QubitV1> test = new ControlledGateTest<QubitV1>().init(QubitV1.factory(2, true));
		test.runAll();
	}

	@Test
	void runMultiControlledGateTest() {
		MultiControlledGateTest<QubitV1> test = new MultiControlledGateTest<QubitV1>().init(QubitV1.factory(3, true));
		test.runAll();
	}

}
