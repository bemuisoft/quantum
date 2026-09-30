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
package org.bemuisoft.quantum.sim.distributed.junit;

import org.bemuisoft.quantum.api.IQubitAnalyzer;
import org.bemuisoft.quantum.api.IQubitFactory;
import org.bemuisoft.quantum.sim.distributed.DecentralizedQubit;
import org.bemuisoft.quantum.test.junit.TwoQubitAnalyzerTest;

/**
 * Tests the required methods of the {@link IQubitAnalyzer}
 * implementation of {@link DecentralizedQubit} in a two-qubit system.
 * 
 * @author Benno Muilwijk
 * 
 * @see TwoQubitAnalyzerTest
 */
class TwoQubitAnalyzerD0Test extends TwoQubitAnalyzerTest<DecentralizedQubit> {

	@Override
	protected IQubitFactory<DecentralizedQubit> getFactory(int n) {
		return DecentralizedQubit.factory(n);
	}

}
