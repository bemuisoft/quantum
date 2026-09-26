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
package org.bemuisoft.qm.sim.junit;

import org.bemuisoft.qm.sim.MinimalQubitQM;
import org.bemuisoft.qm.sim.QubitQM;
import org.bemuisoft.quantum.api.QubitAnalyzer;
import org.bemuisoft.quantum.test.api.QubitTester;
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
 * Automated test for {@link MinimalQubitQM}.
 * 
 * @author Benno Muilwijk
 */
class MinimalQubitQMTest {

	@Test
	void runOneQubitTest() {
		OneQubitTest<MinimalQubitQM> test = new OneQubitTest<MinimalQubitQM>().init(MinimalQubitQM.factory(1, false));
		test.runAll();
	}

	@Test
	void runOneQubitAnalyzerTest() {
		OneQubitAnalyzerTest<QubitAnalyzer> test = new OneQubitAnalyzerTest<QubitAnalyzer>();
		test.init(MinimalQubitQM.analyzerFactory(1, false));
		test.runAll();
	}

	@Test
	void runOneQubitAnalyzerExtraTest() {
		OneQubitAnalyzerExtraTest<QubitAnalyzer> test = new OneQubitAnalyzerExtraTest<QubitAnalyzer>();
		test.init(MinimalQubitQM.analyzerFactory(1, false));
		test.runAll();
	}

	@Test
	void runTwoQubitTest() {
		TwoQubitTest<MinimalQubitQM> test = new TwoQubitTest<MinimalQubitQM>().init(MinimalQubitQM.factory(2, false));
		test.runAll();
	}

	@Test
	void runTwoQubitAnalyzerTest() {
		TwoQubitAnalyzerTest<QubitTester> test = new TwoQubitAnalyzerTest<QubitTester>();
		test.init(QubitTester.factory(MinimalQubitQM.analyzerFactory(2, false), QubitQM.factory(2, false)));
		test.runAll();
	}

	@Test
	void runTwoQubitAnalyzerExtraTest() {
		TwoQubitAnalyzerExtraTest<QubitTester> test = new TwoQubitAnalyzerExtraTest<QubitTester>();
		test.init(QubitTester.factory(MinimalQubitQM.analyzerFactory(2, false), QubitQM.factory(2, false)));
		test.runAll();
	}

	@Test
	void runThreeQubitTest() {
		ThreeQubitTest<MinimalQubitQM> test = new ThreeQubitTest<MinimalQubitQM>().init(MinimalQubitQM.factory(3, false));
		test.runAll();
	}

	@Test
	void runControlledGateTest() {
		ControlledGateTest<MinimalQubitQM> test = new ControlledGateTest<MinimalQubitQM>().init(MinimalQubitQM.factory(2, false));
		test.runAll();
	}

	@Test
	void runMultiControlledGateTest() {
		MultiControlledGateTest<MinimalQubitQM> test = new MultiControlledGateTest<MinimalQubitQM>().init(MinimalQubitQM.factory(3, false));
		test.runAll();
	}

}
