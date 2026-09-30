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

import org.bemuisoft.quantum.api.IQubitFactory;
import org.bemuisoft.quantum.sim.copenhagen.StandardQubit;
import org.bemuisoft.quantum.test.junit.MultiControlledGateTest;

/**
 * Tests controlled gates of {@link StandardQubit}
 * and their reversibility in a three-qubit system.
 * 
 * @author Benno Muilwijk
 * 
 * @see MultiControlledGateTest
 */
class MultiControlledGateQ0Test extends MultiControlledGateTest<StandardQubit> {

	@Override
	protected IQubitFactory<StandardQubit> getFactory(int n) {
		return StandardQubit.factory(n, false);
	}

}
