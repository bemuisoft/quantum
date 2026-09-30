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
package org.bemuisoft.quantum.sim.copenhagen.junit;

import org.bemuisoft.quantum.api.QubitAnalyzer;
import org.bemuisoft.quantum.sim.copenhagen.MinimalQubit;
import org.bemuisoft.quantum.sim.copenhagen.StandardQubit;
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
 * Automated test for {@link MinimalQubit}.
 * 
 * @author Benno Muilwijk
 */
class MinimalQubitTest {

	@Test
	void runOneQubitTest() {
		OneQubitTest<MinimalQubit> test = new OneQubitTest<MinimalQubit>().init(MinimalQubit.factory(1, true));
		test.runAll();
	}

	@Test
	void runOneQubitAnalyzerTest() {
		OneQubitAnalyzerTest<QubitAnalyzer> test = new OneQubitAnalyzerTest<QubitAnalyzer>();
		test.init(MinimalQubit.analyzerFactory(1, true));
		test.runAll();
	}

	@Test
	void runOneQubitAnalyzerExtraTest() {
		OneQubitAnalyzerExtraTest<QubitAnalyzer> test = new OneQubitAnalyzerExtraTest<QubitAnalyzer>();
		test.init(MinimalQubit.analyzerFactory(1, true));
		test.runAll();
	}

	@Test
	void runTwoQubitTest() {
		TwoQubitTest<MinimalQubit> test = new TwoQubitTest<MinimalQubit>().init(MinimalQubit.factory(2, true));
		test.runAll();
	}

	@Test
	void runTwoQubitAnalyzerTest() {
		TwoQubitAnalyzerTest<QubitTester> test = new TwoQubitAnalyzerTest<QubitTester>();
		test.init(QubitTester.factory(MinimalQubit.analyzerFactory(2, true), StandardQubit.factory(2, true)));
		test.runAll();
	}

	@Test
	void runTwoQubitAnalyzerExtraTest() {
		TwoQubitAnalyzerExtraTest<QubitTester> test = new TwoQubitAnalyzerExtraTest<QubitTester>();
		test.init(QubitTester.factory(MinimalQubit.analyzerFactory(2, true), StandardQubit.factory(2, true)));
		test.runAll();
	}

	@Test
	void runThreeQubitTest() {
		ThreeQubitTest<MinimalQubit> test = new ThreeQubitTest<MinimalQubit>().init(MinimalQubit.factory(3, true));
		test.runAll();
	}

	@Test
	void runControlledGateTest() {
		ControlledGateTest<MinimalQubit> test = new ControlledGateTest<MinimalQubit>().init(MinimalQubit.factory(2, true));
		test.runAll();
	}

	@Test
	void runMultiControlledGateTest() {
		MultiControlledGateTest<MinimalQubit> test = new MultiControlledGateTest<MinimalQubit>().init(MinimalQubit.factory(3, true));
		test.runAll();
	}

}
