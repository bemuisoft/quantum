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
package org.bemuisoft.quantum.adapters.quantum4j.junit;

import org.bemuisoft.quantum.adapters.quantum4j.MinimalQubit4J;
import org.bemuisoft.quantum.adapters.quantum4j.Qubit4J;
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
 * Automated test for {@link MinimalQubit4J}.
 * 
 * @author Benno Muilwijk
 */
class MinimalQubit4JTest {

	@Test
	void runOneQubitTest() {
		OneQubitTest<MinimalQubit4J> test = new OneQubitTest<MinimalQubit4J>().init(MinimalQubit4J.factory(1));
		test.runAll();
	}

	@Test
	void runOneQubitAnalyzerTest() {
		OneQubitAnalyzerTest<QubitAnalyzer> test = new OneQubitAnalyzerTest<QubitAnalyzer>();
		test.init(MinimalQubit4J.analyzerFactory(1));
		test.runAll();
	}

	@Test
	void runOneQubitAnalyzerExtraTest() {
		OneQubitAnalyzerExtraTest<QubitAnalyzer> test = new OneQubitAnalyzerExtraTest<QubitAnalyzer>();
		test.init(MinimalQubit4J.analyzerFactory(1));
		test.runAll();
	}

	@Test
	void runTwoQubitTest() {
		TwoQubitTest<MinimalQubit4J> test = new TwoQubitTest<MinimalQubit4J>().init(MinimalQubit4J.factory(2));
		test.runAll();
	}

	@Test
	void runTwoQubitAnalyzerTest() {
		TwoQubitAnalyzerTest<QubitTester> test = new TwoQubitAnalyzerTest<QubitTester>();
		test.init(QubitTester.factory(MinimalQubit4J.analyzerFactory(2), Qubit4J.factory(2)));
		test.runAll();
	}

	@Test
	void runTwoQubitAnalyzerExtraTest() {
		TwoQubitAnalyzerExtraTest<QubitTester> test = new TwoQubitAnalyzerExtraTest<QubitTester>();
		test.init(QubitTester.factory(MinimalQubit4J.analyzerFactory(2), Qubit4J.factory(2)));
		test.runAll();
	}

	@Test
	void runThreeQubitTest() {
		ThreeQubitTest<MinimalQubit4J> test = new ThreeQubitTest<MinimalQubit4J>().init(MinimalQubit4J.factory(3));
		test.runAll();
	}

	@Test
	void runControlledGateTest() {
		ControlledGateTest<MinimalQubit4J> test = new ControlledGateTest<MinimalQubit4J>().init(MinimalQubit4J.factory(2));
		test.runAll();
	}

	@Test
	void runMultiControlledGateTest() {
		MultiControlledGateTest<MinimalQubit4J> test = new MultiControlledGateTest<MinimalQubit4J>().init(MinimalQubit4J.factory(3));
		test.runAll();
	}

}
